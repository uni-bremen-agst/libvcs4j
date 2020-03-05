package de.unibremen.informatik.st.libvcs4j.spoon;

import de.unibremen.informatik.st.libvcs4j.Validate;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.CtScanner;

import java.util.Optional;

/**
 * Extends Spoon's {@link CtScanner} and provides further features. This class,
 * and all its subclasses, are NOT threadsafe! That is, one should NOT call
 * {@link #scan(CtElement)} on the same object from multiple threads.
 */
@RequiredArgsConstructor
public class Scanner extends CtScanner {

	/**
	 * The regex for getter and setter method names.
	 */
	private static final String GETTER_SETTER_REGEX = "get.*|set.*|is.*";

	/**
	 * The cache that is used to speedup lookups.
	 */
	@Getter
	@NonNull
	private final Cache cache;

	/**
	 * Is used to determine whether {@link #visitRoot(CtElement)} has already
	 * been called.
	 */
	private boolean initialized = false;

	/**
	 * Creates a scanner with a new cache.
	 */
	public Scanner() {
		cache = new Cache();
	}

	////////////////////////// Traversing utilities. //////////////////////////

	/**
	 * Scans the given spoon model. Does nothing if {@code model} is
	 * {@code null}.
	 *
	 * @param model
	 * 		The model to scan.
	 */
	public void scan(final CtModel model) {
		if (model != null) {
			scan(model.getRootPackage());
		}
	}

	@Override
	public void scan(final CtElement element) {
		if (!initialized) {
			visitRoot(element);
		} else {
			super.scan(element);
		}
	}

	/**
	 * Visits the root element of the scanned (sub-)AST.
	 *
	 * @param element
	 * 		The root element of the scanned (sub-)AST.
	 */
	public void visitRoot(final CtElement element) {
		initialized = true;
		scan(element);
		initialized = false;
	}

	//////////////////////////// Method utilities. ////////////////////////////

	/**
	 * Tries to resolve {@code invocation} to a {@link CtMethod}. Returns an
	 * empty {@link Optional} if {@code invocation} is {@code null}, is not
	 * resolvable, or does not target a {@link CtMethod}.
	 *
	 * @param invocation
	 * 		The invocation to resolve.
	 * @return
	 * 		The resolved {@link CtMethod}.
	 */
	public Optional<CtMethod> resolveToMethod(final CtInvocation invocation) {
		return Optional.of(invocation)
				.map(CtInvocation::getExecutable)
				.filter(ref -> !ref.isConstructor()
						/* SPOON: fixes null pointer */
						&& ref.getDeclaringType() != null)
				.map(ref -> getCache().getOrResolve(ref))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.filter(decl -> decl instanceof CtMethod)
				.map(decl -> (CtMethod) decl);
	}

	/**
	 * Tries to resolve {@code method} to a {@link CtFieldAccess}. Returns an
	 * empty {@link Optional} if either {@code method} or its body (see
	 * {@link CtMethod#getBody()}) is {@code null}, or if {@code method} does
	 * not consist of a single {@link CtReturn} or {@link CtAssignment}
	 * statement that references a {@link spoon.reflect.declaration.CtField}.
	 *
	 * @param method
	 * 		The method to resolve.
	 * @return
	 * 		The {@link CtFieldAccess} of {@code method}.
	 */
	public Optional<CtFieldAccess> resolveToFieldAccess(final CtMethod method) {
		return Optional.ofNullable(method)
				.map(CtMethod::getBody)
				.map(CtBlock::getStatements)
				.filter(stmts -> stmts.size() == 1)
				.map(stmts -> stmts.get(0))
				.map(stmt -> {
					CtExpression expr = null;
					if (stmt instanceof CtReturn) {
						expr = ((CtReturn) stmt).getReturnedExpression();
					} else if (stmt instanceof CtAssignment) {
						expr = ((CtAssignment) stmt).getAssigned();
					}
					return expr;
				})
				.filter(expr -> expr instanceof CtFieldAccess)
				.map(expr -> (CtFieldAccess) expr);
	}

	/**
	 * Determines whether {@code method} is a field access method
	 * (getter/setter) (using {@link #resolveToFieldAccess(CtMethod)}). If
	 * {@link #resolveToFieldAccess(CtMethod)} returns an empty
	 * {@link Optional} the name of {@code method} is matched with
	 * {@link #GETTER_SETTER_REGEX}. If the name matches, {@code method} is
	 * considered a field access method.
	 *
	 * @param method
	 * 		The method to analyse.
	 * @return
	 * 		{@code true} if {@code method} is a field access method
	 * 		(getter/setter), {@code false} otherwise.
	 */
	public boolean isFieldAccess(final CtMethod method) {
		final Optional<String> name = Optional.ofNullable(method)
				.map(CtNamedElement::getSimpleName);
		return resolveToFieldAccess(method).isPresent() ||
				// Abstract method or interface.
				name.isPresent() && name.get().matches(GETTER_SETTER_REGEX);
	}

	/**
	 * Determines whether {@code invocation} targets a field access method
	 * (getter/setter) (using {@link #resolveToMethod(CtInvocation)} and
	 * {@link #isFieldAccess(CtMethod)}). If
	 * {@link #resolveToMethod(CtInvocation)} returns am empty
	 * {@link Optional}, or if {@link #isFieldAccess(CtMethod)} returns
	 * {@code false}, the name of the target of {@code invocation} is matched
	 * with {@link #GETTER_SETTER_REGEX}. If the name matches,
	 * {@code invocation} is considered a field access invocation.
	 *
	 * @param invocation
	 * 		The invocation to analyse.
	 * @return
	 * 		{@code true} if {@code invocation} targets a field access method
	 * 		(getter/setter), {@code false} otherwise.
	 */
	public boolean isFieldAccess(final CtInvocation invocation) {
		return Optional.ofNullable(invocation)
				.map(this::resolveToMethod)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(this::isFieldAccess)
				.filter(Boolean::booleanValue)
				.isPresent() ||
					Optional.ofNullable(invocation)
					.map(CtInvocation::getExecutable)
					.map(CtExecutableReference::getSimpleName)
					.map(name -> name.matches(GETTER_SETTER_REGEX))
					.filter(Boolean::booleanValue)
					.isPresent();
	}

	//////////////////////////// Scope utilities. /////////////////////////////

	/**
	 * Returns whether {@code element} is in scope of {@code scope}. An element
	 * `n` is in scope of another element `m` if either `n` == `m` or if `m` is
	 * a parent of `n` (similar to {@link CtElement#hasParent(CtElement)}).
	 *
	 * @param element
	 * 			The element that potentially is in scope of {@code scope}.
	 * @param scope
	 * 			The scope to check.
	 * @return
	 * 			{@code true} if {@code element} is in scope of {@code scope},
	 * 			{@code false} otherwise.
	 * @throws NullPointerException
	 * 			If any of the given arguments is {@code null}.
	 */
	public boolean isInScopeOf(final CtElement element, final CtElement scope)
			throws NullPointerException {
		Validate.notNull(element);
		Validate.notNull(scope);
		CtElement current = element;
		do {
			if (current == scope) { // n == m
				return true;
			}
			current = current.getParent();
		} while (current != null);
		return false;
	}
}
