package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

public class ASL extends ReadModWriteInstruction {
    @Override
    void finalStep() {

    }

    @Override
    void finalAccumulatorStep() {
        int res = cpu.a << 1;

        cpu.setCarry(res > 255);
        cpu.setZero(res == 0);
        cpu.setNegative(((res >> 7) & 1) == 1); // If the 7th bit is 1

        cpu.a = res & 255; // Don't overflow
    }

    ASL(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }
}
