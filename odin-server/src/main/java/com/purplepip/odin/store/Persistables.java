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

package com.purplepip.odin.store;

import com.purplepip.odin.store.domain.PersistableThing;

/**
 * Persistable utility functions.
 */
public final class Persistables {
  /**
   * Sometimes toSting() method on an entity is not safe, since the entity might have lazy
   * collections that have not been initialised.  An example of this situation is trying to
   * log the entity toString() after it has been deleted from the store.
   *
   * @param object object to output as string.
   * @return string representation
   */
  public static String toThingString(Object object) {
    String string;
    if (object instanceof PersistableThing) {
      /*
       * Just output the thing string which know will be safe since it does not have any
       * lazy properties.
       */
      string = ((PersistableThing) object).toThingString();
    } else {
      /*
       * If it's not a persistable thing, just call toString as normal.
       */
      string = object.toString();
    }
    return string;
  }

  private Persistables() {
  }
}
