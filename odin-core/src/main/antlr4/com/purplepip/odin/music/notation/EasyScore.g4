/**
 * EasyFlow ANTLR grammar
 * Example : C#5/q, B4, A4, G#4
 */

grammar EasyScore;

WHITESPACE : [ \t]+ -> skip ;
COMMA : [,]+ -> skip ;
SLASH: [/];
NEWLINE: [\n\r];
LETTER: [A-G];
ACCIDENTAL : [#@n];
OCTAVE : [0-9];
DURATION: [/][hq8];

composition : (line)+;

line: NEWLINE* measure+ NEWLINE*;
measure: notes+;
notes: note;
note: letter (accidental)? octave (duration)?;
letter : LETTER;
accidental : ACCIDENTAL;
octave : OCTAVE;
duration: DURATION;