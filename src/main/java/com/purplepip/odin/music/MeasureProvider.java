package com.purplepip.odin.music;

import com.purplepip.odin.series.Tock;

/**
 * Intelligence on what measure we are in.
 */
public interface MeasureProvider {
    /**
     * What measure is the tock in?
     */
    long getMeasureCountForTock(Tock tock);

    /**
     * How many beats are in the measure for the given tock?
     * @param tock
     * @return
     */
    int getBeatsInThisMeasure(Tock tock);

    /**
     * What position in the measure is this tock?  0 => start of the measure.  Note that a tick might be higher
     * resolution than a single beat, e.g. it could be a half beat or a triplet
     *
     * @param tock
     * @return
     */
    long getTickPositionInThisMeasure(Tock tock);
}
