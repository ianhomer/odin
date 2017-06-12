/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
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

package com.purplepip.odin.sequence;

/**
 * Event indicating that no value was found up to the given time that was scanned.  This
 * event signal can be used to allow a tock of a runtime sequence to be moved forward to this
 * point so that future scans for values start from this point.
 */
public class ScanForwardEvent<A> implements Event<A> {
  private long time;

  public ScanForwardEvent(long time) {
    this.time = time;
  }

  @Override
  public A getValue() {
    return null;
  }

  @Override
  public long getTime() {
    return time;
  }
}
