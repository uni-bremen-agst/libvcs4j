/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 The ConQAT Project                                   |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+-------------------------------------------------------------------------*/
package org.conqat.lib.commons.html;

import java.util.EnumSet;
import java.util.Set;

/**
 * Enumeration of all HTML elements.
 * <p>
 * Part of this file was generated from the <a
 * href="http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">XHTML FrameSet
 * DTD</a> using the "dtd2enum.pl" perl script in this directory.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45653 $
 * @ConQAT.Rating GREEN Hash: EFEB92A209C0201795DBF43D145EDDDB
 */
public enum EHTMLElement {

	/** The &lt;{@linkplain #HTML}&gt; element. */
	HTML("html", EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ID, EHTMLAttribute.XMLNS),

	/** The &lt;{@linkplain #HEAD}&gt; element. */
	HEAD("head", EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ID, EHTMLAttribute.PROFILE),

	/** The &lt;{@linkplain #TITLE}&gt; element. */
	TITLE("title", EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ID),

	/** The &lt;{@linkplain #BASE}&gt; element. */
	BASE("base", EHTMLAttribute.ID, EHTMLAttribute.HREF, EHTMLAttribute.TARGET),

	/** The &lt;{@linkplain #META}&gt; element. */
	META("meta", EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ID, EHTMLAttribute.HTTP_EQUIV,
			EHTMLAttribute.NAME, EHTMLAttribute.CONTENT, EHTMLAttribute.SCHEME,
			EHTMLAttribute.CHARSET),

	/** The &lt;{@linkplain #LINK}&gt; element. */
	LINK("link", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.CHARSET,
			EHTMLAttribute.HREF, EHTMLAttribute.HREFLANG, EHTMLAttribute.TYPE,
			EHTMLAttribute.REL, EHTMLAttribute.REV, EHTMLAttribute.MEDIA,
			EHTMLAttribute.TARGET),

	/** The &lt;{@linkplain #STYLE}&gt; element. */
	STYLE("style", EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ID, EHTMLAttribute.TYPE,
			EHTMLAttribute.MEDIA, EHTMLAttribute.TITLE,
			EHTMLAttribute.XML_SPACE),

	/** The &lt;{@linkplain #SCRIPT}&gt; element. */
	SCRIPT("script", EHTMLAttribute.ID, EHTMLAttribute.CHARSET,
			EHTMLAttribute.TYPE, EHTMLAttribute.LANGUAGE, EHTMLAttribute.SRC,
			EHTMLAttribute.DEFER, EHTMLAttribute.XML_SPACE),

	/** The &lt;{@linkplain #NOSCRIPT}&gt; element. */
	NOSCRIPT("noscript", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #FRAMESET}&gt; element. */
	FRAMESET("frameset", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.ROWS,
			EHTMLAttribute.COLS, EHTMLAttribute.ONLOAD, EHTMLAttribute.ONUNLOAD),

	/** The &lt;{@linkplain #FRAME}&gt; element. */
	FRAME("frame", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE,
			EHTMLAttribute.LONGDESC, EHTMLAttribute.NAME, EHTMLAttribute.SRC,
			EHTMLAttribute.FRAMEBORDER, EHTMLAttribute.MARGINWIDTH,
			EHTMLAttribute.MARGINHEIGHT, EHTMLAttribute.NORESIZE,
			EHTMLAttribute.SCROLLING),

	/** The &lt;{@linkplain #IFRAME}&gt; element. */
	IFRAME("iframe", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE,
			EHTMLAttribute.LONGDESC, EHTMLAttribute.NAME, EHTMLAttribute.SRC,
			EHTMLAttribute.FRAMEBORDER, EHTMLAttribute.MARGINWIDTH,
			EHTMLAttribute.MARGINHEIGHT, EHTMLAttribute.SCROLLING,
			EHTMLAttribute.ALIGN, EHTMLAttribute.HEIGHT, EHTMLAttribute.WIDTH),

	/** The &lt;{@linkplain #NOFRAMES}&gt; element. */
	NOFRAMES("noframes", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #BODY}&gt; element. */
	BODY("body", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.ONLOAD,
			EHTMLAttribute.ONUNLOAD, EHTMLAttribute.BACKGROUND,
			EHTMLAttribute.BGCOLOR, EHTMLAttribute.TEXT, EHTMLAttribute.LINK,
			EHTMLAttribute.VLINK, EHTMLAttribute.ALINK),

	/** The &lt;{@linkplain #DIV}&gt; element. */
	DIV("div", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.ALIGN),

	/** The &lt;{@linkplain #P}&gt; element. */
	P("p", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.ALIGN),

	/** The &lt;{@linkplain #H1}&gt; element. */
	H1("h1", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.ALIGN),

	/** The &lt;{@linkplain #H2}&gt; element. */
	H2("h2", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.ALIGN),

	/** The &lt;{@linkplain #H3}&gt; element. */
	H3("h3", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.ALIGN),

	/** The &lt;{@linkplain #H4}&gt; element. */
	H4("h4", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.ALIGN),

	/** The &lt;{@linkplain #H5}&gt; element. */
	H5("h5", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.ALIGN),

	/** The &lt;{@linkplain #H6}&gt; element. */
	H6("h6", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.ALIGN),

	/** The &lt;{@linkplain #UL}&gt; element. */
	UL("ul", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.TYPE, EHTMLAttribute.COMPACT),

	/** The &lt;{@linkplain #OL}&gt; element. */
	OL("ol", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.TYPE,
			EHTMLAttribute.COMPACT, EHTMLAttribute.START),

	/** The &lt;{@linkplain #MENU}&gt; element. */
	MENU("menu", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.COMPACT),

	/** The &lt;{@linkplain #DIR}&gt; element. */
	DIR("dir", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.COMPACT),

	/** The &lt;{@linkplain #LI}&gt; element. */
	LI("li", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.TYPE, EHTMLAttribute.VALUE),

	/** The &lt;{@linkplain #DL}&gt; element. */
	DL("dl", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.COMPACT),

	/** The &lt;{@linkplain #DT}&gt; element. */
	DT("dt", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #DD}&gt; element. */
	DD("dd", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #ADDRESS}&gt; element. */
	ADDRESS("address", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #HR}&gt; element. */
	HR("hr", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.ALIGN,
			EHTMLAttribute.NOSHADE, EHTMLAttribute.SIZE, EHTMLAttribute.WIDTH),

	/** The &lt;{@linkplain #PRE}&gt; element. */
	PRE("pre", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.WIDTH,
			EHTMLAttribute.XML_SPACE),

	/** The &lt;{@linkplain #BLOCKQUOTE}&gt; element. */
	BLOCKQUOTE("blockquote", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP,
			EHTMLAttribute.CITE),

	/** The &lt;{@linkplain #CENTER}&gt; element. */
	CENTER("center", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #INS}&gt; element. */
	INS("ins", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.CITE,
			EHTMLAttribute.DATETIME),

	/** The &lt;{@linkplain #DEL}&gt; element. */
	DEL("del", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.CITE,
			EHTMLAttribute.DATETIME),

	/** The &lt;{@linkplain #A}&gt; element. */
	A("a", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.ACCESSKEY,
			EHTMLAttribute.TABINDEX, EHTMLAttribute.ONFOCUS,
			EHTMLAttribute.ONBLUR, EHTMLAttribute.CHARSET, EHTMLAttribute.TYPE,
			EHTMLAttribute.NAME, EHTMLAttribute.HREF, EHTMLAttribute.HREFLANG,
			EHTMLAttribute.REL, EHTMLAttribute.REV, EHTMLAttribute.SHAPE,
			EHTMLAttribute.COORDS, EHTMLAttribute.TARGET),

	/** The &lt;{@linkplain #SPAN}&gt; element. */
	SPAN("span", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #BDO}&gt; element. */
	BDO("bdo", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR),

	/** The &lt;{@linkplain #BR}&gt; element. */
	BR("br", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.CLEAR),

	/** The &lt;{@linkplain #EM}&gt; element. */
	EM("em", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #STRONG}&gt; element. */
	STRONG("strong", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #DFN}&gt; element. */
	DFN("dfn", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #CODE}&gt; element. */
	CODE("code", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #SAMP}&gt; element. */
	SAMP("samp", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #KBD}&gt; element. */
	KBD("kbd", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #VAR}&gt; element. */
	VAR("var", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #CITE}&gt; element. */
	CITE("cite", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #ABBR}&gt; element. */
	ABBR("abbr", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #ACRONYM}&gt; element. */
	ACRONYM("acronym", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #Q}&gt; element. */
	Q("q", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.CITE),

	/** The &lt;{@linkplain #SUB}&gt; element. */
	SUB("sub", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #SUP}&gt; element. */
	SUP("sup", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #TT}&gt; element. */
	TT("tt", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #I}&gt; element. */
	I("i", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #B}&gt; element. */
	B("b", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #BIG}&gt; element. */
	BIG("big", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #SMALL}&gt; element. */
	SMALL("small", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #U}&gt; element. */
	U("u", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #S}&gt; element. */
	S("s", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #STRIKE}&gt; element. */
	STRIKE("strike", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #BASEFONT}&gt; element. */
	BASEFONT("basefont", EHTMLAttribute.ID, EHTMLAttribute.SIZE,
			EHTMLAttribute.COLOR, EHTMLAttribute.FACE),

	/** The &lt;{@linkplain #FONT}&gt; element. */
	FONT("font", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.SIZE, EHTMLAttribute.COLOR,
			EHTMLAttribute.FACE),

	/** The &lt;{@linkplain #OBJECT}&gt; element. */
	OBJECT("object", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP,
			EHTMLAttribute.DECLARE, EHTMLAttribute.CLASSID,
			EHTMLAttribute.CODEBASE, EHTMLAttribute.DATA, EHTMLAttribute.TYPE,
			EHTMLAttribute.CODETYPE, EHTMLAttribute.ARCHIVE,
			EHTMLAttribute.STANDBY, EHTMLAttribute.HEIGHT,
			EHTMLAttribute.WIDTH, EHTMLAttribute.USEMAP, EHTMLAttribute.NAME,
			EHTMLAttribute.TABINDEX, EHTMLAttribute.ALIGN,
			EHTMLAttribute.BORDER, EHTMLAttribute.HSPACE, EHTMLAttribute.VSPACE),

	/** The &lt;{@linkplain #PARAM}&gt; element. */
	PARAM("param", EHTMLAttribute.ID, EHTMLAttribute.NAME,
			EHTMLAttribute.VALUE, EHTMLAttribute.VALUETYPE, EHTMLAttribute.TYPE),

	/** The &lt;{@linkplain #APPLET}&gt; element. */
	APPLET("applet", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE,
			EHTMLAttribute.CODEBASE, EHTMLAttribute.ARCHIVE,
			EHTMLAttribute.CODE, EHTMLAttribute.OBJECT, EHTMLAttribute.ALT,
			EHTMLAttribute.NAME, EHTMLAttribute.WIDTH, EHTMLAttribute.HEIGHT,
			EHTMLAttribute.ALIGN, EHTMLAttribute.HSPACE, EHTMLAttribute.VSPACE),

	/** The &lt;{@linkplain #IMG}&gt; element. */
	IMG("img", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.SRC, EHTMLAttribute.ALT,
			EHTMLAttribute.NAME, EHTMLAttribute.LONGDESC,
			EHTMLAttribute.HEIGHT, EHTMLAttribute.WIDTH, EHTMLAttribute.USEMAP,
			EHTMLAttribute.ISMAP, EHTMLAttribute.ALIGN, EHTMLAttribute.BORDER,
			EHTMLAttribute.HSPACE, EHTMLAttribute.VSPACE),

	/** The &lt;{@linkplain #MAP}&gt; element. */
	MAP("map", EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.NAME),

	/** The &lt;{@linkplain #AREA}&gt; element. */
	AREA("area", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.ACCESSKEY,
			EHTMLAttribute.TABINDEX, EHTMLAttribute.ONFOCUS,
			EHTMLAttribute.ONBLUR, EHTMLAttribute.SHAPE, EHTMLAttribute.COORDS,
			EHTMLAttribute.HREF, EHTMLAttribute.NOHREF, EHTMLAttribute.ALT,
			EHTMLAttribute.TARGET),

	/** The &lt;{@linkplain #FORM}&gt; element. */
	FORM("form", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.ACTION,
			EHTMLAttribute.METHOD, EHTMLAttribute.NAME, EHTMLAttribute.ENCTYPE,
			EHTMLAttribute.ONSUBMIT, EHTMLAttribute.ONRESET,
			EHTMLAttribute.ACCEPT, EHTMLAttribute.ACCEPT_CHARSET,
			EHTMLAttribute.TARGET),

	/** The &lt;{@linkplain #LABEL}&gt; element. */
	LABEL("label", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP,
			EHTMLAttribute.FOR, EHTMLAttribute.ACCESSKEY,
			EHTMLAttribute.ONFOCUS, EHTMLAttribute.ONBLUR),

	/** The &lt;{@linkplain #INPUT}&gt; element. */
	INPUT("input", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP,
			EHTMLAttribute.ACCESSKEY, EHTMLAttribute.TABINDEX,
			EHTMLAttribute.ONFOCUS, EHTMLAttribute.ONBLUR, EHTMLAttribute.TYPE,
			EHTMLAttribute.NAME, EHTMLAttribute.VALUE, EHTMLAttribute.CHECKED,
			EHTMLAttribute.DISABLED, EHTMLAttribute.READONLY,
			EHTMLAttribute.SIZE, EHTMLAttribute.MAXLENGTH, EHTMLAttribute.SRC,
			EHTMLAttribute.ALT, EHTMLAttribute.USEMAP, EHTMLAttribute.ONSELECT,
			EHTMLAttribute.ONCHANGE, EHTMLAttribute.ACCEPT,
			EHTMLAttribute.ALIGN, EHTMLAttribute.PLACEHOLDER,
			EHTMLAttribute.AUTOFOCUS),

	/** The &lt;{@linkplain #SELECT}&gt; element. */
	SELECT("select", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP,
			EHTMLAttribute.NAME, EHTMLAttribute.SIZE, EHTMLAttribute.MULTIPLE,
			EHTMLAttribute.DISABLED, EHTMLAttribute.TABINDEX,
			EHTMLAttribute.ONFOCUS, EHTMLAttribute.ONBLUR,
			EHTMLAttribute.ONCHANGE),

	/** The &lt;{@linkplain #OPTGROUP}&gt; element. */
	OPTGROUP("optgroup", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP,
			EHTMLAttribute.DISABLED, EHTMLAttribute.LABEL),

	/** The &lt;{@linkplain #OPTION}&gt; element. */
	OPTION("option", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP,
			EHTMLAttribute.SELECTED, EHTMLAttribute.DISABLED,
			EHTMLAttribute.LABEL, EHTMLAttribute.VALUE),

	/** The &lt;{@linkplain #TEXTAREA}&gt; element. */
	TEXTAREA("textarea", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP,
			EHTMLAttribute.ACCESSKEY, EHTMLAttribute.TABINDEX,
			EHTMLAttribute.ONFOCUS, EHTMLAttribute.ONBLUR, EHTMLAttribute.NAME,
			EHTMLAttribute.ROWS, EHTMLAttribute.COLS, EHTMLAttribute.DISABLED,
			EHTMLAttribute.READONLY, EHTMLAttribute.ONSELECT,
			EHTMLAttribute.ONCHANGE),

	/** The &lt;{@linkplain #FIELDSET}&gt; element. */
	FIELDSET("fieldset", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP),

	/** The &lt;{@linkplain #LEGEND}&gt; element. */
	LEGEND("legend", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP,
			EHTMLAttribute.ACCESSKEY, EHTMLAttribute.ALIGN),

	/** The &lt;{@linkplain #BUTTON}&gt; element. */
	BUTTON("button", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP,
			EHTMLAttribute.ACCESSKEY, EHTMLAttribute.TABINDEX,
			EHTMLAttribute.ONFOCUS, EHTMLAttribute.ONBLUR, EHTMLAttribute.NAME,
			EHTMLAttribute.VALUE, EHTMLAttribute.TYPE, EHTMLAttribute.DISABLED),

	/** The &lt;{@linkplain #ISINDEX}&gt; element. */
	ISINDEX("isindex", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR, EHTMLAttribute.PROMPT),

	/** The &lt;{@linkplain #TABLE}&gt; element. */
	TABLE("table", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP,
			EHTMLAttribute.SUMMARY, EHTMLAttribute.WIDTH,
			EHTMLAttribute.BORDER, EHTMLAttribute.FRAME, EHTMLAttribute.RULES,
			EHTMLAttribute.CELLSPACING, EHTMLAttribute.CELLPADDING,
			EHTMLAttribute.ALIGN, EHTMLAttribute.BGCOLOR),

	/** The &lt;{@linkplain #CAPTION}&gt; element. */
	CAPTION("caption", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP,
			EHTMLAttribute.ALIGN),

	/** The &lt;{@linkplain #COLGROUP}&gt; element. */
	COLGROUP("colgroup", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP,
			EHTMLAttribute.SPAN, EHTMLAttribute.WIDTH, EHTMLAttribute.ALIGN,
			EHTMLAttribute.CHAR, EHTMLAttribute.CHAROFF, EHTMLAttribute.VALIGN),

	/** The &lt;{@linkplain #COL}&gt; element. */
	COL("col", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.SPAN, EHTMLAttribute.WIDTH,
			EHTMLAttribute.ALIGN, EHTMLAttribute.CHAR, EHTMLAttribute.CHAROFF,
			EHTMLAttribute.VALIGN),

	/** The &lt;{@linkplain #THEAD}&gt; element. */
	THEAD("thead", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP,
			EHTMLAttribute.ALIGN, EHTMLAttribute.CHAR, EHTMLAttribute.CHAROFF,
			EHTMLAttribute.VALIGN),

	/** The &lt;{@linkplain #TFOOT}&gt; element. */
	TFOOT("tfoot", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP,
			EHTMLAttribute.ALIGN, EHTMLAttribute.CHAR, EHTMLAttribute.CHAROFF,
			EHTMLAttribute.VALIGN),

	/** The &lt;{@linkplain #TBODY}&gt; element. */
	TBODY("tbody", EHTMLAttribute.ID, EHTMLAttribute.CLASS,
			EHTMLAttribute.STYLE, EHTMLAttribute.TITLE, EHTMLAttribute.LANG,
			EHTMLAttribute.XML_LANG, EHTMLAttribute.DIR,
			EHTMLAttribute.ONCLICK, EHTMLAttribute.ONDBLCLICK,
			EHTMLAttribute.ONMOUSEDOWN, EHTMLAttribute.ONMOUSEUP,
			EHTMLAttribute.ONMOUSEOVER, EHTMLAttribute.ONMOUSEMOVE,
			EHTMLAttribute.ONMOUSEOUT, EHTMLAttribute.ONKEYPRESS,
			EHTMLAttribute.ONKEYDOWN, EHTMLAttribute.ONKEYUP,
			EHTMLAttribute.ALIGN, EHTMLAttribute.CHAR, EHTMLAttribute.CHAROFF,
			EHTMLAttribute.VALIGN),

	/** The &lt;{@linkplain #TR}&gt; element. */
	TR("tr", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.ALIGN, EHTMLAttribute.CHAR,
			EHTMLAttribute.CHAROFF, EHTMLAttribute.VALIGN,
			EHTMLAttribute.BGCOLOR),

	/** The &lt;{@linkplain #TH}&gt; element. */
	TH("th", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.ABBR, EHTMLAttribute.AXIS,
			EHTMLAttribute.HEADERS, EHTMLAttribute.SCOPE,
			EHTMLAttribute.ROWSPAN, EHTMLAttribute.COLSPAN,
			EHTMLAttribute.ALIGN, EHTMLAttribute.CHAR, EHTMLAttribute.CHAROFF,
			EHTMLAttribute.VALIGN, EHTMLAttribute.NOWRAP,
			EHTMLAttribute.BGCOLOR, EHTMLAttribute.WIDTH, EHTMLAttribute.HEIGHT),

	/** The &lt;{@linkplain #TD}&gt; element. */
	TD("td", EHTMLAttribute.ID, EHTMLAttribute.CLASS, EHTMLAttribute.STYLE,
			EHTMLAttribute.TITLE, EHTMLAttribute.LANG, EHTMLAttribute.XML_LANG,
			EHTMLAttribute.DIR, EHTMLAttribute.ONCLICK,
			EHTMLAttribute.ONDBLCLICK, EHTMLAttribute.ONMOUSEDOWN,
			EHTMLAttribute.ONMOUSEUP, EHTMLAttribute.ONMOUSEOVER,
			EHTMLAttribute.ONMOUSEMOVE, EHTMLAttribute.ONMOUSEOUT,
			EHTMLAttribute.ONKEYPRESS, EHTMLAttribute.ONKEYDOWN,
			EHTMLAttribute.ONKEYUP, EHTMLAttribute.ABBR, EHTMLAttribute.AXIS,
			EHTMLAttribute.HEADERS, EHTMLAttribute.SCOPE,
			EHTMLAttribute.ROWSPAN, EHTMLAttribute.COLSPAN,
			EHTMLAttribute.ALIGN, EHTMLAttribute.CHAR, EHTMLAttribute.CHAROFF,
			EHTMLAttribute.VALIGN, EHTMLAttribute.NOWRAP,
			EHTMLAttribute.BGCOLOR, EHTMLAttribute.WIDTH, EHTMLAttribute.HEIGHT);

	/** The "real" name of the element. */
	private final String name;

	/** The attributes acutally allowed for this element. */
	private final Set<EHTMLAttribute> allowedAttributes;

	/** Constructor. */
	private EHTMLElement(String name, EHTMLAttribute... attributes) {
		this.name = name;
		if (attributes.length > 0) {
			allowedAttributes = EnumSet.of(attributes[0], attributes);
		} else {
			allowedAttributes = EnumSet.noneOf(EHTMLAttribute.class);
		}
	}

	/** Returns whether the given attribute is allowed for the element. */
	public boolean allowsAttribute(EHTMLAttribute attribute) {
		return allowedAttributes.contains(attribute);
	}

	/** Returns the name as used in the HTML output. */
	public String getName() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return name;
	}
}