package de.unibremen.informatik.st.libvcs4j.spoon.metric;

import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtFieldReference;

import java.util.Optional;

/**
 * This scanner gathers the 'Access to Foreign Data' metric for {@link CtEnum},
 * {@link CtClass}, and {@link CtInterface} elements.
 */
public class ATFD extends IntGatherer {

	/**
	 * The initial metric value.
	 */
	public static final int INITIAL_VALUE = 0;

	@Override
	protected String name() {
		return "ATFD";
	}

	@Override
	protected String abbreviation() {
		return "Access to Foreign Data";
	}

	/**
	 * Returns the 'Access to Foreign Data' metric of {@code type}. Returns an
	 * empty {@link Optional} if {@code type} is {@code null}, or if
	 * {@code type} was not scanned.
	 *
	 * @param type
	 * 		The type whose 'Access to Foreign Data' metric is requested.
	 * @return
	 * 		The 'Access to Foreign Data' metric of {@code type}.
	 */
	public Optional<Integer> ATFDOf(final CtType type) {
		return metricOf(type);
	}

	@Override
	public <T> void visitCtClass(final CtClass<T> ctClass) {
		visitNode(ctClass, Propagation.SUM, INITIAL_VALUE);
	}

	@Override
	public <T> void visitCtInterface(final CtInterface<T> ctInterface) {
		visitNode(ctInterface, Propagation.SUM, INITIAL_VALUE);
	}

	@Override
	public <T extends Enum<?>> void visitCtEnum(final CtEnum<T> ctEnum) {
		visitNode(ctEnum, Propagation.SUM, INITIAL_VALUE);
	}

	@Override
	public <T> void visitCtFieldRead(final CtFieldRead<T> fieldRead) {
		visitCtFieldAccess(fieldRead);
		super.visitCtFieldRead(fieldRead);
	}

	@Override
	public <T> void visitCtFieldWrite(final CtFieldWrite<T> fieldWrite) {
		visitCtFieldAccess(fieldWrite);
		super.visitCtFieldWrite(fieldWrite);
	}

	private void visitCtFieldAccess(final CtFieldAccess fieldAccess) {
		final CtType type = fieldAccess.getParent(CtType.class);
		Optional.ofNullable(type)
				.map(__ -> fieldAccess.getVariable())
				.map(CtFieldReference::getDeclaration)
				.filter(field -> !isInScopeOf(field, type))
				.ifPresent(__ -> inc());
	}

	@Override
	public <T> void visitCtInvocation(final CtInvocation<T> invocation) {
		final CtType parent = invocation.getParent(CtType.class);
		Optional.ofNullable(parent)
				.map(__ -> resolveToMethod(invocation))
				.map(o -> o.orElse(null))
				.filter(this::isFieldAccess)
				.filter(method -> !isInScopeOf(method, parent))
				.ifPresent(__ -> inc());
		super.visitCtInvocation(invocation);
	}
}
