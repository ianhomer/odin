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

import com.purplepip.odin.common.ClassUri;
import com.purplepip.odin.performance.Performance;
import com.purplepip.odin.specificity.Name;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Load the named performance.
 */
@Slf4j
@Name("load")
@ToString
public class LoadAction extends ActionPlugin {
  private String performance;

  public LoadAction performance(Class<? extends Performance> clazz) {
    return performance(new ClassUri(clazz).getUri().getSchemeSpecificPart());
  }

  public LoadAction performance(String performance) {
    this.performance = performance;
    return this;
  }

  public String getPerformance() {
    return performance;
  }

  @Override
  public void execute(ActionContext context) {
    LOG.debug("Executing {} on track {}", this, context.getTrack().getName());
    context.getTrack().setProperty("performance", performance);
    context.getTrack().initialise();
    context.getTrack().start();
  }

  /**
   * Create a copy of this action.
   *
   * @return copy
   */
  @Override
  public LoadAction copy() {
    return copy(new LoadAction(), LoadAction.class);
  }
}
