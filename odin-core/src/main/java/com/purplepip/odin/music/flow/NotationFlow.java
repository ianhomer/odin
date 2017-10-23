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

package com.purplepip.odin.music.flow;

import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.music.sequence.Notation;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.flow.AbstractFlow;
import com.purplepip.odin.sequence.flow.FlowDefinition;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * Notation flow.
 */
@Slf4j
@FlowDefinition(name = "notation", sequence = Notation.class)
public class NotationFlow extends AbstractFlow<Notation, Note> {

  public NotationFlow(Clock clock, MeasureProvider measureProvider) {
    super(clock, measureProvider);
  }
}
