package edu.harvard.chs.citedownutils


import edu.harvard.chs.citedown.ast.RootNode

import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions

import static org.junit.Assert.*
import org.junit.Test

class TestCitedObject {

  
  @Test void testCitedImage() {
    MarkdownUtil mdu = new MarkdownUtil()
    mdu.coll = "http://beta.hpcc.uh.edu/tomcat/hmtcite/collections"

    String testObject = "urn:cite:hmt:msA.12r"
    String expectedUrl = "${mdu.coll}?request=GetObjectPlus&urn=${testObject}"
    assert mdu.urlForCitedUrn(testObject) == expectedUrl
  }
  
}
