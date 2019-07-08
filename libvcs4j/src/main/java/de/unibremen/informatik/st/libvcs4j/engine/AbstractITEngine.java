package de.unibremen.informatik.st.libvcs4j.engine;

import de.unibremen.informatik.st.libvcs4j.ITEngine;
import de.unibremen.informatik.st.libvcs4j.ItModelFactory;
import de.unibremen.informatik.st.libvcs4j.Validate;
import lombok.NonNull;

public abstract class AbstractITEngine implements ITEngine {

	private final String repository;
	private ItModelFactory modelFactory = new ItModelFactory() {};

	public AbstractITEngine(final String pRepository) {
		this.repository = Validate.notEmpty(pRepository);
	}

	@Override
	public String getRepository() {
		return repository;
	}

	@Override
	public ItModelFactory getModelFactory() {
		return modelFactory;
	}

	@Override
	public void setModelFactory(@NonNull final ItModelFactory factory) {
		modelFactory = factory;
	}
}
