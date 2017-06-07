package com.purplepip.odin.sequencer;

/**
 * Channel configuration.
 */
public interface Channel {
  /**
   * Get channel number.
   *
   * @return channel number
   */
  int getNumber();

  /**
   * Set channel number.
   *
   * @param number channel number
   */
  void setNumber(int number);

  /**
   * Get program name.
   *
   * @return program name
   */
  String getProgramName();

  /**
   * Set program name.
   *
   * @param programName program name
   */
  void setProgramName(String programName);
}
