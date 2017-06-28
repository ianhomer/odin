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

package com.purplepip.odin.sequence;

import com.purplepip.odin.common.Copyable;
import com.purplepip.odin.project.Project;
import java.util.Set;

/**
 * Sequence configuration.
 */
public interface Sequence extends Copyable<Sequence> {
  /**
   * Unique sequence ID.
   *
   * @return sequence ID
   */
  long getId();

  /**
   * Units for 1 tick of this sequence.
   *
   * @return tick units.
   */
  Tick getTick();

  /**
   * Length of this sequence in ticks from the offset point.  Note that sequence will stop
   * at offset + length ticks.
   *
   * @return length
   */
  long getLength();

  /**
   * Offset for the start of this sequence.
   *
   * @return offset
   */
  long getOffset();

  /**
   * Channel number for this sequence.
   *
   * @return channel
   */
  int getChannel();

  /**
   * Flow name for this sequence.  This is currently exactly equal to the class name of flow.
   * The flow provides logic for how the sequence progresses over time.
   *
   * @return flow name.
   */
  String getFlowName();

  /**
   * Get the project.
   *
   * @return project
   */
  Project getProject();

  /**
   * Set the project.
   *
   * @param project project
   */
  void setProject(Project project);

  /**
   * Get the sequence layers.
   */
  Set<Layer> getLayers();
}
