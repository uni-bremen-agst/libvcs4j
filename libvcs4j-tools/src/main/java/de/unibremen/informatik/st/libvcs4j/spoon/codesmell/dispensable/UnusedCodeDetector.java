package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.dispensable;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmell;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmellDetector;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Thresholds;
import lombok.NonNull;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.EarlyTerminatingScanner;
import spoon.support.SpoonClassNotFoundException;

import java.util.*;

public class UnusedCodeDetector extends CodeSmellDetector {

	private final boolean strictParameterMode;
	private final boolean privateMode;

	private Map<String, CtTypeReference> types;
	private Map<String, CtExecutableReference> executables;
	private Map<String, CtFieldReference> fields;
	private Set<String> referencedElements;

	/**
	 * Reference to {@link Object}.
	 */
	private CtTypeReference object;

	/**
	 * Reference to {@link Override}.
	 */
	private CtTypeReference<Override> overrideAnnotation;

	/**
	 * Indicates that the currently visited node is part of an abstract method
	 * or non-default method declared by an interface. It is used to exclude
	 * unused parameters declared by methods without implementation.
	 */
	private boolean inAbstractMethod;

	public UnusedCodeDetector(@NonNull final  Revision revision,
			final boolean strictParameterMode, final boolean privateMode)
			throws NullPointerException {
		super(revision);
		this.strictParameterMode = strictParameterMode;
		this.privateMode = privateMode;
	}

	public UnusedCodeDetector(@NonNull final Revision revision)
			throws NullPointerException {
		this(revision, true, true);
	}

	@Override
	public void visitRoot(final CtElement element) {
		types = new HashMap<>();
		executables = new HashMap<>();
		fields = new HashMap<>();
		referencedElements = new HashSet<>();
		object = element.getFactory().Type().OBJECT.clone();
		overrideAnnotation = element.getFactory()
				.Annotation().createReference(Override.class);
		inAbstractMethod = false;

		super.visitRoot(element);

		types.keySet().removeAll(referencedElements);
		types.values().stream()
				.map(CtTypeReference::getDeclaration)
				.forEach(this::addCodeSmell);

		executables.keySet().removeAll(referencedElements);
		executables.values().stream()
				.filter(e -> !overridesReferencedMethod(e))
				.map(CtExecutableReference::getDeclaration)
				.forEach(this::addCodeSmell);

		fields.keySet().removeAll(referencedElements);
		fields.values().stream()
				.map(CtFieldReference::getDeclaration)
				.forEach(this::addCodeSmell);
	}

	private boolean overridesReferencedMethod(
			final CtExecutableReference reference) {
		// declaration must be non-null since `executables` contains
		// only methods of visited classes
		final CtType type = reference.getDeclaringType().getDeclaration();
		final CtTypeReference superClass = getSuperClassIncludingObject(type);
		return overridesReferencedMethod(reference, superClass)
				|| type.getSuperInterfaces().stream().anyMatch(i ->
				overridesReferencedMethod(reference, i));
	}

	private boolean overridesReferencedMethod(
			final CtExecutableReference<?> executableReference,
			final CtTypeReference typeReference) {
		// end of recursion
		if (typeReference == null) {
			return false;
		}

		final boolean isJavaClass =
				typeReference.getQualifiedName().startsWith("java") ||
						typeReference.getQualifiedName().startsWith("javax");

		// load declaration, java classes must be loaded with
		// `getTypeDeclaration`
		CtType<?> type = typeReference.getDeclaration();
		if (type == null && isJavaClass) {
			try {
				type = typeReference.getTypeDeclaration();
			} catch (final SpoonClassNotFoundException e) {
				// there nothing more we can do here
			}
		}

		// we are leaving known classpath, consider methods
		// annotated with @Override to be referenced elsewhere
		if (type == null) {
			return executableReference.getDeclaration()
					.getAnnotation(overrideAnnotation) != null;
		}

		// search for a matching method
		final boolean match = isJavaClass
				// methods defined by java classes and interfaces must not be
				// referenced...
				? type.getMethods().stream()
				.map(CtMethod::getReference)
				.anyMatch(executableReference::isOverriding)
				// ... unlike methods defined by non-java classes
				: type.getMethods().stream()
				.map(CtMethod::getReference)
				.anyMatch(r -> executableReference.isOverriding(r) &&
						referencedElements.contains(r.getSignature()));
		if (match) {
			return true;
		}

		// we reached the top of the class hierarchy, stop searching
		if (typeReference == object) {
			return false;
		}

		// start recursion
		final CtTypeReference superClass = getSuperClassIncludingObject(type);
		return overridesReferencedMethod(executableReference, superClass)
				|| type.getSuperInterfaces().stream().anyMatch(i ->
				overridesReferencedMethod(executableReference, i));
	}

	private CtTypeReference getSuperClassIncludingObject(final CtType type) {
		CtTypeReference superClass = type.getSuperclass();
		if (superClass == null && type instanceof CtClass) {
			superClass = object;
		}
		return superClass;
	}

	private void addCodeSmell(final CtNamedElement unusedElement) {
		addCodeSmell(unusedElement, Collections.emptyList(),
				createSignature(unusedElement).orElse(null));
	}

	@Override
	public CodeSmell.Definition getDefinition() {
		return new CodeSmell.Definition("Unused Code", new Thresholds());
	}

	/////////////////////////// Directly processed ////////////////////////////

	@Override
	public <T> void visitCtLocalVariable(final CtLocalVariable<T> localVar) {
		visitVariable(localVar);
		super.visitCtLocalVariable(localVar);
	}

	@Override
	public <T> void visitCtParameter(CtParameter<T> parameter) {
		final CtExecutable parent = parameter.getParent();
		if (!strictParameterMode ||
				// Strict parameter mode is enabled, but executable is neither
				// empty nor unsupported.
				!isEmpty(parent) && !isUnsupported(parent)) {
			visitVariable(parameter);
		}
		super.visitCtParameter(parameter);
	}

	private <T> void visitVariable(final CtVariable<T> variable) {
		if (inAbstractMethod) {
			return;
		}
		final EarlyTerminatingScanner<CtVariableAccess> scanner =
				new EarlyTerminatingScanner<CtVariableAccess>() {
					@Override
					public <U> void visitCtVariableRead(
							final CtVariableRead<U> variableRead) {
						process(variableRead);
						super.visitCtVariableRead(variableRead);
					}

					@Override
					public <U> void visitCtVariableWrite(
							final CtVariableWrite<U> variableWrite) {
						process(variableWrite);
						super.visitCtVariableWrite(variableWrite);
					}

					private void process(final CtVariableAccess access) {
						if (variable.getSimpleName().equals(
								access.getVariable().getSimpleName())) {
							setResult(access);
							terminate();
						}
					}
				};
		scanner.scan(variable.getParent());
		if (scanner.getResult() == null) {
			addCodeSmell(variable);
		}
	}

	private boolean isUnsupported(final CtExecutable executable) {
		if (executable.getBody().getStatements().size() != 1) {
			return false;
		} else {
			final CtStatement stmt = executable.getBody()
					.getStatements().get(0);
			if (!(stmt instanceof CtThrow)) {
				return false;
			}
			final CtThrow ctThrow = (CtThrow) stmt;
			final CtExpression<? extends Throwable> expr =
					ctThrow.getThrownExpression();
			if (expr == null) {
				return false;
			}
			final CtTypeReference typeRef = expr.getType();
			if (typeRef == null) {
				return false;
			}
			final String name = typeRef.getQualifiedName();
			return name != null && name.equals(
					"java.lang.UnsupportedOperationException");
		}
	}

	private boolean isEmpty(final CtExecutable executable) {
		return executable == null ||
				executable.getBody() == null ||
				executable.getBody().getStatements() == null ||
				executable.getBody().getStatements().isEmpty();
	}

	////////////////////////// Elements of interest ///////////////////////////

	@Override
	public <T> void visitCtClass(final CtClass<T> ctClass) {
		if (!isSuppressedByUnused(ctClass)) {
			if (modifierMatchesMode(ctClass) && !ctClass.isAnonymous()) {
				types.put(ctClass.getQualifiedName(), ctClass.getReference());
			}
			super.visitCtClass(ctClass);
		}
	}

	private boolean isSuppressedByUnused(final CtElement element) {
		SuppressWarnings annotation =
				element.getAnnotation(SuppressWarnings.class);
		return annotation != null && Arrays.stream(annotation.value())
				.anyMatch(s -> s.equals("unused"));
	}

	private boolean modifierMatchesMode(final CtModifiable modifiable) {
		boolean isPrivate = modifiable.getModifiers()
				.contains(ModifierKind.PRIVATE);
		return !(!isPrivate && privateMode);
	}

	@Override
	public <T extends Enum<?>> void visitCtEnum(final CtEnum<T> ctEnum) {
		if (!isSuppressedByUnused(ctEnum)) {
			if (modifierMatchesMode(ctEnum)) {
				types.put(ctEnum.getQualifiedName(), ctEnum.getReference());
			}
			super.visitCtEnum(ctEnum);
		}
	}

	@Override
	public <T> void visitCtInterface(final CtInterface<T> ctInterface) {
		if (!isSuppressedByUnused(ctInterface)) {
			if (modifierMatchesMode(ctInterface)) {
				types.put(ctInterface.getQualifiedName(),
						ctInterface.getReference());
			}
			super.visitCtInterface(ctInterface);
		}
	}

	@Override
	public <T> void visitCtConstructor(final CtConstructor<T> constructor) {
		if (!isSuppressedByUnused(constructor)) {
			if (!constructor.isImplicit()) {
				// exclude private default constructor
				final boolean isPrivateDefaultConstructor =
						constructor.getParameters().size() == 0 &&
								constructor.hasModifier(ModifierKind.PRIVATE);
				if (modifierMatchesMode(constructor) &&
						!isPrivateDefaultConstructor) {
					final CtExecutableReference reference =
							constructor.getReference();
					executables.put(reference.getSignature(), reference);
				}
			}
			super.visitCtConstructor(constructor);
		}
	}

	@Override
	public <T> void visitCtMethod(final CtMethod<T> method) {
		if (!isSuppressedByUnused(method)) {
			inAbstractMethod = method.hasModifier(ModifierKind.ABSTRACT)
					|| method.getDeclaringType().isInterface();
			if (modifierMatchesMode(method) && !inAbstractMethod &&
					isNotMainMethod(method)) {
				final CtExecutableReference reference = method.getReference();
				executables.put(reference.getSignature(), reference);
			}
			super.visitCtMethod(method);
			inAbstractMethod = false;
		}
	}

	private boolean isNotMainMethod(final CtMethod method) {
		return !method.getSimpleName().equals("main");
	}

	@Override
	public <T> void visitCtField(final CtField<T> field) {
		if (!isSuppressedByUnused(field)) {
			if (modifierMatchesMode(field)) {
				final String simpleName = field.getSimpleName();
				if (simpleName != null &&
						!simpleName.equals("serialVersionUID")) {
					final CtFieldReference reference = field.getReference();
					fields.put(reference.getQualifiedName(), reference);
				}
			}
			super.visitCtField(field);
		}
	}

	/////////////////////////// Referenced elements ///////////////////////////

	@Override
	public <T> void visitCtFieldReference(
			final CtFieldReference<T> reference) {
		if (reference.getDeclaringType() != null) {
			referencedElements.add(reference.getQualifiedName());
			referencedElements.add(
					reference.getDeclaringType().getQualifiedName());
		}
		super.visitCtFieldReference(reference);
	}

	@Override
	public <T> void visitCtExecutableReference(
			final CtExecutableReference<T> reference) {
		if (reference.getDeclaringType() != null) {
			referencedElements.add(reference.getSignature());
			referencedElements.add(
					reference.getDeclaringType().getQualifiedName());
		}
		super.visitCtExecutableReference(reference);
	}

	@Override
	public <T> void visitCtTypeReference(
			final CtTypeReference<T> reference) {
		referencedElements.add(reference.getQualifiedName());
		if (reference.getDeclaringType() != null) {
			referencedElements.add(
					reference.getDeclaringType().getQualifiedName());
		}
		super.visitCtTypeReference(reference);
	}
}
