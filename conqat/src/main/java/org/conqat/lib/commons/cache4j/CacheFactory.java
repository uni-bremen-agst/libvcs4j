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
package org.conqat.lib.commons.cache4j;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.cache4j.backend.ECachingStrategy;
import org.conqat.lib.commons.cache4j.backend.ICacheBackend;
import org.conqat.lib.commons.enums.EnumUtils;
import org.conqat.lib.commons.factory.IParameterizedFactory;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * The central factory used for managing and creating caches. The type of cache
 * is determined based on the name of the cache (possibly based on the class
 * name). For this, there is a list of rules with regular expressions. The first
 * rule, whose expression matches the name is used to create the cache. If no
 * rule matches, the default rule is used.
 * <p>
 * The rules can also be loaded from a configuration file, where each line
 * contains a rule (or a comment starting with '#'). Each rule has the format
 * "PATTERN -&gt; THREAD:STRATEGY:PARAMETER", where PATTERN is the pattern used
 * for matching, THREAD is a constant from {@link ECacheThreadSupport}, STRATEGY
 * is a constant from {@link ECachingStrategy}, and PARAMETER is a parameter for
 * the strategy.
 * <p>
 * This factory also manages links to all caches created (via this factory).
 * These links are used for generating statistics. To not avoid garbage
 * collection of caches (e.g. when many caches are created only temporarily), we
 * use weak references which to not hinder the GC from collecting a cache.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 43065 $
 * @ConQAT.Rating GREEN Hash: AC058719262738842E50F3060A1C22DF
 */
public class CacheFactory {

	/**
	 * Number of creations after which the list of caches is cleaned from unused
	 * caches.
	 */
	private static final int CLEANUP_INTERVAL = 100;

	/** Pattern used for parsing rules. */
	private static final Pattern RULE_PATTERN = Pattern
			.compile("([^\\s]+)\\s*->\\s*([^\\s:]+):([^\\s:]+):([0-9]+)");

	/** The singleton instance. */
	private static final CacheFactory INSTANCE = new CacheFactory();

	/** Stores all caches created so far. */
	private final List<CacheInfo> caches = new ArrayList<CacheInfo>();

	/**
	 * Stores creation rules. As we insert both front and back, this is a linked
	 * list.
	 */
	private final List<CacheCreationRule> rules = new LinkedList<CacheCreationRule>();

	/** The default rule that is used if no other tule matches. */
	private CacheCreationRule defaultRule = new CacheCreationRule("",
			ECacheThreadSupport.NONE, ECachingStrategy.OFF, 0);

	/** Counts the number of caches created so far. */
	private int creationCount = 0;

	/** Hidden constructor. */
	private CacheFactory() {
		// nothing to do
	}

	/** Returns the singleton instance. */
	public static CacheFactory getInstance() {
		return INSTANCE;
	}

	/** Sets the default rule. */
	public void setDefaultRule(CacheCreationRule rule) {
		CCSMPre.isNotNull(rule);
		defaultRule = rule;
	}

	/**
	 * Loads the caching rules from a UTF-8 encoded file. See the class comment
	 * for a description of the file format.
	 */
	public void loadCacheConfiguration(File file) throws IOException,
			CacheRuleParsingException {
		loadCacheConfiguration(FileSystemUtils.readFileUTF8(file));
	}

	/**
	 * Loads the caching rules from a string. See the class comment for a
	 * description of the file format.
	 */
	public void loadCacheConfiguration(String config)
			throws CacheRuleParsingException {
		loadCacheConfiguration(StringUtils.splitLines(config));
	}

	/**
	 * Loads the caching rules from individual lines. See the class comment for
	 * a description of the file format.
	 */
	public void loadCacheConfiguration(String[] lines)
			throws CacheRuleParsingException {
		clearRules();

		for (int lineNumber = 0; lineNumber < lines.length; ++lineNumber) {
			String line = lines[lineNumber];

			line = line.trim();
			if (line.isEmpty() || line.startsWith("#")) {
				continue;
			}

			try {
				addRuleBack(parseRule(line));
			} catch (CacheRuleParsingException e) {
				e.setLine(lineNumber + 1);
				throw e;
			}
		}
	}

	/** Parses a rule from a configuration line. */
	private CacheCreationRule parseRule(String line)
			throws CacheRuleParsingException {
		Matcher matcher = RULE_PATTERN.matcher(line);

		if (!matcher.matches()) {
			throw new CacheRuleParsingException(
					"Line does not follow definition pattern!");
		}

		String pattern = matcher.group(1);

		ECacheThreadSupport threadSupport = EnumUtils.valueOfIgnoreCase(
				ECacheThreadSupport.class, matcher.group(2));
		if (threadSupport == null) {
			throw new CacheRuleParsingException("Unknown thread support: "
					+ matcher.group(2));
		}

		ECachingStrategy cachingStrategy = EnumUtils.valueOfIgnoreCase(
				ECachingStrategy.class, matcher.group(3));
		if (cachingStrategy == null) {
			throw new CacheRuleParsingException("Unknown caching strategy: "
					+ matcher.group(3));
		}

		try {
			return new CacheCreationRule(pattern, threadSupport,
					cachingStrategy, Integer.parseInt(matcher.group(4)));
		} catch (PatternSyntaxException e) {
			throw new CacheRuleParsingException("Could not parse pattern: "
					+ e.getMessage(), e);
		} catch (NumberFormatException e) {
			throw new CacheRuleParsingException("Could not parse parameter: "
					+ e.getMessage(), e);
		}
	}

	/** Removes all rules. */
	public void clearRules() {
		rules.clear();
	}

	/** Adds a rule to the beginning (i.e. it is checked before all others). */
	public void addRuleFront(CacheCreationRule rule) {
		rules.add(0, rule);
	}

	/** Adds a rule to the end (i.e. it is checked after all other rules). */
	public void addRuleBack(CacheCreationRule rule) {
		rules.add(rule);
	}

	/** Returns a cache based on the provided name and the configured rules. */
	public static <K, V, X extends Exception> ICache<K, V, X> obtainCache(
			String name, IParameterizedFactory<V, K, X> factory) {
		return getInstance().obtainCacheByRules(name, factory);
	}

	/** Returns a cache based on the provided name and the configured rules. */
	private synchronized <K, V, X extends Exception> ICache<K, V, X> obtainCacheByRules(
			String name, IParameterizedFactory<V, K, X> factory) {
		CacheCreationRule usedRule = defaultRule;
		for (CacheCreationRule rule : rules) {
			if (rule.matches(name)) {
				usedRule = rule;
				break;
			}
		}

		ICache<K, V, X> cache = usedRule.createCache(name, factory);

		creationCount += 1;
		if ((creationCount % CLEANUP_INTERVAL) == 0) {
			cleanCaches();
		}

		caches.add(new CacheInfo(cache, usedRule));
		return cache;
	}

	/** Clears the list of caches from all non-existing ones. */
	private void cleanCaches() {
		List<CacheInfo> copy = new ArrayList<CacheInfo>(caches);
		caches.clear();
		for (CacheInfo cache : copy) {
			if (cache.isAlive()) {
				caches.add(cache);
			}
		}
	}

	/** Returns a cache for the given class. */
	public static <K, V, X extends Exception> ICache<K, V, X> obtainCache(
			Class<?> clazz, IParameterizedFactory<V, K, X> factory) {
		return obtainCache(clazz.getName(), factory);
	}

	/**
	 * Clears all data from all caches managed by cache4j.
	 * 
	 * @param allThreads
	 *            if this is true, cached data will be cleared in all threads.
	 *            Otherwise, only cached data from the current thread will be
	 *            removed (if possible).
	 */
	public void clearAllCachedData(boolean allThreads) {
		for (CacheInfo cache : new ArrayList<CacheInfo>(caches)) {
			cache.clearAllCachedData(allThreads);
		}
	}

	/**
	 * Returns a string containing statistics on all existing caches in CSV
	 * format.
	 */
	public String getStatistics() {
		StringBuilder sb = new StringBuilder();

		sb.append("Name;Thread Support;Cache Strategy; Strategy Parameter; Hits; Misses; Miss Cost Millis"
				+ StringUtils.CR);
		for (CacheInfo cache : new ArrayList<CacheInfo>(caches)) {
			cache.appendStatistics(sb);
		}

		return sb.toString();
	}

	/**
	 * Clears the list of caches used for reporting. This method is only used
	 * for testing.
	 */
	/* package */void clearCaches() {
		caches.clear();
	}

	/** A single rule used for cache creation. */
	public static final class CacheCreationRule {

		/** The pattern used to match the cache name. */
		private final Pattern pattern;

		/** The required thread support. */
		private final ECacheThreadSupport threadSupport;

		/** The caching strategy used. */
		private final ECachingStrategy cachingStrategy;

		/** The parameter used for the caching strategy. */
		private final int cachingStrategyParameter;

		/** Constructor. */
		public CacheCreationRule(Pattern pattern,
				ECacheThreadSupport threadSupport,
				ECachingStrategy cachingStrategy, int cachingStrategyParameter) {
			this.pattern = pattern;
			this.threadSupport = threadSupport;
			this.cachingStrategy = cachingStrategy;
			this.cachingStrategyParameter = cachingStrategyParameter;
		}

		/** Constructor. */
		public CacheCreationRule(String pattern,
				ECacheThreadSupport threadSupport,
				ECachingStrategy cachingStrategy, int cachingStrategyParameter)
				throws PatternSyntaxException {
			this(Pattern.compile(pattern), threadSupport, cachingStrategy,
					cachingStrategyParameter);
		}

		/** Returns whether the rules matches the given name. */
		public boolean matches(String name) {
			return pattern.matcher(name).matches();
		}

		/** Creates the cache from this rule. */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public <K, V, X extends Exception> ICache<K, V, X> createCache(
				String name, IParameterizedFactory<V, K, X> factory) {
			return threadSupport.createCache(name, factory,
					(ICacheBackend) cachingStrategy
							.getBackend(cachingStrategyParameter));
		}
	}

	/** Class for storing information on created caches. */
	private static final class CacheInfo {

		/** Reference to the cache. */
		private final WeakReference<ICache<?, ?, ?>> cacheRef;

		/** The rule from which the cache was created. */
		private final CacheCreationRule creationRule;

		/** Constructor. */
		public CacheInfo(ICache<?, ?, ?> cache, CacheCreationRule creationRule) {
			cacheRef = new WeakReference<ICache<?, ?, ?>>(cache);
			this.creationRule = creationRule;
		}

		/** Clears all cached data. */
		public void clearAllCachedData(boolean allThreads) {
			ICache<?, ?, ?> cache = cacheRef.get();
			if (cache == null) {
				return;
			}

			cache.clear(allThreads);
		}

		/** Returns whether the cache is still in existence. */
		public boolean isAlive() {
			return cacheRef.get() != null;
		}

		/** Appends statistics on this cache in CSV format (including new line) */
		public void appendStatistics(StringBuilder sb) {
			ICache<?, ?, ?> cache = cacheRef.get();
			if (cache == null) {
				return;
			}

			sb.append(cache.getName() + ";" + creationRule.threadSupport.name()
					+ ";" + creationRule.cachingStrategy.name() + ";"
					+ creationRule.cachingStrategyParameter + ";"
					+ cache.getHits() + ";" + cache.getMisses() + ";"
					+ cache.getMissCostMillis() + StringUtils.CR);
		}
	}
}