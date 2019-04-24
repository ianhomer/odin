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

package com.purplepip.odin.midix;

import javax.sound.midi.ShortMessage;

/**
 * Raw MIDI Messages that take raw bytes.  We use this technique as it uses more logic from the
 * generic layer that is also used on Android implementation.
 */
class RawMidiMessage extends ShortMessage {
  RawMidiMessage(byte[] buffer) {
    super(buffer);
  }
}
