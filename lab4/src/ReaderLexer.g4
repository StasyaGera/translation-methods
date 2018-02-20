lexer grammar ReaderLexer;

VBAR          : '|' ;
COLON         : ':' ;
SEMICOLON     : ';' ;
OPAREN        : '(' ;
CPAREN        : ')' ;
OBRACK        : '[' ;
CBRACK        : ']' ;
QUESTION      : '?' ;
PLUS          : '+' ;
ASTERISK      : '*' ;
COMMA         : ',' ;

RETURNS       : 'returns' ;
HEADER        : '@header' ;
MEMBERS       : '@members' ;
INIT          : '@init' ;

RULE          : [a-z_][a-zA-Z0-9_]* ;
TOKEN         : [A-Z][a-zA-Z0-9_]* ;

ANY           : '\''.+?'\'';
CODE          : '{'.+?'}';
REGEXP        : '/'('['.+?']' ('*'|'+'|'|'|'?')?)+'/' ;

WS            : [\r\n\t ]+ -> skip ;
