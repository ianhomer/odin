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

import com.purplepip.odin.devices.AbstractHandleProvider;
import com.purplepip.odin.devices.Handle;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javax.sound.midi.MidiSystem;

public class MidiHandleProvider extends AbstractHandleProvider {
  private static final Set<Class<? extends Handle>> HANDLE_CLASSES =
      Collections.singleton(MidiHandle.class);

  public MidiHandleProvider(List<Handle> prioritisedSinks,
      List<Handle> prioritisedSources) {
    this(prioritisedSinks, prioritisedSources,
        Collections.emptySet(), Collections.emptySet());
  }

  public MidiHandleProvider(List<Handle> prioritisedSinks,
      List<Handle> prioritisedSources,
      Set<Handle> excludeSinks,
      Set<Handle> excludeSources) {
    super(prioritisedSinks, prioritisedSources, excludeSinks, excludeSources);
  }

  @Override
  public Set<Class<? extends Handle>> getHandleClasses() {
    return HANDLE_CLASSES;
  }

  @Override
  protected Stream<Handle> getHandleStream() {
    return Stream.of(MidiSystem.getMidiDeviceInfo()).map(MidiHandle::new);
  }
}
