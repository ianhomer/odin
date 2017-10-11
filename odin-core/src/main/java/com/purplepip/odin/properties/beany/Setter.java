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

package com.purplepip.odin.properties.beany;

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.music.notes.Note;
import java.util.Map;
import jodd.bean.BeanException;
import jodd.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Property setter for a property provider.
 */
@Slf4j
public class Setter {
  private MutablePropertiesProvider provider;
  private Mode mode;

  /**
   * Create new setter for a properties provider.
   *
   * @param provider sequence to set properties on
   */
  public Setter(MutablePropertiesProvider provider) {
    this(provider, Mode.BEST);
  }

  /**
   * Create new setter for a properties provider.
   *
   * @param provider sequence to set properties on
   * @param mode setting mode
   */
  public Setter(MutablePropertiesProvider provider, Mode mode) {
    this.provider = provider;
    this.mode = mode;
  }

  /**
   * Set a int property.
   *
   * @param name property name
   * @param value int value
   * @return this
   */
  public Setter set(String name, int value) {
    return set(name, Integer.valueOf(value));
  }

  /**
   * Set a property.
   *
   * @param name property name
   * @param value value
   * @return this
   */
  public Setter set(String name, Object value) {
    switch (mode) {
      case DECLARED:
        setDeclared(name, value);
        break;
      case BEST:
        if (BeanUtil.declared.hasProperty(provider, name)) {
          setDeclared(name, value);
        } else {
          setProperty(name, value);
        }
        break;
      default:
        throw new OdinRuntimeException("Mode " + mode + " not recognised");
    }
    return this;
  }

  private void setDeclared(String name, Object value) {
    try {
      BeanUtil.declared.setProperty(provider, name, value);
    } catch (BeanException e) {
      LOG.debug("Ignoring non-valid property (full stack)", e);
      LOG.warn("Ignoring non-valid property {} = {} for {}",
          name, value, provider);
    }
  }

  private void setProperty(String name, Object value) {
    if (value instanceof Note) {
      Note note = (Note) value;
      provider.setProperty(name + ".number", note.getNumber());
      provider.setProperty(name + ".velocity", note.getVelocity());
      provider.setProperty(name + ".duration", note.getDuration());
    } else {
      provider.setProperty(name, value.toString());
    }
  }

  public enum Mode {
    DECLARED, BEST
  }

  /**
   * Set the properties based on the properties map.  If the properties provider uses declared
   * properties, then set the declared properties too.
   *
   * @param properties to apply.
   */
  public void applyProperties(Map<String, String> properties) {
    properties.forEach(provider::setProperty);
    if (provider.arePropertiesDeclared()) {
      properties.keySet().forEach(name -> set(name, properties.get(name)));
    }
  }
}
