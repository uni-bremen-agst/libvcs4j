package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.dispensable;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmell;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmellDetector;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Thresholds;
import lombok.NonNull;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;

public class DataClassDetector extends CodeSmellDetector {

	public DataClassDetector(@NonNull final Revision revision) {
		super(revision);
	}

	private Deque<Boolean> isDataClass = new ArrayDeque<>();

	@Override
	public void visitRoot(final CtElement element) {
		isDataClass = new ArrayDeque<>();
		super.visitRoot(element);
	}

	@Override
	public <T> void visitCtClass(final CtClass<T> ctClass) {
		isDataClass.push(true);
		super.visitCtClass(ctClass);
		visitType(ctClass);
	}

	@Override
	public <T> void visitCtInterface(final CtInterface<T> ctInterface) {
		isDataClass.push(true);
		super.visitCtInterface(ctInterface);
		visitType(ctInterface);
	}

	private void visitType(final CtType type) {
		if (isDataClass.pop()
				&& !type.getFields().isEmpty()
				&& !derivesFromThrowable(type)) {
			addCodeSmell(type, Collections.emptyList(),
					createSignature(type).orElse(null));
		}
	}

	private boolean derivesFromThrowable(final CtType type) {
		CtTypeReference<?> parent = type.getSuperclass();
		while (parent != null) {
			if (parent.getSimpleName().equals("Throwable")) {
				return true;
			}
			parent = parent.getSuperclass();
		}
		return false;
	}

	@Override
	public <T> void visitCtMethod(final CtMethod<T> method) {
		if (!isDataClass.isEmpty()) {
			resolveToFieldAccess(method)
					.map(fa -> fa.hasParent(method.getParent(CtType.class)))
					.ifPresent(__ -> {
						isDataClass.pop();
						isDataClass.push(false);
					});
		}
		super.visitCtMethod(method);
	}

	@Override
	public CodeSmell.Definition getDefinition() {
		return new CodeSmell.Definition("Data Class", new Thresholds());
	}
}
