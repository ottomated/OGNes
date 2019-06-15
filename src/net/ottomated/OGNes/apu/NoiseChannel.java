package net.ottomated.OGNes.apu;

import net.ottomated.OGNes.Apu;

public class NoiseChannel {
    private Apu apu;
    public boolean isEnabled;
    public int shiftReg;
    public int progTimerCount;
    public int progTimerMax;
    public int lengthCounter;
    private boolean lengthCounterEnable;
    private boolean envDecayDisable;
    private boolean envDecayLoopEnable;
    private boolean shiftNow;
    private int envDecayRate;
    private int envDecayCounter;
    private int envVolume;
    public int masterVolume;
    public int randomBit;
    public int randomMode;
    public int sampleValue;
    public int tmp;
    public int accCount;
    public int accValue;
    private boolean envReset;

    public NoiseChannel(Apu apu) {
        this.apu = apu;
        shiftReg = 1 << 14;
        reset();
    }

    public void reset() {
        progTimerCount = 0;
        progTimerMax = 0;
        isEnabled = false;
        lengthCounter = 0;
        lengthCounterEnable = false;
        envDecayDisable = false;
        envDecayLoopEnable = false;
        shiftNow = false;
        envDecayRate = 0;
        envDecayCounter = 0;
        envVolume = 0;
        masterVolume = 0;
        shiftReg = 1;
        randomBit = 0;
        randomMode = 0;
        sampleValue = 0;
        tmp = 0;
        accCount = 1;
        accValue = 0;
    }

    public void clockLengthCounter() {
        if (lengthCounterEnable && lengthCounter > 0) {
            lengthCounter--;
            if (lengthCounter == 0) {
                updateSampleValue();
            }
        }
    }

    public void clockEnvDecay() {
        if (envReset) {
            // Reset envelope:
            envReset = false;
            envDecayCounter = envDecayRate + 1;
            envVolume = 0xf;
        } else if (--envDecayCounter <= 0) {
            // Normal handling:
            envDecayCounter = envDecayRate + 1;
            if (envVolume > 0) {
                envVolume--;
            } else {
                envVolume = envDecayLoopEnable ? 0xf : 0;
            }
        }
        if (envDecayDisable) {
            masterVolume = envDecayRate;
        } else {
            masterVolume = envVolume;
        }
        updateSampleValue();
    }

    private void updateSampleValue() {
        if (isEnabled && lengthCounter > 0) {
            sampleValue = randomBit * masterVolume;
        }
    }

    public void writeReg(int address, int value) {
        if (address == 0x400c) {
            // Volume/Envelope decay:
            envDecayDisable = (value & 0x10) == 0;
            envDecayRate = value & 0xf;
            envDecayLoopEnable = (value & 0x20) != 0;
            lengthCounterEnable = (value & 0x20) == 0;
            if (envDecayDisable) {
                masterVolume = envDecayRate;
            } else {
                masterVolume = envVolume;
            }
        } else if (address == 0x400e) {
            // Programmable timer:
            progTimerMax = apu.getNoiseWaveLength(value & 0xf);
            randomMode = value >> 7;
        } else if (address == 0x400f) {
            // Length counter
            lengthCounter = apu.getLengthMax(value & 248);
            envReset = true;
        }
        // Update:
        //updateSampleValue();
    }

    public void setEnabled(boolean value) {
        isEnabled = value;
        if (!value) {
            lengthCounter = 0;
        }
        updateSampleValue();
    }

    public int getLengthStatus() {
        return lengthCounter == 0 || !isEnabled ? 0 : 1;
    }
}
