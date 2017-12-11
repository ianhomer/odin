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

package com.purplepip.odin.profile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Profile snapshot store.
 */
public class Snapshot {
  public List<Metric> records = new ArrayList<>();

  Snapshot() {
  }

  private Snapshot(List<Metric> records) {
    this.records.addAll(records);
  }

  public void add(Metric record) {
    records.add(record);
  }

  public Stream<Metric> getStream() {
    return records.stream();
  }

  public Stream<Metric> getSortedStream() {
    return records.stream().sorted(Comparator.comparing(record -> -record.getTime()));
  }

  public void reset() {
    records.clear();
  }

  public Snapshot copy() {
    return new Snapshot(records);
  }
}
