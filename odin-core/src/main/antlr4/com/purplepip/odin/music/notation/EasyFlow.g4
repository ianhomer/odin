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
SHARP : [#];
OCTAVE : [0-9];
QUAVER: [q];

composition : (line)+;

line: NEWLINE* measure+ NEWLINE*;
measure: notes+;
notes: note (duration)?;
note: letter (sharp)? octave;
letter : LETTER;
sharp : SHARP;
octave : OCTAVE;
duration: SLASH QUAVER;