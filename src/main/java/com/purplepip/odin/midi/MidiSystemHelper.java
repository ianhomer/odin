package com.purplepip.odin.midi;

import com.purplepip.odin.OdinException;
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
            if (synthesizer != null) {
                Instrument[] instruments = synthesizer.getLoadedInstruments();
                for (int i = 0 ; i < instruments.length ; i++) {
                    LOG.debug("Synthesiser instruments (loaded) : % %", i, instruments[i].getName());
                }
                instruments = synthesizer.getAvailableInstruments();
                for (int i = 0 ; i < instruments.length ; i++) {
                    LOG.debug("Synthesiser instruments : {} {}", i, instruments[i].getName());
                }
                MidiChannel[] midiChannels = synthesizer.getChannels();
                for (int i = 0 ; i < midiChannels.length ; i++) {
                    LOG.debug("Synthesiser channels : {} {}", i, midiChannels[i].getProgram());
                }
            }
        } catch (MidiUnavailableException e) {
            LOG.error("Cannot get synthesizer", e);
        }
        return this;
    }

    public MidiDevice findMidiDeviceByName(String name) throws OdinException {
        return findMidiDeviceByName(name, false);
    }

    public MidiDevice findMidiDeviceByName(String name, boolean exceptionOnNotFound) throws OdinException {
        MidiDevice midiDevice = findMidiDeviceByNameInternal(name, exceptionOnNotFound);
        if (midiDevice != null) {
            LOG.info("Found MIDI device : " + name + " ; " + midiDevice.getClass().getName());
            try {
                midiDevice.open();
            } catch (MidiUnavailableException e) {
                throw new OdinException(e);
            }
        }
        return midiDevice;
    }

    private MidiDevice findMidiDeviceByNameInternal(String name, boolean exceptionOnNotFound) throws OdinException {
        for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
            if (info.getName().equals(name)) {
                try {
                    return MidiSystem.getMidiDevice(info);
                } catch (MidiUnavailableException e) {
                    throw new OdinException(e);
                }
            }
        }
        if (exceptionOnNotFound) {
            throw new OdinException("Cannot find midi device " + name);
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
