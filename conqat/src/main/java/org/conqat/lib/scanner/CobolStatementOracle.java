package org.conqat.lib.scanner;

import java.util.EnumSet;

import org.conqat.lib.commons.collections.ILookahead;

/**
 * {@link IStatementOracle} for Cobol.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: AB30EDC7C20011CE9C9D18934A759549
 */
public class CobolStatementOracle implements IStatementOracle {

	/** Those token types mark the beginning of a new statement */
	private final static EnumSet<ETokenType> BOS = EnumSet.of(
			// real verbs:
			ETokenType.ACCEPT, ETokenType.ADD, ETokenType.ALLOCATE,
			ETokenType.ALTER, ETokenType.CALL, ETokenType.CANCEL,
			ETokenType.CLOSE, ETokenType.COMPUTE, ETokenType.CONTINUE,
			ETokenType.DELETE, ETokenType.DISPLAY, ETokenType.DIVIDE,
			ETokenType.EVALUATE, ETokenType.EXIT, ETokenType.GO,
			ETokenType.GOBACK, ETokenType.GOTO, ETokenType.IF,
			ETokenType.INITIALIZE, ETokenType.INSPECT, ETokenType.INVOKE,
			ETokenType.MERGE, ETokenType.MOVE, ETokenType.MULTIPLY,
			ETokenType.OPEN, ETokenType.PERFORM, ETokenType.RELEASE,
			ETokenType.RETURN, ETokenType.REWRITE, ETokenType.SEARCH,
			ETokenType.SET, ETokenType.SORT, ETokenType.START,
			ETokenType.STOP,
			ETokenType.STRING,
			ETokenType.SUBTRACT,
			ETokenType.UNSTRING,
			ETokenType.WRITE,
			ETokenType.XML,
			// other begin of statement:
			ETokenType.BASIS, ETokenType.CBL, ETokenType.CONTROL,
			ETokenType.COPY, ETokenType.DELETE, ETokenType.EJECT,
			ETokenType.ENTER, ETokenType.INSERT, ETokenType.READY,
			ETokenType.RESET, ETokenType.REPLACE, ETokenType.SERVICE,
			ETokenType.SKIP1, ETokenType.SKIP2, ETokenType.SKIP3,
			ETokenType.TITLE, ETokenType.USE, ETokenType.WHEN,
			// begin of statement for DELTA COBOL:
			ETokenType.PREPROCESSOR_DIRECTIVE);

	/** Those token types mark the end of a statement */
	private final static EnumSet<ETokenType> EOS = EnumSet.of(ETokenType.DOT,
			ETokenType.SENTINEL, ETokenType.END_ACCEPT, ETokenType.END_ADD,
			ETokenType.END_CALL, ETokenType.END_COMPUTE, ETokenType.END_DELETE,
			ETokenType.END_DISPLAY, ETokenType.END_DIVIDE,
			ETokenType.END_EVALUATE, ETokenType.END_IF, ETokenType.END_INVOKE,
			ETokenType.END_MULTIPLY, ETokenType.END_PERFORM,
			ETokenType.END_READ, ETokenType.END_RECEIVE, ETokenType.END_RETURN,
			ETokenType.END_REWRITE, ETokenType.END_SEARCH,
			ETokenType.END_START, ETokenType.END_STRING,
			ETokenType.END_UNSTRING, ETokenType.END_UNSTRING,
			ETokenType.END_WRITE, ETokenType.END_XML);

	/** {@inheritDoc} */
	@Override
	public <X extends Exception> boolean isEndOfStatementTokenType(
			ETokenType tokenType, ILookahead<ETokenType, X> lookahead) throws X {
		return EOS.contains(tokenType) || BOS.contains(lookahead.lookahead(1));
	}

	/** {@inheritDoc} */
	@Override
	public <X extends Exception> boolean isEndOfStatementToken(
			ETokenType tokenType, ILookahead<IToken, X> lookahead) throws X {
		return EOS.contains(tokenType)
				|| (lookahead.lookahead(1) != null && BOS.contains(lookahead
						.lookahead(1).getType()));
	}

}
