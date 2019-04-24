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

package com.purplepip.odin.clock;

import static com.purplepip.odin.math.LessThan.lessThan;
import static org.junit.Assert.assertEquals;

import com.purplepip.odin.math.Wholes;
import org.junit.Test;

public class LoopTest {
  @Test
  public void testConstruct() throws Exception {
    assertEquals(Wholes.MINUS_ONE, new Loop().getLength());
    assertEquals(Wholes.MINUS_ONE, new Loop().getPosition());
    assertEquals(Wholes.ONE, new Loop(Wholes.ONE).getPosition());
    assertEquals(Wholes.ONE, new Loop(1).getPosition());
  }

  @Test
  public void testIncrement() throws Exception {
    Loop loop = new Loop(Wholes.valueOf(4), Wholes.ONE);
    loop.increment();
    assertEquals(lessThan(Wholes.TWO), loop.getPosition());
    loop.increment();
    loop.increment();
    loop.increment();
    assertEquals(lessThan(Wholes.ONE), loop.getPosition());
    assertEquals(Wholes.valueOf(4), loop.getStart());
  }

  @Test
  public void testStart() {
    assertEquals(Wholes.valueOf(0), new Loop(Wholes.valueOf(4), Wholes.valueOf(0)).getStart());
    assertEquals(Wholes.valueOf(0), new Loop(Wholes.valueOf(4), Wholes.valueOf(1)).getStart());
    assertEquals(Wholes.valueOf(4), new Loop(Wholes.valueOf(4), Wholes.valueOf(5)).getStart());
    assertEquals(Wholes.valueOf(4), new Loop(Wholes.valueOf(4), Wholes.valueOf(4)).getStart());
  }

}