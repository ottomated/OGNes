package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class ASL extends Instruction {

    public int run(int addr, int cycleAdd) {
        if (mode == AddressingMode.ACCUMULATOR) {
            cpu.setCarry(((cpu.a >> 7) & 1) == 1);
            cpu.a = cpu.a << 1 & 255;
            cpu.setNegative(((cpu.a >> 7) & 1) == 1); // If the 7th bit is 1
            cpu.setZero(cpu.a == 0);
        } else {
            int tmp = cpu.load(addr);
            cpu.setCarry(((tmp >> 7) & 1) == 1);
            tmp = (tmp << 1) & 255;

            cpu.setNegative(((tmp >> 7) & 1) == 1); // If the 7th bit is 1
            cpu.setZero(tmp == 0);
            cpu.write(addr, tmp);
        }
        return 0;
    }

    ASL(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}
