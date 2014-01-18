package edu.harvard.chs.citedownutils


import edu.harvard.chs.citedown.ast.RootNode

import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions

import static org.junit.Assert.*
import org.junit.Test

class TestRefCitation {


  String markdownText = '#Heading#\n\nLinked [text][1].\n\n[1]: http://shot.holycross.edu\n[2]: urn:cts:greekLit:tlg0012.tlg001:1 "Iliad, book 1"\n'

  @Test void testRefCite() {
    MarkdownUtil mdu = new MarkdownUtil(markdownText)

    mdu.collectReferences()

    Set expectedKeySet = ["1","2"]
    assert mdu.referenceMap.keySet() == expectedKeySet

  }
  
}
