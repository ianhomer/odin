package com.purplepip.odin.music;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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