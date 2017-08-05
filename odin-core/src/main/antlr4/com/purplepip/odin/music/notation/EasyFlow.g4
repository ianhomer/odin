/**
 * EasyFlow ANTLR grammar
 * Example : C#5/q, B4, A4, G#4
 */

grammar EasyFlow;

WHITESPACE : [ \t]+ -> skip ;
COMMA : [,]+ -> skip ;
SLASH: [/];
NEWLINE: [\n\r];
LETTER: [A-G];
ACCIDENTAL : [#@n];
OCTAVE : [0-9];
QUAVER: [q];

composition : (line)+;

line: NEWLINE* measure+ NEWLINE*;
measure: notes+;
notes: note (duration)?;
note: letter (accidental)? octave;
letter : LETTER;
accidental : ACCIDENTAL;
octave : OCTAVE;
duration: SLASH QUAVER;