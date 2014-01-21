package edu.harvard.chs.citedownutils

import static org.junit.Assert.*
import org.junit.Test

class TestOrderedList {

  String markdownText = "Text paragraph\n\n1. item 1\n1. no 2\n2. item 3\n\n"
  String mixed =  "Text paragraph\n\n1. item 1\n1. no 2 *numbered* as 1\n2. item 3\n\n"


  @Test void testSimple() {
    MarkdownUtil mdu = new MarkdownUtil(markdownText)
    assert mdu

    String converted = mdu.toMarkdown()
    def lines = converted.readLines()
    assert lines[2] == "1. item 1"
  }


  @Test void testMixed() {
    MarkdownUtil mdu = new MarkdownUtil(mixed)
    assert mdu

    String converted = mdu.toMarkdown()
    def lines = converted.readLines()
    assert lines[3] == "1. no 2 *numbered* as 1"
  }





}
