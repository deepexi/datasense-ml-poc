/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

grammar Ds;
@header{
package com.deepexi.ds.antlr4;
}

tokens {
    DELIMITER
}

standaloneExpression
    : expression EOF
    ;

expression
    : booleanExpression
    ;

booleanExpression
    : valueExpression predicate[$valueExpression.ctx]?  #predicated
    | NOT booleanExpression                             #logicalNot
    | booleanExpression AND booleanExpression           #and
    | booleanExpression OR booleanExpression            #or
    ;

predicate[ParserRuleContext value]
    : comparisonOperator right=valueExpression                            #comparison
    | IS NOT? NULL                                                        #nullPredicate
    ;

valueExpression
    : primaryExpression                                                                 #valueExpressionDefault
    | operator=(MINUS | PLUS) valueExpression                                           #arithmeticUnary
    | left=valueExpression operator=(ASTERISK | SLASH | PERCENT) right=valueExpression  #arithmeticBinary
    | left=valueExpression operator=(PLUS | MINUS) right=valueExpression                #arithmeticBinary
    | left=valueExpression CONCAT right=valueExpression                                 #concatenation
    ;

primaryExpression
    : NULL                                                                                #nullLiteral
    | interval                                                                            #intervalLiteral
    | number                                                                              #numericLiteral
    | booleanValue                                                                        #booleanLiteral
    | string                                                                              #stringLiteral
    | dataTypeValue                                                                       #dataTypeLiteral
    | UDF_FUNCTION  '(' (expression (',' expression)*)? ')'                               #udf
    | qualifiedName '(' (label=identifier '.')? ASTERISK ')'                              #functionCall
    | qualifiedName '(' (expression (',' expression)*)? ')'                               #functionCall
    | CASE operand=expression whenClause+ (ELSE elseExpression=expression)? END           #simpleCase
    | CASE whenClause+ (ELSE elseExpression=expression)? END                              #simpleCase
    | identifier                                                                          #columnReference
    | base=primaryExpression '.' fieldName=identifier                                     #dereference
    | '(' expression ')'                                                                  #parenthesizedExpression
    ;

string
    : STRING                                #basicStringLiteral
    ;

comparisonOperator
    : EQ | NEQ | LT | LTE | GT | GTE
    ;

booleanValue
    : TRUE | FALSE
    ;

interval
    : INTERVAL sign=(PLUS | MINUS)? string from=intervalField (TO to=intervalField)?
    ;

intervalField
    : YEAR | MONTH | DAY | HOUR | MINUTE | SECOND
    ;

whenClause
    : WHEN condition=expression THEN result=expression
    ;

qualifiedName
    : identifier ('.' identifier)*
    ;

identifier
    : IDENTIFIER             #unquotedIdentifier
    | QUOTED_IDENTIFIER      #quotedIdentifier
    | nonReserved            #unquotedIdentifier
    | BACKQUOTED_IDENTIFIER  #backQuotedIdentifier
    | DIGIT_IDENTIFIER       #digitIdentifier
    ;

number
    : MINUS? DECIMAL_VALUE  #decimalLiteral
    | MINUS? DOUBLE_VALUE   #doubleLiteral
    | MINUS? INTEGER_VALUE  #integerLiteral
    ;

dataTypeValue
    : 'BOOL' | 'bool'
    | 'DATE' | 'date'
    | 'DATETIME' | 'datetime'
    | 'DECIMAL' | 'decimal'
    | 'INT' | 'int'
    | 'STRING' | 'string'
    | 'TIME' | 'time'
    | 'TIMESTAMP' | 'timestamp'
    | 'UNKNOWN_TYPE' | 'unknown_type'
    ;

nonReserved
    // IMPORTANT: this rule must only contain tokens. Nested rules are not supported. See SqlParser.exitNonReserved
    : ALL | ANY | ASC
    | COUNT
    | DATE | DAY | DESC
    | EXCLUDING
    | FIRST | FOLLOWING
    | HOUR
    | IF | INCLUDING | INTERVAL
    | LAST | LEADING | LIMIT
    | MINUTE | MONTH
    | NO | NONE | NULLIF
    | OF | OVER
    | RANGE
    | SECOND | SET
    | SUBSTRING
    | TEXT | TIME | TIMESTAMP | TO
    | UNIQUE
    | WINDOW
    | YEAR
    | BASE_DATE
    ;

CASE: 'CASE' | 'case';
WHEN: 'WHEN' | 'when';
THEN: 'THEN' | 'then';
ELSE: 'ELSE' | 'else';
END: 'END' | 'end';
TRUE: 'TRUE' | 'true';
FALSE: 'FALSE' | 'false';
UDF_FUNCTION: 'UDF_FUNCTION' | 'udf_function';

DAY: 'DAY' | 'day';
YEAR: 'YEAR' | 'year';
MONTH: 'MONTH' | 'month';
HOUR: 'HOUR' | 'hour';
MINUTE: 'MINUTE' | 'minute';
SECOND: 'SECOND' | 'second';
BASE_DATE: 'BASE_DATE' | 'base_date';


ALL: 'ALL';
AND: 'AND';
ANY: 'ANY';
AS: 'AS';
ASC: 'ASC';
BY: 'BY';
CAST: 'CAST';
COUNT: 'COUNT';
DATE: 'DATE';
DESC: 'DESC';
DISTINCT: 'DISTINCT';
EXCEPT: 'EXCEPT';
EXCLUDING: 'EXCLUDING';
FIRST: 'FIRST';
FOLLOWING: 'FOLLOWING';
IF: 'IF';
IN: 'IN';
INCLUDING: 'INCLUDING';
INTERVAL: 'INTERVAL';
IS: 'IS';
LAST: 'LAST';
LEADING: 'LEADING';
LIMIT: 'LIMIT';
NO: 'NO';
NONE: 'NONE';
NOT: 'NOT';
NULL: 'NULL';
NULLIF: 'NULLIF';
OF: 'OF';
ON: 'ON';
OR: 'OR';
OVER: 'OVER';
RANGE: 'RANGE';
SET: 'SET';
SUBSTRING: 'SUBSTRING';
TEXT: 'TEXT';
TIME: 'TIME';
TIMESTAMP: 'TIMESTAMP';
TO: 'TO';
TRIM: 'TRIM';
UNIQUE: 'UNIQUE';
WINDOW: 'WINDOW';

EQ: '=';
NEQ: '<>' | '!=';
LT: '<';
LTE: '<=';
GT: '>';
GTE: '>=';

PLUS: '+';
MINUS: '-';
ASTERISK: '*';
SLASH: '/';
PERCENT: '%';
CONCAT: '||';
QUESTION_MARK: '?';

STRING
    : '\'' ( ~'\'' | '\'\'' )* '\''
    ;

INTEGER_VALUE
    : DIGIT+
    ;

DECIMAL_VALUE
    : DIGIT+ '.' DIGIT*
    | '.' DIGIT+
    ;

DOUBLE_VALUE
    : DIGIT+ ('.' DIGIT*)? EXPONENT
    | '.' DIGIT+ EXPONENT
    ;

IDENTIFIER
    : (LETTER | '_') (LETTER | DIGIT | '_')*
    ;

DIGIT_IDENTIFIER
    : DIGIT (LETTER | DIGIT | '_')+
    ;

QUOTED_IDENTIFIER
    : '"' ( ~'"' | '""' )* '"'
    ;

BACKQUOTED_IDENTIFIER
    : '`' ( ~'`' | '``' )* '`'
    ;

fragment EXPONENT
    : 'E' [+-]? DIGIT+
    ;

fragment DIGIT
    : [0-9]
    ;

fragment LETTER
    : [a-zA-Z]
    ;

WS
    : [ \r\n\t]+ -> channel(HIDDEN)
    ;

// Catch-all for anything we can't recognize.
// We use this to be able to ignore and recover all the text
// when splitting statements with DelimiterLexer
UNRECOGNIZED
    : .
    ;
