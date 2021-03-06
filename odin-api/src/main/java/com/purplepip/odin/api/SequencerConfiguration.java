/*
 * Copyright (c) 2017 the original author or authors. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purplepip.odin.api;

import com.purplepip.odin.sequencer.OdinSequencer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Sequencer configuration.
 */
@Component
@Order(1)
@Slf4j
@Profile("!noSequencer")
public class SequencerConfiguration implements CommandLineRunner, DisposableBean {
  @Autowired
  private OdinSequencer sequencer;

  @Value("${purplepip.odin.sequencer.autoStart:true}")
  private boolean autoStart;

  @Override
  public void run(String... args) {
    sequencer.prepare();
    if (autoStart) {
      sequencer.start();
    }
    LOG.info("Sequencer started");
  }

  @Override
  public void destroy() {
    LOG.info("Sequencer stopping ...");
    sequencer.stop();
    sequencer.shutdown();
  }
}
