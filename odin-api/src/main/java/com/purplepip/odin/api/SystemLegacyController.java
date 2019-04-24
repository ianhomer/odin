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

import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.devices.Environment;
import com.purplepip.odin.midix.MidiHandle;
import com.purplepip.odin.midix.SynthesizerDevice;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.web.app.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Legacy system controller which outputs system information using HTML templates.templates.
 * SystemController in the services/system package outputs JSON data which front end can render as
 * it so desires.
 */
@Controller
public class SystemLegacyController {
  @Autowired private Environment environment;

  @Autowired private MeasureProvider measureProvider;

  @Autowired private OdinSequencer sequencer;

  @Autowired private Page page;

  /**
   * System page.
   *
   * @param model model
   * @return template to render
   */
  @RequestMapping("/api/page/system")
  public String system(Model model) {
    environment.findOneSink(MidiHandle.class).ifPresent(sink -> model.addAttribute("sink", sink));
    environment
        .findOneSource(MidiHandle.class)
        .ifPresent(source -> model.addAttribute("source", source));
    model.addAttribute("measureProvider", measureProvider);
    common(model);
    return "system/index";
  }

  /**
   * Instruments page.
   *
   * @param model model
   * @return template to render
   */
  @RequestMapping("/api/page/system/synthesizer")
  public String synthesizer(Model model) {
    environment
        .findOneSink(MidiHandle.class)
        .filter(SynthesizerDevice.class::isInstance)
        .ifPresent(synthesizer -> model.addAttribute("synthesizer", synthesizer));
    common(model);
    return "system/synthesizer";
  }

  /**
   * Sequencer page.
   *
   * @param model model
   * @return template to render
   */
  @RequestMapping("/api/page/system/sequencer")
  public String sequencer(Model model) {
    model.addAttribute("sequencer", sequencer);
    common(model);
    return "system/sequencer";
  }

  private void common(Model model) {
    model.addAttribute("page", page);
  }
}
