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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class MaskedInputStream extends InputStream {
  private InputStream buffer;
  private Map<Pattern, String> maskPatterns = new HashMap<>();
  private boolean readSomething = false;

  protected abstract String readLine();

  public MaskedInputStream(Map<String, String> masks) {
    masks.forEach((k,v) -> maskPatterns.put(java.util.regex.Pattern.compile(k), v));
  }

  @Override
  public int read() throws IOException {
    if (buffer == null) {
      /*
       * Read next line.
       */
      String line = readLine();
      if (line == null) {
        /*
         * Return -1 if we'ver read all the lines
         */
        return -1;
      }
      /*
       * Only include line breaks between lines, not at the end of the stream.
       */
      if (!readSomething) {
        readSomething = true;
      } else {
        line = '\n' + line;
      }
      for (Map.Entry<Pattern, String> entry : maskPatterns.entrySet()) {
        line = entry.getKey().matcher(line).replaceAll(entry.getValue());
      }
      buffer = new ByteArrayInputStream((line).getBytes());
    }
    int result = buffer.read();
    if (result == -1) {
      buffer = null;
      return read();
    }
    return result;
  }
}
