package com.purplepip.odin.project;

import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequencer.Channel;

/**
 * An Odin Project that stores configuration and state of the given runtime.
 */
public interface Project {
  /**
   * Add a sequence to the project.
   *
   * @param sequence sequence
   */
  void addSequence(Sequence sequence);

  /**
   * Get iterable of the sequences stored in this project.
   *
   * @return iterable of sequences
   */
  Iterable<Sequence> getSequences();

  /**
   * Apply project configuration.
   */
  void apply();

  /**
   * Add project listener.
   *
   * @param projectListener project listener.
   */
  void addListener(ProjectListener projectListener);

  /**
   * Remove project listener.
   *
   * @param projectListener project listener
   */
  void removeListener(ProjectListener projectListener);

  /**
   * Add channel configuration.
   *
   * @param channel configuration to add
   */
  void addChannel(Channel channel);

  /**
   * Get iterable of the channels stored in this project.
   *
   * @return iterable of channels
   */
  Iterable<Channel> getChannels();

}
