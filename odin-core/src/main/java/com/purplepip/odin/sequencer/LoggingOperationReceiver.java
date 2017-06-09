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

package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Operation receiver that logs each operation.
 */
public class LoggingOperationReceiver implements OperationReceiver {
  private static final Logger LOG = LoggerFactory.getLogger(LoggingOperationReceiver.class);

  @Override
  public void send(Operation operation, long time) throws OdinException {
    LOG.debug("Operation {} at time {}", operation, time);
  }
}
