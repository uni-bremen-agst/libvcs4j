package de.unibremen.informatik.st.libvcs4j.github;

import de.unibremen.informatik.st.libvcs4j.Issue;
import de.unibremen.informatik.st.libvcs4j.Issue.Comment;
import de.unibremen.informatik.st.libvcs4j.Validate;
import de.unibremen.informatik.st.libvcs4j.engine.AbstractITEngine;
import org.kohsuke.github.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GithubEngine extends AbstractITEngine {

	private final GHRepository github;

	/**
	 * Creates a new engine without username and password.
	 *
	 * @param pRepository
	 *      The repository to track.
	 * @throws IOException
	 *      If an error occurred while connecting to the given repository.
	 */
	public GithubEngine(final String pRepository) throws IOException {
		super(pRepository);
		github = GitHub
				.connectAnonymously()
				.getRepository(pRepository);
	}

	/**
	 * Creates a new engine with given username and password.
	 *
	 * @param pRepository
	 *      The repository to track.
	 * @param pUsername
	 *      The username used for authentication.
	 * @param pPassword
	 *      The password used for authentication.
	 * @throws IOException
	 *      If an error occurred while connecting to the given repository.
	 */
	public GithubEngine(
			final String pRepository, final String pUsername,
			final String pPassword) throws IOException {
		super(pRepository);
		github = GitHub
				.connectUsingPassword(pUsername, pPassword)
				.getRepository(pRepository);
	}

	public GithubEngine(final String pRepository, final String pToken)
			throws IOException {
		super(pRepository);
		github = GitHub
				.connectUsingOAuth(pToken)
				.getRepository(pRepository);
	}

	@Override
	public Optional<Issue> getIssueById(final String pId)
			throws NullPointerException, IllegalArgumentException,
			IOException {
		Validate.notEmpty(pId);
		try {
			final int id = Integer.parseInt(pId);
			GHIssue ghIssue = github.getIssue(id);
			return Optional.of(createIssue(ghIssue));
		} catch (final NumberFormatException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch (final GHFileNotFoundException e) {
			return Optional.empty();
		}
	}

	private Issue createIssue(final GHIssue pGHIssue) throws IOException {
		String author = pGHIssue.getUser().getName();
		if (author == null) {
			author = pGHIssue.getUser().getLogin();
		}
		final LocalDateTime dateTime = pGHIssue
				.getCreatedAt()
				.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
		final List<Comment> comments = new ArrayList<>();
		for (final GHIssueComment c : pGHIssue.getComments()) {
			comments.add(createComment(c));
		}
		return getModelFactory().createIssue(
				String.valueOf(pGHIssue.getNumber()), author,
				pGHIssue.getTitle(), dateTime, comments, this);
	}

	private Comment createComment(final GHIssueComment pComment)
			throws IOException {
		String author = pComment.getUser().getName();
		if (author == null) {
			author = pComment.getUser().getLogin();
		}
		final String message = pComment.getBody();
		final LocalDateTime dateTime = pComment
				.getCreatedAt()
				.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
		return getModelFactory().createComment(
				author, message, dateTime, this);
	}
}
