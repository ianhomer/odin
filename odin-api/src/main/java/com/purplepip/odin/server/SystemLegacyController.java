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

package com.purplepip.odin.server;

import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.midix.MidiSystemWrapper;
import com.purplepip.odin.sequencer.OdinSequencer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Legacy system controller which outputs system information using HTML templates.templates.
 * SystemController in the services/system package outputs JSON data which front end can render
 * as it so desires.
 */
@Controller
public class SystemLegacyController {
  @Autowired
  private MidiDeviceWrapper midiDeviceWrapper;

  @Autowired
  private MeasureProvider measureProvider;

  @Autowired
  private OdinSequencer sequencer;

  @Autowired
  private MidiSystemWrapper midiSystemWrapper;

  /**
   * System page.
   *
   * @param model model
   * @return template to render
   */
  @RequestMapping("/api/page/system")
  public String system(Model model) {
    model.addAttribute("midiSystem", midiSystemWrapper);
    model.addAttribute("midiDevice", midiDeviceWrapper);
    model.addAttribute("measureProvider", measureProvider);
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
    model.addAttribute("midiDevice", midiDeviceWrapper);
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
    return "system/sequencer";
  }
}
