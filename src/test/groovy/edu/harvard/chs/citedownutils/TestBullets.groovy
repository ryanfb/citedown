package edu.harvard.chs.citedownutils

import static org.junit.Assert.*
import org.junit.Test

class TestBullets {

  String markdownText = "Text paragraph\n\n- item 1\n- no 2\n- item 3\n\n"
  String mixedText = "Text paragraph\n\n- *item* 1\n- no 2\n- item 3\n\n"

  @Test void testSimple() {
    MarkdownUtil mdu = new MarkdownUtil(markdownText)
    assert mdu

    String converted = mdu.toMarkdown()
    System.err.println converted
    def lines = converted.readLines()
    assert lines[2] == "- item 1"
  }

  @Test void testMixed() {
    MarkdownUtil mdu = new MarkdownUtil(mixedText)
    assert mdu

    String converted = mdu.toMarkdown()
    def lines = converted.readLines()
    System.err.println "Lines:  " + lines
    assert lines[2] == "- *item* 1"
  }  
}
