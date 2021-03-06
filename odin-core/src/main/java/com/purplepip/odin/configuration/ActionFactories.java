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

package com.purplepip.odin.configuration;

import com.purplepip.odin.creation.action.Action;
import com.purplepip.odin.creation.action.ActionFactory;
import com.purplepip.odin.creation.action.DisableAction;
import com.purplepip.odin.creation.action.EnableAction;
import com.purplepip.odin.creation.action.IncrementAction;
import com.purplepip.odin.creation.action.InitialiseAction;
import com.purplepip.odin.creation.action.LoadAction;
import com.purplepip.odin.creation.action.SetAction;
import com.purplepip.odin.creation.action.StartAction;
import java.util.ArrayList;
import java.util.List;

public final class ActionFactories {
  /**
   * Create a new action factory.
   *
   * @return a new action factory
   */
  public static ActionFactory newActionFactory() {
    List<Class<? extends Action>> classes = new ArrayList<>();
    classes.add(DisableAction.class);
    classes.add(EnableAction.class);
    classes.add(IncrementAction.class);
    classes.add(InitialiseAction.class);
    classes.add(LoadAction.class);
    classes.add(SetAction.class);
    classes.add(StartAction.class);
    return new ActionFactory(classes);
  }

  private ActionFactories() {
  }
}
