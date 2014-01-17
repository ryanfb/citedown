package edu.harvard.chs.citedownutil


import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions

import static org.junit.Assert.*
import org.junit.Test

class TestTreeUtil {

  String markdownText = "#Heading 1#\n\nText paragraph 1.\n"
  
  @Test void testConstructor() {
    def ca = markdownText.toCharArray()
    System.err.println "ca is a " + ca.getClass()
    PegDownProcessor pdp = new PegDownProcessor(Extensions.CITE)


    TreeUtil tu = new TreeUtil(pdp.parser.parse(ca))
    assert tu
  }
  
}
