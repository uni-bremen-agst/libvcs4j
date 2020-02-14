package de.unibremen.informatik.st.libvcs4j.spoon;

import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.RevisionRange;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import spoon.Launcher;
import spoon.reflect.CtModel;

import java.util.Optional;

import static java.lang.System.currentTimeMillis;

/**
 * Allows to build and update a Spoon {@link CtModel}. The resultant model is
 * managed by an instance of {@link Environment} (see {@link #getEnvironment()}
 * and {@link #update(RevisionRange)}).
 */
@Slf4j
public class EnvironmentBuilder {

	/**
	 * Enables or disables auto imports (see
	 * {@link spoon.compiler.Environment#setAutoImports(boolean)}). The default
	 * value is {@code true}.
	 */
	@Getter
	@Setter
	private boolean autoImports = true;

	/**
	 * The environment of the last call of {@link #update(RevisionRange)}.
	 */
	private Environment environment = null;

	/**
	 * Returns the {@link Environment} of the last call of
	 * {@link #update(RevisionRange)}.
	 *
	 * @return
	 * 		The {@link Environment} of the last call of
	 * 		{@link #update(RevisionRange)}.
	 */
	public Optional<Environment> getEnvironment() {
		return Optional.ofNullable(environment);
	}

	/**
	 * Builds (or incrementally updates) the {@link CtModel} of
	 * {@link #environment}.
	 *
	 * @param range
	 * 		The currently checked out range.
	 * @return
	 * 		The resulting {@link Environment}.
	 * @throws NullPointerException
	 * 		If {@code range} is {@code null}.
	 * @throws BuildException
	 * 		If an error occurred while building the model.
	 */
	public Environment update(@NonNull final RevisionRange range)
			throws BuildException {
		final Revision revision = range.getRevision();
		log.info("Building Spoon model for revision {}", revision.getId());
		final long current = currentTimeMillis();
		final Launcher launcher = new Launcher();
		launcher.addInputResource(revision.getOutput().toString());
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setAutoImports(autoImports);
		try {
			environment = new Environment(launcher.buildModel(), range);
			log.info("Model built in {} milliseconds",
					currentTimeMillis() - current);
			return environment;
		} catch (final Exception e) {
			environment = null;
			log.info("Unable to build model", e);
			throw new BuildException(e);
		}
	}
}
