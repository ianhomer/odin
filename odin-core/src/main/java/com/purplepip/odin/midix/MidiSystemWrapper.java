package com.purplepip.odin.midix;

import java.util.HashSet;
import java.util.Set;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;

/**
 * Wrapper around the MidiSystem static.
 */
public class MidiSystemWrapper {
  /**
   * Return a set of MIDI device infos.
   *
   * @return set of MIDI device infos
   */
  public Set<MidiDevice.Info> getMidiDeviceInfos() {
    Set<MidiDevice.Info> infos = new HashSet<>();
    for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
      infos.add(info);
    }
    return infos;
  }
}
