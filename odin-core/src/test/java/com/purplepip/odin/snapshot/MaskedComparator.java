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

package com.purplepip.odin.snapshot;

import java.util.Comparator;
import java.util.regex.Pattern;

public class MaskedComparator implements Comparator<String> {
  private String replacement;
  private Pattern pattern;

  public MaskedComparator(String mask, String replacement) {
    this.replacement = replacement;
    this.pattern = Pattern.compile(mask);
  }

  @Override
  public int compare(String o1, String o2) {
    if (o1 != null && o2 != null) {
      return mask(o1).compareTo(mask(o2));
    }
    return 0;
  }

  private String mask(String s) {
    return pattern.matcher(s).replaceAll(replacement);
  }
}
