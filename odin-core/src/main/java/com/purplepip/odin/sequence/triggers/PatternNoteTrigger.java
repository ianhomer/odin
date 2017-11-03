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

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.notes.Notes;
import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.music.sequence.Pattern;
import com.purplepip.odin.sequence.SequenceConfiguration;
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
  private Note note = Notes.newNote();
  private String patternName;
  private Pattern pattern;

  public PatternNoteTrigger patternName(String patternName) {
    this.patternName = patternName;
    return this;
  }

  /**
   * Create a copy of this sequence.
   *
   * @return copy
   */
  @Override
  public PatternNoteTrigger copy() {
    return copy(new PatternNoteTrigger(), this);
  }

  protected PatternNoteTrigger copy(PatternNoteTrigger copy, PatternNoteTrigger original) {
    copy.patternName = original.patternName;
    super.copy(copy, original);
    return copy;
  }

  @Override
  public boolean isTriggeredBy(Operation operation) {
    assert pattern != null : "Pattern has not been injected";
    return (operation instanceof NoteOnOperation)
        && ((NoteOnOperation) operation).getNumber() == pattern.getNote().getNumber();
  }

  /**
   * Inject sequence configuration into trigger.
   *
   * @param sequence sequence configuration to inject
   */
  public void inject(SequenceConfiguration sequence) {
    if (sequence instanceof Pattern) {
      this.pattern = (Pattern) sequence;
    } else {
      throw new OdinRuntimeException("Only sequences of type Pattern should be injected into a "
          + PatternNoteTrigger.class.getName());
    }
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
