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

package com.purplepip.odin.profile;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ProfileAspect {
  @Pointcut("execution(* com.purplepip.odin.clock.BeatClock.start(..))")
  public void onBeatClockStart() {}

  @Pointcut("execution(* *.onPerformanceStart(..))")
  public void onPerformanceStartMethods() {}

  @Pointcut("execution(* com.purplepip.odin.creation.track.MutableSequenceRoll.*(..))")
  public void inMutableSequenceRoll() {}

  @Pointcut("execution(* com.purplepip.odin.creation.flow.FlowFactory.*(..))")
  public void inFlowFactory() {}

  @Pointcut("execution(* *(..))")
  public void anyExecution(){}

  /**
   * Around injection.
   *
   * @param pjp proceeding join point
   * @return object
   * @throws Throwable throwable
   */
  @Around("onBeatClockStart()"
      + " || onPerformanceStartMethods()"
      + " || inMutableSequenceRoll()"
      + " || inFlowFactory()")
  public Object around(ProceedingJoinPoint pjp) throws Throwable {
    long start = System.nanoTime();
    Object object = pjp.proceed();
    long delta = System.nanoTime() - start;
    Profile.getSnapshot().add(new Record(toString(pjp), delta));
    return object;
  }

  private String toString(ProceedingJoinPoint pjp) {
    StringBuilder name = new StringBuilder();
    if (pjp.getTarget() != null) {
      if (pjp.getTarget().getClass() != null) {
        name.append(pjp.getTarget().getClass().getName());
      } else {
        name.append("{class-null}");
      }
    } else {
      name.append("{target-null}");
    }
    if (pjp.getSignature() != null) {
      name.append('.').append(pjp.getSignature().getName());
    } else {
      name.append("{signature-null");
    }
    return name.toString();
  }
}
