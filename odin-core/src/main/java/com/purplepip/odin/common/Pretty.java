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

import com.google.common.base.Strings;
import java.util.regex.Pattern;

/**
 * Utility class to make strings prettier.
 */
public class Pretty {
  private static final int MAX_REPLACE = 7;
  private static final Pattern[] replaceTrailingZeros = new Pattern[MAX_REPLACE + 1];

  static {
    for (int i = 0 ; i < MAX_REPLACE + 1; i++) {
      replaceTrailingZeros[i] = Pattern.compile("0{0," + i + "}$");
    }
  }

  public static final String replaceTrailingZeros(long i, int maxReplace) {
    return replaceTrailingZeros(String.valueOf(i), maxReplace);
  }

  /**
   * Replace trailing zeros.  In log output (especially test ones) we have large numbers
   * ending with quite a few zeros.  We will replace this so these zeros don't become noise.
   *
   * @param s String to replace
   * @return replaced string
   */
  public static final String replaceTrailingZeros(String s, int maxReplace) {
    if (maxReplace > MAX_REPLACE) {
      throw new OdinRuntimeException("Max replace " + maxReplace + " must not be greater than "
          + MAX_REPLACE);
    } else if (maxReplace < 0) {
      throw new OdinRuntimeException("Max replace " + maxReplace + " must not be less than 0");
    }
    String truncated = replaceTrailingZeros[maxReplace].matcher(s).replaceFirst("");
    return Strings.padEnd(truncated, s.length(), '·');
  }
}
