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

package com.purplepip.odin.sequencer;

import com.purplepip.odin.clock.PerformanceListener;
import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.operation.Operation;

/**
 * Sequenced Operation Receiver.  Note that the operation receiver extends a PerformanceListener
 * since the time that the operation is sent is a performance time.  If the receiver has it's
 * own microsecond provider (e.g. a MIDI receiver) then it needs to be able to convert times
 * to it's own timing based on when the performance started.
 */
public interface OperationReceiver extends PerformanceListener {
  /**
   * Handle operation at the given performance time.
   *
   * @param operation operation to handle
   * @param time performance time
   * @throws OdinException exception
   */
  void handle(Operation operation, long time) throws OdinException;
}
