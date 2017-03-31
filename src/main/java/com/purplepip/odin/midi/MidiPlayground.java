package com.purplepip.odin.midi;

import com.purplepip.odin.server.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.*;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * MIDI playground.
 */
public class MidiPlayground {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String [] args) {
        MidiPlayground playground = new MidiPlayground();
        playground.dumpInfo();
    }

    private void dumpInfo() {
        Arrays.stream(MidiSystem.getMidiDeviceInfo()).collect(Collectors.toList()).forEach((temp) ->
                LOG.info("MIDI device info : " + temp.getDescription()));

        ShortMessage middleC = new ShortMessage();
        ShortMessage middleD = new ShortMessage();
        try {
            middleC.setMessage(ShortMessage.NOTE_ON, 1, 60, 93);
            middleD.setMessage(ShortMessage.NOTE_ON, 1, 62, 60);
        } catch (InvalidMidiDataException e) {
            LOG.error("Cannot create short message", e);
            return;
        }

        Receiver receiver;
        try {
            receiver = MidiSystem.getReceiver();
            //MidiSystem.getSynthesizer().getTransmitter().setReceiver(receiver);
            LOG.info("Receiver : " + receiver);
            receiver.send(middleC, 100);
            LOG.info("Sent note");
        } catch (MidiUnavailableException e) {
            LOG.error("Cannot get MIDI receiver", e);
        }

        Transmitter transmitter;
        try {
            transmitter = MidiSystem.getTransmitter();
            LOG.info("Transmitter : " + transmitter);
        } catch (MidiUnavailableException e) {
            LOG.error("Cannot get MIDI transmitter", e);
        }

        Sequencer sequencer;
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            LOG.info("Sequencer : " + sequencer + " ; " + getDetails(sequencer));
        } catch (MidiUnavailableException e) {
            LOG.error("Cannot get MIDI sequencer", e);
        }

        Synthesizer synthesizer;
        try {
            synthesizer = MidiSystem.getSynthesizer();
            LOG.info("Synthesizer : " + synthesizer + " ; " + getDetails(synthesizer));
            synthesizer.open();
            synthesizer.getReceiver().send(middleC, -1);
            synthesizer.getReceiver().send(middleD, -1);
            LOG.info("Sent notes to synthesizer");
        } catch (MidiUnavailableException e) {
            LOG.error("Cannot get MIDI synthesizer", e);
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            LOG.error("Sleep interrupted", e);
        }
        LOG.info("... stopping");
    }

    private String getDetails(MidiDevice midiDevice) {
        return "number of transmitters = " + midiDevice.getTransmitters().size()
                + "; number of receivers = " + midiDevice.getReceivers().size();
    }
}
