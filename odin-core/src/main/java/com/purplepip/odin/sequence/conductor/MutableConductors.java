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

import com.purplepip.odin.bag.MutableThings;
import com.purplepip.odin.sequence.layer.Layer;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MutableConductors extends MutableThings<Conductor>  {
  /**
   * Refresh the bag of conductors.
   *
   * @param layerStream layer stream to use to do the refresh
   * @param conductorSupplier supplier of new conductors
   */
  public void refresh(Supplier<Stream<Layer>> layerStream,
                      Supplier<LayerConductor> conductorSupplier) {
    removeIf(conductor -> layerStream.get()
        .noneMatch(layer -> layer.getId() == conductor.getValue().getId()));

    layerStream.get().forEach(layer -> {
      /*
       * Add layer if not present in tracks.
       */
      Optional<LayerConductor> existing =
          stream()
              .filter(o -> o instanceof LayerConductor)
              .map(o -> (LayerConductor) o)
              .filter(conductor -> conductor.getId() == layer.getId()).findFirst();

      LayerConductor conductor = null;
      if (existing.isPresent()) {
        if (existing.get().getLayer().equals(layer)) {
          LOG.debug("Layer {} already added and unchanged", layer);
        } else {
          LOG.debug("Updating conductor for {}", layer);
          incrementUpdatedCount();
          conductor = existing.get();
        }
      } else {
        LOG.debug("Creating new conductor for {}", layer);
        conductor = conductorSupplier.get();
        add(conductor);
      }

      if (conductor != null) {
        /*
         * Update layer in new or modified conductor.
         */
        conductor.setLayer(layer);
      }
    });
  }
}
