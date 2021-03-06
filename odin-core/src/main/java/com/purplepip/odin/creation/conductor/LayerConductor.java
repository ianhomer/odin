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

import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.clock.tick.DefaultTickConverter;
import com.purplepip.odin.clock.tick.TickConverter;
import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.creation.layer.Layer;
import com.purplepip.odin.creation.plugin.PluggableAspect;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;
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
@ToString(exclude = {"clock", "parent", "children", "windows"})
public class LayerConductor implements Conductor, PluggableAspect<Layer> {
  private Layer layer;
  private final BeatClock clock;
  private TickConverter tickConverter;
  private Conductor parent;
  private String name;
  private boolean zeroOffset;
  private boolean endless;

  /*
   * Note that ordering of children is important since dictates the ordering of children in
   * a loop.
   */
  private final Map<String, Conductor> children = new LinkedHashMap<>();
  private Real loopLength = Wholes.ZERO;
  private final Map<String, Window> windows = new HashMap<>();

  public LayerConductor(Layer layer, BeatClock clock) {
    this.clock = clock;
    setConfiguration(layer);
  }

  @Override
  public long getId() {
    return layer.getId();
  }

  @Override
  @NotNull
  public String getName() {
    return name;
  }

  @Override
  public void initialise() {
    LOG.warn("Initialise on {} ignored", this);
  }

  @Override
  public Layer getConfiguration() {
    return layer;
  }

  /**
   * Set layer for the layer conductor.
   *
   * @param layer layer
   */
  @Override
  public void setConfiguration(Layer layer) {
    this.layer = layer;
    this.name = layer.getName();
    zeroOffset = layer.getOffset().isZero();
    endless = layer.getLength().isNegative();
    tickConverter = new DefaultTickConverter(clock,
        () -> Ticks.MICROSECOND, layer::getTick, layer::getOffset);
  }

  @Override
  public boolean isVoid() {
    return false;
  }

  private void setParent(Conductor parent) {
    this.parent = parent;
  }

  @Override
  public Conductor getParent() {
    return parent;
  }


  @Override
  public Rational getLength() {
    return layer.getLength();
  }

  @Override
  public Rational getOffset() {
    return layer.getOffset();
  }


  public boolean hasChild(Conductor conductor) {
    return children.containsValue(conductor);
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
        LOG.warn("Conductor {} already has a parent {} defined won't be overridden with {}",
            layerConductor.getName(), layerConductor.getParent().getName(), getName());
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
    Rational position = Wholes.ZERO;
    for (Conductor child : children.values()) {
      /*
       * Child length less than zero means it can always be active.
       */
      if (child.getLength().isPositive()) {
        Rational childWindowLength = child.getLength().plus(child.getOffset());
        Window window = new Window(position, childWindowLength);
        position = window.getEnd();
        windows.put(child.getName(), window);
      }
    }
    loopLength = position;
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
      return false;
    }
    if (zeroOffset && endless) {
      // No need to check position, since always active in this situation.
      return true;
    }
    Real position = getPosition(microseconds);
    return position.ge(getOffset())
        && (getLength().isNegative() || position.lt(getLength()));
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
      Real absolutePosition = tickConverter.convert(Wholes.mutableOf(microseconds));
      if (loopLength.isPositive()) {
        return absolutePosition.modulo(loopLength);
      }
      return absolutePosition;
    }
    return getParent().getPosition(name, microseconds);
  }

  @Override
  public Real getPosition(String name, long microseconds) {
    Real position = getPosition(microseconds);
    Window window = windows.get(name);
    if (window == null) {
      return position;
    } else {
      return position.minus(window.getStart()).modulo(loopLength);
    }
  }
}
