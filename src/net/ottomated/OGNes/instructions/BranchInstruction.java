package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;
import net.ottomated.OGNes.Graphics;

abstract class BranchInstruction extends Instruction {

    public int run(int addr, int cycleAdd) {
        int c = 0;
        if (shouldBranch()) {
            c = (cpu.pc & 0xff00) != (addr & 0xff00) ? 2 : 1;
            cpu.pc = addr;
        }
        return c;
    }
    abstract boolean shouldBranch();

    BranchInstruction(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}
