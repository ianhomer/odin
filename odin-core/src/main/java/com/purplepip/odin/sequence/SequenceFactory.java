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
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.sequence.flow.RationalTypeConverter;
import com.purplepip.odin.sequence.flow.RealTypeConverter;
import jodd.bean.BeanCopy;
import jodd.bean.BeanException;
import jodd.bean.BeanUtil;
import jodd.typeconverter.TypeConverterManager;
import lombok.extern.slf4j.Slf4j;

/**
 * Factory to create sequences.
 */
@Slf4j
public class SequenceFactory {
  static {
    TypeConverterManager.register(Real.class, new RealTypeConverter());
    TypeConverterManager.register(Rational.class, new RationalTypeConverter());
  }

  @SuppressWarnings("unchecked")
  public <S extends Sequence> S createTypedCopy(Class<? extends S> newClassType,
                                                Sequence original) {
    return (S) createCopy(newClassType, newClassType, original);
  }

  @SuppressWarnings("unchecked")
  public <S extends Sequence> S createTypedCopy(Class<? extends S> expectedType,
                             Class<? extends S> newClassType,
                             Sequence original) {
    return (S) createCopy(expectedType, newClassType, original);
  }

  /**
   * Create a copy of the sequence with the expected type.
   *
   * @param expectedType expected type of the returned sequence
   * @param newClassType class to use for new instance if original not of expected type
   * @param original original sequence to copy values from
   * @return sequence of expected type
   */
  public Sequence createCopy(Class<? extends Sequence> expectedType,
                                  Class<? extends Sequence> newClassType,
                                  Sequence original) {

    Sequence newSequence;
    if (expectedType == null || newClassType == null) {
      if (expectedType == null) {
        throw new OdinRuntimeException("Expected sequence type for "
            + original.getFlowName() + " is not set");
      }
      throw new OdinRuntimeException("Expected sequence type for " + original.getFlowName()
        + " is not set");
    } else {
      if (expectedType.isAssignableFrom(original.getClass())) {
        /*
         * If the original is of the correct type then we can simply take a copy
         */
        newSequence = original.copy();
        LOG.debug("Starting flow with sequence copy {}", newSequence);
      } else {
        LOG.debug("Creating new instance of {}", newClassType);
        try {
          newSequence = newClassType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
          throw new OdinRuntimeException("Cannot create new instance of " + newClassType, e);
        }
        final MutableSequence mutableSequence = (MutableSequence) newSequence;
        // TODO : BeanCopy doesn't seem to copy list of layers so we'll do this manually
        mutableSequence.getLayers().addAll(original.getLayers());
        LOG.debug("Copying original object properties to copy");
        BeanCopy.from(original).to(mutableSequence).copy();
        LOG.debug("Copying original properties map to copy");
        original.getPropertyNames()
            .forEach(name -> {
              mutableSequence.setProperty(name, original.getProperty(name));
              try {
                BeanUtil.declared.setProperty(mutableSequence, name, original.getProperty(name));
              } catch (BeanException exception) {
                LOG.warn("Whilst creating sequence {}", exception.getMessage());
              }
            });
        LOG.debug("Starting flow with typed copy {}", newSequence);
      }
    }
    return newSequence;
  }
}
