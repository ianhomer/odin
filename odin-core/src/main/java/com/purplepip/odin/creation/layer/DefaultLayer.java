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

package com.purplepip.odin.creation.layer;

import com.purplepip.odin.clock.tick.AbstractTimeThing;
import com.purplepip.odin.clock.tick.MutableTimeThing;
import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.math.Rational;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;

/**
 * Default layer implementation.
 */
@EqualsAndHashCode(callSuper = true)
public class DefaultLayer extends AbstractTimeThing implements MutableLayer {
  private List<String> layers = new ArrayList<>();

  /**
   * Create empty un-named default layer.
   */
  public DefaultLayer() {
    /*
     * Nothing needs to be done for an empty layer.
     */
  }

  public DefaultLayer(long id) {
    super(id);
  }

  public DefaultLayer(String name) {
    setName(name);
  }

  @Override
  public List<String> getLayers() {
    return layers;
  }

  @Override
  public void addLayer(String layerName) {
    layers.add(layerName);
  }

  @Override
  public DefaultLayer name(String layerName) {
    setName(layerName);
    return this;
  }

  @Override
  public DefaultLayer tick(Tick tick) {
    super.tick(tick);
    return this;
  }

  @Override
  public DefaultLayer offset(long offset) {
    super.offset(offset);
    return this;
  }

  @Override
  public DefaultLayer offset(Rational offset) {
    super.offset(offset);
    return this;
  }

  @Override
  public String toString() {
    return super.toString();
  }

  @Override
  public DefaultLayer length(long length) {
    super.length(length);
    return this;
  }

  @Override
  public DefaultLayer length(Rational length) {
    super.length(length);
    return this;
  }

  /**
   * Set layer names.
   *
   * @param layers layer names
   * @return this
   */
  public DefaultLayer layer(String... layers) {
    for (String layer : layers) {
      addLayer(layer);
    }
    return this;
  }

  @Override
  public DefaultLayer copy() {
    DefaultLayer layer = new DefaultLayer();
    layer.layers.addAll(this.layers);
    super.copy((MutableTimeThing) layer);
    return layer;
  }
}
