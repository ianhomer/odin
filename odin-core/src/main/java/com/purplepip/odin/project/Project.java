package com.purplepip.odin.project;

import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequencer.Channel;
import java.util.Set;

/**
 * An Odin Project that stores configuration and state of the given runtime.
 */
public interface Project {
  String getName();

  /**
   * Get iterable of the sequences stored in this project.
   *
   * @return iterable of sequences
   */
  Set<Sequence> getSequences();

  /**
   * Get iterable of the channels stored in this project.
   *
   * @return iterable of channels
   */
  Set<Channel> getChannels();
}
