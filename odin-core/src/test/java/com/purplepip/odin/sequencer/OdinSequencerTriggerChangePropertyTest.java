/*
 * Copyright (c) 2017 the original author or authors. All Rights Reserved
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

import static com.purplepip.odin.creation.layer.Layers.newLayer;
import static com.purplepip.odin.music.notes.Notes.newNote;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.creation.action.ActionType;
import com.purplepip.odin.creation.triggers.NoteTrigger;
import com.purplepip.odin.music.operations.NoteOffOperation;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.music.sequence.Pattern;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class OdinSequencerTriggerChangePropertyTest {
  @Test
  public void testSequencer() throws OdinException, InterruptedException {
    final CountDownLatch noteLatch = new CountDownLatch(16);

    OperationReceiver operationReceiver = (operation, time) -> {
      if (operation instanceof NoteOnOperation) {
        NoteOnOperation noteOnOperation =
            (NoteOnOperation) operation;
        if (noteOnOperation.getChannel() == 1) {
          // Input channel
        } else if (noteOnOperation.getChannel() == 2) {
          noteLatch.countDown();
        } else {
          LOG.warn("Unexpected note on operation : {}", noteOnOperation);
        }
      } else if (operation instanceof NoteOffOperation) {
        LOG.trace("Ignoring note off operation : {}", operation);
      } else {
        LOG.warn("Unexpected operation : {}", operation);
      }
    };

    TestSequencerEnvironment environment = new TestSequencerEnvironment(operationReceiver);
    environment.getContainer()
        .addLayer(newLayer("groove"))
        .addSequence(new Pattern()
            .bits(1).note(newNote(60))
            .length(-1)
            .channel(2).layer("groove")
            .trigger("success-start-trigger", ActionType.RESET)
            .name("note"))
        .addTrigger(new NoteTrigger().note(70).name("note-trigger"));

    // TODO : Change the note value when note 70 pressed

    environment.start();

    try {
      noteLatch.await(1000, TimeUnit.MILLISECONDS);

      environment.getConfiguration().getOperationTransmitter().send(
          new NoteOnOperation(1, 70, 5), -1
      );

    } finally {
      environment.stop();
    }
  }
}
