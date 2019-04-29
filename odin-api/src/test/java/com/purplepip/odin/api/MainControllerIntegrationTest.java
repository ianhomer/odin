package com.purplepip.odin.api;

import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URL;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "noStore", "noAuditing"})
class MainControllerIntegrationTest {

  @LocalServerPort
  private int port;

  private URL base;

  @Autowired
  private TestRestTemplate template;

  @BeforeEach
  void setUp() throws Exception {
    this.base = new URL("http://localhost:" + port + "/");
  }

  @Test
  void testHome() {
    ResponseEntity<String> response = template.getForEntity(base.toString(),
        String.class);
    assertThat(response.getBody(), CoreMatchers.containsString("odin"));
  }
}