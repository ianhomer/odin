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

package com.purplepip.odin.demo;

import static com.purplepip.odin.sequencer.DeltaOdinSequencerConfiguration.deltaConfiguration;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.sequencer.TestSequencerEnvironment;
import com.purplepip.odin.snapshot.Snapshot;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class GroovePerformanceTest {
  @Test
  public void testPerformance() throws OdinException, InterruptedException {
    Snapshot snapshot = new Snapshot(GroovePerformance.class);
    SnapshotOperationReceiver snapshotter = new SnapshotOperationReceiver(snapshot);

    TestSequencerEnvironment environment =
        new TestSequencerEnvironment(snapshotter, new GroovePerformance(),
            deltaConfiguration().staticBeatsPerMinute(60)
        );
    environment.start();
    try {
      snapshotter.getLatch().await(1000, TimeUnit.MILLISECONDS);
    } finally {
      environment.stop();
    }

    snapshot.expectMatch();
  }
}