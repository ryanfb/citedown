package edu.harvard.chs.citedownutils


import edu.harvard.chs.citedown.ast.RootNode

import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions

import static org.junit.Assert.*
import org.junit.Test

class TestMdConstructor {

  String markdownText = "#Heading 1#\n\nText paragraph 1.\n"
  
  @Test void testConstructor() {
    MarkdownUtil mdu = new MarkdownUtil(markdownText)
    assert mdu
  }
  
}
