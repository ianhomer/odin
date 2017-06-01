package com.purplepip.odin.server.rest;

import com.purplepip.odin.server.rest.repositories.PatternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Pattern controller.
 */
@Controller
public class PatternController {

  @Autowired
  PatternRepository repository;
}
