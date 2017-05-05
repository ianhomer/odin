package com.purplepip.odin.sequence.measure;

import com.purplepip.odin.sequence.Tock;

/**
 * Intelligence on what measure we are in.  For music a measure is bar, generically a measure
 * is collection of beats.
 */
public interface MeasureProvider {
  /**
   * Get the measure that the tock is in.
   *
   * @param tock Current tick
   * @return Current measure number
   */
  long getMeasureCountForTock(Tock tock);

  /**
   * Get the number of beats are in the measure for the given tock.
   *
   * @param tock current tick
   * @return beats in the current measure
   */
  int getBeatsInThisMeasure(Tock tock);

  /**
   * What position in the measure is this tock?  0 => start of the measure.  Note that a tick
   * might be higher resolution than a single beat, e.g. it could be a half beat or a triplet.
   *
   * @param tock current tick
   * @return tick position in the current measure
   */
  long getTickPositionInThisMeasure(Tock tock);
}
