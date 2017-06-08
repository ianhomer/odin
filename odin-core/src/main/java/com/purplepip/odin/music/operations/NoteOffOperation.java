package com.purplepip.odin.music.operations;

import lombok.ToString;

/**
 * Note off operation.  Note that note off provides velocity information as per MIDI
 * specification to cater for after-touch.  Currently we explicitly velocity for note off to 0.
 */
@ToString(callSuper = true)
public class NoteOffOperation extends NoteVelocityOperation {
  /**
   * Create note off operation.
   *
   * @param channel channel
   * @param number note number
   */
  public NoteOffOperation(int channel, int number) {
    setChannel(channel);
    setNumber(number);
    setVelocity(0);
  }
}
