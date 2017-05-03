package com.purplepip.odin.midix;

import javax.sound.midi.MidiDevice;

/**
 * Matcher to detect whether MIDI device matches condition.
 */
public interface MidiDeviceMatcher {
  /**
   * Match based on MIDI device info.
   *
   * @param info MIDI device info to match on
   * @return true if MIDI device matches
   */
  boolean matches(MidiDevice.Info info);

  /**
   * Match based on MIDI device.
   *
   * @param device MIDI device to match on
   * @return true if MIDI device matches
   */
  boolean matches(MidiDevice device);

  String getDescription();
}
