package com.purplepip.odin.sequencer;

import com.purplepip.odin.music.MeasureProvider;
import com.purplepip.odin.music.StaticMeasureProvider;
import com.purplepip.odin.series.BeatsPerMinute;
import com.purplepip.odin.series.MicrosecondPositionProvider;
import com.purplepip.odin.series.StaticBeatsPerMinute;

/**
 * Odin Sequencer Configuration.
 */
public class OdinSequencerConfiguration {
  private BeatsPerMinute beatsPerMinute = new StaticBeatsPerMinute(140);
  private MeasureProvider measureProvider = new StaticMeasureProvider(4);
  private OperationReceiver operationReceiver;
  private MicrosecondPositionProvider microsecondPositionProvider;

  public OdinSequencerConfiguration setBeatsPerMinute(BeatsPerMinute beatsPerMinute) {
    this.beatsPerMinute = beatsPerMinute;
    return this;
  }

  public OdinSequencerConfiguration setMeasureProvider(MeasureProvider measureProvider) {
    this.measureProvider = measureProvider;
    return this;
  }

  public OdinSequencerConfiguration setOperationReceiver(OperationReceiver operationReceiver) {
    this.operationReceiver = operationReceiver;
    return this;
  }

  public OdinSequencerConfiguration setMicrosecondPositionProvider(MicrosecondPositionProvider microsecondPositionProvider) {
    this.microsecondPositionProvider = microsecondPositionProvider;
    return this;
  }

  public BeatsPerMinute getBeatsPerMinute() {
    return beatsPerMinute;
  }

  public MeasureProvider getMeasureProvider() {
    return measureProvider;
  }

  public OperationReceiver getOperationReceiver() {
    return operationReceiver;
  }

  public MicrosecondPositionProvider getMicrosecondPositionProvider() {
    return microsecondPositionProvider;
  }
}
