package net.ottomated.OGNes.apu;

import net.ottomated.OGNes.Apu;

public class SquareChannel {
    private static final int[] dutyLookup = new int[]
            {
                    0, 1, 0, 0, 0, 0, 0, 0,
                    0, 1, 1, 0, 0, 0, 0, 0,
                    0, 1, 1, 1, 1, 0, 0, 0,
                    1, 0, 0, 1, 1, 1, 1, 1
            };
    private static final int[] impLookup = new int[]
            {
                    1, -1, 0, 0, 0, 0, 0, 0,
                    1, 0, -1, 0, 0, 0, 0, 0,
                    1, 0, 0, 0, -1, 0, 0, 0,
                    -1, 0, 1, 0, 0, 0, 0, 0
            };

    boolean isSquare1;
    private Apu apu;
    public int sampleValue;

    public int progTimerCount;
    public int progTimerMax;
    int lengthCounter;
    public int squareCounter;
    int sweepCounter;
    int sweepCounterMax;
    int sweepMode;
    int sweepShiftAmount;
    int envDecayRate;
    int envDecayCounter;
    int envVolume;
    int masterVolume;
    int dutyMode;
    int vol;

    boolean isEnabled;
    boolean lengthCounterEnable;
    boolean sweepActive;
    boolean sweepCarry;
    boolean envDecayDisable;
    boolean envDecayLoopDisable;
    boolean envReset;
    boolean envDecayLoopEnable;
    boolean updateSweepPeriod;

    public SquareChannel(Apu apu, boolean isSquare1) {
        this.isSquare1 = isSquare1;
        this.apu = apu;
    }

    public void reset() {
        progTimerCount = 0;
        progTimerMax = 0;
        lengthCounter = 0;
        squareCounter = 0;
        sweepCounter = 0;
        sweepCounterMax = 0;
        sweepMode = 0;
        sweepShiftAmount = 0;
        envDecayRate = 0;
        envDecayCounter = 0;
        envVolume = 0;
        masterVolume = 0;
        dutyMode = 0;
        vol = 0;
        isEnabled = false;
        lengthCounterEnable = false;
        sweepActive = false;
        sweepCarry = false;
        envDecayDisable = false;
        envDecayLoopDisable = false;
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

    public void clockSweep() {
        if (--sweepCounter <= 0) {
            sweepCounter = sweepCounterMax + 1;
            if (sweepActive && sweepShiftAmount > 0 && progTimerMax > 7) {
                // Calculate result from shifter:
                sweepCarry = false;
                if (sweepMode == 0) {
                    progTimerMax += progTimerMax >> sweepShiftAmount;
                    if (progTimerMax > 4095) {
                        progTimerMax = 4095;
                        sweepCarry = true;
                    }
                } else {
                    progTimerMax = progTimerMax - ((progTimerMax >> sweepShiftAmount) - (isSquare1 ? 1 : 0));
                }
            }
        }

        if (updateSweepPeriod) {
            updateSweepPeriod = false;
            sweepCounter = sweepCounterMax + 1;
        }
    }

    public void updateSampleValue() {

        if (isEnabled && lengthCounter > 0 && progTimerMax > 7) {
            if (sweepMode == 0 && progTimerMax + (progTimerMax >> sweepShiftAmount) > 4095) {
                //if (sweepCarry) {
                sampleValue = 0;
            } else {
                sampleValue = masterVolume * SquareChannel.dutyLookup[(dutyMode << 3) + squareCounter];
            }
        } else {
            sampleValue = 0;
        }
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

    public void writeReg(int address, int value) {
        var addrAdd = isSquare1 ? 0 : 4;
        if (address == 0x4000 + addrAdd) {
            // Volume/Envelope decay:
            envDecayDisable = (value & 0x10) != 0;
            envDecayRate = value & 0xf;
            envDecayLoopEnable = (value & 0x20) != 0;
            dutyMode = (value >> 6) & 0x3;
            lengthCounterEnable = (value & 0x20) == 0;
            if (envDecayDisable) {
                masterVolume = envDecayRate;
            } else {
                masterVolume = envVolume;
            }
            updateSampleValue();
        } else if (address == 0x4001 + addrAdd) {
            // Sweep:
            sweepActive = (value & 0x80) != 0;
            sweepCounterMax = (value >> 4) & 7;
            sweepMode = (value >> 3) & 1;
            sweepShiftAmount = value & 7;
            updateSweepPeriod = true;
        } else if (address == 0x4002 + addrAdd) {
            // Programmable timer:
            progTimerMax &= 0x700;
            progTimerMax |= value;
        } else if (address == 0x4003 + addrAdd) {
            // Programmable timer, length counter
            progTimerMax &= 0xff;
            progTimerMax |= (value & 0x7) << 8;

            if (isEnabled) {
                lengthCounter = apu.getLengthMax(value & 0xf8);
            }

            envReset = true;
        }
    }
}
