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

package com.purplepip.odin.properties.thing;

import static com.purplepip.odin.math.typeconverters.MathTypeConverterManager.requireMathTypeConverters;

import com.purplepip.odin.properties.beany.MutablePropertiesProvider;
import com.purplepip.odin.specificity.ThingConfiguration;
import jodd.bean.BeanCopy;
import jodd.bean.BeanException;
import jodd.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Copy one properties thing to another.
 */
@Slf4j
public class ThingCopy {
  static {
    requireMathTypeConverters();
  }

  private ThingConfiguration source;
  private ThingConfiguration destination;

  public ThingCopy() {
  }

  public ThingCopy from(ThingConfiguration source) {
    this.source = source;
    return this;
  }

  public ThingCopy to(ThingConfiguration destination) {
    this.destination = destination;
    return this;
  }

  /**
   * Copy things.
   */
  public void copy() {
    LOG.trace("Populating bean properties from source");
    BeanCopy.from(source).to(destination).copy();
    LOG.trace("Populating properties map from source");
    if (destination instanceof MutablePropertiesProvider) {
      source.getPropertyNames().forEach(name -> {
        ((MutablePropertiesProvider) destination).setProperty(name, source.getProperty(name));
        if (destination.arePropertiesDeclared()) {
          try {
            BeanUtil.declared.setProperty(destination, name, source.getProperty(name));
          } catch (BeanException e) {
            LOG.debug("Whilst populating thing {} (full stack)", e);
            LOG.warn("Whilst populating thing {} : {}", destination, e.getMessage());
          }
        }
      });
    }
  }
}
