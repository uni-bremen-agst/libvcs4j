package de.unibremen.informatik.st.libvcs4j.data;

import de.unibremen.informatik.st.libvcs4j.VCSEngine;
import de.unibremen.informatik.st.libvcs4j.VCSModelElement;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Pojo implementation of {@link VCSModelElement}.
 */
@Getter
@Setter
public class VCSModelElementImpl implements VCSModelElement {

	/**
	 * The engine of a model element.
	 */
	@NonNull
	private VCSEngine VCSEngine;
}
