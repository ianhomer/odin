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

package com.purplepip.odin.creation.sequence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.purplepip.odin.clock.tick.TimeThing;
import com.purplepip.odin.creation.triggers.Action;
import com.purplepip.odin.specificity.ThingConfiguration;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;

/**
 * Sequence configuration.   This class contains pure data that can be readily persisted.
 * @see Sequence for interface which can include sequence logic.
 */
public interface SequenceConfiguration extends ThingConfiguration, TimeThing  {

  /**
   * Create a copy of this sequence.
   *
   * @return copy
   */
  @Override
  default SequenceConfiguration copy() {
    MutableSequenceConfiguration copy = new GenericSequence(this.getId());
    Sequences.copyCoreValues(this, copy);
    return copy;
  }

  /**
   * Channel number for this sequence.
   *
   * @return channel
   */
  int getChannel();

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
