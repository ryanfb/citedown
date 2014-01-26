package edu.harvard.chs.citedownutils

import static org.junit.Assert.*
import org.junit.Test


class TestSpecChar {

  String code = "#Heading#\n\nParagraph with a special char!  Really!\n\nAnd a calmer, more banal one.\n\n"


  @Test void testCode() {
    MarkdownUtil mdu = new MarkdownUtil(code)
    assert mdu
    String converted = mdu.toMarkdown()
    def lines = converted.readLines()
    System.err.println converted

    String expectedLine = "Paragraph with a special char! Really!"
    assert lines[2] == expectedLine

  }

}
