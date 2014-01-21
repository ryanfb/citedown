package edu.harvard.chs.citedownutils


import edu.harvard.chs.citedown.ast.RootNode

import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions

import static org.junit.Assert.*
import org.junit.Test

class TestCitedImage {

  String testImage = "urn:cite:hmt:vaimg.VA002RN-0003"
  String urlBase = "http://beta.hpcc.uh.edu/tomcat/hmtcite/images"

  @Test void testUrnResolve() {
    MarkdownUtil mdu = new MarkdownUtil()
    mdu.img = urlBase
    mdu.imgCollections = ["urn:cite:hmt:vaimg"]

    String expectedUrl = "http://beta.hpcc.uh.edu/tomcat/hmtcite/images?request=GetImagePlus&urn=urn:cite:hmt:vaimg.VA002RN-0003"
    assert mdu.urlForCitedUrn(testImage) == expectedUrl
  }
  
  @Test void testInMd() {
    String md = '#Cited image#\n\nVen. A, {folio 2}[img] recto\n\n[img]: urn:cite:hmt:vaimg.VA002RN-0003 "VA, folio two recto"\n\n[hmt]: http://www.homerultitext.org\n\n'

    MarkdownUtil mdu = new MarkdownUtil(md)
    mdu.img = urlBase
    mdu.imgCollections = ["urn:cite:hmt:vaimg"]
    String cd = mdu.toMarkdown()
    def lines = cd.readLines()

    String expectedResolution = "Ven. A, [folio 2](http://beta.hpcc.uh.edu/tomcat/hmtcite/images?request=GetImagePlus&urn=urn:cite:hmt:vaimg.VA002RN-0003) recto"    
    assert lines[2] == expectedResolution

    // But URL references are passed through unchanged
    assert lines[4] == "[hmt]: http://www.homerultitext.org"
    System.err.println cd

  }


}
