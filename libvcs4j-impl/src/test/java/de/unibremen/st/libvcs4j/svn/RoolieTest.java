package de.unibremen.st.libvcs4j.svn;

import de.unibremen.st.libvcs4j.FileChange;
import de.unibremen.st.libvcs4j.VCSBaseTest;
import de.unibremen.st.libvcs4j.VCSEngine;
import de.unibremen.st.libvcs4j.Version;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@SuppressWarnings({"deprecation", "ConstantConditions"})
public class RoolieTest extends VCSBaseTest {

	@Override
	public String getTarGZFile() {
		return "roolie.tar.gz";
	}

	@Override
	public String getFolderInTarGZ() {
		return "roolie";
	}

	///////////////////////// Datetime interval tests /////////////////////////

	private SVNEngine createProvider(
			final String pRoot,
			final String pFrom,
			final String pTo) {
		return new SVNEngine(
				"file://" + input.toString(),
				pRoot,
				output,
				pFrom, pTo);
	}

	private SVNEngine createProvider(
			final String pRoot) {
		return createProvider(pRoot, "0", "1000");
	}

	@Test
	public void listRevisionsEmptyRoot() throws IOException {
		final SVNEngine engine = createProvider("");
		final List<String> revisions = engine.listRevisions();
		assertEquals(64, revisions.size());
		for (int i = 0; i < revisions.size(); i++) {
			assertEquals(String.valueOf(i+1), revisions.get(i));
		}
	}

	@Test
	public void listRevisionsRoolieCoreRoot() throws IOException {
		final SVNEngine engine = createProvider("roolie-core");
		final List<String> revisions = engine.listRevisions();
		assertEquals(11, revisions.size());
		assertEquals(String.valueOf("40"), revisions.get(0));
		assertEquals(String.valueOf("41"), revisions.get(1));
		assertEquals(String.valueOf("51"), revisions.get(2));
		assertEquals(String.valueOf("53"), revisions.get(3));
		assertEquals(String.valueOf("55"), revisions.get(5));
		assertEquals(String.valueOf("56"), revisions.get(6));
		assertEquals(String.valueOf("57"), revisions.get(7));
		assertEquals(String.valueOf("58"), revisions.get(8));
		assertEquals(String.valueOf("61"), revisions.get(9));
		assertEquals(String.valueOf("62"), revisions.get(10));
	}

	@Test
	public void listRevisionsComponent() throws IOException {
		final SVNEngine engine = createProvider(
			"roolie-core/src/main/java/net/sf/roolie/core/util/component/");
		final List<String> revisions = engine.listRevisions();
		assertEquals(3, revisions.size());
		assertEquals(String.valueOf("41"), revisions.get(0));
		assertEquals(String.valueOf("61"), revisions.get(1));
		assertEquals(String.valueOf("62"), revisions.get(2));
	}

	@Test
	public void forEachIteration() {
		int i = 1;
		for (final Version v : createProvider("")) {
			assertEquals(String.valueOf(i++), v.getRevision().getId());
		}
	}

	@Test
	public void nextIteration() throws IOException {
		final VCSEngine engine = createProvider("");
		int i = 1;
		for (Optional<Version> optional = engine.next();
			 optional.isPresent();
			 optional = engine.next()) {
			assertEquals(String.valueOf(i++),
					optional.get().getRevision().getId());
		}
	}

	@Test
	public void commitMessage36() throws IOException {
		final VCSEngine engine = createProvider("", "35", "37");
		engine.next();
		final Optional<Version> optional = engine.next();
		assertTrue(optional.isPresent());
		assertEquals(
			"Added more arguments to test and now tests another rule.",
			optional.get().getCommits().get(0).getMessage());
	}

	@Test
	public void changes36() throws IOException {
		final VCSEngine engine = createProvider("", "35", "37");
		engine.next();
		final Optional<Version> optional = engine.next();
		assertTrue(optional.isPresent());
		final Version version = optional.get();
		final List<FileChange> changes = version.getFileChanges();
		assertEquals(1, changes.size());
		assertEquals(FileChange.Type.MODIFY, changes.get(0).getType());

		final String expected = Paths.get(output.toAbsolutePath().toString(),
				"Roolie/src/org/roolie/RulesEngine.java").toString();
		assertEquals(expected, changes.get(0).getOldFile().get().getPath());
		assertEquals(expected, changes.get(0).getNewFile().get().getPath());
	}

	@Test
	public void fileContent36() throws IOException {
		final VCSEngine engine = createProvider("", "35", "37");
		engine.next();
		final Optional<Version> optional = engine.next();
		assertTrue(optional.isPresent());
		final Version version = optional.get();
		final List<FileChange> changes = version.getFileChanges();
		assertEquals(1, changes.size());
		final String oldContent= "///////////////////////////////////////////////////////////////////////////////\n" +
				"//  Copyright 2010 Ryan Kennedy <rallyredevo AT users DOT sourceforge DOT net>\n" +
				"//\n" +
				"//  This file is part of Roolie.\n" +
				"//\n" +
				"//  Roolie is free software: you can redistribute it and/or modify\n" +
				"//  it under the terms of the GNU Lesser General Public License as published by\n" +
				"//  the Free Software Foundation, either version 3 of the License, or any later\n" +
				"//  version.\n" +
				"//\n" +
				"//  Roolie is distributed in the hope that it will be useful,\n" +
				"//  but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
				"//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
				"//  GNU Lesser General Public License for more details.\n" +
				"//\n" +
				"//  You should have received a copy of the GNU Lesser General Public License\n" +
				"//  along with Roolie.  If not, see <http://www.gnu.org/licenses/>.\n" +
				"///////////////////////////////////////////////////////////////////////////////\n" +
				"package org.roolie;\n" +
				"\n" +
				"import java.io.File;\n" +
				"import java.io.InputStream;\n" +
				"import java.util.LinkedList;\n" +
				"import java.util.List;\n" +
				"import org.roolie.config.RulesConfig;\n" +
				"import org.roolie.config.elmt.RuleDefElmt;\n" +
				"import org.roolie.config.elmt.RuleElmt;\n" +
				"import org.roolie.config.elmt.RuleImplElmt;\n" +
				"import org.roolie.factory.InstanceFactory;\n" +
				"import org.roolie.factory.RulesConfigFactory;\n" +
				"import org.roolie.util.RUtil;\n" +
				"import org.w3c.dom.Document;\n" +
				"import org.w3c.dom.Node;\n" +
				"import org.xml.sax.InputSource;\n" +
				"\n" +
				"public class RulesEngine\n" +
				"{\n" +
				"\n" +
				"  protected final RulesConfig rulesConfig;\n" +
				"\n" +
				"  protected final RuleFactory ruleFactory;\n" +
				"\n" +
				"  protected final RuleEvaluator ruleEvaluator;\n" +
				"\n" +
				"  protected static final InstanceFactory<RuleFactory> ruleFactoryFactory =\n" +
				"    new InstanceFactory<RuleFactory>();\n" +
				"\n" +
				"  protected static final InstanceFactory<RuleEvaluator> ruleEvaluatorFactory =\n" +
				"    new InstanceFactory<RuleEvaluator>();\n" +
				"\n" +
				"  public RulesEngine(String configURI)\n" +
				"  {\n" +
				"    this.rulesConfig = RulesConfigInitializer.initRulesConfig(configURI);\n" +
				"    this.ruleFactory = initRuleFactory(this.rulesConfig);\n" +
				"    this.ruleEvaluator = initRuleEvaluator(this.rulesConfig);\n" +
				"  }\n" +
				"\n" +
				"  public RulesEngine(File configFile)\n" +
				"  {\n" +
				"    this.rulesConfig = RulesConfigInitializer.initRulesConfig(configFile);\n" +
				"    this.ruleFactory = initRuleFactory(this.rulesConfig);\n" +
				"    this.ruleEvaluator = initRuleEvaluator(this.rulesConfig);\n" +
				"  }\n" +
				"\n" +
				"  public RulesEngine(InputStream configInputStream)\n" +
				"  {\n" +
				"    this.rulesConfig = RulesConfigInitializer.initRulesConfig(configInputStream);\n" +
				"    this.ruleFactory = initRuleFactory(this.rulesConfig);\n" +
				"    this.ruleEvaluator = initRuleEvaluator(this.rulesConfig);\n" +
				"  }\n" +
				"\n" +
				"  public RulesEngine(InputSource configInputSource)\n" +
				"  {\n" +
				"    this.rulesConfig = RulesConfigInitializer.initRulesConfig(configInputSource);\n" +
				"    this.ruleFactory = initRuleFactory(this.rulesConfig);\n" +
				"    this.ruleEvaluator = initRuleEvaluator(this.rulesConfig);\n" +
				"  }\n" +
				"\n" +
				"  public RulesEngine(Document configDcument)\n" +
				"  {\n" +
				"    this.rulesConfig = RulesConfigInitializer.initRulesConfig(configDcument);\n" +
				"    this.ruleFactory = initRuleFactory(this.rulesConfig);\n" +
				"    this.ruleEvaluator = initRuleEvaluator(this.rulesConfig);\n" +
				"  }\n" +
				"\n" +
				"  public RulesEngine(Node configNode)\n" +
				"  {\n" +
				"    this.rulesConfig = RulesConfigInitializer.initRulesConfig(configNode);\n" +
				"    this.ruleFactory = initRuleFactory(this.rulesConfig);\n" +
				"    this.ruleEvaluator = initRuleEvaluator(this.rulesConfig);\n" +
				"  }\n" +
				"\n" +
				"  /**\n" +
				"   * @param ruleName The name attribute of a rule-def or rule-impl element in\n" +
				"   * the configuration file.\n" +
				"   * @param ruleArgs Arguments to evaluate.\n" +
				"   * @return Whether the rule passes.\n" +
				"   */\n" +
				"  public boolean passesRule(String ruleName, RuleArgs ruleArgs)\n" +
				"  {\n" +
				"    boolean passesRule = true;\n" +
				"\n" +
				"    // Get the ruleDef and ensure it is not null\n" +
				"    RuleDefElmt ruleDef =\n" +
				"      rulesConfig.getRuleDefinitionElmts().getRuleDefElmts().get(ruleName);\n" +
				"    RUtil.assertNotNull(ruleDef, \"There is no rule definition for \" + ruleName);\n" +
				"\n" +
				"    // Get all the RuleElmts of the ruleDef and make sure they aren't null or\n" +
				"    // empty\n" +
				"    List<RuleElmt> ruleElmts = ruleDef.getRuleElmts();\n" +
				"    RUtil.assertNotNullOrEmpty(ruleElmts, \"There are no rule elements for \"\n" +
				"      + ruleName);\n" +
				"\n" +
				"    // List of rule elmts to evaluate \n" +
				"    List<RuleElmt> ruleElmtsToEvaluate = new LinkedList<RuleElmt>();\n" +
				"\n" +
				"    // For each RuleElmt...\n" +
				"    for (RuleElmt ruleElmt : ruleElmts)\n" +
				"    {\n" +
				"      // Add it to ruleElmtsToEvaluate\n" +
				"      ruleElmtsToEvaluate.add(ruleElmt);\n" +
				"\n" +
				"      // If it is to be OR'd with the next one, continue\n" +
				"      if (ruleElmt.isOrNextRule())\n" +
				"      {\n" +
				"        continue;\n" +
				"      }\n" +
				"\n" +
				"      // If we didn't continue, we are going to evaluate all ruleElmtsToEvaluate\n" +
				"      boolean passesTheseRules =\n" +
				"        evaluateORedRules(ruleElmtsToEvaluate, ruleArgs);\n" +
				"      ruleElmtsToEvaluate.clear(); // clear the list of rules to evaluate\n" +
				"\n" +
				"      passesRule = passesRule & passesTheseRules;\n" +
				"\n" +
				"      // If the rule failed already, there is no reason to keep evaluating\n" +
				"      // so break the loop.\n" +
				"      if (false == passesRule)\n" +
				"      {\n" +
				"        break;\n" +
				"      }\n" +
				"    }\n" +
				"\n" +
				"    // Someone might have set isOrNextRule on the last rule in a rule def,\n" +
				"    // so evaluate any rules still in ruleElmtsToEvaluate if present.\n" +
				"    if (!ruleElmtsToEvaluate.isEmpty())\n" +
				"    {\n" +
				"      final boolean passesTheseRules = evaluateORedRules(ruleElmtsToEvaluate, ruleArgs);\n" +
				"      passesRule = passesRule & passesTheseRules;\n" +
				"    }\n" +
				"\n" +
				"    return passesRule;\n" +
				"  }\n" +
				"\n" +
				"  protected boolean evaluateORedRules(List<RuleElmt> ruleElmtsToEvaluate,\n" +
				"    RuleArgs ruleArgs)\n" +
				"  {\n" +
				"    RUtil.assertNotNullOrEmpty(ruleElmtsToEvaluate,\n" +
				"      \"There are no RuleElmt's to evaluate\");\n" +
				"\n" +
				"    boolean passesRule = false;\n" +
				"    for (RuleElmt ruleElmt : ruleElmtsToEvaluate)\n" +
				"    {\n" +
				"      final boolean passesThisRule = evaluateRule(ruleArgs, ruleElmt);\n" +
				"      passesRule = passesRule | passesThisRule;\n" +
				"      if (passesRule)\n" +
				"      {\n" +
				"        break;\n" +
				"      }\n" +
				"    }\n" +
				"    return passesRule;\n" +
				"  }\n" +
				"\n" +
				"  protected boolean evaluateRule(RuleArgs ruleArgs, RuleElmt ruleElmt)\n" +
				"  {\n" +
				"    // Get the RuleImplElmt\n" +
				"    RuleImplElmt ruleImplElmt = ruleElmt.getRuleImplElmtRef();\n" +
				"\n" +
				"    // Get the rule\n" +
				"    Rule rule = ruleFactory.getRule(ruleImplElmt);\n" +
				"\n" +
				"    // Evaluate rule\n" +
				"    boolean evaluation = ruleEvaluator.passesRule(rule, ruleArgs);\n" +
				"\n" +
				"    // If isInverse, invert the evaluation\n" +
				"    if (ruleElmt.isInverse())\n" +
				"    {\n" +
				"      evaluation = !evaluation;\n" +
				"    }\n" +
				"\n" +
				"    return evaluation;\n" +
				"  }\n" +
				"\n" +
				"  protected RuleFactory initRuleFactory(RulesConfig rulesConfig)\n" +
				"  {\n" +
				"    final String ruleFactoryClass =\n" +
				"      rulesConfig.getRoolieConfigElmt().getRuleFactoryClass();\n" +
				"    RuleFactory _ruleFactory =\n" +
				"      ruleFactoryFactory.cachedInstance(ruleFactoryClass);\n" +
				"    return _ruleFactory;\n" +
				"  }\n" +
				"\n" +
				"  protected RuleEvaluator initRuleEvaluator(RulesConfig rulesConfig)\n" +
				"  {\n" +
				"    final String ruleEvaluatorClass =\n" +
				"      rulesConfig.getRoolieConfigElmt().getRuleEvaluatorClass();\n" +
				"    RuleEvaluator _ruleEvaluator =\n" +
				"      ruleEvaluatorFactory.cachedInstance(ruleEvaluatorClass);\n" +
				"    return _ruleEvaluator;\n" +
				"  }\n" +
				"}\n" +
				"\n" +
				"class RulesConfigInitializer\n" +
				"{\n" +
				"\n" +
				"  protected static RulesConfig initRulesConfig(String uri)\n" +
				"  {\n" +
				"    try\n" +
				"    {\n" +
				"      return RulesConfigFactory.getInstance().buildRulesConfig(uri);\n" +
				"    }\n" +
				"    catch (Throwable t)\n" +
				"    {\n" +
				"      throw new RuntimeException(t);\n" +
				"    }\n" +
				"  }\n" +
				"\n" +
				"  protected static RulesConfig initRulesConfig(File configFile)\n" +
				"  {\n" +
				"    try\n" +
				"    {\n" +
				"      return RulesConfigFactory.getInstance().buildRulesConfig(configFile);\n" +
				"    }\n" +
				"    catch (Throwable t)\n" +
				"    {\n" +
				"      throw new RuntimeException(t);\n" +
				"    }\n" +
				"  }\n" +
				"\n" +
				"  protected static RulesConfig initRulesConfig(InputStream inputStream)\n" +
				"  {\n" +
				"    try\n" +
				"    {\n" +
				"      return RulesConfigFactory.getInstance().buildRulesConfig(inputStream);\n" +
				"    }\n" +
				"    catch (Throwable t)\n" +
				"    {\n" +
				"      throw new RuntimeException(t);\n" +
				"    }\n" +
				"  }\n" +
				"\n" +
				"  protected static RulesConfig initRulesConfig(InputSource inputSource)\n" +
				"  {\n" +
				"    try\n" +
				"    {\n" +
				"      return RulesConfigFactory.getInstance().buildRulesConfig(inputSource);\n" +
				"    }\n" +
				"    catch (Throwable t)\n" +
				"    {\n" +
				"      throw new RuntimeException(t);\n" +
				"    }\n" +
				"  }\n" +
				"\n" +
				"  protected static RulesConfig initRulesConfig(Node node)\n" +
				"  {\n" +
				"    try\n" +
				"    {\n" +
				"      return RulesConfigFactory.getInstance().buildRulesConfig(node);\n" +
				"    }\n" +
				"    catch (Throwable t)\n" +
				"    {\n" +
				"      throw new RuntimeException(t);\n" +
				"    }\n" +
				"  }\n" +
				"}\n";
		final String newContent = "///////////////////////////////////////////////////////////////////////////////\n" +
				"//  Copyright 2010 Ryan Kennedy <rallyredevo AT users DOT sourceforge DOT net>\n" +
				"//\n" +
				"//  This file is part of Roolie.\n" +
				"//\n" +
				"//  Roolie is free software: you can redistribute it and/or modify\n" +
				"//  it under the terms of the GNU Lesser General Public License as published by\n" +
				"//  the Free Software Foundation, either version 3 of the License, or any later\n" +
				"//  version.\n" +
				"//\n" +
				"//  Roolie is distributed in the hope that it will be useful,\n" +
				"//  but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
				"//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
				"//  GNU Lesser General Public License for more details.\n" +
				"//\n" +
				"//  You should have received a copy of the GNU Lesser General Public License\n" +
				"//  along with Roolie.  If not, see <http://www.gnu.org/licenses/>.\n" +
				"///////////////////////////////////////////////////////////////////////////////\n" +
				"package org.roolie;\n" +
				"\n" +
				"import java.io.File;\n" +
				"import java.io.InputStream;\n" +
				"import java.util.LinkedList;\n" +
				"import java.util.List;\n" +
				"import org.roolie.config.RulesConfig;\n" +
				"import org.roolie.config.elmt.RuleDefElmt;\n" +
				"import org.roolie.config.elmt.RuleElmt;\n" +
				"import org.roolie.config.elmt.RuleImplElmt;\n" +
				"import org.roolie.factory.InstanceFactory;\n" +
				"import org.roolie.factory.RulesConfigFactory;\n" +
				"import org.roolie.util.RUtil;\n" +
				"import org.w3c.dom.Document;\n" +
				"import org.w3c.dom.Node;\n" +
				"import org.xml.sax.InputSource;\n" +
				"\n" +
				"public class RulesEngine\n" +
				"{\n" +
				"\n" +
				"  protected final RulesConfig rulesConfig;\n" +
				"\n" +
				"  protected final RuleFactory ruleFactory;\n" +
				"\n" +
				"  protected final RuleEvaluator ruleEvaluator;\n" +
				"\n" +
				"  protected static final InstanceFactory<RuleFactory> ruleFactoryFactory =\n" +
				"    new InstanceFactory<RuleFactory>();\n" +
				"\n" +
				"  protected static final InstanceFactory<RuleEvaluator> ruleEvaluatorFactory =\n" +
				"    new InstanceFactory<RuleEvaluator>();\n" +
				"\n" +
				"  public RulesEngine(String configURI)\n" +
				"  {\n" +
				"    this.rulesConfig = RulesConfigInitializer.initRulesConfig(configURI);\n" +
				"    this.ruleFactory = initRuleFactory(this.rulesConfig);\n" +
				"    this.ruleEvaluator = initRuleEvaluator(this.rulesConfig);\n" +
				"  }\n" +
				"\n" +
				"  public RulesEngine(File configFile)\n" +
				"  {\n" +
				"    this.rulesConfig = RulesConfigInitializer.initRulesConfig(configFile);\n" +
				"    this.ruleFactory = initRuleFactory(this.rulesConfig);\n" +
				"    this.ruleEvaluator = initRuleEvaluator(this.rulesConfig);\n" +
				"  }\n" +
				"\n" +
				"  public RulesEngine(InputStream configInputStream)\n" +
				"  {\n" +
				"    this.rulesConfig = RulesConfigInitializer.initRulesConfig(configInputStream);\n" +
				"    this.ruleFactory = initRuleFactory(this.rulesConfig);\n" +
				"    this.ruleEvaluator = initRuleEvaluator(this.rulesConfig);\n" +
				"  }\n" +
				"\n" +
				"  public RulesEngine(InputSource configInputSource)\n" +
				"  {\n" +
				"    this.rulesConfig = RulesConfigInitializer.initRulesConfig(configInputSource);\n" +
				"    this.ruleFactory = initRuleFactory(this.rulesConfig);\n" +
				"    this.ruleEvaluator = initRuleEvaluator(this.rulesConfig);\n" +
				"  }\n" +
				"\n" +
				"  public RulesEngine(Document configDcument)\n" +
				"  {\n" +
				"    this.rulesConfig = RulesConfigInitializer.initRulesConfig(configDcument);\n" +
				"    this.ruleFactory = initRuleFactory(this.rulesConfig);\n" +
				"    this.ruleEvaluator = initRuleEvaluator(this.rulesConfig);\n" +
				"  }\n" +
				"\n" +
				"  public RulesEngine(Node configNode)\n" +
				"  {\n" +
				"    this.rulesConfig = RulesConfigInitializer.initRulesConfig(configNode);\n" +
				"    this.ruleFactory = initRuleFactory(this.rulesConfig);\n" +
				"    this.ruleEvaluator = initRuleEvaluator(this.rulesConfig);\n" +
				"  }\n" +
				"\n" +
				"  /**\n" +
				"   * @param ruleName The name attribute of a rule-def or rule-impl element in\n" +
				"   * the configuration file.\n" +
				"   * @param ruleArgs Arguments to evaluate.\n" +
				"   * @return Whether the rule passes.\n" +
				"   */\n" +
				"  public boolean passesRule(String ruleName, RuleArgs ruleArgs)\n" +
				"  {\n" +
				"    boolean passesRule = true;\n" +
				"\n" +
				"    // Get the ruleDef and ensure it is not null\n" +
				"    RuleDefElmt ruleDef =\n" +
				"      rulesConfig.getRuleDefinitionElmts().getRuleDefElmts().get(ruleName);\n" +
				"    RUtil.assertNotNull(ruleDef, \"There is no rule definition for \" + ruleName);\n" +
				"\n" +
				"    // Get all the RuleElmts of the ruleDef and make sure they aren't null or\n" +
				"    // empty\n" +
				"    List<RuleElmt> ruleElmts = ruleDef.getRuleElmts();\n" +
				"    RUtil.assertNotNullOrEmpty(ruleElmts, \"There are no rule elements for \"\n" +
				"      + ruleName);\n" +
				"\n" +
				"    // List of rule elmts to evaluate \n" +
				"    List<RuleElmt> ruleElmtsToEvaluate = new LinkedList<RuleElmt>();\n" +
				"\n" +
				"    // For each RuleElmt...\n" +
				"    for (RuleElmt ruleElmt : ruleElmts)\n" +
				"    {\n" +
				"      // Add it to ruleElmtsToEvaluate\n" +
				"      ruleElmtsToEvaluate.add(ruleElmt);\n" +
				"\n" +
				"      // If it is to be OR'd with the next one, continue\n" +
				"      if (ruleElmt.isOrNextRule())\n" +
				"      {\n" +
				"        continue;\n" +
				"      }\n" +
				"\n" +
				"      // If we didn't continue, we are going to evaluate all ruleElmtsToEvaluate\n" +
				"      boolean passesTheseRules =\n" +
				"        evaluateORedRules(ruleElmtsToEvaluate, ruleArgs);\n" +
				"      ruleElmtsToEvaluate.clear(); // clear the list of rules to evaluate\n" +
				"\n" +
				"      passesRule = passesRule & passesTheseRules;\n" +
				"\n" +
				"      // If the rule failed already, there is no reason to keep evaluating\n" +
				"      // so break the loop.\n" +
				"      if (false == passesRule)\n" +
				"      {\n" +
				"        break;\n" +
				"      }\n" +
				"    }\n" +
				"\n" +
				"    // Someone might have set isOrNextRule on the last rule in a rule def,\n" +
				"    // so evaluate any rules still in ruleElmtsToEvaluate if present.\n" +
				"    if (!ruleElmtsToEvaluate.isEmpty())\n" +
				"    {\n" +
				"      final boolean passesTheseRules = evaluateORedRules(ruleElmtsToEvaluate, ruleArgs);\n" +
				"      passesRule = passesRule & passesTheseRules;\n" +
				"    }\n" +
				"\n" +
				"    return passesRule;\n" +
				"  }\n" +
				"\n" +
				"  protected boolean evaluateORedRules(List<RuleElmt> ruleElmtsToEvaluate,\n" +
				"    RuleArgs ruleArgs)\n" +
				"  {\n" +
				"    RUtil.assertNotNullOrEmpty(ruleElmtsToEvaluate,\n" +
				"      \"There are no RuleElmt's to evaluate\");\n" +
				"\n" +
				"    boolean passesRule = false;\n" +
				"    for (RuleElmt ruleElmt : ruleElmtsToEvaluate)\n" +
				"    {\n" +
				"      boolean passesThisRule = evaluateRule(ruleArgs, ruleElmt);\n" +
				"      if (ruleElmt.isInverse())\n" +
				"      {\n" +
				"        passesThisRule = !passesThisRule;\n" +
				"      }\n" +
				"      passesRule = passesRule | passesThisRule;\n" +
				"      if (passesRule)\n" +
				"      {\n" +
				"        break;\n" +
				"      }\n" +
				"    }\n" +
				"    return passesRule;\n" +
				"  }\n" +
				"\n" +
				"  protected boolean evaluateRule(RuleArgs ruleArgs, RuleElmt ruleElmt)\n" +
				"  {\n" +
				"    // Get the RuleImplElmt\n" +
				"    RuleImplElmt ruleImplElmt = ruleElmt.getRuleImplElmtRef();\n" +
				"\n" +
				"    // Get the rule\n" +
				"    Rule rule = ruleFactory.getRule(ruleImplElmt);\n" +
				"\n" +
				"    // Evaluate rule\n" +
				"    boolean evaluation = ruleEvaluator.passesRule(rule, ruleArgs);\n" +
				"\n" +
				"    // If isInverse, invert the evaluation\n" +
				"    if (ruleElmt.isInverse())\n" +
				"    {\n" +
				"      evaluation = !evaluation;\n" +
				"    }\n" +
				"\n" +
				"    return evaluation;\n" +
				"  }\n" +
				"\n" +
				"  protected RuleFactory initRuleFactory(RulesConfig rulesConfig)\n" +
				"  {\n" +
				"    final String ruleFactoryClass =\n" +
				"      rulesConfig.getRoolieConfigElmt().getRuleFactoryClass();\n" +
				"    RuleFactory _ruleFactory =\n" +
				"      ruleFactoryFactory.cachedInstance(ruleFactoryClass);\n" +
				"    return _ruleFactory;\n" +
				"  }\n" +
				"\n" +
				"  protected RuleEvaluator initRuleEvaluator(RulesConfig rulesConfig)\n" +
				"  {\n" +
				"    final String ruleEvaluatorClass =\n" +
				"      rulesConfig.getRoolieConfigElmt().getRuleEvaluatorClass();\n" +
				"    RuleEvaluator _ruleEvaluator =\n" +
				"      ruleEvaluatorFactory.cachedInstance(ruleEvaluatorClass);\n" +
				"    return _ruleEvaluator;\n" +
				"  }\n" +
				"}\n" +
				"\n" +
				"class RulesConfigInitializer\n" +
				"{\n" +
				"\n" +
				"  protected static RulesConfig initRulesConfig(String uri)\n" +
				"  {\n" +
				"    try\n" +
				"    {\n" +
				"      return RulesConfigFactory.getInstance().buildRulesConfig(uri);\n" +
				"    }\n" +
				"    catch (Throwable t)\n" +
				"    {\n" +
				"      throw new RuntimeException(t);\n" +
				"    }\n" +
				"  }\n" +
				"\n" +
				"  protected static RulesConfig initRulesConfig(File configFile)\n" +
				"  {\n" +
				"    try\n" +
				"    {\n" +
				"      return RulesConfigFactory.getInstance().buildRulesConfig(configFile);\n" +
				"    }\n" +
				"    catch (Throwable t)\n" +
				"    {\n" +
				"      throw new RuntimeException(t);\n" +
				"    }\n" +
				"  }\n" +
				"\n" +
				"  protected static RulesConfig initRulesConfig(InputStream inputStream)\n" +
				"  {\n" +
				"    try\n" +
				"    {\n" +
				"      return RulesConfigFactory.getInstance().buildRulesConfig(inputStream);\n" +
				"    }\n" +
				"    catch (Throwable t)\n" +
				"    {\n" +
				"      throw new RuntimeException(t);\n" +
				"    }\n" +
				"  }\n" +
				"\n" +
				"  protected static RulesConfig initRulesConfig(InputSource inputSource)\n" +
				"  {\n" +
				"    try\n" +
				"    {\n" +
				"      return RulesConfigFactory.getInstance().buildRulesConfig(inputSource);\n" +
				"    }\n" +
				"    catch (Throwable t)\n" +
				"    {\n" +
				"      throw new RuntimeException(t);\n" +
				"    }\n" +
				"  }\n" +
				"\n" +
				"  protected static RulesConfig initRulesConfig(Node node)\n" +
				"  {\n" +
				"    try\n" +
				"    {\n" +
				"      return RulesConfigFactory.getInstance().buildRulesConfig(node);\n" +
				"    }\n" +
				"    catch (Throwable t)\n" +
				"    {\n" +
				"      throw new RuntimeException(t);\n" +
				"    }\n" +
				"  }\n" +
				"}\n";
		assertNotEquals(oldContent, newContent);
		assertEquals(oldContent, new String(changes.get(0)
				.getOldFile().get().readAllBytes(), Charset.forName("UTF-8")));
		assertEquals(newContent, new String(changes.get(0)
				.getNewFile().get().readAllBytes(), Charset.forName("UTF-8")));
	}

	@Test
	public void commitMessage41() throws IOException {
		final VCSEngine engine = createProvider("", "40", "42");
		engine.next();
		final Optional<Version> optional = engine.next();
		assertTrue(optional.isPresent());
		assertEquals("", optional.get().getCommits().get(0).getMessage());
	}

	@Test
	public void changes41() throws IOException {
		final VCSEngine engine = createProvider("", "40", "42");
		engine.next();
		final Optional<Version> optional = engine.next();
		assertTrue(optional.isPresent());
		final Version version = optional.get();
		final List<FileChange> changes = version.getFileChanges();
		assertEquals(46, changes.size());
		for (final FileChange fc : changes) {
			assertEquals(FileChange.Type.ADD, fc.getType());
			assertTrue(Files.isRegularFile(
					Paths.get(fc.getNewFile().get().getPath())));
		}
	}

	@Test
	public void commitMessage61() throws IOException {
		final VCSEngine engine = createProvider("", "60", "62");
		engine.next();
		final Optional<Version> optional = engine.next();
		assertTrue(optional.isPresent());
		assertEquals("v1.1 - 12/12/2013\n" +
			"* Added support for making rule-defs out of other rule-defs.\n" +
			"* Removed old license information from files.\n" +
			"* Added more unit tests.\n" +
			"* Removed dependency on any parent pom.\n" +
			"* Removed PGP key generation from build.\n",
			optional.get().getCommits().get(0).getMessage());
	}

	///////////////////////// Revision interval tests /////////////////////////

	private SVNEngine createProvider(
			final String pRoot, final int pFrom, final int pTo) {
		return new SVNEngine(
				"file://" + input.toString(),
				pRoot,
				output,
				String.valueOf(pFrom),
				String.valueOf(pTo));
	}

	@Test
	public void listRevisionsEmptyRootWithRevisionInterval()
			throws IOException {
		final SVNEngine engine = createProvider("", 1, 64);
		final List<String> revisions = engine.listRevisions();
		assertEquals(64, revisions.size());
		for (int i = 0; i < revisions.size(); i++) {
			assertEquals(String.valueOf(i+1), revisions.get(i));
		}
	}

	@Test
	public void listRevisionsRoolieCoreRootWithRevisionInterval()
			throws IOException {
		final SVNEngine engine = createProvider("roolie-core", 40, 62);
		final List<String> revisions = engine.listRevisions();
		assertEquals(11, revisions.size());
		assertEquals(String.valueOf("40"), revisions.get(0));
		assertEquals(String.valueOf("41"), revisions.get(1));
		assertEquals(String.valueOf("51"), revisions.get(2));
		assertEquals(String.valueOf("53"), revisions.get(3));
		assertEquals(String.valueOf("55"), revisions.get(5));
		assertEquals(String.valueOf("56"), revisions.get(6));
		assertEquals(String.valueOf("57"), revisions.get(7));
		assertEquals(String.valueOf("58"), revisions.get(8));
		assertEquals(String.valueOf("61"), revisions.get(9));
		assertEquals(String.valueOf("62"), revisions.get(10));
	}

	@Test
	public void listRevisionsComponentWithRevisionInterval()
			throws IOException {
		final SVNEngine engine = createProvider(
				"roolie-core/src/main/java/net/sf/roolie/core/util/component/",
				41, 62);
		final List<String> revisions = engine.listRevisions();
		assertEquals(3, revisions.size());
		assertEquals(String.valueOf("41"), revisions.get(0));
		assertEquals(String.valueOf("61"), revisions.get(1));
		assertEquals(String.valueOf("62"), revisions.get(2));
	}
}
