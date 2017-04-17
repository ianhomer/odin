package com.purplepip.odin.midi;

import com.purplepip.odin.common.OdinException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.MidiDevice;
import java.util.HashSet;
import java.util.Set;

/**
 * Provider of a MIDI device
 */
public class MidiDeviceWrapper {
    private static final Logger LOG = LoggerFactory.getLogger(MidiDeviceWrapper.class);

    private MidiDevice device;
    private MidiDeviceScanner scanner;

    public MidiDeviceWrapper() {
        this(false);
    }

    public MidiDeviceWrapper(boolean scan) {
        if (scan) {
            scanner = new MidiDeviceScanner();
            Thread thread = new Thread(scanner);
            thread.start();
            while (device == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    LOG.error("Thread interrupted", e);
                }
            }
        } else {
            findDevice();
        }
    }

    public MidiDeviceWrapper(MidiDevice device) {
        this.device = device;
    }

    public MidiDevice getDevice() {
        return device;
    }

    public void close() {
        scanner.stop();
    }

    protected void findDevice() {
        try {
            device = new MidiSystemHelper().getInitialisedDevice();
        } catch (OdinException e) {
            LOG.error("Cannot initialise MIDI device", e);
        }
    }

    class MidiDeviceScanner implements Runnable {
        private boolean exit;
        private Set<MidiDevice.Info> knownMidiDevices = new HashSet<>();

        @Override
        public void run() {
            while (!exit) {
                // TODO : Work out if MIDI connections can be refreshed, it might be a challenge on OSX
                // see http://stackoverflow.com/questions/3752352/java-sound-api-scanning-for-midi-devices
                // https://bugs.openjdk.java.net/browse/JDK-8139153
                LOG.debug("Scanning MIDI devices");

                Set<MidiDevice.Info> midiDevices = new MidiSystemHelper().getMidiDeviceInfos();
                if (!midiDevices.equals(knownMidiDevices) || device == null) {
                    LOG.debug("Refreshing MIDI device");
                    knownMidiDevices = midiDevices;
                    findDevice();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LOG.error("Thread interrupted", e);
                }
            }
        }

        public void stop() {
            exit = true;
        }


    }
}
