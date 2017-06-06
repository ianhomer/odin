package com.purplepip.odin.experiments;

import com.purplepip.odin.midix.MidiSystemHelper;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MIDI playground.
 */
public class MidiPlayground {
  private static final Logger LOG = LoggerFactory.getLogger(MidiPlayground.class);

  public static void main(String[] args) {
    MidiPlayground playground = new MidiPlayground();
    playground.dumpInfo();
  }

  private void dumpInfo() {
    new MidiSystemHelper().logInfo();

    ShortMessage middleC = new ShortMessage();
    ShortMessage middleD = new ShortMessage();
    ShortMessage middleCOff = new ShortMessage();
    ShortMessage middleDOff = new ShortMessage();
    try {
      middleC.setMessage(ShortMessage.NOTE_ON, 1, 60, 93);
      middleD.setMessage(ShortMessage.NOTE_ON, 1, 72, 60);
      middleCOff.setMessage(ShortMessage.NOTE_OFF, 1, 60, 93);
      middleDOff.setMessage(ShortMessage.NOTE_OFF, 1, 72, 60);
    } catch (InvalidMidiDataException e) {
      LOG.error("Cannot create short message", e);
      return;
    }

    Receiver receiver = null;
    try {
      receiver = MidiSystem.getReceiver();
      LOG.info("Receiver : " + receiver);
      receiver.send(middleD, -1);
      receiver.send(middleDOff, -1);
      LOG.info("Sent note");
    } catch (MidiUnavailableException e) {
      LOG.error("Cannot get MIDI receiver", e);
    } finally {
      if (receiver != null) {
        receiver.close();
      }
    }

    Transmitter transmitter = null;
    try {
      transmitter = MidiSystem.getTransmitter();
      LOG.info("Transmitter : " + transmitter);
    } catch (MidiUnavailableException e) {
      LOG.error("Cannot get MIDI transmitter", e);
    } finally {
      if (transmitter != null) {
        transmitter.close();
      }
    }


    Sequencer sequencer = null;
    try {
      sequencer = MidiSystem.getSequencer();
      sequencer.open();
      LOG.info("Sequencer : " + sequencer + " ; " + getDetails(sequencer));
    } catch (MidiUnavailableException e) {
      LOG.error("Cannot get MIDI sequencer", e);
    } finally {
      if (sequencer != null) {
        sequencer.close();
      }
    }

    Synthesizer synthesizer = null;
    try {
      synthesizer = MidiSystem.getSynthesizer();
      LOG.info("Synthesizer : " + synthesizer + " ; " + getDetails(synthesizer));
      synthesizer.open();
      synthesizer.getReceiver().send(middleC, -1);
      synthesizer.getReceiver().send(middleD, -1);
      LOG.info("Sent notes to synthesizer");
    } catch (MidiUnavailableException e) {
      LOG.error("Cannot get MIDI synthesizer", e);
    } finally {
      if (synthesizer != null) {
        synthesizer.close();
      }

    }

    LOG.info("... stopping");
  }

  private String getDetails(MidiDevice midiDevice) {
    return "number of transmitters = " + midiDevice.getTransmitters().size()
        + "; number of receivers = " + midiDevice.getReceivers().size();
  }
}
