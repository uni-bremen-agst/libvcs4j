package de.unibremen.informatik.st.libvcs4j.spoon.metric;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

import java.util.Optional;

/**
 * This scanner gathers the 'Weighted Methods per Class' metric for
 * {@link CtClass}, {@link CtInterface}, and {@link CtEnum} elements.
 */
public class WMC extends IntGatherer {

	/**
	 * Weight methods and constructors with their MCC metric.
	 */
	private final MCC mcc = new MCC();

	@Override
	protected String name() {
		return "WMC";
	}

	@Override
	protected String abbreviation() {
		return "Weighted Methods per Class";
	}

	/**
	 * Returns the 'Weighted Methods per Class' metric of {@code type}. Returns
	 * an empty {@link Optional} if {@code type} is {@code null}, or if
	 * {@code type} was not scanned.
	 *
	 * @param type
	 * 		The type whose 'Weighted Methods per Class' metric is requested.
	 * @return
	 * 		The 'Weighted Methods per Class' metric of {@code type}.
	 */
	public Optional<Integer> WMCOf(final CtType type) {
		return metricOf(type);
	}

	@Override
	public void visitRoot(final CtElement element) {
		mcc.scan(element);
		super.visitRoot(element);
	}

	@Override
	public <T> void visitCtClass(final CtClass<T> ctClass) {
		visitNode(ctClass, Propagation.NONE,
				mcc.metricOf(ctClass).map(i -> i - 1)
				.orElseThrow(IllegalStateException::new));
	}

	@Override
	public <T> void visitCtInterface(final CtInterface<T> ctInterface) {
		visitNode(ctInterface, Propagation.NONE,
				mcc.metricOf(ctInterface).map(i -> i - 1)
				.orElseThrow(IllegalStateException::new));
	}

	@Override
	public <T extends Enum<?>> void visitCtEnum(final CtEnum<T> ctEnum) {
		visitNode(ctEnum, Propagation.NONE,
				mcc.metricOf(ctEnum).map(i -> i - 1)
				.orElseThrow(IllegalStateException::new));
	}

	@Override
	public <T> void visitCtMethod(final CtMethod<T> pMethod) {
		if (!pMethod.isImplicit()) {
			inc();
		}
		super.visitCtMethod(pMethod);
	}

	@Override
	public <T> void visitCtConstructor(final CtConstructor<T> pConstructor) {
		if (!pConstructor.isImplicit()) {
			inc();
		}
		super.visitCtConstructor(pConstructor);
	}
}
