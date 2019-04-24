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

package com.purplepip.odin.creation.conductor;

import com.purplepip.odin.bag.Thing;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import java.util.stream.Stream;

/**
 * A conductor is responsible for conducting a collection of sequences.
 */
public interface Conductor extends Thing {
  /**
   * Parent of this conductors.  Note that although layers can have multiple layers, a conductor
   * can only have one parent, this is because the ancestry controls the timing of the given
   * conductor.  Where a layer has multiple parents, multiple conductors will need to be created
   * for each instance of the layer in the composition.  When this is done unique naming of the
   * conductors should be handled by creating a unique name in the collection of conductors
   * based on the the layer name.
   *
   * @return parent conductor
   */
  Conductor getParent();

  /**
   * Stream of child conductors.
   *
   * @return child conductors
   */
  Stream<Conductor> getChildren();

  /**
   * Find child conductor by name.  If conductor is not found then a null is returned.
   *
   * @param name conductor name
   * @return conductor
   */
  Conductor findByName(String name);

  /**
   * Period for which the conductor can be active.
   *
   * @return length
   */
  Rational getLength();

  /**
   * Offset from the start for when the conductor may be active.
   *
   * @return offset
   */
  Rational getOffset();

  /**
   * Get the tock position of the given microsecond in this conductor, relative to the start
   * of the current loop in the conductor.
   *
   * @return tock position
   */
  Real getPosition(long microseconds);

  /**
   * Get the tock position of the given microsecond in the child conductor, relative to the start
   * of the child conductor.
   *
   * @return tock position
   */
  Real getPosition(String name, long microseconds);

  /**
   * Is the conductor actively conducting sequences that it conducts.  If a given track
   * has no active conductors conducting it then the track will not fire any events.
   *
   * @return whether the conductor is active
   */
  boolean isActive(long microseconds);
}
