/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purplepip.odin.server;

import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.midix.MidiSystemWrapper;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequencer.OdinSequencer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Odin Main Controller.
 */
@Controller
public class MainController {
  @Autowired
  private MidiDeviceWrapper midiDeviceWrapper;

  @Autowired
  private MeasureProvider measureProvider;

  @Autowired
  private OdinSequencer sequencer;

  @Autowired
  private MidiSystemWrapper midiSystemWrapper;

  /**
   * Index page.
   *
   * @param model model
   * @return template to render
   */
  @RequestMapping("/")
  public String index(Model model) {
    return "index";
  }

  /**
   * System page.
   *
   * @param model model
   * @return template to render
   */
  @RequestMapping("/system")
  public String system(Model model) {
    model.addAttribute("midiSystem", midiSystemWrapper);
    model.addAttribute("midiDevice", midiDeviceWrapper);
    model.addAttribute("measureProvider", measureProvider);
    model.addAttribute("sequencer", sequencer);
    return "system";
  }
}
