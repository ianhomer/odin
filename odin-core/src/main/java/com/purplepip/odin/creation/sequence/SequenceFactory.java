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

package com.purplepip.odin.creation.sequence;

import com.purplepip.odin.specificity.AbstractSpecificThingFactory;
import com.purplepip.odin.specificity.ThingConfiguration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Factory to create sequences.
 */
@Slf4j
public class SequenceFactory<A> extends AbstractSpecificThingFactory<Sequence<A>> {
  /**
   * Create a new sequence factory.
   *
   * @param classes sequence classes to initialise with
   */
  public SequenceFactory(List<Class<? extends Sequence<A>>> classes) {
    super(classes);
  }

  @Override
  protected void populate(Sequence<A> destination, ThingConfiguration source) {
    if (destination instanceof MutableSequenceConfiguration
        && source instanceof SequenceConfiguration) {
      // TODO : BeanCopy doesn't seem to copy list of layers so we'll do this manually
      destination.getLayers().addAll(((SequenceConfiguration) source).getLayers());
    }
    super.populate(destination, source);
  }
}
