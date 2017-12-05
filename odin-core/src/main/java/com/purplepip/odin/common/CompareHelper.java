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

package com.purplepip.odin.common;

/**
 * Compare helper.
 */
public final class CompareHelper {
  /**
   * Compare two longs.
   *
   * @param x x value
   * @param y y value
   * @return compare result
   */
  public static final int compare(long x, long y) {
    if (x < y) {
      return -1;
    } else if (x > y) {
      return 1;
    }
    return 0;
  }

  /**
   * Compare two ints.
   *
   * @param x x value
   * @param y y value
   * @return compare result
   */
  public static final int compare(int x, int y) {
    if (x < y) {
      return -1;
    } else if (x > y) {
      return 1;
    }
    return 0;
  }
}
