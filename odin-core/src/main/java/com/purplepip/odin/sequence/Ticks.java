/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
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

package com.purplepip.odin.sequence;

/**
 * This class consists exclusively of static properties that utilise the Tick model.
 */
public final class Ticks {
  public static final Tick SECOND = new DefaultTick(TimeUnit.MICROSECOND, 1_000_000);
  public static final Tick MILLISECOND = new DefaultTick(TimeUnit.MICROSECOND, 1_000);
  public static final Tick MICROSECOND = new DefaultTick(TimeUnit.MICROSECOND);
  public static final Tick BEAT = new DefaultTick(TimeUnit.BEAT);
  public static final Tick HALF = new DefaultTick(TimeUnit.BEAT, 1, 2);
  public static final Tick THREE_QUARTERS = new DefaultTick(TimeUnit.BEAT, 3, 4);
  public static final Tick FOUR_THIRDS = new DefaultTick(TimeUnit.BEAT, 4, 3);
  public static final Tick TWO_THIRDS = new DefaultTick(TimeUnit.BEAT, 2, 3);
  public static final Tick THIRD = new DefaultTick(TimeUnit.BEAT, 1, 3);
  public static final Tick QUARTER = new DefaultTick(TimeUnit.BEAT, 1, 4);
  public static final Tick EIGHTH = new DefaultTick(TimeUnit.BEAT, 1, 8);
  public static final Tick MEASURE = new DefaultTick(TimeUnit.MEASURE);

  private Ticks() {
  }
}
