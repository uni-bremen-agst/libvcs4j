package de.unibremen.informatik.st.libvcs4j;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

public class ValidationTest {

	@Test
	public void notNull() {
		assertThatNullPointerException()
				.isThrownBy(() -> Validate.notNull(null));
	}

	@Test
	public void notNullWithMessage() {
		assertThatNullPointerException()
				.isThrownBy(() -> Validate.notNull(null, "%s", "foobar"))
				.withMessage("foobar");
	}

	@Test
	public void notNullWithNonNull() {
		assertThat(Validate.notNull("not null"))
				.isEqualTo("not null");
	}

	@Test
	public void notNullWithNonNullAndMessage() {
		assertThat(Validate.notNull("not null", "%s", "foobar"))
				.isEqualTo("not null");
	}

	@Test
	public void noNullElements() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> Validate.noNullElements(
						Arrays.asList("1", null, "2")));
	}

	@Test
	public void noNullElementsWithMessage() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> Validate.noNullElements(
						Arrays.asList("1", null, "2"), "%s", "foobar"))
				.withMessage("foobar");
	}

	@Test
	public void noNullElementsWithNonNull() {
		final List<String> list = Arrays.asList("1", "2", "3");
		assertThat(Validate.noNullElements(list))
				.isEqualTo(list);
	}

	@Test
	public void noNullElementsWithNonNullAndMessage() {
		final List<String> list = Arrays.asList("1", "2", "3");
		assertThat(Validate.noNullElements(list, "%s", "foobar"))
				.isEqualTo(list);
	}

	@Test
	public void notEmpty() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> Validate.notEmpty(Collections.emptyList()));
	}

	@Test
	public void notEmptyWithMessage() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> Validate.notEmpty(
						Collections.emptyList(), "%s", "foobar"))
				.withMessage("foobar");
	}

	@Test
	public void notEmptyWithNonEmpty() {
		final Set<Integer> set = new HashSet<>();
		set.add(1);
		set.add(10);
		set.add(100);
		assertThat(Validate.notEmpty(set))
				.isEqualTo(set);
	}

	@Test
	public void notEmptyWithNonEmptyAndMessage() {
		final Set<Integer> set = new HashSet<>();
		set.add(1);
		set.add(10);
		set.add(100);
		assertThat(Validate.notEmpty(set, "%s", "foobar"))
				.isEqualTo(set);
	}

	@Test
	public void hasPatter() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> Validate.hasPattern("a", "\\d"));
	}

	@Test
	public void hasPatternWithMessage() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> Validate.hasPattern(
						"a", "\\d", "%s", "foobar"))
				.withMessage("foobar");
	}

	@Test
	public void hasPatternWithMatchingPattern() {
		assertThat(Validate.hasPattern("test", "\\w*"))
				.isEqualTo("test");
	}

	@Test
	public void hasPatternWithMatchingPatternAndMessage() {
		assertThat(Validate.hasPattern("test", "\\w*", "%s", "foobar"))
				.isEqualTo("test");
	}

	@Test
	public void isGreaterThan() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> Validate.isGreaterThan(4, 5));
	}

	@Test
	public void isGreaterThanWithMessage() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> Validate.isGreaterThan(
						4, 5, "%s", "foobar"))
				.withMessage("foobar");
	}

	@Test
	public void isGreaterThanWithEquals() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> Validate.isGreaterThan(5, 5));
	}

	@Test
	public void isGreaterThanWithEqualsAndMessage() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> Validate.isGreaterThan(
						5, 5, "%s", "foobar"))
				.withMessage("foobar");
	}

	@Test
	public void isGreaterThanWithLess() {
		assertThat(Validate.isGreaterThan(10, 6))
				.isEqualTo(10);
	}

	@Test
	public void isGreaterThanWithLessAndMessage() {
		assertThat(Validate.isGreaterThan(10, 6, "%s", "foobar"))
				.isEqualTo(10);
	}

	@Test
	public void isLessThan() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> Validate.isLessThan("c", "a"));
	}

	@Test
	public void isLessThanWithMessage() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> Validate.isLessThan(
						"c", "a", "%s", "foobar"))
				.withMessage("foobar");
	}

	@Test
	public void isLessThanWithEquals() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> Validate.isLessThan("c", "c"));
	}

	@Test
	public void isLessThanWithEqualsAndMessage() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> Validate.isLessThan(
						"c", "c", "%s", "foobar"))
				.withMessage("foobar");
	}

	@Test
	public void isLessThanWithGreater() {
		assertThat(Validate.isLessThan("c", "z"))
				.isEqualTo("c");
	}

	@Test
	public void isLessThanWithGreaterAndMessage() {
		assertThat(Validate.isLessThan("c", "z", "%s", "foobar"))
				.isEqualTo("c");
	}

	@Test
	public void isLessThanOrEquals() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> Validate.isLessThanOrEquals(6.0, 3.3));
	}

	@Test
	public void isLessThanOrEqualsWithMessage() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> Validate.isLessThanOrEquals(
						6.0, 3.3, "%s", "foobar"))
				.withMessage("foobar");
	}

	@Test
	public void isLessThanOrEqualsWithEquals() {
		assertThat(Validate.isLessThanOrEquals(5.5, 5.5))
				.isEqualTo(5.5, offset(0.0001));
	}

	@Test
	public void isLessThanOrEqualsWithEqualsAndMessage() {
		assertThat(Validate.isLessThanOrEquals(5.5, 5.5, "%s", "foobar"))
				.isEqualTo(5.5, offset(0.0001));
	}

	@Test
	public void isLessThanOrEqualsWithGreater() {
		assertThat(Validate.isLessThanOrEquals(7.0, 7.1))
				.isEqualTo(7.0, offset(0.0001));
	}

	@Test
	public void isLessThanOrEqualsWithGreaterAndMessage() {
		assertThat(Validate.isLessThanOrEquals(7.0, 7.1, "%s", "foobar"))
				.isEqualTo(7.0, offset(0.0001));
	}

	@Test
	public void hasRangeWithFromViolation() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> Validate.hasRange(3, 4, 5));
	}

	@Test
	public void hasRangeWithToViolation() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> Validate.hasRange(3, 1, 2));
	}

	@Test
	public void hasRangeWithValidRange() {
		assertThat(Validate.hasRange(5, 2, 7))
				.isEqualTo(5);
	}

	@Test
	public void hasRangeWithValidRangeInclusive() {
		assertThat(Validate.hasRange(10, 10, 10))
				.isEqualTo(10);
	}
}
