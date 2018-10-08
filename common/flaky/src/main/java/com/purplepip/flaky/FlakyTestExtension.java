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

package com.purplepip.flaky;

import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlakyTestExtension implements TestTemplateInvocationContextProvider,
    TestExecutionExceptionHandler {
  private static final Namespace namespace = Namespace.create("com", "purplepip", "flaky");
  private static final Logger LOG = LoggerFactory.getLogger(FlakyTestExtension.class);

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable) {
    LOG.debug("Handling flaky test exception {} : {}", throwable.getMessage(),
        context.getRequiredTestMethod().toString());
    Attempts attempts = context.getStore(namespace)
        .get(context.getRequiredTestMethod().toString(), Attempts.class);
    context.publishReportEntry("flaky exception handled", throwable.getMessage());
    if (attempts != null) {
      attempts.thrown(throwable);
    } else {
      LOG.warn("Cannot find attempts in context");
    }
  }

  @Override
  public boolean supportsTestTemplate(ExtensionContext extensionContext) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
      ExtensionContext context) {
    return stream(spliteratorUnknownSize(attempts(context), ORDERED), false);
  }

  private Attempts attempts(ExtensionContext context) {
    Method method = context.getRequiredTestMethod();
    FlakyTest flakyTest = findAnnotation(method, FlakyTest.class).orElseThrow();
    Attempts retries = new Attempts(flakyTest.value());
    context.getStore(namespace).put(method.toString(), retries);
    return retries;
  }

  /**
   * Attempts iteration.
   */
  private static class Attempts implements Iterator<TestTemplateInvocationContext> {
    private static final Logger LOG = LoggerFactory.getLogger(Attempts.class);
    private int attemptCount = 0;
    private int maxAttempts;
    private final List<Throwable> exceptions;

    private Attempts(int maxAttempts) {
      this.maxAttempts = maxAttempts;
      exceptions = new ArrayList<>();
    }

    void thrown(Throwable throwable) {
      exceptions.add(throwable);
      if (exceptions.size() == maxAttempts) {
        fail("Flaked out");
      }
    }

    @Override
    public boolean hasNext() {
      return areAllFailures() && areMoreAttemptsAllowed();
    }

    private boolean areAllFailures() {
      return attemptCount == exceptions.size();
    }

    private boolean areMoreAttemptsAllowed() {
      return attemptCount < maxAttempts;
    }

    @Override
    public TestTemplateInvocationContext next() {
      attemptCount++;
      return new AttemptTestInvocationContext();
    }
  }
}
