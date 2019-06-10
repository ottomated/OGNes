package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class JSR extends Instruction {

    @Override
    public int run(int addr, int cycleAdd) {
        cpu.pushStack((cpu.pc >> 8) & 0xff);
        cpu.pushStack(cpu.pc & 0xff);
        cpu.pc = addr - 1;
        return 0;
    }

    JSR(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

