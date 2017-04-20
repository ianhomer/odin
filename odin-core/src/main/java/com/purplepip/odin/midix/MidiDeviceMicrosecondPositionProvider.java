package com.purplepip.odin.midix;

import com.purplepip.odin.series.MicrosecondPositionProvider;

/**
 * Microsecond position provider taking the microsecond position from a MIDI device.
 */
public class MidiDeviceMicrosecondPositionProvider implements MicrosecondPositionProvider {
  private MidiDeviceWrapper midiDeviceWrapper;

  public MidiDeviceMicrosecondPositionProvider(MidiDeviceWrapper midiDeviceWrapper) {
    this.midiDeviceWrapper = midiDeviceWrapper;
  }

  @Override
  public long getMicrosecondPosition() {
    return midiDeviceWrapper.getDevice().getMicrosecondPosition();
  }
}
