package edu.harvard.chs.citedown

import static org.junit.Assert.*
import org.junit.Test

class TestMinimum {

  @Test void testConstructor() {
    PegDownProcessor pdp = new PegDownProcessor(Extensions.CITE)
    assert pdp
  }

  @Test void testSimple() {
    PegDownProcessor pdp = new PegDownProcessor(Extensions.CITE)

    String mdSrc = "#Markdown source#\ngoes to html\n"
    String expectedReply = "<h1>Markdown source</h1><p>goes to html</p>"

    assert pdp.markdownToHtml(mdSrc) == expectedReply
  }
  
}
