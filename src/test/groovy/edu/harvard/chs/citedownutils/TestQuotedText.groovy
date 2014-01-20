package edu.harvard.chs.citedownutils


import edu.harvard.chs.citedown.ast.RootNode

import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions

import static org.junit.Assert.*
import org.junit.Test

class TestQuotedText {

  String baseUrl = "http://beta.hpcc.uh.edu/tomcat/hmtcite/texts"
  String testPassage = "urn:cts:greekLit:tlg0012.tlg001:1.1-1.9"

  @Test void testResolution() {
    MarkdownUtil mdu = new MarkdownUtil()
    mdu.cts = baseUrl

    String expectedUrl = "${mdu.cts}?request=GetPassage&urn=${testPassage}"
    assert mdu.urlForQuotedUrn(testPassage) == expectedUrl
  }

  /*
  @Test void testInMarkdown() {
    String md = "#Cited text#\n\nThe {first 9 lines}[psg] of the *Iliad*\n\n[psg]: ${testPassage}\n\n[hmt]: http://www.homermultitext.org\n\n"
    MarkdownUtil mdu = new MarkdownUtil(md)
    mdu.cts = baseUrl

    String cd = mdu.toMarkdown()
    def lines = cd.readLines()

    String expectedLine = "The [first 9 lines](http://beta.hpcc.uh.edu/tomcat/hmtcite/texts?request=GetPassagePlus&urn=urn:cts:greekLit:tlg0012.tlg001:1.1-1.9) of the *Iliad*"
    assert lines[2] == expectedLine

    String expectedLiteral = "[hmt]: http://www.homermultitext.org"
    assert lines[4] == expectedLiteral
  }
  */
  
}
