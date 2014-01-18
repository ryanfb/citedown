package edu.harvard.chs.citedownutils


import edu.harvard.chs.citedown.ast.RootNode

import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions

import static org.junit.Assert.*
import org.junit.Test

class TestCitedText {

  
  @Test void testCitedImage() {
    MarkdownUtil mdu = new MarkdownUtil()
    mdu.cts = "http://beta.hpcc.uh.edu/tomcat/hmtcite/texts"

    String testPassage = "urn:cts:greekLit:tlg0012.tlg001:1.1-1.9"
    String expectedUrl = "${mdu.cts}?request=GetPassagePlus&urn=${testPassage}"
    assert mdu.urlForCitedUrn(testPassage) == expectedUrl
  }
  
}
