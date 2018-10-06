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

package com.purplepip.odin.integration;

import static org.junit.Assert.assertEquals;

import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import java.util.concurrent.TimeUnit;
import lombok.extern.java.Log;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

@Log
public class StepDefinitions {
  private static final String DEFAULT_ROOT = "http://localhost:8080";

  private WebDriver driver;

  private String root = DEFAULT_ROOT;

  /**
   * Before scenario.
   */
  @Before
  public void beforeScenario() {
    driver = newHtmlUnitDriver();
    driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
    driver.manage().timeouts().setScriptTimeout(5, TimeUnit.SECONDS);
  }

  private WebDriver newHtmlUnitDriver() {
    return new HtmlUnitDriver();
  }

  private WebDriver newChromeDriver() {
    final ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.setBinary("/usr/local/bin/chromedriver");
    chromeOptions.addArguments("start-maximized");
    chromeOptions.addArguments("disable-infobars");
    chromeOptions.addArguments("--headless");
    chromeOptions.addArguments("--disable-extensions");
    chromeOptions.addArguments("--disable-gpu");
    chromeOptions.addArguments("--disable-dev-shm-usage");
    chromeOptions.addArguments("--no-sandbox");
    return new ChromeDriver(chromeOptions);
  }

  /**
   * Visit home.
   */
  @When("^I visit home$")
  public void visitHome() {
    driver.get(root + "/web/app");
  }

  /**
   * Page OK.
   */
  @Then("page OK")
  public void pageOk() {
    assertEquals("sequencer : odin", driver.getTitle());
  }
}
