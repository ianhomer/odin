package com.purplepip.odin.project;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.purplepip.odin.sequencer.ProjectBuilder;
import org.junit.Test;

/**
 * Project container test.
 */
public class ProjectContainerTest {
  @Test
  public void testSaveListener() {
    ProjectSaveListener listener = mock(ProjectSaveListener.class);
    ProjectContainer container = new ProjectContainer();
    Project project = new TransientProject();
    container.setProject(project);
    container.addSaveListener(listener);
    container.save();
    container.save();
    verify(listener, times(2)).onProjectSave(project);
    container.removeSaveListener(listener);
    container.save();
    verify(listener, times(2)).onProjectSave(project);
  }

  @Test
  public void testApplyListener() {
    ProjectApplyListener listener = mock(ProjectApplyListener.class);
    ProjectContainer container = new ProjectContainer();
    Project project = new TransientProject();
    container.setProject(project);
    container.addApplyListener(listener);
    container.apply();
    container.apply();
    verify(listener, times(2)).onProjectApply(project);
    container.removeApplyListener(listener);
    container.apply();
    verify(listener, times(2)).onProjectApply(project);
  }

  @Test
  public void testLoadListener() {
    ProjectLoadListener listener = mock(ProjectLoadListener.class);
    ProjectContainer container = new ProjectContainer();
    Project project = new TransientProject();
    container.setProject(project);
    container.addLoadListener(listener);
    container.load();
    container.load();
    verify(listener, times(2)).onProjectLoad(container);
    container.removeLoadListener(listener);
    container.load();
    verify(listener, times(2)).onProjectLoad(container);
  }

  @Test
  public void testIsEmpty() {
    ProjectContainer container = new ProjectContainer();
    container.setProject(new TransientProject());
    assertTrue("Project should be empty", container.isEmpty());
    ProjectBuilder builder = new ProjectBuilder(container);
    builder.addMetronome();
    assertFalse("Project should NOT be empty", container.isEmpty());
  }
}
