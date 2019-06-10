package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class BRK extends Instruction {

    @Override
    public int run(int addr, int cycleAdd) {
        cpu.pc += 2;
        cpu.pushStack((cpu.pc >> 8) & 255);
        cpu.pushStack(cpu.pc & 255);
        cpu.setBreak(true);
        cpu.pushStack(cpu.status);

        cpu.setInterruptDisable(true);
        cpu.pc = cpu.load16Bit(0xfffe);
        cpu.pc--;
        return 0;
    }

    private int pcl;

    BRK(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

