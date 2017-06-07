package com.purplepip.odin.project;

import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequencer.Channel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A light weight non-persistable in memory project implementation.
 */
public class TransientProject extends AbstractProject {
  private static final Logger LOG = LoggerFactory.getLogger(TransientProject.class);

  private Set<Sequence> sequences = new HashSet<>();

  private Set<Channel> channels = new HashSet<>();

  @Override
  public void addSequence(Sequence sequence) {
    LOG.debug("Adding sequence {} with time units {}",
        sequence.getClass().getSimpleName(),
        sequence.getTick().getClass().getSimpleName());
    sequences.add(sequence);
  }

  @Override
  public Iterable<Sequence> getSequences() {
    return Collections.unmodifiableSet(sequences);
  }

  @Override
  public void addChannel(Channel channel) {
    /*
     * For now we replace channel with same number, in future we might merge.
     */
    Optional<Channel> duplicate = channels.stream()
        .filter(o -> o.getNumber() == channel.getNumber())
        .findFirst();
    duplicate.ifPresent(channelToRemove -> channels.remove(channelToRemove));
    channels.add(channel);
  }

  @Override
  public Iterable<Channel> getChannels() {
    return Collections.unmodifiableSet(channels);
  }
}
