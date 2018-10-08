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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlakyTestExtension implements TestTemplateInvocationContextProvider,
    TestExecutionExceptionHandler, BeforeTestExecutionCallback {
  private static final Namespace namespace = Namespace.create("com", "purplepip", "flaky");
  private static final Logger LOG = LoggerFactory.getLogger(FlakyTestExtension.class);

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable) {
    LOG.info("Handling exception {} : {}", throwable.getMessage(),
        context.getRequiredTestMethod().toString());
    Retries attempts = context.getStore(namespace)
        .get(context.getRequiredTestMethod().toString(), Retries.class);
    if (attempts != null) {
      attempts.thrown(throwable);
    } else {
      LOG.warn("Cannot find attempts in context");
    }
  }

  @Override
  public boolean supportsTestTemplate(ExtensionContext extensionContext) {
    LOG.info("Testing if supports test template");
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
      ExtensionContext context) {
    LOG.info("Providing invocation context");
    return stream(spliteratorUnknownSize(retry(context), ORDERED), false);
  }

  private Retries retry(ExtensionContext context) {
    Retries retries = new Retries();
    context.getStore(namespace).put(context.getRequiredTestMethod().toString(), retries);
    return retries;
  }

  @Override
  public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
    LOG.info("Before test");
  }

  private static class Retries implements Iterator<TestTemplateInvocationContext> {
    private static final Logger LOG = LoggerFactory.getLogger(Retries.class);
    private int attemptCount = 0;
    private int maxRetries;
    private final List<Throwable> exceptions;

    private Retries() {
      maxRetries = 1;
      exceptions = new ArrayList<>();
    }

    public void thrown(Throwable throwable) {
      LOG.info("Recorded throwable");
      exceptions.add(throwable);
      if (exceptions.size() == maxRetries) {
        throw new AssertionError("Flaked out");
      }
    }

    @Override
    public boolean hasNext() {
      return attemptCount == exceptions.size() && attemptCount < maxRetries;
    }

    @Override
    public TestTemplateInvocationContext next() {
      attemptCount++;
      return new AttemptTestInvocationContext();
    }
  }
}
