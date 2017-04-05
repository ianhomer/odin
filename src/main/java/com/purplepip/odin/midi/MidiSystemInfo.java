package com.purplepip.odin.midi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.MidiSystem;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Report information on the midi system.
 */
public class MidiSystemInfo {
    private static final Logger LOG = LoggerFactory.getLogger(MidiSystemInfo.class);

    public void logInfo() {
        Arrays.stream(MidiSystem.getMidiDeviceInfo()).collect(Collectors.toList()).forEach((temp) ->
                LOG.info("MIDI device info : " + temp.getDescription()));
    }
}
