package edu.harvard.chs.citedownutils

import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions
import edu.harvard.chs.citedown.ast.RootNode
import org.parboiled.support.ParsingResult

import edu.harvard.chs.cite.CiteUrn
import edu.harvard.chs.cite.CtsUrn
/*
AbbreviationNode	 
AbstractNode	 
AutoLinkNode	 
BlockQuoteNode	 
BulletListNode	 
CiteRefLinkNode	 
CodeNode	 
DefinitionListNode	 
DefinitionNode	 
DefinitionTermNode	 
√ EmphNode	 
ExpImageNode	 
ExpLinkNode	 
√ HeaderNode	 
HtmlBlockNode	 
InlineHtmlNode	 
ListItemNode	 
MailLinkNode	 
OrderedListNode	 
√ ParaNode	 
QuotedNode	 
ReferenceNode	 
RefImageNode	 
RefLinkNode	 
√ RootNode	 
SimpleNode	 
SpecialTextNode	 
√ StrongNode	 
SuperNode	 
TableBodyNode	 
TableCaptionNode	 
TableCellNode	 
TableColumnNode	 
TableHeaderNode	 
TableNode	 
TableRowNode	 
√ TextNode	 
VerbatimNode	 
WikiLinkNode
*/

/** Utilities for working with citedown source and converting
 * to vanilla markdown with all URN references resolved to URLs.
 */
class MarkdownUtil {

  ArrayList terminalNodes = ["TextNode"]
  
  ArrayList blockNodes = ["ParaNode", "HeaderNode", "RootNode", "BulletListNode"]

  ArrayList inlineNodes = ["EmphNode", "StrongNode"]

  /** Root node of pegdown parsing result. */
  RootNode root

  /** Source text in citedown format. */
  String citedown


  /** Base URL value for CTS request. */
  String cts

  /** Base URL value for CITE Collection request. */
  String coll

  /** Base URL value for CITE Collection request. */
  String img

  /** List of collections configured with CITE Image Extension. */
  java.util.ArrayList imgCollections = [] 

  /** Map of reference abbreviations to URNs. */
  java.util.LinkedHashMap referenceMap = [:]


  def inlineStack = []

  String blockTrail = ""

  /** Empty constructor */
  MarkdownUtil() {
  }


  /** Constructor requiring citedown source. */
  MarkdownUtil(String citedownSource) {
    this.citedown = citedownSource
    PegDownProcessor pdp = new PegDownProcessor(Extensions.CITE)
    this.root = pdp.parser.parse(citedownSource.toCharArray())
  }

  void setSource(String citedownSource) {
    this.citedown = citedownSource
    PegDownProcessor pdp = new PegDownProcessor(Extensions.CITE)
    this.root = pdp.parser.parse(citedownSource.toCharArray())
  }


  String extractRef(String s) {
    def pieces = s.split(/:/)
    String ref = pieces[0].replaceFirst('\\[','')
    return ref.replaceFirst('\\]','')
  }

  void collectReferences() {
    collectReferences(root)
  }

  void collectReferences(Object n) {
    String classShort =  n.getClass().name.replaceFirst("edu.harvard.chs.citedown.ast.","")
    if (classShort == "ReferenceNode") {
      String txt = citedown.substring(n.getStartIndex(), n.getEndIndex())
      def pair = [n.getUrl(), n.getTitle()]
      referenceMap[extractRef(txt)] = pair
      
    } else {
      n.getChildren().each { c ->
	collectReferences(c)
      }
    }
  }


  /** Resolves a CITE URN value to a URL.  If the URN value
   * is a CITE URN, the list of collections configured with the
   * CITE Image Extension is checked.
   * @param urnStr String value of the URN to resolve.
   * @returns A URL pointing to the CITE *Plus method of
   * the appropriate CITE service for the URN.
   * @throws Exception if urnStr cannot be parsed as
   * either a CTS URN or a CITE Collection URN.
   */
  String urlForCitedUrn(String urnStr) 
  throws Exception {
    String reply = null
    try {
      CtsUrn urn = new CtsUrn(urnStr)
      reply = "${cts}?request=GetPassagePlus&urn=${urn}"
    } catch (Exception ctse) {
    }

    try {
      CiteUrn urn = new CiteUrn(urnStr)
      String collectionUrn = "urn:cite:${urn.getNs()}:${urn.getCollection()}"
      if (imgCollections.contains(collectionUrn) ) {
	reply = "${img}?request=GetImagePlus&urn=${urn}"
      } else {
	reply = "${coll}?request=GetObjectPlus&urn=${urn}"
      }
    } catch (Exception obje) {
    }
    if (reply == null) {
      throw new Exception ("CitedownToMarkdown:  could not resolve URN ${urnStr} to a URL.")
    } else {
      return reply
    }
  }



  String toMarkdown() {
    return toMarkdown(root, "", "")
  }

  String toMarkdown(Object n, String accumulated, String contextNote) {

    String txt = ""
    Integer startIdx = n.getStartIndex()
    Integer endIdx = n.getEndIndex()

    String shortName = n.getClass().name.replaceFirst("edu.harvard.chs.citedown.ast.","")
    if (blockNodes.contains(shortName)) {
      // record context
      contextNote = shortName
      // pop off inlineStack and add blockTrail
      // as needed
      while (inlineStack.size() > 0) {
	def lastPair = inlineStack.pop()
	txt = txt + lastPair[0]
      }
      if (blockTrail.size() > 0) {
	txt = "${txt}${blockTrail}\n\n"
      }
    }

    switch (n.getClass().name) {

    case "edu.harvard.chs.citedown.ast.TextNode":
    txt = n.getText()

    // check for stuff to append:
    if (inlineStack.size() > 0) {
      boolean done = false
      while (!done) {
	def lastPair = inlineStack.pop()
	if (lastPair[1] == endIdx) {
	  txt = txt + lastPair[0]
	} else {
	  inlineStack.push(lastPair)
	  done = true
	}
	if (inlineStack.size() == 0) {
	  done = true
	}
      }
    }
    break

    case "edu.harvard.chs.citedown.ast.EmphNode":
    txt = "*"
    def pair = ["*", endIdx - 1]
    inlineStack.push(pair)
    break


    case "edu.harvard.chs.citedown.ast.StrongNode":
    txt = "**"
    def pair = ["**", endIdx - 2]
    inlineStack.push(pair)
    break

    case "edu.harvard.chs.citedown.ast.ListItemNode":
    System.err.println "Context is " + contextNote
    if (contextNote == "BulletListNode") {
       txt = "\n- "
    }
    break

    case "edu.harvard.chs.citedown.ast.HeaderNode":
    Integer level = n.getLevel()
    Integer count = 1
    while (count <= level) {
      txt = txt + "#"
      count++;
    }
    blockTrail = txt
    break
    

    // Ignore these block classes:
    case "edu.harvard.chs.citedown.ast.BulletListNode":
    case "edu.harvard.chs.citedown.ast.ParaNode":
    case "edu.harvard.chs.citedown.ast.RootNode":
    case "edu.harvard.chs.citedown.ast.SuperNode":
    break

    default:
    System.err.println "n is " + n.getClass() + ":   " + n
    break
    }
    accumulated += txt


    n.getChildren().each { c ->
      accumulated =  toMarkdown(c, accumulated, contextNote)
    }
    return accumulated.toString()
  }

}
