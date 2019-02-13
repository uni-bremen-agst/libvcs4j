package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.dispensable;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.Validate;
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
import java.util.Arrays;
import java.util.Collections;

public class CommentsDetector extends CodeSmellDetector {

    private static final BigDecimal DEFAULT_RATIO_THRESHOLD =
            BigDecimal.valueOf(0.5);
    private static final int DEFAULT_LOC_THRESHOLD = 10;

    private BigDecimal ratioThreshold;
    private int locThreshold;

    public CommentsDetector(@NonNull final Revision revision,
                            final int locThreshold,
                            final BigDecimal ratioThreshold)
            throws NullPointerException, IllegalArgumentException {
        super(revision);
        this.locThreshold = Validate.notNegative(locThreshold);
        Validate.isTrue(ratioThreshold.compareTo(BigDecimal.ZERO) >= 0);
        this.ratioThreshold = ratioThreshold;
    }

    public CommentsDetector(@NonNull final Revision revision)
            throws NullPointerException, IllegalArgumentException{
        this(revision, DEFAULT_LOC_THRESHOLD, DEFAULT_RATIO_THRESHOLD);
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

        if (numberOfStatements < locThreshold) {
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

        final BigDecimal ratio = BigDecimal.valueOf(numberOfComments)
                .divide(BigDecimal.valueOf(numberOfStatements),
                        8,
                        BigDecimal.ROUND_UP);

        if (ratio.compareTo(ratioThreshold) >= 0) {
            addCodeSmell(executable,
                    Arrays.asList(createRatioMetric(ratio),
                            createLocMetric(numberOfStatements)),
                    createSignature(executable).orElse(null));

        }
    }

    @Override
    public CodeSmell.Definition getDefinition() {
        final Threshold ratioThreshold = new Threshold(
                createRatioMetric(this.ratioThreshold),
                Threshold.Relation.GREATER_EQUALS);
        final Threshold locThreshold = new Threshold(
                createLocMetric(this.locThreshold),
                Threshold.Relation.GREATER_EQUALS);
        final Thresholds thresholds = new Thresholds(
                Arrays.asList(ratioThreshold, locThreshold),
                Thresholds.Connective.AND);
        return new CodeSmell.Definition("Comments", thresholds);
    }

    private Metric createLocMetric(final int val) {
        return new Metric("Lines of Code", val);
    }

    private Metric createRatioMetric(final BigDecimal val) {
        return new Metric("Comment Ratio", val.doubleValue());
    }
}
