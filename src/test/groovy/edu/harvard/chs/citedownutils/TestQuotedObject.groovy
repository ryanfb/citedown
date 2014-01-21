package edu.harvard.chs.citedownutils


import edu.harvard.chs.citedown.ast.RootNode

import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions

import static org.junit.Assert.*
import org.junit.Test

class TestQuotedObject {


  String urlBase =  "http://beta.hpcc.uh.edu/tomcat/hmtcite/collections"
  String testObject = "urn:cite:hmt:msA.12r"

  @Test void testCitation() {
    MarkdownUtil mdu = new MarkdownUtil()
    mdu.coll = urlBase

    String expectedUrl = "${mdu.coll}?request=GetObject&urn=${testObject}"
    assert mdu.urlForQuotedUrn(testObject) == expectedUrl
  }


  @Test void testInMarkdown() {
    String md = "#Quoted object#\n\nOpening of Iliad !{folio 12r}[12r]\n\n[12r]: ${testObject}\n\n[hmt]: http://www.homermultitext.org\n\n"

    MarkdownUtil mdu = new MarkdownUtil(md)
    mdu.coll = urlBase
    String cd = mdu.toMarkdown()
    System.err.println cd

  }
}
