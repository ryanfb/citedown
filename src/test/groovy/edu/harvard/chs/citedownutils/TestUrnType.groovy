package edu.harvard.chs.citedownutils


import edu.harvard.chs.citedown.ast.RootNode

import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions

import static org.junit.Assert.*
import org.junit.Test

class TestUrnType {

  
  @Test void testConstructor() {
    MarkdownUtil mdu = new MarkdownUtil()
    mdu.img = "http://beta.hpcc.uh.edu/tomcat/hmtcite/images?request=GetImagePlus&"
    mdu.imgCollections = ["urn:cite:hmt:vaimg"]
    assert mdu
    String testImage = "urn:cite:hmt:vaimg.VA002RN-0003"

    System.err.println "Rsolvd = " + mdu.urlForUrn(testImage)

  }
  
}
