# LR1
A left-right (or shift-reduce) parser for determining the validity and value of a simple expression. Possible operations are addition, subtraction, multiplication, and division. It will also evaluate expressions that contain parentheses.

The parser uses terminal symbols (any positive integer n, +, -, &ast;, /, (, ), as well as end of expression represented by $) and non-terminal symbols E, T, and F, which are made up of terminal symbols and must be reduced to terminal symbols for evaluation. 

Expressions are parsed using a stack. A state (numbered 0-11 on the left side of the parsing table) and symbol are pushed onto the stack based on the current symbol in the expression and the corresponding rule in the parsing table, and the expression is calculated. If the current symbol and state don't have a corresponding rule, the expression is invalid.
