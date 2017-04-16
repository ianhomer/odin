package com.purplepip.odin.midi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.util.List;

/**
 * The default Odin Midi devie
 */
@Component
public class DefaultOdinMidiDevice implements OdinMidiDevice {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultOdinMidiDevice.class);

    @PostConstruct
    public void postConstruct() {
        LOG.info("Default midi device initialised");
    }

    @Override
    public Info getDeviceInfo() {
        return null;
    }

    @Override
    public void open() throws MidiUnavailableException {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public long getMicrosecondPosition() {
        return 0;
    }

    @Override
    public int getMaxReceivers() {
        return 0;
    }

    @Override
    public int getMaxTransmitters() {
        return 0;
    }

    @Override
    public Receiver getReceiver() throws MidiUnavailableException {
        return null;
    }

    @Override
    public List<Receiver> getReceivers() {
        return null;
    }

    @Override
    public Transmitter getTransmitter() throws MidiUnavailableException {
        return null;
    }

    @Override
    public List<Transmitter> getTransmitters() {
        return null;
    }
}
