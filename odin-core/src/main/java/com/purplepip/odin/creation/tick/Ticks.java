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

package com.purplepip.odin.creation.tick;

import com.purplepip.odin.math.Rationals;
import com.purplepip.odin.math.Wholes;

/**
 * This class consists exclusively of static properties that utilise the Tick model.
 */
public final class Ticks {
  public static final Tick SECOND = new DefaultTick(TimeUnit.MICROSECOND, 1_000_000);
  public static final Tick MILLISECOND = new DefaultTick(TimeUnit.MICROSECOND, 1_000);
  public static final Tick MICROSECOND = new DefaultTick(TimeUnit.MICROSECOND);
  public static final Tick BEAT = new DefaultTick(TimeUnit.BEAT);
  public static final Tick TWO_BEAT = new DefaultTick(TimeUnit.BEAT, Wholes.TWO);
  public static final Tick HALF = new DefaultTick(TimeUnit.BEAT, Rationals.HALF);
  public static final Tick THIRD = new DefaultTick(TimeUnit.BEAT, Rationals.THIRD);
  public static final Tick TWO_THIRDS = new DefaultTick(TimeUnit.BEAT, Rationals.TWO_THIRDS);
  public static final Tick FOUR_THIRDS = new DefaultTick(TimeUnit.BEAT, Rationals.FOUR_THIRDS);
  public static final Tick QUARTER = new DefaultTick(TimeUnit.BEAT, Rationals.QUARTER);
  public static final Tick THREE_QUARTERS = new DefaultTick(TimeUnit.BEAT,
      Rationals.THREE_QUARTERS);
  public static final Tick EIGHTH = new DefaultTick(TimeUnit.BEAT, Rationals.EIGTH);
  public static final Tick MEASURE = new DefaultTick(TimeUnit.MEASURE);

  private Ticks() {
  }
}
