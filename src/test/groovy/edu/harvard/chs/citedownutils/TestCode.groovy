package edu.harvard.chs.citedownutils

import static org.junit.Assert.*
import org.junit.Test

class TestCode {

  String code = "#Heading with `code`#\n\nText paragraph and `code` inline.\n\nPara 2\n\n"


  @Test void testCode() {
    MarkdownUtil mdu = new MarkdownUtil(code)
    assert mdu
    String converted = mdu.toMarkdown()
    def lines = converted.readLines()

    String expectedHeader = "#Heading with `code`#"
    assert lines[0] == expectedHeader

    String expectedLine = "Text paragraph and `code` inline."
    assert lines[2] == expectedLine

  }

}
