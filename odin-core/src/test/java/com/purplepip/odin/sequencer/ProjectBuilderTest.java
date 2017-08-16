package com.purplepip.odin.sequencer;

import static com.purplepip.odin.sequence.tick.Ticks.BEAT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.layer.Layer;
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
        .addLayer("layer1").addLayer("layer2").addLayer("layer3")
        .withChannel(1).withLayers("layer1").addMetronome()
        .withChannel(2).withLayers("layer2", "layer3").addMetronome()
        .withChannel(3).addNotation(BEAT, "C");
    Sequence sequence1 = builder.getSequenceByOrder(0);
    assertNotNull(builder.getLayerByOrder(0));


    Optional<String> firstLayerName = sequence1.getLayers().stream().findFirst();
    assertTrue(firstLayerName.isPresent());
    Optional<Layer> firstLayer = project.getLayers()
        .stream().filter(l -> l.getName().equals(firstLayerName.get())).findFirst();
    assertTrue(firstLayer.isPresent());
    assertThat("Sequence 1", firstLayer.get().getTick(), equalTo(BEAT));
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
}