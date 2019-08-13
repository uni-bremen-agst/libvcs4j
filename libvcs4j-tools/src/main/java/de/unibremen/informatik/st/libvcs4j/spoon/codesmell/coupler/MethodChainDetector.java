package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.coupler;

import de.unibremen.informatik.st.libvcs4j.Validate;
import de.unibremen.informatik.st.libvcs4j.spoon.Environment;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmell;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmellDetector;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Metric;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Threshold;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Thresholds;
import lombok.NonNull;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtTypedElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MethodChainDetector extends CodeSmellDetector {

	private static final String
			QUALIFIED_NAME_OF_UNKNOWN_DECLARATION = "<unknown>";

	private static final int DEFAULT_THRESHOLD = 4;

	private final int threshold;

	private List<CtInvocation> invocations = null;

	public MethodChainDetector(@NonNull final Environment environment,
			final int threshold) throws NullPointerException,
			IllegalArgumentException {
		super(environment);
		this.threshold = Validate.notNegative(threshold);
	}

	public MethodChainDetector(@NonNull final Environment environment) {
		this(environment, DEFAULT_THRESHOLD);
	}

	@Override
	protected void enter(final CtElement element) {
		// we are currently following a chain...
		if (invocations != null) {
			// ... and found another invocation to append
			if (element instanceof CtInvocation) {
				invocations.add((CtInvocation) element);
			// ... and reached the end of the chain
			} else {
				final List<String> targets = invocations.stream()
						.map(CtInvocation::getTarget)
						.filter(Objects::nonNull)
						.map(CtTypedElement::getType)
						.filter(Objects::nonNull)
						.map(t -> getCache().getOrResolve(t))
						.map(d -> d.isEmpty() ?
								QUALIFIED_NAME_OF_UNKNOWN_DECLARATION :
								d.get().getQualifiedName())
						.collect(Collectors.toList());

				final Set<String> distinctTargets = new HashSet<>(targets);
				final int midVal = distinctTargets.size();
				if (midVal >= threshold) {
					addCodeSmell(invocations.get(0),
							Collections.singletonList(createMetric(midVal)),
							null, null);
				}
				invocations = null;
			}
		// there is no chain yet...
		} else {
			// ... but we found a top level invocation
			if (element instanceof CtInvocation) {
				invocations = new ArrayList<>();
				invocations.add((CtInvocation) element);
			}
		}
		super.enter(element);
	}

	@Override
	public CodeSmell.Definition getDefinition() {
		final Threshold th = new Threshold(
				createMetric(threshold),
				Threshold.Relation.GREATER_EQUALS);
		final Thresholds thresholds = new Thresholds(th);
		return new CodeSmell.Definition("Method Chain", thresholds);
	}

	public Metric createMetric(final int val) {
		return new Metric("Method Invocation Distance", val);
	}
}
