

 
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
package org.conqat.lib.scanner;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableSet;

/**
 * This enumeration describes the type of a token. Each type belongs to a token
 * class (described by enumeration <code>TokenClass</code>).
 * <p>
 * NOTE: This class was automatically generated. DO NOT MODIFY.
 */
@SuppressWarnings("all")
public enum ETokenType {

			/** Token ASSEMBLER of class KEYWORD */
		ASSEMBLER(ETokenClass.KEYWORD),
			/** Token END_SEARCH of class KEYWORD */
		END_SEARCH(ETokenClass.KEYWORD),
			/** Token INFOTYPES of class KEYWORD */
		INFOTYPES(ETokenClass.KEYWORD),
			/** Token PROGRAM_POINTER of class KEYWORD */
		PROGRAM_POINTER(ETokenClass.KEYWORD),
			/** Token NOT_KEYWORD of class KEYWORD */
		NOT_KEYWORD(ETokenClass.KEYWORD),
			/** Token SPMD of class KEYWORD */
		SPMD(ETokenClass.KEYWORD),
			/** Token USER_COMMAND of class KEYWORD */
		USER_COMMAND(ETokenClass.KEYWORD),
			/** Token APV of class KEYWORD */
		APV(ETokenClass.KEYWORD),
			/** Token CHECKPOINT of class KEYWORD */
		CHECKPOINT(ETokenClass.KEYWORD),
			/** Token RECORDS of class KEYWORD */
		RECORDS(ETokenClass.KEYWORD),
			/** Token COMPRESSION of class KEYWORD */
		COMPRESSION(ETokenClass.KEYWORD),
			/** Token CDATE of class KEYWORD */
		CDATE(ETokenClass.KEYWORD),
			/** Token SCATTER of class KEYWORD */
		SCATTER(ETokenClass.KEYWORD),
			/** Token FINAL of class KEYWORD */
		FINAL(ETokenClass.KEYWORD),
			/** Token ARC of class KEYWORD */
		ARC(ETokenClass.KEYWORD),
			/** Token RPAREN of class DELIMITER */
		RPAREN(ETokenClass.DELIMITER),
			/** Token SUPPRESS of class KEYWORD */
		SUPPRESS(ETokenClass.KEYWORD),
			/** Token ARE of class KEYWORD */
		ARE(ETokenClass.KEYWORD),
			/** Token BSM of class KEYWORD */
		BSM(ETokenClass.KEYWORD),
			/** Token OCIDURATION of class KEYWORD */
		OCIDURATION(ETokenClass.KEYWORD),
			/** Token TIMEZONE_MINUTE of class KEYWORD */
		TIMEZONE_MINUTE(ETokenClass.KEYWORD),
			/** Token OFF of class KEYWORD */
		OFF(ETokenClass.KEYWORD),
			/** Token LOOP of class KEYWORD */
		LOOP(ETokenClass.KEYWORD),
			/** Token WRITETEXT of class KEYWORD */
		WRITETEXT(ETokenClass.KEYWORD),
			/** Token GET of class KEYWORD */
		GET(ETokenClass.KEYWORD),
			/** Token SPARSE of class KEYWORD */
		SPARSE(ETokenClass.KEYWORD),
			/** Token FOOTING of class KEYWORD */
		FOOTING(ETokenClass.KEYWORD),
			/** Token SEGMENT of class KEYWORD */
		SEGMENT(ETokenClass.KEYWORD),
			/** Token CSHORT of class KEYWORD */
		CSHORT(ETokenClass.KEYWORD),
			/** Token SHADOWS of class KEYWORD */
		SHADOWS(ETokenClass.KEYWORD),
			/** Token D of class KEYWORD */
		D(ETokenClass.KEYWORD),
			/** Token E of class KEYWORD */
		E(ETokenClass.KEYWORD),
			/** Token F of class KEYWORD */
		F(ETokenClass.KEYWORD),
			/** Token RETURNCODE of class KEYWORD */
		RETURNCODE(ETokenClass.KEYWORD),
			/** Token G of class KEYWORD */
		G(ETokenClass.KEYWORD),
			/** Token NONLOCAL of class KEYWORD */
		NONLOCAL(ETokenClass.KEYWORD),
			/** Token A of class KEYWORD */
		A(ETokenClass.KEYWORD),
			/** Token B of class KEYWORD */
		B(ETokenClass.KEYWORD),
			/** Token OCIINTERVAL of class KEYWORD */
		OCIINTERVAL(ETokenClass.KEYWORD),
			/** Token ASC of class KEYWORD */
		ASC(ETokenClass.KEYWORD),
			/** Token C of class KEYWORD */
		C(ETokenClass.KEYWORD),
			/** Token OCISTRING of class KEYWORD */
		OCISTRING(ETokenClass.KEYWORD),
			/** Token POOL of class KEYWORD */
		POOL(ETokenClass.KEYWORD),
			/** Token M of class KEYWORD */
		M(ETokenClass.KEYWORD),
			/** Token STATIC of class KEYWORD */
		STATIC(ETokenClass.KEYWORD),
			/** Token END_START of class KEYWORD */
		END_START(ETokenClass.KEYWORD),
			/** Token N of class KEYWORD */
		N(ETokenClass.KEYWORD),
			/** Token LSHIFT of class OPERATOR */
		LSHIFT(ETokenClass.OPERATOR),
			/** Token O of class OPERATOR */
		O(ETokenClass.OPERATOR),
			/** Token TEMPLATE_TEXT of class SPECIAL */
		TEMPLATE_TEXT(ETokenClass.SPECIAL),
			/** Token DIVIDE_CORRESPONDING of class KEYWORD */
		DIVIDE_CORRESPONDING(ETokenClass.KEYWORD),
			/** Token DELEGATE of class KEYWORD */
		DELEGATE(ETokenClass.KEYWORD),
			/** Token ASM of class KEYWORD */
		ASM(ETokenClass.KEYWORD),
			/** Token I of class KEYWORD */
		I(ETokenClass.KEYWORD),
			/** Token U of class KEYWORD */
		U(ETokenClass.KEYWORD),
			/** Token NOZERODIVIDE of class KEYWORD */
		NOZERODIVIDE(ETokenClass.KEYWORD),
			/** Token PROCESSING of class KEYWORD */
		PROCESSING(ETokenClass.KEYWORD),
			/** Token V of class KEYWORD */
		V(ETokenClass.KEYWORD),
			/** Token P of class KEYWORD */
		P(ETokenClass.KEYWORD),
			/** Token OCCURRENCE of class KEYWORD */
		OCCURRENCE(ETokenClass.KEYWORD),
			/** Token R of class KEYWORD */
		R(ETokenClass.KEYWORD),
			/** Token FILE of class KEYWORD */
		FILE(ETokenClass.KEYWORD),
			/** Token ACCORDING of class KEYWORD */
		ACCORDING(ETokenClass.KEYWORD),
			/** Token INVALID of class KEYWORD */
		INVALID(ETokenClass.KEYWORD),
			/** Token X of class KEYWORD */
		X(ETokenClass.KEYWORD),
			/** Token Z of class OPERATOR */
		Z(ETokenClass.OPERATOR),
			/** Token LIMITS of class KEYWORD */
		LIMITS(ETokenClass.KEYWORD),
			/** Token TABBED of class KEYWORD */
		TABBED(ETokenClass.KEYWORD),
			/** Token FLOOR_DIVEQ of class OPERATOR */
		FLOOR_DIVEQ(ETokenClass.OPERATOR),
			/** Token KEYLENGTH of class KEYWORD */
		KEYLENGTH(ETokenClass.KEYWORD),
			/** Token DEPENDING of class KEYWORD */
		DEPENDING(ETokenClass.KEYWORD),
			/** Token USES of class KEYWORD */
		USES(ETokenClass.KEYWORD),
			/** Token SUBSCREEN of class KEYWORD */
		SUBSCREEN(ETokenClass.KEYWORD),
			/** Token USER of class KEYWORD */
		USER(ETokenClass.KEYWORD),
			/** Token ALIAS of class KEYWORD */
		ALIAS(ETokenClass.KEYWORD),
			/** Token FROMALIEN of class KEYWORD */
		FROMALIEN(ETokenClass.KEYWORD),
			/** Token BINARY_LONG of class KEYWORD */
		BINARY_LONG(ETokenClass.KEYWORD),
			/** Token END_DISPLAY of class KEYWORD */
		END_DISPLAY(ETokenClass.KEYWORD),
			/** Token SMART of class KEYWORD */
		SMART(ETokenClass.KEYWORD),
			/** Token TIMEZONE_HOUR of class KEYWORD */
		TIMEZONE_HOUR(ETokenClass.KEYWORD),
			/** Token LBRACK of class DELIMITER */
		LBRACK(ETokenClass.DELIMITER),
			/** Token CURRENT_USER of class KEYWORD */
		CURRENT_USER(ETokenClass.KEYWORD),
			/** Token PREPROCESSOR_PROCESS of class KEYWORD */
		PREPROCESSOR_PROCESS(ETokenClass.KEYWORD),
			/** Token LBRACE of class DELIMITER */
		LBRACE(ETokenClass.DELIMITER),
			/** Token LINES of class KEYWORD */
		LINES(ETokenClass.KEYWORD),
			/** Token FIND of class KEYWORD */
		FIND(ETokenClass.KEYWORD),
			/** Token DOCUMENTATION_COMMENT of class COMMENT */
		DOCUMENTATION_COMMENT(ETokenClass.COMMENT),
			/** Token ORDERBY of class KEYWORD */
		ORDERBY(ETokenClass.KEYWORD),
			/** Token PERFORMING of class KEYWORD */
		PERFORMING(ETokenClass.KEYWORD),
			/** Token LINE_SIZE of class KEYWORD */
		LINE_SIZE(ETokenClass.KEYWORD),
			/** Token RESUMBALE of class KEYWORD */
		RESUMBALE(ETokenClass.KEYWORD),
			/** Token CLOB_BASE of class KEYWORD */
		CLOB_BASE(ETokenClass.KEYWORD),
			/** Token COLOR of class KEYWORD */
		COLOR(ETokenClass.KEYWORD),
			/** Token SINGLE_QUOTE of class DELIMITER */
		SINGLE_QUOTE(ETokenClass.DELIMITER),
			/** Token GAPS of class KEYWORD */
		GAPS(ETokenClass.KEYWORD),
			/** Token INTERVALS of class KEYWORD */
		INTERVALS(ETokenClass.KEYWORD),
			/** Token AT_DATE of class OPERATOR */
		AT_DATE(ETokenClass.OPERATOR),
			/** Token MATRIX_MULT of class OPERATOR */
		MATRIX_MULT(ETokenClass.OPERATOR),
			/** Token DANGEROUS of class KEYWORD */
		DANGEROUS(ETokenClass.KEYWORD),
			/** Token AVG of class KEYWORD */
		AVG(ETokenClass.KEYWORD),
			/** Token TXT of class KEYWORD */
		TXT(ETokenClass.KEYWORD),
			/** Token DUMP of class KEYWORD */
		DUMP(ETokenClass.KEYWORD),
			/** Token TXW of class KEYWORD */
		TXW(ETokenClass.KEYWORD),
			/** Token FRAMES of class KEYWORD */
		FRAMES(ETokenClass.KEYWORD),
			/** Token DEFINITION of class KEYWORD */
		DEFINITION(ETokenClass.KEYWORD),
			/** Token TXL of class KEYWORD */
		TXL(ETokenClass.KEYWORD),
			/** Token RETURNS of class KEYWORD */
		RETURNS(ETokenClass.KEYWORD),
			/** Token VARRAY of class KEYWORD */
		VARRAY(ETokenClass.KEYWORD),
			/** Token OCCURRENCES of class KEYWORD */
		OCCURRENCES(ETokenClass.KEYWORD),
			/** Token SERIALIZABLE of class KEYWORD */
		SERIALIZABLE(ETokenClass.KEYWORD),
			/** Token REDEFINITION of class KEYWORD */
		REDEFINITION(ETokenClass.KEYWORD),
			/** Token OCIDATE of class KEYWORD */
		OCIDATE(ETokenClass.KEYWORD),
			/** Token EQEQEQ of class OPERATOR */
		EQEQEQ(ETokenClass.OPERATOR),
			/** Token RIGHT of class KEYWORD */
		RIGHT(ETokenClass.KEYWORD),
			/** Token NOWRITE of class KEYWORD */
		NOWRITE(ETokenClass.KEYWORD),
			/** Token VSAM of class KEYWORD */
		VSAM(ETokenClass.KEYWORD),
			/** Token THIS_THREAD of class KEYWORD */
		THIS_THREAD(ETokenClass.KEYWORD),
			/** Token SUPPLY of class KEYWORD */
		SUPPLY(ETokenClass.KEYWORD),
			/** Token COLON of class OPERATOR */
		COLON(ETokenClass.OPERATOR),
			/** Token COLUMNS of class KEYWORD */
		COLUMNS(ETokenClass.KEYWORD),
			/** Token NOTEQ of class OPERATOR */
		NOTEQ(ETokenClass.OPERATOR),
			/** Token DEBUG_ITEM of class KEYWORD */
		DEBUG_ITEM(ETokenClass.KEYWORD),
			/** Token OMITTED of class KEYWORD */
		OMITTED(ETokenClass.KEYWORD),
			/** Token END_WITH of class KEYWORD */
		END_WITH(ETokenClass.KEYWORD),
			/** Token DETACH of class KEYWORD */
		DETACH(ETokenClass.KEYWORD),
			/** Token DISPLAY_MODE of class KEYWORD */
		DISPLAY_MODE(ETokenClass.KEYWORD),
			/** Token WITHEVENTS of class KEYWORD */
		WITHEVENTS(ETokenClass.KEYWORD),
			/** Token ENTER of class KEYWORD */
		ENTER(ETokenClass.KEYWORD),
			/** Token ROWS of class KEYWORD */
		ROWS(ETokenClass.KEYWORD),
			/** Token LINE_SELECTION of class KEYWORD */
		LINE_SELECTION(ETokenClass.KEYWORD),
			/** Token DIRECTORY of class KEYWORD */
		DIRECTORY(ETokenClass.KEYWORD),
			/** Token ARROW of class OPERATOR */
		ARROW(ETokenClass.OPERATOR),
			/** Token AT_OPERATOR of class OPERATOR */
		AT_OPERATOR(ETokenClass.OPERATOR),
			/** Token INTERFACE of class KEYWORD */
		INTERFACE(ETokenClass.KEYWORD),
			/** Token REPORTING of class KEYWORD */
		REPORTING(ETokenClass.KEYWORD),
			/** Token PARAMETER_SEPARATOR of class DELIMITER */
		PARAMETER_SEPARATOR(ETokenClass.DELIMITER),
			/** Token LONG of class KEYWORD */
		LONG(ETokenClass.KEYWORD),
			/** Token OPTIONS of class KEYWORD */
		OPTIONS(ETokenClass.KEYWORD),
			/** Token ENVIRONMENT of class KEYWORD */
		ENVIRONMENT(ETokenClass.KEYWORD),
			/** Token NO_GROUPING of class KEYWORD */
		NO_GROUPING(ETokenClass.KEYWORD),
			/** Token CTL360 of class KEYWORD */
		CTL360(ETokenClass.KEYWORD),
			/** Token JNIENVPTR of class KEYWORD */
		JNIENVPTR(ETokenClass.KEYWORD),
			/** Token RESET of class KEYWORD */
		RESET(ETokenClass.KEYWORD),
			/** Token TRAN of class KEYWORD */
		TRAN(ETokenClass.KEYWORD),
			/** Token HELP_ID of class KEYWORD */
		HELP_ID(ETokenClass.KEYWORD),
			/** Token END_ENUM of class KEYWORD */
		END_ENUM(ETokenClass.KEYWORD),
			/** Token MODIFIER of class KEYWORD */
		MODIFIER(ETokenClass.KEYWORD),
			/** Token PASS of class KEYWORD */
		PASS(ETokenClass.KEYWORD),
			/** Token CODE of class KEYWORD */
		CODE(ETokenClass.KEYWORD),
			/** Token DOWNTO of class KEYWORD */
		DOWNTO(ETokenClass.KEYWORD),
			/** Token OVERFLOW of class KEYWORD */
		OVERFLOW(ETokenClass.KEYWORD),
			/** Token BIT_OR of class OPERATOR */
		BIT_OR(ETokenClass.OPERATOR),
			/** Token SYMMETRIC_DIFFERENCE of class KEYWORD */
		SYMMETRIC_DIFFERENCE(ETokenClass.KEYWORD),
			/** Token AREAS of class KEYWORD */
		AREAS(ETokenClass.KEYWORD),
			/** Token BYPASSING of class KEYWORD */
		BYPASSING(ETokenClass.KEYWORD),
			/** Token NOTOVERRIDABLE of class KEYWORD */
		NOTOVERRIDABLE(ETokenClass.KEYWORD),
			/** Token GENERATE of class KEYWORD */
		GENERATE(ETokenClass.KEYWORD),
			/** Token COMMENT of class KEYWORD */
		COMMENT(ETokenClass.KEYWORD),
			/** Token ARRAY of class KEYWORD */
		ARRAY(ETokenClass.KEYWORD),
			/** Token BOUNDARIES of class KEYWORD */
		BOUNDARIES(ETokenClass.KEYWORD),
			/** Token LITTLEENDIAN of class KEYWORD */
		LITTLEENDIAN(ETokenClass.KEYWORD),
			/** Token SCROLLING of class KEYWORD */
		SCROLLING(ETokenClass.KEYWORD),
			/** Token COBJ of class KEYWORD */
		COBJ(ETokenClass.KEYWORD),
			/** Token ENDPAGE of class KEYWORD */
		ENDPAGE(ETokenClass.KEYWORD),
			/** Token HANDLE of class KEYWORD */
		HANDLE(ETokenClass.KEYWORD),
			/** Token SECTION of class KEYWORD */
		SECTION(ETokenClass.KEYWORD),
			/** Token DOUBLE_QUESTION of class OPERATOR */
		DOUBLE_QUESTION(ETokenClass.OPERATOR),
			/** Token ALPHABETIC_LOWER of class KEYWORD */
		ALPHABETIC_LOWER(ETokenClass.KEYWORD),
			/** Token ICON of class KEYWORD */
		ICON(ETokenClass.KEYWORD),
			/** Token LINE_COUNTER of class KEYWORD */
		LINE_COUNTER(ETokenClass.KEYWORD),
			/** Token LINAGE of class KEYWORD */
		LINAGE(ETokenClass.KEYWORD),
			/** Token VALUE_REQUEST of class KEYWORD */
		VALUE_REQUEST(ETokenClass.KEYWORD),
			/** Token BACKUP of class KEYWORD */
		BACKUP(ETokenClass.KEYWORD),
			/** Token NO_TITLE of class KEYWORD */
		NO_TITLE(ETokenClass.KEYWORD),
			/** Token SORT_RETURN of class KEYWORD */
		SORT_RETURN(ETokenClass.KEYWORD),
			/** Token UNIT of class KEYWORD */
		UNIT(ETokenClass.KEYWORD),
			/** Token UCT of class KEYWORD */
		UCT(ETokenClass.KEYWORD),
			/** Token EXIT_DO of class KEYWORD */
		EXIT_DO(ETokenClass.KEYWORD),
			/** Token CLASS_LITERAL of class LITERAL */
		CLASS_LITERAL(ETokenClass.LITERAL),
			/** Token REMAINDER of class KEYWORD */
		REMAINDER(ETokenClass.KEYWORD),
			/** Token SHIFT_IN of class KEYWORD */
		SHIFT_IN(ETokenClass.KEYWORD),
			/** Token NATIONAL of class KEYWORD */
		NATIONAL(ETokenClass.KEYWORD),
			/** Token SUMMING of class KEYWORD */
		SUMMING(ETokenClass.KEYWORD),
			/** Token UNIX of class KEYWORD */
		UNIX(ETokenClass.KEYWORD),
			/** Token STDDEV of class KEYWORD */
		STDDEV(ETokenClass.KEYWORD),
			/** Token INTENSIFIED of class KEYWORD */
		INTENSIFIED(ETokenClass.KEYWORD),
			/** Token INVERTED_DATE of class KEYWORD */
		INVERTED_DATE(ETokenClass.KEYWORD),
			/** Token ASCII of class KEYWORD */
		ASCII(ETokenClass.KEYWORD),
			/** Token FIELD_SYMBOL of class KEYWORD */
		FIELD_SYMBOL(ETokenClass.KEYWORD),
			/** Token PROVIDE of class KEYWORD */
		PROVIDE(ETokenClass.KEYWORD),
			/** Token MULTIPLY_CORRESPONDING of class KEYWORD */
		MULTIPLY_CORRESPONDING(ETokenClass.KEYWORD),
			/** Token UNALIGNED of class KEYWORD */
		UNALIGNED(ETokenClass.KEYWORD),
			/** Token NULLIF of class KEYWORD */
		NULLIF(ETokenClass.KEYWORD),
			/** Token OCL of class KEYWORD */
		OCL(ETokenClass.KEYWORD),
			/** Token RECEIVING of class KEYWORD */
		RECEIVING(ETokenClass.KEYWORD),
			/** Token CLASS_METHODS of class KEYWORD */
		CLASS_METHODS(ETokenClass.KEYWORD),
			/** Token TABSTRIP of class KEYWORD */
		TABSTRIP(ETokenClass.KEYWORD),
			/** Token FUNCTION_POINTER of class KEYWORD */
		FUNCTION_POINTER(ETokenClass.KEYWORD),
			/** Token SPLIT of class KEYWORD */
		SPLIT(ETokenClass.KEYWORD),
			/** Token DEMAND of class KEYWORD */
		DEMAND(ETokenClass.KEYWORD),
			/** Token CAA of class KEYWORD */
		CAA(ETokenClass.KEYWORD),
			/** Token NUMERIC_EDITED of class KEYWORD */
		NUMERIC_EDITED(ETokenClass.KEYWORD),
			/** Token DEFERRED of class KEYWORD */
		DEFERRED(ETokenClass.KEYWORD),
			/** Token BACKTICK_STRING_LITERAL of class LITERAL */
		BACKTICK_STRING_LITERAL(ETokenClass.LITERAL),
			/** Token FILTER_TABLE of class KEYWORD */
		FILTER_TABLE(ETokenClass.KEYWORD),
			/** Token DEDENT of class SYNTHETIC */
		DEDENT(ETokenClass.SYNTHETIC),
			/** Token SYSTEM_DEFAULT of class KEYWORD */
		SYSTEM_DEFAULT(ETokenClass.KEYWORD),
			/** Token ABSTRACT of class KEYWORD */
		ABSTRACT(ETokenClass.KEYWORD),
			/** Token RESOURCE of class KEYWORD */
		RESOURCE(ETokenClass.KEYWORD),
			/** Token MULTIPLE of class KEYWORD */
		MULTIPLE(ETokenClass.KEYWORD),
			/** Token FIXEDOVERFLOW of class KEYWORD */
		FIXEDOVERFLOW(ETokenClass.KEYWORD),
			/** Token BACKGROUND of class KEYWORD */
		BACKGROUND(ETokenClass.KEYWORD),
			/** Token REDECLARED of class KEYWORD */
		REDECLARED(ETokenClass.KEYWORD),
			/** Token PARAMARRAY of class KEYWORD */
		PARAMARRAY(ETokenClass.KEYWORD),
			/** Token UB4 of class KEYWORD */
		UB4(ETokenClass.KEYWORD),
			/** Token DEBUG_LINE of class KEYWORD */
		DEBUG_LINE(ETokenClass.KEYWORD),
			/** Token MULTIPLY of class KEYWORD */
		MULTIPLY(ETokenClass.KEYWORD),
			/** Token REFERENCES of class KEYWORD */
		REFERENCES(ETokenClass.KEYWORD),
			/** Token UB2 of class KEYWORD */
		UB2(ETokenClass.KEYWORD),
			/** Token PARTITION of class KEYWORD */
		PARTITION(ETokenClass.KEYWORD),
			/** Token UB1 of class KEYWORD */
		UB1(ETokenClass.KEYWORD),
			/** Token EQUAL of class KEYWORD */
		EQUAL(ETokenClass.KEYWORD),
			/** Token ENDMODULE of class KEYWORD */
		ENDMODULE(ETokenClass.KEYWORD),
			/** Token NEXT of class KEYWORD */
		NEXT(ETokenClass.KEYWORD),
			/** Token EXACT of class KEYWORD */
		EXACT(ETokenClass.KEYWORD),
			/** Token NORMAL of class KEYWORD */
		NORMAL(ETokenClass.KEYWORD),
			/** Token WITH of class KEYWORD */
		WITH(ETokenClass.KEYWORD),
			/** Token BIGENDIAN of class KEYWORD */
		BIGENDIAN(ETokenClass.KEYWORD),
			/** Token STATUS of class KEYWORD */
		STATUS(ETokenClass.KEYWORD),
			/** Token BACKWARD of class KEYWORD */
		BACKWARD(ETokenClass.KEYWORD),
			/** Token UNTERMINATED_CHARACTER_LITERAL of class ERROR */
		UNTERMINATED_CHARACTER_LITERAL(ETokenClass.ERROR),
			/** Token SYNCLOCK of class KEYWORD */
		SYNCLOCK(ETokenClass.KEYWORD),
			/** Token INSTALLATION of class KEYWORD */
		INSTALLATION(ETokenClass.KEYWORD),
			/** Token SORT_FILE_SIZE of class KEYWORD */
		SORT_FILE_SIZE(ETokenClass.KEYWORD),
			/** Token WRITE of class KEYWORD */
		WRITE(ETokenClass.KEYWORD),
			/** Token LAYOUT of class KEYWORD */
		LAYOUT(ETokenClass.KEYWORD),
			/** Token TOP_OF_PAGE of class KEYWORD */
		TOP_OF_PAGE(ETokenClass.KEYWORD),
			/** Token PROGRAM_ID of class KEYWORD */
		PROGRAM_ID(ETokenClass.KEYWORD),
			/** Token QUESTION of class OPERATOR */
		QUESTION(ETokenClass.OPERATOR),
			/** Token ADDRESS of class KEYWORD */
		ADDRESS(ETokenClass.KEYWORD),
			/** Token CBL of class KEYWORD */
		CBL(ETokenClass.KEYWORD),
			/** Token TEMPLATE of class KEYWORD */
		TEMPLATE(ETokenClass.KEYWORD),
			/** Token BUFFOFF of class KEYWORD */
		BUFFOFF(ETokenClass.KEYWORD),
			/** Token OVERRIDE of class KEYWORD */
		OVERRIDE(ETokenClass.KEYWORD),
			/** Token ERRORS of class KEYWORD */
		ERRORS(ETokenClass.KEYWORD),
			/** Token FLOAT_EXTENDED of class KEYWORD */
		FLOAT_EXTENDED(ETokenClass.KEYWORD),
			/** Token VARIANCE of class KEYWORD */
		VARIANCE(ETokenClass.KEYWORD),
			/** Token WINMAIN of class KEYWORD */
		WINMAIN(ETokenClass.KEYWORD),
			/** Token AUTHOR of class KEYWORD */
		AUTHOR(ETokenClass.KEYWORD),
			/** Token MEDIUM of class KEYWORD */
		MEDIUM(ETokenClass.KEYWORD),
			/** Token INSTANCES of class KEYWORD */
		INSTANCES(ETokenClass.KEYWORD),
			/** Token PLAINTEXT of class KEYWORD */
		PLAINTEXT(ETokenClass.KEYWORD),
			/** Token SOFTFLOAT of class KEYWORD */
		SOFTFLOAT(ETokenClass.KEYWORD),
			/** Token CLONE of class KEYWORD */
		CLONE(ETokenClass.KEYWORD),
			/** Token REMOVE of class KEYWORD */
		REMOVE(ETokenClass.KEYWORD),
			/** Token TESTING of class KEYWORD */
		TESTING(ETokenClass.KEYWORD),
			/** Token ENDAT of class KEYWORD */
		ENDAT(ETokenClass.KEYWORD),
			/** Token EDITOR_CALL of class KEYWORD */
		EDITOR_CALL(ETokenClass.KEYWORD),
			/** Token OPENROWSET of class KEYWORD */
		OPENROWSET(ETokenClass.KEYWORD),
			/** Token DOUBLE_DOT of class OPERATOR */
		DOUBLE_DOT(ETokenClass.OPERATOR),
			/** Token CASE_DEFAULT of class KEYWORD */
		CASE_DEFAULT(ETokenClass.KEYWORD),
			/** Token TYPE of class KEYWORD */
		TYPE(ETokenClass.KEYWORD),
			/** Token IS_FLOAT of class OPERATOR */
		IS_FLOAT(ETokenClass.OPERATOR),
			/** Token OCILOBLOCATOR of class KEYWORD */
		OCILOBLOCATOR(ETokenClass.KEYWORD),
			/** Token FRM of class KEYWORD */
		FRM(ETokenClass.KEYWORD),
			/** Token BEGIN of class KEYWORD */
		BEGIN(ETokenClass.KEYWORD),
			/** Token VALID of class KEYWORD */
		VALID(ETokenClass.KEYWORD),
			/** Token PREPROCESSOR_XINSCAN of class KEYWORD */
		PREPROCESSOR_XINSCAN(ETokenClass.KEYWORD),
			/** Token ENABLED of class KEYWORD */
		ENABLED(ETokenClass.KEYWORD),
			/** Token BCF of class KEYWORD */
		BCF(ETokenClass.KEYWORD),
			/** Token COLS of class KEYWORD */
		COLS(ETokenClass.KEYWORD),
			/** Token ATTRIBUTE_INDICATOR of class OPERATOR */
		ATTRIBUTE_INDICATOR(ETokenClass.OPERATOR),
			/** Token ORADATA of class KEYWORD */
		ORADATA(ETokenClass.KEYWORD),
			/** Token READ_ONLY of class KEYWORD */
		READ_ONLY(ETokenClass.KEYWORD),
			/** Token PLACEHOLDER of class IDENTIFIER */
		PLACEHOLDER(ETokenClass.IDENTIFIER),
			/** Token ORLVARY of class KEYWORD */
		ORLVARY(ETokenClass.KEYWORD),
			/** Token UNBUFFERED of class KEYWORD */
		UNBUFFERED(ETokenClass.KEYWORD),
			/** Token KEY of class KEYWORD */
		KEY(ETokenClass.KEYWORD),
			/** Token DEFINED of class KEYWORD */
		DEFINED(ETokenClass.KEYWORD),
			/** Token ADA of class KEYWORD */
		ADA(ETokenClass.KEYWORD),
			/** Token LOAD of class KEYWORD */
		LOAD(ETokenClass.KEYWORD),
			/** Token TRAILING of class KEYWORD */
		TRAILING(ETokenClass.KEYWORD),
			/** Token NULL_LITERAL of class LITERAL */
		NULL_LITERAL(ETokenClass.LITERAL),
			/** Token WAIT of class KEYWORD */
		WAIT(ETokenClass.KEYWORD),
			/** Token RESULTS of class KEYWORD */
		RESULTS(ETokenClass.KEYWORD),
			/** Token BEGINNING of class KEYWORD */
		BEGINNING(ETokenClass.KEYWORD),
			/** Token THREADVAR of class KEYWORD */
		THREADVAR(ETokenClass.KEYWORD),
			/** Token ABS of class KEYWORD */
		ABS(ETokenClass.KEYWORD),
			/** Token FTN of class KEYWORD */
		FTN(ETokenClass.KEYWORD),
			/** Token PLAN of class KEYWORD */
		PLAN(ETokenClass.KEYWORD),
			/** Token OUT of class KEYWORD */
		OUT(ETokenClass.KEYWORD),
			/** Token END_OF_SELECTION of class KEYWORD */
		END_OF_SELECTION(ETokenClass.KEYWORD),
			/** Token FURTHER of class KEYWORD */
		FURTHER(ETokenClass.KEYWORD),
			/** Token ASSIGNED of class KEYWORD */
		ASSIGNED(ETokenClass.KEYWORD),
			/** Token PRINT_CONTROL of class KEYWORD */
		PRINT_CONTROL(ETokenClass.KEYWORD),
			/** Token FUNCTIONALITY of class KEYWORD */
		FUNCTIONALITY(ETokenClass.KEYWORD),
			/** Token SUPPLIED of class KEYWORD */
		SUPPLIED(ETokenClass.KEYWORD),
			/** Token FRIENDS of class KEYWORD */
		FRIENDS(ETokenClass.KEYWORD),
			/** Token REVERSE of class KEYWORD */
		REVERSE(ETokenClass.KEYWORD),
			/** Token DYNAMIC of class KEYWORD */
		DYNAMIC(ETokenClass.KEYWORD),
			/** Token CLASS_ID of class KEYWORD */
		CLASS_ID(ETokenClass.KEYWORD),
			/** Token UNLOCK of class KEYWORD */
		UNLOCK(ETokenClass.KEYWORD),
			/** Token MINUSEQ of class OPERATOR */
		MINUSEQ(ETokenClass.OPERATOR),
			/** Token REREAD of class KEYWORD */
		REREAD(ETokenClass.KEYWORD),
			/** Token EDIT of class KEYWORD */
		EDIT(ETokenClass.KEYWORD),
			/** Token ATTRIBUTES of class KEYWORD */
		ATTRIBUTES(ETokenClass.KEYWORD),
			/** Token IDENTITY of class KEYWORD */
		IDENTITY(ETokenClass.KEYWORD),
			/** Token REFER of class KEYWORD */
		REFER(ETokenClass.KEYWORD),
			/** Token THE of class KEYWORD */
		THE(ETokenClass.KEYWORD),
			/** Token PLUSPLUS of class OPERATOR */
		PLUSPLUS(ETokenClass.OPERATOR),
			/** Token NO_WAY of class KEYWORD */
		NO_WAY(ETokenClass.KEYWORD),
			/** Token ANNOTATION_INTERFACE of class KEYWORD */
		ANNOTATION_INTERFACE(ETokenClass.KEYWORD),
			/** Token RECORDING of class KEYWORD */
		RECORDING(ETokenClass.KEYWORD),
			/** Token OCCURS of class KEYWORD */
		OCCURS(ETokenClass.KEYWORD),
			/** Token RANDOM of class KEYWORD */
		RANDOM(ETokenClass.KEYWORD),
			/** Token DOUBLE_ARROW of class OPERATOR */
		DOUBLE_ARROW(ETokenClass.OPERATOR),
			/** Token INTERPOLATIONSTART of class SPECIAL */
		INTERPOLATIONSTART(ETokenClass.SPECIAL),
			/** Token WRITEONLY of class KEYWORD */
		WRITEONLY(ETokenClass.KEYWORD),
			/** Token ANDAND of class OPERATOR */
		ANDAND(ETokenClass.OPERATOR),
			/** Token PAGE_COUNTER of class KEYWORD */
		PAGE_COUNTER(ETokenClass.KEYWORD),
			/** Token TIME of class KEYWORD */
		TIME(ETokenClass.KEYWORD),
			/** Token END_DELETE of class KEYWORD */
		END_DELETE(ETokenClass.KEYWORD),
			/** Token IMPLEMENTS of class KEYWORD */
		IMPLEMENTS(ETokenClass.KEYWORD),
			/** Token END_TYPE of class KEYWORD */
		END_TYPE(ETokenClass.KEYWORD),
			/** Token REJECT of class KEYWORD */
		REJECT(ETokenClass.KEYWORD),
			/** Token XML_NTEXT of class KEYWORD */
		XML_NTEXT(ETokenClass.KEYWORD),
			/** Token OCINUMBER of class KEYWORD */
		OCINUMBER(ETokenClass.KEYWORD),
			/** Token SIGNAL of class KEYWORD */
		SIGNAL(ETokenClass.KEYWORD),
			/** Token INSTANTIABLE of class KEYWORD */
		INSTANTIABLE(ETokenClass.KEYWORD),
			/** Token ENDDO of class KEYWORD */
		ENDDO(ETokenClass.KEYWORD),
			/** Token STANDARD of class KEYWORD */
		STANDARD(ETokenClass.KEYWORD),
			/** Token FWD of class KEYWORD */
		FWD(ETokenClass.KEYWORD),
			/** Token ADD of class KEYWORD */
		ADD(ETokenClass.KEYWORD),
			/** Token INITIALIZATION of class KEYWORD */
		INITIALIZATION(ETokenClass.KEYWORD),
			/** Token INTEGER of class KEYWORD */
		INTEGER(ETokenClass.KEYWORD),
			/** Token STATICS of class KEYWORD */
		STATICS(ETokenClass.KEYWORD),
			/** Token OVERLOADS of class KEYWORD */
		OVERLOADS(ETokenClass.KEYWORD),
			/** Token EXPONENTIATIONEQ of class OPERATOR */
		EXPONENTIATIONEQ(ETokenClass.OPERATOR),
			/** Token UNASSIGN of class KEYWORD */
		UNASSIGN(ETokenClass.KEYWORD),
			/** Token OVERLAPS of class KEYWORD */
		OVERLAPS(ETokenClass.KEYWORD),
			/** Token VISIBLE of class KEYWORD */
		VISIBLE(ETokenClass.KEYWORD),
			/** Token KEYTO of class KEYWORD */
		KEYTO(ETokenClass.KEYWORD),
			/** Token RELATIVE of class KEYWORD */
		RELATIVE(ETokenClass.KEYWORD),
			/** Token TEXT of class KEYWORD */
		TEXT(ETokenClass.KEYWORD),
			/** Token IGNORE of class KEYWORD */
		IGNORE(ETokenClass.KEYWORD),
			/** Token HOUR of class KEYWORD */
		HOUR(ETokenClass.KEYWORD),
			/** Token CONVERSION of class KEYWORD */
		CONVERSION(ETokenClass.KEYWORD),
			/** Token PREPROCESSOR_INSCAN of class KEYWORD */
		PREPROCESSOR_INSCAN(ETokenClass.KEYWORD),
			/** Token BOOLEAN_LITERAL of class LITERAL */
		BOOLEAN_LITERAL(ETokenClass.LITERAL),
			/** Token NON_QUICK of class KEYWORD */
		NON_QUICK(ETokenClass.KEYWORD),
			/** Token DECIMAL of class KEYWORD */
		DECIMAL(ETokenClass.KEYWORD),
			/** Token CLOSE of class KEYWORD */
		CLOSE(ETokenClass.KEYWORD),
			/** Token CHECKED of class KEYWORD */
		CHECKED(ETokenClass.KEYWORD),
			/** Token STACKALLOC of class KEYWORD */
		STACKALLOC(ETokenClass.KEYWORD),
			/** Token TABLEVIEW of class KEYWORD */
		TABLEVIEW(ETokenClass.KEYWORD),
			/** Token BIT of class KEYWORD */
		BIT(ETokenClass.KEYWORD),
			/** Token BULK of class KEYWORD */
		BULK(ETokenClass.KEYWORD),
			/** Token FIRST of class KEYWORD */
		FIRST(ETokenClass.KEYWORD),
			/** Token BIG of class KEYWORD */
		BIG(ETokenClass.KEYWORD),
			/** Token WEND of class KEYWORD */
		WEND(ETokenClass.KEYWORD),
			/** Token INTERNAL of class KEYWORD */
		INTERNAL(ETokenClass.KEYWORD),
			/** Token SIGNED of class KEYWORD */
		SIGNED(ETokenClass.KEYWORD),
			/** Token EXPONENT of class KEYWORD */
		EXPONENT(ETokenClass.KEYWORD),
			/** Token NONASSIGNABLE of class KEYWORD */
		NONASSIGNABLE(ETokenClass.KEYWORD),
			/** Token WHILE of class KEYWORD */
		WHILE(ETokenClass.KEYWORD),
			/** Token DETERMINISTIC of class KEYWORD */
		DETERMINISTIC(ETokenClass.KEYWORD),
			/** Token SCREEN of class KEYWORD */
		SCREEN(ETokenClass.KEYWORD),
			/** Token VARYING of class KEYWORD */
		VARYING(ETokenClass.KEYWORD),
			/** Token CSTR of class KEYWORD */
		CSTR(ETokenClass.KEYWORD),
			/** Token QUEUE_ONLY of class KEYWORD */
		QUEUE_ONLY(ETokenClass.KEYWORD),
			/** Token TRIGGER of class KEYWORD */
		TRIGGER(ETokenClass.KEYWORD),
			/** Token ENCODING of class KEYWORD */
		ENCODING(ETokenClass.KEYWORD),
			/** Token MINUSMINUS of class OPERATOR */
		MINUSMINUS(ETokenClass.OPERATOR),
			/** Token TIMEZONE_ABBR of class KEYWORD */
		TIMEZONE_ABBR(ETokenClass.KEYWORD),
			/** Token I_O_CONTROL of class KEYWORD */
		I_O_CONTROL(ETokenClass.KEYWORD),
			/** Token PACKED_DECIMAL of class KEYWORD */
		PACKED_DECIMAL(ETokenClass.KEYWORD),
			/** Token INSERT of class KEYWORD */
		INSERT(ETokenClass.KEYWORD),
			/** Token CARET of class KEYWORD */
		CARET(ETokenClass.KEYWORD),
			/** Token PARAMETER of class KEYWORD */
		PARAMETER(ETokenClass.KEYWORD),
			/** Token ENDIF of class KEYWORD */
		ENDIF(ETokenClass.KEYWORD),
			/** Token ENDWHILE of class KEYWORD */
		ENDWHILE(ETokenClass.KEYWORD),
			/** Token UNSAFE of class KEYWORD */
		UNSAFE(ETokenClass.KEYWORD),
			/** Token ENDSWITCH of class KEYWORD */
		ENDSWITCH(ETokenClass.KEYWORD),
			/** Token REPOSITORY of class KEYWORD */
		REPOSITORY(ETokenClass.KEYWORD),
			/** Token NEAR of class KEYWORD */
		NEAR(ETokenClass.KEYWORD),
			/** Token TYPENAME of class KEYWORD */
		TYPENAME(ETokenClass.KEYWORD),
			/** Token UNIQUE of class KEYWORD */
		UNIQUE(ETokenClass.KEYWORD),
			/** Token RESTORE of class KEYWORD */
		RESTORE(ETokenClass.KEYWORD),
			/** Token UNWIND of class KEYWORD */
		UNWIND(ETokenClass.KEYWORD),
			/** Token VIEW of class KEYWORD */
		VIEW(ETokenClass.KEYWORD),
			/** Token MATRIX_LEFT_DIV of class OPERATOR */
		MATRIX_LEFT_DIV(ETokenClass.OPERATOR),
			/** Token EXPORTING of class KEYWORD */
		EXPORTING(ETokenClass.KEYWORD),
			/** Token TRT of class KEYWORD */
		TRT(ETokenClass.KEYWORD),
			/** Token READER of class KEYWORD */
		READER(ETokenClass.KEYWORD),
			/** Token TASK of class KEYWORD */
		TASK(ETokenClass.KEYWORD),
			/** Token TRANSACTIONAL of class KEYWORD */
		TRANSACTIONAL(ETokenClass.KEYWORD),
			/** Token BUFSP of class KEYWORD */
		BUFSP(ETokenClass.KEYWORD),
			/** Token TRY of class KEYWORD */
		TRY(ETokenClass.KEYWORD),
			/** Token ALI of class KEYWORD */
		ALI(ETokenClass.KEYWORD),
			/** Token PASSWORD of class KEYWORD */
		PASSWORD(ETokenClass.KEYWORD),
			/** Token SAVING of class KEYWORD */
		SAVING(ETokenClass.KEYWORD),
			/** Token UNTERMINATED_REGEX_LITERAL of class ERROR */
		UNTERMINATED_REGEX_LITERAL(ETokenClass.ERROR),
			/** Token AUTHID of class KEYWORD */
		AUTHID(ETokenClass.KEYWORD),
			/** Token OPENQUERY of class KEYWORD */
		OPENQUERY(ETokenClass.KEYWORD),
			/** Token PROPERTY of class KEYWORD */
		PROPERTY(ETokenClass.KEYWORD),
			/** Token UNTIL of class KEYWORD */
		UNTIL(ETokenClass.KEYWORD),
			/** Token NEW_PAGE of class KEYWORD */
		NEW_PAGE(ETokenClass.KEYWORD),
			/** Token IF_ZERO_BLOCK of class COMMENT */
		IF_ZERO_BLOCK(ETokenClass.COMMENT),
			/** Token TEST of class KEYWORD */
		TEST(ETokenClass.KEYWORD),
			/** Token TSP of class KEYWORD */
		TSP(ETokenClass.KEYWORD),
			/** Token TSTACK of class KEYWORD */
		TSTACK(ETokenClass.KEYWORD),
			/** Token FLG of class KEYWORD */
		FLG(ETokenClass.KEYWORD),
			/** Token WRITE_ONLY of class KEYWORD */
		WRITE_ONLY(ETokenClass.KEYWORD),
			/** Token MARKUP of class SPECIAL */
		MARKUP(ETokenClass.SPECIAL),
			/** Token CORR of class KEYWORD */
		CORR(ETokenClass.KEYWORD),
			/** Token INVALIDATE of class KEYWORD */
		INVALIDATE(ETokenClass.KEYWORD),
			/** Token CLASS of class KEYWORD */
		CLASS(ETokenClass.KEYWORD),
			/** Token CONTROLS of class KEYWORD */
		CONTROLS(ETokenClass.KEYWORD),
			/** Token VARARGS of class KEYWORD */
		VARARGS(ETokenClass.KEYWORD),
			/** Token OVERLAY of class KEYWORD */
		OVERLAY(ETokenClass.KEYWORD),
			/** Token YYYY of class KEYWORD */
		YYYY(ETokenClass.KEYWORD),
			/** Token NOEXECOPS of class KEYWORD */
		NOEXECOPS(ETokenClass.KEYWORD),
			/** Token ORN of class KEYWORD */
		ORN(ETokenClass.KEYWORD),
			/** Token SQLSTATE of class KEYWORD */
		SQLSTATE(ETokenClass.KEYWORD),
			/** Token FOR of class KEYWORD */
		FOR(ETokenClass.KEYWORD),
			/** Token DISTRIBUTED of class KEYWORD */
		DISTRIBUTED(ETokenClass.KEYWORD),
			/** Token OFFSETS of class KEYWORD */
		OFFSETS(ETokenClass.KEYWORD),
			/** Token CONFIGURATION of class KEYWORD */
		CONFIGURATION(ETokenClass.KEYWORD),
			/** Token HOLD of class KEYWORD */
		HOLD(ETokenClass.KEYWORD),
			/** Token MATRIX_RIGHT_DIV of class OPERATOR */
		MATRIX_RIGHT_DIV(ETokenClass.OPERATOR),
			/** Token AND of class OPERATOR */
		AND(ETokenClass.OPERATOR),
			/** Token IDENTIFIED of class KEYWORD */
		IDENTIFIED(ETokenClass.KEYWORD),
			/** Token LOCK of class KEYWORD */
		LOCK(ETokenClass.KEYWORD),
			/** Token FON of class KEYWORD */
		FON(ETokenClass.KEYWORD),
			/** Token WIDECHAR of class KEYWORD */
		WIDECHAR(ETokenClass.KEYWORD),
			/** Token DATASET of class KEYWORD */
		DATASET(ETokenClass.KEYWORD),
			/** Token LEFT_DIV of class OPERATOR */
		LEFT_DIV(ETokenClass.OPERATOR),
			/** Token UNDER of class KEYWORD */
		UNDER(ETokenClass.KEYWORD),
			/** Token BOX of class DELIMITER */
		BOX(ETokenClass.DELIMITER),
			/** Token END_FUNCTION of class KEYWORD */
		END_FUNCTION(ETokenClass.KEYWORD),
			/** Token CONTINUE of class KEYWORD */
		CONTINUE(ETokenClass.KEYWORD),
			/** Token OBJECT of class KEYWORD */
		OBJECT(ETokenClass.KEYWORD),
			/** Token COPY of class KEYWORD */
		COPY(ETokenClass.KEYWORD),
			/** Token IDENTIFIER of class IDENTIFIER */
		IDENTIFIER(ETokenClass.IDENTIFIER),
			/** Token OCIDATETIME of class KEYWORD */
		OCIDATETIME(ETokenClass.KEYWORD),
			/** Token XPL of class KEYWORD */
		XPL(ETokenClass.KEYWORD),
			/** Token VALIDATE_STATUS of class KEYWORD */
		VALIDATE_STATUS(ETokenClass.KEYWORD),
			/** Token ALL of class KEYWORD */
		ALL(ETokenClass.KEYWORD),
			/** Token BOT of class KEYWORD */
		BOT(ETokenClass.KEYWORD),
			/** Token PRINTING of class KEYWORD */
		PRINTING(ETokenClass.KEYWORD),
			/** Token UNDEF of class KEYWORD */
		UNDEF(ETokenClass.KEYWORD),
			/** Token RELOAD of class KEYWORD */
		RELOAD(ETokenClass.KEYWORD),
			/** Token PARTIAL of class KEYWORD */
		PARTIAL(ETokenClass.KEYWORD),
			/** Token ENDENHANCEMENT of class KEYWORD */
		ENDENHANCEMENT(ETokenClass.KEYWORD),
			/** Token ISOLATION of class KEYWORD */
		ISOLATION(ETokenClass.KEYWORD),
			/** Token CSNG of class KEYWORD */
		CSNG(ETokenClass.KEYWORD),
			/** Token RAISEEVENT of class KEYWORD */
		RAISEEVENT(ETokenClass.KEYWORD),
			/** Token BYTE of class KEYWORD */
		BYTE(ETokenClass.KEYWORD),
			/** Token XOR of class OPERATOR */
		XOR(ETokenClass.OPERATOR),
			/** Token NOINVALIDOP of class KEYWORD */
		NOINVALIDOP(ETokenClass.KEYWORD),
			/** Token OTHER of class KEYWORD */
		OTHER(ETokenClass.KEYWORD),
			/** Token TITLE_LINES of class KEYWORD */
		TITLE_LINES(ETokenClass.KEYWORD),
			/** Token TEXTSIZE of class KEYWORD */
		TEXTSIZE(ETokenClass.KEYWORD),
			/** Token VALUES of class KEYWORD */
		VALUES(ETokenClass.KEYWORD),
			/** Token HASH of class KEYWORD */
		HASH(ETokenClass.KEYWORD),
			/** Token SECURITYAUDIT of class KEYWORD */
		SECURITYAUDIT(ETokenClass.KEYWORD),
			/** Token COMP of class OPERATOR */
		COMP(ETokenClass.OPERATOR),
			/** Token GTEQ of class OPERATOR */
		GTEQ(ETokenClass.OPERATOR),
			/** Token COMPLEX_TRANSPOSE of class OPERATOR */
		COMPLEX_TRANSPOSE(ETokenClass.OPERATOR),
			/** Token INVALID_DEDENT of class ERROR */
		INVALID_DEDENT(ETokenClass.ERROR),
			/** Token END_PROTECT of class KEYWORD */
		END_PROTECT(ETokenClass.KEYWORD),
			/** Token BOOT_ASSIGN of class OPERATOR */
		BOOT_ASSIGN(ETokenClass.OPERATOR),
			/** Token CORRESPONDING of class KEYWORD */
		CORRESPONDING(ETokenClass.KEYWORD),
			/** Token ANY of class KEYWORD */
		ANY(ETokenClass.KEYWORD),
			/** Token ABBREVIATED of class KEYWORD */
		ABBREVIATED(ETokenClass.KEYWORD),
			/** Token ENDTRY of class KEYWORD */
		ENDTRY(ETokenClass.KEYWORD),
			/** Token BUFND of class KEYWORD */
		BUFND(ETokenClass.KEYWORD),
			/** Token ANN of class KEYWORD */
		ANN(ETokenClass.KEYWORD),
			/** Token TAGGED of class KEYWORD */
		TAGGED(ETokenClass.KEYWORD),
			/** Token LSHIFTEQ of class OPERATOR */
		LSHIFTEQ(ETokenClass.OPERATOR),
			/** Token BUFNI of class KEYWORD */
		BUFNI(ETokenClass.KEYWORD),
			/** Token BINARY of class KEYWORD */
		BINARY(ETokenClass.KEYWORD),
			/** Token KEYS of class KEYWORD */
		KEYS(ETokenClass.KEYWORD),
			/** Token TIMES of class KEYWORD */
		TIMES(ETokenClass.KEYWORD),
			/** Token TOP of class KEYWORD */
		TOP(ETokenClass.KEYWORD),
			/** Token TAPE of class KEYWORD */
		TAPE(ETokenClass.KEYWORD),
			/** Token METHOD of class KEYWORD */
		METHOD(ETokenClass.KEYWORD),
			/** Token ENDON of class KEYWORD */
		ENDON(ETokenClass.KEYWORD),
			/** Token MULTISET of class KEYWORD */
		MULTISET(ETokenClass.KEYWORD),
			/** Token FREETEXT of class KEYWORD */
		FREETEXT(ETokenClass.KEYWORD),
			/** Token END_ACCEPT of class KEYWORD */
		END_ACCEPT(ETokenClass.KEYWORD),
			/** Token BYREF of class KEYWORD */
		BYREF(ETokenClass.KEYWORD),
			/** Token GROUP_USAGE of class KEYWORD */
		GROUP_USAGE(ETokenClass.KEYWORD),
			/** Token FIXED of class KEYWORD */
		FIXED(ETokenClass.KEYWORD),
			/** Token CLUSTERS of class KEYWORD */
		CLUSTERS(ETokenClass.KEYWORD),
			/** Token SYN of class KEYWORD */
		SYN(ETokenClass.KEYWORD),
			/** Token NODESCRIPTOR of class KEYWORD */
		NODESCRIPTOR(ETokenClass.KEYWORD),
			/** Token COMPLEX of class KEYWORD */
		COMPLEX(ETokenClass.KEYWORD),
			/** Token EXCEPT of class KEYWORD */
		EXCEPT(ETokenClass.KEYWORD),
			/** Token KILL of class KEYWORD */
		KILL(ETokenClass.KEYWORD),
			/** Token END_SELECT of class KEYWORD */
		END_SELECT(ETokenClass.KEYWORD),
			/** Token PGM of class KEYWORD */
		PGM(ETokenClass.KEYWORD),
			/** Token DOUBLE_STAR of class OPERATOR */
		DOUBLE_STAR(ETokenClass.OPERATOR),
			/** Token IMAGINARY_LITERAL of class LITERAL */
		IMAGINARY_LITERAL(ETokenClass.LITERAL),
			/** Token DISTANCE of class KEYWORD */
		DISTANCE(ETokenClass.KEYWORD),
			/** Token MYBASE of class KEYWORD */
		MYBASE(ETokenClass.KEYWORD),
			/** Token BYTE_CS of class OPERATOR */
		BYTE_CS(ETokenClass.OPERATOR),
			/** Token WORDS of class KEYWORD */
		WORDS(ETokenClass.KEYWORD),
			/** Token BYTE_CN of class OPERATOR */
		BYTE_CN(ETokenClass.OPERATOR),
			/** Token BYTE_CO of class OPERATOR */
		BYTE_CO(ETokenClass.OPERATOR),
			/** Token ELEMENT of class KEYWORD */
		ELEMENT(ETokenClass.KEYWORD),
			/** Token SUPPORT of class KEYWORD */
		SUPPORT(ETokenClass.KEYWORD),
			/** Token SEND of class KEYWORD */
		SEND(ETokenClass.KEYWORD),
			/** Token PFR of class KEYWORD */
		PFR(ETokenClass.KEYWORD),
			/** Token NON_UNIQUE of class KEYWORD */
		NON_UNIQUE(ETokenClass.KEYWORD),
			/** Token END_OF_LINE_COMMENT of class COMMENT */
		END_OF_LINE_COMMENT(ETokenClass.COMMENT),
			/** Token BOUND of class KEYWORD */
		BOUND(ETokenClass.KEYWORD),
			/** Token SHARED of class KEYWORD */
		SHARED(ETokenClass.KEYWORD),
			/** Token CVR of class KEYWORD */
		CVR(ETokenClass.KEYWORD),
			/** Token PIC of class KEYWORD */
		PIC(ETokenClass.KEYWORD),
			/** Token SYMBOL of class KEYWORD */
		SYMBOL(ETokenClass.KEYWORD),
			/** Token DESTINATION of class KEYWORD */
		DESTINATION(ETokenClass.KEYWORD),
			/** Token DEBUG_NAME of class KEYWORD */
		DEBUG_NAME(ETokenClass.KEYWORD),
			/** Token PRESERVE of class KEYWORD */
		PRESERVE(ETokenClass.KEYWORD),
			/** Token GOSUB of class KEYWORD */
		GOSUB(ETokenClass.KEYWORD),
			/** Token REPORTS of class KEYWORD */
		REPORTS(ETokenClass.KEYWORD),
			/** Token COBOL of class KEYWORD */
		COBOL(ETokenClass.KEYWORD),
			/** Token REFRESH of class KEYWORD */
		REFRESH(ETokenClass.KEYWORD),
			/** Token BLANK of class KEYWORD */
		BLANK(ETokenClass.KEYWORD),
			/** Token LOCATOR_HANDLE of class OPERATOR */
		LOCATOR_HANDLE(ETokenClass.OPERATOR),
			/** Token DATE_WRITTEN of class KEYWORD */
		DATE_WRITTEN(ETokenClass.KEYWORD),
			/** Token WORKING_STORAGE of class KEYWORD */
		WORKING_STORAGE(ETokenClass.KEYWORD),
			/** Token CHARGRAPHIC of class KEYWORD */
		CHARGRAPHIC(ETokenClass.KEYWORD),
			/** Token REORDER of class KEYWORD */
		REORDER(ETokenClass.KEYWORD),
			/** Token PERCENTAGE of class KEYWORD */
		PERCENTAGE(ETokenClass.KEYWORD),
			/** Token WRITER of class KEYWORD */
		WRITER(ETokenClass.KEYWORD),
			/** Token ENDIAN of class KEYWORD */
		ENDIAN(ETokenClass.KEYWORD),
			/** Token ADDBUFF of class KEYWORD */
		ADDBUFF(ETokenClass.KEYWORD),
			/** Token GENERAL of class KEYWORD */
		GENERAL(ETokenClass.KEYWORD),
			/** Token INTERSECT of class KEYWORD */
		INTERSECT(ETokenClass.KEYWORD),
			/** Token FETCH of class KEYWORD */
		FETCH(ETokenClass.KEYWORD),
			/** Token CDEC of class KEYWORD */
		CDEC(ETokenClass.KEYWORD),
			/** Token ALSO of class KEYWORD */
		ALSO(ETokenClass.KEYWORD),
			/** Token WIDTH of class KEYWORD */
		WIDTH(ETokenClass.KEYWORD),
			/** Token DESCRIBE of class KEYWORD */
		DESCRIBE(ETokenClass.KEYWORD),
			/** Token CONSTANT of class KEYWORD */
		CONSTANT(ETokenClass.KEYWORD),
			/** Token SEARCH of class KEYWORD */
		SEARCH(ETokenClass.KEYWORD),
			/** Token SYSTEM_USER of class KEYWORD */
		SYSTEM_USER(ETokenClass.KEYWORD),
			/** Token OBJECT_COMPUTER of class KEYWORD */
		OBJECT_COMPUTER(ETokenClass.KEYWORD),
			/** Token SELECT_OPTIONS of class KEYWORD */
		SELECT_OPTIONS(ETokenClass.KEYWORD),
			/** Token CDECL of class KEYWORD */
		CDECL(ETokenClass.KEYWORD),
			/** Token XML of class KEYWORD */
		XML(ETokenClass.KEYWORD),
			/** Token SERIAL of class KEYWORD */
		SERIAL(ETokenClass.KEYWORD),
			/** Token MODIFY of class KEYWORD */
		MODIFY(ETokenClass.KEYWORD),
			/** Token COMPONENTS of class KEYWORD */
		COMPONENTS(ETokenClass.KEYWORD),
			/** Token OBLIGATORY of class KEYWORD */
		OBLIGATORY(ETokenClass.KEYWORD),
			/** Token TABLE of class KEYWORD */
		TABLE(ETokenClass.KEYWORD),
			/** Token SOURCE of class KEYWORD */
		SOURCE(ETokenClass.KEYWORD),
			/** Token SUB of class KEYWORD */
		SUB(ETokenClass.KEYWORD),
			/** Token SESSION_USER of class KEYWORD */
		SESSION_USER(ETokenClass.KEYWORD),
			/** Token VERSION of class KEYWORD */
		VERSION(ETokenClass.KEYWORD),
			/** Token FOREVER of class KEYWORD */
		FOREVER(ETokenClass.KEYWORD),
			/** Token SUM of class KEYWORD */
		SUM(ETokenClass.KEYWORD),
			/** Token RETURN_CODE of class KEYWORD */
		RETURN_CODE(ETokenClass.KEYWORD),
			/** Token INDENT of class SYNTHETIC */
		INDENT(ETokenClass.SYNTHETIC),
			/** Token DEBUG of class KEYWORD */
		DEBUG(ETokenClass.KEYWORD),
			/** Token STORAGE of class KEYWORD */
		STORAGE(ETokenClass.KEYWORD),
			/** Token STR of class KEYWORD */
		STR(ETokenClass.KEYWORD),
			/** Token SAMPLE of class KEYWORD */
		SAMPLE(ETokenClass.KEYWORD),
			/** Token DECIMAL_POINT of class KEYWORD */
		DECIMAL_POINT(ETokenClass.KEYWORD),
			/** Token MULTILINE_COMMENT of class COMMENT */
		MULTILINE_COMMENT(ETokenClass.COMMENT),
			/** Token BFILE_BASE of class KEYWORD */
		BFILE_BASE(ETokenClass.KEYWORD),
			/** Token ACCEPTING of class KEYWORD */
		ACCEPTING(ETokenClass.KEYWORD),
			/** Token RECSIZE of class KEYWORD */
		RECSIZE(ETokenClass.KEYWORD),
			/** Token CONNECTED of class KEYWORD */
		CONNECTED(ETokenClass.KEYWORD),
			/** Token ASYNC of class KEYWORD */
		ASYNC(ETokenClass.KEYWORD),
			/** Token PREPROCESSOR_DIRECTIVE of class SPECIAL */
		PREPROCESSOR_DIRECTIVE(ETokenClass.SPECIAL),
			/** Token LEFT of class KEYWORD */
		LEFT(ETokenClass.KEYWORD),
			/** Token YIELD of class KEYWORD */
		YIELD(ETokenClass.KEYWORD),
			/** Token AUTO of class KEYWORD */
		AUTO(ETokenClass.KEYWORD),
			/** Token PLUS of class OPERATOR */
		PLUS(ETokenClass.OPERATOR),
			/** Token STF of class KEYWORD */
		STF(ETokenClass.KEYWORD),
			/** Token MODULES of class KEYWORD */
		MODULES(ETokenClass.KEYWORD),
			/** Token FORM of class KEYWORD */
		FORM(ETokenClass.KEYWORD),
			/** Token STG of class KEYWORD */
		STG(ETokenClass.KEYWORD),
			/** Token STK of class KEYWORD */
		STK(ETokenClass.KEYWORD),
			/** Token ARRAY_SEPARATOR of class OPERATOR */
		ARRAY_SEPARATOR(ETokenClass.OPERATOR),
			/** Token CLOCK of class KEYWORD */
		CLOCK(ETokenClass.KEYWORD),
			/** Token CDBL of class KEYWORD */
		CDBL(ETokenClass.KEYWORD),
			/** Token COLLATE of class KEYWORD */
		COLLATE(ETokenClass.KEYWORD),
			/** Token OUTER of class KEYWORD */
		OUTER(ETokenClass.KEYWORD),
			/** Token MARK of class KEYWORD */
		MARK(ETokenClass.KEYWORD),
			/** Token LOCKING of class KEYWORD */
		LOCKING(ETokenClass.KEYWORD),
			/** Token REMOVEHANDLER of class KEYWORD */
		REMOVEHANDLER(ETokenClass.KEYWORD),
			/** Token TYPEDEF of class KEYWORD */
		TYPEDEF(ETokenClass.KEYWORD),
			/** Token NATIONAL_EDITED of class KEYWORD */
		NATIONAL_EDITED(ETokenClass.KEYWORD),
			/** Token HAVING of class KEYWORD */
		HAVING(ETokenClass.KEYWORD),
			/** Token PROTOTYPE of class KEYWORD */
		PROTOTYPE(ETokenClass.KEYWORD),
			/** Token AUTHORITY_CHECK of class KEYWORD */
		AUTHORITY_CHECK(ETokenClass.KEYWORD),
			/** Token KIND of class KEYWORD */
		KIND(ETokenClass.KEYWORD),
			/** Token ITERATE of class KEYWORD */
		ITERATE(ETokenClass.KEYWORD),
			/** Token LEFT_ANGLE_BRACKET of class DELIMITER */
		LEFT_ANGLE_BRACKET(ETokenClass.DELIMITER),
			/** Token MASK of class KEYWORD */
		MASK(ETokenClass.KEYWORD),
			/** Token EXIT_COMMAND of class KEYWORD */
		EXIT_COMMAND(ETokenClass.KEYWORD),
			/** Token SQLCODE of class KEYWORD */
		SQLCODE(ETokenClass.KEYWORD),
			/** Token BLOCK of class KEYWORD */
		BLOCK(ETokenClass.KEYWORD),
			/** Token YYMMDD of class KEYWORD */
		YYMMDD(ETokenClass.KEYWORD),
			/** Token LARGE of class KEYWORD */
		LARGE(ETokenClass.KEYWORD),
			/** Token DELETING of class KEYWORD */
		DELETING(ETokenClass.KEYWORD),
			/** Token DESCENDING of class KEYWORD */
		DESCENDING(ETokenClass.KEYWORD),
			/** Token SELF of class KEYWORD */
		SELF(ETokenClass.KEYWORD),
			/** Token TITLE of class KEYWORD */
		TITLE(ETokenClass.KEYWORD),
			/** Token SUMMARY of class KEYWORD */
		SUMMARY(ETokenClass.KEYWORD),
			/** Token OLDFPCCALL of class KEYWORD */
		OLDFPCCALL(ETokenClass.KEYWORD),
			/** Token RIGHT_JUSTIFIED of class KEYWORD */
		RIGHT_JUSTIFIED(ETokenClass.KEYWORD),
			/** Token GENERIC of class KEYWORD */
		GENERIC(ETokenClass.KEYWORD),
			/** Token UNDERFLOW of class KEYWORD */
		UNDERFLOW(ETokenClass.KEYWORD),
			/** Token REDEFINES of class KEYWORD */
		REDEFINES(ETokenClass.KEYWORD),
			/** Token RELIES_ON of class KEYWORD */
		RELIES_ON(ETokenClass.KEYWORD),
			/** Token TRACE of class KEYWORD */
		TRACE(ETokenClass.KEYWORD),
			/** Token END_ENHANCEMENT_SECTION of class KEYWORD */
		END_ENHANCEMENT_SECTION(ETokenClass.KEYWORD),
			/** Token DAY of class KEYWORD */
		DAY(ETokenClass.KEYWORD),
			/** Token BASED_FLOATING_POINT_LITERAL of class LITERAL */
		BASED_FLOATING_POINT_LITERAL(ETokenClass.LITERAL),
			/** Token CANCEL of class KEYWORD */
		CANCEL(ETokenClass.KEYWORD),
			/** Token EXTENDED of class KEYWORD */
		EXTENDED(ETokenClass.KEYWORD),
			/** Token QUOTE of class KEYWORD */
		QUOTE(ETokenClass.KEYWORD),
			/** Token CONNECT of class KEYWORD */
		CONNECT(ETokenClass.KEYWORD),
			/** Token INHERITS of class KEYWORD */
		INHERITS(ETokenClass.KEYWORD),
			/** Token ENUMERATION of class KEYWORD */
		ENUMERATION(ETokenClass.KEYWORD),
			/** Token OBJECTS of class KEYWORD */
		OBJECTS(ETokenClass.KEYWORD),
			/** Token FRIEND of class KEYWORD */
		FRIEND(ETokenClass.KEYWORD),
			/** Token QUEUE of class KEYWORD */
		QUEUE(ETokenClass.KEYWORD),
			/** Token LESS of class KEYWORD */
		LESS(ETokenClass.KEYWORD),
			/** Token DOUBLE of class KEYWORD */
		DOUBLE(ETokenClass.KEYWORD),
			/** Token CONSECUTIVE of class KEYWORD */
		CONSECUTIVE(ETokenClass.KEYWORD),
			/** Token LINKAGE of class KEYWORD */
		LINKAGE(ETokenClass.KEYWORD),
			/** Token AT_SUM of class OPERATOR */
		AT_SUM(ETokenClass.OPERATOR),
			/** Token PAN of class KEYWORD */
		PAN(ETokenClass.KEYWORD),
			/** Token SECOND of class KEYWORD */
		SECOND(ETokenClass.KEYWORD),
			/** Token INITIALIZE of class KEYWORD */
		INITIALIZE(ETokenClass.KEYWORD),
			/** Token PAG of class KEYWORD */
		PAG(ETokenClass.KEYWORD),
			/** Token VAL_STATUS of class KEYWORD */
		VAL_STATUS(ETokenClass.KEYWORD),
			/** Token NUMERIC of class KEYWORD */
		NUMERIC(ETokenClass.KEYWORD),
			/** Token ENDDECLARE of class KEYWORD */
		ENDDECLARE(ETokenClass.KEYWORD),
			/** Token EGCS of class KEYWORD */
		EGCS(ETokenClass.KEYWORD),
			/** Token NOSTRINGRANGE of class KEYWORD */
		NOSTRINGRANGE(ETokenClass.KEYWORD),
			/** Token LAB of class KEYWORD */
		LAB(ETokenClass.KEYWORD),
			/** Token SPECIAL_NAMES of class KEYWORD */
		SPECIAL_NAMES(ETokenClass.KEYWORD),
			/** Token SEPARATED of class KEYWORD */
		SEPARATED(ETokenClass.KEYWORD),
			/** Token USHORT of class KEYWORD */
		USHORT(ETokenClass.KEYWORD),
			/** Token DEBUG_SUB_1 of class KEYWORD */
		DEBUG_SUB_1(ETokenClass.KEYWORD),
			/** Token DEBUG_SUB_2 of class KEYWORD */
		DEBUG_SUB_2(ETokenClass.KEYWORD),
			/** Token TCW of class KEYWORD */
		TCW(ETokenClass.KEYWORD),
			/** Token DEBUG_SUB_3 of class KEYWORD */
		DEBUG_SUB_3(ETokenClass.KEYWORD),
			/** Token SUBROUTINE of class KEYWORD */
		SUBROUTINE(ETokenClass.KEYWORD),
			/** Token TDO of class KEYWORD */
		TDO(ETokenClass.KEYWORD),
			/** Token NOWDOC of class LITERAL */
		NOWDOC(ETokenClass.LITERAL),
			/** Token DEBUGGER of class KEYWORD */
		DEBUGGER(ETokenClass.KEYWORD),
			/** Token NOINIT of class KEYWORD */
		NOINIT(ETokenClass.KEYWORD),
			/** Token ANDALSO of class KEYWORD */
		ANDALSO(ETokenClass.KEYWORD),
			/** Token CURRENT_DATE of class KEYWORD */
		CURRENT_DATE(ETokenClass.KEYWORD),
			/** Token TEXTPOOL of class KEYWORD */
		TEXTPOOL(ETokenClass.KEYWORD),
			/** Token WHERE of class KEYWORD */
		WHERE(ETokenClass.KEYWORD),
			/** Token DEC of class KEYWORD */
		DEC(ETokenClass.KEYWORD),
			/** Token FORWARD of class KEYWORD */
		FORWARD(ETokenClass.KEYWORD),
			/** Token DEF of class KEYWORD */
		DEF(ETokenClass.KEYWORD),
			/** Token HDR of class KEYWORD */
		HDR(ETokenClass.KEYWORD),
			/** Token LIMIT of class KEYWORD */
		LIMIT(ETokenClass.KEYWORD),
			/** Token DEL of class KEYWORD */
		DEL(ETokenClass.KEYWORD),
			/** Token INTDIV of class KEYWORD */
		INTDIV(ETokenClass.KEYWORD),
			/** Token TRANSFORMATION of class KEYWORD */
		TRANSFORMATION(ETokenClass.KEYWORD),
			/** Token SPACE of class KEYWORD */
		SPACE(ETokenClass.KEYWORD),
			/** Token TRUNCATION of class KEYWORD */
		TRUNCATION(ETokenClass.KEYWORD),
			/** Token NOCHARGRAPHIC of class KEYWORD */
		NOCHARGRAPHIC(ETokenClass.KEYWORD),
			/** Token LOCATOR of class KEYWORD */
		LOCATOR(ETokenClass.KEYWORD),
			/** Token DONEMODELOPTIONS of class KEYWORD */
		DONEMODELOPTIONS(ETokenClass.KEYWORD),
			/** Token DIALOG of class KEYWORD */
		DIALOG(ETokenClass.KEYWORD),
			/** Token NOTHING of class KEYWORD */
		NOTHING(ETokenClass.KEYWORD),
			/** Token PAR of class KEYWORD */
		PAR(ETokenClass.KEYWORD),
			/** Token INDEX_LINE of class KEYWORD */
		INDEX_LINE(ETokenClass.KEYWORD),
			/** Token MODEL of class KEYWORD */
		MODEL(ETokenClass.KEYWORD),
			/** Token MODEQ of class OPERATOR */
		MODEQ(ETokenClass.OPERATOR),
			/** Token FUNCTION_POOL of class KEYWORD */
		FUNCTION_POOL(ETokenClass.KEYWORD),
			/** Token SUBMULTISET of class KEYWORD */
		SUBMULTISET(ETokenClass.KEYWORD),
			/** Token NOLOCK of class KEYWORD */
		NOLOCK(ETokenClass.KEYWORD),
			/** Token VIEWS of class KEYWORD */
		VIEWS(ETokenClass.KEYWORD),
			/** Token THREAD of class KEYWORD */
		THREAD(ETokenClass.KEYWORD),
			/** Token INTERFACE_POOL of class KEYWORD */
		INTERFACE_POOL(ETokenClass.KEYWORD),
			/** Token MODIF of class KEYWORD */
		MODIF(ETokenClass.KEYWORD),
			/** Token ENDCASE of class KEYWORD */
		ENDCASE(ETokenClass.KEYWORD),
			/** Token BYTE_CA of class OPERATOR */
		BYTE_CA(ETokenClass.OPERATOR),
			/** Token CONVERT of class KEYWORD */
		CONVERT(ETokenClass.KEYWORD),
			/** Token TRAIT of class KEYWORD */
		TRAIT(ETokenClass.KEYWORD),
			/** Token VARS of class KEYWORD */
		VARS(ETokenClass.KEYWORD),
			/** Token WRAPPED of class KEYWORD */
		WRAPPED(ETokenClass.KEYWORD),
			/** Token FORALL of class KEYWORD */
		FORALL(ETokenClass.KEYWORD),
			/** Token ENDPROVIDE of class KEYWORD */
		ENDPROVIDE(ETokenClass.KEYWORD),
			/** Token TAB of class KEYWORD */
		TAB(ETokenClass.KEYWORD),
			/** Token VARY of class KEYWORD */
		VARY(ETokenClass.KEYWORD),
			/** Token IDENTIFICATION of class KEYWORD */
		IDENTIFICATION(ETokenClass.KEYWORD),
			/** Token END_LOCK of class KEYWORD */
		END_LOCK(ETokenClass.KEYWORD),
			/** Token INCREMENT of class KEYWORD */
		INCREMENT(ETokenClass.KEYWORD),
			/** Token INITIATE of class KEYWORD */
		INITIATE(ETokenClass.KEYWORD),
			/** Token RIGHT_LABEL_BRACKET of class DELIMITER */
		RIGHT_LABEL_BRACKET(ETokenClass.DELIMITER),
			/** Token DBL of class KEYWORD */
		DBL(ETokenClass.KEYWORD),
			/** Token LABEL of class KEYWORD */
		LABEL(ETokenClass.KEYWORD),
			/** Token RAISING of class KEYWORD */
		RAISING(ETokenClass.KEYWORD),
			/** Token TABAUTH of class KEYWORD */
		TABAUTH(ETokenClass.KEYWORD),
			/** Token NONCLUSTERED of class KEYWORD */
		NONCLUSTERED(ETokenClass.KEYWORD),
			/** Token NOSIZE of class KEYWORD */
		NOSIZE(ETokenClass.KEYWORD),
			/** Token FOUND of class KEYWORD */
		FOUND(ETokenClass.KEYWORD),
			/** Token ASSIGN of class KEYWORD */
		ASSIGN(ETokenClass.KEYWORD),
			/** Token ALTERNATIVE of class OPERATOR */
		ALTERNATIVE(ETokenClass.OPERATOR),
			/** Token JUST of class KEYWORD */
		JUST(ETokenClass.KEYWORD),
			/** Token OVERLOAD of class KEYWORD */
		OVERLOAD(ETokenClass.KEYWORD),
			/** Token CLUSTER of class KEYWORD */
		CLUSTER(ETokenClass.KEYWORD),
			/** Token LEN of class KEYWORD */
		LEN(ETokenClass.KEYWORD),
			/** Token WINDOW of class KEYWORD */
		WINDOW(ETokenClass.KEYWORD),
			/** Token DBR of class KEYWORD */
		DBR(ETokenClass.KEYWORD),
			/** Token COLON_PLUS of class OPERATOR */
		COLON_PLUS(ETokenClass.OPERATOR),
			/** Token PLACES of class KEYWORD */
		PLACES(ETokenClass.KEYWORD),
			/** Token LET of class KEYWORD */
		LET(ETokenClass.KEYWORD),
			/** Token SHR of class KEYWORD */
		SHR(ETokenClass.KEYWORD),
			/** Token PIVOT of class KEYWORD */
		PIVOT(ETokenClass.KEYWORD),
			/** Token SHL of class KEYWORD */
		SHL(ETokenClass.KEYWORD),
			/** Token CONTAINS of class KEYWORD */
		CONTAINS(ETokenClass.KEYWORD),
			/** Token PWD of class KEYWORD */
		PWD(ETokenClass.KEYWORD),
			/** Token IDENTITYCOL of class KEYWORD */
		IDENTITYCOL(ETokenClass.KEYWORD),
			/** Token UNPACK of class KEYWORD */
		UNPACK(ETokenClass.KEYWORD),
			/** Token FLUSH of class KEYWORD */
		FLUSH(ETokenClass.KEYWORD),
			/** Token FOREIGN of class KEYWORD */
		FOREIGN(ETokenClass.KEYWORD),
			/** Token REQUIRE of class KEYWORD */
		REQUIRE(ETokenClass.KEYWORD),
			/** Token CHARACTER of class KEYWORD */
		CHARACTER(ETokenClass.KEYWORD),
			/** Token READY of class KEYWORD */
		READY(ETokenClass.KEYWORD),
			/** Token SIS of class KEYWORD */
		SIS(ETokenClass.KEYWORD),
			/** Token INTERACTIVE of class KEYWORD */
		INTERACTIVE(ETokenClass.KEYWORD),
			/** Token SCROLL_BOUNDARY of class KEYWORD */
		SCROLL_BOUNDARY(ETokenClass.KEYWORD),
			/** Token EXCLAMATION of class OPERATOR */
		EXCLAMATION(ETokenClass.OPERATOR),
			/** Token SIN of class KEYWORD */
		SIN(ETokenClass.KEYWORD),
			/** Token STRINGRANGE of class KEYWORD */
		STRINGRANGE(ETokenClass.KEYWORD),
			/** Token SENTENCE of class KEYWORD */
		SENTENCE(ETokenClass.KEYWORD),
			/** Token MINIMUM of class KEYWORD */
		MINIMUM(ETokenClass.KEYWORD),
			/** Token PWI of class KEYWORD */
		PWI(ETokenClass.KEYWORD),
			/** Token SBYTE of class KEYWORD */
		SBYTE(ETokenClass.KEYWORD),
			/** Token EXPORT of class KEYWORD */
		EXPORT(ETokenClass.KEYWORD),
			/** Token SNAP of class KEYWORD */
		SNAP(ETokenClass.KEYWORD),
			/** Token REGISTER of class KEYWORD */
		REGISTER(ETokenClass.KEYWORD),
			/** Token DISABLE of class KEYWORD */
		DISABLE(ETokenClass.KEYWORD),
			/** Token ASSIGNABLE of class KEYWORD */
		ASSIGNABLE(ETokenClass.KEYWORD),
			/** Token POSITIVE of class KEYWORD */
		POSITIVE(ETokenClass.KEYWORD),
			/** Token SUBMIT of class KEYWORD */
		SUBMIT(ETokenClass.KEYWORD),
			/** Token LEGACY of class KEYWORD */
		LEGACY(ETokenClass.KEYWORD),
			/** Token MONEY_LITERAL of class LITERAL */
		MONEY_LITERAL(ETokenClass.LITERAL),
			/** Token SUBPARTITION of class KEYWORD */
		SUBPARTITION(ETokenClass.KEYWORD),
			/** Token LIN of class KEYWORD */
		LIN(ETokenClass.KEYWORD),
			/** Token HOTSPOT of class KEYWORD */
		HOTSPOT(ETokenClass.KEYWORD),
			/** Token MULTEQ of class OPERATOR */
		MULTEQ(ETokenClass.OPERATOR),
			/** Token STRICTFP of class KEYWORD */
		STRICTFP(ETokenClass.KEYWORD),
			/** Token CUSTOMDATUM of class KEYWORD */
		CUSTOMDATUM(ETokenClass.KEYWORD),
			/** Token NATIVE of class KEYWORD */
		NATIVE(ETokenClass.KEYWORD),
			/** Token NO_SCROLLING of class KEYWORD */
		NO_SCROLLING(ETokenClass.KEYWORD),
			/** Token VARIANT of class KEYWORD */
		VARIANT(ETokenClass.KEYWORD),
			/** Token UPTHRU of class KEYWORD */
		UPTHRU(ETokenClass.KEYWORD),
			/** Token EVENTS of class KEYWORD */
		EVENTS(ETokenClass.KEYWORD),
			/** Token SKIPPING of class KEYWORD */
		SKIPPING(ETokenClass.KEYWORD),
			/** Token SIZ of class KEYWORD */
		SIZ(ETokenClass.KEYWORD),
			/** Token LIB of class KEYWORD */
		LIB(ETokenClass.KEYWORD),
			/** Token END_UNSTRING of class KEYWORD */
		END_UNSTRING(ETokenClass.KEYWORD),
			/** Token READTEXT of class KEYWORD */
		READTEXT(ETokenClass.KEYWORD),
			/** Token BRACKET_MISMATCH of class ERROR */
		BRACKET_MISMATCH(ETokenClass.ERROR),
			/** Token LANGUAGE of class KEYWORD */
		LANGUAGE(ETokenClass.KEYWORD),
			/** Token SERVICE of class KEYWORD */
		SERVICE(ETokenClass.KEYWORD),
			/** Token LEVEL of class KEYWORD */
		LEVEL(ETokenClass.KEYWORD),
			/** Token DOUBLE_COLON of class OPERATOR */
		DOUBLE_COLON(ETokenClass.OPERATOR),
			/** Token NONE of class KEYWORD */
		NONE(ETokenClass.KEYWORD),
			/** Token HEAD_LINES of class KEYWORD */
		HEAD_LINES(ETokenClass.KEYWORD),
			/** Token REPEAT of class KEYWORD */
		REPEAT(ETokenClass.KEYWORD),
			/** Token CEN of class KEYWORD */
		CEN(ETokenClass.KEYWORD),
			/** Token LOCATOR_POINTER of class OPERATOR */
		LOCATOR_POINTER(ETokenClass.OPERATOR),
			/** Token RESCUE of class KEYWORD */
		RESCUE(ETokenClass.KEYWORD),
			/** Token OPTION of class KEYWORD */
		OPTION(ETokenClass.KEYWORD),
			/** Token SDA of class KEYWORD */
		SDA(ETokenClass.KEYWORD),
			/** Token FREETEXTTABLE of class KEYWORD */
		FREETEXTTABLE(ETokenClass.KEYWORD),
			/** Token PART of class KEYWORD */
		PART(ETokenClass.KEYWORD),
			/** Token ACTUAL of class KEYWORD */
		ACTUAL(ETokenClass.KEYWORD),
			/** Token CTYPE of class KEYWORD */
		CTYPE(ETokenClass.KEYWORD),
			/** Token UNTERMINATED_STRING_LITERAL of class ERROR */
		UNTERMINATED_STRING_LITERAL(ETokenClass.ERROR),
			/** Token LIBRARY of class KEYWORD */
		LIBRARY(ETokenClass.KEYWORD),
			/** Token COMPUTATIONAL_5 of class KEYWORD */
		COMPUTATIONAL_5(ETokenClass.KEYWORD),
			/** Token SORT_CORE_SIZE of class KEYWORD */
		SORT_CORE_SIZE(ETokenClass.KEYWORD),
			/** Token CHARSET of class KEYWORD */
		CHARSET(ETokenClass.KEYWORD),
			/** Token COMPUTATIONAL_3 of class KEYWORD */
		COMPUTATIONAL_3(ETokenClass.KEYWORD),
			/** Token COMPUTATIONAL_4 of class KEYWORD */
		COMPUTATIONAL_4(ETokenClass.KEYWORD),
			/** Token OCIREFCURSOR of class KEYWORD */
		OCIREFCURSOR(ETokenClass.KEYWORD),
			/** Token COMPUTATIONAL_1 of class KEYWORD */
		COMPUTATIONAL_1(ETokenClass.KEYWORD),
			/** Token COMPUTATIONAL_2 of class KEYWORD */
		COMPUTATIONAL_2(ETokenClass.KEYWORD),
			/** Token LPAREN of class DELIMITER */
		LPAREN(ETokenClass.DELIMITER),
			/** Token POSITION of class KEYWORD */
		POSITION(ETokenClass.KEYWORD),
			/** Token SCALARVARYING of class KEYWORD */
		SCALARVARYING(ETokenClass.KEYWORD),
			/** Token OFFSET of class KEYWORD */
		OFFSET(ETokenClass.KEYWORD),
			/** Token PREPROCESSOR_REPLACE of class KEYWORD */
		PREPROCESSOR_REPLACE(ETokenClass.KEYWORD),
			/** Token ACCESS of class KEYWORD */
		ACCESS(ETokenClass.KEYWORD),
			/** Token PIPE of class KEYWORD */
		PIPE(ETokenClass.KEYWORD),
			/** Token POWEREQ of class OPERATOR */
		POWEREQ(ETokenClass.OPERATOR),
			/** Token HASH_COMMENT of class COMMENT */
		HASH_COMMENT(ETokenClass.COMMENT),
			/** Token FILLER of class KEYWORD */
		FILLER(ETokenClass.KEYWORD),
			/** Token ENDFILE of class KEYWORD */
		ENDFILE(ETokenClass.KEYWORD),
			/** Token COLON_EQ of class OPERATOR */
		COLON_EQ(ETokenClass.OPERATOR),
			/** Token SQLNAME of class KEYWORD */
		SQLNAME(ETokenClass.KEYWORD),
			/** Token B_OR of class KEYWORD */
		B_OR(ETokenClass.KEYWORD),
			/** Token ALPHANUMERIC_EDITED of class KEYWORD */
		ALPHANUMERIC_EDITED(ETokenClass.KEYWORD),
			/** Token SAP_SPOOL of class KEYWORD */
		SAP_SPOOL(ETokenClass.KEYWORD),
			/** Token ADD_CORRESPONDING of class KEYWORD */
		ADD_CORRESPONDING(ETokenClass.KEYWORD),
			/** Token TRKOFL of class KEYWORD */
		TRKOFL(ETokenClass.KEYWORD),
			/** Token LNA of class KEYWORD */
		LNA(ETokenClass.KEYWORD),
			/** Token BLOB_BASE of class KEYWORD */
		BLOB_BASE(ETokenClass.KEYWORD),
			/** Token ACTIVE_CLASS of class KEYWORD */
		ACTIVE_CLASS(ETokenClass.KEYWORD),
			/** Token FINALIZATION of class KEYWORD */
		FINALIZATION(ETokenClass.KEYWORD),
			/** Token SET of class KEYWORD */
		SET(ETokenClass.KEYWORD),
			/** Token CHK of class KEYWORD */
		CHK(ETokenClass.KEYWORD),
			/** Token UPDATETEXT of class KEYWORD */
		UPDATETEXT(ETokenClass.KEYWORD),
			/** Token BREAK_POINT of class KEYWORD */
		BREAK_POINT(ETokenClass.KEYWORD),
			/** Token PREPROCESSOR_OPTION of class KEYWORD */
		PREPROCESSOR_OPTION(ETokenClass.KEYWORD),
			/** Token TSEQUAL of class KEYWORD */
		TSEQUAL(ETokenClass.KEYWORD),
			/** Token ACCEPT of class KEYWORD */
		ACCEPT(ETokenClass.KEYWORD),
			/** Token PRINT of class KEYWORD */
		PRINT(ETokenClass.KEYWORD),
			/** Token UNION of class KEYWORD */
		UNION(ETokenClass.KEYWORD),
			/** Token UNICODE of class KEYWORD */
		UNICODE(ETokenClass.KEYWORD),
			/** Token CLASS_POOL of class KEYWORD */
		CLASS_POOL(ETokenClass.KEYWORD),
			/** Token ECHO of class KEYWORD */
		ECHO(ETokenClass.KEYWORD),
			/** Token CREATING of class KEYWORD */
		CREATING(ETokenClass.KEYWORD),
			/** Token UNSIGNED of class KEYWORD */
		UNSIGNED(ETokenClass.KEYWORD),
			/** Token ENUM of class KEYWORD */
		ENUM(ETokenClass.KEYWORD),
			/** Token INVALIDOP of class KEYWORD */
		INVALIDOP(ETokenClass.KEYWORD),
			/** Token BYTE_ORDER of class KEYWORD */
		BYTE_ORDER(ETokenClass.KEYWORD),
			/** Token HEREDOC of class LITERAL */
		HEREDOC(ETokenClass.LITERAL),
			/** Token HOLDLOCK of class KEYWORD */
		HOLDLOCK(ETokenClass.KEYWORD),
			/** Token DROP of class KEYWORD */
		DROP(ETokenClass.KEYWORD),
			/** Token VALIDATE of class KEYWORD */
		VALIDATE(ETokenClass.KEYWORD),
			/** Token OCIREF of class KEYWORD */
		OCIREF(ETokenClass.KEYWORD),
			/** Token TRADITIONAL_COMMENT of class COMMENT */
		TRADITIONAL_COMMENT(ETokenClass.COMMENT),
			/** Token SHIFT_OUT of class KEYWORD */
		SHIFT_OUT(ETokenClass.KEYWORD),
			/** Token PRIOR of class KEYWORD */
		PRIOR(ETokenClass.KEYWORD),
			/** Token SIGN of class KEYWORD */
		SIGN(ETokenClass.KEYWORD),
			/** Token LATE of class KEYWORD */
		LATE(ETokenClass.KEYWORD),
			/** Token ALLOCATE of class KEYWORD */
		ALLOCATE(ETokenClass.KEYWORD),
			/** Token SPC of class KEYWORD */
		SPC(ETokenClass.KEYWORD),
			/** Token STORED of class KEYWORD */
		STORED(ETokenClass.KEYWORD),
			/** Token LOG of class KEYWORD */
		LOG(ETokenClass.KEYWORD),
			/** Token LOC of class KEYWORD */
		LOC(ETokenClass.KEYWORD),
			/** Token PNN of class KEYWORD */
		PNN(ETokenClass.KEYWORD),
			/** Token GOT of class KEYWORD */
		GOT(ETokenClass.KEYWORD),
			/** Token ENDSELECT of class KEYWORD */
		ENDSELECT(ETokenClass.KEYWORD),
			/** Token UNSET of class KEYWORD */
		UNSET(ETokenClass.KEYWORD),
			/** Token RERUN of class KEYWORD */
		RERUN(ETokenClass.KEYWORD),
			/** Token SUBTYPE of class KEYWORD */
		SUBTYPE(ETokenClass.KEYWORD),
			/** Token LOW of class KEYWORD */
		LOW(ETokenClass.KEYWORD),
			/** Token WORD of class KEYWORD */
		WORD(ETokenClass.KEYWORD),
			/** Token SQL of class KEYWORD */
		SQL(ETokenClass.KEYWORD),
			/** Token NOTEQEQ of class OPERATOR */
		NOTEQEQ(ETokenClass.OPERATOR),
			/** Token ALIASES of class KEYWORD */
		ALIASES(ETokenClass.KEYWORD),
			/** Token METHODS of class KEYWORD */
		METHODS(ETokenClass.KEYWORD),
			/** Token COP of class KEYWORD */
		COP(ETokenClass.KEYWORD),
			/** Token POS of class KEYWORD */
		POS(ETokenClass.KEYWORD),
			/** Token COS of class KEYWORD */
		COS(ETokenClass.KEYWORD),
			/** Token PROMPT of class KEYWORD */
		PROMPT(ETokenClass.KEYWORD),
			/** Token XML_NAME of class IDENTIFIER */
		XML_NAME(ETokenClass.IDENTIFIER),
			/** Token COM of class KEYWORD */
		COM(ETokenClass.KEYWORD),
			/** Token COL of class KEYWORD */
		COL(ETokenClass.KEYWORD),
			/** Token ADDHANDLER of class KEYWORD */
		ADDHANDLER(ETokenClass.KEYWORD),
			/** Token MUSTOVERRIDE of class KEYWORD */
		MUSTOVERRIDE(ETokenClass.KEYWORD),
			/** Token SAVEPOINT of class KEYWORD */
		SAVEPOINT(ETokenClass.KEYWORD),
			/** Token CHAR_BASE of class KEYWORD */
		CHAR_BASE(ETokenClass.KEYWORD),
			/** Token CON of class KEYWORD */
		CON(ETokenClass.KEYWORD),
			/** Token RESPECTING of class KEYWORD */
		RESPECTING(ETokenClass.KEYWORD),
			/** Token LOB of class KEYWORD */
		LOB(ETokenClass.KEYWORD),
			/** Token IMPLEMENTATION of class KEYWORD */
		IMPLEMENTATION(ETokenClass.KEYWORD),
			/** Token RAISE of class KEYWORD */
		RAISE(ETokenClass.KEYWORD),
			/** Token LAST of class KEYWORD */
		LAST(ETokenClass.KEYWORD),
			/** Token ABORT of class KEYWORD */
		ABORT(ETokenClass.KEYWORD),
			/** Token SQR of class KEYWORD */
		SQR(ETokenClass.KEYWORD),
			/** Token WORK of class KEYWORD */
		WORK(ETokenClass.KEYWORD),
			/** Token CLIENT of class KEYWORD */
		CLIENT(ETokenClass.KEYWORD),
			/** Token NODE of class KEYWORD */
		NODE(ETokenClass.KEYWORD),
			/** Token TYPE_POOL of class KEYWORD */
		TYPE_POOL(ETokenClass.KEYWORD),
			/** Token CLM of class KEYWORD */
		CLM(ETokenClass.KEYWORD),
			/** Token MULT of class OPERATOR */
		MULT(ETokenClass.OPERATOR),
			/** Token DETAIL of class KEYWORD */
		DETAIL(ETokenClass.KEYWORD),
			/** Token EXCLUDING of class KEYWORD */
		EXCLUDING(ETokenClass.KEYWORD),
			/** Token INHERITED of class KEYWORD */
		INHERITED(ETokenClass.KEYWORD),
			/** Token LIST of class KEYWORD */
		LIST(ETokenClass.KEYWORD),
			/** Token ASSERT of class KEYWORD */
		ASSERT(ETokenClass.KEYWORD),
			/** Token OCIRAW of class KEYWORD */
		OCIRAW(ETokenClass.KEYWORD),
			/** Token DENY of class KEYWORD */
		DENY(ETokenClass.KEYWORD),
			/** Token INVERSE of class KEYWORD */
		INVERSE(ETokenClass.KEYWORD),
			/** Token CINT of class KEYWORD */
		CINT(ETokenClass.KEYWORD),
			/** Token END_RETURN of class KEYWORD */
		END_RETURN(ETokenClass.KEYWORD),
			/** Token INTEGER_LITERAL of class LITERAL */
		INTEGER_LITERAL(ETokenClass.LITERAL),
			/** Token COMPILED of class KEYWORD */
		COMPILED(ETokenClass.KEYWORD),
			/** Token SEGMENT_LIMIT of class KEYWORD */
		SEGMENT_LIMIT(ETokenClass.KEYWORD),
			/** Token FIELD of class KEYWORD */
		FIELD(ETokenClass.KEYWORD),
			/** Token INLINE of class KEYWORD */
		INLINE(ETokenClass.KEYWORD),
			/** Token DURING of class KEYWORD */
		DURING(ETokenClass.KEYWORD),
			/** Token PACK of class KEYWORD */
		PACK(ETokenClass.KEYWORD),
			/** Token REVERSED of class KEYWORD */
		REVERSED(ETokenClass.KEYWORD),
			/** Token AUTOMATIC of class KEYWORD */
		AUTOMATIC(ETokenClass.KEYWORD),
			/** Token CRT of class KEYWORD */
		CRT(ETokenClass.KEYWORD),
			/** Token MAIN of class KEYWORD */
		MAIN(ETokenClass.KEYWORD),
			/** Token PRT of class KEYWORD */
		PRT(ETokenClass.KEYWORD),
			/** Token INNER of class KEYWORD */
		INNER(ETokenClass.KEYWORD),
			/** Token IS_DATE of class OPERATOR */
		IS_DATE(ETokenClass.OPERATOR),
			/** Token AUTHORIZATION of class KEYWORD */
		AUTHORIZATION(ETokenClass.KEYWORD),
			/** Token DIGITS of class KEYWORD */
		DIGITS(ETokenClass.KEYWORD),
			/** Token SLN of class KEYWORD */
		SLN(ETokenClass.KEYWORD),
			/** Token CELL of class KEYWORD */
		CELL(ETokenClass.KEYWORD),
			/** Token SECONDS of class KEYWORD */
		SECONDS(ETokenClass.KEYWORD),
			/** Token GENKEY of class KEYWORD */
		GENKEY(ETokenClass.KEYWORD),
			/** Token PRO of class KEYWORD */
		PRO(ETokenClass.KEYWORD),
			/** Token CONCATENATIONEQ of class OPERATOR */
		CONCATENATIONEQ(ETokenClass.OPERATOR),
			/** Token INTERVAL of class KEYWORD */
		INTERVAL(ETokenClass.KEYWORD),
			/** Token LINEFEED of class KEYWORD */
		LINEFEED(ETokenClass.KEYWORD),
			/** Token NUMBER_WORD of class LITERAL */
		NUMBER_WORD(ETokenClass.LITERAL),
			/** Token INDEX of class KEYWORD */
		INDEX(ETokenClass.KEYWORD),
			/** Token PSZ of class KEYWORD */
		PSZ(ETokenClass.KEYWORD),
			/** Token LINE_COUNT of class KEYWORD */
		LINE_COUNT(ETokenClass.KEYWORD),
			/** Token WATERMARK of class KEYWORD */
		WATERMARK(ETokenClass.KEYWORD),
			/** Token EJECT of class KEYWORD */
		EJECT(ETokenClass.KEYWORD),
			/** Token TILDE of class OPERATOR */
		TILDE(ETokenClass.OPERATOR),
			/** Token EXTRACT of class KEYWORD */
		EXTRACT(ETokenClass.KEYWORD),
			/** Token COMPONENT of class KEYWORD */
		COMPONENT(ETokenClass.KEYWORD),
			/** Token STABLE of class KEYWORD */
		STABLE(ETokenClass.KEYWORD),
			/** Token GRF of class KEYWORD */
		GRF(ETokenClass.KEYWORD),
			/** Token DANGLING of class KEYWORD */
		DANGLING(ETokenClass.KEYWORD),
			/** Token INDICATOR of class KEYWORD */
		INDICATOR(ETokenClass.KEYWORD),
			/** Token OPERATOR of class KEYWORD */
		OPERATOR(ETokenClass.KEYWORD),
			/** Token REDUCED of class KEYWORD */
		REDUCED(ETokenClass.KEYWORD),
			/** Token IMMEDIATELY of class KEYWORD */
		IMMEDIATELY(ETokenClass.KEYWORD),
			/** Token BUFFER of class KEYWORD */
		BUFFER(ETokenClass.KEYWORD),
			/** Token REFERENCE of class KEYWORD */
		REFERENCE(ETokenClass.KEYWORD),
			/** Token SUBTRACT_CORRESPONDING of class KEYWORD */
		SUBTRACT_CORRESPONDING(ETokenClass.KEYWORD),
			/** Token STRUCT of class KEYWORD */
		STRUCT(ETokenClass.KEYWORD),
			/** Token ALPHABETIC of class KEYWORD */
		ALPHABETIC(ETokenClass.KEYWORD),
			/** Token OVERRIDES of class KEYWORD */
		OVERRIDES(ETokenClass.KEYWORD),
			/** Token XML_TEXT of class KEYWORD */
		XML_TEXT(ETokenClass.KEYWORD),
			/** Token TRUE of class KEYWORD */
		TRUE(ETokenClass.KEYWORD),
			/** Token PREPROCESSOR_ACTIVATE of class KEYWORD */
		PREPROCESSOR_ACTIVATE(ETokenClass.KEYWORD),
			/** Token ENDCATCH of class KEYWORD */
		ENDCATCH(ETokenClass.KEYWORD),
			/** Token MAJOR_ID of class KEYWORD */
		MAJOR_ID(ETokenClass.KEYWORD),
			/** Token OPEN of class KEYWORD */
		OPEN(ETokenClass.KEYWORD),
			/** Token PAGE of class KEYWORD */
		PAGE(ETokenClass.KEYWORD),
			/** Token HEXADEC of class KEYWORD */
		HEXADEC(ETokenClass.KEYWORD),
			/** Token LOW_VALUES of class KEYWORD */
		LOW_VALUES(ETokenClass.KEYWORD),
			/** Token DISK of class KEYWORD */
		DISK(ETokenClass.KEYWORD),
			/** Token BUFFERS of class KEYWORD */
		BUFFERS(ETokenClass.KEYWORD),
			/** Token UNDEFINEDFILE of class KEYWORD */
		UNDEFINEDFILE(ETokenClass.KEYWORD),
			/** Token COMPUTATIONAL of class KEYWORD */
		COMPUTATIONAL(ETokenClass.KEYWORD),
			/** Token ACTIVATION of class KEYWORD */
		ACTIVATION(ETokenClass.KEYWORD),
			/** Token DESC of class KEYWORD */
		DESC(ETokenClass.KEYWORD),
			/** Token DATE of class KEYWORD */
		DATE(ETokenClass.KEYWORD),
			/** Token BINARY_DOUBLE of class KEYWORD */
		BINARY_DOUBLE(ETokenClass.KEYWORD),
			/** Token ENDLOOP of class KEYWORD */
		ENDLOOP(ETokenClass.KEYWORD),
			/** Token DATA of class KEYWORD */
		DATA(ETokenClass.KEYWORD),
			/** Token PUT of class KEYWORD */
		PUT(ETokenClass.KEYWORD),
			/** Token FUNCTION of class KEYWORD */
		FUNCTION(ETokenClass.KEYWORD),
			/** Token HEAP of class KEYWORD */
		HEAP(ETokenClass.KEYWORD),
			/** Token FUNCTION_ID of class KEYWORD */
		FUNCTION_ID(ETokenClass.KEYWORD),
			/** Token FILE_CONTROL of class KEYWORD */
		FILE_CONTROL(ETokenClass.KEYWORD),
			/** Token EC of class KEYWORD */
		EC(ETokenClass.KEYWORD),
			/** Token BEGIN_BLOCK of class KEYWORD */
		BEGIN_BLOCK(ETokenClass.KEYWORD),
			/** Token CASTING of class KEYWORD */
		CASTING(ETokenClass.KEYWORD),
			/** Token XOREQ of class OPERATOR */
		XOREQ(ETokenClass.OPERATOR),
			/** Token CONST of class KEYWORD */
		CONST(ETokenClass.KEYWORD),
			/** Token MUSTINHERIT of class KEYWORD */
		MUSTINHERIT(ETokenClass.KEYWORD),
			/** Token PRECISION of class KEYWORD */
		PRECISION(ETokenClass.KEYWORD),
			/** Token DO of class KEYWORD */
		DO(ETokenClass.KEYWORD),
			/** Token SELECTION_SETS of class KEYWORD */
		SELECTION_SETS(ETokenClass.KEYWORD),
			/** Token ENABLE of class KEYWORD */
		ENABLE(ETokenClass.KEYWORD),
			/** Token BASED of class KEYWORD */
		BASED(ETokenClass.KEYWORD),
			/** Token DE of class KEYWORD */
		DE(ETokenClass.KEYWORD),
			/** Token DD of class KEYWORD */
		DD(ETokenClass.KEYWORD),
			/** Token END_EVALUATE of class KEYWORD */
		END_EVALUATE(ETokenClass.KEYWORD),
			/** Token AWAIT of class KEYWORD */
		AWAIT(ETokenClass.KEYWORD),
			/** Token NONCONNECTED of class KEYWORD */
		NONCONNECTED(ETokenClass.KEYWORD),
			/** Token BUILTIN of class KEYWORD */
		BUILTIN(ETokenClass.KEYWORD),
			/** Token INC of class KEYWORD */
		INC(ETokenClass.KEYWORD),
			/** Token MYCLASS of class KEYWORD */
		MYCLASS(ETokenClass.KEYWORD),
			/** Token FD of class KEYWORD */
		FD(ETokenClass.KEYWORD),
			/** Token UNCHECKED of class KEYWORD */
		UNCHECKED(ETokenClass.KEYWORD),
			/** Token FB of class KEYWORD */
		FB(ETokenClass.KEYWORD),
			/** Token MOVE_CORRESPONDING of class KEYWORD */
		MOVE_CORRESPONDING(ETokenClass.KEYWORD),
			/** Token RETURN of class KEYWORD */
		RETURN(ETokenClass.KEYWORD),
			/** Token FIELD_GROUPS of class KEYWORD */
		FIELD_GROUPS(ETokenClass.KEYWORD),
			/** Token EO of class KEYWORD */
		EO(ETokenClass.KEYWORD),
			/** Token EQ of class OPERATOR */
		EQ(ETokenClass.OPERATOR),
			/** Token STREAM of class KEYWORD */
		STREAM(ETokenClass.KEYWORD),
			/** Token EVALUATE of class KEYWORD */
		EVALUATE(ETokenClass.KEYWORD),
			/** Token DIVIDE of class KEYWORD */
		DIVIDE(ETokenClass.KEYWORD),
			/** Token LEFT_LABEL_BRACKET of class DELIMITER */
		LEFT_LABEL_BRACKET(ETokenClass.DELIMITER),
			/** Token MMDDYY of class KEYWORD */
		MMDDYY(ETokenClass.KEYWORD),
			/** Token IMG of class KEYWORD */
		IMG(ETokenClass.KEYWORD),
			/** Token GRAPHIC of class KEYWORD */
		GRAPHIC(ETokenClass.KEYWORD),
			/** Token RBRACK of class DELIMITER */
		RBRACK(ETokenClass.DELIMITER),
			/** Token ENDFORM of class KEYWORD */
		ENDFORM(ETokenClass.KEYWORD),
			/** Token RENAMING of class KEYWORD */
		RENAMING(ETokenClass.KEYWORD),
			/** Token STRICT of class KEYWORD */
		STRICT(ETokenClass.KEYWORD),
			/** Token RBRACE of class DELIMITER */
		RBRACE(ETokenClass.DELIMITER),
			/** Token TRANSACTION of class KEYWORD */
		TRANSACTION(ETokenClass.KEYWORD),
			/** Token END_REWRITE of class KEYWORD */
		END_REWRITE(ETokenClass.KEYWORD),
			/** Token STEP_LOOP of class KEYWORD */
		STEP_LOOP(ETokenClass.KEYWORD),
			/** Token FS of class KEYWORD */
		FS(ETokenClass.KEYWORD),
			/** Token B_NOT of class KEYWORD */
		B_NOT(ETokenClass.KEYWORD),
			/** Token DATAINFO of class KEYWORD */
		DATAINFO(ETokenClass.KEYWORD),
			/** Token MULTIPLE_EOL of class WHITESPACE */
		MULTIPLE_EOL(ETokenClass.WHITESPACE),
			/** Token INT of class KEYWORD */
		INT(ETokenClass.KEYWORD),
			/** Token CONTENT of class KEYWORD */
		CONTENT(ETokenClass.KEYWORD),
			/** Token TRANSLATE of class KEYWORD */
		TRANSLATE(ETokenClass.KEYWORD),
			/** Token GROUPS of class KEYWORD */
		GROUPS(ETokenClass.KEYWORD),
			/** Token IRREDUCIBLE of class KEYWORD */
		IRREDUCIBLE(ETokenClass.KEYWORD),
			/** Token APPENDING of class KEYWORD */
		APPENDING(ETokenClass.KEYWORD),
			/** Token LEADING of class KEYWORD */
		LEADING(ETokenClass.KEYWORD),
			/** Token XML_CODE of class KEYWORD */
		XML_CODE(ETokenClass.KEYWORD),
			/** Token EMPTY of class KEYWORD */
		EMPTY(ETokenClass.KEYWORD),
			/** Token RESIGNAL of class KEYWORD */
		RESIGNAL(ETokenClass.KEYWORD),
			/** Token GROUP of class KEYWORD */
		GROUP(ETokenClass.KEYWORD),
			/** Token END_WRITE of class KEYWORD */
		END_WRITE(ETokenClass.KEYWORD),
			/** Token AREA of class KEYWORD */
		AREA(ETokenClass.KEYWORD),
			/** Token SAFECALL of class KEYWORD */
		SAFECALL(ETokenClass.KEYWORD),
			/** Token SELECTION_TABLE of class KEYWORD */
		SELECTION_TABLE(ETokenClass.KEYWORD),
			/** Token LOG_POINT of class KEYWORD */
		LOG_POINT(ETokenClass.KEYWORD),
			/** Token GX of class KEYWORD */
		GX(ETokenClass.KEYWORD),
			/** Token GT of class OPERATOR */
		GT(ETokenClass.OPERATOR),
			/** Token PERSON of class KEYWORD */
		PERSON(ETokenClass.KEYWORD),
			/** Token SHUTDOWN of class KEYWORD */
		SHUTDOWN(ETokenClass.KEYWORD),
			/** Token LOWER of class KEYWORD */
		LOWER(ETokenClass.KEYWORD),
			/** Token GO of class KEYWORD */
		GO(ETokenClass.KEYWORD),
			/** Token REGEX_LITERAL of class LITERAL */
		REGEX_LITERAL(ETokenClass.LITERAL),
			/** Token SYNTAX_CHECK of class KEYWORD */
		SYNTAX_CHECK(ETokenClass.KEYWORD),
			/** Token INVOKE of class KEYWORD */
		INVOKE(ETokenClass.KEYWORD),
			/** Token BLOCKS of class KEYWORD */
		BLOCKS(ETokenClass.KEYWORD),
			/** Token CONSTRAINT of class KEYWORD */
		CONSTRAINT(ETokenClass.KEYWORD),
			/** Token REGIONAL of class KEYWORD */
		REGIONAL(ETokenClass.KEYWORD),
			/** Token END_SUB of class KEYWORD */
		END_SUB(ETokenClass.KEYWORD),
			/** Token COM_REG of class KEYWORD */
		COM_REG(ETokenClass.KEYWORD),
			/** Token APPLY of class KEYWORD */
		APPLY(ETokenClass.KEYWORD),
			/** Token INTERFACE_ID of class KEYWORD */
		INTERFACE_ID(ETokenClass.KEYWORD),
			/** Token REPORT of class KEYWORD */
		REPORT(ETokenClass.KEYWORD),
			/** Token FORCE of class KEYWORD */
		FORCE(ETokenClass.KEYWORD),
			/** Token NCHAR of class KEYWORD */
		NCHAR(ETokenClass.KEYWORD),
			/** Token ENDMETHOD of class KEYWORD */
		ENDMETHOD(ETokenClass.KEYWORD),
			/** Token ANYCONDITION of class KEYWORD */
		ANYCONDITION(ETokenClass.KEYWORD),
			/** Token LTEQ of class OPERATOR */
		LTEQ(ETokenClass.OPERATOR),
			/** Token FLOAT of class KEYWORD */
		FLOAT(ETokenClass.KEYWORD),
			/** Token AT_LENGTH of class OPERATOR */
		AT_LENGTH(ETokenClass.OPERATOR),
			/** Token BLKSIZE of class KEYWORD */
		BLKSIZE(ETokenClass.KEYWORD),
			/** Token PUBLISHED of class KEYWORD */
		PUBLISHED(ETokenClass.KEYWORD),
			/** Token NOWAIT of class KEYWORD */
		NOWAIT(ETokenClass.KEYWORD),
			/** Token ZERO of class KEYWORD */
		ZERO(ETokenClass.KEYWORD),
			/** Token LOAD_OF_PROGRAM of class KEYWORD */
		LOAD_OF_PROGRAM(ETokenClass.KEYWORD),
			/** Token AT of class KEYWORD */
		AT(ETokenClass.KEYWORD),
			/** Token B4 of class KEYWORD */
		B4(ETokenClass.KEYWORD),
			/** Token AS of class KEYWORD */
		AS(ETokenClass.KEYWORD),
			/** Token B3 of class KEYWORD */
		B3(ETokenClass.KEYWORD),
			/** Token INCLUDE_ONCE of class KEYWORD */
		INCLUDE_ONCE(ETokenClass.KEYWORD),
			/** Token B2 of class KEYWORD */
		B2(ETokenClass.KEYWORD),
			/** Token ANYCASE of class KEYWORD */
		ANYCASE(ETokenClass.KEYWORD),
			/** Token COMPRESS of class KEYWORD */
		COMPRESS(ETokenClass.KEYWORD),
			/** Token TRUNCATE of class KEYWORD */
		TRUNCATE(ETokenClass.KEYWORD),
			/** Token GATHER of class KEYWORD */
		GATHER(ETokenClass.KEYWORD),
			/** Token ZERODIVIDE of class KEYWORD */
		ZERODIVIDE(ETokenClass.KEYWORD),
			/** Token NODES of class KEYWORD */
		NODES(ETokenClass.KEYWORD),
			/** Token FKEQ of class KEYWORD */
		FKEQ(ETokenClass.KEYWORD),
			/** Token BINARY_LITERAL of class LITERAL */
		BINARY_LITERAL(ETokenClass.LITERAL),
			/** Token LAMBDA of class KEYWORD */
		LAMBDA(ETokenClass.KEYWORD),
			/** Token PROTECT of class KEYWORD */
		PROTECT(ETokenClass.KEYWORD),
			/** Token OCITYPE of class KEYWORD */
		OCITYPE(ETokenClass.KEYWORD),
			/** Token MODE of class KEYWORD */
		MODE(ETokenClass.KEYWORD),
			/** Token DOWNTHRU of class KEYWORD */
		DOWNTHRU(ETokenClass.KEYWORD),
			/** Token LIKE of class KEYWORD */
		LIKE(ETokenClass.KEYWORD),
			/** Token BY of class KEYWORD */
		BY(ETokenClass.KEYWORD),
			/** Token BX of class KEYWORD */
		BX(ETokenClass.KEYWORD),
			/** Token PARALLEL_ENABLE of class KEYWORD */
		PARALLEL_ENABLE(ETokenClass.KEYWORD),
			/** Token CA of class OPERATOR */
		CA(ETokenClass.OPERATOR),
			/** Token ALPHABET of class KEYWORD */
		ALPHABET(ETokenClass.KEYWORD),
			/** Token START_OF_SELECTION of class KEYWORD */
		START_OF_SELECTION(ETokenClass.KEYWORD),
			/** Token TABLEHEADER of class KEYWORD */
		TABLEHEADER(ETokenClass.KEYWORD),
			/** Token TEXTFRAME of class KEYWORD */
		TEXTFRAME(ETokenClass.KEYWORD),
			/** Token EXEC of class KEYWORD */
		EXEC(ETokenClass.KEYWORD),
			/** Token MINUS of class OPERATOR */
		MINUS(ETokenClass.OPERATOR),
			/** Token HEADING of class KEYWORD */
		HEADING(ETokenClass.KEYWORD),
			/** Token READONLY of class KEYWORD */
		READONLY(ETokenClass.KEYWORD),
			/** Token REPLACING of class KEYWORD */
		REPLACING(ETokenClass.KEYWORD),
			/** Token LINE of class SPECIAL */
		LINE(ETokenClass.SPECIAL),
			/** Token NOCHECK of class KEYWORD */
		NOCHECK(ETokenClass.KEYWORD),
			/** Token SHORTDUMP_ID of class KEYWORD */
		SHORTDUMP_ID(ETokenClass.KEYWORD),
			/** Token FKGE of class KEYWORD */
		FKGE(ETokenClass.KEYWORD),
			/** Token BEGIN_PROPERTY of class KEYWORD */
		BEGIN_PROPERTY(ETokenClass.KEYWORD),
			/** Token ZEROES of class KEYWORD */
		ZEROES(ETokenClass.KEYWORD),
			/** Token DATABASE of class KEYWORD */
		DATABASE(ETokenClass.KEYWORD),
			/** Token CS of class OPERATOR */
		CS(ETokenClass.OPERATOR),
			/** Token DB of class KEYWORD */
		DB(ETokenClass.KEYWORD),
			/** Token KEYLOC of class KEYWORD */
		KEYLOC(ETokenClass.KEYWORD),
			/** Token URSHIFT of class OPERATOR */
		URSHIFT(ETokenClass.OPERATOR),
			/** Token CH of class KEYWORD */
		CH(ETokenClass.KEYWORD),
			/** Token CF of class KEYWORD */
		CF(ETokenClass.KEYWORD),
			/** Token CD of class KEYWORD */
		CD(ETokenClass.KEYWORD),
			/** Token EXCEPTION_OBJECT of class KEYWORD */
		EXCEPTION_OBJECT(ETokenClass.KEYWORD),
			/** Token CO of class OPERATOR */
		CO(ETokenClass.OPERATOR),
			/** Token CP of class OPERATOR */
		CP(ETokenClass.OPERATOR),
			/** Token CN of class OPERATOR */
		CN(ETokenClass.OPERATOR),
			/** Token CLUSTERED of class KEYWORD */
		CLUSTERED(ETokenClass.KEYWORD),
			/** Token LT of class OPERATOR */
		LT(ETokenClass.OPERATOR),
			/** Token OPAQUE of class KEYWORD */
		OPAQUE(ETokenClass.KEYWORD),
			/** Token ATTENTION of class KEYWORD */
		ATTENTION(ETokenClass.KEYWORD),
			/** Token SENTINEL of class WHITESPACE */
		SENTINEL(ETokenClass.WHITESPACE),
			/** Token ME of class KEYWORD */
		ME(ETokenClass.KEYWORD),
			/** Token PUSHBUTTON of class KEYWORD */
		PUSHBUTTON(ETokenClass.KEYWORD),
			/** Token FIELDTYPE of class KEYWORD */
		FIELDTYPE(ETokenClass.KEYWORD),
			/** Token COUNT of class KEYWORD */
		COUNT(ETokenClass.KEYWORD),
			/** Token IDS of class KEYWORD */
		IDS(ETokenClass.KEYWORD),
			/** Token BREAK of class KEYWORD */
		BREAK(ETokenClass.KEYWORD),
			/** Token LITTLE of class KEYWORD */
		LITTLE(ETokenClass.KEYWORD),
			/** Token OVERRIDING of class KEYWORD */
		OVERRIDING(ETokenClass.KEYWORD),
			/** Token PATTERN of class KEYWORD */
		PATTERN(ETokenClass.KEYWORD),
			/** Token GREATER of class KEYWORD */
		GREATER(ETokenClass.KEYWORD),
			/** Token ESCAPE of class KEYWORD */
		ESCAPE(ETokenClass.KEYWORD),
			/** Token IS_NUMBER of class OPERATOR */
		IS_NUMBER(ETokenClass.OPERATOR),
			/** Token MM of class KEYWORD */
		MM(ETokenClass.KEYWORD),
			/** Token NONVARYING of class KEYWORD */
		NONVARYING(ETokenClass.KEYWORD),
			/** Token CCHAR of class KEYWORD */
		CCHAR(ETokenClass.KEYWORD),
			/** Token ALPHABETIC_UPPER of class KEYWORD */
		ALPHABETIC_UPPER(ETokenClass.KEYWORD),
			/** Token SKIP3 of class KEYWORD */
		SKIP3(ETokenClass.KEYWORD),
			/** Token SELECTION_SET of class KEYWORD */
		SELECTION_SET(ETokenClass.KEYWORD),
			/** Token NA of class OPERATOR */
		NA(ETokenClass.OPERATOR),
			/** Token BKWD of class KEYWORD */
		BKWD(ETokenClass.KEYWORD),
			/** Token SKIP1 of class KEYWORD */
		SKIP1(ETokenClass.KEYWORD),
			/** Token COMMENT_KEYWORD of class KEYWORD */
		COMMENT_KEYWORD(ETokenClass.KEYWORD),
			/** Token OTHERWISE of class KEYWORD */
		OTHERWISE(ETokenClass.KEYWORD),
			/** Token SORTED of class KEYWORD */
		SORTED(ETokenClass.KEYWORD),
			/** Token SKIP2 of class KEYWORD */
		SKIP2(ETokenClass.KEYWORD),
			/** Token EXCEPTION of class KEYWORD */
		EXCEPTION(ETokenClass.KEYWORD),
			/** Token EXIT of class KEYWORD */
		EXIT(ETokenClass.KEYWORD),
			/** Token COALESCE of class KEYWORD */
		COALESCE(ETokenClass.KEYWORD),
			/** Token RULE of class KEYWORD */
		RULE(ETokenClass.KEYWORD),
			/** Token NS of class OPERATOR */
		NS(ETokenClass.OPERATOR),
			/** Token PRIVATE of class KEYWORD */
		PRIVATE(ETokenClass.KEYWORD),
			/** Token NP of class OPERATOR */
		NP(ETokenClass.OPERATOR),
			/** Token NO of class KEYWORD */
		NO(ETokenClass.KEYWORD),
			/** Token EVERY of class KEYWORD */
		EVERY(ETokenClass.KEYWORD),
			/** Token ON of class KEYWORD */
		ON(ETokenClass.KEYWORD),
			/** Token SECONDARY of class KEYWORD */
		SECONDARY(ETokenClass.KEYWORD),
			/** Token TABLEFOOTER of class KEYWORD */
		TABLEFOOTER(ETokenClass.KEYWORD),
			/** Token MATCH of class KEYWORD */
		MATCH(ETokenClass.KEYWORD),
			/** Token NO_EXTENSION of class KEYWORD */
		NO_EXTENSION(ETokenClass.KEYWORD),
			/** Token IFN of class KEYWORD */
		IFN(ETokenClass.KEYWORD),
			/** Token OF of class KEYWORD */
		OF(ETokenClass.KEYWORD),
			/** Token EGI of class KEYWORD */
		EGI(ETokenClass.KEYWORD),
			/** Token STAMP of class KEYWORD */
		STAMP(ETokenClass.KEYWORD),
			/** Token COLAUTH of class KEYWORD */
		COLAUTH(ETokenClass.KEYWORD),
			/** Token HIGH of class KEYWORD */
		HIGH(ETokenClass.KEYWORD),
			/** Token ULINE of class KEYWORD */
		ULINE(ETokenClass.KEYWORD),
			/** Token BUFFERED of class KEYWORD */
		BUFFERED(ETokenClass.KEYWORD),
			/** Token PIPELINED of class KEYWORD */
		PIPELINED(ETokenClass.KEYWORD),
			/** Token STATISTICS of class KEYWORD */
		STATISTICS(ETokenClass.KEYWORD),
			/** Token OR of class OPERATOR */
		OR(ETokenClass.OPERATOR),
			/** Token FILTER of class KEYWORD */
		FILTER(ETokenClass.KEYWORD),
			/** Token BOTTOM of class KEYWORD */
		BOTTOM(ETokenClass.KEYWORD),
			/** Token JUSTIFIED of class KEYWORD */
		JUSTIFIED(ETokenClass.KEYWORD),
			/** Token GETTYPE of class KEYWORD */
		GETTYPE(ETokenClass.KEYWORD),
			/** Token OPENDATASOURCE of class KEYWORD */
		OPENDATASOURCE(ETokenClass.KEYWORD),
			/** Token FOREACH of class KEYWORD */
		FOREACH(ETokenClass.KEYWORD),
			/** Token PH of class KEYWORD */
		PH(ETokenClass.KEYWORD),
			/** Token IGNORING of class KEYWORD */
		IGNORING(ETokenClass.KEYWORD),
			/** Token COLLECT of class KEYWORD */
		COLLECT(ETokenClass.KEYWORD),
			/** Token CURSOR of class KEYWORD */
		CURSOR(ETokenClass.KEYWORD),
			/** Token PF of class KEYWORD */
		PF(ETokenClass.KEYWORD),
			/** Token TIMESTAMP of class KEYWORD */
		TIMESTAMP(ETokenClass.KEYWORD),
			/** Token UPPER of class KEYWORD */
		UPPER(ETokenClass.KEYWORD),
			/** Token OTHERS of class KEYWORD */
		OTHERS(ETokenClass.KEYWORD),
			/** Token SELECTION_SCREEN of class KEYWORD */
		SELECTION_SCREEN(ETokenClass.KEYWORD),
			/** Token MEMBER of class KEYWORD */
		MEMBER(ETokenClass.KEYWORD),
			/** Token IMPORTED of class KEYWORD */
		IMPORTED(ETokenClass.KEYWORD),
			/** Token REQUESTED of class KEYWORD */
		REQUESTED(ETokenClass.KEYWORD),
			/** Token HIDE of class KEYWORD */
		HIDE(ETokenClass.KEYWORD),
			/** Token BIT_AND of class OPERATOR */
		BIT_AND(ETokenClass.OPERATOR),
			/** Token YEAR of class KEYWORD */
		YEAR(ETokenClass.KEYWORD),
			/** Token REENTRANT of class KEYWORD */
		REENTRANT(ETokenClass.KEYWORD),
			/** Token UINT of class KEYWORD */
		UINT(ETokenClass.KEYWORD),
			/** Token VALIST of class KEYWORD */
		VALIST(ETokenClass.KEYWORD),
			/** Token PRAGMA of class KEYWORD */
		PRAGMA(ETokenClass.KEYWORD),
			/** Token MAX of class KEYWORD */
		MAX(ETokenClass.KEYWORD),
			/** Token CONSTANTS of class KEYWORD */
		CONSTANTS(ETokenClass.KEYWORD),
			/** Token ID of class KEYWORD */
		ID(ETokenClass.KEYWORD),
			/** Token CROSS of class KEYWORD */
		CROSS(ETokenClass.KEYWORD),
			/** Token CONTEXT of class KEYWORD */
		CONTEXT(ETokenClass.KEYWORD),
			/** Token LENGTH of class KEYWORD */
		LENGTH(ETokenClass.KEYWORD),
			/** Token STRINGSIZE of class KEYWORD */
		STRINGSIZE(ETokenClass.KEYWORD),
			/** Token IF of class KEYWORD */
		IF(ETokenClass.KEYWORD),
			/** Token BACKWARDS of class KEYWORD */
		BACKWARDS(ETokenClass.KEYWORD),
			/** Token BOOLEAN of class KEYWORD */
		BOOLEAN(ETokenClass.KEYWORD),
			/** Token IN of class KEYWORD */
		IN(ETokenClass.KEYWORD),
			/** Token IP of class KEYWORD */
		IP(ETokenClass.KEYWORD),
			/** Token IS of class KEYWORD */
		IS(ETokenClass.KEYWORD),
			/** Token CALLING of class KEYWORD */
		CALLING(ETokenClass.KEYWORD),
			/** Token ENSURE of class KEYWORD */
		ENSURE(ETokenClass.KEYWORD),
			/** Token CBYTE of class KEYWORD */
		CBYTE(ETokenClass.KEYWORD),
			/** Token UNLESS of class KEYWORD */
		UNLESS(ETokenClass.KEYWORD),
			/** Token OBJECT_REFERENCE of class KEYWORD */
		OBJECT_REFERENCE(ETokenClass.KEYWORD),
			/** Token VOLATILE of class KEYWORD */
		VOLATILE(ETokenClass.KEYWORD),
			/** Token DBCS of class KEYWORD */
		DBCS(ETokenClass.KEYWORD),
			/** Token TERMINAL of class KEYWORD */
		TERMINAL(ETokenClass.KEYWORD),
			/** Token INDICATE of class KEYWORD */
		INDICATE(ETokenClass.KEYWORD),
			/** Token UPON of class KEYWORD */
		UPON(ETokenClass.KEYWORD),
			/** Token DEFAULT of class KEYWORD */
		DEFAULT(ETokenClass.KEYWORD),
			/** Token B_XOR of class KEYWORD */
		B_XOR(ETokenClass.KEYWORD),
			/** Token CONCATENATION of class OPERATOR */
		CONCATENATION(ETokenClass.OPERATOR),
			/** Token PURGE of class KEYWORD */
		PURGE(ETokenClass.KEYWORD),
			/** Token ERRLVL of class KEYWORD */
		ERRLVL(ETokenClass.KEYWORD),
			/** Token EVENT of class KEYWORD */
		EVENT(ETokenClass.KEYWORD),
			/** Token REINTRODUCE of class KEYWORD */
		REINTRODUCE(ETokenClass.KEYWORD),
			/** Token ECF of class KEYWORD */
		ECF(ETokenClass.KEYWORD),
			/** Token SPECIFIED of class KEYWORD */
		SPECIFIED(ETokenClass.KEYWORD),
			/** Token REEL of class KEYWORD */
		REEL(ETokenClass.KEYWORD),
			/** Token PROCEDURE of class KEYWORD */
		PROCEDURE(ETokenClass.KEYWORD),
			/** Token ECK of class KEYWORD */
		ECK(ETokenClass.KEYWORD),
			/** Token CONCATENATE of class KEYWORD */
		CONCATENATE(ETokenClass.KEYWORD),
			/** Token OPTIONAL of class KEYWORD */
		OPTIONAL(ETokenClass.KEYWORD),
			/** Token MAP of class KEYWORD */
		MAP(ETokenClass.KEYWORD),
			/** Token EXIT_FOR of class KEYWORD */
		EXIT_FOR(ETokenClass.KEYWORD),
			/** Token BIT_NOT of class OPERATOR */
		BIT_NOT(ETokenClass.OPERATOR),
			/** Token THROUGH of class KEYWORD */
		THROUGH(ETokenClass.KEYWORD),
			/** Token IMMEDIATE of class KEYWORD */
		IMMEDIATE(ETokenClass.KEYWORD),
			/** Token NOSTACKFRAME of class KEYWORD */
		NOSTACKFRAME(ETokenClass.KEYWORD),
			/** Token MINUTE of class KEYWORD */
		MINUTE(ETokenClass.KEYWORD),
			/** Token DBCC of class KEYWORD */
		DBCC(ETokenClass.KEYWORD),
			/** Token POINTERTO of class OPERATOR */
		POINTERTO(ETokenClass.OPERATOR),
			/** Token PERFORM of class KEYWORD */
		PERFORM(ETokenClass.KEYWORD),
			/** Token INCLUDING of class KEYWORD */
		INCLUDING(ETokenClass.KEYWORD),
			/** Token VS of class KEYWORD */
		VS(ETokenClass.KEYWORD),
			/** Token END_READ of class KEYWORD */
		END_READ(ETokenClass.KEYWORD),
			/** Token REDO of class KEYWORD */
		REDO(ETokenClass.KEYWORD),
			/** Token MOD of class OPERATOR */
		MOD(ETokenClass.OPERATOR),
			/** Token END_OF_DEFINITION of class KEYWORD */
		END_OF_DEFINITION(ETokenClass.KEYWORD),
			/** Token EXTERN of class KEYWORD */
		EXTERN(ETokenClass.KEYWORD),
			/** Token VB of class KEYWORD */
		VB(ETokenClass.KEYWORD),
			/** Token ENDEXEC of class KEYWORD */
		ENDEXEC(ETokenClass.KEYWORD),
			/** Token SUBTRACT of class KEYWORD */
		SUBTRACT(ETokenClass.KEYWORD),
			/** Token MOV of class KEYWORD */
		MOV(ETokenClass.KEYWORD),
			/** Token REDIM of class KEYWORD */
		REDIM(ETokenClass.KEYWORD),
			/** Token SYMBOLIC of class KEYWORD */
		SYMBOLIC(ETokenClass.KEYWORD),
			/** Token DAYLIGHT of class KEYWORD */
		DAYLIGHT(ETokenClass.KEYWORD),
			/** Token TRANSPOSE of class OPERATOR */
		TRANSPOSE(ETokenClass.OPERATOR),
			/** Token ANALYZER of class KEYWORD */
		ANALYZER(ETokenClass.KEYWORD),
			/** Token ARROWSTAR of class OPERATOR */
		ARROWSTAR(ETokenClass.OPERATOR),
			/** Token UP of class KEYWORD */
		UP(ETokenClass.KEYWORD),
			/** Token REWIND of class KEYWORD */
		REWIND(ETokenClass.KEYWORD),
			/** Token INCLUDE of class KEYWORD */
		INCLUDE(ETokenClass.KEYWORD),
			/** Token ARITHMETIC of class KEYWORD */
		ARITHMETIC(ETokenClass.KEYWORD),
			/** Token ENDCLASS of class KEYWORD */
		ENDCLASS(ETokenClass.KEYWORD),
			/** Token DIVISION of class KEYWORD */
		DIVISION(ETokenClass.KEYWORD),
			/** Token COMMON of class KEYWORD */
		COMMON(ETokenClass.KEYWORD),
			/** Token DBMS of class KEYWORD */
		DBMS(ETokenClass.KEYWORD),
			/** Token MNT of class KEYWORD */
		MNT(ETokenClass.KEYWORD),
			/** Token HANDLES of class KEYWORD */
		HANDLES(ETokenClass.KEYWORD),
			/** Token VIRTUAL of class KEYWORD */
		VIRTUAL(ETokenClass.KEYWORD),
			/** Token HANDLER of class KEYWORD */
		HANDLER(ETokenClass.KEYWORD),
			/** Token BLOB of class KEYWORD */
		BLOB(ETokenClass.KEYWORD),
			/** Token CONTROL of class KEYWORD */
		CONTROL(ETokenClass.KEYWORD),
			/** Token XU of class KEYWORD */
		XU(ETokenClass.KEYWORD),
			/** Token SOURCE_COMPUTER of class KEYWORD */
		SOURCE_COMPUTER(ETokenClass.KEYWORD),
			/** Token NO_DISPLAY of class KEYWORD */
		NO_DISPLAY(ETokenClass.KEYWORD),
			/** Token XN of class KEYWORD */
		XN(ETokenClass.KEYWORD),
			/** Token MESSAGES of class KEYWORD */
		MESSAGES(ETokenClass.KEYWORD),
			/** Token PREPROCESSOR_XINCLUDE of class KEYWORD */
		PREPROCESSOR_XINCLUDE(ETokenClass.KEYWORD),
			/** Token XML_EVENT of class KEYWORD */
		XML_EVENT(ETokenClass.KEYWORD),
			/** Token NUMBER of class KEYWORD */
		NUMBER(ETokenClass.KEYWORD),
			/** Token BROWSE of class KEYWORD */
		BROWSE(ETokenClass.KEYWORD),
			/** Token CHARACTER_LITERAL of class LITERAL */
		CHARACTER_LITERAL(ETokenClass.LITERAL),
			/** Token COMMITTED of class KEYWORD */
		COMMITTED(ETokenClass.KEYWORD),
			/** Token RECURSIVE of class KEYWORD */
		RECURSIVE(ETokenClass.KEYWORD),
			/** Token SEPARATE_STATIC of class KEYWORD */
		SEPARATE_STATIC(ETokenClass.KEYWORD),
			/** Token ROLLBACK of class KEYWORD */
		ROLLBACK(ETokenClass.KEYWORD),
			/** Token WX of class KEYWORD */
		WX(ETokenClass.KEYWORD),
			/** Token TEMPLATE_CODE_BEGIN of class SPECIAL */
		TEMPLATE_CODE_BEGIN(ETokenClass.SPECIAL),
			/** Token ILLEGAL_ESCAPE_SEQUENCE of class ERROR */
		ILLEGAL_ESCAPE_SEQUENCE(ETokenClass.ERROR),
			/** Token LINE_KEYWORD of class KEYWORD */
		LINE_KEYWORD(ETokenClass.KEYWORD),
			/** Token RISK of class KEYWORD */
		RISK(ETokenClass.KEYWORD),
			/** Token REAL of class KEYWORD */
		REAL(ETokenClass.KEYWORD),
			/** Token NOCOPY of class KEYWORD */
		NOCOPY(ETokenClass.KEYWORD),
			/** Token ENDFOREACH of class KEYWORD */
		ENDFOREACH(ETokenClass.KEYWORD),
			/** Token READ of class KEYWORD */
		READ(ETokenClass.KEYWORD),
			/** Token RECONFIGURE of class KEYWORD */
		RECONFIGURE(ETokenClass.KEYWORD),
			/** Token REPLACEMENT of class KEYWORD */
		REPLACEMENT(ETokenClass.KEYWORD),
			/** Token STYLE of class KEYWORD */
		STYLE(ETokenClass.KEYWORD),
			/** Token MESSAGE_ID of class KEYWORD */
		MESSAGE_ID(ETokenClass.KEYWORD),
			/** Token IMPORTS of class KEYWORD */
		IMPORTS(ETokenClass.KEYWORD),
			/** Token CALL of class KEYWORD */
		CALL(ETokenClass.KEYWORD),
			/** Token ORLANY of class KEYWORD */
		ORLANY(ETokenClass.KEYWORD),
			/** Token SIZE of class KEYWORD */
		SIZE(ETokenClass.KEYWORD),
			/** Token RH of class KEYWORD */
		RH(ETokenClass.KEYWORD),
			/** Token ERASE of class KEYWORD */
		ERASE(ETokenClass.KEYWORD),
			/** Token RF of class KEYWORD */
		RF(ETokenClass.KEYWORD),
			/** Token RD of class KEYWORD */
		RD(ETokenClass.KEYWORD),
			/** Token PARFOR of class KEYWORD */
		PARFOR(ETokenClass.KEYWORD),
			/** Token WITH_HEADING of class KEYWORD */
		WITH_HEADING(ETokenClass.KEYWORD),
			/** Token TITLEBAR of class KEYWORD */
		TITLEBAR(ETokenClass.KEYWORD),
			/** Token EXECUTE of class KEYWORD */
		EXECUTE(ETokenClass.KEYWORD),
			/** Token STATUSINFO of class KEYWORD */
		STATUSINFO(ETokenClass.KEYWORD),
			/** Token SHIFT of class KEYWORD */
		SHIFT(ETokenClass.KEYWORD),
			/** Token EXPLICIT of class KEYWORD */
		EXPLICIT(ETokenClass.KEYWORD),
			/** Token REQUIRE_ONCE of class KEYWORD */
		REQUIRE_ONCE(ETokenClass.KEYWORD),
			/** Token PLUSEQ of class OPERATOR */
		PLUSEQ(ETokenClass.OPERATOR),
			/** Token NOINLINE of class KEYWORD */
		NOINLINE(ETokenClass.KEYWORD),
			/** Token END_MULTIPLY of class KEYWORD */
		END_MULTIPLY(ETokenClass.KEYWORD),
			/** Token EXTEND of class KEYWORD */
		EXTEND(ETokenClass.KEYWORD),
			/** Token SUFFIX of class KEYWORD */
		SUFFIX(ETokenClass.KEYWORD),
			/** Token KEEPING of class KEYWORD */
		KEEPING(ETokenClass.KEYWORD),
			/** Token REPLACE of class KEYWORD */
		REPLACE(ETokenClass.KEYWORD),
			/** Token COLUMN of class KEYWORD */
		COLUMN(ETokenClass.KEYWORD),
			/** Token TRUSTED of class KEYWORD */
		TRUSTED(ETokenClass.KEYWORD),
			/** Token ENDFUNCTION of class KEYWORD */
		ENDFUNCTION(ETokenClass.KEYWORD),
			/** Token CPPDECL of class KEYWORD */
		CPPDECL(ETokenClass.KEYWORD),
			/** Token ORACLE of class KEYWORD */
		ORACLE(ETokenClass.KEYWORD),
			/** Token RSHIFT of class OPERATOR */
		RSHIFT(ETokenClass.OPERATOR),
			/** Token ROWCOUNT of class KEYWORD */
		ROWCOUNT(ETokenClass.KEYWORD),
			/** Token INSTEADOF of class KEYWORD */
		INSTEADOF(ETokenClass.KEYWORD),
			/** Token NOSTRINGSIZE of class KEYWORD */
		NOSTRINGSIZE(ETokenClass.KEYWORD),
			/** Token TO of class KEYWORD */
		TO(ETokenClass.KEYWORD),
			/** Token FIRST_LINE of class KEYWORD */
		FIRST_LINE(ETokenClass.KEYWORD),
			/** Token TP of class KEYWORD */
		TP(ETokenClass.KEYWORD),
			/** Token HELP_REQUEST of class KEYWORD */
		HELP_REQUEST(ETokenClass.KEYWORD),
			/** Token REUSE of class KEYWORD */
		REUSE(ETokenClass.KEYWORD),
			/** Token TODO of class KEYWORD */
		TODO(ETokenClass.KEYWORD),
			/** Token INSTANCEOF of class KEYWORD */
		INSTANCEOF(ETokenClass.KEYWORD),
			/** Token MIN of class KEYWORD */
		MIN(ETokenClass.KEYWORD),
			/** Token FIXME of class KEYWORD */
		FIXME(ETokenClass.KEYWORD),
			/** Token PREFERRED of class KEYWORD */
		PREFERRED(ETokenClass.KEYWORD),
			/** Token CURRENT of class KEYWORD */
		CURRENT(ETokenClass.KEYWORD),
			/** Token CHANGE of class KEYWORD */
		CHANGE(ETokenClass.KEYWORD),
			/** Token ANDIF of class KEYWORD */
		ANDIF(ETokenClass.KEYWORD),
			/** Token SD of class KEYWORD */
		SD(ETokenClass.KEYWORD),
			/** Token NULLS of class KEYWORD */
		NULLS(ETokenClass.KEYWORD),
			/** Token COMMIT of class KEYWORD */
		COMMIT(ETokenClass.KEYWORD),
			/** Token WITH_TITLE of class KEYWORD */
		WITH_TITLE(ETokenClass.KEYWORD),
			/** Token END_BLOCK of class KEYWORD */
		END_BLOCK(ETokenClass.KEYWORD),
			/** Token HYP of class KEYWORD */
		HYP(ETokenClass.KEYWORD),
			/** Token WHEN of class KEYWORD */
		WHEN(ETokenClass.KEYWORD),
			/** Token MOVE of class KEYWORD */
		MOVE(ETokenClass.KEYWORD),
			/** Token FILLFACTOR of class KEYWORD */
		FILLFACTOR(ETokenClass.KEYWORD),
			/** Token BIT_XOR of class OPERATOR */
		BIT_XOR(ETokenClass.OPERATOR),
			/** Token ADDRESSOF of class KEYWORD */
		ADDRESSOF(ETokenClass.KEYWORD),
			/** Token DIM of class KEYWORD */
		DIM(ETokenClass.KEYWORD),
			/** Token WINDOWS of class KEYWORD */
		WINDOWS(ETokenClass.KEYWORD),
			/** Token DECLARE of class KEYWORD */
		DECLARE(ETokenClass.KEYWORD),
			/** Token PROGRAM of class KEYWORD */
		PROGRAM(ETokenClass.KEYWORD),
			/** Token ASSIGNMENT of class OPERATOR */
		ASSIGNMENT(ETokenClass.OPERATOR),
			/** Token DIV of class OPERATOR */
		DIV(ETokenClass.OPERATOR),
			/** Token BETWEEN of class KEYWORD */
		BETWEEN(ETokenClass.KEYWORD),
			/** Token AGGREGATE of class KEYWORD */
		AGGREGATE(ETokenClass.KEYWORD),
			/** Token BYVALUE of class KEYWORD */
		BYVALUE(ETokenClass.KEYWORD),
			/** Token CHANGING of class KEYWORD */
		CHANGING(ETokenClass.KEYWORD),
			/** Token CAST of class OPERATOR */
		CAST(ETokenClass.OPERATOR),
			/** Token END_COMPUTE of class KEYWORD */
		END_COMPUTE(ETokenClass.KEYWORD),
			/** Token CURRENT_TIME of class KEYWORD */
		CURRENT_TIME(ETokenClass.KEYWORD),
			/** Token LIST_PROCESSING of class KEYWORD */
		LIST_PROCESSING(ETokenClass.KEYWORD),
			/** Token DSC of class KEYWORD */
		DSC(ETokenClass.KEYWORD),
			/** Token CASE of class KEYWORD */
		CASE(ETokenClass.KEYWORD),
			/** Token DEALLOCATE of class KEYWORD */
		DEALLOCATE(ETokenClass.KEYWORD),
			/** Token BYVAL of class KEYWORD */
		BYVAL(ETokenClass.KEYWORD),
			/** Token ORELSE of class KEYWORD */
		ORELSE(ETokenClass.KEYWORD),
			/** Token REQUEUE of class KEYWORD */
		REQUEUE(ETokenClass.KEYWORD),
			/** Token CASCADE of class KEYWORD */
		CASCADE(ETokenClass.KEYWORD),
			/** Token NO_QUICK_BLOCKS of class KEYWORD */
		NO_QUICK_BLOCKS(ETokenClass.KEYWORD),
			/** Token SUB_QUEUE_3 of class KEYWORD */
		SUB_QUEUE_3(ETokenClass.KEYWORD),
			/** Token FULL of class KEYWORD */
		FULL(ETokenClass.KEYWORD),
			/** Token INPUT_OUTPUT of class KEYWORD */
		INPUT_OUTPUT(ETokenClass.KEYWORD),
			/** Token SUB_QUEUE_1 of class KEYWORD */
		SUB_QUEUE_1(ETokenClass.KEYWORD),
			/** Token SUB_QUEUE_2 of class KEYWORD */
		SUB_QUEUE_2(ETokenClass.KEYWORD),
			/** Token SHARING of class KEYWORD */
		SHARING(ETokenClass.KEYWORD),
			/** Token REMOVAL of class KEYWORD */
		REMOVAL(ETokenClass.KEYWORD),
			/** Token SHARE of class KEYWORD */
		SHARE(ETokenClass.KEYWORD),
			/** Token ENDING of class KEYWORD */
		ENDING(ETokenClass.KEYWORD),
			/** Token PF_STATUS of class KEYWORD */
		PF_STATUS(ETokenClass.KEYWORD),
			/** Token INTERFACES of class KEYWORD */
		INTERFACES(ETokenClass.KEYWORD),
			/** Token SYNONYM of class KEYWORD */
		SYNONYM(ETokenClass.KEYWORD),
			/** Token DUP of class KEYWORD */
		DUP(ETokenClass.KEYWORD),
			/** Token SETUSER of class KEYWORD */
		SETUSER(ETokenClass.KEYWORD),
			/** Token CLEANUP of class KEYWORD */
		CLEANUP(ETokenClass.KEYWORD),
			/** Token DUPLICATES of class KEYWORD */
		DUPLICATES(ETokenClass.KEYWORD),
			/** Token DISPOSE of class KEYWORD */
		DISPOSE(ETokenClass.KEYWORD),
			/** Token ORDINAL of class KEYWORD */
		ORDINAL(ETokenClass.KEYWORD),
			/** Token PRIMARY of class KEYWORD */
		PRIMARY(ETokenClass.KEYWORD),
			/** Token RANGES of class KEYWORD */
		RANGES(ETokenClass.KEYWORD),
			/** Token DTV of class KEYWORD */
		DTV(ETokenClass.KEYWORD),
			/** Token MUL of class KEYWORD */
		MUL(ETokenClass.KEYWORD),
			/** Token PASCAL of class KEYWORD */
		PASCAL(ETokenClass.KEYWORD),
			/** Token SEQUENCE of class KEYWORD */
		SEQUENCE(ETokenClass.KEYWORD),
			/** Token PRIORITY of class KEYWORD */
		PRIORITY(ETokenClass.KEYWORD),
			/** Token BOUNDS of class KEYWORD */
		BOUNDS(ETokenClass.KEYWORD),
			/** Token UNSTRING of class KEYWORD */
		UNSTRING(ETokenClass.KEYWORD),
			/** Token ANDEQ of class OPERATOR */
		ANDEQ(ETokenClass.OPERATOR),
			/** Token FINISH of class KEYWORD */
		FINISH(ETokenClass.KEYWORD),
			/** Token MEMORY of class KEYWORD */
		MEMORY(ETokenClass.KEYWORD),
			/** Token CATCH of class KEYWORD */
		CATCH(ETokenClass.KEYWORD),
			/** Token NOOVERFLOW of class KEYWORD */
		NOOVERFLOW(ETokenClass.KEYWORD),
			/** Token INTEGER_DIV of class OPERATOR */
		INTEGER_DIV(ETokenClass.OPERATOR),
			/** Token BACKSLASH of class SPECIAL */
		BACKSLASH(ETokenClass.SPECIAL),
			/** Token DESTRUCTOR of class KEYWORD */
		DESTRUCTOR(ETokenClass.KEYWORD),
			/** Token YY of class KEYWORD */
		YY(ETokenClass.KEYWORD),
			/** Token ORDER of class KEYWORD */
		ORDER(ETokenClass.KEYWORD),
			/** Token SQLDATA of class KEYWORD */
		SQLDATA(ETokenClass.KEYWORD),
			/** Token RESUMABLE of class KEYWORD */
		RESUMABLE(ETokenClass.KEYWORD),
			/** Token BYTE_NA of class OPERATOR */
		BYTE_NA(ETokenClass.OPERATOR),
			/** Token MATCHCODE of class KEYWORD */
		MATCHCODE(ETokenClass.KEYWORD),
			/** Token UNPIVOT of class KEYWORD */
		UNPIVOT(ETokenClass.KEYWORD),
			/** Token PRAGMA_DIRECTIVE of class SPECIAL */
		PRAGMA_DIRECTIVE(ETokenClass.SPECIAL),
			/** Token ATTRIBUTE of class KEYWORD */
		ATTRIBUTE(ETokenClass.KEYWORD),
			/** Token MSG of class KEYWORD */
		MSG(ETokenClass.KEYWORD),
			/** Token OPENXML of class KEYWORD */
		OPENXML(ETokenClass.KEYWORD),
			/** Token MSK of class KEYWORD */
		MSK(ETokenClass.KEYWORD),
			/** Token END_XML of class KEYWORD */
		END_XML(ETokenClass.KEYWORD),
			/** Token SCROLL of class KEYWORD */
		SCROLL(ETokenClass.KEYWORD),
			/** Token BYTE_NS of class OPERATOR */
		BYTE_NS(ETokenClass.OPERATOR),
			/** Token NO_GAPS of class KEYWORD */
		NO_GAPS(ETokenClass.KEYWORD),
			/** Token CHARACTERS of class KEYWORD */
		CHARACTERS(ETokenClass.KEYWORD),
			/** Token PRESENT of class KEYWORD */
		PRESENT(ETokenClass.KEYWORD),
			/** Token RSHIFTEQ of class OPERATOR */
		RSHIFTEQ(ETokenClass.OPERATOR),
			/** Token TALLYING of class KEYWORD */
		TALLYING(ETokenClass.KEYWORD),
			/** Token ALPHANUMERIC of class KEYWORD */
		ALPHANUMERIC(ETokenClass.KEYWORD),
			/** Token PROC of class KEYWORD */
		PROC(ETokenClass.KEYWORD),
			/** Token ENHANCEMENT of class KEYWORD */
		ENHANCEMENT(ETokenClass.KEYWORD),
			/** Token TEMPLATE_CODE_END of class SPECIAL */
		TEMPLATE_CODE_END(ETokenClass.SPECIAL),
			/** Token DOT of class DELIMITER */
		DOT(ETokenClass.DELIMITER),
			/** Token CURRENT_TIMESTAMP of class KEYWORD */
		CURRENT_TIMESTAMP(ETokenClass.KEYWORD),
			/** Token CONTROLLED of class KEYWORD */
		CONTROLLED(ETokenClass.KEYWORD),
			/** Token LINAGE_COUNTER of class KEYWORD */
		LINAGE_COUNTER(ETokenClass.KEYWORD),
			/** Token GOBACK of class KEYWORD */
		GOBACK(ETokenClass.KEYWORD),
			/** Token ASSIGNING of class KEYWORD */
		ASSIGNING(ETokenClass.KEYWORD),
			/** Token INDEXES of class KEYWORD */
		INDEXES(ETokenClass.KEYWORD),
			/** Token SUBSCRIPTRANGE of class KEYWORD */
		SUBSCRIPTRANGE(ETokenClass.KEYWORD),
			/** Token ASSEMBLY of class KEYWORD */
		ASSEMBLY(ETokenClass.KEYWORD),
			/** Token RESERVE of class KEYWORD */
		RESERVE(ETokenClass.KEYWORD),
			/** Token FACTORY of class KEYWORD */
		FACTORY(ETokenClass.KEYWORD),
			/** Token MAXLEN of class KEYWORD */
		MAXLEN(ETokenClass.KEYWORD),
			/** Token CIRCULAR of class KEYWORD */
		CIRCULAR(ETokenClass.KEYWORD),
			/** Token INDEXED of class KEYWORD */
		INDEXED(ETokenClass.KEYWORD),
			/** Token NESTED of class KEYWORD */
		NESTED(ETokenClass.KEYWORD),
			/** Token REVERT of class KEYWORD */
		REVERT(ETokenClass.KEYWORD),
			/** Token FETCHABLE of class KEYWORD */
		FETCHABLE(ETokenClass.KEYWORD),
			/** Token CHARSETID of class KEYWORD */
		CHARSETID(ETokenClass.KEYWORD),
			/** Token ABSOLUTE of class KEYWORD */
		ABSOLUTE(ETokenClass.KEYWORD),
			/** Token PACKAGE of class KEYWORD */
		PACKAGE(ETokenClass.KEYWORD),
			/** Token HIGH_VALUES of class KEYWORD */
		HIGH_VALUES(ETokenClass.KEYWORD),
			/** Token ROW of class KEYWORD */
		ROW(ETokenClass.KEYWORD),
			/** Token SUBMATCHES of class KEYWORD */
		SUBMATCHES(ETokenClass.KEYWORD),
			/** Token WHEN_COMPILED of class KEYWORD */
		WHEN_COMPILED(ETokenClass.KEYWORD),
			/** Token POINTER of class KEYWORD */
		POINTER(ETokenClass.KEYWORD),
			/** Token CODE_SET of class KEYWORD */
		CODE_SET(ETokenClass.KEYWORD),
			/** Token PROCEED of class KEYWORD */
		PROCEED(ETokenClass.KEYWORD),
			/** Token TYPES of class KEYWORD */
		TYPES(ETokenClass.KEYWORD),
			/** Token FAR of class KEYWORD */
		FAR(ETokenClass.KEYWORD),
			/** Token QUOTES of class KEYWORD */
		QUOTES(ETokenClass.KEYWORD),
			/** Token ORIF of class OPERATOR */
		ORIF(ETokenClass.OPERATOR),
			/** Token CLEAR of class KEYWORD */
		CLEAR(ETokenClass.KEYWORD),
			/** Token CREATE of class KEYWORD */
		CREATE(ETokenClass.KEYWORD),
			/** Token STRING_LITERAL of class LITERAL */
		STRING_LITERAL(ETokenClass.LITERAL),
			/** Token DECLARATIVES of class KEYWORD */
		DECLARATIVES(ETokenClass.KEYWORD),
			/** Token USER_DEFAULT of class KEYWORD */
		USER_DEFAULT(ETokenClass.KEYWORD),
			/** Token SPOTS of class KEYWORD */
		SPOTS(ETokenClass.KEYWORD),
			/** Token LIKEC of class KEYWORD */
		LIKEC(ETokenClass.KEYWORD),
			/** Token ROUNDED of class KEYWORD */
		ROUNDED(ETokenClass.KEYWORD),
			/** Token NOCOMPRESS of class KEYWORD */
		NOCOMPRESS(ETokenClass.KEYWORD),
			/** Token DUPLICATE of class KEYWORD */
		DUPLICATE(ETokenClass.KEYWORD),
			/** Token EQEQ of class OPERATOR */
		EQEQ(ETokenClass.OPERATOR),
			/** Token DEFINING of class KEYWORD */
		DEFINING(ETokenClass.KEYWORD),
			/** Token STANDARD_2 of class KEYWORD */
		STANDARD_2(ETokenClass.KEYWORD),
			/** Token JOB of class KEYWORD */
		JOB(ETokenClass.KEYWORD),
			/** Token ELLIPSIS of class OPERATOR */
		ELLIPSIS(ETokenClass.OPERATOR),
			/** Token DELTA of class KEYWORD */
		DELTA(ETokenClass.KEYWORD),
			/** Token POWER of class OPERATOR */
		POWER(ETokenClass.OPERATOR),
			/** Token STOP of class KEYWORD */
		STOP(ETokenClass.KEYWORD),
			/** Token SORT_MESSAGE of class KEYWORD */
		SORT_MESSAGE(ETokenClass.KEYWORD),
			/** Token VALUE of class KEYWORD */
		VALUE(ETokenClass.KEYWORD),
			/** Token STANDARD_1 of class KEYWORD */
		STANDARD_1(ETokenClass.KEYWORD),
			/** Token TABLESAMPLE of class KEYWORD */
		TABLESAMPLE(ETokenClass.KEYWORD),
			/** Token RESTRICT of class KEYWORD */
		RESTRICT(ETokenClass.KEYWORD),
			/** Token REWRITE of class KEYWORD */
		REWRITE(ETokenClass.KEYWORD),
			/** Token ALTERNATE of class KEYWORD */
		ALTERNATE(ETokenClass.KEYWORD),
			/** Token NO_TOPOFPAGE of class KEYWORD */
		NO_TOPOFPAGE(ETokenClass.KEYWORD),
			/** Token ENHANCEMENT_POINT of class KEYWORD */
		ENHANCEMENT_POINT(ETokenClass.KEYWORD),
			/** Token PACKED of class KEYWORD */
		PACKED(ETokenClass.KEYWORD),
			/** Token TYPEOF of class KEYWORD */
		TYPEOF(ETokenClass.KEYWORD),
			/** Token LITERAL_OPERATOR of class OPERATOR */
		LITERAL_OPERATOR(ETokenClass.OPERATOR),
			/** Token AGENT of class KEYWORD */
		AGENT(ETokenClass.KEYWORD),
			/** Token SWITCHSTATES of class KEYWORD */
		SWITCHSTATES(ETokenClass.KEYWORD),
			/** Token SELECTIONS of class KEYWORD */
		SELECTIONS(ETokenClass.KEYWORD),
			/** Token ELSIF of class KEYWORD */
		ELSIF(ETokenClass.KEYWORD),
			/** Token ANSI of class KEYWORD */
		ANSI(ETokenClass.KEYWORD),
			/** Token SYSTEM_EXCEPTIONS of class KEYWORD */
		SYSTEM_EXCEPTIONS(ETokenClass.KEYWORD),
			/** Token CONSTRUCTOR of class KEYWORD */
		CONSTRUCTOR(ETokenClass.KEYWORD),
			/** Token NOSUBSCRIPTRANGE of class KEYWORD */
		NOSUBSCRIPTRANGE(ETokenClass.KEYWORD),
			/** Token NAN of class KEYWORD */
		NAN(ETokenClass.KEYWORD),
			/** Token ILLEGAL_CHARACTER of class ERROR */
		ILLEGAL_CHARACTER(ETokenClass.ERROR),
			/** Token PROTECTED of class KEYWORD */
		PROTECTED(ETokenClass.KEYWORD),
			/** Token ENABLING of class KEYWORD */
		ENABLING(ETokenClass.KEYWORD),
			/** Token ALTER of class KEYWORD */
		ALTER(ETokenClass.KEYWORD),
			/** Token PROPERTIES of class KEYWORD */
		PROPERTIES(ETokenClass.KEYWORD),
			/** Token PARAMETERS of class KEYWORD */
		PARAMETERS(ETokenClass.KEYWORD),
			/** Token TOTAL of class KEYWORD */
		TOTAL(ETokenClass.KEYWORD),
			/** Token TALLY of class KEYWORD */
		TALLY(ETokenClass.KEYWORD),
			/** Token TOP_LINES of class KEYWORD */
		TOP_LINES(ETokenClass.KEYWORD),
			/** Token CLNG of class KEYWORD */
		CLNG(ETokenClass.KEYWORD),
			/** Token CLOB of class KEYWORD */
		CLOB(ETokenClass.KEYWORD),
			/** Token RESULT_CACHE of class KEYWORD */
		RESULT_CACHE(ETokenClass.KEYWORD),
			/** Token QUESTION_TO of class KEYWORD */
		QUESTION_TO(ETokenClass.KEYWORD),
			/** Token ASCENDING of class KEYWORD */
		ASCENDING(ETokenClass.KEYWORD),
			/** Token END_STRING of class KEYWORD */
		END_STRING(ETokenClass.KEYWORD),
			/** Token ZON of class KEYWORD */
		ZON(ETokenClass.KEYWORD),
			/** Token DESCRIPTOR of class KEYWORD */
		DESCRIPTOR(ETokenClass.KEYWORD),
			/** Token NOTINHERITABLE of class KEYWORD */
		NOTINHERITABLE(ETokenClass.KEYWORD),
			/** Token VARYINGZ of class KEYWORD */
		VARYINGZ(ETokenClass.KEYWORD),
			/** Token IS_NOT of class KEYWORD */
		IS_NOT(ETokenClass.KEYWORD),
			/** Token ZONE of class KEYWORD */
		ZONE(ETokenClass.KEYWORD),
			/** Token ALIGNED of class KEYWORD */
		ALIGNED(ETokenClass.KEYWORD),
			/** Token MESSAGE of class KEYWORD */
		MESSAGE(ETokenClass.KEYWORD),
			/** Token COMMUNICATION of class KEYWORD */
		COMMUNICATION(ETokenClass.KEYWORD),
			/** Token NO_ZERO of class KEYWORD */
		NO_ZERO(ETokenClass.KEYWORD),
			/** Token TIMEZONE_REGION of class KEYWORD */
		TIMEZONE_REGION(ETokenClass.KEYWORD),
			/** Token IMPLICIT of class KEYWORD */
		IMPLICIT(ETokenClass.KEYWORD),
			/** Token MERGE of class KEYWORD */
		MERGE(ETokenClass.KEYWORD),
			/** Token NO_HEADING of class KEYWORD */
		NO_HEADING(ETokenClass.KEYWORD),
			/** Token ENDRECORD of class KEYWORD */
		ENDRECORD(ETokenClass.KEYWORD),
			/** Token SORT of class KEYWORD */
		SORT(ETokenClass.KEYWORD),
			/** Token LIKE4 of class KEYWORD */
		LIKE4(ETokenClass.KEYWORD),
			/** Token INFINITE of class KEYWORD */
		INFINITE(ETokenClass.KEYWORD),
			/** Token SHORT of class KEYWORD */
		SHORT(ETokenClass.KEYWORD),
			/** Token LIKE2 of class KEYWORD */
		LIKE2(ETokenClass.KEYWORD),
			/** Token RIGHT_ANGLE_BRACKET of class DELIMITER */
		RIGHT_ANGLE_BRACKET(ETokenClass.DELIMITER),
			/** Token MODULE of class KEYWORD */
		MODULE(ETokenClass.KEYWORD),
			/** Token SAME of class KEYWORD */
		SAME(ETokenClass.KEYWORD),
			/** Token END_CALL of class KEYWORD */
		END_CALL(ETokenClass.KEYWORD),
			/** Token B_AND of class KEYWORD */
		B_AND(ETokenClass.KEYWORD),
			/** Token LISTBOX of class KEYWORD */
		LISTBOX(ETokenClass.KEYWORD),
			/** Token HIDDEN of class KEYWORD */
		HIDDEN(ETokenClass.KEYWORD),
			/** Token RAISERROR of class KEYWORD */
		RAISERROR(ETokenClass.KEYWORD),
			/** Token END_PROPERTY of class KEYWORD */
		END_PROPERTY(ETokenClass.KEYWORD),
			/** Token EQGT of class OPERATOR */
		EQGT(ETokenClass.OPERATOR),
			/** Token LOCALE of class KEYWORD */
		LOCALE(ETokenClass.KEYWORD),
			/** Token UNTRUSTED of class KEYWORD */
		UNTRUSTED(ETokenClass.KEYWORD),
			/** Token FORMAT of class KEYWORD */
		FORMAT(ETokenClass.KEYWORD),
			/** Token SEQUENTIAL of class KEYWORD */
		SEQUENTIAL(ETokenClass.KEYWORD),
			/** Token PUBLIC of class KEYWORD */
		PUBLIC(ETokenClass.KEYWORD),
			/** Token CLASS_EVENTS of class KEYWORD */
		CLASS_EVENTS(ETokenClass.KEYWORD),
			/** Token KANJI of class KEYWORD */
		KANJI(ETokenClass.KEYWORD),
			/** Token COMPUTE of class KEYWORD */
		COMPUTE(ETokenClass.KEYWORD),
			/** Token RGB of class KEYWORD */
		RGB(ETokenClass.KEYWORD),
			/** Token CHAR of class KEYWORD */
		CHAR(ETokenClass.KEYWORD),
			/** Token NEW of class KEYWORD */
		NEW(ETokenClass.KEYWORD),
			/** Token CLASSDEF of class KEYWORD */
		CLASSDEF(ETokenClass.KEYWORD),
			/** Token VBS of class KEYWORD */
		VBS(ETokenClass.KEYWORD),
			/** Token RELEASE of class KEYWORD */
		RELEASE(ETokenClass.KEYWORD),
			/** Token PADDING of class KEYWORD */
		PADDING(ETokenClass.KEYWORD),
			/** Token LEAVE of class KEYWORD */
		LEAVE(ETokenClass.KEYWORD),
			/** Token IMPORTING of class KEYWORD */
		IMPORTING(ETokenClass.KEYWORD),
			/** Token THAN of class KEYWORD */
		THAN(ETokenClass.KEYWORD),
			/** Token INTERPOLATIONEND of class SPECIAL */
		INTERPOLATIONEND(ETokenClass.SPECIAL),
			/** Token FLOATING_POINT_LITERAL of class LITERAL */
		FLOATING_POINT_LITERAL(ETokenClass.LITERAL),
			/** Token RESERVED of class KEYWORD */
		RESERVED(ETokenClass.KEYWORD),
			/** Token SYSTEM of class KEYWORD */
		SYSTEM(ETokenClass.KEYWORD),
			/** Token END_RECEIVE of class KEYWORD */
		END_RECEIVE(ETokenClass.KEYWORD),
			/** Token EXIT_SUB of class KEYWORD */
		EXIT_SUB(ETokenClass.KEYWORD),
			/** Token NGT of class OPERATOR */
		NGT(ETokenClass.OPERATOR),
			/** Token IMAGE of class KEYWORD */
		IMAGE(ETokenClass.KEYWORD),
			/** Token RETURNING of class KEYWORD */
		RETURNING(ETokenClass.KEYWORD),
			/** Token SORT_MERGE of class KEYWORD */
		SORT_MERGE(ETokenClass.KEYWORD),
			/** Token MORE_LABELS of class KEYWORD */
		MORE_LABELS(ETokenClass.KEYWORD),
			/** Token BASE of class KEYWORD */
		BASE(ETokenClass.KEYWORD),
			/** Token VAR of class KEYWORD */
		VAR(ETokenClass.KEYWORD),
			/** Token BODY of class KEYWORD */
		BODY(ETokenClass.KEYWORD),
			/** Token CTLASA of class KEYWORD */
		CTLASA(ETokenClass.KEYWORD),
			/** Token SUPER of class KEYWORD */
		SUPER(ETokenClass.KEYWORD),
			/** Token EACH of class KEYWORD */
		EACH(ETokenClass.KEYWORD),
			/** Token RESERVES of class KEYWORD */
		RESERVES(ETokenClass.KEYWORD),
			/** Token SELECT of class KEYWORD */
		SELECT(ETokenClass.KEYWORD),
			/** Token INTO of class KEYWORD */
		INTO(ETokenClass.KEYWORD),
			/** Token PREPROCESSOR_INCLUDE of class KEYWORD */
		PREPROCESSOR_INCLUDE(ETokenClass.KEYWORD),
			/** Token SPOOL of class KEYWORD */
		SPOOL(ETokenClass.KEYWORD),
			/** Token ENDFOR of class KEYWORD */
		ENDFOR(ETokenClass.KEYWORD),
			/** Token ENTRIES of class KEYWORD */
		ENTRIES(ETokenClass.KEYWORD),
			/** Token OROR of class OPERATOR */
		OROR(ETokenClass.OPERATOR),
			/** Token TRANSFER of class KEYWORD */
		TRANSFER(ETokenClass.KEYWORD),
			/** Token SWITCH of class KEYWORD */
		SWITCH(ETokenClass.KEYWORD),
			/** Token WITHOUT of class KEYWORD */
		WITHOUT(ETokenClass.KEYWORD),
			/** Token NOFIXEDOVERFLOW of class KEYWORD */
		NOFIXEDOVERFLOW(ETokenClass.KEYWORD),
			/** Token NULL of class KEYWORD */
		NULL(ETokenClass.KEYWORD),
			/** Token DDMMYY of class KEYWORD */
		DDMMYY(ETokenClass.KEYWORD),
			/** Token CONDENSE of class KEYWORD */
		CONDENSE(ETokenClass.KEYWORD),
			/** Token FLOAT_LONG of class KEYWORD */
		FLOAT_LONG(ETokenClass.KEYWORD),
			/** Token SORT_MODE_SIZE of class KEYWORD */
		SORT_MODE_SIZE(ETokenClass.KEYWORD),
			/** Token DELETE of class KEYWORD */
		DELETE(ETokenClass.KEYWORD),
			/** Token ERROR of class KEYWORD */
		ERROR(ETokenClass.KEYWORD),
			/** Token NO_GAP of class KEYWORD */
		NO_GAP(ETokenClass.KEYWORD),
			/** Token OPERAT of class KEYWORD */
		OPERAT(ETokenClass.KEYWORD),
			/** Token TABLES of class KEYWORD */
		TABLES(ETokenClass.KEYWORD),
			/** Token CHARSETFORM of class KEYWORD */
		CHARSETFORM(ETokenClass.KEYWORD),
			/** Token TERMINATE of class KEYWORD */
		TERMINATE(ETokenClass.KEYWORD),
			/** Token CALLABLE of class KEYWORD */
		CALLABLE(ETokenClass.KEYWORD),
			/** Token QUICKINFO of class KEYWORD */
		QUICKINFO(ETokenClass.KEYWORD),
			/** Token NIL of class KEYWORD */
		NIL(ETokenClass.KEYWORD),
			/** Token SPACES of class KEYWORD */
		SPACES(ETokenClass.KEYWORD),
			/** Token HIGH_VALUE of class KEYWORD */
		HIGH_VALUE(ETokenClass.KEYWORD),
			/** Token REPLY of class KEYWORD */
		REPLY(ETokenClass.KEYWORD),
			/** Token SIZEOF of class KEYWORD */
		SIZEOF(ETokenClass.KEYWORD),
			/** Token ROUND of class KEYWORD */
		ROUND(ETokenClass.KEYWORD),
			/** Token CURRENCY of class KEYWORD */
		CURRENCY(ETokenClass.KEYWORD),
			/** Token END_LINES of class KEYWORD */
		END_LINES(ETokenClass.KEYWORD),
			/** Token FALSE of class KEYWORD */
		FALSE(ETokenClass.KEYWORD),
			/** Token END_OF_PAGE of class KEYWORD */
		END_OF_PAGE(ETokenClass.KEYWORD),
			/** Token OUTPUT of class KEYWORD */
		OUTPUT(ETokenClass.KEYWORD),
			/** Token ENHANCEMENT_SECTION of class KEYWORD */
		ENHANCEMENT_SECTION(ETokenClass.KEYWORD),
			/** Token APPEND of class KEYWORD */
		APPEND(ETokenClass.KEYWORD),
			/** Token HASHED of class KEYWORD */
		HASHED(ETokenClass.KEYWORD),
			/** Token DIRECT of class KEYWORD */
		DIRECT(ETokenClass.KEYWORD),
			/** Token KEYFROM of class KEYWORD */
		KEYFROM(ETokenClass.KEYWORD),
			/** Token CONTEXTS of class KEYWORD */
		CONTEXTS(ETokenClass.KEYWORD),
			/** Token SORT_CONTROL of class KEYWORD */
		SORT_CONTROL(ETokenClass.KEYWORD),
			/** Token DELIM of class KEYWORD */
		DELIM(ETokenClass.KEYWORD),
			/** Token DEFINE of class KEYWORD */
		DEFINE(ETokenClass.KEYWORD),
			/** Token USAGE of class KEYWORD */
		USAGE(ETokenClass.KEYWORD),
			/** Token DUMMY of class KEYWORD */
		DUMMY(ETokenClass.KEYWORD),
			/** Token NCP of class KEYWORD */
		NCP(ETokenClass.KEYWORD),
			/** Token RAW of class KEYWORD */
		RAW(ETokenClass.KEYWORD),
			/** Token CUSTOMER_FUNCTION of class KEYWORD */
		CUSTOMER_FUNCTION(ETokenClass.KEYWORD),
			/** Token FLOOR_DIV of class OPERATOR */
		FLOOR_DIV(ETokenClass.OPERATOR),
			/** Token LINESIZE of class KEYWORD */
		LINESIZE(ETokenClass.KEYWORD),
			/** Token COMMA of class DELIMITER */
		COMMA(ETokenClass.DELIMITER),
			/** Token TRANSIENT of class KEYWORD */
		TRANSIENT(ETokenClass.KEYWORD),
			/** Token SOME of class KEYWORD */
		SOME(ETokenClass.KEYWORD),
			/** Token ADVANCING of class KEYWORD */
		ADVANCING(ETokenClass.KEYWORD),
			/** Token OPTLINK of class KEYWORD */
		OPTLINK(ETokenClass.KEYWORD),
			/** Token EXCLUDE of class KEYWORD */
		EXCLUDE(ETokenClass.KEYWORD),
			/** Token DOTSTAR of class OPERATOR */
		DOTSTAR(ETokenClass.OPERATOR),
			/** Token PREPROCESSOR_PUSH of class KEYWORD */
		PREPROCESSOR_PUSH(ETokenClass.KEYWORD),
			/** Token DEBUG_CONTENTS of class KEYWORD */
		DEBUG_CONTENTS(ETokenClass.KEYWORD),
			/** Token BLANKS of class KEYWORD */
		BLANKS(ETokenClass.KEYWORD),
			/** Token GRANT of class KEYWORD */
		GRANT(ETokenClass.KEYWORD),
			/** Token FLOAT_SHORT of class KEYWORD */
		FLOAT_SHORT(ETokenClass.KEYWORD),
			/** Token NDL of class KEYWORD */
		NDL(ETokenClass.KEYWORD),
			/** Token NEGATIVE of class KEYWORD */
		NEGATIVE(ETokenClass.KEYWORD),
			/** Token TYPE_POOLS of class KEYWORD */
		TYPE_POOLS(ETokenClass.KEYWORD),
			/** Token KEYED of class KEYWORD */
		KEYED(ETokenClass.KEYWORD),
			/** Token RESOLUTION of class KEYWORD */
		RESOLUTION(ETokenClass.KEYWORD),
			/** Token VIA of class KEYWORD */
		VIA(ETokenClass.KEYWORD),
			/** Token SAVE of class KEYWORD */
		SAVE(ETokenClass.KEYWORD),
			/** Token OCIROWID of class KEYWORD */
		OCIROWID(ETokenClass.KEYWORD),
			/** Token TRANSPORTING of class KEYWORD */
		TRANSPORTING(ETokenClass.KEYWORD),
			/** Token GIVING of class KEYWORD */
		GIVING(ETokenClass.KEYWORD),
			/** Token REM of class KEYWORD */
		REM(ETokenClass.KEYWORD),
			/** Token DESCRIPTORS of class KEYWORD */
		DESCRIPTORS(ETokenClass.KEYWORD),
			/** Token SUBSTRING of class KEYWORD */
		SUBSTRING(ETokenClass.KEYWORD),
			/** Token REF of class KEYWORD */
		REF(ETokenClass.KEYWORD),
			/** Token ELSEIF of class KEYWORD */
		ELSEIF(ETokenClass.KEYWORD),
			/** Token PICTURE of class KEYWORD */
		PICTURE(ETokenClass.KEYWORD),
			/** Token PROCEDURE_POINTER of class KEYWORD */
		PROCEDURE_POINTER(ETokenClass.KEYWORD),
			/** Token NEQ of class OPERATOR */
		NEQ(ETokenClass.OPERATOR),
			/** Token CLOCK_UNITS of class KEYWORD */
		CLOCK_UNITS(ETokenClass.KEYWORD),
			/** Token DATE_BASE of class KEYWORD */
		DATE_BASE(ETokenClass.KEYWORD),
			/** Token FBS of class KEYWORD */
		FBS(ETokenClass.KEYWORD),
			/** Token LOCATE of class KEYWORD */
		LOCATE(ETokenClass.KEYWORD),
			/** Token NOUNDERFLOW of class KEYWORD */
		NOUNDERFLOW(ETokenClass.KEYWORD),
			/** Token ABNORMAL of class KEYWORD */
		ABNORMAL(ETokenClass.KEYWORD),
			/** Token DATA_POINTER of class KEYWORD */
		DATA_POINTER(ETokenClass.KEYWORD),
			/** Token MAXIMUM of class KEYWORD */
		MAXIMUM(ETokenClass.KEYWORD),
			/** Token GLOBAL of class KEYWORD */
		GLOBAL(ETokenClass.KEYWORD),
			/** Token CBOOL of class KEYWORD */
		CBOOL(ETokenClass.KEYWORD),
			/** Token NUMBER_BASE of class KEYWORD */
		NUMBER_BASE(ETokenClass.KEYWORD),
			/** Token MINOR_ID of class KEYWORD */
		MINOR_ID(ETokenClass.KEYWORD),
			/** Token ASSOCIATION of class OPERATOR */
		ASSOCIATION(ETokenClass.OPERATOR),
			/** Token IS_TIME of class OPERATOR */
		IS_TIME(ETokenClass.OPERATOR),
			/** Token OVERRIDABLE of class KEYWORD */
		OVERRIDABLE(ETokenClass.KEYWORD),
			/** Token SUBSTITUTABLE of class KEYWORD */
		SUBSTITUTABLE(ETokenClass.KEYWORD),
			/** Token BINARY_CHAR of class KEYWORD */
		BINARY_CHAR(ETokenClass.KEYWORD),
			/** Token FILTERS of class KEYWORD */
		FILTERS(ETokenClass.KEYWORD),
			/** Token BYADDR of class KEYWORD */
		BYADDR(ETokenClass.KEYWORD),
			/** Token EXCEPTIONS of class KEYWORD */
		EXCEPTIONS(ETokenClass.KEYWORD),
			/** Token CONDITION of class KEYWORD */
		CONDITION(ETokenClass.KEYWORD),
			/** Token REGEX of class KEYWORD */
		REGEX(ETokenClass.KEYWORD),
			/** Token NOT of class OPERATOR */
		NOT(ETokenClass.OPERATOR),
			/** Token EOF of class WHITESPACE */
		EOF(ETokenClass.WHITESPACE),
			/** Token MONTH of class KEYWORD */
		MONTH(ETokenClass.KEYWORD),
			/** Token ROWGUIDCOL of class KEYWORD */
		ROWGUIDCOL(ETokenClass.KEYWORD),
			/** Token EOP of class KEYWORD */
		EOP(ETokenClass.KEYWORD),
			/** Token NESTING of class KEYWORD */
		NESTING(ETokenClass.KEYWORD),
			/** Token INHERITING of class KEYWORD */
		INHERITING(ETokenClass.KEYWORD),
			/** Token IMPORT of class KEYWORD */
		IMPORT(ETokenClass.KEYWORD),
			/** Token EOL of class WHITESPACE */
		EOL(ETokenClass.WHITESPACE),
			/** Token EOJ of class KEYWORD */
		EOJ(ETokenClass.KEYWORD),
			/** Token USING of class KEYWORD */
		USING(ETokenClass.KEYWORD),
			/** Token EXPORTS of class KEYWORD */
		EXPORTS(ETokenClass.KEYWORD),
			/** Token RADIOBUTTON of class KEYWORD */
		RADIOBUTTON(ETokenClass.KEYWORD),
			/** Token MARGIN of class KEYWORD */
		MARGIN(ETokenClass.KEYWORD),
			/** Token THIS of class KEYWORD */
		THIS(ETokenClass.KEYWORD),
			/** Token I_O of class KEYWORD */
		I_O(ETokenClass.KEYWORD),
			/** Token OCICOLL of class KEYWORD */
		OCICOLL(ETokenClass.KEYWORD),
			/** Token GOTO of class KEYWORD */
		GOTO(ETokenClass.KEYWORD),
			/** Token ATTACH of class KEYWORD */
		ATTACH(ETokenClass.KEYWORD),
			/** Token INDEXAREA of class KEYWORD */
		INDEXAREA(ETokenClass.KEYWORD),
			/** Token EXCEPTION_TABLE of class KEYWORD */
		EXCEPTION_TABLE(ETokenClass.KEYWORD),
			/** Token ELL of class KEYWORD */
		ELL(ETokenClass.KEYWORD),
			/** Token RETRY of class KEYWORD */
		RETRY(ETokenClass.KEYWORD),
			/** Token ELS of class KEYWORD */
		ELS(ETokenClass.KEYWORD),
			/** Token INITIAL of class KEYWORD */
		INITIAL(ETokenClass.KEYWORD),
			/** Token METHOD_ID of class KEYWORD */
		METHOD_ID(ETokenClass.KEYWORD),
			/** Token ELSE of class KEYWORD */
		ELSE(ETokenClass.KEYWORD),
			/** Token CHECKBOX of class KEYWORD */
		CHECKBOX(ETokenClass.KEYWORD),
			/** Token BOOL of class KEYWORD */
		BOOL(ETokenClass.KEYWORD),
			/** Token BOXED of class KEYWORD */
		BOXED(ETokenClass.KEYWORD),
			/** Token SB2 of class KEYWORD */
		SB2(ETokenClass.KEYWORD),
			/** Token ZEROS of class KEYWORD */
		ZEROS(ETokenClass.KEYWORD),
			/** Token DAY_OF_WEEK of class KEYWORD */
		DAY_OF_WEEK(ETokenClass.KEYWORD),
			/** Token SB1 of class KEYWORD */
		SB1(ETokenClass.KEYWORD),
			/** Token SEPARATE of class KEYWORD */
		SEPARATE(ETokenClass.KEYWORD),
			/** Token ORGANIZATION of class KEYWORD */
		ORGANIZATION(ETokenClass.KEYWORD),
			/** Token SB4 of class KEYWORD */
		SB4(ETokenClass.KEYWORD),
			/** Token SEMICOLON of class DELIMITER */
		SEMICOLON(ETokenClass.DELIMITER),
			/** Token INDICES of class KEYWORD */
		INDICES(ETokenClass.KEYWORD),
			/** Token DURATION of class KEYWORD */
		DURATION(ETokenClass.KEYWORD),
			/** Token END_PROC of class KEYWORD */
		END_PROC(ETokenClass.KEYWORD),
			/** Token STRINGVALUE of class KEYWORD */
		STRINGVALUE(ETokenClass.KEYWORD),
			/** Token EMI of class KEYWORD */
		EMI(ETokenClass.KEYWORD),
			/** Token FIXED_POINT of class KEYWORD */
		FIXED_POINT(ETokenClass.KEYWORD),
			/** Token RECEIVED of class KEYWORD */
		RECEIVED(ETokenClass.KEYWORD),
			/** Token INSPECT of class KEYWORD */
		INSPECT(ETokenClass.KEYWORD),
			/** Token END of class KEYWORD */
		END(ETokenClass.KEYWORD),
			/** Token UNIVERSAL of class KEYWORD */
		UNIVERSAL(ETokenClass.KEYWORD),
			/** Token INIT of class KEYWORD */
		INIT(ETokenClass.KEYWORD),
			/** Token RENAME of class KEYWORD */
		RENAME(ETokenClass.KEYWORD),
			/** Token REPLICATION of class KEYWORD */
		REPLICATION(ETokenClass.KEYWORD),
			/** Token DELAY of class KEYWORD */
		DELAY(ETokenClass.KEYWORD),
			/** Token COLLATING of class KEYWORD */
		COLLATING(ETokenClass.KEYWORD),
			/** Token LIMITED of class KEYWORD */
		LIMITED(ETokenClass.KEYWORD),
			/** Token TRANSMIT of class KEYWORD */
		TRANSMIT(ETokenClass.KEYWORD),
			/** Token HARMLESS of class KEYWORD */
		HARMLESS(ETokenClass.KEYWORD),
			/** Token ONLY of class KEYWORD */
		ONLY(ETokenClass.KEYWORD),
			/** Token EXPONENTIATION of class OPERATOR */
		EXPONENTIATION(ETokenClass.OPERATOR),
			/** Token INPUT of class KEYWORD */
		INPUT(ETokenClass.KEYWORD),
			/** Token STEP of class KEYWORD */
		STEP(ETokenClass.KEYWORD),
			/** Token ULONG of class KEYWORD */
		ULONG(ETokenClass.KEYWORD),
			/** Token STRUCTURE of class KEYWORD */
		STRUCTURE(ETokenClass.KEYWORD),
			/** Token SELECTION of class KEYWORD */
		SELECTION(ETokenClass.KEYWORD),
			/** Token FREE of class KEYWORD */
		FREE(ETokenClass.KEYWORD),
			/** Token SLASH of class DELIMITER */
		SLASH(ETokenClass.DELIMITER),
			/** Token THEN of class KEYWORD */
		THEN(ETokenClass.KEYWORD),
			/** Token NO_SIGN of class KEYWORD */
		NO_SIGN(ETokenClass.KEYWORD),
			/** Token SOURCES of class KEYWORD */
		SOURCES(ETokenClass.KEYWORD),
			/** Token GKGE of class KEYWORD */
		GKGE(ETokenClass.KEYWORD),
			/** Token EXISTS of class KEYWORD */
		EXISTS(ETokenClass.KEYWORD),
			/** Token HEADER of class KEYWORD */
		HEADER(ETokenClass.KEYWORD),
			/** Token END_PERFORM of class KEYWORD */
		END_PERFORM(ETokenClass.KEYWORD),
			/** Token END_SUBTRACT of class KEYWORD */
		END_SUBTRACT(ETokenClass.KEYWORD),
			/** Token PARAMS of class KEYWORD */
		PARAMS(ETokenClass.KEYWORD),
			/** Token IDENTITY_INSERT of class KEYWORD */
		IDENTITY_INSERT(ETokenClass.KEYWORD),
			/** Token DELIMITED of class KEYWORD */
		DELIMITED(ETokenClass.KEYWORD),
			/** Token INTER of class KEYWORD */
		INTER(ETokenClass.KEYWORD),
			/** Token NON_UNICODE of class KEYWORD */
		NON_UNICODE(ETokenClass.KEYWORD),
			/** Token RESULT of class KEYWORD */
		RESULT(ETokenClass.KEYWORD),
			/** Token DATE_COMPILED of class KEYWORD */
		DATE_COMPILED(ETokenClass.KEYWORD),
			/** Token CRITICAL of class KEYWORD */
		CRITICAL(ETokenClass.KEYWORD),
			/** Token LOW_VALUE of class KEYWORD */
		LOW_VALUE(ETokenClass.KEYWORD),
			/** Token PARAMETER_TABLE of class KEYWORD */
		PARAMETER_TABLE(ETokenClass.KEYWORD),
			/** Token BADI of class KEYWORD */
		BADI(ETokenClass.KEYWORD),
			/** Token RESUME of class KEYWORD */
		RESUME(ETokenClass.KEYWORD),
			/** Token CONVERTING of class KEYWORD */
		CONVERTING(ETokenClass.KEYWORD),
			/** Token PREPROCESSOR_POP of class KEYWORD */
		PREPROCESSOR_POP(ETokenClass.KEYWORD),
			/** Token SCHEMA of class KEYWORD */
		SCHEMA(ETokenClass.KEYWORD),
			/** Token ADJACENT of class KEYWORD */
		ADJACENT(ETokenClass.KEYWORD),
			/** Token OUTPUT_LENGTH of class KEYWORD */
		OUTPUT_LENGTH(ETokenClass.KEYWORD),
			/** Token END_IF of class KEYWORD */
		END_IF(ETokenClass.KEYWORD),
			/** Token START of class KEYWORD */
		START(ETokenClass.KEYWORD),
			/** Token MATRIX_POWER of class OPERATOR */
		MATRIX_POWER(ETokenClass.OPERATOR),
			/** Token STDCALL of class KEYWORD */
		STDCALL(ETokenClass.KEYWORD),
			/** Token DIRECTCAST of class KEYWORD */
		DIRECTCAST(ETokenClass.KEYWORD),
			/** Token REVOKE of class KEYWORD */
		REVOKE(ETokenClass.KEYWORD),
			/** Token GKEQ of class KEYWORD */
		GKEQ(ETokenClass.KEYWORD),
			/** Token RECEIVE of class KEYWORD */
		RECEIVE(ETokenClass.KEYWORD),
			/** Token NLT of class OPERATOR */
		NLT(ETokenClass.OPERATOR),
			/** Token STRING of class KEYWORD */
		STRING(ETokenClass.KEYWORD),
			/** Token NOCONVERSION of class KEYWORD */
		NOCONVERSION(ETokenClass.KEYWORD),
			/** Token EXTERNAL of class KEYWORD */
		EXTERNAL(ETokenClass.KEYWORD),
			/** Token CONTAINSTABLE of class KEYWORD */
		CONTAINSTABLE(ETokenClass.KEYWORD),
			/** Token DECIMALS of class KEYWORD */
		DECIMALS(ETokenClass.KEYWORD),
			/** Token ARCHIVE of class KEYWORD */
		ARCHIVE(ETokenClass.KEYWORD),
			/** Token END_DIVIDE of class KEYWORD */
		END_DIVIDE(ETokenClass.KEYWORD),
			/** Token FIELD_SYMBOLS of class KEYWORD */
		FIELD_SYMBOLS(ETokenClass.KEYWORD),
			/** Token BINARY_SHORT of class KEYWORD */
		BINARY_SHORT(ETokenClass.KEYWORD),
			/** Token PERSISTENT of class KEYWORD */
		PERSISTENT(ETokenClass.KEYWORD),
			/** Token PREPROCESSOR_NOPRINT of class KEYWORD */
		PREPROCESSOR_NOPRINT(ETokenClass.KEYWORD),
			/** Token THRU of class KEYWORD */
		THRU(ETokenClass.KEYWORD),
			/** Token IS_EMPTY of class OPERATOR */
		IS_EMPTY(ETokenClass.OPERATOR),
			/** Token CRASH of class KEYWORD */
		CRASH(ETokenClass.KEYWORD),
			/** Token SEALED of class KEYWORD */
		SEALED(ETokenClass.KEYWORD),
			/** Token END_ADD of class KEYWORD */
		END_ADD(ETokenClass.KEYWORD),
			/** Token BACK of class KEYWORD */
		BACK(ETokenClass.KEYWORD),
			/** Token NAME of class KEYWORD */
		NAME(ETokenClass.KEYWORD),
			/** Token EXP of class KEYWORD */
		EXP(ETokenClass.KEYWORD),
			/** Token COMPARING of class KEYWORD */
		COMPARING(ETokenClass.KEYWORD),
			/** Token UNCONNECTED of class KEYWORD */
		UNCONNECTED(ETokenClass.KEYWORD),
			/** Token BOTH of class KEYWORD */
		BOTH(ETokenClass.KEYWORD),
			/** Token SORTABLE of class KEYWORD */
		SORTABLE(ETokenClass.KEYWORD),
			/** Token DIMENSION of class KEYWORD */
		DIMENSION(ETokenClass.KEYWORD),
			/** Token VOID of class KEYWORD */
		VOID(ETokenClass.KEYWORD),
			/** Token PREPROCESSOR_NOTE of class KEYWORD */
		PREPROCESSOR_NOTE(ETokenClass.KEYWORD),
			/** Token PAGES of class KEYWORD */
		PAGES(ETokenClass.KEYWORD),
			/** Token DISPLAY_1 of class KEYWORD */
		DISPLAY_1(ETokenClass.KEYWORD),
			/** Token REDUCIBLE of class KEYWORD */
		REDUCIBLE(ETokenClass.KEYWORD),
			/** Token TRIPLE_DOT of class OPERATOR */
		TRIPLE_DOT(ETokenClass.OPERATOR),
			/** Token RECORD of class KEYWORD */
		RECORD(ETokenClass.KEYWORD),
			/** Token CENTERED of class KEYWORD */
		CENTERED(ETokenClass.KEYWORD),
			/** Token JAVA of class KEYWORD */
		JAVA(ETokenClass.KEYWORD),
			/** Token END_INVOKE of class KEYWORD */
		END_INVOKE(ETokenClass.KEYWORD),
			/** Token WAITFOR of class KEYWORD */
		WAITFOR(ETokenClass.KEYWORD),
			/** Token RENAMES of class KEYWORD */
		RENAMES(ETokenClass.KEYWORD),
			/** Token THROWS of class KEYWORD */
		THROWS(ETokenClass.KEYWORD),
			/** Token LOCAL of class KEYWORD */
		LOCAL(ETokenClass.KEYWORD),
			/** Token DATE_LITERAL of class LITERAL */
		DATE_LITERAL(ETokenClass.LITERAL),
			/** Token SIZE_T of class KEYWORD */
		SIZE_T(ETokenClass.KEYWORD),
			/** Token PROCEDURES of class KEYWORD */
		PROCEDURES(ETokenClass.KEYWORD),
			/** Token ETX of class KEYWORD */
		ETX(ETokenClass.KEYWORD),
			/** Token NAMESPACE of class KEYWORD */
		NAMESPACE(ETokenClass.KEYWORD),
			/** Token SKIP of class KEYWORD */
		SKIP(ETokenClass.KEYWORD),
			/** Token EQUIV of class OPERATOR */
		EQUIV(ETokenClass.OPERATOR),
			/** Token ELIF of class KEYWORD */
		ELIF(ETokenClass.KEYWORD),
			/** Token URSHIFTEQ of class OPERATOR */
		URSHIFTEQ(ETokenClass.OPERATOR),
			/** Token IEEE of class KEYWORD */
		IEEE(ETokenClass.KEYWORD),
			/** Token VARIABLE of class KEYWORD */
		VARIABLE(ETokenClass.KEYWORD),
			/** Token COUNTRY of class KEYWORD */
		COUNTRY(ETokenClass.KEYWORD),
			/** Token DISPLAY of class KEYWORD */
		DISPLAY(ETokenClass.KEYWORD),
			/** Token LOOP_BODY of class KEYWORD */
		LOOP_BODY(ETokenClass.KEYWORD),
			/** Token CHECK of class KEYWORD */
		CHECK(ETokenClass.KEYWORD),
			/** Token USE of class KEYWORD */
		USE(ETokenClass.KEYWORD),
			/** Token DOWN of class KEYWORD */
		DOWN(ETokenClass.KEYWORD),
			/** Token FROM of class KEYWORD */
		FROM(ETokenClass.KEYWORD),
			/** Token DISTINCT of class KEYWORD */
		DISTINCT(ETokenClass.KEYWORD),
			/** Token THROW of class KEYWORD */
		THROW(ETokenClass.KEYWORD),
			/** Token NEW_LINE of class KEYWORD */
		NEW_LINE(ETokenClass.KEYWORD),
			/** Token OVER of class KEYWORD */
		OVER(ETokenClass.KEYWORD),
			/** Token FORTRAN of class KEYWORD */
		FORTRAN(ETokenClass.KEYWORD),
			/** Token ESI of class KEYWORD */
		ESI(ETokenClass.KEYWORD),
			/** Token LOCAL_STORAGE of class KEYWORD */
		LOCAL_STORAGE(ETokenClass.KEYWORD),
			/** Token DIVEQ of class OPERATOR */
		DIVEQ(ETokenClass.OPERATOR),
			/** Token UPDATE of class KEYWORD */
		UPDATE(ETokenClass.KEYWORD),
			/** Token EXCLUSIVE of class KEYWORD */
		EXCLUSIVE(ETokenClass.KEYWORD),
			/** Token ERR of class KEYWORD */
		ERR(ETokenClass.KEYWORD),
			/** Token END_OF_FILE of class KEYWORD */
		END_OF_FILE(ETokenClass.KEYWORD),
			/** Token NEW_SECTION of class KEYWORD */
		NEW_SECTION(ETokenClass.KEYWORD),
			/** Token SYNCHRONIZED of class KEYWORD */
		SYNCHRONIZED(ETokenClass.KEYWORD),
			/** Token SCOPE of class OPERATOR */
		SCOPE(ETokenClass.OPERATOR),
			/** Token OREQ of class OPERATOR */
		OREQ(ETokenClass.OPERATOR),
			/** Token DEBUGGING of class KEYWORD */
		DEBUGGING(ETokenClass.KEYWORD),
			/** Token PENDING of class KEYWORD */
		PENDING(ETokenClass.KEYWORD),
			/** Token EST of class KEYWORD */
		EST(ETokenClass.KEYWORD),
			/** Token SECURITY of class KEYWORD */
		SECURITY(ETokenClass.KEYWORD),
			/** Token PAGESIZE of class KEYWORD */
		PAGESIZE(ETokenClass.KEYWORD),
			/** Token LINENO of class KEYWORD */
		LINENO(ETokenClass.KEYWORD),
			/** Token PERCENT of class KEYWORD */
		PERCENT(ETokenClass.KEYWORD),
			/** Token SYNC of class KEYWORD */
		SYNC(ETokenClass.KEYWORD),
			/** Token ENDINTERFACE of class KEYWORD */
		ENDINTERFACE(ETokenClass.KEYWORD),
			/** Token RETCODE of class KEYWORD */
		RETCODE(ETokenClass.KEYWORD),
			/** Token RANGE of class KEYWORD */
		RANGE(ETokenClass.KEYWORD),
			/** Token END_EXEC of class KEYWORD */
		END_EXEC(ETokenClass.KEYWORD),
			/** Token BEFORE of class KEYWORD */
		BEFORE(ETokenClass.KEYWORD),
			/** Token ENTRY of class KEYWORD */
		ENTRY(ETokenClass.KEYWORD),
			/** Token FIELDS of class KEYWORD */
		FIELDS(ETokenClass.KEYWORD),
			/** Token AFTER of class KEYWORD */
		AFTER(ETokenClass.KEYWORD),
			/** Token COMP_1 of class KEYWORD */
		COMP_1(ETokenClass.KEYWORD),
			/** Token LEFT_JUSTIFIED of class KEYWORD */
		LEFT_JUSTIFIED(ETokenClass.KEYWORD),
			/** Token DYNPRO of class KEYWORD */
		DYNPRO(ETokenClass.KEYWORD),
			/** Token COMP_3 of class KEYWORD */
		COMP_3(ETokenClass.KEYWORD),
			/** Token COMP_2 of class KEYWORD */
		COMP_2(ETokenClass.KEYWORD),
			/** Token JOIN of class KEYWORD */
		JOIN(ETokenClass.KEYWORD),
			/** Token COMP_5 of class KEYWORD */
		COMP_5(ETokenClass.KEYWORD),
			/** Token COMP_4 of class KEYWORD */
		COMP_4(ETokenClass.KEYWORD),
			/** Token CHAR_TO_HEX of class KEYWORD */
		CHAR_TO_HEX(ETokenClass.KEYWORD),
			/** Token RIGHT_DIV of class OPERATOR */
		RIGHT_DIV(ETokenClass.OPERATOR),
			/** Token RUN of class KEYWORD */
		RUN(ETokenClass.KEYWORD),
			/** Token SINGLE of class KEYWORD */
		SINGLE(ETokenClass.KEYWORD),
			/** Token FINALLY of class KEYWORD */
		FINALLY(ETokenClass.KEYWORD),
			/** Token BASIS of class KEYWORD */
		BASIS(ETokenClass.KEYWORD),
			/** Token STARTING of class KEYWORD */
		STARTING(ETokenClass.KEYWORD),
			/** Token SUBKEY of class KEYWORD */
		SUBKEY(ETokenClass.KEYWORD),
			/** Token FRAME of class KEYWORD */
		FRAME(ETokenClass.KEYWORD),
			/** Token CLASS_DATA of class KEYWORD */
		CLASS_DATA(ETokenClass.KEYWORD),
			/** Token EXTENDS of class KEYWORD */
		EXTENDS(ETokenClass.KEYWORD),
			/** Token PREPROCESSOR_DEACTIVATE of class KEYWORD */
		PREPROCESSOR_DEACTIVATE(ETokenClass.KEYWORD),
	
	/**This token type is used by no scanner. */
	NEVER_USED_TOKEN_TYPE(ETokenClass.SPECIAL);

	/** An enum set containing all identifiers. */
	public static final UnmodifiableSet<ETokenType> IDENTIFIERS = CollectionUtils
			.asUnmodifiable(getTokenTypesByClass(ETokenClass.IDENTIFIER));

	/** An enum set containing all literals. */
	public static final UnmodifiableSet<ETokenType> LITERALS = CollectionUtils
			.asUnmodifiable(getTokenTypesByClass(ETokenClass.LITERAL));

	/** An enum set containing all keywords. */
	public static final UnmodifiableSet<ETokenType> KEYWORDS = CollectionUtils
			.asUnmodifiable(getTokenTypesByClass(ETokenClass.KEYWORD));

	/** An enum set containing all operators. */
	public static final UnmodifiableSet<ETokenType> OPERATORS = CollectionUtils
			.asUnmodifiable(getTokenTypesByClass(ETokenClass.OPERATOR));

	/** An enum set containing all delimiters. */
	public static final UnmodifiableSet<ETokenType> DELIMITERS = CollectionUtils
			.asUnmodifiable(getTokenTypesByClass(ETokenClass.DELIMITER));

	/** An enum set containing all special token types. */
	public static final UnmodifiableSet<ETokenType> SPECIALS = CollectionUtils
			.asUnmodifiable(getTokenTypesByClass(ETokenClass.SPECIAL));

	/** An enum set containing all comments. */
	public static final UnmodifiableSet<ETokenType> COMMENTS = CollectionUtils
			.asUnmodifiable(getTokenTypesByClass(ETokenClass.COMMENT));

	/**
	 * Enumeration describing the classes token types belong to.
	 */
	public enum ETokenClass {
		/** Literals */
		LITERAL,
		/** Keywords */
		KEYWORD,
		/** Identifiers */
		IDENTIFIER,
		/** Operators */
		OPERATOR,
		/** Delimiters */
		DELIMITER,
		/** Comments */
		COMMENT,
		/** Special token types */
		SPECIAL,
		/** Error tokens */
		ERROR,
		/** Linebreaks, Whitespace and other non visible characters */
		WHITESPACE,
		/** Synthetic tokens */
		SYNTHETIC;

		/** Convenience method that gets an array of the token class names */
		public static String[] getTokenClassNames() {
			List<String> tokenClassNames = new ArrayList<String>();
			for (ETokenClass tokenClass : values()) {
				tokenClassNames.add(tokenClass.name());
			}
			return tokenClassNames.toArray(new String[] {});
		}

	}

	/** The token class of this token type. */
	private final ETokenClass tokenClass;

	/**
	 * Create new token type.
	 * 
	 * @param tokenClass
	 *            token class this token type belongs to.
	 */
	private ETokenType(ETokenClass tokenClass) {
		this.tokenClass = tokenClass;
	}

	/**
	 * Is this token type a literal?
	 */
	public boolean isLiteral() {
		return tokenClass == ETokenClass.LITERAL;
	}

	/**
	 * Is this token type a keyword?
	 */
	public boolean isKeyword() {
		return tokenClass == ETokenClass.KEYWORD;
	}

	/**
	 * Is this token type an identifier?
	 */
	public boolean isIdentifier() {
		return tokenClass == ETokenClass.IDENTIFIER;
	}

	/**
	 * Is this token type an operator?
	 */
	public boolean isOperator() {
		return tokenClass == ETokenClass.OPERATOR;
	}

	/**
	 * Is this token type a delimiter?
	 */
	public boolean isDelimiter() {
		return tokenClass == ETokenClass.DELIMITER;
	}

	/**
	 * Is this a special token type?
	 */
	public boolean isSpecial() {
		return tokenClass == ETokenClass.SPECIAL;
	}

	/**
	 * Is this a special token type?
	 */
	public boolean isError() {
		return tokenClass == ETokenClass.ERROR;
	}

	/**
	 * Get token class
	 */
	public ETokenClass getTokenClass() {
		return tokenClass;
	}

	/**
	 * Obtain all token types that belong to a specific token class. This has
	 * running time linear in the number of token types. Nevertheless for any
	 * reasonably sized set of token types this is really fast (about 0ms). To
	 * obtain a specific set of token types (e.g. literals) use the predefined
	 * sets like {@link #LITERALS}. These are constructed only once.
	 */
	public static EnumSet<ETokenType> getTokenTypesByClass(
			ETokenClass tokenClass) {

		EnumSet<ETokenType> result = EnumSet.noneOf(ETokenType.class);
		for (ETokenType type : values()) {
			if (type.tokenClass == tokenClass) {
				result.add(type);
			}
		}
		return result;
	}

}
