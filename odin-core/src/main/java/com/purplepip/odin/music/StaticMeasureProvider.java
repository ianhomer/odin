package com.purplepip.odin.music;

import com.purplepip.odin.series.Tock;

/**
 * A simple static measure provider, e.g. static time signature
 */
public class StaticMeasureProvider implements MeasureProvider {
  private int beatsPerBar;

  public StaticMeasureProvider(int beatsPerBar) {
    this.beatsPerBar = beatsPerBar;
  }

  @Override
  public long getMeasureCountForTock(Tock tock) {
    return tock.getCount() * tock.getTick().getDenominator() / (beatsPerBar * tock.getTick().getNumerator());
  }

  @Override
  public int getBeatsInThisMeasure(Tock tock) {
    return beatsPerBar;
  }

  @Override
  public long getTickPositionInThisMeasure(Tock tock) {
    return tock.getCount() % (beatsPerBar * tock.getTick().getDenominator() / tock.getTick().getNumerator());
  }
}
