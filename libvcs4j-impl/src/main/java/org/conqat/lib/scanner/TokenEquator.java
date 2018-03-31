/*-----------------------------------------------------------------------+
 | eu.cqse.conqat.engine.sourcecode
 |                                                                       |
   $Id: TokenEquator.java 48432 2014-03-07 15:46:50Z kanis $            
 |                                                                       |
 | Copyright (c)  2009-2012 CQSE GmbH                                 |
 +-----------------------------------------------------------------------*/
package org.conqat.lib.scanner;

import org.conqat.lib.commons.equals.IEquator;
import org.conqat.lib.scanner.IToken;

/**
 * A class for testing tokens for equality with respect to type and content.
 * 
 * @author $Author: kanis $
 * @version $Rev: 48432 $
 * @ConQAT.Rating GREEN Hash: A72F82720604D06D64807591EE83A306
 */
public class TokenEquator implements IEquator<IToken> {

	/** Reusable instance. */
	public static final TokenEquator INSTANCE = new TokenEquator();

	/** {@inheritDoc} */
	@Override
	public boolean equals(IToken token1, IToken token2) {
		return token1.getType() == token2.getType()
				&& token1.getText().equals(token2.getText());
	}
}