package edu.harvard.chs.citedownutils

import static org.junit.Assert.*
import org.junit.Test


class TestMailTo {

  String code = "#Heading#\n\nHere is para with a <mail.me@example.info> mail address\n\n"



  @Test void testCode() {
    MarkdownUtil mdu = new MarkdownUtil(code)
    assert mdu
    String converted = mdu.toMarkdown()
    def lines = converted.readLines()

    String expectedLine = "Here is para with a <mail.me@example.info> mail address"
    assert lines[2] == expectedLine
  }

}
