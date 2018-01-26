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

import com.purplepip.odin.common.OdinRuntimeException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class MaskedFileInputStream extends MaskedInputStream {
  private BufferedReader reader;
  private Path path;

  /**
   * Create new masked file input stream.
   *
   * @param path path of the file
   * @param masks map of masks, key is pattern and value is what to replace matches with
   */
  public MaskedFileInputStream(Path path, Map<String, String> masks) {
    super(masks);
    this.path = path;
    try {
      reader = new BufferedReader(new FileReader(path.toFile()));
    } catch (FileNotFoundException e) {
      throw new OdinRuntimeException("Cannot find file " + path, e);
    }
  }

  @Override
  protected String readLine() {
    try {
      return reader.readLine();
    } catch (IOException e) {
      throw new OdinRuntimeException("Cannot read line from " + path, e);
    }
  }
}
