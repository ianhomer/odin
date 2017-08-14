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

package com.purplepip.odin.sequence.conductor;

import com.purplepip.odin.bag.Thing;

/**
 * A conductor is responsible for conducting a collection of sequences.
 */
public interface Conductor extends Thing {
  /**
   * Is the conductor actively conducting sequences that it conducts.  If a given track
   * has no active conductors conducting it then the track will not fire any events.
   *
   * @return whether the conductor is active
   */
  boolean getActive();
}
