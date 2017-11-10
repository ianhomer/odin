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

import com.purplepip.odin.bag.MutableThings;
import com.purplepip.odin.creation.layer.Layer;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(callSuper = true)
public class MutableConductors extends MutableThings<Conductor>  {
  /**
   * Refresh the bag of conductors.
   *
   * @param layerStream layer stream to use to do the refresh
   * @param conductorSupplier supplier of new conductors
   */
  public void refresh(Stream<Layer> layerStream,
                      Supplier<LayerConductor> conductorSupplier) {
    Set<Long> ids = getIds();
    layerStream.forEach(layer -> {
      ids.remove(layer.getId());

      /*
       * Add conductor if not present in conductors.
       */
      Optional<LayerConductor> existing =
          stream()
              .filter(o -> o instanceof LayerConductor).map(o -> (LayerConductor) o)
              .filter(conductor -> conductor.getId() == layer.getId()).findFirst();

      LayerConductor conductor;
      if (existing.isPresent()) {
        if (existing.get().getLayer().equals(layer)) {
          LOG.debug("Layer {} already added and unchanged", layer);
        } else {
          LOG.debug("Updating conductor for {}", layer);
          incrementUpdatedCount();
          conductor = existing.get();
          conductor.setLayer(layer);
        }
      } else {
        LOG.debug("Creating new conductor for {}", layer);
        conductor = conductorSupplier.get();
        conductor.setLayer(layer);
        add(conductor);
      }
    });

    /*
     * Remove if not found.
     */
    removeIf(thing -> ids.contains(thing.getKey()));


    /*
     * Wire up parent and children.
     */

    stream().forEach(conductor -> {
      if (conductor instanceof LayerConductor) {
        LayerConductor layerConductor = (LayerConductor) conductor;
        layerConductor.getLayer().getLayers().forEach(layerName -> {
          Conductor child = findByName(layerName);
          if (child == null) {
            LOG.warn("Cannot find conductor named {} to wire into children of {}", layerName, this);
          } else {
            LOG.debug("Adding child layer {} to parent {}",
                child.getName(), layerConductor.getName());
            layerConductor.addChild(child);
          }
        });
        layerConductor.afterChildrenAdded();
      }
    });
  }
}
