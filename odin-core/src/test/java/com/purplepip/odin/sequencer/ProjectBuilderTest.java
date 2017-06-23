package com.purplepip.odin.sequencer;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.Layer;
import com.purplepip.odin.sequence.Sequence;
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

    assertThat("Sequence 1 : " + sequence1,
        sequence1.getLayers().stream().map(Layer::getName).collect(Collectors.toList()),
        contains("layer1"));
    assertThat("Sequence 2 : " + sequence2,
        sequence2.getLayers().stream().map(Layer::getName).collect(Collectors.toList()),
        contains("layer2", "layer3"));
    assertThat("Sequence 2 : " + sequence2,
        sequence2.getLayers().stream().map(Layer::getName).collect(Collectors.toList()),
        not(contains("layer1")));
  }

}