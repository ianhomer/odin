package com.purplepip.odin.music;

/**
 * Default Note
 */
public class DefaultNote implements Note {
    private int number;
    private int velocity;

    public DefaultNote() {
        this(60);
    }

    public DefaultNote(int number) {
        this(number, 40);
    }

    public DefaultNote(int number, int velocity) {
        this.number = number;
        this.velocity = velocity;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public int getVelocity() {
        return velocity;
    }
}
