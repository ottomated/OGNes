package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class EOR extends Instruction {

    @Override
    public int run(int addr, int cycleAdd) {
        cpu.a = (cpu.load(addr) ^ cpu.a) & 0xff;

        cpu.setZero(cpu.a == 0);
        cpu.setNegative(((cpu.a >> 7) & 1) == 1); // If the 7th bit is 1
        return cycleAdd;
    }


    EOR(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}
