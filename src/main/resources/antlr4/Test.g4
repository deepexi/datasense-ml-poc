grammar Test;

@header {
package com.deepexi.ds.antlr4;
}

stmt : expr;

expr : expr NUL expr    # Mul
     | expr ADD expr    # Add
     | expr DIV expr    # Div
     | expr MIN expr    # Min
     | INT              # Int
     ;

NUL : '*';
ADD : '+';
DIV : '/';
MIN : '-';

INT : Digit+;
Digit : [0-9];

WS : [ \t\u000C\r\n]+ -> skip;
NL: '\r'? '\n' -> skip;
SHEBANG : '#' '!' ~('\n'|'\r')* -> channel(HIDDEN);