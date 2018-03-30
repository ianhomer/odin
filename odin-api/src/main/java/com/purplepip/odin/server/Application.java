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

package com.purplepip.odin.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;

/**
 * Odin Application.
 */
@SpringBootApplication
@ComponentScan({"com.purplepip.odin.server"})
public class Application {
  private static final Logger LOG = LoggerFactory.getLogger(Application.class);

  /**
   * Odin server start up.
   *
   * @param args arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  /**
   * Commands to execute on start up.
   *
   * @param ctx spring application context
   * @return command line runner
   */
  @Bean
  @Order(9)
  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    return args -> LOG.info("Odin API Started.");
  }
}

