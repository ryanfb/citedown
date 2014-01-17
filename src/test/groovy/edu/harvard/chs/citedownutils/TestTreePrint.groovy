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
    TreeUtil tu = new TreeUtil(root)
    String printed = tu.printSimpleTree()

    Integer expectedSize = 4
    def lines =  printed.readLines()
    assert lines.size() == expectedSize
    
    String expectedStart = "\t1. HeaderNode"
    assert lines[0] == expectedStart
  }
  
}
