package de.unibremen.informatik.st.libvcs4j.gitlab;

import de.unibremen.informatik.st.libvcs4j.Issue;
import de.unibremen.informatik.st.libvcs4j.data.CommentImpl;
import de.unibremen.informatik.st.libvcs4j.data.IssueImpl;
import de.unibremen.informatik.st.libvcs4j.engine.AbstractITEngine;
import org.apache.commons.lang3.Validate;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabIssue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Optional;

public class GitlabEngine extends AbstractITEngine {

	private final String host;
	private final GitlabAPI gitlab;

	public GitlabEngine(
			final String pHost, final String pRepository, final String pToken)
			throws NullPointerException, IllegalArgumentException {
		super(pRepository);
		host = Validate.notEmpty(pHost);
		gitlab = GitlabAPI.connect(host, pToken);
	}

	public String getHost() {
		return host;
	}

	@Override
	public Optional<Issue> getIssueById(final String pId) throws IOException {
		Validate.notEmpty(pId);
		try {
			final GitlabIssue glIssue = gitlab.getIssue(
					getRepository(), Integer.parseInt(pId));
			return Optional.of(createIssue(glIssue));
		} catch (final NumberFormatException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch (final FileNotFoundException e) {
			return Optional.empty();
		}
	}

	private Issue createIssue(final GitlabIssue pGLIssue) {
		final IssueImpl issue = new IssueImpl();
		issue.setITEngine(this);
		issue.setId(String.valueOf(pGLIssue.getId()));
		String author = pGLIssue.getAuthor().getName();
		if (author == null) {
			author = pGLIssue.getAuthor().getUsername();
		}
		issue.setAuthor(author);
		issue.setTitle(pGLIssue.getTitle());
		final LocalDateTime dateTime = pGLIssue
				.getCreatedAt()
				.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
		issue.setDateTime(dateTime);

		final CommentImpl comment = new CommentImpl();
		comment.setITEngine(this);
		comment.setAuthor(author);
		comment.setDateTime(dateTime);
		comment.setMessage(pGLIssue.getDescription());
		issue.setComments(Collections.singletonList(comment));

		return issue;
	}
}
