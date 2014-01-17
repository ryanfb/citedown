package edu.harvard.chs.citedownutils

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

  /** Root node of pegdown parsing result. */
  RootNode root
  


  /** Constructor requiring root node. */
  TreeUtil(RootNode rootNode) {
    this.root = rootNode
  }
  

  /** Prints ASCII-formatted tree layout to standard out. */
  void printTree() {
    printTree(root,0)
  }




  /** Prints ASCII-formatted tree layout to standard out. 
   * @param n Node to descend from.
   * @param indents Indentation level of current node.
   */
  void printTree(Object n, Integer indents) {
    indents++;
    n.getChildren().each { c ->
      for (i in 0..indents) {
	print "\t"
      }
      print "${indents}. "
      String txt =  c.getClass().name.replaceFirst("edu.harvard.chs.citedown.ast.","")
      if (c instanceof edu.harvard.chs.citedown.ast.TextNode) {
	txt = ': "' + c.getText() + '"'
      }
      println txt
      descendTree(c, indents)
    }
  
  }

}
