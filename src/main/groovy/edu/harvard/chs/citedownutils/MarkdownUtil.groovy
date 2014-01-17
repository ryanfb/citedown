package edu.harvard.chs.citedownutils

import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions
import edu.harvard.chs.citedown.ast.RootNode

/** Utilities for working with citedown source and converting
 * to vanilla markdown.
 */
class MarkdownUtil {

  /** Root node of pegdown parsing result. */
  RootNode root

  /** Source text in citedown format. */
  String citedown

  /** Constructor requiring root node. */
  MarkdownUtil(String citedownSource) {
    this.citedown = citedownSource
    PegDownProcessor pdp = new PegDownProcessor(Extensions.CITE)
    this.root = pdp.parser.parse(citedownSource.toCharArray())
  }

  String toMarkdown() {
    return toMarkdown(root, "", "")
  }



  String toMarkdown(Object n, String accumulated, String contextNote) {

    String txt = ""
    Integer starthere = n.getStartIndex()
    Integer endhere = n.getEndIndex()

    switch (n.getClass().name) {

    case "edu.harvard.chs.citedown.ast.BulletListNode":
    System.err.println "BULL LIST: " + citedown.substring(starthere,endhere)
    contextNote = "BULLLIST"
    break


    case "edu.harvard.chs.citedown.ast.ListItemNode":
    if (contextNote == "BULLLIST") {
      txt = "-"
    }
    contextNote = ""
    System.err.println "LIST ITEM: " + citedown.substring(starthere,endhere)
    break


    case "edu.harvard.chs.citedown.ast.SuperNode":
    txt = "${citedown.substring(starthere,endhere).replaceAll(/\t/,'')}" + "\n"
    contextNote = ""
    break

    case "edu.harvard.chs.citedown.ast.HeaderNode":
    txt = "${citedown.substring(starthere,endhere).replaceAll(/\t/,'')}\n"
    contextNote = ""
    break
    
    default:
    break
    }
    accumulated += txt


    n.getChildren().each { c ->
      accumulated =  toMarkdown(c, accumulated, contextNote)
    }
    return accumulated.toString()
  }

}
