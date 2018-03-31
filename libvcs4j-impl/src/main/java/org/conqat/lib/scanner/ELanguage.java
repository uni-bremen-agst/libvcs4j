/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 The ConQAT Project                                   |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+-------------------------------------------------------------------------*/
package org.conqat.lib.scanner;

import static org.conqat.lib.commons.collections.CollectionUtils.asHashSet;
import static org.conqat.lib.scanner.ELanguageConstants.CLIKE_COMMENT_REGEX;
import static org.conqat.lib.scanner.ETokenType.COLON;
import static org.conqat.lib.scanner.ETokenType.DOT;
import static org.conqat.lib.scanner.ETokenType.EOF;
import static org.conqat.lib.scanner.ETokenType.EOL;
import static org.conqat.lib.scanner.ETokenType.EXCLAMATION;
import static org.conqat.lib.scanner.ETokenType.LBRACE;
import static org.conqat.lib.scanner.ETokenType.LEFT_ANGLE_BRACKET;
import static org.conqat.lib.scanner.ETokenType.MULTIPLE_EOL;
import static org.conqat.lib.scanner.ETokenType.QUESTION;
import static org.conqat.lib.scanner.ETokenType.RBRACE;
import static org.conqat.lib.scanner.ETokenType.RIGHT_ANGLE_BRACKET;
import static org.conqat.lib.scanner.ETokenType.SEMICOLON;
import static org.conqat.lib.scanner.ETokenType.SLASH;
import static org.conqat.lib.scanner.ETokenType.THEN;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Enumeration class for the languages support by the scanner framework.
 *
 * @author $Author: hummelb $
 * @version $Revision: 51687 $
 * @ConQAT.Rating GREEN Hash: 8A9953BD58BC2B868B6A9422AD6B970F
 */
public enum ELanguage {

	// we need to use the ugly workaround with asHashSet here as we cannot use
	// varargs twice in the constructor

	/** Java */
	JAVA("Java", asHashSet(SEMICOLON, RBRACE, LBRACE), CLIKE_COMMENT_REGEX,
			true, "java"),

	/** C/C++ */
	CPP("C/C++", asHashSet(SEMICOLON, RBRACE, LBRACE), CLIKE_COMMENT_REGEX,
			true, "cpp", "cc", "c", "h", "hh", "hpp", "cxx", "hxx", "inl",
			"inc", "pc"),

	/** Visual Basic */
	VB("Visual Basic", asHashSet(COLON, EOL), "^ *'", false, "vb", "frm",
			"cls", "bas"),

	/** PL/I */
	PL1("PL/I", asHashSet(SEMICOLON, RBRACE, LBRACE), CLIKE_COMMENT_REGEX,
			false, "pl1", "pli"),

	/** COBOL */
	COBOL("COBOL", new CobolStatementOracle(), "^ *[*/]+", false, "cbl", "cob",
			"cobol", "cpy"),

	/** C# */
	CS("C#", asHashSet(SEMICOLON, RBRACE, LBRACE), CLIKE_COMMENT_REGEX, true,
			"cs"),

	/** ABAP */
	ABAP("ABAP", asHashSet(DOT), "^ *[*\"]+", false, "abap"),

	/** Ada */
	ADA("Ada", asHashSet(SEMICOLON, THEN), "^ *--+", false, "ada", "ads", "adb"),

	/** Natural language text */
	TEXT("Plain Text", asHashSet(DOT, QUESTION, EXCLAMATION, COLON,
			MULTIPLE_EOL), "", false, "txt"),

	/** XML */
	XML("XML", asHashSet(LEFT_ANGLE_BRACKET, SLASH, RIGHT_ANGLE_BRACKET),
			"(^ *<!--+)|(--+>$)", true, "xml", "xsl", "xslt", "architecture",
			"cqb"),

	/** PL/SQL */
	PLSQL("PL/SQL", asHashSet(SEMICOLON), "(^ *(--+|/[*]+))|([*]+/ *$)", false,
			"sql", "pks", "pkb", "trg", "fnc", "typ", "tyb", "prc"),

	/** Python */
	PYTHON("Python", asHashSet(EOL, EOF), "^ *#+", true, "py"),

	/** T-SQL aka Transact SQL. */
	TSQL("Transact-SQL", asHashSet(EOL), "(^ *(--+|/[*]+))|([*]+/ *$)", false,
			"tsql"),

	/** Matlab */
	MATLAB("Matlab", asHashSet(EOL, SEMICOLON), "^ *%+", true, "m"),

	/** PHP */
	PHP("PHP", asHashSet(SEMICOLON, RBRACE, LBRACE), CLIKE_COMMENT_REGEX, true,
			"php", "php3", "php4", "php5"),

	/** Ruby */
	RUBY("Ruby", asHashSet(EOL), "^ *#+", true, "rb"),

	/**
	 * JavaScript.
	 * <p>
	 * Note that the statement oracle only works if semicolons are used
	 * consistently. However, semicolons are optional in JavaScript (rules
	 * described here: http://bclary.com/2004/11/07/#a-7.9), but to determine
	 * end of statement in this case requires a full blown parser (hard to
	 * decide locally in some cases). As most coding guidelines recommend using
	 * semicolons anyway, we stick with this solution.
	 */
	JAVASCRIPT("JavaScript", asHashSet(SEMICOLON, RBRACE, LBRACE),
			CLIKE_COMMENT_REGEX, true, "js", "sj"),

	/** The language used within the M/Text printing system. */
	MTEXT("M/TEXT", asHashSet(EOL), "(?i)^ *[.]DSC", true, "mtx"),

	/**
	 * The "Just Your Average Computer Company Procedural Language". A C-like
	 * language being part of the Panther framework developed by the company
	 * Prolifics. The language is used in the archive system d.3 developed by
	 * the company "d.velop".
	 */
	JPL("JPL", asHashSet(EOL), CLIKE_COMMENT_REGEX, true, "jpl"),

	/**
	 * Use this for languages for which no dedicated scanner is available.
	 * Creates a token per line (and creates EOL tokens).
	 */
	LINE("Line-based Text", asHashSet(EOL), "", false),

	/**
	 * The <a
	 * href="http://en.wikipedia.org/wiki/Magik_%28programming_language%29"
	 * >Magik</a> language, which is a part of GE's Smallworld GIS.
	 */
	MAGIK("Magik", asHashSet(EOL, EOF), "^ *#+", true, "magik"),

	/** Delphi */
	DELPHI("Delphi", asHashSet(SEMICOLON),
			"(^ *([(][*]+|[{]|//+)|([*]+[)]|[}])$)", false, "pas", "dpr");

	/** This maps from extensions to languages. */
	private static ListMap<String, ELanguage> extension2LanguageMap = new ListMap<String, ELanguage>();

	/** The statement oracle for this language. */
	private final IStatementOracle statementOracle;

	/** Initialize {@link #extension2LanguageMap}. */
	static {
		for (ELanguage language : values()) {
			for (String extension : language.extensions) {
				extension2LanguageMap.add(extension.toLowerCase(), language);
			}
		}
	}

	/** File extensions commonly used for this language. */
	private final String[] extensions;

	/**
	 * Pattern describing the parts of a comment line that should be trimmed to
	 * reveal the text.
	 */
	private final Pattern commentLineTrimPattern;

	/** Whether the language is case sensitive. */
	private final boolean caseSensitive;

	/** The readable name of the language, to be used, e.g., in a UI. */
	private final String readableName;

	/** Create language. */
	private ELanguage(String readableName, Set<ETokenType> statementDelimiters,
			String commentLineTrimRegex, boolean caseSensitive,
			String... extensions) {
		this(readableName, new StatementOracle(statementDelimiters),
				commentLineTrimRegex, caseSensitive, extensions);
	}

	/** Create language. */
	private ELanguage(String readableName, IStatementOracle oracle,
			String commentLineTrimRegex, boolean caseSensitive,
			String... extensions) {
		statementOracle = oracle;
		this.commentLineTrimPattern = Pattern.compile(commentLineTrimRegex);
		this.caseSensitive = caseSensitive;
		this.extensions = extensions;
		this.readableName = readableName;
	}

	/** Get the file extensions commonly used for this language. */
	public String[] getFileExtensions() {
		return CollectionUtils.copyArray(extensions);
	}

	/** Get statement oracle for this language. */
	public IStatementOracle getStatementOracle() {
		return statementOracle;
	}

	/**
	 * Gets the {@link ELanguage} value corresponding to the file extension of
	 * the path. Returns null if no extension was found. If there are multiple
	 * possible languages, the first one is returned.
	 */
	public static ELanguage fromPath(String path) {
		return fromFile(new File(path));
	}

	/**
	 * Gets the {@link ELanguage} value corresponding to the file extension of
	 * the file. Returns null if no extension was found. If there are multiple
	 * possible languages, the first one is returned.
	 */
	public static ELanguage fromFile(File file) {
		return fromFileExtension(FileSystemUtils.getFileExtension(file));
	}

	/**
	 * Gets the {@link ELanguage} value corresponding to the given file
	 * extension (without a dot). Returns null if no extension was found. If
	 * there are multiple possible languages, the first one is returned.
	 */
	public static ELanguage fromFileExtension(String extension) {
		if (extension == null) {
			return null;
		}

		List<ELanguage> result = extension2LanguageMap.getCollection(extension
				.toLowerCase());

		if (result == null || result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}

	/**
	 * Get the content of a comment, i.e. with the comment delimiters removed.
	 */
	public String getCommentContent(String commentText) {
		StringBuffer content = new StringBuffer();
		for (String line : StringUtils.splitLinesAsList(commentText)) {
			if (content.length() > 0) {
				content.append(StringUtils.CR);
			}
			content.append(commentLineTrimPattern.matcher(line)
					.replaceAll(StringUtils.EMPTY_STRING).trim());
		}
		return content.toString();
	}

	/** Return whether the language is case sensitive. */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/** Returns {@link #readableName}. */
	public String getReadableName() {
		return readableName;
	}
}