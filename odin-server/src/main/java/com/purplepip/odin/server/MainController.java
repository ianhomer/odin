package com.purplepip.odin.server;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Odin Main Controller.
 */
@RestController
public class MainController {
  @RequestMapping("/")
  public String index() {
    return "Hello from Odin.";
  }
}
