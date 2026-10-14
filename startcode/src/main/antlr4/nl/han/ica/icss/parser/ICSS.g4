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
stylesheet: line* EOF;

line: stylerule | variableassignment;

stylerule: selector OPEN_BRACE styleline* CLOSE_BRACE;
variableassignment: variablereference ASSIGNMENT_OPERATOR expression SEMICOLON;

selector: tagselector | classselector | idselector;
tagselector: (LOWER_IDENT | CAPITAL_IDENT) ;
classselector: CLASS_IDENT;
idselector: ID_IDENT;

styleline: declaration | ifstatement;

declaration: propertyname COLON expression SEMICOLON;

propertyname: LOWER_IDENT;

ifstatement: ifclause elseclause?;
ifclause: IF BOX_BRACKET_OPEN conditionalexpression BOX_BRACKET_CLOSE thenclause;
elseclause: ELSE thenclause;
thenclause: OPEN_BRACE styleline* CLOSE_BRACE;

conditionalexpression: booleanvalue | booleanvarref;
expression: value | operation | variablereference;

operation: (addoperation | subtractoperation | multiplyoperation)+;
addoperation: operatorvalue PLUS operatorvalue;
subtractoperation: operatorvalue MIN operatorvalue;
multiplyoperation: operatorvalue MUL operatorvalue;


value: booleanvalue | colorvalue | pixelvalue | percentagevalue | scalarvalue | variablereference;

operatorvalue: pixelvalue | percentagevalue | scalarvalue | operationalvarref;
pixelvalue: PIXELSIZE;
percentagevalue: PERCENTAGE;
scalarvalue: SCALAR;
booleanvalue: TRUE | FALSE;
colorvalue: COLOR;

variablereference: scalarvarref | pixelvarref | percentagevarref | booleanvarref | colorvarref |  operationalvarref;

scalarvarref: CAPITAL_IDENT | LOWER_IDENT;
pixelvarref: CAPITAL_IDENT | LOWER_IDENT;
percentagevarref: CAPITAL_IDENT | LOWER_IDENT;
booleanvarref: CAPITAL_IDENT | LOWER_IDENT;
colorvarref: CAPITAL_IDENT | LOWER_IDENT;
operationalvarref: percentagevarref | pixelvarref | scalarvarref;


