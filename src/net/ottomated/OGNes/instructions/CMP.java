package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class CMP extends ReadInstruction {

    void finalStep() {
        int res = cpu.a - m;
        cpu.setCarry(cpu.a >= m);
        cpu.setNegative(((res >> 7) & 1) == 1); // If the 7th bit is 1
        cpu.setZero(cpu.a == m);
    }

    CMP(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }

}