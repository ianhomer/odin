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

import static com.purplepip.flaky.ExceptionUtils.extractMessage;
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
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlakyTestExtension implements TestTemplateInvocationContextProvider,
    TestExecutionExceptionHandler, AfterEachCallback,
    AfterTestExecutionCallback {
  private static final Namespace NAMESPACE = Namespace.create("com", "purplepip", "flaky");
  private static final Logger LOG = LoggerFactory.getLogger(FlakyTestExtension.class);

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable) {
    Attempts attempts = getAttempts(context);
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
    return stream(spliteratorUnknownSize(newAttempts(context), ORDERED), false);
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
  }

  @Override
  public void afterEach(ExtensionContext context) {
  }

  private Attempts newAttempts(ExtensionContext context) {
    Method method = context.getRequiredTestMethod();
    FlakyTest flakyTest = findAnnotation(method, FlakyTest.class).orElseThrow();
    Attempts attempts = new Attempts(method, flakyTest.value());
    getStore(context).put(method.getName(), attempts);
    return attempts;
  }

  private Store getStore(ExtensionContext context) {
    return context.getStore(NAMESPACE);
  }

  private Attempts getAttempts(ExtensionContext context) {
    return getStore(context).get(context.getRequiredTestMethod().getName(), Attempts.class);
  }

  /**
   * Attempts iteration.
   */
  private static class Attempts implements Iterator<TestTemplateInvocationContext> {
    private int attemptCount = 0;
    private int maxAttempts;
    private Method method;
    private final List<Execution> executions;

    private Attempts(Method method, int maxAttempts) {
      this.method = method;
      this.maxAttempts = maxAttempts;
      executions = new ArrayList<>();
    }

    void thrown(Throwable throwable) {
      executions.add(new Execution(throwable, attemptCount));
      if (executions.size() == maxAttempts) {
        fail("Flaked out after " + maxAttempts + " attempts" + report());
      }
    }

    private String report() {
      StringBuilder sb = new StringBuilder();
      executions.forEach(execution -> sb.append(
          String.format("\n  Run %s: %s: %s", execution.getIndex(),
              extractMessage(execution.getThrowable()), method.getName())));
      return sb.toString();
    }

    @Override
    public boolean hasNext() {
      return areAllFailures() && areMoreAttemptsAllowed();
    }

    private boolean areAllFailures() {
      return attemptCount == executions.size();
    }

    private boolean areMoreAttemptsAllowed() {
      return attemptCount < maxAttempts;
    }

    @Override
    public TestTemplateInvocationContext next() {
      attemptCount++;
      return new AttemptTestInvocationContext(method);
    }
  }

  private static class Execution {
    private Throwable throwable;
    private int index;

    Execution(Throwable throwable, int index) {
      this.throwable = throwable;
      this.index = index;
    }

    Throwable getThrowable() {
      return throwable;
    }

    int getIndex() {
      return index;
    }

  }
}
