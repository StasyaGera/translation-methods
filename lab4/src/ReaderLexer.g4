lexer grammar ReaderLexer;

VBAR          : '|' ;
COLON         : ':' ;
SEMICOLON     : ';' ;
OPAREN        : '(' ;
CPAREN        : ')' ;
QUESTION      : '?' ;
PLUS          : '+' ;
ASTERISK      : '*' ;

RULE          : [a-z_][a-zA-Z0-9_]* ;
TOKEN         : [A-Z][A-Z0-9_]* ;

ANY           : '\''.+?'\'';
REGEXP        : ('['.+?']'('*'|'+'|'?')?)+ ;

WS            : [\r\n\t ]+ -> skip ;
