package de.unibremen.informatik.st.libvcs4j.spoon;

import de.unibremen.informatik.st.libvcs4j.Validate;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtScanner;

import java.util.Optional;

/**
 * Extends Spoon's {@link CtScanner} and provides further features. This class,
 * and all its subclasses, are NOT threadsafe! That is, one should NOT call
 * {@link #scan(CtElement)} on the same object from multiple threads.
 */
public class Scanner extends CtScanner {

	/**
	 * The regex for getter and setter method names.
	 */
	private static final String GETTER_SETTER_REGEX = "get.*|set.*|is.*";

	/**
	 * Is used to determine whether {@link #visitRoot(CtElement)} has already
	 * been called.
	 */
	private boolean initialized = false;

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

	/**
	 * Tries to resolve the given invocation to a {@link CtMethod}. Returns an
	 * empty {@link Optional} if {@code invocation} is {@code null}, is not
	 * resolvable, or is not a {@link CtMethod}.
	 *
	 * @param invocation
	 * 			The invocation which may call a method.
	 * @return
	 * 			The resolved {@link CtMethod}.
	 */
	public Optional<CtMethod> resolveToMethod(final CtInvocation invocation) {
		return Optional.of(invocation)
				.map(CtInvocation::getExecutable)
				.filter(ref -> !ref.isConstructor()
						/* SPOON: fixes null pointer */
						&& ref.getDeclaringType() != null)
				.map(CtReference::getDeclaration)
				.filter(decl -> decl instanceof CtMethod)
				.map(decl -> (CtMethod) decl);
	}

	/**
	 * Tries to resolve the given method to a {@link CtFieldAccess}. Returns an
	 * empty {@link Optional} if either {@code method} or its body (see
	 * {@link CtMethod#getBody()}) is {@code null}, or if {@code method} does
	 * not consist of a single {@link CtReturn} or {@link CtAssignment}
	 * referencing a field.
	 *
	 * This method may be used to get the field that is accessed by a getter or
	 * setter function. Use {@link #isFieldAccess(CtMethod)} to heuristically
	 * determine whether a method without body is a getter/setter.
	 *
	 * @param method
	 * 			The method to resolve.
	 * @return
	 * 			The {@link CtFieldAccess} of {@code method} if {@code method}
	 * 			is a getter or setter function.
	 */
	public static Optional<CtFieldAccess> resolveToFieldAccess(
			final CtMethod method) {
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
	 * Determines whether {@code method} is a getter or setter function. A
	 * getter or setter consists either of a single {@link CtFieldAccess}
	 * statement (see {@link #resolveToFieldAccess(CtMethod)}), or has no body
	 * (see {@link CtMethod#getBody()}), but its name starts with
	 * {@link #GETTER_SETTER_REGEX}.
	 *
	 * @param method
	 * 			The method to analyse.
	 * @return
	 * 			{@code true} if {@code method} is a getter or setter function,
	 * 			{@code false} otherwise.
	 * @throws NullPointerException
	 * 			If {@code method == null}.
	 */
	public static boolean isFieldAccess(final CtMethod method)
			throws NullPointerException {
		final String name = Validate.notNull(method).getSimpleName();
		return resolveToFieldAccess(method).isPresent() ||
				// Abstract method or interface.
				name != null && name.matches(GETTER_SETTER_REGEX);

	}

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
	public boolean isInScopeOf(final CtElement element,
			final CtElement scope) throws NullPointerException {
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
