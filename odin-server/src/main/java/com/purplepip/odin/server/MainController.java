package com.purplepip.odin.server;

import com.purplepip.odin.midix.MidiDeviceWrapper;
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

  @RequestMapping("/")
  public String index(Model model, @RequestParam(value="name", required=false, defaultValue="World") String name) {
    model.addAttribute("name", name);
    return "index";
  }
}
