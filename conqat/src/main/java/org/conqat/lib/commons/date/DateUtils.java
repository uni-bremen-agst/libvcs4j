/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 The ConQAT Project                                   |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+-------------------------------------------------------------------------*/
package org.conqat.lib.commons.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.lib.commons.factory.IFactory;

/**
 * Utility methods for working on date objects.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 49912 $
 * @ConQAT.Rating GREEN Hash: 7E9A46576E9F4458F3FB3AE678F37F96
 */
public class DateUtils {

	/** The number of milliseconds per minute */
	private static final long MILLIS_PER_MINUTE = 1000 * 60;

	/** The number of milliseconds per hour */
	private static final long MILLIS_PER_HOUR = MILLIS_PER_MINUTE * 60;

	/** The number of milliseconds per day */
	private static final long MILLIS_PER_DAY = MILLIS_PER_HOUR * 24;

	/** Simple date format used by {@link #truncateToBeginOfDay(Date)} */
	private static final SimpleDateFormat yyyMMddFormat = new SimpleDateFormat(
			"yyyy-MM-dd");

	/** The factory used to create the date in {@link #getNow()}. */
	private static IFactory<Date, NeverThrownRuntimeException> nowFactory;

	/** Returns the latest date in a collection of dates */
	public static Date getLatest(Collection<Date> dates) {
		if (dates.isEmpty()) {
			return null;
		}
		return Collections.max(dates);
	}

	/** Returns the earliest date in a collection of dates */
	public static Date getEarliest(Collection<Date> dates) {
		if (dates.isEmpty()) {
			return null;
		}
		return Collections.min(dates);
	}

	/** Returns the earlier of two dates, or null, if one of the dates is null */
	public static Date min(Date d1, Date d2) {
		if (d1 == null || d2 == null) {
			return null;
		}

		if (d1.compareTo(d2) < 0) {
			return d1;
		}
		return d2;
	}

	/** Returns the later of two dates or null, if one of the dates is null. */
	public static Date max(Date d1, Date d2) {
		if (d1 == null || d2 == null) {
			return null;
		}

		if (d2.compareTo(d1) > 0) {
			return d2;
		}
		return d1;
	}

	/**
	 * Retrieves the date which is exactly the given number of days before now.
	 */
	public static Date getDateBefore(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -days);
		return calendar.getTime();
	}

	/**
	 * Returns the current time as a {@link Date} object. This is preferred to
	 * directly calling the {@link Date#Date()} constructor, as this method
	 * provides a central entry point that allows the notion of time to be
	 * tweaked (e.g. for testing, when we want a certain date to be returned).
	 * 
	 * The behavior of this method can be affected either by calling the
	 * {@link #testcode_setNowFactory(IFactory)} method, or by providing a fixed
	 * date in format "yyyyMMddHHmmss" as the system property
	 * "org.conqat.lib.commons.date.now".
	 */
	public static synchronized Date getNow() {
		if (nowFactory == null) {
			String property = System
					.getProperty("org.conqat.lib.commons.date.now");
			if (property == null) {
				nowFactory = new CurrentDateFactory();
			} else {
				try {
					nowFactory = new FixedDateFactory(new SimpleDateFormat(
							"yyyyMMddHHmmss").parse(property));
				} catch (ParseException e) {
					// fail hard in case of misconfiguration
					throw new RuntimeException(
							"Invalid date string provided via system property: "
									+ property, e);
				}
			}
		}
		return nowFactory.create();
	}

	/**
	 * Returns the factory that affects the notion of "now" in {@link #getNow()}
	 * . This should be only called from test code!
	 */
	public static synchronized IFactory<Date, NeverThrownRuntimeException> testcode_getNowFactory() {
		return nowFactory;
	}

	/**
	 * Sets the factory that affects the notion of "now" in {@link #getNow()}.
	 * This should be only called from test code!
	 */
	public static synchronized void testcode_setNowFactory(
			IFactory<Date, NeverThrownRuntimeException> nowFactory) {
		DateUtils.nowFactory = nowFactory;
	}

	/**
	 * Sets the given fixed date for "now" in {@link #getNow()}. This should be
	 * only called from test code!
	 */
	public static synchronized void testcode_setFixedDate(Date date) {
		testcode_setNowFactory(new FixedDateFactory(date));
	}

	/** A factory returning a fixed date. */
	public static class FixedDateFactory implements
			IFactory<Date, NeverThrownRuntimeException> {

		/** The date used as now. */
		private final Date now;

		/** Constructor. */
		public FixedDateFactory(Date now) {
			this.now = now;
		}

		/** {@inheritDoc} */
		@Override
		public Date create() throws NeverThrownRuntimeException {
			return (Date) now.clone();
		}
	}

	/** A factory returning the current date. */
	public static class CurrentDateFactory implements
			IFactory<Date, NeverThrownRuntimeException> {

		/** {@inheritDoc} */
		@Override
		public Date create() throws NeverThrownRuntimeException {
			return new Date();
		}
	}

	/** Returns a new Date that is one day later than the given date. */
	public static Date incrementByOneDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, 1);
		return c.getTime();
	}

	/**
	 * Returns a normalized version of the given date. Normalization is done by
	 * removing all time-of-day information.
	 */
	public static Date truncateToBeginOfDay(Date date) {
		String normalized = yyyMMddFormat.format(date);
		try {
			return yyyMMddFormat.parse(normalized);
		} catch (ParseException e) {
			throw new AssertionError(
					"Bug in SimpleDateFormat. Produces String it cannot parse.");
		}
	}

	/** Converts the given number of days into milliseconds. */
	public static long daysToMilliseconds(int days) {
		return days * MILLIS_PER_DAY;
	}

	/** Converts the given number of hours into milliseconds. */
	public static long hoursToMilliseconds(int hours) {
		return hours * MILLIS_PER_HOUR;
	}

	/** Converts the given number of minutes into milliseconds. */
	public static long minutesToMilliseconds(int minutes) {
		return minutes * MILLIS_PER_MINUTE;
	}

	/**
	 * Returns the difference between two dates in the given time unit.
	 */
	public static long diff(Date earlier, Date later, TimeUnit timeUnit) {
		long diffInMillies = later.getTime() - earlier.getTime();
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

}