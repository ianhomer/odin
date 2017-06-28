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

package com.purplepip.odin.sequencer;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.MutableSequenceRuntime;
import com.purplepip.odin.sequence.SequenceRuntime;

/**
 * SequenceRuntime driving a sequenced track.
 */
public class SequenceTrack {
  private MutableSequenceRuntime<Note> rootSequenceRuntime;
  private SequenceRuntime<Note> sequenceRuntime;

  public SequenceTrack(SequenceRuntime<Note> sequenceRuntime,
                       MutableSequenceRuntime<Note> rootSequenceRuntime) {
    this.sequenceRuntime = sequenceRuntime;
    this.rootSequenceRuntime = rootSequenceRuntime;
  }

  public int getChannel() {
    return sequenceRuntime.getSequence().getChannel();
  }

  /**
   * Get end of pipe line sequence runtime, after any post processing.
   *
   * @return sequence runtime.
   */
  public SequenceRuntime<Note> getSequenceRuntime() {
    return sequenceRuntime;
  }

  /**
   * Get the root sequence runtime, before post-processing.
   *
   * @return root sequence runtime.
   */
  public MutableSequenceRuntime<Note> getRootSequenceRuntime() {
    return rootSequenceRuntime;
  }
}
