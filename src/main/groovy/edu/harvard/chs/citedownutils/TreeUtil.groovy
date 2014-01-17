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
  

  /** Prints ASCII-formatted tree layout to a String.
   * @returns A pretty-printed representation of the parse tree
   * stripped down to class names and text of TextNodes.
   */
  String printSimpleTree() {
    return printSimpleTree(root,new StringBuffer(), 0)
  }




  /** Prints ASCII-formatted tree layout to standard out. 
   * @param n Node to descend from.
   * @param indents Indentation level of current node.
   * @returns A pretty-printed representation of the parse tree
   * stripped down to class names and text of TextNodes.
   */
  String printSimpleTree(Object n, StringBuffer buffer, Integer indents) {
    n.getChildren().each { c ->
      for (i in 0..indents) {
	buffer.append("\t")
      }
      indents++;
      buffer.append( "${indents}. ")
      String txt =  c.getClass().name.replaceFirst("edu.harvard.chs.citedown.ast.","")
      if (c instanceof edu.harvard.chs.citedown.ast.TextNode) {
	txt = ': "' + c.getText() + '"'
      }
      buffer.append("${txt}\n")
      printSimpleTree(c, buffer, indents)
    }
    return buffer.toString()
  }


  void showReff() {
    System.err.println "reff gets you " + this.root.getReferences().getClass()    
  }

}
