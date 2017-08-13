package com.purplepip.odin.music.notes;

import static com.purplepip.odin.music.notes.Letter.A;
import static com.purplepip.odin.music.notes.Letter.B;
import static com.purplepip.odin.music.notes.Letter.C;
import static com.purplepip.odin.music.notes.Letter.D;
import static com.purplepip.odin.music.notes.Letter.E;
import static com.purplepip.odin.music.notes.Letter.F;
import static com.purplepip.odin.music.notes.Letter.G;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class NoteNumberTest {
  @Test
  public void testNumbers() {
    assertEquals(12, new NoteNumber(C, 0, 0).getValue());
    assertEquals(72, new NoteNumber(C, 0, 5).getValue());
    assertEquals(73, new NoteNumber(C, 1, 5).getValue());
    assertEquals(74, new NoteNumber(D, 0, 5).getValue());
    assertEquals(75, new NoteNumber(D, 1, 5).getValue());
    assertEquals(75, new NoteNumber(E, -1, 5).getValue());
    assertEquals(76, new NoteNumber(E, 0, 5).getValue());
    assertEquals(77, new NoteNumber(F, 0, 5).getValue());
    assertEquals(78, new NoteNumber(F, 1, 5).getValue());
    assertEquals(79, new NoteNumber(G, 0, 5).getValue());
    assertEquals(80, new NoteNumber(G, 1, 5).getValue());
    assertEquals(81, new NoteNumber(A, 0, 5).getValue());
    assertEquals(82, new NoteNumber(A, 1, 5).getValue());
    assertEquals(83, new NoteNumber(B, 0, 5).getValue());
  }
}