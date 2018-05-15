package de.unibremen.informatik.st.libvcs4j.data;

import de.unibremen.informatik.st.libvcs4j.ITEngine;
import de.unibremen.informatik.st.libvcs4j.ITModelElement;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Pojo implementation of {@link ITModelElement}.
 */
@Getter
@Setter
public class ITModelElementImpl implements ITModelElement {

	/**
	 * The engine of a model element.
	 */
	@NonNull
	private ITEngine ITEngine;
}
