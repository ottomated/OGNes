package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class AND extends ReadInstruction {

    void finalStep() {
        int res = cpu.a & m;

        cpu.setNegative(((res >> 7) & 1) == 1); // If the 7th bit is 1
        cpu.setZero(res == 0);
        cpu.a = res & 255; // Don't overflow
    }

    AND(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }
}
