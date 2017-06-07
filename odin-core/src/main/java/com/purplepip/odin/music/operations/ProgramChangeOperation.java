package com.purplepip.odin.music.operations;

import com.purplepip.odin.sequencer.AbstractChannelOperation;

/**
 * Program change operation.
 */
public class ProgramChangeOperation extends AbstractChannelOperation {
  private int bank;
  private int program;

  /**
   * Create program change operation.
   *
   * @param channel channel
   * @param bank bank
   * @param program program
   */
  public ProgramChangeOperation(int channel, int bank, int program) {
    setChannel(channel);
    setBank(bank);
    setProgram(program);
  }

  protected void setBank(int bank) {
    this.bank = bank;
  }

  public int getBank() {
    return bank;
  }

  protected void setProgram(int program) {
    this.program = program;
  }

  public int getProgram() {
    return program;
  }

}
