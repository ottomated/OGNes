package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class JMP extends Instruction {

    @Override
    public int run(int addr, int cycleAdd) {
        cpu.pc = addr - 1;
        return 0;
    }

    JMP(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

