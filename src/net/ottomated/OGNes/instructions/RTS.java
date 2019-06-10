package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class RTS extends Instruction {

    public int run(int addr, int addStatus) {

        cpu.pc = cpu.popStack();
        cpu.pc += cpu.popStack() << 8;
        if (cpu.pc == 0xffff) System.out.println("PC out of range?");
        return 0;
    }

    RTS(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}

