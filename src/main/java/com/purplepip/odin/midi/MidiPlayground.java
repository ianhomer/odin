package com.purplepip.odin.midi;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Midi server.
 */
public class MidiPlayground {
    public static void main(String [] args) {
        MidiPlayground playground = new MidiPlayground();
        playground.dumpInfo();
    }

    private void dumpInfo() {
        Arrays.stream(MidiSystem.getMidiDeviceInfo()).collect(Collectors.toList()).forEach((temp) -> {
            System.out.println("Midi device info : " + temp.getDescription());
        });

        Receiver receiver = null;
        try {
            receiver = MidiSystem.getReceiver();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
        System.out.println("Receiver : " + receiver);

        Transmitter transmitter = null;
        try {
            transmitter = MidiSystem.getTransmitter();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        System.out.println("Transmitter : " + transmitter);
    }
}
