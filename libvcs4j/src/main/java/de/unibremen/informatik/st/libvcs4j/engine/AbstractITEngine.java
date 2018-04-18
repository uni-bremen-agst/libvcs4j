package de.unibremen.informatik.st.libvcs4j.engine;

import de.unibremen.informatik.st.libvcs4j.ITEngine;
import org.apache.commons.lang3.Validate;

public abstract class AbstractITEngine implements ITEngine {

	private final String repository;

	public AbstractITEngine(final String pRepository) {
		this.repository = Validate.notEmpty(pRepository);
	}

	@Override
	public String getRepository() {
		return repository;
	}
}
