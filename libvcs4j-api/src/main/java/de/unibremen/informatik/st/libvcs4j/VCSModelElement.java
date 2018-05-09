package de.unibremen.informatik.st.libvcs4j;

/**
 * Defines the API of all version control system related model elements.
 */
public interface VCSModelElement {

	/**
	 * Returns the {@link VCSEngine} used to extract this model element.
	 *
	 * @return
	 * 		The {@link VCSEngine} used to extract this model element.
	 */
	VCSEngine getVCSEngine();
}
