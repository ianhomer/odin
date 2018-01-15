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

import com.google.common.collect.Sets;
import com.purplepip.odin.music.notes.Note;
import com.purplepip.odin.properties.beany.MutablePropertiesProvider;
import com.purplepip.odin.specificity.ThingConfiguration;
import java.beans.BeanInfo;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Set;
import jodd.bean.BeanCopy;
import jodd.bean.BeanException;
import jodd.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Copy one properties thing to another.
 */
@Slf4j
public class ThingCopy {
  private static Set<String> IGNORE_PROPERTIES = Sets
      .newHashSet("class", "propertyEntries", "propertyNames");

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
      /*
       * Copy generic to specific.  This involves copying the properties in the property map
       * to the declared properties.
       */
      if (!source.arePropertiesDeclared() && destination.arePropertiesDeclared()) {
        source.getPropertyNames().forEach(name -> {
          try {
            BeanUtil.declared.setProperty(destination, name, source.getProperty(name));
          } catch (BeanException e) {
            LOG.debug("Whilst populating thing " + destination + " (full stack)", e);
            LOG.warn("Whilst populating thing {} : {}", destination, e.getMessage());
          }
        });
      } else if (source.arePropertiesDeclared() && !destination.arePropertiesDeclared()) {
        /*
         * Copy specific to generic.   This involves copying the declared properties to
         * the generic properties map.
         */
        try {
          BeanInfo beanInfo = Introspector.getBeanInfo(source.getClass());
          PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
          Arrays.stream(propertyDescriptors)
              .map(FeatureDescriptor::getName)
              .filter(name -> !IGNORE_PROPERTIES.contains(name))
              .forEach(name -> {
                if (!BeanUtil.declared.hasProperty(destination, name)) {
                  /*
                   * Only copy property into properties map if it is not a declared property
                   * in the destination.
                   */
                  Object value = BeanUtil.declared.getProperty(source, name);
                  MutablePropertiesProvider mutableDestination =
                      (MutablePropertiesProvider) destination;
                  if (value != null) {
                    if (value instanceof Note) {
                      Note note = (Note) value;
                      mutableDestination.setProperty(name + ".number", note.getNumber());
                      mutableDestination.setProperty(name + ".velocity", note.getVelocity());
                      mutableDestination.setProperty(name + ".duration", note.getDuration());
                    } else {
                      mutableDestination.setProperty(name, value.toString());
                    }
                  }
                }
              });
        } catch (IntrospectionException e) {
          LOG.debug("Whilst getting bean info for thing " + destination + " (full stack)", e);
          LOG.warn("Whilst getting bean info for thing {} : {}", destination, e.getMessage());
        }
      } else if (!source.arePropertiesDeclared() && !destination.arePropertiesDeclared()) {
        /*
         * Copy generic to generic.   This involves just copying the properties map.
         */
        source.getPropertyNames().forEach(name ->
            ((MutablePropertiesProvider) destination).setProperty(name, source.getProperty(name))
        );
      }
    }
  }
}
