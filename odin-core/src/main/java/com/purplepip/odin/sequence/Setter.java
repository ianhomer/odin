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

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.music.notes.Note;
import jodd.bean.BeanException;
import jodd.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Sequence property setter.
 */
@Slf4j
public class Setter {
  private MutableSequence sequence;
  private Mode mode;

  /**
   * Create new setter for a sequence.
   *
   * @param sequence sequence to set properties on
   */
  public Setter(Sequence sequence) {
    this(sequence, Mode.BEST);
  }

  /**
   * Create new setter for a sequence.
   *
   * @param sequence sequence to set properties on
   * @param mode setting mode
   */
  public Setter(Sequence sequence, Mode mode) {
    if (!(sequence instanceof MutableSequence)) {
      throw new OdinRuntimeException(
          "Setter can only be used for mutable sequences, not " + sequence);
    }
    this.sequence = (MutableSequence) sequence;
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
        if (BeanUtil.declared.hasProperty(sequence, name)) {
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
      BeanUtil.declared.setProperty(sequence, name, value);
    } catch (BeanException e) {
      LOG.debug("Ignoring non-valid sequence property (full stack)", e);
      LOG.warn("Ignoring non-valid sequence property {} = {} for {}",
          name, value, sequence);
    }
  }

  private void setProperty(String name, Object value) {
    if (value instanceof Note) {
      Note note = (Note) value;
      sequence.setProperty(name + ".number", note.getNumber());
      sequence.setProperty(name + ".velocity", note.getVelocity());
      sequence.setProperty(name + ".duration", note.getDuration());
    } else {
      sequence.setProperty(name, value.toString());
    }
  }

  public enum Mode {
    DECLARED, BEST
  }
}
