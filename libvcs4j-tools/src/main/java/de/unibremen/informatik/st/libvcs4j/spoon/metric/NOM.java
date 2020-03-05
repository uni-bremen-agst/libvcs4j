package de.unibremen.informatik.st.libvcs4j.spoon.metric;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;

import java.util.Optional;

/**
 * This scanner gathers the 'Number of Methods' metric for {@link CtInterface},
 * {@link CtClass}, and {@link CtEnum} elements.
 */
public class NOM extends IntGatherer {

	@Override
	public String name() {
		return "Number of Methods";
	}

	@Override
	public String abbreviation() {
		return "NOM";
	}

	/**
	 * Returns the 'Number of Methods' metric of {@code type}. Returns an empty
	 * {@link Optional} if {@code type} is {@code null}, or if {@code type} was
	 * not scanned.
	 *
	 * @param type
	 * 		The type whose 'Number of Methods' metric is requested.
	 * @return
	 * 		The 'Number of Methods' metric of {@code type}.
	 */
	public Optional<Integer> NOMOf(final CtType type) {
		return metricOf(type);
	}

	@Override
	public <T> void visitCtClass(final CtClass<T> ctClass) {
		visitNode(ctClass, super::visitCtClass,
				(__, parent) -> parent, ctClass.getMethods().size());
	}

	@Override
	public <T> void visitCtInterface(final CtInterface<T> ctInterface) {
		visitNode(ctInterface, super::visitCtInterface,
				(__, parent) -> parent, ctInterface.getMethods().size());
	}

	@Override
	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
		visitNode(ctEnum, super::visitCtEnum,
				(__, parent) -> parent, ctEnum.getMethods().size());
	}
}
