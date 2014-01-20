package edu.harvard.chs.citedownutils


import edu.harvard.chs.citedown.ast.RootNode

import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions

import static org.junit.Assert.*
import org.junit.Test

class TestQuotedText {

  def ctsXmlNs = new groovy.xml.Namespace("http://chs.harvard.edu/xmlns/cts")

  String baseUrl = "http://beta.hpcc.uh.edu/tomcat/hmtcite/texts"
  String testPassage = "urn:cts:greekLit:tlg0012.tlg001.msA:1.1-1.9"

  @Test void testResolution() {
    MarkdownUtil mdu = new MarkdownUtil()
    mdu.cts = baseUrl

    String expectedUrl = "${mdu.cts}?request=GetPassage&urn=${testPassage}"
    assert mdu.urlForQuotedUrn(testPassage) == expectedUrl
  }


  @Test void testInMarkdown() {
    String md = "#Quoted text#\n\n*Iliad* 1.1-1.9 !{here}[psg]\n\n[psg]: ${testPassage}\n\n[hmt]: http://www.homermultitext.org\n\n"
    MarkdownUtil mdu = new MarkdownUtil(md)
    mdu.cts = baseUrl

    String cd = mdu.toMarkdown()
    def lines = cd.readLines()

    System.err.println cd
    System.err.println lines

    File tstout = new File("/tmp/cdutst-quotetext.md")
    tstout.setText(cd, "UTF-8")

    //    String expectedLiteral = "[hmt]: http://www.homermultitext.org"

  }
  
}
