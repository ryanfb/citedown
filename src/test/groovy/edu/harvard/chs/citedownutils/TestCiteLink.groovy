package edu.harvard.chs.citedownutils


import edu.harvard.chs.citedown.ast.RootNode

import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions

import static org.junit.Assert.*
import org.junit.Test

class TestCiteLink {

  String md = "{folio 2}[img]"
  String expectedRef = "img"
  String expectedText = "folio 2"

  @Test void testRefLink() {
    MarkdownUtil mdu = new MarkdownUtil()
    assert mdu.extractCiteLinkedText(md) == expectedText
    assert mdu.extractCiteLinkedRef(md) == expectedRef    
  }

}
