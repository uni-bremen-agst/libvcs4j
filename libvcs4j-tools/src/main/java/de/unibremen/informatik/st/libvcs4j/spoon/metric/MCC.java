package de.unibremen.informatik.st.libvcs4j.spoon.metric;

import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * This scanner gathers the 'McCabe Complexity' metric for {@link CtClass},
 * {@link CtInterface} (since Java 8 interfaces may have default
 * implementations), {@link CtEnum}, {@link CtAnnotation}, {@link CtMethod},
 * {@link CtConstructor}, and {@link CtCase} elements.
 */
public class MCC extends IntGatherer {

	/**
	 * The initial metric value.
	 */
	private static final int INITIAL_VALUE = 1;

	@Override
	public String name() {
		return "McCabe Complexity";
	}

	@Override
	public String abbreviation() {
		return "MCC";
	}

	/**
	 * Returns the 'McCabe Complexity' metric of {@code type}. Returns an empty
	 * {@link Optional} if {@code type} is {@code null}, or if {@code type} was
	 * not scanned.
	 *
	 * @param type
	 * 		The type whose 'McCabe Complexity' metric is requested.
	 * @return
	 * 		The 'McCabe Complexity' metric of {@code type}.
	 */
	public Optional<Integer> MCCOf(final CtType type) {
		return metricOf(type);
	}

	/**
	 * Returns the 'McCabe Complexity' metric of {@code annotation}. Returns an
	 * empty {@link Optional} if {@code annotation} is {@code null}, or if
	 * {@code annotation} was not scanned.
	 *
	 * @param annotation
	 * 		The annotation whose 'McCabe Complexity' metric is requested.
	 * @return
	 * 		The 'McCabe Complexity' metric of {@code annotation}.
	 */
	public Optional<Integer> MCCOf(final CtAnnotation annotation) {
		return metricOf(annotation);
	}

	/**
	 * Returns the 'McCabe Complexity' metric of {@code executable}. Returns an
	 * empty {@link Optional} if {@code executable} is {@code null}, or if
	 * {@code executable} was not scanned.
	 *
	 * @param executable
	 * 		The expression whose 'McCabe Complexity' metric is requested.
	 * @return
	 * 		The 'McCabe Complexity' metric of {@code executable}.
	 */
	public Optional<Integer> MCCOf(final CtExecutable executable) {
		return metricOf(executable);
	}

	/**
	 * Returns the 'McCabe Complexity' metric of {@code ctCase}. Returns an
	 * empty {@link Optional} if {@code ctCase} is {@code null}, or if
	 * {@code ctCase} was not scanned.
	 *
	 * @param ctCase
	 * 		The case statement whose 'McCabe Complexity' metric is requested.
	 * @return
	 * 		The 'McCabe Complexity' metric of {@code ctCase}.
	 */
	public Optional<Integer> MCCOf(final CtCase ctCase) {
		return metricOf(ctCase);
	}

	@Override
	public <T> void visitCtClass(final CtClass<T> pClass) {
		visitNode(pClass, super::visitCtClass,
				Integer::sum, INITIAL_VALUE);
	}

	@Override
	public <T> void visitCtInterface(final CtInterface<T> ctInterface) {
		visitNode(ctInterface, super::visitCtInterface,
				Integer::sum, INITIAL_VALUE);
	}

	@Override
	public <T extends Enum<?>> void visitCtEnum(final CtEnum<T> ctEnum) {
		visitNode(ctEnum, super::visitCtEnum,
				Integer::sum, INITIAL_VALUE);
	}

	@Override
	public <A extends Annotation> void visitCtAnnotation(
			final CtAnnotation<A> ctAnnotation) {
		visitNode(ctAnnotation, super::visitCtAnnotation,
				Integer::sum, INITIAL_VALUE);
	}

	@Override
	public <T> void visitCtMethod(final CtMethod<T> method) {
		visitNode(method, super::visitCtMethod,
				Integer::sum, INITIAL_VALUE);
	}

	@Override
	public <T> void visitCtConstructor(final CtConstructor<T> constructor) {
		visitNode(constructor, super::visitCtConstructor,
				Integer::sum, INITIAL_VALUE);
	}

	@Override
	public <T> void visitCtAssert(final CtAssert<T> ctAssert) {
		inc();
		super.visitCtAssert(ctAssert);
	}

	@Override
	public <T> void visitCtConditional(final CtConditional<T> conditional) {
		// a conditional is a ternary expressions
		inc();
		super.visitCtConditional(conditional);
	}

	@Override
	public void visitCtIf(final CtIf ctIf) {
		inc();
		super.visitCtIf(ctIf);
	}

	@Override
	public <T> void visitCtSwitch(final CtSwitch<T> ctSwitch) {
		inc(ctSwitch.getCases().size());
		super.visitCtSwitch(ctSwitch);
	}

	@Override
	public void visitCtDo(final CtDo doLoop) {
		inc();
		super.visitCtDo(doLoop);
	}

	@Override
	public void visitCtForEach(final CtForEach forEachLoop) {
		inc();
		super.visitCtForEach(forEachLoop);
	}

	@Override
	public void visitCtFor(final CtFor forLoop) {
		inc();
		super.visitCtFor(forLoop);
	}

	@Override
	public void visitCtWhile(final CtWhile whileLoop) {
		inc();
		super.visitCtWhile(whileLoop);
	}

	@Override
	public <S> void visitCtCase(final CtCase<S> caseStatement) {
		visitNode(caseStatement, super::visitCtCase,
				(c, p) -> (c - 1) + p, INITIAL_VALUE);
	}
}
