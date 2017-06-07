package com.purplepip.odin.sequencer;

/**
 * Default channel implementation.
 */
public class DefaultChannel implements Channel {
  private int number;
  private String programName;
  private int program;

  @Override
  public int getNumber() {
    return number;
  }

  @Override
  public void setNumber(int number) {
    this.number = number;
  }

  @Override
  public String getProgramName() {
    return programName;
  }

  @Override
  public void setProgramName(String programName) {
    this.programName = programName;
  }

  @Override
  public int getProgram() {
    return program;
  }

  @Override
  public void setProgram(int program) {
    this.program = program;
  }
}
