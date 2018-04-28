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

import com.purplepip.odin.creation.plugin.Plugin;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public abstract class ActionPlugin extends GenericAction implements Action, Plugin {

  /**
   * By default the ripples from an action plugin is just this plugin itself.  The ListAction
   * is an example of an action plugin that overrides this behaviour.
   *
   * @return ripples from this action
   */
  public Stream<Action> getRipples() {
    return Stream.of(this);
  }
}
