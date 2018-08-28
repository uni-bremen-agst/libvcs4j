package de.unibremen.informatik.st.libvcs4j.data;

import de.unibremen.informatik.st.libvcs4j.LineInfo;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class LineInfoImpl implements LineInfo {

	@NonNull
	private String id;

	@NonNull
	private String author;

	@NonNull
	private String message;

	@NonNull
	private LocalDateTime dateTime;

	private int line;

	@NonNull
	private String content;

	@NonNull
	private VCSFile file;
}
