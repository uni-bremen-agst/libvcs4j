package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.ooabusers;

import de.unibremen.informatik.st.libvcs4j.spoon.Environment;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmell;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmellDetector;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Thresholds;
import lombok.NonNull;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.visitor.filter.FieldAccessFilter;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;

public class TemporaryFieldDetector extends CodeSmellDetector {

    private Map<CtExecutable, Set<CtField>> fieldAccesses = new HashMap<>();

    public TemporaryFieldDetector(@NonNull final Environment environment) {
        super(environment);
    }

    @Override
    public <T> void visitCtClass(final CtClass<T> ctClass) {
        fieldAccesses.clear();
        super.visitCtClass(ctClass);
        Map<CtField, CtExecutable> potentialTemporaryFields = new HashMap<>();
        fieldAccesses.forEach((executable, set) -> {
            final Collection<Set<CtField>> temp =
                    new ArrayList<>(fieldAccesses.values());
            temp.remove(set);
            set.forEach(field -> {
                final boolean distinct = temp.stream()
                        .noneMatch(fields -> fields.contains(field));
                if (distinct) {
                    potentialTemporaryFields.put(field, executable);
                }
            });
        });
        potentialTemporaryFields.entrySet()
                .stream()
                .filter(entry -> entry.getValue()
                        .getElements(new FieldAccessFilter(entry
                                .getKey()
                                .getReference()))
                        .get(0) instanceof CtFieldWrite)
                .forEach(entry -> addCodeSmell(entry.getKey(),
                        Collections.emptyList(),
                        createSignature(entry.getKey()).orElse(null), null));
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
