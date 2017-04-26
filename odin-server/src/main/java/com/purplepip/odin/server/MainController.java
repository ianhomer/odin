package com.purplepip.odin.server;

import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.midix.MidiSystemWrapper;
import com.purplepip.odin.music.MeasureProvider;
import com.purplepip.odin.sequencer.OdinSequencer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
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

  @RequestMapping("/")
  public String index(Model model, @RequestParam(value="name", required=false, defaultValue="World") String name) {
    model.addAttribute("name", name);
    model.addAttribute("midiSystem", midiSystemWrapper);
    model.addAttribute("midiDevice", midiDeviceWrapper);
    model.addAttribute("measureProvider", measureProvider);
    model.addAttribute("sequencer", sequencer);
    return "index";
  }
}
