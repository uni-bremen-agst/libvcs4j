package de.unibremen.informatik.st.libvcs4j.mapping;

import de.unibremen.informatik.st.libvcs4j.*;
import de.unibremen.informatik.st.libvcs4j.pmd.PMDDetectionResult;
import de.unibremen.informatik.st.libvcs4j.pmd.PMDRunner;
import de.unibremen.informatik.st.libvcs4j.pmd.PMDViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class Main {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(Main.class);

    private static Collection<Mappable<String>> from;

    private static Collection<Mappable<String>> to;

    public static void main(String[] args) {
        Tracker<String> tracker = new Tracker<>();
        Mapping<String> mapping = new Mapping<>();
        VCSEngine engine = VCSEngineBuilder.ofGit("/home/dominique/git/gson").withRoot("gson").build();
        for (RevisionRange range : engine) {
            PMDRunner runner = new PMDRunner("java-basic");
            PMDDetectionResult result = null;
            try {
                result = runner.run(range.getRevision());
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }

            Collection<Mappable<String>> mappables = new ArrayList<>();
            result.getViolations().values().forEach(list -> list.forEach(violation -> mappables.add(new MappableWrapper(violation))));
            Mapping.Result<String> maStringResult;
            if (to == null) {
                to = mappables;
                try {
                    maStringResult = mapping.map(new ArrayList<>(), to, range);
                } catch (IOException e) {
                    LOGGER.info("generating mapping failed");
                    LOGGER.error(e.getMessage());
                    return;
                }
            } else {
                from = to;
                to = mappables;
                try {
                    maStringResult = mapping.map(from, to, range);
                } catch (IOException e) {
                    LOGGER.info("generating mapping failed");
                    e.printStackTrace();
                    return;
                }
            }
            tracker.add(maStringResult);
            LOGGER.info("Lifespans managed: " + tracker.getLifespans().size());
        }
    }

    public static class MappableWrapper implements Mappable<String> {

        private List<VCSFile.Range> ranges;

        private String metadata;

        public MappableWrapper(PMDViolation violation) {
            Validate.notNull(violation);
            ranges = Collections.singletonList(violation.getRange());
            metadata = violation.getRule();
        }

        @Override
        public List<VCSFile.Range> getRanges() {
            return ranges;
        }

        @Override
        public Optional<String> getMetadata() {
            return Optional.of(metadata);
        }
    }
}
