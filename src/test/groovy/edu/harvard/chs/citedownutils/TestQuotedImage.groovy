package edu.harvard.chs.citedownutils


import edu.harvard.chs.citedown.ast.RootNode

import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions

import static org.junit.Assert.*
import org.junit.Test

class TestQuotedImage {

  String testImage = "urn:cite:hmt:vaimg.VA002RN-0003"
  String urlBase = "http://beta.hpcc.uh.edu/tomcat/hmtcite/images"
  def collections =  ["urn:cite:hmt:vaimg"]


  @Test void testUrnResolve() {
    MarkdownUtil mdu = new MarkdownUtil()
    mdu.img = urlBase
    mdu.imgCollections = collections.clone()

    String expectedUrl = "http://beta.hpcc.uh.edu/tomcat/hmtcite/images?request=GetBinaryImage&urn=urn:cite:hmt:vaimg.VA002RN-0003"
    assert mdu.urlForQuotedUrn(testImage) == expectedUrl
  }
  

  @Test void testInMd() {
    String md =  "#Quoted image#\n\nEmbedded image: !{folio 2 r}[img]\n\n[img]: ${testImage}\n\n[hmt]:  http://www.homermultitext.org\n\n"

    MarkdownUtil mdu = new MarkdownUtil(md)
    mdu.img = urlBase
    mdu.imgCollections = collections.clone()

    String cd = mdu.toMarkdown()
    def lines = cd .readLines()

    String expectedLink = "Embedded image: ![!folio 2 r](http://beta.hpcc.uh.edu/tomcat/hmtcite/images?request=GetBinaryImage&urn=urn:cite:hmt:vaimg.VA002RN-0003)"
    assert lines[2] == expectedLink

    String expectedLiteral = "[hmt]:  http://www.homermultitext.org"
    assert lines[4] == expectedLiteral

  }

}
