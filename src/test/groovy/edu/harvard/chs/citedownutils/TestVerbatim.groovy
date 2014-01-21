package edu.harvard.chs.citedownutils

import static org.junit.Assert.*
import org.junit.Test

class TestVerbatim {

  String code = "#Heading with `code`#\n\nText paragraph and `code` inline.\n\n    a = a + 1\n\n    print a\n\n"


  @Test void testCode() {
    MarkdownUtil mdu = new MarkdownUtil(code)
    assert mdu
    String converted = mdu.toMarkdown()
    def lines = converted.readLines()

    String expectedVerbatim = "    a = a + 1"
    assert lines[4].size() == expectedVerbatim.size()
    assert lines[4] == expectedVerbatim
  }

}
