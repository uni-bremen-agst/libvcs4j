package de.unibremen.informatik.st.libvcs4j.data;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * Pojo implementation of {@link VCSFile}.
 */
public class VCSFileImpl extends VCSModelElementImpl implements VCSFile {

	/**
	 * The relative path of a file.
	 */
	@NonNull
	@Getter
	@Setter
	private String relativePath;

	/**
	 * The revision of a file.
	 */
	@NonNull
	@Getter
	@Setter
	private Revision revision;

	/**
	 * Caches the contents of {@link #readAllBytes()}.
	 */
	private byte[] cache;

	@Override
	public Optional<Charset> guessCharset() throws IOException {
		final CharsetDetector detector = new CharsetDetector();
		detector.setText(readAllBytes());
		final CharsetMatch match = detector.detect();
		try {
			return Optional.ofNullable(match)
					.map(m -> Charset.forName(m.getName()));
		} catch (final Exception e) {
			return Optional.empty();
		}
	}

	@Override
	public byte[] readAllBytes() throws IOException {
		if (cache == null) {
			cache = getVCSEngine().readAllBytes(this);
		}
		return cache;
	}
}
