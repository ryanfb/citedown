# citedown #

This repository is a fork of the original [pegdown parser for parsing markdown using a PEG grammar][1]:  for information about the source repository, see the accompanying file `README-pegdown.md`.

citedown extends markdown with support for citation (external reference to a resource) and quotation (embedded content from a resource) of scholarly material identified by CITE URN.  Construct a `PegDownProcessor`  with the CITE extension to enable this support, e.g.,

    PegDownProcessor citeProcessor = new PegDownProcessor(Extensions.CITE)



## Other features of this project ##

This repository adds to the `sbt` project in the original pegdown repository a gradle project for building from source.

It also adds a `citedownutils` package that includes a simplified ASCII tree view of the parse tree, and a citedown-to-markdown utility that resolves citedown URNs to URLs, and converts the citedown source to generic markdown.  Further information about citedown notation and its conversion to pure markdown can be found in files in the docs directory.

[1]: https://github.com/sirthias/pegdown
