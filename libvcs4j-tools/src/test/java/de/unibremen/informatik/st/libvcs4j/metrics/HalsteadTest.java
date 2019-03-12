package de.unibremen.informatik.st.libvcs4j.metrics;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.*;

public class HalsteadTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private final Complexity.Halstead halstead =
			new Complexity.Halstead(12, 7, 27, 15);

	@Test
	public void vocabulary() {
		assertThat(halstead.getVocabulary()).isEqualTo(19);
	}

	@Test
	public void programLength() {
		assertThat(halstead.getProgramLength()).isEqualTo(42);
	}

	@Test
	public void volume() {
		assertThat(halstead.getVolume()).isEqualTo(178.4, within(0.1));
	}

	@Test
	public void difficulty() {
		assertThat(halstead.getDifficulty()).isEqualTo(12.85, within(0.01));
	}

	@Test
	public void programLevel() {
		assertThat(halstead.getProgramLevel()).isEqualTo(0.077, within(0.01));
	}

	@Test
	public void effort() {
		// within(2.0) is necessary due to double precision limits.
		assertThat(halstead.getEffort()).isEqualTo(2292, within(2.0));
	}

	@Test
	public void requiredTime() {
		assertThat(halstead.getRequiredTime()).isEqualTo(127.35, within(0.1));
	}

	@Test
	public void bugs() {
		assertThat(halstead.getBugs()).isEqualTo(0.05, within(0.01));
	}

	@Test
	public void constructor001() {
		thrown.expect(IllegalArgumentException.class);
		new Complexity.Halstead(3, 0, 2, 0);
	}

	@Test
	public void constructor002() {
		thrown.expect(IllegalArgumentException.class);
		new Complexity.Halstead(0, 6, 0, 2);
	}

	@Test
	public void constructor003() {
		Complexity.Halstead copy = new Complexity.Halstead(halstead);
		assertThat(copy.getNumDistinctOperators())
				.isEqualTo(halstead.getNumDistinctOperators());
		assertThat(copy.getNumDistinctOperands())
				.isEqualTo(halstead.getNumDistinctOperands());
		assertThat(copy.getNumOperators())
				.isEqualTo(halstead.getNumOperators());
		assertThat(copy.getNumOperands())
				.isEqualTo(halstead.getNumOperands());
	}

	@Test
	public void constructor004() {
		thrown.expect(NullPointerException.class);
		new Complexity.Halstead(null);
	}

	@Test
	public void add() {
		final Complexity.Halstead add =
				halstead.add(new Complexity.Halstead(7, 7, 7, 7));
		assertThat(add.getNumDistinctOperators()).isEqualTo(19);
		assertThat(add.getNumDistinctOperands()).isEqualTo(14);
		assertThat(add.getNumOperators()).isEqualTo(34);
		assertThat(add.getNumOperands()).isEqualTo(22);
	}

	@Test
	public void volumeOfEmpty() {
		assertThat(Complexity.Halstead.EMPTY_HALSTEAD.getVolume())
				.isEqualTo(0);
	}

	@Test
	public void difficultyOfEmpty() {
		assertThat(Complexity.Halstead.EMPTY_HALSTEAD.getDifficulty())
				.isEqualTo(0);
	}

	@Test
	public void programLevelOfEmpty() {
		assertThat(Complexity.Halstead.EMPTY_HALSTEAD.getProgramLevel())
				.isEqualTo(0);
	}
}
