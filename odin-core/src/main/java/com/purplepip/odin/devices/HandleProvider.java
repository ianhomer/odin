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

package com.purplepip.odin.devices;

import java.util.Set;

/**
 * A provider that gives handles on to devices.
 */
public interface HandleProvider {
  /**
   * Handle classes that this handle provider can return.
   *
   * @return handle classes
   */
  Set<Class<? extends Handle>> getHandleClasses();

  /*
   * Get handles for this provider
   */
  Set<Handle> getHandles();
}
