package edu.harvard.chs.citedownutils

import static org.junit.Assert.*
import org.junit.Test

class TestMdHeader {

  String simpleHeader = "#Heading 1#\n\nText paragraph 1.\n\n\n"
  String mixedContent = "#Heading with *emphasis*#\n\nText paragraph 1.\n\n\n"

  @Test void testSimple() {
    MarkdownUtil mdu = new MarkdownUtil(simpleHeader)
    assert mdu

    String converted = mdu.toMarkdown()
    def lines = converted.readLines()
    assert lines[0] == "#Heading 1#"
    assert lines[2] == "Text paragraph 1."
  }

  @Test void testMixedContent() {
    MarkdownUtil mdu = new MarkdownUtil(mixedContent)
    assert mdu
    String converted = mdu.toMarkdown()
    def lines = converted.readLines()
    assert lines[0] == "#Heading with *emphasis*#"
    assert lines[2] == "Text paragraph 1."

    
  }



  
}
