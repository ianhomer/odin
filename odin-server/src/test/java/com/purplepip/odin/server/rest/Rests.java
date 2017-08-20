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

package com.purplepip.odin.server.rest;

import java.nio.charset.Charset;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Static helper for REST tests.
 */
public class Rests {
  private static MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(),
      Charset.forName("utf8"));

  public static MockHttpServletRequestBuilder
      sendingJson(MockHttpServletRequestBuilder requestBuilder) {
    requestBuilder.contentType(contentType);
    return requestBuilder;
  }

  /*
   * For example get value $._embedded.notations[0]._links.self.href
   */
  public static String createHrefJsonPath(String entitiesName, int index) {
    return "$._embedded." + entitiesName + "[" + index + "]._links.self.href";
  }
}
