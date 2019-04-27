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

package com.purplepip.odin.common;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Test;

public class StringyTest {
  @Test
  public void testStringy() {
    StringyObject o = new StringyObject();
    o.name = "my-name";
    o.property = "my-value";
    o.test = "test-value";
    o.properties.put("property1", "value1");
    o.properties.put("property2", "value2");
    assertEquals("StringyObject(name=my-name, property=my-value, "
            + "test-ok=test-value, "
            + "properties=[property2=value2, property1=value1])",
        Stringy.of(StringyObject.class)
            .add("name", o.name)
            .add("property", o.property)
            .add("test-nok", o.test, value -> !value.equals("test-value"))
            .add("test-ok", o.test, value -> value.equals("test-value"))
            .add("properties", o.properties.entrySet().stream())
            .build());
  }

  @Test
  public void testExtends() {
    ExtendsStringyObject o = new ExtendsStringyObject();
    o.setName("my-name");
    o.extra = "extra-value";
    assertEquals(".(name=my-name, extra=extra-value)",
        Stringy.of(StringyObject.class, o)
            .add("name", o.getName())
            .add("extra", o.getExtra())
            .build());
  }

  @Test
  public void testExtendsEmpty() {
    ExtendsStringyObject o = new ExtendsStringyObject();
    assertEquals(".",
        Stringy.of(StringyObject.class, o)
            .add("name", o.getName())
            .add("extra", o.getExtra())
            .build());
  }

  @Test
  public void testStringyIncludeNulls() {
    StringyObject o = new StringyObject();
    o.name = "my-name";
    o.properties.put("property1", null);
    assertEquals("StringyObject(name=my-name, property=null, properties=[property1=null])",
        Stringy.of(StringyObject.class).includeNulls()
            .add("name", o.name)
            .add("property", o.property)
            .add("properties", o.properties.entrySet().stream())
            .build());
  }


  @Test
  public void testStringyIgnoreNulls() {
    StringyObject o = new StringyObject();
    o.name = "my-name";
    o.properties.put("property1", null);
    assertEquals("StringyObject(name=my-name)",
        Stringy.of(StringyObject.class)
            .add("name", o.name)
            .add("property", o.property)
            .add("properties", o.properties.entrySet().stream())
            .build());
  }

  @Data
  @EqualsAndHashCode
  private class StringyObject {
    private String name;
    private String property;
    private String test;
    private Map<String, String> properties = new HashMap<>();
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  private class ExtendsStringyObject extends StringyObject {
    private String extra;
  }
}