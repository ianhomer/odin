package com.purplepip.odin.sequencer;

/**
 * Channel based operation.
 */
@FunctionalInterface
public interface ChannelOperation extends Operation {
  int getChannel();
}
