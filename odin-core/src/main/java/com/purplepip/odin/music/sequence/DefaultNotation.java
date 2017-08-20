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

package com.purplepip.odin.music.sequence;

import com.purplepip.odin.sequence.DefaultSequence;

public class DefaultNotation extends DefaultSequence implements Notation {
  private String format;
  private String notation;

  public DefaultNotation() {
    super();
  }

  public DefaultNotation(long id) {
    super(id);
  }

  @Override
  public void setFormat(String format) {
    this.format = format;
  }

  @Override
  public String getFormat() {
    return format;
  }

  @Override
  public void setNotation(String notation) {
    this.notation = notation;
  }

  @Override
  public String getNotation() {
    return notation;
  }
}
