package com.purplepip.odin.midix;

import javax.sound.midi.MidiDevice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Matches MIDI device in.
 */
public class MidiDeviceInMatcher extends MidiDeviceNameStartsWithMatcher {
  private static final Logger LOG = LoggerFactory.getLogger(MidiDeviceInMatcher.class);

  public MidiDeviceInMatcher(String prefix) {
    super(prefix);
  }

  @Override
  public boolean matches(MidiDevice device) {
    LOG.debug("Device {} max transmitters {}", device.getDeviceInfo().getName(),
        device.getMaxTransmitters());
    boolean result = device.getMaxTransmitters() > -1;
    LOG.debug("Device {} match {}", device.getDeviceInfo().getName(), result);
    return result;
  }
}
