package com.purplepip.odin.sequencer;

import static com.purplepip.odin.clock.tick.Ticks.BEAT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.purplepip.logcapture.LogCaptor;
import com.purplepip.logcapture.LogCapture;
import com.purplepip.odin.creation.channel.Channel;
import com.purplepip.odin.creation.layer.Layer;
import com.purplepip.odin.creation.sequence.Action;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.creation.triggers.NoteTrigger;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.performance.PerformanceContainer;
import com.purplepip.odin.performance.TransientPerformance;
import java.util.HashSet;
import java.util.Optional;
import org.junit.Test;

/**
 * Project builder test.
 */
public class PerformanceBuilderTest {
  @Test
  public void testAddChannel() {
    TransientPerformance project = new TransientPerformance();
    PerformanceBuilder builder = new PerformanceBuilder(new PerformanceContainer(project));
    builder.withChannel(1).changeProgramTo("test");
    Channel channel = project.getChannels().iterator().next();
    assertEquals("Program name not correct", "test", channel.getProgramName());
    assertEquals("Channel number not correct", 1, channel.getNumber());
  }

  @Test
  public void testAddLayer() {
    TransientPerformance performance = new TransientPerformance();
    PerformanceBuilder builder = new PerformanceBuilder(new PerformanceContainer(performance));
    builder
        .addLayer("layer4").addLayer("layer3")
        .withLayers("layer3", "layer4").addLayer("layer2")
        .withLayers("layer2").addLayer("layer1")
        .withChannel(1).withLayers("layer1").addMetronome()
        .withChannel(2).withLayers("layer2", "layer3").addMetronome()
        .withChannel(3).addNotation(BEAT, "C");
    assertEquals("3 sequences should have been created, however "
        + performance.getSequences().size() + " have", 3, performance.getSequences().size());
    SequenceConfiguration sequence1 = builder.getSequenceByOrder(0);
    assertNotNull(builder.getLayerByOrder(0));

    Optional<String> firstLayerName = sequence1.getLayers().stream().findFirst();
    assertTrue(firstLayerName.isPresent());

    Layer layer1 = performance.getLayers()
        .stream().filter(l -> l.getName().equals("layer1")).findFirst().orElse(null);
    assertThat(layer1, is(notNullValue()));
    assertThat(layer1.getName(), equalTo("layer1"));
    assertThat("Layer 1", layer1.getTick(), equalTo(BEAT));
    assertThat(layer1.getLayers(), containsInAnyOrder("layer2"));

    Layer layer2 = builder.getLayer("layer2");
    assertThat(layer2, is(notNullValue()));
    assertThat(layer2.getName(), equalTo("layer2"));
    assertThat("Layer 2", layer2.getTick(), equalTo(BEAT));
    assertThat(layer2.getLayers(), contains("layer3", "layer4"));


    assertThat("Sequence 1 : " + sequence1,
        new HashSet<>(sequence1.getLayers()), containsInAnyOrder("layer1"));
    SequenceConfiguration sequence2 = builder.getSequenceByOrder(1);
    assertThat("Sequence 2 : " + sequence2,
        new HashSet<>(sequence2.getLayers()), containsInAnyOrder("layer2", "layer3"));
    assertThat("Sequence 2 : " + sequence2,
        new HashSet<>(sequence2.getLayers()), not(containsInAnyOrder("layer1")));
    SequenceConfiguration sequence3 = builder.getSequenceByOrder(2);
    assertThat("Sequence 2 : " + sequence2,
        new HashSet<>(sequence3.getLayers()), containsInAnyOrder("layer2", "layer3"));
  }

  @Test
  public void testAddNotation() {
    TransientPerformance performance = new TransientPerformance();
    PerformanceBuilder builder = new PerformanceBuilder(new PerformanceContainer(performance));
    builder.withName("notes").addNotation(BEAT, "a");
    Notation notation = (Notation) builder.getSequenceByOrder(0);
    assertEquals("a", notation.getNotation());
    assertEquals("notes", notation.getName());
  }

  @Test
  public void testAddPattern() {
    TransientPerformance performance = new TransientPerformance();
    PerformanceBuilder builder = new PerformanceBuilder(new PerformanceContainer(performance));
    builder.addPattern(BEAT, 1);
    Pattern pattern = (Pattern) builder.getSequenceByOrder(0);
    assertEquals(1, pattern.getBits());
    assertEquals(60, pattern.getNote().getNumber());
    assertTrue(pattern.isEnabled());
  }

  @Test
  public void testAddDisabledPattern() {
    TransientPerformance performance = new TransientPerformance();
    PerformanceBuilder builder = new PerformanceBuilder(new PerformanceContainer(performance));
    builder.withEnabled(false).addPattern(BEAT, 1);
    Pattern pattern = (Pattern) builder.getSequenceByOrder(0);
    assertFalse(pattern.isEnabled());
  }

  @Test
  public void testAddEnabledPattern() {
    TransientPerformance project = new TransientPerformance();
    PerformanceBuilder builder = new PerformanceBuilder(new PerformanceContainer(project));
    builder.withEnabled(true).addPattern(BEAT, 1);
    Pattern pattern = (Pattern) builder.getSequenceByOrder(0);
    assertTrue(pattern.isEnabled());
  }

  @Test
  public void testAddPatternUsingProperties() {
    TransientPerformance project = new TransientPerformance();
    PerformanceBuilder builder = new PerformanceBuilder(new PerformanceContainer(project));
    builder.withProperty("bits", 7);
    builder.withProperty("note.number", "58");
    builder.addPattern(BEAT, 1);
    Pattern pattern = (Pattern) builder.getSequenceByOrder(0);
    assertEquals(7, pattern.getBits());
    assertEquals(58, pattern.getNote().getNumber());
  }

  @Test
  public void testWithLayerDuplicationWarning() {
    try (LogCaptor captor = new LogCapture().from(PerformanceBuilder.class).warn().start()) {
      TransientPerformance project = new TransientPerformance();
      PerformanceBuilder builder = new PerformanceBuilder(new PerformanceContainer(project));
      builder.withLayers("layer1", "layer2", "layer2", "layer3", "layer3", "layer3", "layer4");
      assertEquals(1, captor.size());
      assertEquals("Creating entity with layers [layer1, layer2, layer2, layer3, "
          + "layer3, layer3, layer4] that have duplicates [layer3, layer2]", captor.getMessage(0));
    }
  }

  @Test
  public void testAddTrigger() {
    TransientPerformance project = new TransientPerformance();
    PerformanceBuilder builder = new PerformanceBuilder(new PerformanceContainer(project));
    builder.withName("trigger1").withNote(60).addNoteTrigger();
    NoteTrigger trigger = (NoteTrigger) builder.getTriggerByOrder(0);
    assertEquals("trigger1", trigger.getName());
    assertEquals(60, trigger.getNote().getNumber());

    /*
     * Add pattern using this trigger.
     */
    builder.withTrigger("trigger1", Action.ENABLE)
        .withEnabled(false).addPattern(BEAT, 1);
    Pattern pattern = (Pattern) builder.getSequenceByOrder(0);
    assertEquals(Action.ENABLE, pattern.getTriggers().get("trigger1"));
  }
}