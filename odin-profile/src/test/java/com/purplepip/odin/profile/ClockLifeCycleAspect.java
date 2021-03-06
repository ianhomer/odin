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

import com.codahale.metrics.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ClockLifeCycleAspect {
  @Pointcut("execution(* com.purplepip.odin.clock.BeatClock.start(..))")
  public void onBeatClockStart() {}

  @Pointcut("execution(* *.onPerformanceStart(..))")
  public void onPerformanceStartMethods() {}

  @Pointcut("execution(* *.getNextEvent(..))")
  public void onGetNextEvent() {}

  @Pointcut("execution(* com.purplepip.odin.creation.flow.DefaultFlow.initialise(..))")
  public void onInitialise() {}

  @Pointcut("execution(* com.purplepip.odin.music.sequence.Notation.initialiseComposition(..))")
  public void onInitialiseComposition() {}

  @Pointcut("execution(* com.purplepip.odin.creation.track.MutableSequenceRoll.*(..))"
      + " && !execution(* *.getSequence(..))")
  public void inMutableSequenceRoll() {}

  @Pointcut("execution(* com.purplepip.odin.creation.flow.DefaultFlow.getNextEvent(..))"
      + " || execution(* com.purplepip.odin.creation.flow.DefaultFlow.getMaxScanForward(..))")
  public void inFlow() {}

  @Pointcut("execution(* com.purplepip.odin.creation.flow.FlowFactory.*(..))")
  public void inFlowFactory() {}

  @Pointcut("execution(* *(..))")
  public void anyExecution(){}

  @Pointcut("execution(* *..lambda*(..))")
  public void inLambda(){}

  /**
   * Around injection.
   *
   * @param pjp proceeding join point
   * @return object
   * @throws Throwable throwable
   */
  @Around("!inLambda() && (onBeatClockStart()"
      + " || onInitialise()"
      + " || onInitialiseComposition()"
      + " || onGetNextEvent()"
      + " || onPerformanceStartMethods()"
      + " || inMutableSequenceRoll()"
      + " || inFlow()"
      + " || inFlowFactory())")
  public Object around(ProceedingJoinPoint pjp) throws Throwable {
    try (Timer.Context context = Profile.getMetrics().timer(pjp.toShortString()).time()) {
      return pjp.proceed();
    }
  }
}
