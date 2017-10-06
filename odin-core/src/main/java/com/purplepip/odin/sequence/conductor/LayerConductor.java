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

package com.purplepip.odin.sequence.conductor;

import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.sequence.BeatClock;
import com.purplepip.odin.sequence.DefaultTickConverter;
import com.purplepip.odin.sequence.TickConverter;
import com.purplepip.odin.sequence.layer.Layer;
import com.purplepip.odin.sequence.tick.Ticks;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Conductor based on a layer.
 */
/*
 * TODO : Note this logic is based on layer tick unit all being the same, namely a beat.  It
 * would be good to relax this constraint one day.
 */
@Slf4j
@ToString(exclude = {"clock"})
public class LayerConductor implements Conductor {
  private Layer layer;
  private BeatClock clock;
  private TickConverter tickConverter;
  private Conductor parent;
  /*
   * Note that ordering of children is important since dictates the ordering of children in
   * a loop.
   */
  private Map<String, Conductor> children = new LinkedHashMap<>();
  private Real loopLength = Wholes.ZERO;
  private Map<String, Window> windows = new HashMap<>();

  public LayerConductor(BeatClock clock) {
    this.clock = clock;
  }

  @Override
  public long getId() {
    return layer.getId();
  }

  @Override
  @NotNull
  public String getName() {
    return layer.getName();
  }

  public Layer getLayer() {
    return layer;
  }

  /**
   * Set layer for the layer conductor.
   *
   * @param layer layer
   */
  public void setLayer(Layer layer) {
    this.layer = layer;
    tickConverter = new DefaultTickConverter(clock,
        () -> Ticks.MICROSECOND, layer::getTick, layer::getOffset);
  }

  public void setParent(Conductor parent) {
    this.parent = parent;
  }

  @Override
  public Conductor getParent() {
    return parent;
  }


  @Override
  public long getLength() {
    return layer.getLength();
  }

  @Override
  public long getOffset() {
    return layer.getOffset();
  }


  /**
   * Add child conductor.
   *
   * @param conductor child conductor to add
   */
  public void addChild(Conductor conductor) {
    children.put(conductor.getName(), conductor);
    if (conductor instanceof LayerConductor) {
      LayerConductor layerConductor = (LayerConductor) conductor;
      if (layerConductor.getParent() != null) {
        LOG.warn("Conductor already has a parent defined won't be overridden");
      } else {
        layerConductor.setParent(this);
      }
    }
  }

  @Override
  public Stream<Conductor> getChildren() {
    return children.values().stream();
  }

  /**
   * Find child conductor by name.
   *
   * @param name conductor name
   * @return child conductor
   */
  @Override
  public Conductor findByName(String name) {
    return children.get(name);
  }

  void afterChildrenAdded() {
    /*
     * Calculate windows of activity for each conductor.  Note that the individual conductor might
     * have an offset which will be added to this start point to give the point when the conductor
     * becomes active.
     */
    long position = 0;
    for (Conductor child : children.values()) {
      /*
       * Child length less than zero means it can always be active.
       */
      if (child.getLength() > 0) {
        long childWindowLength = child.getOffset() + child.getLength();
        Window window = new Window(position, childWindowLength);
        position = window.getEnd();
        windows.put(child.getName(), window);
      }
    }
    loopLength = Whole.valueOf(position);
  }

  /**
   * Whether the conductor is active.
   *
   * @param microseconds microsecond position to test whether conductor is active
   * @return true if conductor is active
   */
  @Override
  public boolean isActive(long microseconds) {
    if (getParent() != null && !getParent().isActive(microseconds)) {
      LOG.debug("parent {} of {} is not active : {}", getParent().getName(),
          getName(), microseconds);
      return false;
    }
    long position = getPosition(microseconds).floor();
    boolean result = position >= getOffset()
        && (getLength() <= 0 || position < getLength());
    if (LOG.isDebugEnabled()) {
      LOG.debug("isActive {} : {}, tock {}, µs {}, length {}, loop {}",
          getName(), result, position, microseconds, getLength(), loopLength);
    }
    return result;
  }

  /**
   * Get tock position, relative to start of conductor, for the given microsecond position.
   *
   * @param microseconds microsecond position
   * @return tock position
   */
  @Override
  public Real getPosition(long microseconds) {
    if (parent == null) {
      Real absolutePosition = tickConverter.convert(Whole.valueOf(microseconds));
      if (loopLength.isPositive()) {
        return absolutePosition.modulo(loopLength);
      }
      return absolutePosition;
    }
    return getParent().getPosition(this.getName(), microseconds);
  }

  @Override
  public Real getPosition(String name, long microseconds) {
    Real position = getPosition(microseconds);
    Window window = windows.get(name);
    if (window == null) {
      return position;
    } else {
      return position.minus(Whole.valueOf(window.getStart())).modulo(loopLength);
    }
  }
}
