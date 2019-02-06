package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.ooabusers;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmell;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmellDetector;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Thresholds;
import lombok.NonNull;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;

public class TemporaryFieldDetector extends CodeSmellDetector {

    private Map<CtExecutable, Set<CtField>> fieldAccesses = new HashMap<>();

    public TemporaryFieldDetector(@NonNull final Revision revision) {
        super(revision);
    }

    @Override
    public <T> void visitCtClass(final CtClass<T> ctClass) {
        fieldAccesses.clear();
        super.visitCtClass(ctClass);
        Map<CtField, Integer> hits = new HashMap<>();
        ctClass.getFields()
                .forEach(field -> fieldAccesses.values()
                        .stream()
                        .filter(set -> set.contains(field))
                        .forEach(set -> hits.merge(field, 1, Integer::sum)));
        hits.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == 1)
                .forEach(entry -> addCodeSmell(entry.getKey(),
                        Collections.emptyList(),
                        createSignature(entry.getKey()).orElse(null)));
    }

    @Override
    public <T> void visitCtConstructor(CtConstructor<T> c) {
        visitCtExecutable(c, c.getDeclaringType());
        super.visitCtConstructor(c);
    }

    @Override
    public <T> void visitCtMethod(CtMethod<T> m) {
        visitCtExecutable(m, m.getDeclaringType());
        super.visitCtMethod(m);
    }

    private void visitCtExecutable(final CtExecutable executable,
                                   final CtType declaringType) {
        executable.getElements(new TypeFilter<>(CtFieldAccess.class))
                .stream()
                .map(CtFieldAccess::getVariable)
                .filter(ref -> ref.getDeclaration() != null)
                .map(CtFieldReference::getDeclaration)
                .filter(field -> field.getDeclaringType() != null)
                .filter(field -> field.getDeclaringType().equals(declaringType))
                .filter(CtModifiable::isPrivate)
                .filter(field -> !field.isStatic())
                .forEach(field -> fieldAccesses.computeIfAbsent(
                        executable, __ -> new HashSet<>()).add(field));
    }

    @Override
    public CodeSmell.Definition getDefinition() {
        return new CodeSmell.Definition("Temporary Field", new Thresholds());
    }
}
