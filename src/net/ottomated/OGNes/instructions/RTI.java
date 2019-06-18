package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class RTI extends Instruction {

    @Override
    public int run(int addr, int cycleAdd) {
        cpu.status = cpu.popStack();
        cpu.pc = cpu.popStack();
        cpu.pc += cpu.popStack() << 8;
        if (cpu.pc == 0xffff) System.out.println("PC out of range?");
        cpu.pc--;
        cpu.setUnused(true);
        return 0;
    }

    RTI(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

