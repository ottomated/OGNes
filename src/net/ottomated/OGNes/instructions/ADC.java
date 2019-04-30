package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class ADC extends ReadInstruction {

    void finalStep() {
        int res = cpu.a + m + (cpu.getCarry() ? 1 : 0);

        cpu.setOverflow(((cpu.a ^ m) & 0x80) == 0 && // If the sign bit isn't set but should be
                ((cpu.a ^ res & 0x80) != 0));
        cpu.setCarry(res > 255);
        cpu.setNegative(((res >> 7) & 1) == 1); // If the 7th bit is 1
        cpu.setZero(res == 0);
        cpu.a = res & 255; // Don't overflow
    }

    ADC(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }

    @Override
    public void cycle() {
        steps[c].go();
        c++;
        if (length == c)
            done = true;

    }
}
