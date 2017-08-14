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

import com.purplepip.odin.sequence.layer.Layer;

public class DefaultConductor implements Conductor {
  private Layer layer;

  public DefaultConductor(Layer layer) {
    this.layer = layer;
  }

  @Override
  public long getId() {
    return layer.getId();
  }

  public Layer getLayer() {
    return layer;
  }

  @Override
  public boolean getActive() {
    return true;
  }

}
