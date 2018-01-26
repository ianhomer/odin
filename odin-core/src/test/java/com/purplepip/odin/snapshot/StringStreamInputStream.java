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
import java.util.Spliterator;
import java.util.stream.Stream;

/**
 * Wrap a stream of strings into an input stream.
 */
public class StringStreamInputStream extends InputStream {
  private Spliterator<String> spliterator;
  private InputStream buffer;

  StringStreamInputStream(Stream<String> stream) {
    spliterator = stream.spliterator();
  }

  @Override
  public int read() throws IOException {
    if (buffer == null) {
      /*
       * Read next line.
       */
      // TODO : Consider character sets
      if (!spliterator
          .tryAdvance(string -> buffer = new ByteArrayInputStream((string + "\n").getBytes()))) {
        /*
         * Return -1 if we'ver read all the lines
         */
        return -1;
      }
    }
    int result = buffer.read();
    if (result == -1) {
      buffer = null;
      return read();
    }
    return result;
  }
}
