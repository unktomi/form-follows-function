/*
 * Copyright 2007-2009 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

/////////////////////////////////////////////////////////////////////////////////
// Version 4 of the F3 lexer
//
// @author Jim Idle
//
// Version 4 of the grammar reverts to a spearate lexer and parser grammar without a separate
// ANTLR based AST walker. This is because this is the easiest way (at the time of writing)
// to confine error recovery to the smallest possible set of side effects on the resulting
// F3Tree. This is important for down stream tools such as code completion, which require
// as much of the AST as is possible to produce if they are to be effective.
// 
// The lexer is spearated from teh paresr because ANTLR cannot specify separate 
// superclasses for the lexer and the parser and v4 requires a different lexer
// superClass to the v3 lexer but must co-exits with the v3 lexer.
//
// Derived from prior versions by:
//
// @author Robert Field
// @author Zhiqun Chen
//
lexer grammar v4Lexer;

options { 

	// Rather than embed lexer oriented Java code in this grammar, just to override
	// methods in the ANTLR base recognizer and derviative classes, we
	// instruct ANTLR to generate a class which is dervied from our own
	// super class. The super class is where we embody any code that does
	// not require direct access to the terminals and methods generated 
	// to implement the lexer.
	//
	superClass 	= AbstractGeneratedLexerV4; 
	
}


// -----------------------------------------------------------------
// This section provides package, imports and other information
// to the lexer. It is inserted at the start of the generated lexer
// code
//
@lexer::header {

package org.f3.tools.antlr;

import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Convert;
import com.sun.tools.javac.util.Log;
import org.f3.tools.util.MsgSym;

}

// ------------------------------------------------------------------
// Members required by the generated lexer class are included verbatim in the
// generated lexer code.
//
@lexer::members {

    // Constructor that creates a message log sink for the 
    // current context.
    // 
    public v4Lexer(Context context, CharStream input) {
    	this(input);
        this.log = Log.instance(context);
    }


}

 
//------------------------------------------------------------------
// LEXER RULES

// --------
// Keywords
//
ABSTRACT		: 'abstract';
AFTER			: 'after';
AND				: 'and';
AS				: 'as';
ASSERT			: 'assert';
AT				: 'at';
ATTRIBUTE		: 'attribute';
BEFORE			: 'before';
BIND			: 'bind';
BOUND			: 'bound';
BREAK			: 'break';
CATCH			: 'catch';
CLASS			: 'class';
CONTINUE		: 'continue';
DEF				: 'def';
DELETE			: 'delete';
ELSE			: 'else';
EXCLUSIVE		: 'exclusive';
EXTENDS			: 'extends';
FALSE			: 'false';
FINALLY			: 'finally';
FIRST			: 'first';
FOR				: 'for';
FROM			: 'from';
FUNCTION		: 'function';
IF				: 'if';
IMPORT			: 'import';
INDEXOF			: 'indexof';
IN				: 'in';
INIT			: 'init';
INSERT			: 'insert';
INSTANCEOF		: 'instanceof';
INTO			: 'into';
INVERSE			: 'inverse';
LAST			: 'last';
LAZY			: 'lazy';
MOD				: 'mod';
NEW				: 'new';
NOT				: 'not';
NULL			: 'null';
ON				: 'on';
OR				: 'or';
OVERRIDE		: 'override';
PACKAGE			: 'package';
POSTINIT		: 'postinit';
PRIVATE			: 'private';
PROTECTED		: 'protected';
PUBLIC_INIT     : 'public-init';
PUBLIC			: 'public';
PUBLIC_READ     : 'public-read';
REPLACE			: 'replace';
RETURN			: 'return';
REVERSE			: 'reverse';
SIZEOF			: 'sizeof';
STATIC			: 'static';
STEP			: 'step';
SUPER			: 'super';
THEN			: 'then';
THIS			: 'this';
THROW			: 'throw';
TRIGGER			: 'trigger';
TRUE			: 'true';
TRY				: 'try';
TWEEN			: 'tween';
TYPEOF			: 'typeof';
VAR				: 'var';
WHERE			: 'where';
WHILE			: 'while';
WITH			: 'with';

// -------------------------------
// Punctuation and syntactic sugar
//
LBRACKET	: '[';
LPAREN		: '(';
POUND		: '#';
PIPE		: '|';
PLUSPLUS	: '++';
DOTDOT		: '..';
RPAREN		: ')';
RBRACKET	: ']';
SEMI		: ';';
COMMA		: ',';
DOT			: '.';
EQEQ		: '==';
EQ			: '=';
GT			: '>';
LT			: '<';
LTGT		: '<>';
LTEQ		: '<=';
GTEQ		: '>=';
PLUS		: '+';
SUB			: '-';
STAR		: '*';
SLASH		: '/';
PERCENT		: '%';
PLUSEQ		: '+=';
SUBEQ		: '-=';
STAREQ		: '*=';
SLASHEQ		: '/=';
PERCENTEQ	: '%=';
NOTEQ		: '!=';
COLON		: ':';
QUES		: '?';
SUCHTHAT	: '=>';
SUBSUB		: '--';



// Whitespace characters are essentially ignored
// by the parser and AST. They are preserved in the token stream
// by hiding the tokens on a token stream channel that the parser does not examine.
//
WS  :  (
			  (' '|'\t'|'\u000C')
			| ('\n'|'\r')
		)
		{
			$channel=HIDDEN;
		}
    ;
    
// String literals being with either a single or double quote
// character (which must be matched). Additionally, string literals
// may span mulitple lines.
//
// TODO: Allow EOF to terminate a string so that we can
//       detect unterminated string literals and report them
//       as such, rather than trying to doctor this message in the
//       lexer superclass.
//
STRING_LITERAL  		
	: '"' DoubleQuoteBody 
				(
					  '"' // Well formed string
					| '{' // Expression  	

			  			NextIsPercent[DBL_QUOTE_CTX] 
							
					)
				
	
			
	| '\'' SingleQuoteBody
	
				(
					  '\'' // Well formed string
					| '{' // Expression  	

			  			NextIsPercent[SNG_QUOTE_CTX] 

				)
	
	;
	
DOC_STRING
	: ('"'
	 (
			  ~('}'|'{' |'"'|'\\'|'\n'|'\r')
			| '\\' .
			
		 )*  
		 '"')
		 | ( '\''
(
			  ~('}'|'{' |'\''|'\\'|'\n'|'\r')
			| '\\' .
		)*  		 '\'')
	;

	
// String Expression token implementation.
//
// String literals may contain formatting instructions
// which contain the sequence LBRACE PERCENT. This lexer
// rule is selected when, after looking ahead, we determine
// that the string contains this sequence. We then being
// to break out the literal strings in to multiple tokens
// so that the paresr can verify that they are correctly
// formulated.
//
// Note that we indicate the context (double or single quote)
// to the NextIsPercent fragment, which will invoke enterBrace()
// and begin tracking this level of brace string vs " or ' string.
//
// This lexer rule is not matched here but used to set the
// token type if discovered by the string literal rule.
//
fragment
QUOTE_LBRACE_STRING_LITERAL
	: '"'
	| '\''
	;
	
// The left brace character is significant 
// within quoted strings and as a block delimiter.
// If we find it vai this rule, then we enter a new brace context in the
// lexer, which, because we did not find this as a format
// introducer in QUOTE_LBRACE_STRING_LITERAL, means that
// it is not the start of a format specification.
//
LBRACE				
	: '{'				{ enterBrace(0, false); } 
	;
	
// When we currently scanning within a left brace context
// within a quoted string, then the next right brace terminates
// a format or expression embedded withing a quoted string.
// the semantic predicate rightBraceLikeQuote() selects this
// lexer rule if we scan to a right brace and determine that
// we are currently expecting right brace to delimit an expression
// or format. This rule then matches the rest of the quoted string literal if it
// has no more embedded expressions.
//
RBRACE_QUOTE_STRING_LITERAL 	
	: { rightBraceLikeQuote(DBL_QUOTE_CTX) }?=>
		
		  (
		  		  '}' DoubleQuoteBody
		  
			  		(
						  '"' // Well formed string
						  
						| { input.mark(); }
							
							('\n'|'\r'|EOF) // Badly formed string
							
							{
								log.error(eStringStart, MsgSym.MESSAGE_F3_UNTERMINATED_STRING);
								input.rewind();
								setText(getText() + "\"");
							}
					)					
		  )  
		  		{ 
		  			leaveBrace(); 
		         	leaveQuote(); 
		         	processString(); 
		       	}
	
	| { rightBraceLikeQuote(SNG_QUOTE_CTX) }?=>
		
		  (
		  	  '}' SingleQuoteBody
		  
		  		(
					  '\'' // Well formed string
					  
					| { input.mark(); }
						('\n'|'\r'|EOF) // Badly formed string
						{
							log.error(eStringStart, MsgSym.MESSAGE_F3_UNTERMINATED_STRING);
							input.rewind();
							setText(getText() + "'");
						}
				)
								
		  )  
				  
		  		{ 
		  			leaveBrace(); 
		         	leaveQuote(); 
		         	processString(); 
		      	}
	;
	
// As in the rule RBRACE_QUOTE_STRING_LITERAL, this rule
// is selected when we are expecting a right brace to act as
// a delimiter for an embedded expression. However, here
// we find that the quoted string has further embedded
// format expressions and we therefore generate a different token
// to allow the parser to verify the syntax.
//
RBRACE_LBRACE_STRING_LITERAL 	
	: { rightBraceLikeQuote(DBL_QUOTE_CTX) }?=>
	
		  '}' DoubleQuoteBody '{'	
				  
		  		{ 
		  			leaveBrace(); 
					processString(); 
		    	}
				   
		   NextIsPercent[CUR_QUOTE_CTX]	
				   
	| { rightBraceLikeQuote(SNG_QUOTE_CTX) }?=>
	
				  '}' SingleQuoteBody '{'	
				  
				  		{ 
				  			leaveBrace(); 
				       		processString(); 
				       	}
				       	
				   NextIsPercent[CUR_QUOTE_CTX]	
	;
	
// Here, a right brace is returned as a simple token 
// type, because the semantic predicate will select this
// rule when it is determiend that we are not expecting
// right brace to terminate an embedded expression. This
// is the case when it is delimiting code blocks or block
// expressions.
//
RBRACE	
	:	{ !rightBraceLikeQuote(CUR_QUOTE_CTX) }?=>
		
			  '}'				{ leaveBrace(); }
	;

// Scans through the valid body of a double quoted
// string.
//
fragment
DoubleQuoteBody  
	:	 (
			  ~('}'|'{' |'"'|'\\'|'\n'|'\r')
			| '\\' .
			| '}'
				{
					log.error(getCharIndex()-1, MsgSym.MESSAGE_F3_UNESCAPED_RBRACE);
				}
			
		 )*  
	;

// Scans through the body of a single quoted string
//
fragment
SingleQuoteBody  
	:	 (
			  ~('}'|'{' |'\''|'\\'|'\n'|'\r')
			| '\\' .
			| '}'
				{
					log.error(getCharIndex()-1, MsgSym.MESSAGE_F3_UNESCAPED_RBRACE);
				}
		)*  
	;

// This rules is used as a syntactic predicate by quoted
// string rules to determine if the next character
// following a brace is a precentage sign, indicating that
// that this is a formatting sequence, or not, indicating
// that the left brace was introducing an embedded expression.
//
fragment
NextIsPercent[int quoteContext] 			
	:	((' '|'\r'|'\t'|'\u000C'|'\n')* '%')=>
	
			{ enterBrace(quoteContext, true); }
			
	|		{ enterBrace(quoteContext, false); }
	;
				
// If we have entered an embedded brace expression within
// a literal string, and determined that it is a formatting
// expression, then the semantic predicate function percentIsFormat
// will return 'true' and select this rule to introduce the
// formating string to the parser.
//
FORMAT_STRING_LITERAL		
	: 	{ percentIsFormat() }?=>
	
			'%' (~' ')* 			
			
				{ 
					processFormatString();
	 				resetPercentIsFormat(); 
	 			}
	;

// A translation key sequence is used to prefix string literals and 
// as the name suggests, translate it to something else according
// to locales.
//
TRANSLATION_KEY                 
	: '##' 
    	( 
        	'[' TranslationKeyBody ']'
        )?                            
        
        { 
        	processTranslationKey(); 
        }
	;

// The body of the translation key itself.
//
fragment
TranslationKeyBody              
	: (~('[' | ']' | '\\')|'\\' .)+
    ;
 
//------------------------------------------------------------
// Numeric literals.
// These are handled specailly to reduce lexer complexity and
// negate the need to override standard ANTLR lexing methods.
// This improves performance and enhance readibility.
// The following fragmetn rules are to documetn the types and
// to provide a lexer symbol for the token type. The actual
// parsing is carried out in the FLOATING_POINT_LITERAL rule.
//

// Time literals are self evident in meaning and are currently
// recognized by the lexer. This may change as in some cases 
// trying to do too much in the lexer results in lexing errors
// that are difficult to recover from.
//
fragment	TIME_LITERAL 		: 	;

// Decimal literals may not have leading zeros unless
// they are just the constant 0. They are integer only.
// In order to do more accurate error processing, these
// numeric literlas may merge into one rule that overrides
// the type.
//
fragment	DECIMAL_LITERAL 	:	;

// Octal literals are preceeded by a leading zero and must be followed
// by one or more valid octal digits. 
//
fragment	OCTAL_LITERAL 		: 	;

// Hex literals are preceded by 0X or 0x and must have one or
// more valid hex digits following them. The problem with specifying
// it like this is that a string such as 0x will cause a lexing error
// rather than a parse or semantic error, which is probably better
// and so this may change (see comments assocaited with DECIMAL_LITERAL)
//
fragment	HEX_LITERAL 		:	;

// ------------------------------------------------------------
// This rule is in fact the proxy rule for all types of numeric
// literals. ANTLR lexers are LL recognizers rather than pattern
// matchers such as flex. Hence we want to hand craft this rule
// to guide it through all the possible combinatsion of digits and
// dots in the most efficent way.
//
// This rule presents all the decision points in definite order,
// giving the scanner little hard work to do to select the
// correct token to match. The fragment rules above (TIME_LITERAL, DOTDOT
// and so on), are essentially just there to create the token
// types.
//
FLOATING_POINT_LITERAL

@init
{
	// Indicates out of range digit
	//
	boolean rangeError = false;
	
	// First character of rule
	//
	int		sPos = getCharIndex();
	
	// Is this going to be a negative numeric?
	//
	boolean negative = input.LT(-1) == '-';
	
}
    :	
    	// A leading zero can either be a decimal literal
    	// (if it is the sole component) or introduces
    	// an octal or hexadecimal number. Time sequences
    	// are also possible for the single '0' digit.
    	//
    	'0'
    		(
    			  ('x'|'X')		// Hex literal indicated
    			  
    			  {
    			  	// Always set the type, so the parser is not confused
    			  	//
    			  	$type = HEX_LITERAL;
    			  }
    			  (
    			  		// We consume any letters and digits that follow 0x
    			  		// and control the error that we issue.  
    			  	  (
    			  	  	  ('0'..'9'|'a'..'f'|'A'..'F')		// Valid Hex
    			  	  	| ('g'..'z' |'G'..'Z')				// Invalid hex
    			  	  		
    			  	  		{
    			  	  			rangeError = true;	// Signal at least one bad digit
    			  	  		}
    			  	  )+
    			  	  
    			  	  {
    			  	  		setText(getText().substring(2, getText().length()));
    			  	  		if	(rangeError)
    			  	  		{
    			  	  			// Error - malformed hex constant
    			  	  			//
    			  	  			log.error(sPos, MsgSym.MESSAGE_F3_HEX_MALFORMED);
    			  	  			setText("0");
    			  	  		}
    			  	  		else
    			  	  		{
    			  	  			if (! checkIntLiteralRange(getText(), getCharIndex(), 16, negative))
    			  	  			{
    			  	  				setText("0");
    			  	  			}
							}
    			  	  }
    			  	  
    			  	  (
    			  	  		// Hex numbers cannot be floating point, but catch this here
    			  	  		// rather than mismatch it.
    			  	  		//
    			  	  			{ input.LA(2) != '.'}?=> 
    			  	  			
    			  	  				{ sPos = getCharIndex(); } 
    			  	  				
    			  	  				'.' (
    			  	  	  					  ('0'..'9'|'a'..'f'|'A'..'F')		// Valid Hex
    			  	  						| ('g'..'z' |'G'..'Z')				// Invalid hex
    			  	  		
    			  	  					)*
    			  	  				
    			  	  				{ 
    			  	  					// Error - malformed hex constant
    			  	  					//
    			  	  					log.error(sPos, MsgSym.MESSAGE_F3_HEX_FLOAT);
    			  	  					setText("0");
    			  	  				}
    			  	  		|
    			  	  	
    			  	  )
    			  	  
    			  	|	// If no digits follow 0x then it is an error
    			  		//
    			  		{
    			  			log.error(getCharIndex()-1, MsgSym.MESSAGE_F3_HEX_MISSING);
    			  			setText("0");
    			  		}
    			  		
    			  )
    			  
    			| 	// Digits indicate an octal sequence
    				// but we allow a match for any standard ASCII digit
    				// and issue a controlled error, rather than allow
    				// the lexer to throw mismatch errors. This is much nicer
    				// for users.
    				//
    				(
    					  '0'..'7'	// Valid octal digit
    					
    					| '8'..'9'	// Invalid octal digit
    					
    						{ 
    							rangeError = true; // Signal that at least one digit was wrong
    						}
    				)+
    				
    				//Octal
    			  	  
    			|	// Time sequence specifier means this was 0 length time
    				// in whatever units.
    				//
    				('m' 's'? | 's' | 'h')
    				
    				{ $type = TIME_LITERAL; }
    				
  				
    			|	// We can of course have 0.nnnnn
    				//
    				{ input.LA(2) != '.'}?=> '.' 
    					(
    						  // Decimal, but possibly time
    						  //
    						  Digits Exponent?
    						  
    						  	(
    						  		  	('m' 's'? | 's' | 'h')
    				
				    					{ $type = TIME_LITERAL; }
				    				
				    				| 	// Just 0.nnn
				    					//
				    					{ $type = FLOATING_POINT_LITERAL; }
    						  	)
    						  	
    						|	// Just 0.
    							//
    							{ $type = FLOATING_POINT_LITERAL; }
    					)
    				
    			|	// Decimal	
    		)
    
    |	// Leading non zero digits can only be base 10, but might
    	// be a floating point or a time, 
    	//
    	('1'..'9') Digits?
    	
    		// Numeric so far, resolve float and times
    		//
    		(
    				
    			{ input.LA(2) != '.'}?=>
    			
    				(
    				  // HAving determined that this is not a range, we check to 
    				  // see that it looks like something that shoudl be a float.
    				  // We can have an expression such as 1.intVal() and so that
    				  // needs to be '1' '.' 'intVal' '(' ')'
    				  // Note that 1.exxxx will always find an erroneous scientific
    				  // notation, but then if anyone is dumb enough to define a method beginning
    				  // with 'e' or 'E' for an integer literal, then all bets are off.
    				  //
    				  ('.' (~('a'..'d'|'f'..'z'|'A'..'D'|'F'..'Z')))=>
    				  	'.' Digits? Exponent?
    			
    				(
    					  ('m' 's'? | 's' | 'h')
    				
				    		{ $type = TIME_LITERAL; }
				    				
				    	| 	// Just n.nnn
				    					//
				    		{ $type = FLOATING_POINT_LITERAL; }
    				)
    				| // Just n, possibly followed by something like .intValue()
				    		//
				    		{ 
				    			$type = DECIMAL_LITERAL; 
				    			if (! checkIntLiteralRange(getText(), getCharIndex(), 10, negative))
				    			{
				    				setText("0");
				    			}
				    		}
    				)
    				
    			|	// Just a decimal literal
    				//
    				(
    					  ('m' 's'? | 's' | 'h')
    				
				    		{ $type = TIME_LITERAL; }
				    
				    	| Exponent				
				    	
				    		{
				    			$type = FLOATING_POINT_LITERAL;
				    		}
				    		
				    	| 	// Just n, possibly followed by something like .intValue()
				    		//
				    		{ 
				    			$type = DECIMAL_LITERAL; 
				    			if (! checkIntLiteralRange(getText(), getCharIndex(), 10, negative))
				    			{
				    				setText("0");
				    			}
				    		}
    				)
    		)

    |
    	'.'
    	
    		(	  // Float, but is it a time?
    			  //
    			  Digits Exponent?
    			  
    			  	(
    			  		 ('m' 's'? | 's' | 'h')	
    			 
    			 			{ $type = TIME_LITERAL; }
    			 			
    			 		| 	// Just  floating point
    			 			//
    			 			{ $type = FLOATING_POINT_LITERAL; }
    			 	
    				)
    				
    			|	// Is it a range specifer?
    				//
    				'.'
    				{
    					$type = DOTDOT;	// Yes, it was ..
    				}
    				
    			|	// It was just a single .
    				//
    				
    				{ $type = DOT; }
    		)
    ;

	
fragment
Digits	
	:	('0'..'9')+ 
 	;
 	
fragment
Exponent 
	: 	('e'|'E') ('+'|'-')? 
	
			(
				  Digits
				| 	{ 
						log.error(getCharIndex()-1, MsgSym.MESSAGE_F3_EXPONENT_MALFORMED); 
						setText("0.0");
					}
			)
 	;

// Identifiers are any sequence of vharacters considered
// to be alphanumeric in the Java specification. Identifiers
// that cannot match this pattern may be 'quoted' by surrounding
// them with '<<' and '>>' - this allows external references to
// methods, properties and so on , where the external language
// does not restrict the identifer names to such a pattern or does
// not regard F3 keywords as invalid identifiers.
//
IDENTIFIER 
	: Letter (Letter|JavaIDDigit)*
	| '<<' (~'>'| '>' ~'>')* '>'* '>>'			
	
			{ 
				setText(getText().substring(2, getText().length()-2)); 
			}
	;

// Validate the range of characters considered to be Alpha letter
//
fragment
Letter
    : '\u0024' 
    | '\u0041'..'\u005a' 
    | '\u005f' 
    | '\u0061'..'\u007a' 
    | '\u00c0'..'\u00d6' 
    | '\u00d8'..'\u00f6' 
    | '\u00f8'..'\u00ff' 
    | '\u0100'..'\u1fff' 
    | '\u3040'..'\u318f' 
    | '\u3300'..'\u337f' 
    | '\u3400'..'\u3d2d' 
    | '\u4e00'..'\u9fff' 
    | '\uf900'..'\ufaff'
    ;

// Validate the range of characters considered to be digits.
//
fragment
JavaIDDigit
    : '\u0030'..'\u0039' 
    | '\u0660'..'\u0669' 
    | '\u06f0'..'\u06f9' 
    | '\u0966'..'\u096f' 
    | '\u09e6'..'\u09ef' 
    | '\u0a66'..'\u0a6f' 
    | '\u0ae6'..'\u0aef' 
    | '\u0b66'..'\u0b6f' 
    | '\u0be7'..'\u0bef' 
    | '\u0c66'..'\u0c6f' 
    | '\u0ce6'..'\u0cef' 
    | '\u0d66'..'\u0d6f' 
    | '\u0e50'..'\u0e59' 
    | '\u0ed0'..'\u0ed9' 
    | '\u1040'..'\u1049'
	;



// As with whitespace, F3 comments are not seen by the parser.
// However, certain constructs such as the script itself, will search
// the token stream for documentation comments of the form '/**' .* '*/'
// The are therefore preserved on the hidden channel.
//
COMMENT
    :   '/*' ( options {greedy=false;} : . )* '*/' 
    
    	{
    		$channel=HIDDEN;
    	}
    ;

fragment
DOC_COMMENT
	: '/**'	// Just for documentation, all we need is the token type
	;

LINE_COMMENT
    : 	'//' ~('\n'|'\r')* '\r'? ('\n'|EOF) 
    	
    	{
    		$channel=HIDDEN;
    	}
    ;


    
// This special token is always the last rule in the lexer grammar. It
// is basically a catch all for characters that are not covered by any
// other lexical construct and are therefore illegal. This rule allows
// us to create a sensible error message.
//
INVALIDC
	: .
		{
			// We assume it isn't safe to print as otherwise we would have matched it
			//	
			String disp = $text;
			
			if	(disp == null) {
			
				// Something very strange happened
				//
				log.error(getCharIndex()-1, MsgSym.MESSAGE_F3_BAD_CHARACTER, "<unknown>");
				
			} else {
			
				log.error(getCharIndex()-1, MsgSym.MESSAGE_F3_BAD_CHARACTER, getCharErrorDisplay( disp.charAt(0) ) );
			}
		}
	;
	
