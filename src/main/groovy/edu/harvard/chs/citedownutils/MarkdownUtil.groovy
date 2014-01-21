package edu.harvard.chs.citedownutils

import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions
import edu.harvard.chs.citedown.ast.RootNode
import org.parboiled.support.ParsingResult

import edu.harvard.chs.cite.CiteUrn
import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.f1k.GreekNode



/* The following list names all Node types implemented in pegdown.
 * Checked items in the list have been implemented and tested in
 * citedown-to-markdown conversion.
 */
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
WikiLinkNode: (WILL NOT BE SUPPORTED IN citedown)
*/


/** Utilities for working with citedown source and converting
 * to vanilla markdown with all URN references resolved to URLs.
 */
class MarkdownUtil {

  // tmp var to remove in production release ....
  Integer debug  = 0

  /** List of block type nodes that are mutually
   * exclusive in markdown.
. */
  ArrayList blockNodes = ["ParaNode", "HeaderNode", "BulletListNode", "OrderedListNode","ReferenceNode"]

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

  /** Suffix string (or one-level stack, if you prefer) for current block context. */
  String blockTrail = ""

  /** Index of substring for ordered lists within
   * parsed string. */
  Integer olIdx

  /** Namespace for CTS XML */
  groovy.xml.Namespace ctsXmlNs = new groovy.xml.Namespace("http://chs.harvard.edu/xmlns/cts")

  /** Namespace for CITE XML */
  groovy.xml.Namespace citeXmlNs = new groovy.xml.Namespace("http://chs.harvard.edu/xmlns/cite")


  /** True if quoted URNs should embed only text content of XML returned
   * from a CTS GetPassage request. False implies that the well-formed XML
   * of the <passage> element will be embedded.
   */
  boolean simpleTextInQuotation = true

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


  /** Extracts linked text from a CITE reference link.
   * @param s The citedown source.
   * @returns The linked text contained within curly brackets
   * in the original source.
   */
  String extractCiteLinkedText(String s) {
    String stripped = s.replaceFirst("\\{",'')
    stripped = stripped.replaceFirst("\\}.+",'')
    return stripped
  }

 /** Extracts bracketed reference ID from a CITE reference link.
   * @param s The citedown source.
   * @returns The linked text contained within squre brackets
   * in the original source.
   */
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

      def pair = [n.getUrl(), n.getTitle()]
      String txt = citedown.substring(n.getStartIndex(), n.getEndIndex())
      referenceMap[extractRef(txt)] = pair
      
    } else {
      if (n == null) {
	System.err.println "MarkdownUtil: reached a null Node while collecting references! (${classShort})"
      } else {
	n.getChildren().each { c ->
	  collectReferences(c)
	}
      }
    }
  }


  /** Resolves a CITE URN value to a URL.  If the URN value
   * is a CITE URN, the list of collections configured with the
   * CITE Image Extension is checked. 
   * @param urnStr String value of the URN to resolve.
   * @returns A URL pointing to the minimum CITE retrieval method of
   * the appropriate CITE service for the URN.
   * @throws Exception if urnStr cannot be parsed as
   * either a CTS URN or a CITE Collection URN.
   */
  String urlForQuotedUrn(String urnStr) 
  throws Exception {
    String reply = null
    try {
      CtsUrn urn = new CtsUrn(urnStr)
      reply = "${cts}?request=GetPassage&urn=${urn}"
    } catch (Exception ctse) {
    }

    try {
      CiteUrn urn = new CiteUrn(urnStr)
      String collectionUrn = "urn:cite:${urn.getNs()}:${urn.getCollection()}"
      if (imgCollections.contains(collectionUrn) ) {
	reply = "${img}?request=GetBinaryImage&urn=${urn}"
      } else {
	reply = "${coll}?request=GetObject&urn=${urn}"
      }
    } catch (Exception obje) {
    }
    if (reply == null) {
      throw new Exception ("CitedownToMarkdown:  could not resolve URN ${urnStr} to a URL.")
    } else {
      return reply
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


  /** Issues a GetObject request to the configured CITE Collection service,
   * formats the reply's CITE Collection object as a table
   * (multimarkdown extension to markdown) with property names as column 
   * headings.
   * @param urnStr CITE Collection URN value of the object to quote.
   * @returns A string with a mutimarkdown table.
   */
  String quoteObject(String urnStr) {
    String quotation = "\n\n"
    URL citeUrl = new URL("${coll}?request=GetObject&urn=${urnStr}")
    String reply = citeUrl.getText("UTF-8")
    def docRoot    
    try {
      docRoot = new XmlParser().parseText(reply)
    } catch (Exception e) {
      System.err.println "MarkdowUtil, quoteObject: Quoted object reply to ${citeUrl} failed to parse.  ${e}"
    }


    docRoot[citeXmlNs.reply][citeXmlNs.citeObject].each { obj ->
      // create a labelling row:
      Integer propCount = 0
      quotation +=  "| "
      obj[citeXmlNs.citeProperty].each { prop ->
	quotation += prop.'@label' + " |"
	propCount++
      }
      quotation += "\n|"
      while (propCount > 0) {
	quotation += " ---- |"
	propCount--
      }
      quotation += "\n|"
      // then get data values:
      obj[citeXmlNs.citeProperty].each { prop ->
	quotation += prop.text() + " |"
	propCount++
      }
      quotation += "\n\n"
    }
    return quotation
  }


  /** Formats a passage of text identified by URN as a markdown
   * blockquote.  Issues a GetPassage request to the configured CTS,
   * then uses the value of the boolean simpleTextInQuotation
   * to decide whether to quote the full XML of the GetPassage reply
   * (false) or just the content of the XML's text nodes (true).
   * @param urnStr CTS URN value identifying the passage to quote.
   * @returns A markdown blockquoted String.
   */
  String quoteText(String urnStr) {
    String quotation = "\n\n"
    URL ctsUrl = new URL("${cts}?request=GetPassage&urn=${urnStr}")
    String reply = ctsUrl.getText("UTF-8")
    def docRoot    
    try {
      docRoot = new XmlParser().parseText(reply)
    } catch (Exception e) {
      System.err.println "MarkdownUtil, quoteText:  Quoted text reply to ${ctsUrl} failed to parse.  ${e}"
    }

    docRoot[ctsXmlNs.reply][ctsXmlNs.passage].each { psg ->
      String replyText
      GreekNode gn = new GreekNode(psg)

      if (simpleTextInQuotation) {
	replyText = gn.collectText()

      } else {
	try {
	  replyText = gn.toXml(true)
	} catch (Exception e) {
	  System.err.println "MarkdownUtil, quoteText:  could not convert GreekNode to XML: " + e
	}
      }
      replyText.readLines().each { ln ->
	quotation = quotation + "> " + ln + "\n"
      }
    }
    
    return quotation + "\n"
  }


  /** Formats a quotation of a URN in citedown format
   * as markdown.  For image URNs, this is a simple conversion
   * of citedown to comparable markdown notation formatted like "![CAPTION]".  
   * For text and collection objects, MarkdownUtil must submit a GetPassage()
   * or GetObject() request to the configured service, and extract the appropriate
   * section of the contents, so this method will fail if the server
   * cannot be reached.
   * @param src The citedown source.
   * @returns A String with markdown resolution of the citation.
   */
  String mdForQuotedUrnContent(String src) {
    
    String reply = ""

    String urn = ""
    String refId = extractCiteLinkedRef(src)
    referenceMap.keySet().each { k ->
      if (k == refId) {
	def record = referenceMap[k]
	urn = record[0]
      }
    }
      
    try {
      CtsUrn ctsUrn = new CtsUrn(urn)
      reply = quoteText(urn)

    } catch (Exception ctse) {
      if (debug > 1) { System.err.println "MarkdownUtil, mdForQuotedUrnContent: " +  urn + " is not a cts URN"}
    }

    try {
      CiteUrn citeUrn = new CiteUrn(urn)
      String collectionUrn = "urn:cite:${citeUrn.getNs()}:${citeUrn.getCollection()}"
      if (imgCollections.contains(collectionUrn) ) {
	String imgUrl = urlForQuotedUrn(urn)
	reply =  "![${extractCiteLinkedText(src)}](${imgUrl})"   

      } else {
	reply = quoteObject(urn)
      }
    } catch (Exception obje) {
      if (debug > 1) { System.err.println "MarkdownUtil, mdForQuotedUrnContent: ${urn} is not a cite urn."}
    }
    
    return  reply
  }

  /** Formats a citation of a URN in citedown format
   * as markdown.
   * @param src The citedown source.
   * @returns A String with markdown resolution of the citation.
   */
  String mdForCitedUrnContent(String src) {
      String url = ""

      String refId = extractCiteLinkedRef(src)
      referenceMap.keySet().each { k ->
	if (k == refId) {
	  def record = referenceMap[k]
	  String urn = record[0]
	  url = urlForCitedUrn(urn)
	}
      }
      return "[${extractCiteLinkedText(src)}](${url})"
  }

  /** Converts the citedown content parsed in
   * root to generic markdown with URN values
   * resolved to URLs.
   * @returns A string of generic markdown.
   * @throws Exception if root is null.
   */
  String toMarkdown() 
  throws Exception {
    if (root == null) {
      throw new Exception("MarkdownUtil, toMarkdown():  no parsed document root defined!")
    }
    referenceMap.clear()
    collectReferences()
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


    if (debug > 0) {
      System.err.println "At ${shortName}, accum: ||"  + accumulated + "||"
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
	blockTrail = ""
      }

      if (accumulated.size() > 0) {

	switch (shortName) {

	case "ReferenceNode":
	if (n.getUrl() ==~ /urn.+/) {
	  // entire node will be omitted since URN will be resolved in place
	  // at citation.
	} else {
	  // use default block behavior for URLs
	  txt = txt + "\n\n"
	}
	break

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
    txt = txt + n.getText()

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
    String trail = ""
    while (count <= level) {
      txt = txt + "#"
      trail = trail + "#"
      count++;
    }
    blockTrail = trail
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
    if (n.getUrl() ==~ /urn.+/) {
      // omit since URN will be resolved in place
      // at citation.

    } else {
      // mirror without trailing \n:
      txt = txt + citedown.substring(startIdx, endIdx).replaceAll(/\n/,"")
    }
    break


    case "edu.harvard.chs.citedown.ast.CiteRefLinkNode":
    // CHECK FOR BANG QUOTATION
    String literalStr = citedown.substring(startIdx, endIdx)
    if (literalStr[0] == "!") {
      txt = txt + mdForQuotedUrnContent(literalStr)
    } else {
      txt = txt + mdForCitedUrnContent(literalStr)
    }
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
    if (debug > 0) { System.err.println "For ${shortName}, accum now: ||"  + accumulated + "||" }
    if (terminalNodes.contains(shortName)) {
    } else {
      n.getChildren().each { c ->
	accumulated =  toMarkdown(c, accumulated, contextNote)
      }
    }
    return accumulated.toString()
  }

}
