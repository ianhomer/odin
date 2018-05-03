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

import com.google.common.collect.Lists;
import com.purplepip.odin.specificity.Name;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Execute multiple actions in order.  This is special case of the ActionPlugin and is NOT
 * available explicitly in performance configuration, it is however used to store multiple
 * actions against one trigger.
 */
@Slf4j
@Name("start")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ListAction extends ActionPlugin {
  public static final String LIST_ACTION_START = "...";
  public static final String LIST_ACTION_INDEX = "~~~";
  private List<Action> actionList;

  public ListAction(Action... actions) {
    this(Lists.newArrayList(actions));
  }

  private ListAction(List<Action> actionList) {
    this.actionList = actionList;
  }

  @Override
  public Stream<Action> getRipples() {
    return actionList.stream();
  }

  @Override
  public void execute(ActionContext context) {
    actionList.forEach(action -> action.execute(context));
  }

  /**
   * Get map of multiple actions applied to a single trigger store as map of trigger actions.
   * When a list plugin is loaded then this will be assimilated into a ListAction plugin.
   *
   * @param trigger trigger name
   * @param actions multiple actions to be applied to a trigger
   * @return map of trigger actions
   */
  public static Map<String, ActionConfiguration> asActionConfigurationMap(
      String trigger, ActionConfiguration... actions) {
    Map<String, ActionConfiguration> map = new HashMap<>();
    map.put(trigger, new GenericAction("list"));
    for (int i = 0 ; i < actions.length ; i++) {
      map.put(LIST_ACTION_START + trigger + LIST_ACTION_INDEX + i, actions[i]);
    }
    return map;
  }

  /**
   * Extract list action from actions map.
   *
   * @param trigger trigger name
   * @param actions actions map
   * @param actionFactory action factory
   * @return list action
   */
  public static ListAction asListAction(
      String trigger, Map<String, ActionConfiguration> actions, ActionFactory actionFactory) {
    List<Action> actionList = new ArrayList<>();
    actions.keySet().stream().sorted().forEach(key -> {
      String prefix = LIST_ACTION_START + trigger + LIST_ACTION_INDEX;
      if (key.startsWith(prefix)) {
        actionList.add(actionFactory.newInstance(actions.get(key)));
      }
    });
    return new ListAction(actionList);
  }
}
