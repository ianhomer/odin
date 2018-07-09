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

package com.purplepip.odin.math.typeconverters;

import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Whole;
import jodd.typeconverter.TypeConverterManager;
import lombok.extern.slf4j.Slf4j;

/**
 * Type converters need to be initialised.
 */
@Slf4j
public class MathTypeConverterManager {
  static {
    LOG.debug("Math type converters loaded");
    // TODO : Roll Jodd up to v4.x when resolution to https://github.com/ianhomer/odin/issues/6
    // Jodd 4.x needs
    TypeConverterManager manager = TypeConverterManager.get();
    manager.register(Rational.class, new RationalTypeConverter());
    manager.register(Real.class, new RealTypeConverter());
    manager.register(Whole.class, new WholeTypeConverter());

    //TypeConverterManager.register(Rational.class, new RationalTypeConverter());
    //TypeConverterManager.register(Real.class, new RealTypeConverter());
    //TypeConverterManager.register(Whole.class, new WholeTypeConverter());
  }

  private MathTypeConverterManager() {
  }

  public static void requireMathTypeConverters() {
    // No operation, just loading the class loads dependencies.  This method just makes
    // code more descriptive.
  }
}
