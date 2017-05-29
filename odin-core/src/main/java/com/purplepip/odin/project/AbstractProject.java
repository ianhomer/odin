package com.purplepip.odin.project;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract project.
 */
public abstract class AbstractProject implements Project {
  private List<ProjectListener> listeners = new ArrayList<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public void addListener(ProjectListener projectListener) {
    listeners.add(projectListener);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeListener(ProjectListener projectListener) {
    listeners.remove(projectListener);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void apply() {
    for (ProjectListener listener : listeners) {
      listener.onProjectApply();
    }
  }
}
