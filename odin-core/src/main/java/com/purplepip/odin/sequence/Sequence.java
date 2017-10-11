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

package com.purplepip.odin.sequence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.purplepip.odin.common.Copyable;
import com.purplepip.odin.project.Project;
import com.purplepip.odin.properties.beany.PropertiesProvider;
import com.purplepip.odin.sequence.tick.TimeThing;
import com.purplepip.odin.sequence.triggers.Action;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;

/**
 * Sequence configuration.
 */
public interface Sequence extends Copyable<Sequence>, TimeThing, PropertiesProvider {
  /**
   * Create a copy of this sequence.
   *
   * @return copy
   */
  @Override
  default Sequence copy() {
    MutableSequence copy = new DefaultSequence(this.getId());
    Sequences.copyCoreValues(this, copy);
    return copy;
  }

  /**
   * Whether this class is a specialised sequence.
   *
   * @return true if it is a specialised sequence.
   */
  @JsonIgnore
  // TODO : NOT particulary robust default implementation, a better one will be provided
  default boolean isSpecialised() {
    return getClass().getInterfaces().length > 1
        || getClass().getInterfaces()[0] != MutableSequence.class;
  }

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
  @NotNull
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
   * Get the sequence layer names.
   */
  @NotNull
  List<String> getLayers();

  /**
   * Whether the sequence will never generate any events.
   *
   * @return is empty
   */
  @JsonIgnore
  boolean isEmpty();

  /**
   * Map of triggers and corresponding action.
   *
   * @return trigger names
   */
  Map<String, Action> getTriggers();
}
