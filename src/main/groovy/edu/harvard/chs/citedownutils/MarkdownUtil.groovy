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
√ BulletListNode	 
√ CiteRefLinkNode	 
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
√ ListItemNode	 
MailLinkNode	 
√ OrderedListNode	 
√ ParaNode	 
QuotedNode	 
√ ReferenceNode	 
RefImageNode	 
RefLinkNode	 
√ RootNode	 
SimpleNode	 
SpecialTextNode	 
√ StrongNode	 
√ SuperNode	 
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

  // tmp var to remove ....
  def debug 

  /** List of block type nodes that are mutually
   * exclusive in markdown.
. */
  ArrayList blockNodes = ["ParaNode", "HeaderNode", "BulletListNode", "OrderedListNode"]

  /** List of node types that will be mirrored without
   * recursive processing
   */
  ArrayList terminalNodes = ["ReferenceNode", "CiteRefLinkNode"]

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

  /** Stack of suffixes for inline markdown.*/
  def inlineStack = []


  /** Suffix string for current block context. */
  String blockTrail = ""


  /** Index of substring for ordered lists within
   * parsed string. */
  Integer olIdx

  /** Empty constructor */
  MarkdownUtil() {
  }


  /** Constructor requiring citedown source. */
  MarkdownUtil(String citedownSource) {
    this.citedown = citedownSource
    PegDownProcessor pdp = new PegDownProcessor(Extensions.CITE)
    this.root = pdp.parser.parse(citedownSource.toCharArray())
  }


  /** Assigns an parses a String of citedown
   * content.
   * @param citedownSource The text source to parse.
   */
  void setSource(String citedownSource) {
    this.citedown = citedownSource
    PegDownProcessor pdp = new PegDownProcessor(Extensions.CITE)
    this.root = pdp.parser.parse(citedownSource.toCharArray())
  }



  /** Finds the reference identifier given in
   * square brackets in the text of a ReferenceNode.
   * @param s The full string of a citedown ReferenceNode.
   * @returns The reference identifier identified
   * in square brackets.
   */
  String extractRef(String s) {
    def pieces = s.split(/:/)
    String ref = pieces[0].replaceFirst('\\[','')
    return ref.replaceFirst('\\]','')
  }



  String extractCiteLinkedText(String s) {
    String stripped = s.replaceFirst("\\{",'')
    stripped = stripped.replaceFirst("\\}.+",'')
    return stripped
  }
  String extractCiteLinkedRef(String s) {
    String stripped = s.replaceFirst("[^\\}]+\\}\\[",'')
    stripped = stripped.replaceFirst("\\]",'')
    return stripped
  }

  /** Builds a map of reference identifiers
   * by recursively examining the document tree
   * from its root. Reference identifiers 
   * are mapped to a pair of values giving the URL 
   * and (possibly null) title of the reference.
   */
  void collectReferences() {
    collectReferences(root)
  }

  /** Builds a map of reference identifiers
   * by recursively examining document tree
   * from node n.
   * The identifiers are mapped to a pair of
   * values giving the URL and (possibly null)
   * title of the reference.
   * @param n Node to examine.
   */
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


  /** Converts the citedown content parsed in
   * root to generic markdown with URN values
   * resolved to URLs.
   * @returns A string of generic markdown.
   */
  String toMarkdown() {
    return toMarkdown(root, "", "") + "\n\n"
  }


  /** Converts the citedown content in a Node
   * to generic markdown with URN values
   * resolved to URLs.
   * @param n The node to convert.
   * @param accumulated Accumulator storing
   * recursively gathered markdown.
   * @param contextNote A string recording the short
   * name of the current block context.
   * @returns A string of generic markdown.
   */
  String toMarkdown(Object n, String accumulated, String contextNote) {

    String txt = ""
    Integer startIdx = n.getStartIndex()
    Integer endIdx = n.getEndIndex()

    String shortName = n.getClass().name.replaceFirst("edu.harvard.chs.citedown.ast.","")


    if (debug) {
      System.err.println "At ${shortName}, accum: || "  + accumulated + "||"
    }


    if (blockNodes.contains(shortName)) {
      // record context
      contextNote = shortName
      // pop off entire inlineStack and 
      // append any closing markup for block
      while (inlineStack.size() > 0) {
	def lastPair = inlineStack.pop()
	txt = txt + lastPair[0]
      }
      if (blockTrail.size() > 0) {
	txt = "${txt}${blockTrail}"
      }

      if (accumulated.size() > 0) {
	if (debug) { System.err.println "Add newlines before " + shortName	
	}

	switch (shortName) {
	case "OrderedListNode":
	case "BulletListNode":
	txt = txt + "\n"
	break

	default:
	txt = txt + "\n\n"
	break
	}

      }
    }

    switch (n.getClass().name) {

    case "edu.harvard.chs.citedown.ast.TextNode":
    // TextNode is the one class where we emit
    // the text content of the Node.
    txt = n.getText()
    System.err.println "TEXT NODE : ||"  + citedown.substring(startIdx,endIdx) + "||"

    // Also need to check for stuff to append when
    // TextNode is following inline markup:
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


    ////////////////////////////////////////////
    /// SIMPLE INLINE ELEMENTS 
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

    ////////////////////////////////////////////



    ////////////////////////////////////////////
    /// SPECIAL BLOCK ELEMENTS 

    case "edu.harvard.chs.citedown.ast.HeaderNode":
    Integer level = n.getLevel()
    Integer count = 1
    while (count <= level) {
      txt = txt + "#"
      count++;
    }
    blockTrail = txt
    break

    ////////////////////////////////////////////




    ////////////////////////////////////////////
    /// LISTS

    case "edu.harvard.chs.citedown.ast.ListItemNode":
    if (contextNote == "BulletListNode") {
       txt = "\n- "
    } else if (contextNote == "OrderedListNode") {
      if (olIdx < startIdx) {
	txt = "\n" + citedown.substring(olIdx, startIdx)
      }
      olIdx = endIdx
    }
    break

    
    case "edu.harvard.chs.citedown.ast.OrderedListNode":
    olIdx = startIdx
    break
    ////////////////////////////////////////////

    ////////////////////////////////////////////
    /// CITATION

    case "edu.harvard.chs.citedown.ast.ReferenceNode":
    // mirror without trailing \n:
    txt = citedown.substring(startIdx, endIdx)
    break


    case "edu.harvard.chs.citedown.ast.CiteRefLinkNode":
    String literalStr = citedown.substring(startIdx, endIdx)
    txt = "[${extractCiteLinkedText(literalStr)}][${extractCiteLinkedRef(literalStr)}]"

    break
    ////////////////////////////////////////////



    break
    // Ignore these block classes:
    case "edu.harvard.chs.citedown.ast.ParaNode":
    case "edu.harvard.chs.citedown.ast.BulletListNode":
    case "edu.harvard.chs.citedown.ast.RootNode":
    case "edu.harvard.chs.citedown.ast.SuperNode":
    break

    // Identify unhandled Nodes classes
    default:
    System.err.println "UNHANDLED CLASS " + n.getClass() + ":   " + n
    break
    }
    accumulated += txt
    System.err.println "For ${shortName}, accum now: || "  + accumulated + "||"
    if (terminalNodes.contains(shortName)) {
    } else {
      n.getChildren().each { c ->
	accumulated =  toMarkdown(c, accumulated, contextNote)
      }
    }
    return accumulated.toString()
  }

}
