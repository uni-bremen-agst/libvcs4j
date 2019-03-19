package de.unibremen.informatik.st.libvcs4j.spoon.codesmell.ooabusers;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.Validate;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Metric;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmellDetector;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.CodeSmell;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Threshold;
import de.unibremen.informatik.st.libvcs4j.spoon.codesmell.Thresholds;
import de.unibremen.informatik.st.libvcs4j.spoon.metric.MCC;
import lombok.NonNull;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.declaration.CtElement;

import java.util.List;
import java.util.stream.Collectors;


public class SwitchStatementDetector extends CodeSmellDetector {

    private static final int DEFAULT_MCC_THRESHOLD = 3;

    private final int mccThreshold;

    private final MCC mcc = new MCC();

    public SwitchStatementDetector(@NonNull final Revision revision,
                                   final int mccThreshold)
            throws NullPointerException, IllegalArgumentException {
        super(revision);
        this.mccThreshold = Validate.notNegative(mccThreshold);
    }

    public SwitchStatementDetector(@NonNull final Revision revision)
            throws NullPointerException, IllegalArgumentException{
        this(revision, DEFAULT_MCC_THRESHOLD);
    }

    @Override
    public void visitRoot(final CtElement element) {
        mcc.scan(element);
        super.visitRoot(element);
    }

    @Override
    public <S> void visitCtSwitch(CtSwitch<S> switchStatement) {
        super.visitCtSwitch(switchStatement);
        final List<Integer> mccValues = switchStatement.getCases()
                .stream()
                .map(mcc::MCCOf)
                .map(o -> o.orElseThrow(IllegalStateException::new))
                .collect(Collectors.toList());

        if (mccValues.stream().allMatch(mccValue -> mccValue >= mccThreshold)) {
            addCodeSmell(switchStatement,
                    mccValues.stream()
                            .map(this::createMccMetric)
                            .collect(Collectors.toList()),
                    createSignature(switchStatement).orElse(null));
        }

    }

    @Override
    public CodeSmell.Definition getDefinition() {
        final Threshold threshold = new Threshold(
                createMccMetric(this.mccThreshold),
                Threshold.Relation.GREATER_EQUALS);
        return new CodeSmell.Definition("Switch Statements",
                new Thresholds(threshold));
    }

    private Metric createMccMetric(final int val) {
        return new Metric(mcc.name() ,val);
    }
}
