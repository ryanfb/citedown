package edu.harvard.chs.citedownutils


import edu.harvard.chs.citedown.ast.RootNode

import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions

import static org.junit.Assert.*
import org.junit.Test

class TestTreePrint {

  String markdownText = "#Heading 1#\n\nText paragraph 1.\n"
  
  @Test void testConstructor() {
    PegDownProcessor pdp = new PegDownProcessor(Extensions.CITE)
    RootNode root = pdp.parser.parse(markdownText.toCharArray())
    System.err.println "Root is a " + root.getClass()
    //    TreeUtil tu = new TreeUtil(root)
    ///    System.err.println tu.printTree()
  }
  
}
