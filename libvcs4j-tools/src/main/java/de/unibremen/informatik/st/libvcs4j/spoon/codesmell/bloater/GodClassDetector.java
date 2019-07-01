package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.bloater;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.Validate;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmell;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmellDetector;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Metric;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Threshold;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Thresholds;
import de.unibremen.informatik.st.libvcs4j.spoon.metric.ATFD;
import de.unibremen.informatik.st.libvcs4j.spoon.metric.NOA;
import de.unibremen.informatik.st.libvcs4j.spoon.metric.TCC;
import de.unibremen.informatik.st.libvcs4j.spoon.metric.WMC;
import lombok.NonNull;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;

import java.math.BigDecimal;
import java.util.Arrays;

public class GodClassDetector extends CodeSmellDetector {

	public static final int DEFAULT_NOA_THRESHOLD = 5;
	public static final int DEFAULT_WMC_THRESHOLD = 75;
	public static final int DEFAULT_ATFD_THRESHOLD = 5;
	public static final BigDecimal DEFAULT_TCC_THRESHOLD =
			BigDecimal.valueOf(0.1);

	private final int noaThreshold;
	private final int wmcThreshold;
	private final int atfdThreshold;
	private final BigDecimal tccThreshold;

	private final NOA noa = new NOA();
	private final WMC wmc = new WMC();
	private final ATFD atfd = new ATFD();
	private final TCC tcc = new TCC();

	public GodClassDetector(@NonNull final Revision revision,
			final int noaThreshold, final int wmcThreshold,
			final int atfdThreshold, final BigDecimal tccThreshold)
			throws NullPointerException, IllegalArgumentException {
		super(revision);
		this.noaThreshold = Validate.notNegative(noaThreshold);
		this.wmcThreshold = Validate.notNegative(wmcThreshold);
		this.atfdThreshold = Validate.notNegative(atfdThreshold);
		Validate.isTrue(tccThreshold.compareTo(BigDecimal.ZERO) >= 0);
		this.tccThreshold = tccThreshold;
	}

	public GodClassDetector(@NonNull final Revision revision)
			throws NullPointerException, IllegalArgumentException {
		this(revision, DEFAULT_NOA_THRESHOLD, DEFAULT_WMC_THRESHOLD,
				DEFAULT_ATFD_THRESHOLD, DEFAULT_TCC_THRESHOLD);
	}

	@Override
	public void visitRoot(final CtElement element) {
		noa.scan(element);
		wmc.scan(element);
		atfd.scan(element);
		tcc.scan(element);
		super.visitRoot(element);
	}

	@Override
	public <T> void visitCtClass(final CtClass<T> ctClass) {
		visitType(ctClass);
		super.visitCtClass(ctClass);
	}

	@Override
	public <T> void visitCtInterface(final CtInterface<T> ctInterface) {
		visitType(ctInterface);
		super.visitCtInterface(ctInterface);
	}

	private void visitType(final CtType type) {
		final int noaVal = noa.NOAOf(type)
				.orElseThrow(IllegalStateException::new);
		final int wmcVal = wmc.WMCOf(type)
				.orElseThrow(IllegalStateException::new);
		final int atfdVal = atfd.ATFDOf(type)
				.orElseThrow(IllegalStateException::new);
		final BigDecimal tccVal = tcc.TCCOf(type)
				.orElseThrow(IllegalStateException::new);
		if (noaVal >= noaThreshold
				&& wmcVal >= wmcThreshold
				&& atfdVal >= atfdThreshold
				&& tccVal.compareTo(tccThreshold) >= 0) {
			addCodeSmell(type, Arrays.asList(
						createNOAMetric(noaVal),
						createWMCMetric(wmcVal),
						createATFDMetric(atfdVal),
						createTCCMetric(tccVal)),
					createSignature(type).orElse(null), null);
			}
	}

	@Override
	public CodeSmell.Definition getDefinition() {
		final Threshold nth = new Threshold(
				createNOAMetric(noaThreshold),
				Threshold.Relation.GREATER_EQUALS);
		final Threshold wth = new Threshold(
				createWMCMetric(wmcThreshold),
				Threshold.Relation.GREATER_EQUALS);
		final Threshold ath = new Threshold(
				createATFDMetric(atfdThreshold),
				Threshold.Relation.GREATER_EQUALS);
		final Threshold tth = new Threshold(
				createTCCMetric(tccThreshold),
				Threshold.Relation.GREATER_EQUALS);

		final Thresholds thresholds = new Thresholds(
				Arrays.asList(nth, wth, ath, tth),
				Thresholds.Connective.AND);

		return new CodeSmell.Definition("God Class", thresholds);
	}

	public Metric createNOAMetric(final int val) {
		return new Metric(noa.name(), val);
	}

	public Metric createWMCMetric(final int val) {
		return new Metric(wmc.name(), val);
	}

	public Metric createATFDMetric(final int val) {
		return new Metric(atfd.name(), val);
	}

	public Metric createTCCMetric(final BigDecimal val) {
		return new Metric(tcc.name(), val, true);
	}
}
