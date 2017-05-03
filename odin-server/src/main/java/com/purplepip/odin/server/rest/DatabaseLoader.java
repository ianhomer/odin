package com.purplepip.odin.server.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Database loader.
 */
@Component
public class DatabaseLoader {
  private final SequenceRepository repository;

  @Autowired
  public DatabaseLoader(SequenceRepository repository) {
    this.repository = repository;
  }
}
