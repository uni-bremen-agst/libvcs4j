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

/**
 * Enumeration of all HTML attributes.
 * <p>
 * Part of this file was generated from the <a
 * href="http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">XHTML FrameSet
 * DTD</a> using the "dtd2enum.pl" perl script in this directory.
 * <p>
 * Manual additions (from HTML5) are found towards the end.
 * 
 * @author $Author: juergens $
 * @version $Rev: 41853 $
 * @ConQAT.Rating GREEN Hash: F8D02C3DD492E8A7CF0A96C32DB926D0
 */
public enum EHTMLAttribute {
	/** The {@linkplain #TR} attribute. */
	TR("tr"),

	/** The {@linkplain #STRIKE} attribute. */
	STRIKE("strike"),

	/** The {@linkplain #HTTP_EQUIV} attribute. */
	HTTP_EQUIV("http-equiv"),

	/** The {@linkplain #FORM} attribute. */
	FORM("form"),

	/** The {@linkplain #NOHREF} attribute. */
	NOHREF("nohref"),

	/** The {@linkplain #ONKEYDOWN} attribute. */
	ONKEYDOWN("onkeydown"),

	/** The {@linkplain #TARGET} attribute. */
	TARGET("target"),

	/** The {@linkplain #ONKEYUP} attribute. */
	ONKEYUP("onkeyup"),

	/** The {@linkplain #ONRESET} attribute. */
	ONRESET("onreset"),

	/** The {@linkplain #CODE} attribute. */
	CODE("code"),

	/** The {@linkplain #ACRONYM} attribute. */
	ACRONYM("acronym"),

	/** The {@linkplain #BR} attribute. */
	BR("br"),

	/** The {@linkplain #VALIGN} attribute. */
	VALIGN("valign"),

	/** The {@linkplain #NAME} attribute. */
	NAME("name"),

	/** The {@linkplain #CHARSET} attribute. */
	CHARSET("charset"),

	/** The {@linkplain #H4} attribute. */
	H4("h4"),

	/** The {@linkplain #PROMPT} attribute. */
	PROMPT("prompt"),

	/** The {@linkplain #ACCEPT_CHARSET} attribute. */
	ACCEPT_CHARSET("accept-charset"),

	/** The {@linkplain #EM} attribute. */
	EM("em"),

	/** The {@linkplain #REV} attribute. */
	REV("rev"),

	/** The {@linkplain #TITLE} attribute. */
	TITLE("title"),

	/** The {@linkplain #START} attribute. */
	START("start"),

	/** The {@linkplain #SMALL} attribute. */
	SMALL("small"),

	/** The {@linkplain #ENCTYPE} attribute. */
	ENCTYPE("enctype"),

	/** The {@linkplain #USEMAP} attribute. */
	USEMAP("usemap"),

	/** The {@linkplain #NOWRAP} attribute. */
	NOWRAP("nowrap"),

	/** The {@linkplain #AREA} attribute. */
	AREA("area"),

	/** The {@linkplain #COORDS} attribute. */
	COORDS("coords"),

	/** The {@linkplain #ONBLUR} attribute. */
	ONBLUR("onblur"),

	/** The {@linkplain #DATETIME} attribute. */
	DATETIME("datetime"),

	/** The {@linkplain #DIR} attribute. */
	DIR("dir"),

	/** The {@linkplain #COLOR} attribute. */
	COLOR("color"),

	/** The {@linkplain #VSPACE} attribute. */
	VSPACE("vspace"),

	/** The {@linkplain #UL} attribute. */
	UL("ul"),

	/** The {@linkplain #BACKGROUND} attribute. */
	BACKGROUND("background"),

	/** The {@linkplain #HEIGHT} attribute. */
	HEIGHT("height"),

	/** The {@linkplain #DFN} attribute. */
	DFN("dfn"),

	/** The {@linkplain #IFRAME} attribute. */
	IFRAME("iframe"),

	/** The {@linkplain #CHAR} attribute. */
	CHAR("char"),

	/** The {@linkplain #CODEBASE} attribute. */
	CODEBASE("codebase"),

	/** The {@linkplain #PROFILE} attribute. */
	PROFILE("profile"),

	/** The {@linkplain #REL} attribute. */
	REL("rel"),

	/** The {@linkplain #ONSUBMIT} attribute. */
	ONSUBMIT("onsubmit"),

	/** The {@linkplain #A} attribute. */
	A("a"),

	/** The {@linkplain #IMG} attribute. */
	IMG("img"),

	/** The {@linkplain #MARGINWIDTH} attribute. */
	MARGINWIDTH("marginwidth"),

	/** The {@linkplain #NOFRAMES} attribute. */
	NOFRAMES("noframes"),

	/** The {@linkplain #ONCHANGE} attribute. */
	ONCHANGE("onchange"),

	/** The {@linkplain #U} attribute. */
	U("u"),

	/** The {@linkplain #ABBR} attribute. */
	ABBR("abbr"),

	/** The {@linkplain #HREF} attribute. */
	HREF("href"),

	/** The {@linkplain #SUP} attribute. */
	SUP("sup"),

	/** The {@linkplain #ADDRESS} attribute. */
	ADDRESS("address"),

	/** The {@linkplain #ID} attribute. */
	ID("id"),

	/** The {@linkplain #BASEFONT} attribute. */
	BASEFONT("basefont"),

	/** The {@linkplain #H1} attribute. */
	H1("h1"),

	/** The {@linkplain #HEAD} attribute. */
	HEAD("head"),

	/** The {@linkplain #TBODY} attribute. */
	TBODY("tbody"),

	/** The {@linkplain #VALUE} attribute. */
	VALUE("value"),

	/** The {@linkplain #DATA} attribute. */
	DATA("data"),

	/** The {@linkplain #DD} attribute. */
	DD("dd"),

	/** The {@linkplain #S} attribute. */
	S("s"),

	/** The {@linkplain #DECLARE} attribute. */
	DECLARE("declare"),

	/** The {@linkplain #LI} attribute. */
	LI("li"),

	/** The {@linkplain #TD} attribute. */
	TD("td"),

	/** The {@linkplain #TYPE} attribute. */
	TYPE("type"),

	/** The {@linkplain #HEADERS} attribute. */
	HEADERS("headers"),

	/** The {@linkplain #OBJECT} attribute. */
	OBJECT("object"),

	/** The {@linkplain #DIV} attribute. */
	DIV("div"),

	/** The {@linkplain #NORESIZE} attribute. */
	NORESIZE("noresize"),

	/** The {@linkplain #ROWSPAN} attribute. */
	ROWSPAN("rowspan"),

	/** The {@linkplain #DEFER} attribute. */
	DEFER("defer"),

	/** The {@linkplain #CELLSPACING} attribute. */
	CELLSPACING("cellspacing"),

	/** The {@linkplain #OPTION} attribute. */
	OPTION("option"),

	/** The {@linkplain #CHAROFF} attribute. */
	CHAROFF("charoff"),

	/** The {@linkplain #SELECT} attribute. */
	SELECT("select"),

	/** The {@linkplain #I} attribute. */
	I("i"),

	/** The {@linkplain #ACCEPT} attribute. */
	ACCEPT("accept"),

	/** The {@linkplain #ALT} attribute. */
	ALT("alt"),

	/** The {@linkplain #ONMOUSEOUT} attribute. */
	ONMOUSEOUT("onmouseout"),

	/** The {@linkplain #BORDER} attribute. */
	BORDER("border"),

	/** The {@linkplain #ONUNLOAD} attribute. */
	ONUNLOAD("onunload"),

	/** The {@linkplain #FIELDSET} attribute. */
	FIELDSET("fieldset"),

	/** The {@linkplain #BIG} attribute. */
	BIG("big"),

	/** The {@linkplain #CELLPADDING} attribute. */
	CELLPADDING("cellpadding"),

	/** The {@linkplain #BUTTON} attribute. */
	BUTTON("button"),

	/** The {@linkplain #VALUETYPE} attribute. */
	VALUETYPE("valuetype"),

	/** The {@linkplain #NOSCRIPT} attribute. */
	NOSCRIPT("noscript"),

	/** The {@linkplain #INPUT} attribute. */
	INPUT("input"),

	/** The {@linkplain #TABLE} attribute. */
	TABLE("table"),

	/** The {@linkplain #CONTENT} attribute. */
	CONTENT("content"),

	/** The {@linkplain #CLEAR} attribute. */
	CLEAR("clear"),

	/** The {@linkplain #H5} attribute. */
	H5("h5"),

	/** The {@linkplain #META} attribute. */
	META("meta"),

	/** The {@linkplain #ISINDEX} attribute. */
	ISINDEX("isindex"),

	/** The {@linkplain #MAP} attribute. */
	MAP("map"),

	/** The {@linkplain #TFOOT} attribute. */
	TFOOT("tfoot"),

	/** The {@linkplain #CAPTION} attribute. */
	CAPTION("caption"),

	/** The {@linkplain #ONMOUSEUP} attribute. */
	ONMOUSEUP("onmouseup"),

	/** The {@linkplain #SCOPE} attribute. */
	SCOPE("scope"),

	/** The {@linkplain #BASE} attribute. */
	BASE("base"),

	/** The {@linkplain #ONMOUSEOVER} attribute. */
	ONMOUSEOVER("onmouseover"),

	/** The {@linkplain #LANG} attribute. */
	LANG("lang"),

	/** The {@linkplain #ALIGN} attribute. */
	ALIGN("align"),

	/** The {@linkplain #STRONG} attribute. */
	STRONG("strong"),

	/** The {@linkplain #SCHEME} attribute. */
	SCHEME("scheme"),

	/** The {@linkplain #FRAMEBORDER} attribute. */
	FRAMEBORDER("frameborder"),

	/** The {@linkplain #ONMOUSEDOWN} attribute. */
	ONMOUSEDOWN("onmousedown"),

	/** The {@linkplain #Q} attribute. */
	Q("q"),

	/** The {@linkplain #B} attribute. */
	B("b"),

	/** The {@linkplain #APPLET} attribute. */
	APPLET("applet"),

	/** The {@linkplain #ONCLICK} attribute. */
	ONCLICK("onclick"),

	/** The {@linkplain #SPAN} attribute. */
	SPAN("span"),

	/** The {@linkplain #WIDTH} attribute. */
	WIDTH("width"),

	/** The {@linkplain #VLINK} attribute. */
	VLINK("vlink"),

	/** The {@linkplain #ISMAP} attribute. */
	ISMAP("ismap"),

	/** The {@linkplain #FRAME} attribute. */
	FRAME("frame"),

	/** The {@linkplain #SIZE} attribute. */
	SIZE("size"),

	/** The {@linkplain #BODY} attribute. */
	BODY("body"),

	/** The {@linkplain #FACE} attribute. */
	FACE("face"),

	/** The {@linkplain #OL} attribute. */
	OL("ol"),

	/** The {@linkplain #SUMMARY} attribute. */
	SUMMARY("summary"),

	/** The {@linkplain #HTML} attribute. */
	HTML("html"),

	/** The {@linkplain #BGCOLOR} attribute. */
	BGCOLOR("bgcolor"),

	/** The {@linkplain #TEXT} attribute. */
	TEXT("text"),

	/** The {@linkplain #VAR} attribute. */
	VAR("var"),

	/** The {@linkplain #METHOD} attribute. */
	METHOD("method"),

	/** The {@linkplain #STANDBY} attribute. */
	STANDBY("standby"),

	/** The {@linkplain #LANGUAGE} attribute. */
	LANGUAGE("language"),

	/** The {@linkplain #DEL} attribute. */
	DEL("del"),

	/** The {@linkplain #TABINDEX} attribute. */
	TABINDEX("tabindex"),

	/** The {@linkplain #BLOCKQUOTE} attribute. */
	BLOCKQUOTE("blockquote"),

	/** The {@linkplain #ONMOUSEMOVE} attribute. */
	ONMOUSEMOVE("onmousemove"),

	/** The {@linkplain #STYLE} attribute. */
	STYLE("style"),

	/** The {@linkplain #CODETYPE} attribute. */
	CODETYPE("codetype"),

	/** The {@linkplain #MULTIPLE} attribute. */
	MULTIPLE("multiple"),

	/** The {@linkplain #H3} attribute. */
	H3("h3"),

	/** The {@linkplain #TEXTAREA} attribute. */
	TEXTAREA("textarea"),

	/** The {@linkplain #XMLNS} attribute. */
	XMLNS("xmlns"),

	/** The {@linkplain #ONDBLCLICK} attribute. */
	ONDBLCLICK("ondblclick"),

	/** The {@linkplain #AXIS} attribute. */
	AXIS("axis"),

	/** The {@linkplain #FONT} attribute. */
	FONT("font"),

	/** The {@linkplain #TT} attribute. */
	TT("tt"),

	/** The {@linkplain #COLS} attribute. */
	COLS("cols"),

	/** The {@linkplain #THEAD} attribute. */
	THEAD("thead"),

	/** The {@linkplain #READONLY} attribute. */
	READONLY("readonly"),

	/** The {@linkplain #MEDIA} attribute. */
	MEDIA("media"),

	/** The {@linkplain #H6} attribute. */
	H6("h6"),

	/** The {@linkplain #PARAM} attribute. */
	PARAM("param"),

	/** The {@linkplain #TH} attribute. */
	TH("th"),

	/** The {@linkplain #COMPACT} attribute. */
	COMPACT("compact"),

	/** The {@linkplain #FOR} attribute. */
	FOR("for"),

	/** The {@linkplain #SRC} attribute. */
	SRC("src"),

	/** The {@linkplain #LEGEND} attribute. */
	LEGEND("legend"),

	/** The {@linkplain #XML_SPACE} attribute. */
	XML_SPACE("xml:space"),

	/** The {@linkplain #HREFLANG} attribute. */
	HREFLANG("hreflang"),

	/** The {@linkplain #CHECKED} attribute. */
	CHECKED("checked"),

	/** The {@linkplain #HR} attribute. */
	HR("hr"),

	/** The {@linkplain #ONKEYPRESS} attribute. */
	ONKEYPRESS("onkeypress"),

	/** The {@linkplain #LABEL} attribute. */
	LABEL("label"),

	/** The {@linkplain #CLASS} attribute. */
	CLASS("class"),

	/** The {@linkplain #SHAPE} attribute. */
	SHAPE("shape"),

	/** The {@linkplain #DL} attribute. */
	DL("dl"),

	/** The {@linkplain #KBD} attribute. */
	KBD("kbd"),

	/** The {@linkplain #ACCESSKEY} attribute. */
	ACCESSKEY("accesskey"),

	/** The {@linkplain #DISABLED} attribute. */
	DISABLED("disabled"),

	/** The {@linkplain #SCROLLING} attribute. */
	SCROLLING("scrolling"),

	/** The {@linkplain #DT} attribute. */
	DT("dt"),

	/** The {@linkplain #PRE} attribute. */
	PRE("pre"),

	/** The {@linkplain #RULES} attribute. */
	RULES("rules"),

	/** The {@linkplain #ROWS} attribute. */
	ROWS("rows"),

	/** The {@linkplain #CENTER} attribute. */
	CENTER("center"),

	/** The {@linkplain #ALINK} attribute. */
	ALINK("alink"),

	/** The {@linkplain #ONFOCUS} attribute. */
	ONFOCUS("onfocus"),

	/** The {@linkplain #COLSPAN} attribute. */
	COLSPAN("colspan"),

	/** The {@linkplain #SAMP} attribute. */
	SAMP("samp"),

	/** The {@linkplain #COL} attribute. */
	COL("col"),

	/** The {@linkplain #CITE} attribute. */
	CITE("cite"),

	/** The {@linkplain #MARGINHEIGHT} attribute. */
	MARGINHEIGHT("marginheight"),

	/** The {@linkplain #MAXLENGTH} attribute. */
	MAXLENGTH("maxlength"),

	/** The {@linkplain #LINK} attribute. */
	LINK("link"),

	/** The {@linkplain #ONSELECT} attribute. */
	ONSELECT("onselect"),

	/** The {@linkplain #SCRIPT} attribute. */
	SCRIPT("script"),

	/** The {@linkplain #ARCHIVE} attribute. */
	ARCHIVE("archive"),

	/** The {@linkplain #BDO} attribute. */
	BDO("bdo"),

	/** The {@linkplain #CLASSID} attribute. */
	CLASSID("classid"),

	/** The {@linkplain #LONGDESC} attribute. */
	LONGDESC("longdesc"),

	/** The {@linkplain #MENU} attribute. */
	MENU("menu"),

	/** The {@linkplain #COLGROUP} attribute. */
	COLGROUP("colgroup"),

	/** The {@linkplain #XML_LANG} attribute. */
	XML_LANG("xml:lang"),

	/** The {@linkplain #H2} attribute. */
	H2("h2"),

	/** The {@linkplain #NOSHADE} attribute. */
	NOSHADE("noshade"),

	/** The {@linkplain #INS} attribute. */
	INS("ins"),

	/** The {@linkplain #P} attribute. */
	P("p"),

	/** The {@linkplain #HSPACE} attribute. */
	HSPACE("hspace"),

	/** The {@linkplain #SUB} attribute. */
	SUB("sub"),

	/** The {@linkplain #ACTION} attribute. */
	ACTION("action"),

	/** The {@linkplain #ONLOAD} attribute. */
	ONLOAD("onload"),

	/** The {@linkplain #FRAMESET} attribute. */
	FRAMESET("frameset"),

	/** The {@linkplain #SELECTED} attribute. */
	SELECTED("selected"),

	/** The {@linkplain #OPTGROUP} attribute. */
	OPTGROUP("optgroup"),

	/** New in HTML5. The {@linkplain #AUTOFOCUS} attribute. */
	AUTOFOCUS("autofocus"),

	/** New in HTML5. The {@linkplain #PLACEHOLDER} attribute. */
	PLACEHOLDER("placeholder");

	/** The "real" name of the attribute. */
	private final String name;

	/** Constructor. */
	private EHTMLAttribute(String name) {
		this.name = name;
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