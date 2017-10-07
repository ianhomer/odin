package com.purplepip.odin.sequencer;

import static com.purplepip.odin.sequence.tick.Ticks.BEAT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.purplepip.logcapture.LogCaptor;
import com.purplepip.logcapture.LogCapture;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.layer.Layer;
import com.purplepip.odin.sequence.triggers.Action;
import com.purplepip.odin.sequence.triggers.NoteTrigger;
import java.util.HashSet;
import java.util.Optional;
import org.junit.Test;

/**
 * Project builder test.
 */
public class ProjectBuilderTest {
  @Test
  public void testAddChannel() {
    TransientProject project = new TransientProject();
    ProjectBuilder builder = new ProjectBuilder(new ProjectContainer(project));
    builder.withChannel(1).changeProgramTo("test");
    Channel channel = project.getChannels().iterator().next();
    assertEquals("Program name not correct", "test", channel.getProgramName());
    assertEquals("Channel number not correct", 1, channel.getNumber());
    assertEquals("Project not correct", project, channel.getProject());
  }

  @Test
  public void testAddLayer() {
    TransientProject project = new TransientProject();
    ProjectBuilder builder = new ProjectBuilder(new ProjectContainer(project));
    builder
        .addLayer("layer4").addLayer("layer3")
        .withLayers("layer3", "layer4").addLayer("layer2")
        .withLayers("layer2").addLayer("layer1")
        .withChannel(1).withLayers("layer1").addMetronome()
        .withChannel(2).withLayers("layer2", "layer3").addMetronome()
        .withChannel(3).addNotation(BEAT, "C");
    Sequence sequence1 = builder.getSequenceByOrder(0);
    assertNotNull(builder.getLayerByOrder(0));

    Optional<String> firstLayerName = sequence1.getLayers().stream().findFirst();
    assertTrue(firstLayerName.isPresent());

    Layer layer1 = project.getLayers()
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
        new HashSet<>(sequence1.getLayers()),
        containsInAnyOrder("layer1"));
    Sequence sequence2 = builder.getSequenceByOrder(1);
    assertThat("Sequence 2 : " + sequence2,
        new HashSet<>(sequence2.getLayers()),
        containsInAnyOrder("layer2", "layer3"));
    assertThat("Sequence 2 : " + sequence2,
        new HashSet<>(sequence2.getLayers()),
        not(containsInAnyOrder("layer1")));
    Sequence sequence3 = builder.getSequenceByOrder(2);
    assertThat("Sequence 2 : " + sequence2,
        new HashSet<>(sequence3.getLayers()),
        containsInAnyOrder("layer2", "layer3"));
  }

  @Test
  public void testAddNotation() {
    TransientProject project = new TransientProject();
    ProjectBuilder builder = new ProjectBuilder(new ProjectContainer(project));
    builder.withName("notes").addNotation(BEAT, "a");
    Notation notation = (Notation) builder.getSequenceByOrder(0);
    assertEquals("a", notation.getNotation());
    assertEquals("notes", notation.getName());
  }

  @Test
  public void testAddPattern() {
    TransientProject project = new TransientProject();
    ProjectBuilder builder = new ProjectBuilder(new ProjectContainer(project));
    builder.addPattern(BEAT, 1);
    Pattern pattern = (Pattern) builder.getSequenceByOrder(0);
    assertEquals(1, pattern.getBits());
    assertEquals(60, pattern.getNote().getNumber());
    assertEquals(true, pattern.isActive());
  }

  @Test
  public void testAddInactivePattern() {
    TransientProject project = new TransientProject();
    ProjectBuilder builder = new ProjectBuilder(new ProjectContainer(project));
    builder.withActive(false).addPattern(BEAT, 1);
    Pattern pattern = (Pattern) builder.getSequenceByOrder(0);
    assertEquals(false, pattern.isActive());
  }


  @Test
  public void testAddPatternUsingProperties() {
    TransientProject project = new TransientProject();
    ProjectBuilder builder = new ProjectBuilder(new ProjectContainer(project));
    builder.withProperty("bits", 7);
    builder.withProperty("note.number", "58");
    builder.addPattern(BEAT, 1);
    Pattern pattern = (Pattern) builder.getSequenceByOrder(0);
    assertEquals(7, pattern.getBits());
    assertEquals(58, pattern.getNote().getNumber());
  }

  @Test
  public void testWithLayerDuplicationWarning() {
    try (LogCaptor captor = new LogCapture().from(ProjectBuilder.class).warn().start()) {
      TransientProject project = new TransientProject();
      ProjectBuilder builder = new ProjectBuilder(new ProjectContainer(project));
      builder.withLayers("layer1", "layer2", "layer2", "layer3", "layer3", "layer3", "layer4");
      assertEquals(1, captor.size());
      assertEquals("Creating entity with layers [layer1, layer2, layer2, layer3, "
          + "layer3, layer3, layer4] that have duplicates [layer3, layer2]", captor.getMessage(0));
    }
  }

  @Test
  public void testAddTrigger() {
    TransientProject project = new TransientProject();
    ProjectBuilder builder = new ProjectBuilder(new ProjectContainer(project));
    builder.withName("trigger1").withNote(60).addNoteTrigger();
    NoteTrigger trigger = (NoteTrigger) builder.getTriggerByOrder(0);
    assertEquals("trigger1", trigger.getName());
    assertEquals(60, trigger.getNote());
    builder.withTrigger("trigger1", Action.ENABLE)
        .withActive(false).addPattern(BEAT, 1);
    Pattern pattern = (Pattern) builder.getSequenceByOrder(0);
    assertEquals(Action.ENABLE, pattern.getTriggers().get("trigger1"));
  }
}