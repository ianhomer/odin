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

package com.purplepip.odin.creation.conductor;

import com.purplepip.odin.creation.layer.Layer;
import com.purplepip.odin.creation.plugin.AbstractPluggableAspects;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(callSuper = true)
public class LayerConductors extends AbstractPluggableAspects<LayerConductor, Layer> {
  @Override
  protected void afterRefresh() {
    /*
     * Wire up parent and children.
     */

    stream().forEach(conductor -> {
      conductor.getConfiguration().getLayers().forEach(layerName -> {
        Conductor child = findByName(layerName);
        if (child == null) {
          LOG.warn("Cannot find conductor named {} to wire into children of {}", layerName, this);
        } else {
          LOG.debug("Adding child layer {} to parent {}",
              child.getName(), conductor.getName());
          conductor.addChild(child);
        }
      });
      conductor.afterChildrenAdded();
    });
  }
}
