package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class INX extends ReadInstruction {

    void finalStep() {
        int res = cpu.x + 1;
        cpu.setNegative(((res >> 7) & 1) == 1); // If the 7th bit is 1
        cpu.setZero(res == 0);
        cpu.x = res;
    }

    INX(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }

}