package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class BIT extends ReadInstruction {

    void finalStep() {
        int res = cpu.a & m;

        cpu.setZero(res == 0);
        cpu.setNegative(((res >> 7) & 1) == 1); // If the 7th bit is 1
        cpu.setOverflow(((res >> 6) & 1) == 1); // If the 6th bit is 1

    }

    BIT(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }
}

