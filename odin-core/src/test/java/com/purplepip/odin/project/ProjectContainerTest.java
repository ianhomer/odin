package com.purplepip.odin.project;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

/**
 * Project container test.
 */
public class ProjectContainerTest {
  @Test
  public void testListener() {
    ProjectListener listener = mock(ProjectListener.class);
    ProjectContainer container = new ProjectContainer();
    container.setProject(new TransientProject());
    container.addListener(listener);
    container.apply();
    container.apply();
    verify(listener, times(2)).onProjectApply();
    container.removeListener(listener);
    verify(listener, times(2)).onProjectApply();
  }
}
