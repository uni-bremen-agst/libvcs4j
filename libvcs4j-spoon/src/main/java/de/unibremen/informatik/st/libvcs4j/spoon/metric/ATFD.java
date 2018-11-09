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
 * This visitor gathers the 'access to foreign data' metric for {@link CtEnum},
 * {@link CtClass}, and {@link CtInterface} elements. Use
 * {@link #ATFDOf(CtType)} to get the metric for a given {@link CtType}.
 */
public class ATFD extends IntMetric {

	/**
	 * The initial metric value.
	 */
	public static final int INITIAL_VALUE = 0;

	/**
	 * Returns the 'access to foreign data' metric of {@code type}. Returns an
	 * empty {@link Optional} if {@code type} is {@code null}.
	 *
	 * @param type
	 * 		The type whose 'access to foreign data' metric is requested.
	 * @return
	 * 		The 'access to foreign data' metric of {@code type}.
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
		final CtType parent = fieldAccess.getParent(CtType.class);
		Optional.ofNullable(parent)
				.map(__ -> fieldAccess.getVariable())
				.map(CtFieldReference::getDeclaration)
				.filter(field -> !isInScopeOf(field, parent))
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
