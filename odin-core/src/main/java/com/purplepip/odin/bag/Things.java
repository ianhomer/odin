/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
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

package com.purplepip.odin.bag;

import java.util.stream.Stream;

public interface Things<T extends Thing> extends Iterable<T> {
  /**
   * Stream of things.
   *
   * @return stream of things.
   */
  Stream<T> stream();

  /**
   * Number of things.
   *
   * @return size
   */
  int size();

  /**
   * Find thing by ID.
   *
   * @return thing
   */
  T findById(long id);

  /**
   * Access statistics.
   */
  ThingStatistics getStatistics();
}
