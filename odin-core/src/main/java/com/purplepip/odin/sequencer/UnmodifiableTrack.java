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

import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.sequence.Roll;
import com.purplepip.odin.sequence.UnmodifiableRoll;
import com.purplepip.odin.sequence.tick.Tick;

public class UnmodifiableTrack implements Track {
  private Track underlyingTrack;

  public UnmodifiableTrack(Track track) {
    this.underlyingTrack = track;
  }

  @Override
  public int getChannel() {
    return underlyingTrack.getChannel();
  }

  @Override
  public Roll<Note> getRoll() {
    return new UnmodifiableRoll<>(underlyingTrack.getRoll());
  }

  @Override
  public Tick getTick() {
    return underlyingTrack.getTick();
  }
}
