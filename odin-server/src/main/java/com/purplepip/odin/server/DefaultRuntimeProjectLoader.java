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

package com.purplepip.odin.server;

import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.sequence.tick.Ticks;
import com.purplepip.odin.store.PersistableProjectBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@Slf4j
@Order(4)
public class DefaultRuntimeProjectLoader implements CommandLineRunner {
  private static String BREAK = "break";
  private static String VERSE = "verse";

  @Autowired
  private ProjectContainer projectContainer;

  /*
   * Default project creator must have been loaded before loading the runtime project, hence
   * this is autowired, but NOT used.
   */
  @Autowired
  private DefaultProjectCreator defaultProjectCreator;

  @Override
  public void run(String... args) throws Exception {
    if (projectContainer.isEmpty()) {
      new PersistableProjectBuilder(projectContainer)
          .withChannel(1).changeProgramTo("piano")
          .withChannel(2).changeProgramTo("rock")
          .withChannel(3).changeProgramTo("strings")
          .withChannel(4).changeProgramTo("aahs")
          .withChannel(5).changeProgramTo("bass")
          .withChannel(9).changeProgramTo("Power Drums")
          .addLayer("overlay")
          .withLength(16).addLayer("intro").addLayer(BREAK)
          .withLength(4).addLayer("out").addLayer("in")
          .withLength(4).addLayer("a").addLayer("b")
          .withLength(8).addLayer("c")
          .withLength(48).withLayers("a", "b", "c").addLayer(VERSE)
          .withLength(-1).withLayers("in", "intro", VERSE, BREAK, "out").addLayer("groove")
          .withChannel(1)
          .withLayers("a", "c")
          .withName("piano-a").addNotation(Ticks.BEAT, "A/q G/8 A/q E")
          .withLayers("b")
          .withName("piano-c").addNotation(Ticks.BEAT, "C5/q A4/8 C5/q D")
          .withLayers("a")
          .withName("piano-a3").addNotation(Ticks.BEAT, "A3 A A/8 A A/q")
          .withName("bass-3").withChannel(5).addNotation(Ticks.BEAT, "A3 A A/8 A A/q")
          .withLayers("b")
          .withChannel(1)
          .withName("piano-c3").addNotation(Ticks.BEAT, "C3 C C/8 C C/q")
          .withLayers("c")
          .withName("piano-g3").addNotation(Ticks.BEAT, "G3 G G/8 A C/q G2 G G3/8 G G/q")
          .withLayers("a").withName("notes-c5").addNotation(Ticks.BEAT, "C5/h C/q")
          .withLayers("b").withName("notes-c4").addNotation(Ticks.BEAT, "C4/h C/q")
          .withLayers(BREAK).withName("notes")
          .addNotation(Ticks.BEAT, "C5/h C/q C6 A5 A5/8 C5/8 A5")
          .withChannel(3)
          .withLayers("intro").withName("strings-c5").addNotation(Ticks.BEAT, "C5/h C/q")
          .withLayers("intro").withName("strings-c4").addNotation(Ticks.BEAT, "C4/h C/q")
          .withLayers(BREAK).withName("strings").addNotation(Ticks.BEAT, "C5/h C/q C6 A5")
          .withChannel(2)
          .withLayers("a", "in").withName("organ-a").addNotation(Ticks.BEAT, "A5/8")
          .withLayers("b").withName("organ-b").addNotation(Ticks.BEAT, "G5/q")
          .withLayers("c").withName("organ-c")
          .addNotation(Ticks.BEAT, "G/q C C/8 A/8 G/q C D/8 A/8")
          .withChannel(4)
          .withLayers("a").withName("aahs-a").addNotation(Ticks.BEAT, "C A C5/h C5/8")
          .withLayers("c").withName("aahs-c").addNotation(Ticks.TWO_BEAT, "C5/h A4 G4 E C")
          .withLayers(BREAK).withName("aahs-br").addNotation(Ticks.BEAT, "C5/h A4 G4 E C")
          .withChannel(9)
          .withLayers("overlay").withName("shake").withNote(69).addPattern(Ticks.BEAT, 15)
          .withLayers("a", "b").withName("kick1").withNote(33).addPattern(Ticks.HALF, 13)
          .withLayers("c").withName("kick2").withNote(33).addPattern(Ticks.QUARTER, 65)
          .withLayers(BREAK).withName("kick3").withNote(33).addPattern(Ticks.QUARTER, 87)
          .withLayers(VERSE).withName("hi").withNote(42).addPattern(Ticks.HALF, 15)
          .withLayers("c").withName("crash").withNote(46).addPattern(Ticks.HALF, 87);
      projectContainer.save();
      LOG.info("Default sequences loaded");
      projectContainer.apply();
      LOG.info("Default project populated");
      projectContainer.load();
    } else {
      LOG.warn("Default project has already been loaded");
    }
  }
}
