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

styleline: declaration | ifclause;

declaration: propertyname COLON expression SEMICOLON;

propertyname: LOWER_IDENT;


ifclause: IF BOX_BRACKET_OPEN conditionalexpression BOX_BRACKET_CLOSE thenclause elseclause?;
elseclause: ELSE thenclause;
thenclause: OPEN_BRACE (styleline | variableassignment)* CLOSE_BRACE;

conditionalexpression:
    (TRUE | FALSE) # BooleanLiteralCondition
    | variablereference # BooleanVariableCondition;

expression: value | variablereference | operation;

operation: (add | subtract | multiply)+;
add: operatorvalue PLUS operatorvalue;
subtract: operatorvalue MIN operatorvalue;
multiply: operatorvalue MUL operatorvalue;

operatorvalue:
    PIXELSIZE # OperatorPixelLiteral
    | SCALAR # OperatorScalarLiteral
    | variablereference # OperatorVariableReference;

value:
    (TRUE | FALSE) # BooleanLiteral
    | COLOR # ColorLiteral
    | PIXELSIZE # PixelLiteral
    | PERCENTAGE # PercentageLiteral
    | SCALAR # ScalarLiteral;

variablereference: CAPITAL_IDENT | LOWER_IDENT;