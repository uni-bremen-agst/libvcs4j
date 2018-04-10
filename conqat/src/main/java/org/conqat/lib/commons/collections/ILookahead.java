package org.conqat.lib.commons.collections;

/**
 * Offers the ability to perform lookahead on an ordered collection.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 7B5BD6E848F8DB7C1E86B5E375BB029E
 */
public interface ILookahead<Data, X extends Exception> {

	/**
	 * Returned elements remain in the underlying data structure and are still
	 * accessible.
	 * 
	 * @param index
	 *            Number of elements that are looked ahead
	 * 
	 * @return Element at index, or null, if the provider doesn't contain that
	 *         many elements anymore
	 */
	public Data lookahead(int index) throws X;
}
