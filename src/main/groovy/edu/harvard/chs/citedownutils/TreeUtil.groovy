package edu.harvard.chs.citedownutil

import edu.harvard.chs.citedown.ast.RootNode


import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.Rule;
import org.parboiled.annotations.*;
import org.parboiled.common.ArrayBuilder;
import org.parboiled.common.ImmutableList;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.StringBuilderVar;
import org.parboiled.support.StringVar;
import org.parboiled.support.Var;

import edu.harvard.chs.citedown.ast.*;
import edu.harvard.chs.citedown.ast.SimpleNode.Type;

import static org.parboiled.errors.ErrorUtils.printParseErrors;
import static org.parboiled.common.StringUtils.repeat;

/** Utilities for working with a parboiled parse tree as a ParsingResult object.
 */
class TreeUtil {

  RootNode root
  

  TreeUtil(RootNode rootNode) {
    this.root = rootNode
  }
}
