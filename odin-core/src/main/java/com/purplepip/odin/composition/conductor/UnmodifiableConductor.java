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

package com.purplepip.odin.composition.conductor;

import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import java.util.stream.Stream;
import lombok.ToString;

@ToString
public class UnmodifiableConductor implements Conductor {
  private Conductor underlyingConductor;

  public UnmodifiableConductor(Conductor conductor) {
    this.underlyingConductor = conductor;
  }

  @Override
  public long getId() {
    return underlyingConductor.getId();
  }

  @Override
  public String getName() {
    return underlyingConductor.getName();
  }

  @Override
  public Conductor getParent() {
    return underlyingConductor.getParent();
  }

  @Override
  public Stream<Conductor> getChildren() {
    return underlyingConductor.getChildren().map(UnmodifiableConductor::new);
  }

  @Override
  public Conductor findByName(String name) {
    return underlyingConductor.findByName(name);
  }

  @Override
  public Rational getLength() {
    return underlyingConductor.getLength();
  }

  @Override
  public long getOffset() {
    return underlyingConductor.getOffset();
  }

  @Override
  public Real getPosition(long microseconds) {
    return underlyingConductor.getPosition(microseconds);
  }

  @Override
  public Real getPosition(String name, long microseconds) {
    return underlyingConductor.getPosition(name, microseconds);
  }

  @Override
  public boolean isActive(long microseconds) {
    return underlyingConductor.isActive(microseconds);
  }
}
