package de.unibremen.informatik.st.libvcs4j.spoon.metric;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;

import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * This scanner gathers the 'Number of Attributes' metric for {@link CtClass},
 * {@link CtInterface}, {@link CtEnum}, and {@link CtAnnotation} (usually named
 * values) elements.
 */
public class NOA extends IntGatherer {

	@Override
	public String name() {
		return "Number of Attributes";
	}

	@Override
	public String abbreviation() {
		return "NOA";
	}

	/**
	 * Returns the 'Number of Attributes' metric of {@code type}. Returns an
	 * empty {@link Optional} if {@code type} is {@code null}, or if
	 * {@code type} was not scanned.
	 *
	 * @param type
	 * 		The type whose 'Number of Attributes' metric is requested.
	 * @return
	 * 		The 'Number of Attributes' metric of {@code type}.
	 */
	public Optional<Integer> NOAOf(final CtType type) {
		return metricOf(type);
	}

	/**
	 * Returns the 'number of attributes' (usually named values) metric of
	 * {@code annotation}. Returns an empty {@link Optional} if
	 * {@code annotation} is {@code null}, or if {@code annotation} was not
	 * scanned.
	 *
	 * @param annotation
	 * 		The annotation whose 'number of attributes' metric is requested.
	 * @return
	 * 		The 'number of attributes' metric of {@code annotation}.
	 */
	public Optional<Integer> NOAOf(final CtAnnotation annotation) {
		return metricOf(annotation);
	}

	@Override
	public <T> void visitCtClass(final CtClass<T> ctClass) {
		visitNode(ctClass, Propagation.NONE, ctClass.getFields().size());
	}

	@Override
	public <T> void visitCtInterface(final CtInterface<T> ctInterface) {
		visitNode(ctInterface, Propagation.NONE,
				ctInterface.getFields().size());
	}

	@Override
	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
		visitNode(ctEnum, Propagation.NONE, ctEnum.getFields().size());
	}

	@Override
	public <A extends Annotation> void visitCtAnnotation(
			final CtAnnotation<A> annotation) {
		visitNode(annotation, Propagation.NONE, annotation.getValues().size());
	}
}
