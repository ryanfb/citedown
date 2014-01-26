package edu.harvard.chs.citedownutils

import static org.junit.Assert.*
import org.junit.Test

class TestInline {

  String emph = "#Heading with *emphasis*#\n\nText paragraph 1.\n\n\n"
  String strong = "#Heading with *emphasis*#\n\n**Text** paragraph 1.\n\n\n"


  @Test void testEmph() {
    MarkdownUtil mdu = new MarkdownUtil(emph)
    assert mdu

    String converted = mdu.toMarkdown()
    def lines = converted.readLines()
    
    System.err.print lines
    assert lines[0] == "#Heading with *emphasis*#"
  }


  @Test void testStrong() {
    MarkdownUtil mdu = new MarkdownUtil(strong)
    assert mdu
    String converted = mdu.toMarkdown()
    def lines = converted.readLines()
    System.err.println lines
    assert lines[2] == "**Text** paragraph 1."
    
  }

  
}
