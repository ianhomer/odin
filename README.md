# odin

Harmonising Chaos.

# Background

Maintains ingested environmental state and streams live patterns that can be experienced by our human senses.

The first milestone of this software will focus on driving live music performance via MIDI.  Out of this specific
use case will emerge an abstract concept to drive other outputs and consume other inputs.

# Example

Live music generated by environmental influences such as weather, knobs and keyboards.

# Build

    mvn package
    java -jar target/odin-1.0-SNAPSHOT.jar

# Access Server

Root

    http://localhost:8080/

Health check

    http://localhost:8080/health

# Manual Testing

Run the MIDI playground script

    mvn exec:java

Lots of combinations and choice exist to generate and receive MIDI signals, for basics I use :

* A Korg microKEY to act as MIDI in signal.  Midi Mock (in Mac App Store) is a software keyboard that drives MIDI in
* Ableton Live as a receiver for MIDI out.

For performance I use a Nord Electro 5 and Nord Lead 4.
