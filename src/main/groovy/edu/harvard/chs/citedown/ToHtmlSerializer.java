/*
 * Copyright (C) 2010-2011 Mathias Doenitz
 *
 * Based on peg-markdown (C) 2008-2010 John MacFarlane
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.harvard.chs.citedown;

import org.parboiled.common.StringUtils;
import edu.harvard.chs.citedown.ast.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.parboiled.common.Preconditions.checkArgNotNull;

public class ToHtmlSerializer implements Visitor {

    protected Printer printer = new Printer();
    protected final Map<String, ReferenceNode> references = new HashMap<String, ReferenceNode>();
    protected final Map<String, String> abbreviations = new HashMap<String, String>();
    protected final LinkRenderer linkRenderer;

    protected TableNode currentTableNode;
    protected int currentTableColumn;
    protected boolean inTableHeader;

    public ToHtmlSerializer(LinkRenderer linkRenderer) {
        this.linkRenderer = linkRenderer;
    }

    public String toHtml(RootNode astRoot) {
        checkArgNotNull(astRoot, "astRoot");
        astRoot.accept(this);
        return printer.getString();
    }
    
    public void visit(RootNode node) {
        for (ReferenceNode refNode : node.getReferences()) {
            visitChildren(refNode);
            references.put(normalize(printer.getString()), refNode);
            printer.clear();
        }
        for (AbbreviationNode abbrNode : node.getAbbreviations()) {
            visitChildren(abbrNode);
            String abbr = printer.getString();
            printer.clear();
            abbrNode.getExpansion().accept(this);
            String expansion = printer.getString();
            abbreviations.put(abbr, expansion);
            printer.clear();
        }
        visitChildren(node);
    }

    public void visit(AbbreviationNode node) {
    }

    public void visit(AutoLinkNode node) {
        printLink(linkRenderer.render(node));
    }

    public void visit(BlockQuoteNode node) {
        printIndentedTag(node, "blockquote");
    }

    public void visit(BulletListNode node) {
        printIndentedTag(node, "ul");
    }

    public void visit(CodeNode node) {
        printTag(node, "code");
    }

    public void visit(DefinitionListNode node) {
        printIndentedTag(node, "dl");
    }

    public void visit(DefinitionNode node) {
        printTag(node, "dd");
    }

    public void visit(DefinitionTermNode node) {
        printTag(node, "dt");
    }

    public void visit(EmphNode node) {
        printTag(node, "em");
    }

    public void visit(ExpImageNode node) {
        printImageTag(node, node.url);
    }

    public void visit(ExpLinkNode node) {
        String text = printChildrenToString(node);
        printLink(linkRenderer.render(node, text));
    }

    public void visit(HeaderNode node) {
        printTag(node, "h" + node.getLevel());
    }

    public void visit(HtmlBlockNode node) {
        String text = node.getText();
        if (text.length() > 0) printer.println();
        printer.print(text);
    }

    public void visit(InlineHtmlNode node) {
        printer.print(node.getText());
    }

    public void visit(ListItemNode node) {
        printer.println();
        printTag(node, "li");
    }

    public void visit(MailLinkNode node) {
        printLink(linkRenderer.render(node));
    }

    public void visit(OrderedListNode node) {
        printIndentedTag(node, "ol");
    }

    public void visit(ParaNode node) {
        printTag(node, "p");
    }

    public void visit(QuotedNode node) {
        switch (node.getType()) {
            case DoubleAngle:
                printer.print("&laquo;");
                visitChildren(node);
                printer.print("&raquo;");
                break;
            case Double:
                printer.print("&ldquo;");
                visitChildren(node);
                printer.print("&rdquo;");
                break;
            case Single:
                printer.print("&lsquo;");
                visitChildren(node);
                printer.print("&rsquo;");
                break;
        }
    }

    public void visit(ReferenceNode node) {
        // reference nodes are not printed
    }

    public void visit(RefImageNode node) {
        String text = printChildrenToString(node);
        String key = node.referenceKey != null ? printChildrenToString(node.referenceKey) : text;
        ReferenceNode refNode = references.get(normalize(key));
        if (refNode == null) { // "fake" reference image link
            printer.print("![").print(text).print(']');
            if (node.separatorSpace != null) {
                printer.print(node.separatorSpace).print('[');
                if (node.referenceKey != null) printer.print(key);
                printer.print(']');
            }
        } else printImageTag(node, refNode.getUrl());
    }

    public void visit(RefLinkNode node) {
        String text = printChildrenToString(node);
        String key = node.referenceKey != null ? printChildrenToString(node.referenceKey) : text;
        ReferenceNode refNode = references.get(normalize(key));
        if (refNode == null) { // "fake" reference link
            printer.print('[').print(text).print(']');
            if (node.separatorSpace != null) {
                printer.print(node.separatorSpace).print('[');
                if (node.referenceKey != null) printer.print(key);
                printer.print(']');
            }
        } else printLink(linkRenderer.render(node, refNode.getUrl(), refNode.getTitle(), text));
    }

    public void visit(CiteRefLinkNode node) {
        String text = printChildrenToString(node);
        String key = node.referenceKey != null ? printChildrenToString(node.referenceKey) : text;
        ReferenceNode refNode = references.get(normalize(key));

        if(node.image && node.inline) {
            printer.println().print("<img");
            printAttribute("class", "cite-img");
            printAttribute("src", refNode.getUrl());
            printAttribute("alt", text);
            if (!StringUtils.isEmpty(refNode.getTitle())) {
                printAttribute("title", refNode.getTitle());
            }
            printer.print("/>");
        }
        else {
            if(node.inline) {
                printer.println().print("<blockquote");
            } else {
                printer.println().print("<a");
            }

            if(refNode.getUrl().startsWith("urn:cts:")) {
                printAttribute("class", "cite-cts");
            } else if(refNode.getUrl().startsWith("urn:cite:")) {
                if(node.image) {
                    printAttribute("class", "cite-img");
                }
                else {
                    printAttribute("class", "cite-coll");
                }
            }
            
            if(node.inline) {
                printAttribute("cite", refNode.getUrl());
            } else {
                printAttribute("href", refNode.getUrl());
            }

            if (!StringUtils.isEmpty(refNode.getTitle())) {
                printAttribute("title", refNode.getTitle());
            }
            printer.print(">");

            printer.print(text);

            if(node.inline) {
                printer.print("</blockquote>");
            } else {
                printer.print("</a>");
            }
        }
    }

    public void visit(SimpleNode node) {
        switch (node.getType()) {
            case Apostrophe:
                printer.print("&rsquo;");
                break;
            case Ellipsis:
                printer.print("&hellip;");
                break;
            case Emdash:
                printer.print("&mdash;");
                break;
            case Endash:
                printer.print("&ndash;");
                break;
            case HRule:
                printer.println().print("<hr/>");
                break;
            case Linebreak:
                printer.print("<br/>");
                break;
            case Nbsp:
                printer.print("&nbsp;");
                break;
            default:
                throw new IllegalStateException();
        }
    }

    public void visit(StrongNode node) {
        printTag(node, "strong");
    }

    public void visit(TableBodyNode node) {
        printIndentedTag(node, "tbody");
    }

    @Override
    public void visit(TableCaptionNode node) {
        printer.println().print("<caption>");
        visitChildren(node);
        printer.print("</caption>");
    }
    public void visit(TableCellNode node) {
        String tag = inTableHeader ? "th" : "td";
        List<TableColumnNode> columns = currentTableNode.getColumns();
        TableColumnNode column = columns.get(Math.min(currentTableColumn, columns.size()-1));

        printer.println().print('<').print(tag);
        column.accept(this);
        if (node.getColSpan() > 1) printer.print(" colspan=\"").print(Integer.toString(node.getColSpan())).print('"');
        printer.print('>');
        visitChildren(node);
        printer.print('<').print('/').print(tag).print('>');

        currentTableColumn += node.getColSpan();
    }

    public void visit(TableColumnNode node) {
        switch (node.getAlignment()) {
            case None:
                break;
            case Left:
                printer.print(" align=\"left\"");
                break;
            case Right:
                printer.print(" align=\"right\"");
                break;
            case Center:
                printer.print(" align=\"center\"");
                break;
            default:
                throw new IllegalStateException();
        }
    }

    public void visit(TableHeaderNode node) {
        inTableHeader = true;
        printIndentedTag(node, "thead");
        inTableHeader = false;
    }

    public void visit(TableNode node) {
        currentTableNode = node;
        printIndentedTag(node, "table");
        currentTableNode = null;
    }

    public void visit(TableRowNode node) {
        currentTableColumn = 0;
        printIndentedTag(node, "tr");
    }

    public void visit(VerbatimNode node) {
        printer.println().print("<pre><code");
        if (!StringUtils.isEmpty(node.getType())) {
            printAttribute("class", node.getType());
        }
        printer.print(">");
        String text = node.getText();
        // print HTML breaks for all initial newlines
        while(text.charAt(0) == '\n') {
            printer.print("<br/>");
            text = text.substring(1);
        }
        printer.printEncoded(text);
        printer.print("</code></pre>");
    }

    public void visit(WikiLinkNode node) {
        printLink(linkRenderer.render(node));
    }

    public void visit(TextNode node) {
        if (abbreviations.isEmpty()) {
            printer.print(node.getText());
        } else {
            printWithAbbreviations(node.getText());
        }
    }

    public void visit(SpecialTextNode node) {
        printer.printEncoded(node.getText());
    }

    public void visit(SuperNode node) {
        visitChildren(node);
    }

    public void visit(Node node) {
        // override this method for processing custom Node implementations
        throw new RuntimeException("Not implemented");
    }

    // helpers

    protected void visitChildren(SuperNode node) {
        for (Node child : node.getChildren()) {
            child.accept(this);
        }
    }

    protected void printTag(TextNode node, String tag) {
        printer.print('<').print(tag).print('>');
        printer.printEncoded(node.getText());
        printer.print('<').print('/').print(tag).print('>');
    }

    protected void printTag(SuperNode node, String tag) {
        printer.print('<').print(tag).print('>');
        visitChildren(node);
        printer.print('<').print('/').print(tag).print('>');
    }

    protected void printIndentedTag(SuperNode node, String tag) {
        printer.println().print('<').print(tag).print('>').indent(+2);
        visitChildren(node);
        printer.indent(-2).println().print('<').print('/').print(tag).print('>');
    }

    protected void printImageTag(SuperNode imageNode, String url) {
        printer.print("<img src=\"").print(url).print("\"  alt=\"")
                .printEncoded(printChildrenToString(imageNode)).print("\"/>");
    }

    protected void printLink(LinkRenderer.Rendering rendering) {
        printer.print('<').print('a');
        printAttribute("href", rendering.href);
        for (LinkRenderer.Attribute attr : rendering.attributes) {
            printAttribute(attr.name, attr.value);
        }
        printer.print('>').print(rendering.text).print("</a>");
    }

    private void printAttribute(String name, String value) {
        printer.print(' ').print(name).print('=').print('"').print(value).print('"');
    }

    protected String printChildrenToString(SuperNode node) {
        Printer priorPrinter = printer;
        printer = new Printer();
        visitChildren(node);
        String result = printer.getString();
        printer = priorPrinter;
        return result;
    }

    protected String normalize(String string) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            switch(c) {
                case ' ':
                case '\n':
                case '\t':
                    continue;
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    protected void printWithAbbreviations(String string) {
        Map<Integer, Map.Entry<String, String>> expansions = null;

        for (Map.Entry<String, String> entry : abbreviations.entrySet()) {
            // first check, whether we have a legal match
            String abbr = entry.getKey();

            int ix = 0;
            while (true) {
                int sx = string.indexOf(abbr, ix);
                if (sx == -1) break;

                // only allow whole word matches
                ix = sx + abbr.length();

                if (sx > 0 && Character.isLetterOrDigit(string.charAt(sx - 1))) continue;
                if (ix < string.length() && Character.isLetterOrDigit(string.charAt(ix))) {
                    continue;
                }

                // ok, legal match so save an expansions "task" for all matches
                if (expansions == null) {
                    expansions = new TreeMap<Integer, Map.Entry<String, String>>();
                }
                expansions.put(sx, entry);
            }
        }

        if (expansions != null) {
            int ix = 0;
            for (Map.Entry<Integer, Map.Entry<String, String>> entry : expansions.entrySet()) {
                int sx = entry.getKey();
                String abbr = entry.getValue().getKey();
                String expansion = entry.getValue().getValue();

                printer.printEncoded(string.substring(ix, sx));
                printer.print("<abbr");
                if (StringUtils.isNotEmpty(expansion)) {
                    printer.print(" title=\"");
                    printer.printEncoded(expansion);
                    printer.print('"');
                }
                printer.print('>');
                printer.printEncoded(abbr);
                printer.print("</abbr>");
                ix = sx + abbr.length();
            }
            printer.print(string.substring(ix));
        } else {
            printer.print(string);
        }
    }
}
