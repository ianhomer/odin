package com.purplepip.odin.midix;

import javax.sound.midi.ShortMessage;

/**
 * Raw MIDI Messages that take raw bytes.  We use this technique as it uses more logic from the
 * generic layer that is also used on Android implementation.
 */
class RawMidiMessage extends ShortMessage {
  RawMidiMessage(byte[] buffer) {
    super(buffer);
  }
}
