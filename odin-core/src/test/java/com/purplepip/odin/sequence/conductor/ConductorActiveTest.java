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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.BeatClock;
import com.purplepip.odin.sequence.MicrosecondPositionProvider;
import com.purplepip.odin.sequence.MovableMicrosecondPositionProvider;
import com.purplepip.odin.sequence.StaticBeatsPerMinute;
import com.purplepip.odin.sequencer.ProjectBuilder;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class ConductorActiveTest {
  private static final long ZERO = 0;
  private static final long HALF_SECOND = 500_000;
  private static final long ONE_SECOND = 1_000_000;
  private static final long TWO_SECONDS = 1_000_000;
  private static final long FOUR_SECONDS = 4_000_000;
  private static final long SIX_SECONDS = 6_000_000;
  private static final long EIGHT_SECONDS = 8_000_000;
  private static final long TEN_SECONDS = 10_000_000;
  private static final long TWELVE_SECONDS = 12_000_000;

  private static final String LAYER1 = "layer1";
  private static final String LAYER2 = "layer2";
  private static final String LAYER3 = "layer3";
  private static final String LAYER4 = "layer4";
  private static final String LAYER5 = "layer5";
  private static final String LAYER6 = "layer6";
  private static final String LAYER7 = "layer7";
  private static final String LAYER8 = "layer8";
  private static final String LAYER9 = "layer9";

  /*
   * BEFORE is non-inclusive of boundary (since length 4 beats should be 0,1,2,3)
   * AFTER is inclusive of boundary (since offset of 4 should be 4,5,6)
   */
  private static final Long[] ALWAYS = { ZERO, ONE_SECOND, EIGHT_SECONDS };
  private static final Long[] BEFORE_ONE_SECOND = { ZERO, HALF_SECOND };
  private static final Long[] AFTER_ONE_SECOND = { ONE_SECOND, FOUR_SECONDS, EIGHT_SECONDS };
  private static final Long[] BEFORE_FOUR_SECONDS = { ZERO, HALF_SECOND, ONE_SECOND };
  private static final Long[] AFTER_FOUR_SECONDS = { FOUR_SECONDS, EIGHT_SECONDS };

  private BeatClock clock;
  private MicrosecondPositionProvider microsecondPositionProvider;
  private MutableConductors conductors;
  private TransientProject project;
  private ProjectBuilder builder;


  @Before
  public void setUp() {
    microsecondPositionProvider = new MovableMicrosecondPositionProvider();
    clock = new BeatClock(new StaticBeatsPerMinute(60), microsecondPositionProvider);
    conductors = new MutableConductors();
    project = new TransientProject();
    builder = new ProjectBuilder(new ProjectContainer(project));
  }

  @Test
  public void testSingleLayer() {
    if (refresh(() -> builder.addLayer(LAYER1))) assertActive(LAYER1, ALWAYS);
    if (refresh(() -> builder.withLength(1).addLayer(LAYER1))) {
      assertActive(LAYER1, BEFORE_ONE_SECOND);
      assertNotActive(LAYER1, AFTER_ONE_SECOND);
    }
    if (refresh(() -> builder.withLength(4).addLayer(LAYER1))) {
      assertActive(LAYER1, BEFORE_FOUR_SECONDS);
      assertNotActive(LAYER1, AFTER_FOUR_SECONDS);
    }
    if (refresh(() -> builder.withOffset(1).withLength(3).addLayer(LAYER1))) {
      assertNotActive(LAYER1, BEFORE_ONE_SECOND);
      assertActive(LAYER1, ONE_SECOND, TWO_SECONDS);
      assertNotActive(LAYER1, AFTER_FOUR_SECONDS);
    }
  }

  @Test
  public void testMultipleLayers() {
    if (refresh(() -> builder.withOffset(1).withLength(3).addLayer(LAYER1, LAYER2))) {
      assertNotActive(LAYER1, BEFORE_ONE_SECOND);
      assertActive(LAYER1, ONE_SECOND, TWO_SECONDS);
      assertNotActive(LAYER1, AFTER_FOUR_SECONDS);
      assertNotActive(LAYER2, BEFORE_ONE_SECOND);
      assertActive(LAYER2, ONE_SECOND, TWO_SECONDS);
      assertNotActive(LAYER2, AFTER_FOUR_SECONDS);
    }
  }

  /*
   * LAYER1 (top)
   *    LAYER2 (4 beats) : LAYER3 (4 beats)
   *
   * Should lead to top layer always on with looping between 4 beats of layer1 and then
   * 4 beats of layer2.
   */
  @Test
  public void testChildLayers() {
    if (refresh(() -> builder
        .withOffset(0).withLength(4).addLayer(LAYER2)
        .withOffset(0).withLength(4).addLayer(LAYER3)
        .withOffset(0).withLength(-1).withLayers(LAYER2, LAYER3).addLayer(LAYER1))) {
      assertActive(LAYER1, ALWAYS);
      assertActive(LAYER2, ZERO, TWO_SECONDS, EIGHT_SECONDS, TEN_SECONDS);
      assertNotActive(LAYER2, FOUR_SECONDS, SIX_SECONDS, TWELVE_SECONDS);
      assertActive(LAYER3, FOUR_SECONDS, SIX_SECONDS, TWELVE_SECONDS);
      assertNotActive(LAYER3, ZERO, TWO_SECONDS, EIGHT_SECONDS, TEN_SECONDS);
    }
  }

  /*
   * LAYER1 (top)
   *    LAYER2 (16)                          : LAYER3 (16)
   *    LAYER4 (4)            : LAYER5 (4)     LAYER6 (4) LAYER7 (4)
   *    LAYER8 (2) LAYER9 (2)
   * Should lead to top layer always on with looping between 4 beats of layer1 and then
   * 4 beats of layer2.
   */
  @Test
  public void testDeepChildLayers() {
    if (refresh(() -> builder
        .withOffset(0).withLength(2).addLayer(LAYER8)
        .withOffset(0).withLength(2).addLayer(LAYER9)
        .withOffset(0).withLength(4).withLayers(LAYER8, LAYER9).addLayer(LAYER4)
        .withOffset(0).withLength(4).withLayers().addLayer(LAYER5)
        .withOffset(0).withLength(4).withLayers().addLayer(LAYER6)
        .withOffset(0).withLength(4).withLayers().addLayer(LAYER7)
        .withOffset(0).withLength(16).withLayers(LAYER4, LAYER5).addLayer(LAYER2)
        .withOffset(0).withLength(16).withLayers(LAYER6, LAYER7).addLayer(LAYER3)
        .withOffset(0).withLength(-1).withLayers(LAYER2, LAYER3).addLayer(LAYER1))) {
      assertOnlyActive(ZERO, LAYER1, LAYER2, LAYER4, LAYER8);
      assertOnlyActive(TWO_SECONDS, LAYER1, LAYER2, LAYER4, LAYER8);
      assertOnlyActive(SIX_SECONDS, LAYER1, LAYER2, LAYER5);
      assertOnlyActive(TEN_SECONDS, LAYER1, LAYER2, LAYER4, LAYER9);
    }
  }

  private boolean refresh(Runnable logic) {
    project.clear();
    builder.reset();
    logic.run();
    apply();
    return true;
  }

  private boolean apply() {
    conductors.refresh(() -> project.getLayers().stream(), this::createConductor);
    return true;
  }

  private void assertActive(String layerName, Long... positions) {
    Conductor conductor = conductors.findByName(layerName);
    Arrays.asList(positions).forEach(position -> assertTrue(conductor.isActive(position)));
  }

  private void assertOnlyActive(long position, String... layerNames) {
    List<String> names = Arrays.asList(layerNames);
    project.getLayers().forEach(layer -> {
      Conductor conductor = conductors.findByName(layer.getName());
      if (names.contains(layer.getName())) {
        assertTrue(getReason(true, layer.getName(), position), conductor.isActive(position));
      } else {
        assertFalse(getReason(true, layer.getName(), position), conductor.isActive(position));
      }
    });
  }

  private String getReason(boolean expect, String name, long position) {
    return "Expected " + expect + " for " + name + " at " + position;
  }

  private void assertNotActive(String layerName, Long... positions) {
    Conductor conductor = conductors.findByName(layerName);
    Arrays.asList(positions).forEach(position -> assertFalse(conductor.isActive(position)));
  }

  private LayerConductor createConductor() {
    return new LayerConductor(clock);
  }
}
