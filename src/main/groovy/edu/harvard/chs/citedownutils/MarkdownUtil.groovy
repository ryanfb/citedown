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
StrongNode	 
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
  
  ArrayList blockNodes = ["ParaNode", "HeaderNode", "RootNode"]


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



  String trail = ""

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
    Integer starthere = n.getStartIndex()
    Integer endhere = n.getEndIndex()

    String shortName = n.getClass().name.replaceFirst("edu.harvard.chs.citedown.ast.","")
    System.err.println "Check " + shortName + " in " + blockNodes
    if (blockNodes.contains(shortName)) {
      if (trail.size() > 0) {
	txt = "${trail}\n\n"
      }
    }

    switch (n.getClass().name) {

      //    case "edu.harvard.chs.citedown.ast.BulletListNode":
      //    System.err.println "BULL LIST: " + citedown.substring(starthere,endhere)
      //contextNote = "BULLLIST"
      //break

    case "edu.harvard.chs.citedown.ast.TextNode":
    txt = n.getText()
    break

    case "edu.harvard.chs.citedown.ast.EmphNode":
    txt = "*"
    trail = "*" + trail 
    break

    //case "edu.harvard.chs.citedown.ast.ListItemNode":
    //if (contextNote == "BULLLIST") {
    //   txt = "-"
    //}
    //contextNote = ""
    //System.err.println "LIST ITEM: " + citedown.substring(starthere,endhere)
    //break


    //case "edu.harvard.chs.citedown.ast.SuperNode":
    //txt = "${citedown.substring(starthere,endhere).replaceAll(/\t/,'')}" + "\n"
    //contextNote = ""
    //break


    case "edu.harvard.chs.citedown.ast.ParaNode":
    break

    case "edu.harvard.chs.citedown.ast.HeaderNode":
    //txt = "${citedown.substring(starthere,endhere).replaceAll(/\t/,'')}\n"
    Integer level = n.getLevel()
    Integer count = 1
    while (count <= level) {
      txt = txt + "#"
      count++;
    }
    contextNote = "HEADER"
    trail = txt
    break
    
    default:
    System.err.println "n is " + n.getClass()
    break
    }
    accumulated += txt


    n.getChildren().each { c ->
      accumulated =  toMarkdown(c, accumulated, contextNote)
    }
    return accumulated.toString()
  }

}
