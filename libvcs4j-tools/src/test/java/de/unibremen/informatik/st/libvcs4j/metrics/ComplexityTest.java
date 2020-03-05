package de.unibremen.informatik.st.libvcs4j.metrics;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.*;

public class ComplexityTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private final Complexity complexity =
			new Complexity(9, new Complexity.Halstead(12, 7, 27, 15));

	@Test
	public void constructor001() {
		thrown.expect(IllegalArgumentException.class);
		new Complexity(-1, Complexity.Halstead.EMPTY_HALSTEAD);
	}

	@Test
	public void constructor002() {
		thrown.expect(NullPointerException.class);
		new Complexity(0, null);
	}

	@Test
	public void constructor003() {
		Complexity copy = new Complexity(complexity);
		assertThat(copy.getMcCabe()).isEqualTo(complexity.getMcCabe());
		assertThat(copy.getHalstead()).isEqualTo(complexity.getHalstead());
	}

	@Test
	public void add() {
		Complexity add = complexity.add(complexity);
		assertThat(add.getMcCabe()).isEqualTo(18);
	}

	@Test
	public void mcCabeOfEmpty() {
		assertThat(Complexity.EMPTY_COMPLEXITY.getMcCabe()).isEqualTo(0);
	}
}
