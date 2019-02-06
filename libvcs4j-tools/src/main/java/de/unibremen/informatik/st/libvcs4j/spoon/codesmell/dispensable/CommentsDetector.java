package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.dispensable;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Metric;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmellDetector;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmell;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Threshold;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Thresholds;
import lombok.NonNull;
import spoon.reflect.code.CtComment;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.TypeFilter;

import java.math.BigDecimal;
import java.util.Collections;

public class CommentsDetector extends CodeSmellDetector {

    private static final BigDecimal DEFAULT_THRESHOLD = BigDecimal.valueOf(0.5);

    private BigDecimal threshold;

    public CommentsDetector(@NonNull final Revision revision,
                            final BigDecimal threshold) {
        super(revision);
        this.threshold = threshold;
    }

    public CommentsDetector(@NonNull final Revision revision) {
        this(revision, DEFAULT_THRESHOLD);
    }

    @Override
    public <T> void visitCtClass(CtClass<T> ctClass) {
        super.visitCtClass(ctClass);
    }

    @Override
    public <T> void visitCtConstructor(CtConstructor<T> c) {
        visitCtExecutable(c);
        super.visitCtConstructor(c);
    }

    @Override
    public <T> void visitCtMethod(CtMethod<T> m) {
        visitCtExecutable(m);
        super.visitCtMethod(m);
    }

    private void visitCtExecutable(final CtExecutable executable) {
        if (executable.getBody() == null) {
            return;
        }

        final int numberOfStatements = executable.getBody()
                .getStatements()
                .stream()
                .filter(ctStatement -> !ctStatement.isImplicit())
                .map(CtElement::getPosition)
                .filter(p -> !p.equals(SourcePosition.NOPOSITION))
                .map(p -> p.getEndLine() - p.getLine() + 1)
                .reduce(0, Integer::sum);

        if (numberOfStatements == 0) {
            return;
        }

        final int numberOfComments = executable.getBody()
                .getElements(new TypeFilter<>(CtComment.class))
                .stream()
                .filter(ctStatement -> !ctStatement.isImplicit())
                .map(CtElement::getPosition)
                .filter(p -> !p.equals(SourcePosition.NOPOSITION))
                .map(p -> p.getEndLine() - p.getLine() + 1)
                .reduce(0, Integer::sum);

        final BigDecimal ratio = BigDecimal.valueOf(
                (double) numberOfComments / numberOfStatements);

        if (ratio.compareTo(threshold) >= 0) {
            addCodeSmell(executable,
                    Collections.singletonList(createMetric(ratio)),
                    createSignature(executable).orElse(null));

        }
    }

    @Override
    public CodeSmell.Definition getDefinition() {
        final Threshold threshold = new Threshold(
                createMetric(this.threshold),
                Threshold.Relation.GREATER_EQUALS);
        final Thresholds thresholds = new Thresholds(threshold);
        return new CodeSmell.Definition("Comments", thresholds);
    }

    private Metric createMetric(final BigDecimal val) {
        return new Metric("Ratio of CLOC to SLOC", val.doubleValue());
    }
}
