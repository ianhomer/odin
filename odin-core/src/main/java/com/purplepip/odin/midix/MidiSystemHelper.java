package com.purplepip.odin.midix;

import com.purplepip.odin.common.BeanUtils;
import com.purplepip.odin.common.OdinException;
import com.sun.media.sound.JDK13Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.*;
import javax.sound.midi.spi.MidiDeviceProvider;
import java.util.*;

/**
 * Report information on the midi system.
 */
public class MidiSystemHelper {
    private static final Logger LOG = LoggerFactory.getLogger(MidiSystemHelper.class);

    public MidiSystemHelper logInfo() {
        for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
            LOG.info("MIDI device info : {} ; {} ; {}", info.getVendor(), info.getName(), info.getDescription());
        }

        List<?> list = JDK13Services.getProviders(MidiDeviceProvider.class);
        for (Object midiDeviceProvider : list) {
            if (midiDeviceProvider instanceof MidiDeviceProvider) {
                log((MidiDeviceProvider) midiDeviceProvider);
            }
        }

        return this;
    }

    public void log(MidiDeviceProvider midiDeviceProvider) {
        new BeanUtils().dumpStaticMethodResponse(midiDeviceProvider.getClass(), "nGetNumDevices");
        for (MidiDevice.Info info : midiDeviceProvider.getDeviceInfo()) {
            LOG.info("{} : {}", midiDeviceProvider.getClass(), info);
        }
    }

    public Set<MidiDevice.Info> getMidiDeviceInfos() {
        Set<MidiDevice.Info> infos = new HashSet<>();
        for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
            infos.add(info);
        }
        return infos;
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

    public MidiDevice getInitialisedDevice() throws OdinException {
        // TODO : Externalise and prioritise external MIDI devices to connect to.
        MidiDevice device = new MidiSystemHelper().findMidiDeviceByName("MidiMock IN");
        if (device == null) {
            device = new MidiSystemHelper().findMidiDeviceByName("Gervill");
        }
        LOG.debug("MIDI device : {}", device);

        if ("Gervill".equals(device.getDeviceInfo().getName())) {
            LOG.debug("Initialising internal synthesizer");
            try {
                // TODO : Externalise configuration - 41 is strings in internal Java engine
                device.getReceiver().send(new ShortMessage(ShortMessage.PROGRAM_CHANGE, 0, 41, 0),
                        -1);
            } catch (MidiUnavailableException | InvalidMidiDataException e) {
                LOG.error("Cannot change synthesizer instruments", e);
            }
        }
        return device;
    }}
