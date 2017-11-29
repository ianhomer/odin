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

package com.purplepip.odin.creation.action;

import com.purplepip.odin.creation.track.Track;
import com.purplepip.odin.specificity.Name;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Set properties in the sequence.
 */
// TODO : Increment action will probably get deprecated when SetAction supports expressions.
@Slf4j
@Name("increment")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class IncrementAction extends ActionPlugin {
  private String propertyName;
  private int increment;

  @Override
  public void execute(ActionContext context) {
    Track track = context.getTrack();
    track.setProperty(propertyName,
        String.valueOf(Integer.valueOf(track.getProperty(propertyName)) + increment));
  }

  /**
   * Create a copy of this action.
   *
   * @return copy
   */
  @Override
  public IncrementAction copy() {
    return copy(new IncrementAction(), IncrementAction.class);
  }

  public IncrementAction propertyName(String propertyName) {
    this.propertyName = propertyName;
    return this;
  }

  public IncrementAction increment(int increment) {
    this.increment = increment;
    return this;
  }
}
