package com.purplepip.odin.server;

import com.purplepip.odin.server.rest.Pattern;
import com.purplepip.odin.server.rest.SequenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Database Loader.
 */
@Component
public class DatabaseLoader implements CommandLineRunner {
  private final SequenceRepository repository;

  @Autowired
  public DatabaseLoader(SequenceRepository repository) {
    this.repository = repository;
  }

  @Override
  public void run(String... strings) throws Exception {
    this.repository.save(new Pattern(1));
  }
}
