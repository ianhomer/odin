package com.purplepip.odin.sequencer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.layer.Layer;
import com.purplepip.odin.sequence.tick.Ticks;
import java.util.stream.Collectors;
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
        .withChannel(2).withLayers("layer2", "layer3").addMetronome();
    Sequence sequence1 = builder.getSequenceByOrder(0);
    Sequence sequence2 = builder.getSequenceByOrder(1);

    assertThat("Sequence 1 : " + sequence1,sequence1.getLayers().stream().findFirst()
        .orElse(null).getTick(), equalTo(Ticks.BEAT));
    assertThat("Sequence 1 : " + sequence1,
        sequence1.getLayers().stream().map(Layer::getName).collect(Collectors.toSet()),
        containsInAnyOrder("layer1"));
    assertThat("Sequence 2 : " + sequence2,
        sequence2.getLayers().stream().map(Layer::getName).collect(Collectors.toSet()),
        containsInAnyOrder("layer2", "layer3"));
    assertThat("Sequence 2 : " + sequence2,
        sequence2.getLayers().stream().map(Layer::getName).collect(Collectors.toSet()),
        not(containsInAnyOrder("layer1")));
  }

  @Test
  public void testAddNotation() {
    TransientProject project = new TransientProject();
    ProjectBuilder builder = new ProjectBuilder(new ProjectContainer(project));
    builder.addNotation(Ticks.BEAT, "a");
    Notation notation = (Notation) builder.getSequenceByOrder(0);
    assertEquals("a", notation.getNotation());
  }
}