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

package com.purplepip.odin.api;

import com.purplepip.odin.web.app.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Odin App Controller.
 */
@Controller
public class MainController {
  @Autowired
  private Page page;

  /**
   * Index page.
   *
   * @return redirect reference
   */
  @RequestMapping("/")
  public String index() {
    return "redirect:/web/app";
  }

  /**
   * Index page.
   *
   * @return template to render
   */
  @RequestMapping("/web/app/**")
  public String app(Model model) {
    model.addAttribute("page", page);
    return "index";
  }
}
