package edu.harvard.chs.citedownutils

import static org.junit.Assert.*
import org.junit.Test


/* It is valid to construct an empty MarkdownUtil object,
 * but the toMarkdown method requires that a parsed document
 * root already be defined (either via a constructor passing in
 * a source String, or using the setSource() method.
 */

class TestSetSource extends GroovyTestCase {

  String tinyInput = "Hello, world.\n\n"

  @Test void testEmptyRoot() {
    MarkdownUtil mdu = new MarkdownUtil()
    assert mdu
    shouldFail {
      assert mdu.toMarkdown()
    }
  }


  @Test void testValidSource() {
    MarkdownUtil mdInConstructor = new MarkdownUtil(tinyInput)

    MarkdownUtil mdSetManually = new MarkdownUtil()
    mdSetManually.setSource(tinyInput)

    assert mdInConstructor.toMarkdown() == mdSetManually.toMarkdown()
  }

  
}
