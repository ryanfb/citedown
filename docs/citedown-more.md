#More details about the  citedown-to-markdown utility#

- User manual:  see userguide.md


## Overview of output ##

This document summarizes the markdown that the citedown-to-markdown utility produce for a citation or quotation of a given type of CITE URN.


## Converting citedown citations to markdown ##

Citations using citedown's {} notation are converted to markdown links with square brackets.  The resolved URLs submit a CITE "Plus" request to the relevant service: `GetPassagePlus` for a CTS, `GetObjectPlus` for a CITE Collection and `GetImagePlus` for a CITE Image service.

## Converting citedown quotations with `!` to markdown ##

Quoted images are mapped to markdown's embbeded image notation, which expresses that an image should be embedded in the text contents.

Quotation of texts and objects is more complex.  Quoted text retrieved from a CTS is formatted in markdown's "block quote"  notation (lines beginning `> `).  But what does it mean to quote in citedown a text retrieved in an XML reply from a Canonical Text Service? Perhaps you actually want to embed the full XML of the CTS reply's `passage` element, but more probably the content of the XML text nodes is a better match to the plain-text writing of a citedown document.  You can set whichever option you prefer with the boolean property simpleTextInQuotation.  The default value is true, meaning only the content of XML text nodes will be quoted. 

CITE collections are sets of objects that share a common set of properties.  A table is a natural representaion for he properties of a an object, and by default quotations of CITE objects are converted to Multimarkdown's table format, using property labels as headings for the table.  It would be reasonable to have a configurable alternative since Multimarkdown's extension to the original markdown definition is not universally supported, but in the current version no alternative mapping (e.g., as a list of label-value pairs) is implemented.
