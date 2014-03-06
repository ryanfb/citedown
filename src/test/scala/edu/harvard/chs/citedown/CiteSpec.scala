package edu.harvard.chs.citedown

import Extensions._


class CiteSpec extends AbstractPegDownSpec {
	"The PegDownProcessor" should {
    	"pass the CiteMarkdown test suite" in {
      		implicit val processor = new PegDownProcessor(CITE)

      		test("CiteMarkdown/References")
      		test("CiteMarkdown/Inline_References")
      		test("CiteMarkdown/AthPol")
      		test("CiteMarkdown/Image_References")
      		test("CiteMarkdown/Footnotes")
  		}
  	}
}
