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

package com.purplepip.odin.creation.plugin;

import com.purplepip.odin.bag.Thing;
import com.purplepip.odin.creation.aspect.Aspect;

/**
 * An aspect driven by a plugin.
 *
 * @param <C> configuration for the aspect.
 */
public interface PluggableAspect<C extends Thing> extends Aspect {
  C getConfiguration();

  /**
   * Set the configuration for the aspect and in turn ensure that the correct plugin has been
   * loaded.
   *
   * @param configuration configuration for the aspect.
   */
  void setConfiguration(C configuration);
}
