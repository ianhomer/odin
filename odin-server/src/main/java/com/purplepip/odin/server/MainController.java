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
