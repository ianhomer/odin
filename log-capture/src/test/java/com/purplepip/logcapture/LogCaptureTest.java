package com.purplepip.logcapture;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log capture test.
 */
public class LogCaptureTest {
  private static final Logger LOG = LoggerFactory.getLogger(LogCaptureTest.class);

  @Test
  public void testCaptureDefault() {
    try (LogCaptor captor = new LogCapture().start()) {
      LOG.info("testCaptureDefault: Test info message");
      assertEquals(1, captor.size());
      LOG.debug("testCaptureDefault: Test debug message");
      assertEquals(1, captor.size());
    }
  }

  @Test
  public void testCaptureWithPassthrough() {
    try (LogCaptor captor = new LogCapture().withPassThrough().start()) {
      LOG.info("testCaptureDefault: Test info message");
      assertEquals(1, captor.size());
      LOG.debug("testCaptureDefault: Test debug message");
      assertEquals(1, captor.size());
    }
  }

  @Test
  public void testCapture() {
    try (LogCaptor captor = new LogCapture().debug().from(LogCaptureTest.class).start()) {
      LOG.info("testCapture : Test info message : {}", "parameter-value");
      assertEquals(1, captor.size());
      assertEquals("testCapture : Test info message : parameter-value",
          captor.getMessage(0));
      LOG.debug("testCapture : Test debug message");
      assertEquals(2, captor.size());
    }
  }

  @Test
  public void testCaptureFromStringCategory() {
    try (LogCaptor captor = new LogCapture().debug()
        .from(LogCaptureTest.class.getName()).start()) {
      LOG.info("testCapture : Test info message");
      assertEquals(1, captor.size());
      LOG.debug("testCapture : Test debug message");
      assertEquals(2, captor.size());
    }
  }

  @Test
  public void testCaptureInfo() {
    try (LogCaptor captor = new LogCapture().from(LogCaptureTest.class).start()) {
      LOG.info("testCaptureInfo : Test info message");
      assertEquals(1, captor.size());
      LOG.debug("testCaptureInfo : Test debug message");
      assertEquals(1, captor.size());
    }
  }

  @Test
  public void testCaptureError() {
    try (LogCaptor captor = new LogCapture().from(LogCaptureTest.class).start()) {
      LOG.error("testCaptureError : Test info message");
      assertEquals(1, captor.size());
      assertEquals("testCaptureError : Test info message", captor.getMessage(0));
    }
  }
}