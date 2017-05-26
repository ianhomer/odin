package com.purplepip.odin.server.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Sequence controller.
 */
@Controller
public class SequenceController {

  @Autowired
  SequenceRepository repository;
}
