package com.purplepip.odin.server.rest;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@ContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "testDataLoaded", "noAuditing"})
@Slf4j
public class PreLoadedSequenceRestTest {
  @Autowired
  private MockMvc mvc;

  @Test
  public void testSequencesList() throws Exception {
    new EndPointSnapshot(mvc, "/api/sequence").expectMatch();
  }

}