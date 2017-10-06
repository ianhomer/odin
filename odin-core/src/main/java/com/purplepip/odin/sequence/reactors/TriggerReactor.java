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

package com.purplepip.odin.sequence.reactors;

import com.purplepip.odin.sequence.triggers.MessageTrigger;
import com.purplepip.odin.sequence.triggers.Trigger;
import javax.validation.constraints.NotNull;

public class TriggerReactor implements Reactor {
  private Trigger trigger;
  private String sequenceName;

  @Override
  public long getId() {
    return trigger.getId();
  }

  @Override
  @NotNull
  public String getName() {
    return trigger.getName();
  }

  public void setTrigger(Trigger trigger) {
    this.trigger = trigger;
  }

  public Trigger getTrigger() {
    return trigger;
  }

  public MessageTrigger getMessageTrigger() {
    return MessageTrigger.class.cast(trigger);
  }

  public void setSequenceName(String name) {
    this.sequenceName = sequenceName;
  }
}
