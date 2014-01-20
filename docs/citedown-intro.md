#Introduction to citedown#

citedown adds one syntactic feature to markdown.  In markdown, square brackets define text attached to a linked URL;  in citedown, additionally, curly brackets define text attached to a linked URN.

citedown also extends the use of one markdown feature.  In markdown, the exclamation point can precede a link when the linked URL is an image:  it means "embed the image, rather than link to it".  citedown allows the use of the exclamation point with any URN to mean "quote the reference rather than citing it".  This means that *any* object identified by URN can be either cited or quoted.

##Reference definitions in citedown##

Citedown requires you to identify URNs you cite using markdown's reference list syntax.  In this markdown notation, an identifer in square brackets is followed by a colon, white space, and the URN.  You may also put a label for this reference in quotes on the same line;  while not required, this is good practice, and can be used by markdown tools to do things like construct an annotated bibliography for your document.

While citedown permits you to include reference definitions anywhere in your document, it is conventional to gather them in a single list, like a bibliography at the end of your text.  Here is an example reference list, defining references for an image, a passage of text, and an object.

[img]: urn:cite:hmt:vaimg.VA012RN-0013@0.049,0.2106,0.481,0.2031 "Detail showing first nine lines of the text"

[txt]: urn:cts:greekLit:tlg0012.tlg001.msA:1.1-1.9 "Book 1, lines 1-9"

[obj]: urn:cite:hmt:msA.12r "Folio 12 recto"

##Citing references##

The following examples illustrate the use of citedown's curly brackets to cite the passage of text, the object and the region of an image defined in the reference list above.

The first {nine lines of the *Iliad*}[txt] are visible in {this image}[img], which occurs on {folio 12 recto}[obj] in the Venetus A  manuscript.


##Quotations in citedown##

In the next examples, you can see the same three references quoted, rather than cited.

The first nine lines of the *Iliad*, !{lines 1-9}[txt]

These lines look like !{this}[img] in the Venetus A manuscript.

They occur on folio 12 recto, !{here}[obj]

