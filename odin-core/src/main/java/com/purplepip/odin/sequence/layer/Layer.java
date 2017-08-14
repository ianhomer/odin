/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
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

package com.purplepip.odin.sequence.layer;

import com.purplepip.odin.project.Project;
import com.purplepip.odin.sequence.tick.Tick;
import java.util.Set;

/**
 * A layer applies behaviour to sequences and child layers.  This include default properties
 * and filters (which will come soon).  Filters will adjust the sequence - for example
 * add variability to velocity, duration, note (transpose) or switch on / off (e.g. chorus / verse).
 */
public interface Layer {
  /**
   * Layer name.
   *
   * @return layer
   */
  String getName();

  /**
   * Parent layer.
   *
   * @return parent layers
   */
  Set<Layer> getParents();

  /**
   * The project that this layer belongs to.
   *
   * @return project
   */
  Project getProject();

  /**
   * Units for 1 tick of this sequence.
   *
   * @return tick units.
   */
  Tick getTick();

  /**
   * Length of this layer in ticks from the offset point.  Note that layer will stop
   * at offset + length ticks.
   *
   * @return length
   */
  long getLength();

  /**
   * Offset for the start of this layer.
   *
   * @return offset
   */
  long getOffset();
}
