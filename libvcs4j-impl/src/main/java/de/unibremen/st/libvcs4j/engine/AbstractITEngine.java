package de.unibremen.st.libvcs4j.engine;

import de.unibremen.st.libvcs4j.ITEngine;
import org.apache.commons.lang3.Validate;

import java.util.Optional;

public abstract class AbstractITEngine implements ITEngine {

	private final String repository;
	private final String username;
	private final String password;
	private final String token;

	public AbstractITEngine(final String pRepository) {
		this.repository = Validate.notEmpty(pRepository);
		username = password = token = null;
	}

	public AbstractITEngine(
			final String pRepository,
			final String pUsername,
			final String pPassword) {
		repository = Validate.notEmpty(pRepository);
		username = Validate.notEmpty(pUsername);
		password = Validate.notEmpty(pPassword);
		token = null;
	}

	public AbstractITEngine(
			final String pRepository,
			final String pToken) {
		repository = Validate.notEmpty(pRepository);
		token = Validate.notEmpty(pToken);
		username = password = null;
	}

	@Override
	public String getRepository() {
		return repository;
	}

	@Override
	public Optional<String> getUsername() {
		return Optional.ofNullable(username);
	}

	@Override
	public Optional<String> getPassword() {
		return Optional.ofNullable(password);
	}

	@Override
	public Optional<String> getToken() {
		return Optional.ofNullable(token);
	}
}
