package com.purplepip.odin.midi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.*;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Report information on the midi system.
 */
public class MidiSystemHelper {
    private static final Logger LOG = LoggerFactory.getLogger(MidiSystemHelper.class);

    public MidiSystemHelper logInfo() {
        Arrays.stream(MidiSystem.getMidiDeviceInfo()).collect(Collectors.toList()).forEach((info) ->
                LOG.info("MIDI device info : " + info.getVendor() + " ; " + info.getName() + " ; " + info.getDescription()));
        return this;
    }

    public MidiSystemHelper logInstruments() {
        Synthesizer synthesizer;
        try {
            synthesizer = MidiSystem.getSynthesizer();
            if (synthesizer != null){
                Arrays.stream(synthesizer.getLoadedInstruments())
                        .collect(Collectors.toList()).forEach((instrument) ->
                        LOG.debug("Synthesiser instruments (loaded) : " + instrument.getName()));

                Arrays.stream(synthesizer.getAvailableInstruments())
                        .collect(Collectors.toList()).forEach((instrument) ->
                        LOG.debug("Synthesiser instruments : " + instrument.getName()));
            }
        } catch (MidiUnavailableException e) {
            LOG.error("Cannot get synthesizer", e);
        }
        return this;
    }

    public MidiDevice findMidiDeviceByName(String name) throws MidiException {
        return findMidiDeviceByName(name, false);
    }

    public MidiDevice findMidiDeviceByName(String name, boolean exceptionOnNotFound) throws MidiException {
        MidiDevice midiDevice = findMidiDeviceByNameInternal(name, exceptionOnNotFound);
        if (midiDevice != null) {
            LOG.info("Found MIDI device : " + name + " ; " + midiDevice.getClass().getName());
            try {
                midiDevice.open();
            } catch (MidiUnavailableException e) {
                throw new MidiException(e);
            }
        }
        return midiDevice;
    }

    private MidiDevice findMidiDeviceByNameInternal(String name, boolean exceptionOnNotFound) throws MidiException {
        for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
            if (info.getName().equals(name)) {
                try {
                    return MidiSystem.getMidiDevice(info);
                } catch (MidiUnavailableException e) {
                    throw new MidiException(e);
                }
            }
        }
        if (exceptionOnNotFound) {
            throw new MidiException("Cannot find midi device " + name);
        }
        return null;
    }

    public Instrument findInstrumentByName(String name) throws MidiUnavailableException {
        for (Instrument instrument : MidiSystem.getSynthesizer().getAvailableInstruments()) {
            if (instrument.getName().equals(name)) {
                return instrument;
            }
        }
        return null;
    }
}
