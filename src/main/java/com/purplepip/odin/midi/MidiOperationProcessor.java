package com.purplepip.odin.midi;

import com.purplepip.odin.OdinException;
import com.purplepip.odin.sequencer.Operation;
import com.purplepip.odin.sequencer.OperationProcessor;
import com.purplepip.odin.series.MicrosecondPositionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import java.util.PriorityQueue;

/**
 * Processor responsible for taking MIDI messages off the queue and sending them to the MIDI receivers in a timely
 * manner.  Note that according to the Java MIDI specification messages should not be sent to devices too early.  The
 * time argument in the receiver send method call is really for synchronisation.  If events are fired into a receiver
 * too early then the MIDI instrument might end up handling them early.
 */
public class MidiOperationProcessor implements OperationProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(MidiOperationProcessor.class);

    boolean exit = false;
    // TODO : Externalise configuration
    private long refreshPeriod = 10;
    private long forwardPollingTime = refreshPeriod * 1000 * 5;
    private PriorityQueue<MidiMessageEvent> queue = new PriorityQueue<>(127, new MidiMessageComparator());
    private MicrosecondPositionProvider microsecondPositionProvider;
    private MidiDevice device;

    public MidiOperationProcessor(MicrosecondPositionProvider microsecondPositionProvider, MidiDevice device) {
        if (microsecondPositionProvider == null) {
            throw new RuntimeException("MicrosecondPositionProvider must not be null");
        }
        this.microsecondPositionProvider = microsecondPositionProvider;
        if (device == null) {
            throw new RuntimeException("Device must not be null");
        }
        this.device = device;
    }

    public void send(Operation operation, long time) throws OdinException {
        MidiMessageEvent midiMessageEvent = null;
        try {
            midiMessageEvent = new MidiMessageEvent(operation, time);
            queue.add(midiMessageEvent);
        } catch (InvalidMidiDataException e) {
            throw new OdinException("Cannot create MIDI message", e);
        }
    }

    @Override
    public void run() {
        while (!exit) {
            MidiMessageEvent nextEvent = queue.peek();
            long microsecondPosition = microsecondPositionProvider.getMicrosecondPosition();
            while (nextEvent != null && nextEvent.getTime() < microsecondPosition + forwardPollingTime) {
                nextEvent = queue.poll();
                if (nextEvent == null) {
                    // TODO : Understand why this might have happened, and if can't reproduce then remove this branch.
                    LOG.error("Next event in queue is null, where did it go?");
                } else {
                    LOG.trace("Send message {} at time {} ; device time {}", nextEvent.getMidiMessage(),
                            nextEvent.getTime(), microsecondPosition);
                    try {
                        device.getReceiver().send(nextEvent.getMidiMessage(), nextEvent.getTime());
                    } catch (MidiUnavailableException e) {
                        LOG.error("Cannot send midi message {}", nextEvent.getMidiMessage());
                    }
                }
            }
            try {
                Thread.sleep(refreshPeriod);
            } catch (InterruptedException e) {
                LOG.error("Thread interrupted", e);
            }

        }
    }

    @Override
    public void stop() {
        exit = true;
    }
}
