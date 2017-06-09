/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * <p>Utilities to capture log output.  Typically used within test cases where log messages
 * are expected, but noisy, perhaps during error handling validation.</p>
 *
 * <p>For example the following example captures the debug log messages from the code inside the
 * try block.  These log messages are NOT propagated to the logging system, but are captured
 * so that they could be validated if desired.</p>
 *
 * <pre>
 * {@code
 * try (LogCaptor captor = new LogCapture().debug().from(LogCaptureTest.class).start()) {
 *   LOG.info("testCapture : Test info message");
 *   assertEquals(1, captor.size());
 *   LOG.debug("testCapture : Test debug message");
 *   assertEquals(2, captor.size());
 * }
 * }
 * </pre>
 */
package com.purplepip.logcapture;
