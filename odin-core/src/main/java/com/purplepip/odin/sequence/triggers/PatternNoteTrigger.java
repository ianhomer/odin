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

package com.purplepip.odin.sequence.triggers;

import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.notes.Notes;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.properties.runtime.Property;
import com.purplepip.odin.sequencer.Operation;
import com.purplepip.odin.specificity.Name;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Trigger that gets triggered when the note for the given pattern sequence is received.
 */
@Slf4j
@Data
@Name("patternNote")
public class PatternNoteTrigger extends GenericTrigger implements SpecialisedTrigger {
  private Note note = Notes.newDefault();
  private String patternName;
  private Property<Pattern> pattern;

  public PatternNoteTrigger() {
  }

  public PatternNoteTrigger(long id) {
    super(id);
  }


  /**
   * Create a copy of this sequence.
   *
   * @return copy
   */
  @Override
  public PatternNoteTrigger copy() {
    PatternNoteTrigger copy = new PatternNoteTrigger(this.getId());
    Triggers.copyCoreValues(this, copy);
    copy.setPatternName(this.getPatternName());
    return copy;
  }

  @Override
  public boolean isTriggeredBy(Operation operation) {
    return (operation instanceof NoteOnOperation)
        && ((NoteOnOperation) operation).getNumber() == pattern.get().getNote().getNumber();
  }

  public void inject(Property<Pattern> pattern) {
    this.pattern = pattern;
  }

  /**
   * Initialisation after properties are set.
   */
  @Override
  public void afterPropertiesSet() {
    LOG.debug("Initialising pattern note trigger with {}", patternName);
    registerDependency(patternName);
  }
}
