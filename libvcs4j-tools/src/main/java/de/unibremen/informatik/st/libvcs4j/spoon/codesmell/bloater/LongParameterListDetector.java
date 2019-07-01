package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.bloater;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.Validate;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmell;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmellDetector;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Metric;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Threshold;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Thresholds;
import de.unibremen.informatik.st.libvcs4j.spoon.metric.NOP;
import lombok.NonNull;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;

import java.util.Collections;
import java.util.List;

public class LongParameterListDetector extends CodeSmellDetector {

	public static final int DEFAULT_THRESHOLD = 5;

	private final int threshold;

	private final NOP nop = new NOP();

	public LongParameterListDetector(@NonNull final Revision revision,
			final int threshold) throws NullPointerException,
			IllegalArgumentException {
		super(revision);
		this.threshold = Validate.notNegative(threshold);
	}

	public LongParameterListDetector(Revision revision) {
		this(revision, DEFAULT_THRESHOLD);
	}

	@Override
	public void visitRoot(final CtElement element) {
		nop.scan(element);
		super.visitRoot(element);
	}

	@Override
	public <T> void visitCtMethod(final CtMethod<T> method) {
		visitExecutable(method);
		super.visitCtMethod(method);
	}

	@Override
	public <T> void visitCtConstructor(final CtConstructor<T> constructor) {
		visitExecutable(constructor);
		super.visitCtConstructor(constructor);
	}

	private void visitExecutable(CtExecutable<?> executable) {
		final int val = nop.NOPOf(executable)
				.orElseThrow(IllegalStateException::new);
		if (val >= threshold) {
			final List<CtParameter<?>> params = executable.getParameters();
			addCodeSmellRange(params.get(0), params.get(params.size() - 1),
					Collections.singletonList(createMetric(val)), null, null);
		}
	}

	@Override
	public CodeSmell.Definition getDefinition() {
		final Threshold th = new Threshold(
				createMetric(threshold),
				Threshold.Relation.GREATER_EQUALS);
		final Thresholds thresholds = new Thresholds(th);
		return new CodeSmell.Definition("Long Parameter List", thresholds);
	}

	public Metric createMetric(final int val) {
		return new Metric(nop.name(), val);
	}
}
