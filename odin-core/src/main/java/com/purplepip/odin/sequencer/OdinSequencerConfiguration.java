package com.purplepip.odin.sequencer;

import com.google.common.collect.Lists;
import com.purplepip.odin.sequence.BeatsPerMinute;
import com.purplepip.odin.sequence.MicrosecondPositionProvider;
import com.purplepip.odin.sequence.StaticBeatsPerMinute;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequence.measure.StaticMeasureProvider;

import java.util.ArrayList;

/**
 * Odin Sequencer Configuration.
 */
public class OdinSequencerConfiguration {
  private BeatsPerMinute beatsPerMinute = new StaticBeatsPerMinute(140);
  private MeasureProvider measureProvider = new StaticMeasureProvider(4);
  private OperationReceiver operationReceiver;
  private MicrosecondPositionProvider microsecondPositionProvider;
  private boolean isLoggingOperationReceiverEnabled = true;

  public OdinSequencerConfiguration setBeatsPerMinute(BeatsPerMinute beatsPerMinute) {
    this.beatsPerMinute = beatsPerMinute;
    return this;
  }

  public OdinSequencerConfiguration setMeasureProvider(MeasureProvider measureProvider) {
    this.measureProvider = measureProvider;
    return this;
  }

  /**
   * Set operation receiver.
   *
   * @param operationReceiver operation receiver
   * @return this configuration
   */
  public OdinSequencerConfiguration setOperationReceiver(OperationReceiver operationReceiver) {
    if (isLoggingOperationReceiverEnabled) {
      if (operationReceiver instanceof OperationReceiverCollection) {
        ArrayList<OperationReceiver> operationReceiverList =
            new ArrayList<>(Lists.newArrayList(operationReceiver));
        operationReceiverList.add(new LoggingOperationReceiver());
        this.operationReceiver = new OperationReceiverCollection(operationReceiverList);
      } else {
        this.operationReceiver = new OperationReceiverCollection(operationReceiver,
            new LoggingOperationReceiver());
      }
    } else {
      this.operationReceiver = operationReceiver;
    }
    return this;
  }

  public OdinSequencerConfiguration setMicrosecondPositionProvider(
      MicrosecondPositionProvider microsecondPositionProvider) {
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

  /**
   * Get whether logging operation receiver is automatically added.
   *
   * @return whether logging operation receiver is automatically added
   */
  public boolean isLoggingOperationReceiverEnabled() {
    return  isLoggingOperationReceiverEnabled;
  }

  /**
   * Set whether logging operation receiver is automatically added.
   *
   * @param isLoggingOperationReceiverEnabled is logging operation receiver is automatically added
   * @return this configuration
   */
  public OdinSequencerConfiguration setLoggingOperationReceiverEnabled(
      boolean isLoggingOperationReceiverEnabled) {
    this.isLoggingOperationReceiverEnabled = isLoggingOperationReceiverEnabled;
    return this;
  }
}
