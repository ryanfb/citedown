package edu.harvard.chs.citedownutils

import static org.junit.Assert.*
import org.junit.Test

class TestMdHeader {

  String markdownText = "#Heading 1#\n\nText paragraph 1.\n"
  
  @Test void testConstructor() {
    MarkdownUtil mdu = new MarkdownUtil(markdownText)
    assert mdu

    String converted = mdu.toMarkdown()
    def lines = converted.readLines()
    assert lines[0] == "#Heading 1#"
  }
  
}
