package edu.harvard.chs.citedownutils


import edu.harvard.chs.citedown.ast.RootNode

import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions

import static org.junit.Assert.*
import org.junit.Test

class TestCitedObject {

  String urlBase =  "http://beta.hpcc.uh.edu/tomcat/hmtcite/collections"

  @Test void testCitation() {
    MarkdownUtil mdu = new MarkdownUtil()
    mdu.coll = urlBase

    String testObject = "urn:cite:hmt:msA.12r"
    String expectedUrl = "${mdu.coll}?request=GetObjectPlus&urn=${testObject}"
    assert mdu.urlForCitedUrn(testObject) == expectedUrl
  }


  @Test void testInMd() {
    String md = '#Cited object#\n\nVen. A, {folio 12}[obj] recto\n\n[obj]: urn:cite:hmt:msA.12r "Venetus A, folio twelve recto"\n\n[hmt]: http://www.homerultitext.org\n\n'

    MarkdownUtil mdu = new MarkdownUtil(md)
    mdu.coll = urlBase
    String cd = mdu.toMarkdown()
    def lines = cd.readLines()


    String expectedLine = "Ven. A, [folio 12](http://beta.hpcc.uh.edu/tomcat/hmtcite/collections?request=GetObjectPlus&urn=urn:cite:hmt:msA.12r) recto"
    assert lines[2] == expectedLine

    String expectedLiteral = "[hmt]: http://www.homerultitext.org"
    assert lines[4] == expectedLiteral

  }
  
}
