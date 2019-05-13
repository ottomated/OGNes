package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class INY extends ReadInstruction {

    void finalStep() {
        int res = cpu.y + 1;
        cpu.setNegative(((res >> 7) & 1) == 1); // If the 7th bit is 1
        cpu.setZero(res == 0);
        cpu.y = res;
    }

    INY(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }

}