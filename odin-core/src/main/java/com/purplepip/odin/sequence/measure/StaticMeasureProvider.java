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

package com.purplepip.odin.sequence.measure;

import com.purplepip.odin.sequence.Tock;

/**
 * A simple static measure provider, e.g. for music this is a static time signature
 */
public class StaticMeasureProvider implements MeasureProvider {
  private int beatsPerMeasure;

  public StaticMeasureProvider(int beatsPerMeasure) {
    this.beatsPerMeasure = beatsPerMeasure;
  }

  @Override
  public long getMeasureCountForTock(Tock tock) {
    return tock.getCount() * tock.getTick().getDenominator()
        / (beatsPerMeasure * tock.getTick().getNumerator());
  }

  @Override
  public int getBeatsInThisMeasure(Tock tock) {
    return beatsPerMeasure;
  }

  @Override
  public long getTickPositionInThisMeasure(Tock tock) {
    return tock.getCount() % (beatsPerMeasure * tock.getTick().getDenominator()
        / tock.getTick().getNumerator());
  }
}
