package com.purplepip.odin.project;

import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequencer.Channel;
import java.util.HashSet;
import java.util.Set;

/**
 * A light weight non-persistable in memory project implementation.
 */
public class TransientProject implements Project {
  private static final String TRANSIENT_PROJECT_NAME = "transient";

  private Set<Sequence> sequences = new HashSet<>();

  private Set<Channel> channels = new HashSet<>();

  @Override
  public String getName() {
    return TRANSIENT_PROJECT_NAME;
  }

  @Override
  public Set<Sequence> getSequences() {
    return sequences;
  }

  @Override
  public Set<Channel> getChannels() {
    return channels;
  }
}
