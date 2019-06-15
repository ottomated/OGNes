package net.ottomated.OGNes.apu;

import net.ottomated.OGNes.Apu;

public class TriangleChannel {
    private Apu apu;

    public boolean isEnabled;
    public int progTimerCount;
    public int progTimerMax;
    public int triangleCounter;
    public boolean sampleCondition;
    public int lengthCounter;
    private boolean lengthCounterEnable;
    public int linearCounter;
    private int lcLoadValue;
    private boolean lcHalt;
    private boolean lcControl;
    private int tmp;
    public int sampleValue;


    public TriangleChannel(Apu apu) {
        this.apu = apu;
        reset();
    }

    public void reset() {

        progTimerCount = 0;
        progTimerMax = 0;
        triangleCounter = 0;
        isEnabled = false;
        sampleCondition = false;
        lengthCounter = 0;
        lengthCounterEnable = false;
        linearCounter = 0;
        lcLoadValue = 0;
        lcHalt = true;
        lcControl = false;
        tmp = 0;
        sampleValue = 0xf;
    }

    public void clockLengthCounter() {
        if (lengthCounterEnable && lengthCounter > 0) {
            lengthCounter--;
            if (lengthCounter == 0) {
                updateSampleCondition();
            }
        }
    }

    public void clockLinearCounter() {
        if (lcHalt) {
            // Load:
            linearCounter = lcLoadValue;
            updateSampleCondition();
        } else if (linearCounter > 0) {
            // Decrement:
            linearCounter--;
            updateSampleCondition();
        }
        if (!lcControl) {
            // Clear halt flag:
            lcHalt = false;
        }
    }

    public int getLengthStatus() {
        return this.lengthCounter == 0 || !isEnabled ? 0 : 1;
    }

    public int readReg(int address) {
        return 0;
    }

    public void writeReg(int address, int value) {
        if (address == 0x4008) {
            // New values for linear counter:
            lcControl = (value & 0x80) != 0;
            lcLoadValue = value & 0x7f;

            // Length counter enable:
            lengthCounterEnable = !lcControl;
        } else if (address == 0x400a) {
            // Programmable timer:
            progTimerMax &= 0x700;
            progTimerMax |= value;
        } else if (address == 0x400b) {
            // Programmable timer, length counter
            progTimerMax &= 0xff;
            progTimerMax |= (value & 0x07) << 8;
            lengthCounter = apu.getLengthMax(value & 0xf8);
            lcHalt = true;
        }

        updateSampleCondition();
    }

    public void clockProgrammableTimer(int cycles) {
        if (progTimerMax > 0) {
            progTimerCount += cycles;
            while (progTimerMax > 0 && progTimerCount >= progTimerMax) {
                progTimerCount -= progTimerMax;
                if (isEnabled && lengthCounter > 0 && linearCounter > 0) {
                    clockTriangleGenerator();
                }
            }
        }
    }

    private void clockTriangleGenerator() {
        triangleCounter++;
        triangleCounter &= 0x1f;
    }

    public void setEnabled(boolean value) {
        isEnabled = value;
        if (!value) {
            lengthCounter = 0;
        }
        updateSampleCondition();
    }

    private void updateSampleCondition() {
        sampleCondition = isEnabled &&
                progTimerMax > 7 &&
                linearCounter > 0 &&
                lengthCounter > 0;
    }
}
