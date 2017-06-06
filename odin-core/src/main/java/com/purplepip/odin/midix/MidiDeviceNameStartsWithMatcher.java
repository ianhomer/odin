package com.purplepip.odin.midix;

import javax.sound.midi.MidiDevice;

/**
 * Match MIDI devices where the name matches the given starts with string.
 */
public class MidiDeviceNameStartsWithMatcher implements MidiDeviceMatcher {
  private String prefix;

  public MidiDeviceNameStartsWithMatcher(String prefix) {
    this.prefix = prefix;
  }

  @Override
  public boolean matches(MidiDevice.Info info) {
    return info.getName().startsWith(prefix);
  }

  @Override
  public boolean matches(MidiDevice device) {
    return matches(device.getDeviceInfo());
  }

  @Override
  public String getDescription() {
    return "Starts with " + prefix;
  }
}
