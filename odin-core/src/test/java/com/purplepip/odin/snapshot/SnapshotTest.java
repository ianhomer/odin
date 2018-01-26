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

package com.purplepip.odin.snapshot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ThreadLocalRandom;
import org.junit.Test;

public class SnapshotTest {
  @Test
  public void testSnapshotWithTime() {
    Snapshot snapshot = new Snapshot(Snapshot.class, true);
    assertTrue(snapshot.getPath().toString()
            .endsWith("com/purplepip/odin/snapshot/snapshot/Snapshot.snap"));
    snapshot.writeLine(0,"Hello");
    snapshot.expectMatch();
  }

  @Test
  public void testSnapshotWithTimeWithoutHeader() {
    Snapshot snapshot = new Snapshot(Snapshot.class)
        .header(false).separator("-").variation("noHeader").initialise();
    String path = snapshot.getPath().toString();
    assertTrue(path + " not correct",
        path.endsWith("com/purplepip/odin/snapshot/snapshot/Snapshot-noHeader.snap"));
    snapshot.writeLine("Hello");
    snapshot.expectMatch();
  }

  @Test
  public void testSnapshotWithJsonWithLocation() {
    Snapshot snapshot = new Snapshot(Snapshot.class)
        .root("src/test/resources")
        .path("com/purplepip/odin/snapshot/snapshot/Snapshot")
        .extension("json")
        .header(false).initialise();
    String path = snapshot.getPath().toString();
    assertTrue(path + " not correct",
        path.endsWith("com/purplepip/odin/snapshot/snapshot/Snapshot.json"));
    snapshot.writeLine(0,"{ \"x\" : 1}");
    snapshot.expectMatch();
  }

  @Test
  public void testSnapshotWithVariabilityMask() {
    Snapshot snapshot = new Snapshot(Snapshot.class)
        .root("src/test/resources")
        .path("com/purplepip/odin/snapshot/snapshot/Snapshot")
        .variation("variable")
        .extension("json")
        .mask("(?<=http://localhost/api/channel/)[0-9]+(?=[^\"]*)")
        .header(false).initialise();
    int number = ThreadLocalRandom.current().nextInt(0, 99);
    snapshot.writeLine(0,"{ "
        + "\"x\" : \"http://localhost/api/channel/" + number + "\","
        + "\"y\" : \"http://localhost/api/channel/" + number + "/performance\""
        + "}");
    snapshot.expectMatch();
    assertEquals("{ "
        + "\"x\" : \"http://localhost/api/channel/__VAR__\","
        + "\"y\" : \"http://localhost/api/channel/__VAR__/performance\""
        + "}", snapshot.getExpected());
  }
}
