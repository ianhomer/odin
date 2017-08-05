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
    assertEquals(0, new NoteNumber(C, 0, 0).getValue());
    assertEquals(60, new NoteNumber(C, 0, 5).getValue());
    assertEquals(61, new NoteNumber(C, 1, 5).getValue());
    assertEquals(62, new NoteNumber(D, 0, 5).getValue());
    assertEquals(63, new NoteNumber(D, 1, 5).getValue());
    assertEquals(63, new NoteNumber(E, -1, 5).getValue());
    assertEquals(64, new NoteNumber(E, 0, 5).getValue());
    assertEquals(65, new NoteNumber(F, 0, 5).getValue());
    assertEquals(66, new NoteNumber(F, 1, 5).getValue());
    assertEquals(67, new NoteNumber(G, 0, 5).getValue());
    assertEquals(68, new NoteNumber(G, 1, 5).getValue());
    assertEquals(69, new NoteNumber(A, 0, 5).getValue());
    assertEquals(70, new NoteNumber(A, 1, 5).getValue());
    assertEquals(71, new NoteNumber(B, 0, 5).getValue());
  }
}