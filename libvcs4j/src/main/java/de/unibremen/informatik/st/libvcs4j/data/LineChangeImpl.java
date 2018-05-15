package de.unibremen.informatik.st.libvcs4j.data;

import de.unibremen.informatik.st.libvcs4j.LineChange;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Pojo implementation of {@link LineChange}.
 */
@Getter
@Setter
public class LineChangeImpl extends VCSModelElementImpl implements LineChange {

	/**
	 * The type of a line change.
	 */
	@NonNull
	private Type type;

	/**
	 * The line number of a line change.
	 */
	@NonNull
	private int line;

	/**
	 * The content of a line change.
	 */
	@NonNull
	private String content;

	/**
	 * The file a line change belongs to.
	 */
	@NonNull
	private VCSFile file;
}
