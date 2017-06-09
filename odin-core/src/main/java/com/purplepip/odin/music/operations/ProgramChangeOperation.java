/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purplepip.odin.music.operations;

import com.purplepip.odin.sequencer.AbstractChannelOperation;
import com.purplepip.odin.sequencer.Channel;
import lombok.ToString;

/**
 * Program change operation.
 */
@ToString(callSuper = true)
public class ProgramChangeOperation extends AbstractChannelOperation {
  private int bank;
  private int program;
  private String programName;

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

  /**
   * Create program change operation from channel configuration.
   *
   * @param channel channel configuration
   */
  public ProgramChangeOperation(Channel channel) {
    setChannel(channel.getNumber());
    setProgram(channel.getProgram());
    setProgramName(channel.getProgramName());
  }

  protected final void setBank(int bank) {
    this.bank = bank;
  }

  public int getBank() {
    return bank;
  }

  protected final void setProgram(int program) {
    this.program = program;
  }

  public int getProgram() {
    return program;
  }

  private void setProgramName(String programName) {
    this.programName = programName;
  }

  public String getProgramName() {
    return programName;
  }

  public boolean isAbsolute() {
    return programName == null;
  }
}
