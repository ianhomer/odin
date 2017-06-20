/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

  /**
   * Add channel.
   */
  void addChannel(Channel channel);

  /**
   * Remove channel.
   */
  void removeChannel(Channel channel);

  /**
   * Add sequence.
   */
  void addSequence(Sequence sequence);

  /**
   * Remove channel.
   */
  void removeSequence(Sequence sequence);

}
