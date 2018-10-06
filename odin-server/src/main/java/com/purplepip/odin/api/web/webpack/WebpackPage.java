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

package com.purplepip.odin.api.web.webpack;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.purplepip.odin.web.app.page.Page;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import lombok.extern.java.Log;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * Create a page from a webpack asset manifest file.
 */
@Component
@Primary
@Log
public class WebpackPage implements Page, InitializingBean {
  @Value( "${odin.app.package:com/purplepip/odin/web/app}" )
  private String appPackage;

  @Autowired
  private ResourceLoader resourceLoader;

  private List<String> cssLinks;

  private List<String> scripts;

  private String mainJs;

  public void afterPropertiesSet() throws IOException {
    Resource resource = resourceLoader.getResource("classpath:" + appPackage + "/asset-manifest.json");
    LOG.info("Loaded webpack manifest : " + resource);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.readTree(resource.getInputStream());
    Set<String> chunks = new TreeSet<>();
    node.fields().forEachRemaining(item -> {
      String key = item.getKey();
      String value = item.getValue().asText();
      if ("main.css".equals(key)) {
        cssLinks = List.of(value);
      } else if ("main.js".equals(key)) {
        mainJs = value;
      } else if (key.startsWith("static/js") && key.endsWith(".js")) {
        chunks.add(value);
      }
    });
    scripts = new LinkedList<>();
    // Script chunks in numerical order ...
    scripts.addAll(chunks);
    // then main js
    scripts.add(mainJs);
  }

  @Override
  public List<String> getCssLinks() {
    return cssLinks;
  }

  @Override
  public List<String> getScripts() {
    return scripts;
  }
}
