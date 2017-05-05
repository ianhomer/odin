package com.purplepip.odin.sequence.measure;

import com.purplepip.odin.sequence.Tock;
import com.purplepip.odin.sequence.measure.MeasureProvider;

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
