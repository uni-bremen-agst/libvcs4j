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
 * This enum contains all CSS properties we are using. The rationale is to
 * include as many properties as possible, but to exclude those that are merely
 * a composition of other properties (such as 'font' which can be expressed
 * using 'font-face', 'font-size', etc.). Additionally with all the browser
 * specific extension and new standards the file is extended on a "as required"
 * basis. So the file should be never seen as complete or even "correct".
 * <p>
 * The first version of this file was based on the full property table of CSS2.1
 * (http://www.w3.org/TR/CSS21/propidx.html), preprocessed using emacs and
 * manually fine-tuned.
 * <p>
 * Manual additions (from CSS3) are found towards the end.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 0CA62488D2027AE84B79D4C2A8562DAF
 */
public enum ECSSProperty {

	/** CSS property azimuth. */
	AZIMUTH("azimuth"),

	/** CSS property background-attachment. */
	BACKGROUND_ATTACHMENT("background-attachment"),

	/** CSS property background-color. */
	BACKGROUND_COLOR("background-color"),

	/** CSS property background-image. */
	BACKGROUND_IMAGE("background-image"),

	/** CSS property background-position. */
	BACKGROUND_POSITION("background-position"),

	/** CSS property background-repeat . */
	BACKGROUND_REPEAT("background-repeat"),

	/** CSS property border-collapse . */
	BORDER_COLLAPSE("border-collapse"),

	/** CSS property border-spacing . */
	BORDER_SPACING("border-spacing"),

	/** CSS property border-top-color. */
	BORDER_TOP_COLOR("border-top-color"),

	/** CSS property border-right-color. */
	BORDER_RIGHT_COLOR("border-right-color"),

	/** CSS property border-bottom-color. */
	BORDER_BOTTOM_COLOR("border-bottom-color"),

	/** CSS property border-left-color. */
	BORDER_LEFT_COLOR("border-left-color"),

	/** CSS property border-top-style. */
	BORDER_TOP_STYLE("border-top-style"),

	/** CSS property border-right-style. */
	BORDER_RIGHT_STYLE("border-right-style"),

	/** CSS property border-bottom-style. */
	BORDER_BOTTOM_STYLE("border-bottom-style"),

	/** CSS property border-left-style. */
	BORDER_LEFT_STYLE("border-left-style"),

	/** CSS property border-top-width. */
	BORDER_TOP_WIDTH("border-top-width"),

	/** CSS property border-right-width. */
	BORDER_RIGHT_WIDTH("border-right-width"),

	/** CSS property border-bottom-width. */
	BORDER_BOTTOM_WIDTH("border-bottom-width"),

	/** CSS property border-left-width. */
	BORDER_LEFT_WIDTH("border-left-width"),

	/** CSS property bottom. */
	BOTTOM("bottom"),

	/** CSS property caption-side. */
	CAPTION_SIDE("caption-side"),

	/** CSS property clear . */
	CLEAR("clear"),

	/** CSS property clip. */
	CLIP("clip"),

	/** CSS property color. */
	COLOR("color"),

	/** CSS property content. */
	CONTENT("content"),

	/** CSS property counter-increment. */
	COUNTER_INCREMENT("counter-increment"),

	/** CSS property counter-reset . */
	COUNTER_RESET("counter-reset"),

	/** CSS property cue-after. */
	CUE_AFTER("cue-after"),

	/** CSS property cue-before. */
	CUE_BEFORE("cue-before"),

	/** CSS property cursor . */
	CURSOR("cursor"),

	/** CSS property direction. */
	DIRECTION("direction"),

	/** CSS property display . */
	DISPLAY("display"),

	/** CSS property elevation. */
	ELEVATION("elevation"),

	/** CSS property empty-cells. */
	EMPTY_CELLS("empty-cells"),

	/** CSS property float. */
	FLOAT("float"),

	/** CSS property font-family. */
	FONT_FAMILY("font-family"),

	/** CSS property font-size. */
	FONT_SIZE("font-size"),

	/** CSS property font-style. */
	FONT_STYLE("font-style"),

	/** CSS property font-variant. */
	FONT_VARIANT("font-variant"),

	/** CSS property font-weight. */
	FONT_WEIGHT("font-weight"),

	/** CSS property height. */
	HEIGHT("height"),

	/** CSS property left. */
	LEFT("left"),

	/** CSS property letter-spacing. */
	LETTER_SPACING("letter-spacing"),

	/** CSS property line-height. */
	LINE_HEIGHT("line-height"),

	/** CSS property list-style-image. */
	LIST_STYLE_IMAGE("list-style-image"),

	/** CSS property list-style-position. */
	LIST_STYLE_POSITION("list-style-position"),

	/** CSS property list-style-type. */
	LIST_STYLE_TYPE("list-style-type"),

	/** CSS property margin-right. */
	MARGIN_RIGHT("margin-right"),

	/** CSS property margin-left. */
	MARGIN_LEFT("margin-left"),

	/** CSS property margin-top. */
	MARGIN_TOP("margin-top"),

	/** CSS property margin-bottom. */
	MARGIN_BOTTOM("margin-bottom"),

	/** CSS property max-height. */
	MAX_HEIGHT("max-height"),

	/** CSS property max-width . */
	MAX_WIDTH("max-width"),

	/** CSS property min-height. */
	MIN_HEIGHT("min-height"),

	/** CSS property min-width . */
	MIN_WIDTH("min-width"),

	/** CSS property orphans. */
	ORPHANS("orphans"),

	/** CSS property outline-color. */
	OUTLINE_COLOR("outline-color"),

	/** CSS property outline-style. */
	OUTLINE_STYLE("outline-style"),

	/** CSS property outline-width. */
	OUTLINE_WIDTH("outline-width"),

	/** CSS property overflow. */
	OVERFLOW("overflow"),

	/** CSS property padding-top. */
	PADDING_TOP("padding-top"),

	/** CSS property padding-right. */
	PADDING_RIGHT("padding-right"),

	/** CSS property padding-bottom. */
	PADDING_BOTTOM("padding-bottom"),

	/** CSS property padding-left. */
	PADDING_LEFT("padding-left"),

	/** CSS property page-break-after. */
	PAGE_BREAK_AFTER("page-break-after"),

	/** CSS property page-break-before. */
	PAGE_BREAK_BEFORE("page-break-before"),

	/** CSS property page-break-inside. */
	PAGE_BREAK_INSIDE("page-break-inside"),

	/** CSS property pause-after. */
	PAUSE_AFTER("pause-after"),

	/** CSS property pause-before. */
	PAUSE_BEFORE("pause-before"),

	/** CSS property pitch-range. */
	PITCH_RANGE("pitch-range"),

	/** CSS property pitch . */
	PITCH("pitch"),

	/** CSS property play-during. */
	PLAY_DURING("play-during"),

	/** CSS property position. */
	POSITION("position"),

	/** CSS property quotes . */
	QUOTES("quotes"),

	/** CSS property richness. */
	RICHNESS("richness"),

	/** CSS property right . */
	RIGHT("right"),

	/** CSS property speak-header. */
	SPEAK_HEADER("speak-header"),

	/** CSS property speak-numeral. */
	SPEAK_NUMERAL("speak-numeral"),

	/** CSS property speak-punctuation. */
	SPEAK_PUNCTUATION("speak-punctuation"),

	/** CSS property speak. */
	SPEAK("speak"),

	/** CSS property speech-rate. */
	SPEECH_RATE("speech-rate"),

	/** CSS property stress . */
	STRESS("stress"),

	/** CSS property table-layout. */
	TABLE_LAYOUT("table-layout"),

	/** CSS property text-align . */
	TEXT_ALIGN("text-align"),

	/** CSS property text-decoration. */
	TEXT_DECORATION("text-decoration"),

	/** CSS property text-indent. */
	TEXT_INDENT("text-indent"),

	/** CSS property text-transform. */
	TEXT_TRANSFORM("text-transform"),

	/** CSS property top. */
	TOP("top"),

	/** CSS property unicode-bidi. */
	UNICODE_BIDI("unicode-bidi"),

	/** CSS property vertical-align. */
	VERTICAL_ALIGN("vertical-align"),

	/** CSS property visibility. */
	VISIBILITY("visibility"),

	/** CSS property voice-family. */
	VOICE_FAMILY("voice-family"),

	/** CSS property volume. */
	VOLUME("volume"),

	/** CSS property white-space. */
	WHITE_SPACE("white-space"),

	/** CSS property widows . */
	WIDOWS("widows"),

	/** CSS property width. */
	WIDTH("width"),

	/** CSS property word-spacing. */
	WORD_SPACING("word-spacing"),

	/** CSS property z-index. */
	Z_INDEX("z-index"),

	/** CSS property filter. */
	FILTER("filter"),

	/** CSS property opacity. */
	OPACITY("opacity"),

	/** New in CSS3. The {@linkplain #BORDER_RADIUS} attribute. */
	BORDER_RADIUS("border-radius");

	/** The real name of this property. */
	private final String name;

	/** Constructor. */
	private ECSSProperty(String name) {
		this.name = name;
	}

	/** Returns the "real" name of this property. */
	public String getName() {
		return name;
	}

	/** Returns the "real" name of this property. */
	@Override
	public String toString() {
		return name;
	}
}