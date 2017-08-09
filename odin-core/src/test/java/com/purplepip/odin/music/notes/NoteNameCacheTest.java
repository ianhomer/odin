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

package com.purplepip.odin.music.notes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NoteNameCacheTest {
  @Test
  public void testName() {
    NoteNameCache cache = new NoteNameCache();
    assertEquals("C0", cache.getName(0));
    assertEquals("C0", cache.getName(0, false));
    assertEquals("D#4", cache.getName(51));
    assertEquals("D#4", cache.getName(51, true));
    assertEquals("Eb4", cache.getName(51, false));
    assertEquals("F6", cache.getName(77, false));
    assertEquals("F6", cache.getName(77));
    assertEquals("F6", cache.getName(77, false));
    assertEquals("G10", cache.getName(127));
    assertEquals("G10", cache.getName(127, false));
  }
}