package edu.harvard.chs.citedownutils

import static org.junit.Assert.*
import org.junit.Test


class TestFn {


  // Pandoc-style footnotes are not recognized by the citedown parser per se,
  // so in converting to markdown we pass them along literally where your
  // favorite markdown implementation could deal with them.
  String code = "#Heading#\n\nHere is a footnote reference,[^1] in line.\n\n[^1]: Here is the footnote.\n\n"



  @Test void testCode() {
    MarkdownUtil mdu = new MarkdownUtil(code)
    assert mdu
    String converted = mdu.toMarkdown()
    def lines = converted.readLines()

    String expectedRef = "Here is a footnote reference,[^1] in line."
    String expectedNote = "[^1]: Here is the footnote."
    
    assert lines[2] == expectedRef
    assert lines[4] == expectedNote

  }

}
