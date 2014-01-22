package edu.harvard.chs.citedownutils


import edu.harvard.chs.citedown.ast.RootNode

import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions

import static org.junit.Assert.*
import org.junit.Test

class TestLinks {


  String links = '#Heading#\n\nLink using [reference notation][1] in this paragraph\n\nDirect [link](http://www.homermultitext.org) here\n\n[1]:  http://shot.holycross.edu/ "A web page"\n\n'


  @Test void testLinks() {
    MarkdownUtil mdu = new MarkdownUtil(links)
    assert mdu
    String converted = mdu.toMarkdown()
    def lines = converted.readLines()

    String expectedRefLink =  "Link using [reference notation][1] in this paragraph"
    assert lines[2] == expectedRefLink

    String expectedExpLink = "Direct [link](http://www.homermultitext.org) here"
    assert lines[4] == expectedExpLink

  }


}
