package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
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
}