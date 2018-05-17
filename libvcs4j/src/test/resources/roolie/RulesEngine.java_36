///////////////////////////////////////////////////////////////////////////////
//  Copyright 2010 Ryan Kennedy <rallyredevo AT users DOT sourceforge DOT net>
//
//  This file is part of Roolie.
//
//  Roolie is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or any later
//  version.
//
//  Roolie is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with Roolie.  If not, see <http://www.gnu.org/licenses/>.
///////////////////////////////////////////////////////////////////////////////
package org.roolie;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import org.roolie.config.RulesConfig;
import org.roolie.config.elmt.RuleDefElmt;
import org.roolie.config.elmt.RuleElmt;
import org.roolie.config.elmt.RuleImplElmt;
import org.roolie.factory.InstanceFactory;
import org.roolie.factory.RulesConfigFactory;
import org.roolie.util.RUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class RulesEngine
{

  protected final RulesConfig rulesConfig;

  protected final RuleFactory ruleFactory;

  protected final RuleEvaluator ruleEvaluator;

  protected static final InstanceFactory<RuleFactory> ruleFactoryFactory =
    new InstanceFactory<RuleFactory>();

  protected static final InstanceFactory<RuleEvaluator> ruleEvaluatorFactory =
    new InstanceFactory<RuleEvaluator>();

  public RulesEngine(String configURI)
  {
    this.rulesConfig = RulesConfigInitializer.initRulesConfig(configURI);
    this.ruleFactory = initRuleFactory(this.rulesConfig);
    this.ruleEvaluator = initRuleEvaluator(this.rulesConfig);
  }

  public RulesEngine(File configFile)
  {
    this.rulesConfig = RulesConfigInitializer.initRulesConfig(configFile);
    this.ruleFactory = initRuleFactory(this.rulesConfig);
    this.ruleEvaluator = initRuleEvaluator(this.rulesConfig);
  }

  public RulesEngine(InputStream configInputStream)
  {
    this.rulesConfig = RulesConfigInitializer.initRulesConfig(configInputStream);
    this.ruleFactory = initRuleFactory(this.rulesConfig);
    this.ruleEvaluator = initRuleEvaluator(this.rulesConfig);
  }

  public RulesEngine(InputSource configInputSource)
  {
    this.rulesConfig = RulesConfigInitializer.initRulesConfig(configInputSource);
    this.ruleFactory = initRuleFactory(this.rulesConfig);
    this.ruleEvaluator = initRuleEvaluator(this.rulesConfig);
  }

  public RulesEngine(Document configDcument)
  {
    this.rulesConfig = RulesConfigInitializer.initRulesConfig(configDcument);
    this.ruleFactory = initRuleFactory(this.rulesConfig);
    this.ruleEvaluator = initRuleEvaluator(this.rulesConfig);
  }

  public RulesEngine(Node configNode)
  {
    this.rulesConfig = RulesConfigInitializer.initRulesConfig(configNode);
    this.ruleFactory = initRuleFactory(this.rulesConfig);
    this.ruleEvaluator = initRuleEvaluator(this.rulesConfig);
  }

  /**
   * @param ruleName The name attribute of a rule-def or rule-impl element in
   * the configuration file.
   * @param ruleArgs Arguments to evaluate.
   * @return Whether the rule passes.
   */
  public boolean passesRule(String ruleName, RuleArgs ruleArgs)
  {
    boolean passesRule = true;

    // Get the ruleDef and ensure it is not null
    RuleDefElmt ruleDef =
      rulesConfig.getRuleDefinitionElmts().getRuleDefElmts().get(ruleName);
    RUtil.assertNotNull(ruleDef, "There is no rule definition for " + ruleName);

    // Get all the RuleElmts of the ruleDef and make sure they aren't null or
    // empty
    List<RuleElmt> ruleElmts = ruleDef.getRuleElmts();
    RUtil.assertNotNullOrEmpty(ruleElmts, "There are no rule elements for "
      + ruleName);

    // List of rule elmts to evaluate 
    List<RuleElmt> ruleElmtsToEvaluate = new LinkedList<RuleElmt>();

    // For each RuleElmt...
    for (RuleElmt ruleElmt : ruleElmts)
    {
      // Add it to ruleElmtsToEvaluate
      ruleElmtsToEvaluate.add(ruleElmt);

      // If it is to be OR'd with the next one, continue
      if (ruleElmt.isOrNextRule())
      {
        continue;
      }

      // If we didn't continue, we are going to evaluate all ruleElmtsToEvaluate
      boolean passesTheseRules =
        evaluateORedRules(ruleElmtsToEvaluate, ruleArgs);
      ruleElmtsToEvaluate.clear(); // clear the list of rules to evaluate

      passesRule = passesRule & passesTheseRules;

      // If the rule failed already, there is no reason to keep evaluating
      // so break the loop.
      if (false == passesRule)
      {
        break;
      }
    }

    // Someone might have set isOrNextRule on the last rule in a rule def,
    // so evaluate any rules still in ruleElmtsToEvaluate if present.
    if (!ruleElmtsToEvaluate.isEmpty())
    {
      final boolean passesTheseRules = evaluateORedRules(ruleElmtsToEvaluate, ruleArgs);
      passesRule = passesRule & passesTheseRules;
    }

    return passesRule;
  }

  protected boolean evaluateORedRules(List<RuleElmt> ruleElmtsToEvaluate,
    RuleArgs ruleArgs)
  {
    RUtil.assertNotNullOrEmpty(ruleElmtsToEvaluate,
      "There are no RuleElmt's to evaluate");

    boolean passesRule = false;
    for (RuleElmt ruleElmt : ruleElmtsToEvaluate)
    {
      boolean passesThisRule = evaluateRule(ruleArgs, ruleElmt);
      if (ruleElmt.isInverse())
      {
        passesThisRule = !passesThisRule;
      }
      passesRule = passesRule | passesThisRule;
      if (passesRule)
      {
        break;
      }
    }
    return passesRule;
  }

  protected boolean evaluateRule(RuleArgs ruleArgs, RuleElmt ruleElmt)
  {
    // Get the RuleImplElmt
    RuleImplElmt ruleImplElmt = ruleElmt.getRuleImplElmtRef();

    // Get the rule
    Rule rule = ruleFactory.getRule(ruleImplElmt);

    // Evaluate rule
    boolean evaluation = ruleEvaluator.passesRule(rule, ruleArgs);

    // If isInverse, invert the evaluation
    if (ruleElmt.isInverse())
    {
      evaluation = !evaluation;
    }

    return evaluation;
  }

  protected RuleFactory initRuleFactory(RulesConfig rulesConfig)
  {
    final String ruleFactoryClass =
      rulesConfig.getRoolieConfigElmt().getRuleFactoryClass();
    RuleFactory _ruleFactory =
      ruleFactoryFactory.cachedInstance(ruleFactoryClass);
    return _ruleFactory;
  }

  protected RuleEvaluator initRuleEvaluator(RulesConfig rulesConfig)
  {
    final String ruleEvaluatorClass =
      rulesConfig.getRoolieConfigElmt().getRuleEvaluatorClass();
    RuleEvaluator _ruleEvaluator =
      ruleEvaluatorFactory.cachedInstance(ruleEvaluatorClass);
    return _ruleEvaluator;
  }
}

class RulesConfigInitializer
{

  protected static RulesConfig initRulesConfig(String uri)
  {
    try
    {
      return RulesConfigFactory.getInstance().buildRulesConfig(uri);
    }
    catch (Throwable t)
    {
      throw new RuntimeException(t);
    }
  }

  protected static RulesConfig initRulesConfig(File configFile)
  {
    try
    {
      return RulesConfigFactory.getInstance().buildRulesConfig(configFile);
    }
    catch (Throwable t)
    {
      throw new RuntimeException(t);
    }
  }

  protected static RulesConfig initRulesConfig(InputStream inputStream)
  {
    try
    {
      return RulesConfigFactory.getInstance().buildRulesConfig(inputStream);
    }
    catch (Throwable t)
    {
      throw new RuntimeException(t);
    }
  }

  protected static RulesConfig initRulesConfig(InputSource inputSource)
  {
    try
    {
      return RulesConfigFactory.getInstance().buildRulesConfig(inputSource);
    }
    catch (Throwable t)
    {
      throw new RuntimeException(t);
    }
  }

  protected static RulesConfig initRulesConfig(Node node)
  {
    try
    {
      return RulesConfigFactory.getInstance().buildRulesConfig(node);
    }
    catch (Throwable t)
    {
      throw new RuntimeException(t);
    }
  }
}
