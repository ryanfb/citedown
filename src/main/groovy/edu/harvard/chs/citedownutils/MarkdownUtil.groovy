package edu.harvard.chs.citedownutils

import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions
import edu.harvard.chs.citedown.ast.RootNode


import edu.harvard.chs.cite.CiteUrn
import edu.harvard.chs.cite.CtsUrn


/** Utilities for working with citedown source and converting
 * to vanilla markdown.
 */
class MarkdownUtil {

  // remove this
  def debug

  /** Root node of pegdown parsing result. */
  RootNode root

  /** Source text in citedown format. */
  String citedown


  /** Base URL value for CTS request. */
  String cts // = "http://beta.hpcc.uh.edu/tomcat/hmtcite/texts?request=GetPassagePlus&"

  /** Base URL value for CITE Collection request. */
  String coll //= "http://beta.hpcc.uh.edu/tomcat/hmtcite/collections?request=GetObjectPlus&"

  /** Base URL value for CITE Collection request. */
  String img

  /** List of collections configured with CITE Image Extension. */
  java.util.ArrayList imgCollections = [] 

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
        if (debug > 0) { System.err.println "Test " + urnStr} 
        String reply = null
        try {
            CtsUrn urn = new CtsUrn(urnStr)
            reply = "${cts}urn=${urn}"
            if (debug  > 0) { System.err.println "${urn} is a CTS URN."}
        } catch (Exception ctse) {
        }

        try {
            CiteUrn urn = new CiteUrn(urnStr)
            String collectionUrn = "urn:cite:${urn.getNs()}:${urn.getCollection()}"
            if (imgCollections.contains(collectionUrn) ) {
                reply = "${img}?request=GetImagePlus&urn=${urn}"
            } else {
                reply = "${coll}urn=${urn}"
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
