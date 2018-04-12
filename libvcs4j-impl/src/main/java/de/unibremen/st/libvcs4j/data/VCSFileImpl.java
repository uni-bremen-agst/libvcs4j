package de.unibremen.st.libvcs4j.data;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import de.unibremen.st.libvcs4j.Complexity;
import de.unibremen.st.libvcs4j.Revision;
import de.unibremen.st.libvcs4j.Size;
import de.unibremen.st.libvcs4j.VCSEngine;
import de.unibremen.st.libvcs4j.VCSFile;
import org.apache.commons.lang3.Validate;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IScanner;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.ScannerException;
import org.conqat.lib.scanner.ScannerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation for {@link VCSFile}.
 */
public class VCSFileImpl implements VCSFile {

	private final VCSEngine engine;
	private String path;
	private String relativePath;
	private Revision revision;

	public VCSFileImpl(final VCSEngine pEngine) {
		engine = Validate.notNull(pEngine);
	}

	@Override
	public String getPath() {
		return path;
	}

	public void setPath(final String pPath) {
		path = Validate.notNull(pPath);
	}

	@Override
	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(final String pRelativePath) {
		relativePath = Validate.notNull(pRelativePath);
	}

	@Override
	public Revision getRevision() {
		return revision;
	}

	public void setRevision(final Revision pRevision) {
		revision = Validate.notNull(pRevision);
	}

	@Override
	public Optional<Charset> guessCharset() throws IOException {
		final CharsetDetector detector = new CharsetDetector();
		detector.setText(readAllBytes());
		final CharsetMatch match = detector.detect();
		try {
			return Optional.ofNullable(match)
					.map(m -> Charset.forName(m.getName()));
		} catch (final Exception e) {
			return Optional.empty();
		}
	}

	@Override
	public Optional<Size> computeSize() throws IOException {
		final Optional<ELanguage> maybeLang = getLanguage();
		if (!maybeLang.isPresent()) {
			return Optional.empty();
		}

		final ELanguage lang = maybeLang.get();
		final String content = readeContent();
		final IScanner scanner = ScannerFactory
				.newLenientScanner(lang, content, "");

		final String LINE_SEP = "\\r?\\n";
		int loc = 0, sloc = 0, cloc = 0, not = 0, snot = 0, cnot = 0;
		IToken lastToken = null;
		try {
			for (IToken token = scanner.getNextToken();
				 token.getType() != ETokenType.EOF;
				 lastToken = token, token = scanner.getNextToken()) {
				not++;
				final boolean comment = isCommentType(token);
				if (comment) {
					cnot++;
				} else {
					snot++;
				}
				if (lastToken == null) {
					if (comment) {
						cloc = 1;
					} else {
						sloc = 1;
					}
				} else {
					final String ltText = content.substring(
							lastToken.getOffset(),
							lastToken.getEndOffset() + 1);
					final int ltEndLine = lastToken.getLineNumber() +
							ltText.split(LINE_SEP).length - 1;
					final String text = content.substring(
							token.getOffset(),
							token.getEndOffset() + 1);
					int linesToAdd = text.split(LINE_SEP).length;
					if (ltEndLine == token.getLineNumber()) {
						linesToAdd--;
					}
					if (comment) {
						cloc += linesToAdd;
					} else {
						sloc += linesToAdd;
					}
				}
			}
		} catch (final ScannerException e) {
			throw new IOException(e);
		}
		if (lastToken != null) {
			final String text = content.substring(
					lastToken.getOffset(),
					lastToken.getEndOffset() + 1);
			final int endLine = lastToken.getLineNumber()
					+ text.split(LINE_SEP).length - 1;
			loc = endLine + 1;
		}

		final SizeImpl size = new SizeImpl();
		size.setLOC(loc);
		size.setSLOC(sloc);
		size.setCLOC(cloc);
		size.setNOT(not);
		size.setSNOT(snot);
		size.setCNOT(cnot);
		return Optional.of(size);
	}

	@Override
	public Optional<Complexity> computeComplexity() throws IOException {
		final Optional<ELanguage> maybeLang = getLanguage();
		if (!maybeLang.isPresent()) {
			return Optional.empty();
		}

		final ELanguage lang = maybeLang.get();
		final String content = readeContent();
		final IScanner scanner = ScannerFactory
				.newLenientScanner(lang, content, "");

		final List<IToken> operators = new ArrayList<>();
		final List<IToken> operands = new ArrayList<>();
		int mccabe = 1;
		try {
			for (IToken token = scanner.getNextToken();
				 token.getType() != ETokenType.EOF;
				 token = scanner.getNextToken()) {
				if (isCommentType(token)) {
					continue;
				} else if (token.getType().isError()) {
					continue;
				} else if (token.getType().isOperator()) {
					operators.add(token);
				} else {
					operands.add(token);
				}

				if (isControlType(token)) {
					mccabe++;
				}
			}
		} catch (final ScannerException e) {
			throw new IOException(e);
		}
		final List<String> distinctOperators =
				operators.stream()
				.map(IToken::getText)
				.distinct()
				.collect(Collectors.toList());
		final List<String> distinctOperands =
				operands.stream()
				.map(IToken::getText)
				.distinct()
				.collect(Collectors.toList());

		final HalsteadImpl halstead = new HalsteadImpl();
		halstead.setn1(distinctOperators.size());
		halstead.setn2(distinctOperands.size());
		halstead.setN1(operators.size());
		halstead.setN2(operands.size());

		final ComplexityImpl complexity = new ComplexityImpl();
		complexity.setHalstead(halstead);
		complexity.setMccabe(mccabe);
		return Optional.of(complexity);
	}

	@Override
	public byte[] readAllBytes() throws IOException {
		return engine.readAllBytes(this);
	}

	private boolean isCommentType(final IToken pToken) {
		final ETokenType type = pToken.getType();
		return type == ETokenType.COMMENT ||
				type == ETokenType.COMMENT_KEYWORD ||
				type == ETokenType.DOCUMENTATION_COMMENT ||
				type == ETokenType.TRADITIONAL_COMMENT ||
				type == ETokenType.END_OF_LINE_COMMENT ||
				type == ETokenType.HASH_COMMENT;
	}

	private boolean isControlType(final IToken pToken) {
		final ETokenType type = pToken.getType();
		return
				// if condition
				type == ETokenType.IF ||
				type == ETokenType.IFN ||
				type == ETokenType.ANDIF ||
				type == ETokenType.ORIF ||
				type == ETokenType.ELIF ||
				type == ETokenType.ELSIF ||
				type == ETokenType.ELSEIF ||
				type == ETokenType.MODIF ||
				type == ETokenType.NULLIF ||
				// is condition
				type == ETokenType.IS_DATE ||
				type == ETokenType.IS_EMPTY ||
				type == ETokenType.IS_FLOAT ||
				type == ETokenType.ISOLATION ||
				type == ETokenType.IS_NOT ||
				type == ETokenType.IS_NUMBER ||
				type == ETokenType.IS_TIME ||
				// for loop
				type == ETokenType.FOR ||
				type == ETokenType.FORALL ||
				type == ETokenType.FOREACH ||
				// while loop
				type == ETokenType.WHILE ||
				// try block
				type == ETokenType.TRY;
	}

	private Optional<ELanguage> getLanguage() {
		final ELanguage l = ELanguage.fromFile(toFile());
		return l == null || l == ELanguage.TEXT || l == ELanguage.LINE
				? Optional.empty()
				: Optional.of(l);
	}
}
