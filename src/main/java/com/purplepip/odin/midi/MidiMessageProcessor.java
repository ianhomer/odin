package com.purplepip.odin.midi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class MidiMessageProcessor implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(MidiMessageProcessor.class);

    boolean exit = false;
    // TODO : Externalise configuration
    private long refreshPeriod = 10;
    private long forwardPollingTime = refreshPeriod * 1000 * 5;
    private PriorityQueue<MidiMessageEvent> queue = new PriorityQueue<>(127, new MidiMessageComparator());
    private MidiDevice device;

    public MidiMessageProcessor(MidiDevice device) {
        if (device == null) {
            throw new RuntimeException("Device must not be null");
        }
        this.device = device;
    }

    public void send(MidiMessage message, long time) {
        MidiMessageEvent midiMessageEvent = new MidiMessageEvent(message, time);
        queue.add(midiMessageEvent);
    }

    @Override
    public void run() {
        while (!exit) {
            MidiMessageEvent nextEvent = queue.peek();
            while (nextEvent != null && nextEvent.getTime() < device.getMicrosecondPosition() + forwardPollingTime) {
                nextEvent = queue.poll();
                if (nextEvent == null) {
                    // TODO : Understand why this might have happened, and if can't reproduce then remove this branch.
                    LOG.error("Next event in queue is null, where did it go?");
                } else {
                    LOG.debug("Send message {} at time {} ; device time {}", nextEvent.getMidiMessage(),
                            nextEvent.getTime(), device.getMicrosecondPosition());
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

    public void stop() {
        exit = true;
    }
}
