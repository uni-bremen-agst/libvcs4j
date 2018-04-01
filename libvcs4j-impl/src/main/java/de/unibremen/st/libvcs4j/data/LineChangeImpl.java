package de.unibremen.st.libvcs4j.data;

import de.unibremen.st.libvcs4j.LineChange;
import de.unibremen.st.libvcs4j.VCSFile;
import org.apache.commons.lang3.Validate;

/**
 * Implementation for {@link LineChange}.
 */
public class LineChangeImpl implements LineChange {

	private Type type;
	private int line;
	private String content;
	private VCSFile file;

	@Override
	public Type getType() {
		return type;
	}

	public void setType(Type pType) {
		type = Validate.notNull(pType);
	}

	@Override
	public int getLine() {
		return line;
	}

	public void setLine(int pLine) {
		Validate.isTrue(pLine >= 0, "%d < 0", pLine);
		line = pLine;
	}

	@Override
	public String getContent() {
		return content;
	}

	public void setContent(String pContent) {
		content = Validate.notNull(pContent);
	}

	@Override
	public VCSFile getFile() {
		return file;
	}

	public void setFile(VCSFile pFile) {
		file = Validate.notNull(pFile);
	}
}
