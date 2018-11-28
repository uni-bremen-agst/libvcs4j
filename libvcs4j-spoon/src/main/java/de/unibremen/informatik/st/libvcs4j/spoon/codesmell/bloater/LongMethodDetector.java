package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.bloater;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.Validate;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmell;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmellDetector;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Metric;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Threshold;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Thresholds;
import lombok.NonNull;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

public class LongMethodDetector extends CodeSmellDetector {

	private static final int DEFAULT_THRESHOLD = 30;

	private final int threshold;

	public LongMethodDetector(@NonNull Revision revision,
			final int threshold) throws NullPointerException,
			IllegalArgumentException {
		super(revision);
		this.threshold = Validate.notNegative(threshold);
	}

	public LongMethodDetector(@NonNull Revision revision)
			throws NullPointerException, IllegalArgumentException {
		this(revision, DEFAULT_THRESHOLD);
	}

	@Override
	public <T> void visitCtMethod(final CtMethod<T> pMethod) {
		visitExecutable(pMethod);
		super.visitCtMethod(pMethod);
	}

	@Override
	public <T> void visitCtConstructor(final CtConstructor<T> constructor) {
		visitExecutable(constructor);
		super.visitCtConstructor(constructor);
	}

	private <T> void visitExecutable(final CtExecutable<T> executable) {
		Optional.ofNullable(executable.getBody())
				.map(body -> body.getStatements().stream()
						.filter(ctStatement -> !ctStatement.isImplicit())
						.map(CtElement::getPosition)
						.filter(p -> !p.equals(SourcePosition.NOPOSITION))
						.map(p -> p.getEndLine() - p.getLine() + 1)
						.reduce(0, Integer::sum))
				.filter(size -> size >= threshold)
				.ifPresent(val -> addCodeSmell(executable,
						Collections.singletonList(createMetric(val)),
						createSignature(executable).orElse(null)));

	}

	@Override
	public CodeSmell.Definition getDefinition() {
		final Threshold threshold = new Threshold(
				createMetric(this.threshold),
				Threshold.Relation.GREATER_EQUALS);
		final Thresholds thresholds = new Thresholds(threshold);
		return new CodeSmell.Definition("Long Method", thresholds);
	}

	private Metric createMetric(final int val) {
		return new Metric("SLOC", val);
	}
}
