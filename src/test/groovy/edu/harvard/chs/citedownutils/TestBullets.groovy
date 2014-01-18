package edu.harvard.chs.citedownutils

import static org.junit.Assert.*
import org.junit.Test

class TestBullets {

  String markdownText = "Text paragraph\n\n- item 1\n- no 2\n- item 3\n\n"

  @Test void testConstructor() {
    MarkdownUtil mdu = new MarkdownUtil(markdownText)
    assert mdu

    String converted = mdu.toMarkdown()
    def lines = converted.readLines()
    assert lines[1] == "-item 1"

  }
  
}
