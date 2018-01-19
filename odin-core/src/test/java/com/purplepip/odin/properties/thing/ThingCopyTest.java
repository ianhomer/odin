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

package com.purplepip.odin.properties.thing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;
import com.purplepip.logcapture.LogCaptor;
import com.purplepip.logcapture.LogCapture;
import com.purplepip.odin.bag.Thing;
import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.creation.layer.DefaultLayer;
import com.purplepip.odin.creation.sequence.GenericSequence;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.demo.GroovePerformance;
import com.purplepip.odin.demo.KotlinPerformance;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.music.sequence.Notation;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class ThingCopyTest {
  @Test
  public void copy() throws Exception {
    Thing source = new DefaultLayer("test")
        .layer("layer1", "layer2").length(1).offset(8).enabled(false);
    Thing destination = new DefaultLayer(source.getId());
    new ThingCopy().source(source).destination(destination).copy();
    assertEquals(source, destination);
  }

  @Test
  public void testCopyFromGenericToGeneric() {
    GenericSequence destination = new GenericSequence().name("test");
    assertFalse(destination.arePropertiesDeclared());
    GenericSequence source = new GenericSequence().offset(8)
        .property("undeclared", "value1")
        .property("undeclared.nested", "value2");
    try (LogCaptor captor = new LogCapture().warn().from(ThingCopy.class)
        .withPassThrough().start()) {
      new ThingCopy().source(source).destination(destination).copy();
      assertEquals("No warnings should be logged", 0, captor.size());
    }

    /*
     * For generic to generic properties are copied across as is without warnings since we have
     * no knowledge of what declared properties to expect.
     */
    assertEquals("value1", destination.getProperty("undeclared"));
    assertEquals("value2", destination.getProperty("undeclared.nested"));
    assertEquals(Wholes.valueOf(8), destination.getOffset());
  }

  @Test
  public void testCopyFromGenericToSpecific() {
    GenericSequence source = new GenericSequence().offset(8)
        .property("notation", "A B C");
    Notation destination = (Notation) new Notation().name("test");
    assertTrue(destination.arePropertiesDeclared());
    try (LogCaptor captor = new LogCapture().warn().from(ThingCopy.class)
        .withPassThrough().start()) {
      new ThingCopy().source(source).destination(destination).copy();
      assertEquals("No warnings should be logged", 0, captor.size());
    }

    assertEquals("A B C", destination.getNotation());
    /*
     * Property map should not be populated in specific plugins.
     */
    assertNull(destination.getProperty("notation"));
    assertEquals(Wholes.valueOf(8), destination.getOffset());
  }

  @Test
  public void testCopyFromSpecificToGeneric() {
    GenericSequence destination = new GenericSequence();
    Notation source = (Notation) new Notation().notation("A B C")
        .offset(8).name("test");
    new ThingCopy().source(source).destination(destination).copy();
    assertEquals(Wholes.valueOf(8), destination.getOffset());
    assertEquals("A B C", destination.getProperty("notation"));
    assertEquals("test", destination.getName());
  }

  @Test
  public void testCopyFromPluginToPlugin() {
    GenericSequence source = new Notation().notation("A B C").offset(8)
        .tick(Ticks.HALF);
    Notation destination = (Notation) new Notation().name("test").tick(Ticks.BEAT);
    new ThingCopy().source(source).destination(destination).copy();

    assertEquals(Ticks.HALF, destination.getTick());
    assertEquals("A B C", destination.getNotation());
    assertEquals(Wholes.valueOf(8), destination.getOffset());
  }

  @Test
  public void testCopyFromGenericToSpecificWithUndeclaredProperty() {
    GenericSequence destination = new Notation().name("test").tick(Ticks.BEAT);
    assertTrue(destination.arePropertiesDeclared());
    GenericSequence source = new GenericSequence()
        .tick(Ticks.HALF)
        .property("undeclared", "value1")
        .property("undeclared.nested", "value2");
    try (LogCaptor captor = new LogCapture().warn().from(ThingCopy.class).start()) {
      new ThingCopy().source(source).destination(destination).copy();
      /*
       * When we are copying to configuration where properties are declared, e.g. to a plugin
       * then we expect undeclared properties to be logged with a warning since it is an
       * indication of an invalid property in the properties map.
       */
      assertEquals("2 warnings should be logged", 2, captor.size());
    }
    assertEquals(Ticks.HALF, destination.getTick());
  }

  @Test
  /*
   * Test that all objects in a few performances copy from specific to generic back to specific
   * without corruption.
   */
  public void testFullCycle() {
    Lists.newArrayList(new GroovePerformance(), new KotlinPerformance()).forEach(performance ->
        performance.getSequences().forEach(source -> {
          GenericSequence destination = new GenericSequence(source.getId());
          new ThingCopy().source(source).destination(destination).copy();
          try {
            SequenceConfiguration copyOfCopy = source.getClass().newInstance();
            new ThingCopy().source(destination).destination(copyOfCopy).copy();
            assertEquals(source, copyOfCopy);
          } catch (InstantiationException | IllegalAccessException e) {
            LOG.error("Cannot create new instance", e);
          }
        })
    );
  }
}