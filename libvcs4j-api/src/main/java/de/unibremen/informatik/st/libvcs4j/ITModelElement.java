package de.unibremen.informatik.st.libvcs4j;

/**
 * Defines the API of all issue tracker related model elements.
 */
public interface ITModelElement {

	/**
	 * Returns the {@link ITEngine} used to extract this model element.
	 *
	 * @return
	 * 		The {@link ITEngine} used to extract this model element.
	 */
	ITEngine getITEngine();
}
