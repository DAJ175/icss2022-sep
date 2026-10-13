grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';




//--- PARSER: ---
stylesheet: stylerule* EOF;

stylerule: tagselector | classselector | idselector | variableassignment;

tagselector: (LOWER_IDENT | CAPITAL_IDENT) OPEN_BRACE line* CLOSE_BRACE;
classselector: CLASS_IDENT OPEN_BRACE line* CLOSE_BRACE;
idselector: ID_IDENT OPEN_BRACE line* CLOSE_BRACE;
variableassignment: variablereference ASSIGNMENT_OPERATOR propertyvalue SEMICOLON ;

line: styleline | ifstatement;
styleline: propertyname COLON propertyvalue SEMICOLON;

propertyname: LOWER_IDENT;
propertyvalue: expression | value;

booleanStatement: TRUE | FALSE | booleanvarref;

ifstatement: ifclause thenclause elseclause?;
ifclause: IF BOX_BRACKET_OPEN booleanStatement BOX_BRACKET_CLOSE;
elseclause: ELSE thenclause;
thenclause: OPEN_BRACE (ifclause | styleline)* CLOSE_BRACE;

expression: operatorvalue | expression operations operatorvalue;
operations: PLUS | MIN | MUL;
value: TRUE | FALSE | COLOR | PIXELSIZE | PERCENTAGE | SCALAR | variablereference;
booleanvalue: TRUE | FALSE | booleanvarref;
operatorvalue: PIXELSIZE | PERCENTAGE | SCALAR | operationalvarref;
variablereference: booleanvarref | operationalvarref | CAPITAL_IDENT | LOWER_IDENT;

booleanvarref: CAPITAL_IDENT | LOWER_IDENT;
operationalvarref: CAPITAL_IDENT | LOWER_IDENT;

