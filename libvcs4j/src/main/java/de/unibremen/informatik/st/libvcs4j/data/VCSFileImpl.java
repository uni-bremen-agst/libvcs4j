package de.unibremen.informatik.st.libvcs4j.data;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import de.unibremen.informatik.st.libvcs4j.Revision;
import de.unibremen.informatik.st.libvcs4j.VCSFile;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Pojo implementation of {@link VCSFile}.
 */
@ToString(of = {"relativePath", "revision"}, doNotUseGetters = true)
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
	 * Caches the contents of this file (see {@link #readAllBytes()}). Use a
	 * {@link SoftReference} to avoid an {@link OutOfMemoryError} due to
	 * hundrets of thousands of cached file contents.
	 */
	private SoftReference<byte[]> contentsCache = new SoftReference<>(null);

	/**
	 * Caches the charset of this file (see {@link #guessCharset()}).
	 */
	private AtomicReference<Charset> charsetCache = null;

	@Override
	public Optional<Charset> guessCharset() throws IOException {
		if (charsetCache == null) {
			final CharsetDetector detector = new CharsetDetector();
			detector.setText(readAllBytes());
			final CharsetMatch match = detector.detect();
			Charset charset;
			try {
				charset = Optional.ofNullable(match)
						.map(m -> Charset.forName(m.getName()))
						.orElse(null);
			} catch (final Exception e) {
				charset = null;
			}
			charsetCache = new AtomicReference<>(charset);
		}
		return Optional.ofNullable(charsetCache.get());
	}

	@Override
	public byte[] readAllBytes() throws IOException {
		byte[] bytes = contentsCache.get();
		if (bytes == null) {
			bytes = getVCSEngine().readAllBytes(this);
			contentsCache = new SoftReference<>(bytes);
		}
		return bytes;
	}
}
