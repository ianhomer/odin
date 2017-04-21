package com.purplepip.odin.music;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Default note test.
 */
public class DefaultNoteTest {
  @Test
  public void testDefaultNote() {
    Note note = new DefaultNote();
    assertEquals("Default note number not correct", 60, note.getNumber());
    assertEquals("Default note velocity not correct", 40, note.getVelocity());
  }
}