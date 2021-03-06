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

package com.purplepip.odin.events;

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;

public class GenericEvent<A> implements Event {
  private Real time;
  private A value;

  public GenericEvent(A value, long time) {
    setValue(value);
    setTime(time);
  }

  public GenericEvent(A value, Real time) {
    setValue(value);
    setTime(time);
  }

  protected void setTime(Real time) {
    this.time = time;
  }

  protected void setTime(long time) {
    this.time = Wholes.valueOf(time);
  }

  @Override
  public Real getTime() {
    return time;
  }

  public String toString() {
    return getClass().getSimpleName() + "(" + getValue() + " @ " + getTime() + ")";
  }

  protected void setValue(A value) {
    if (value == null) {
      throw new OdinRuntimeException("Cannot create an event with a null value");
    }
    this.value = value;
  }

  @Override
  public A getValue() {
    return value;
  }
}
