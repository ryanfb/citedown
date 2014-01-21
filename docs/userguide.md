# User's guide to the citedown-to-markdown utilities #


## Ways to use the citedown-to-markdown converter ##

- in Java/groovy programs (currently implemented and documented here)
- from a gradle build task (not yet implemented)
- from a groovy script (not yet implemented)


## Design and configuration ##

citedown allows you to write in simple text with technology-independent URNs.  To translate citedown to markdown, we have to resolve URNs to a URL.  We can do this by mapping URNs to a CITE service on the Web that can deliver content identified by URN.  

The CITE architecture defines two types of URNs, CTS URNs to cite texts in the OHCO2 model, and CITE URNs to cite any kind of discrete object.  In addition, the CITE Image extension defines additional ways of working with CITE objects that include binary image data.

To keep this utility as minimal and simple as markdown itself, you can define a base URL for a CITE service for each type of CITE URN you want to use.  If you only want to cite text URNs, you can configure only a base URL for a CTS service, or if you only want to cite CITE Collection objects, you can configure only a base URL for a CITE Collection service.  Since CITE URNs for images are simply generic CITE Collection URNs, to refer to an object as an image, you need to configure  a CITE Image service, and identify what CITE collections you cite as images.

How to configure:

- from a Java/groovy program
- for the gradle build task
- for the included groovy script
