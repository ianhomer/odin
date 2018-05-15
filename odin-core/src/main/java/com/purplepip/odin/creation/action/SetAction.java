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

import com.google.common.base.Splitter;
import com.purplepip.odin.specificity.Name;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Set properties in the sequence.
 */
@Slf4j
@Name("set")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SetAction extends ActionPlugin {
  /**
   * Properties as a string, e.g. a=1;b=2
   */
  // TODO : We'll support expressions here one day, e.g. a=${a+1};b=2
  private String nameValuePairs;
  private transient Map<String, String> values;

  /**
   * Initialisation after properties are set.
   */
  @Override
  public void initialise() {
    LOG.debug("Initialising {}", this);
    initialiseValues();
    super.initialise();
  }

  @Override
  public void execute(ActionContext context) {
    values.forEach((key, value) -> context.getTrack().setProperty(key, value));
  }

  /**
   * Create a copy of this action.
   *
   * @return copy
   */
  @Override
  public SetAction copy() {
    return copy(new SetAction());
  }

  /**
   * Set properties that should be set by this action.
   *
   * @param nameValuePairs name value pairs string, e.g. "a=b;c=2"
   * @return this
   */
  public SetAction nameValuePairs(String nameValuePairs) {
    this.nameValuePairs = nameValuePairs;
    initialiseValues();
    return this;
  }

  private void initialiseValues() {
    if (nameValuePairs != null) {
      values = Splitter.on(';').withKeyValueSeparator('=').split(nameValuePairs);
    }
  }
}
