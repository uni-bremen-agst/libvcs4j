package de.unibremen.informatik.st.libvcs4j.spoon.metric;

import de.unibremen.informatik.st.libvcs4j.Validate;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtFieldReference;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This scanner gathers the 'Tight Class Cohesion' metric for {@link CtClass},
 * {@link CtInterface}, and {@link CtEnum} elements.
 */
public class TCC extends DecimalGatherer {

	/**
	 * The initial metric value.
	 */
	private static final BigDecimal INITIAL_VALUE = BigDecimal.ZERO;

	/**
	 * Maps a type `t` to its methods, which in turn are mapped to the fields
	 * they are accessing, which in turn are in scope of `t`.
	 */
	private Map<CtType, Map<CtMethod, Set<CtField>>>
			typeInfo = new IdentityHashMap<>();

	@Override
	public void visitRoot(final CtElement element) {
		typeInfo.clear();
		super.visitRoot(element);
	}

	@Override
	public String name() {
		return "Tight Class Cohesion";
	}

	@Override
	public String abbreviation() {
		return "TCC";
	}

	/**
	 * Returns the 'Tight Class Cohesion' metric of {@code type}. Returns an
	 * empty {@link Optional} if {@code type} is {@code null}, or if
	 * {@code type} was not scanned.
	 *
	 * @param type
	 * 		The type whose 'Tight Class Cohesion' metric is requested.
	 * @return
	 * 		The 'Tight Class Cohesion' metric of {@code type}.
	 */
	public Optional<BigDecimal> TCCOf(final CtType type) {
		return metricOf(type);
	}

	@Override
	public <T> void visitCtClass(final CtClass<T> ctClass) {
		typeInfo.put(ctClass, new IdentityHashMap<>());
		visitNode(ctClass, super::visitCtClass, this::visitType,
				(__, parent) -> parent, INITIAL_VALUE);
	}

	@Override
	public <T> void visitCtInterface(final CtInterface<T> ctInterface) {
		typeInfo.put(ctInterface, new IdentityHashMap<>());
		visitNode(ctInterface, super::visitCtInterface, this::visitType,
				(__, parent) -> parent, INITIAL_VALUE);
	}

	@Override
	public <T extends Enum<?>> void visitCtEnum(final CtEnum<T> ctEnum) {
		typeInfo.put(ctEnum, new IdentityHashMap<>());
		visitNode(ctEnum, super::visitCtEnum, this::visitType,
				(__, parent) -> parent, INITIAL_VALUE);
	}

	private void visitType(final CtType type) {
		final Map<CtMethod, Set<CtField>> ti = typeInfo.get(type);
		if (ti != null) {
			final List<CtMethod> methods = new ArrayList<>(ti.keySet());
			final int numMethods = methods.size();
			final int totalPairs = (numMethods * (numMethods - 1)) / 2;
			int pairs = 0;
			for (int i = 0; i < numMethods; i++) {
				for (int j = i + 1; j < numMethods; j++) {
					final CtMethod m1 = methods.get(i);
					final CtMethod m2 = methods.get(j);
					Validate.validateState(m1 != m2);
					final Set<CtField> fields = new HashSet<>(ti.get(m1));
					fields.retainAll(ti.get(m2));
					if (!fields.isEmpty()) {
						pairs++;
					}
				}
			}
			final BigDecimal tcc = totalPairs == 0 || pairs == 0
					? BigDecimal.ZERO: BigDecimal.valueOf(
							(double) pairs/ (double) totalPairs);
			inc(tcc);
		}
	}

	@Override
	public <T> void visitCtMethod(final CtMethod<T> method) {
		typeInfo.get(method.getParent(CtType.class))
				.put(method, new HashSet<>());
		super.visitCtMethod(method);
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
		if (type != null && typeInfo.containsKey(type)) {
			final Optional<CtField> field = Optional
					.ofNullable(fieldAccess.getVariable())
					.map(CtFieldReference::getDeclaration)
					.filter(f -> isInScopeOf(f, type));
			final Optional<CtMethod> method = field
					.map(f -> fieldAccess.getParent(CtMethod.class))
					.filter(CtModifiable::isPublic);
			if (field.isPresent() && method.isPresent()) {
				typeInfo.get(type).computeIfAbsent(
						method.get(), __ -> new HashSet<>())
						.add(field.get());
			}
		}
	}

	@Override
	public <T> void visitCtInvocation(final CtInvocation<T> invocation) {
		resolveToMethod(invocation)
				.map(this::resolveToFieldAccess)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(CtFieldAccess::getVariable)
				.map(CtFieldReference::getDeclaration)
				.ifPresent(field -> {
					final CtType type = invocation.getParent(CtType.class);
					final CtMethod met =invocation.getParent(CtMethod.class);
					typeInfo.get(type).computeIfAbsent(
							met, __ -> new HashSet<>()).add(field);
				});
		super.visitCtInvocation(invocation);
	}
}
