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

package com.purplepip.odin.creation.triggers;

import com.purplepip.odin.creation.sequence.SequenceStartOperation;
import com.purplepip.odin.operation.Operation;
import com.purplepip.odin.specificity.Name;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data()
@Name("sequenceStart")
public class SequenceStartTrigger extends GenericTrigger implements SpecialisedTrigger {
  private String sequenceName;

  public SequenceStartTrigger sequenceName(String sequenceName) {
    this.sequenceName = sequenceName;
    return this;
  }

  /**
   * Create a copy of this sequence.
   *
   * @return copy
   */
  @Override
  public SequenceStartTrigger copy() {
    return copy(new SequenceStartTrigger(), this);
  }

  protected SequenceStartTrigger copy(SequenceStartTrigger copy, SequenceStartTrigger original) {
    copy.sequenceName = original.sequenceName;
    super.copy(copy, original);
    return copy;
  }

  @Override
  public boolean isTriggeredBy(Operation operation) {
    return (operation instanceof SequenceStartOperation)
        && ((SequenceStartOperation) operation).getSequenceName().equals(sequenceName);
  }
}
