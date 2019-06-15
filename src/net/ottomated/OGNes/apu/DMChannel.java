package net.ottomated.OGNes.apu;

import net.ottomated.OGNes.Apu;

public class DMChannel {
    public enum Mode {
        NORMAL, LOOP, IRQ
    }

    private Apu apu;
    private boolean isEnabled;
    private boolean hasSample;
    private boolean irqGenerated;
    public Mode playMode;
    private int dmaFrequency;
    private int dmaCounter;
    private int deltaCounter;
    private int playStartAddress;
    private int playAddress;
    private int playLength;
    private int playLengthCounter;
    private int sample;
    private int dacLsb;
    private int shiftCounter;
    private int reg4012;
    private int reg4013;
    private int data;

    public DMChannel(Apu apu) {
        this.apu = apu;
        reset();
    }

    public void reset() {
        isEnabled = false;
        irqGenerated = false;
        playMode = Mode.NORMAL;
        dmaFrequency = 0;
        dmaCounter = 0;
        deltaCounter = 0;
        playStartAddress = 0;
        playAddress = 0;
        playLength = 0;
        playLengthCounter = 0;
        sample = 0;
        dacLsb = 0;
        shiftCounter = 0;
        reg4012 = 0;
        reg4013 = 0;
        data = 0;
    }
    public int getIrqStatus() {
        return irqGenerated ? 1 : 0;
    }
    public int getLengthStatus() {
        return playLengthCounter == 0;
    }
}
