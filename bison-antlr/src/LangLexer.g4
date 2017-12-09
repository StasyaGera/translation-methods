lexer grammar LangLexer;

IF            : 'if' ;
ELSE          : 'else' ;
READ          : 'read' ;
PRINT         : 'print' ;
COLON         : ':' ;
DOT           : '.' ;
COMMA         : ',' ;

ADD           : '+' ;
SUB           : '-' ;
MUL           : '*' ;
DIV           : '/' ;
ASSIGN        : '=' ;
L             : '<' ;
G             : '>' ;
LEQ           : '<=' ;
GEQ           : '>=' ;
EQ            : '==' ;
NE            : '!=' ;
LPAREN        : '(' ;
RPAREN        : ')' ;

INT           : '0'|[1-9][0-9]* ;
ID            : [a-zA-Z_][A-Za-z0-9_]* ;

NEWLINE       : '\r\n' | 'r' | '\n' ;
WS            : [\t ]+ -> skip ;
