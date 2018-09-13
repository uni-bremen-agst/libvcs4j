package de.unibremen.informatik.st.libvcs4j;

import de.unibremen.informatik.st.libvcs4j.github.GithubEngine;
import de.unibremen.informatik.st.libvcs4j.gitlab.GitlabEngine;

import java.io.IOException;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ITEngineBuilder {

	private enum Engine {
		GITLAB, GITHUB
	}

	////////////////////////////// Configuration //////////////////////////////

	private String host;

	private String repository;

	private Engine engine = Engine.GITHUB;

	private String username;

	private String password;

	private String token;

	////////////////////////////// Constructors ///////////////////////////////

	public ITEngineBuilder(final String pRepository) {
		repository = Validate.notEmpty(pRepository);
	}

	public static ITEngineBuilder of(final String pRepository) {
		return new ITEngineBuilder(pRepository);
	}

	public static ITEngineBuilder ofGitlab(
			final String pHost, final String pRepository) {
		return of(pRepository).withHost(pHost).withGitlab();
	}

	public static ITEngineBuilder ofGithub(final String pRepository) {
		return of(pRepository).withGithub();
	}

	/////////////////////////////// Fluent API ////////////////////////////////

	public ITEngineBuilder withHost(final String pHost) {
		host = Validate.notEmpty(pHost);
		return this;
	}

	public ITEngineBuilder withRepository(final String pRepository) {
		repository = Validate.notEmpty(pRepository);
		return this;
	}

	public ITEngineBuilder withGithub() {
		engine = Engine.GITHUB;
		return this;
	}

	public ITEngineBuilder withGitlab() {
		engine = Engine.GITLAB;
		return this;
	}

	public ITEngineBuilder withUsername(final String pUsername) {
		username = pUsername;
		token = null;
		return this;
	}

	public ITEngineBuilder withPassword(final String pPassword) {
		password = pPassword;
		token = null;
		return this;
	}

	public ITEngineBuilder withToken(final String pToken) {
		token = pToken;
		username = password = null;
		return this;
	}

	public ITEngineBuilder withNoAuthentication() {
		username = password = token = null;
		return this;
	}

	public ITEngine build() throws IOException {
		if (engine == Engine.GITLAB) {
			if (token != null) {
				return new GitlabEngine(host, repository, token);
			} else {
				throw new IllegalArgumentException(
						"Gitlab engine requires token");
			}
		} else if (engine == Engine.GITHUB) {
			if (token != null) {
				return new GithubEngine(repository, token);
			} else if (username != null || password != null) {
				return new GithubEngine(repository, username, password);
			} else {
				return new GithubEngine(repository);
			}
		} else {
			throw new IllegalStateException(String.format(
					"Unknown IT engine '%s'", engine));
		}
	}
}
