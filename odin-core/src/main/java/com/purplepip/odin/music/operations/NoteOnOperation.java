/*
 * Copyright (c) 2017 the original author or authors. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purplepip.odin.music.operations;

/**
 * Note on operation.
 *
 * <p>Note that this is bias towards music, but it will be abstracted at some point to other
 * operations.
 */
public class NoteOnOperation extends AbstractNoteVelocityOperation {

  /**
   * Create an operation.
   *
   * @param channel Channel for the operation
   * @param number Number for the operation
   * @param velocity Velocity for the operation
   */
  public NoteOnOperation(int channel, int number, int velocity) {
    setChannel(channel);
    setNumber(number);
    setVelocity(velocity);
  }

  public String toString() {
    return "▼" + this.getNumber() + "↑" + this.getVelocity() + " → "
        + super.getChannel();
  }
}
