package de.unibremen.informatik.st.libvcs4j.spoon.metric;

import de.unibremen.informatik.st.libvcs4j.Validate;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
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
	 * Maps a type `t` to its methods, which in turn are mapped to their field
	 * accesses referencing a field of `t`.
	 */
	private Map<CtType, Map<CtMethod, Set<CtField>>>
			typeInfo = new IdentityHashMap<>();

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
		super.visitCtClass(ctClass);
		visitCtType(ctClass);
	}

	@Override
	public <T> void visitCtInterface(final CtInterface<T> ctInterface) {
		super.visitCtInterface(ctInterface);
		visitCtType(ctInterface);
	}

	@Override
	public <T extends Enum<?>> void visitCtEnum(final CtEnum<T> ctEnum) {
		super.visitCtEnum(ctEnum);
		visitCtType(ctEnum);
	}

	private void visitCtType(final CtType type) {
		final Map<CtMethod, Set<CtField>> fas = typeInfo.get(type);
		if (fas != null) {
			final List<CtMethod> methods = new ArrayList<>(fas.keySet());
			final int numMethods = methods.size();
			final int totalPairs = numMethods * (numMethods + 1);
			int pairs = 0;
			for (int i = 0; i < numMethods; i++) {
				for (int j = i + 1; j < numMethods; j++) {
					final CtMethod m1 = methods.get(i);
					final CtMethod m2 = methods.get(j);
					Validate.validateState(m1 != m2);
					final Set<CtField> fields = new HashSet<>(fas.get(m1));
					fields.retainAll(fas.get(m2));
					if (!fields.isEmpty()) {
						pairs++;
					}
				}
			}
		}
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
				.filter(field -> isInScopeOf(field, type))
				.ifPresent(field -> {
					final CtMethod method = field.getParent(CtMethod.class);
					if (!method.isPrivate()) {
						typeInfo.computeIfAbsent(type,
										__ -> new IdentityHashMap<>())
								.computeIfAbsent(method,
										__ -> new HashSet<>())
								.add(field);
					}
				});
	}
}
