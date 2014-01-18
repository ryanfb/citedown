package edu.harvard.chs.citedownutils


import edu.harvard.chs.citedown.ast.RootNode

import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions

import static org.junit.Assert.*
import org.junit.Test

class TestCitedImage {

  
  @Test void testCitedImage() {
    MarkdownUtil mdu = new MarkdownUtil()
    mdu.img = "http://beta.hpcc.uh.edu/tomcat/hmtcite/images"
    mdu.imgCollections = ["urn:cite:hmt:vaimg"]
    String testImage = "urn:cite:hmt:vaimg.VA002RN-0003"
    String expectedUrl = "http://beta.hpcc.uh.edu/tomcat/hmtcite/images?request=GetImagePlus&urn=urn:cite:hmt:vaimg.VA002RN-0003"
    assert mdu.urlForCitedUrn(testImage) == expectedUrl
  }
  
}
