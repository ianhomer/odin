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
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.creation.action.ActionOperation;
import com.purplepip.odin.creation.action.SetAction;
import com.purplepip.odin.creation.triggers.NoteTrigger;
import com.purplepip.odin.music.operations.NoteOffOperation;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.operation.OperationHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class OdinSequencerTriggerChangePropertyTest {
  @Test
  public void testSequencer() throws OdinException, InterruptedException {
    final CountDownLatch note60Latch = new CountDownLatch(16);
    final CountDownLatch note61Latch = new CountDownLatch(16);

    OperationHandler operationReceiver = (operation, time) -> {
      if (operation instanceof NoteOnOperation) {
        NoteOnOperation noteOnOperation =
            (NoteOnOperation) operation;
        if (noteOnOperation.getChannel() == 1) {
          // Input channel
        } else if (noteOnOperation.getChannel() == 2) {
          if (noteOnOperation.getNumber() == 60) {
            note60Latch.countDown();
          } else if (noteOnOperation.getNumber() == 61) {
            note61Latch.countDown();
          } else {
            LOG.warn("Unexpected note number for note on operation : {}", noteOnOperation);
          }
        } else {
          LOG.warn("Unexpected note on operation : {}", noteOnOperation);
        }
      } else if (operation instanceof NoteOffOperation || operation instanceof ActionOperation) {
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
            .trigger("note-trigger",
                new SetAction().nameValuePairs("note.number=61"))
            .name("note"))
        .addTrigger(new NoteTrigger().note(70).name("note-trigger"));

    environment.start();

    try {
      note60Latch.await(5000, TimeUnit.MILLISECONDS);
      assertEquals("Note 60 should have been fired",0, note60Latch.getCount());

      environment.getConfiguration().getOperationTransmitter().handle(
          new NoteOnOperation(1, 70, 5), -1
      );

      note61Latch.await(5000, TimeUnit.MILLISECONDS);
      assertEquals("Note 61 should have been fired",0, note61Latch.getCount());

    } finally {
      environment.shutdown();
    }
  }
}
