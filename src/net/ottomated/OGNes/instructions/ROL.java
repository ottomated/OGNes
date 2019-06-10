package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class ROL extends Instruction {

    public int run(int addr, int cycleAdd) {
        int tmp, add;
        if (mode == AddressingMode.ACCUMULATOR) {
            tmp = cpu.a;
            add = cpu.getCarry() ? 1 : 0;
            cpu.setCarry(((tmp >> 7) & 1) == 1);
            tmp = ((tmp << 1) & 0xff) + add;
            cpu.a = tmp;
        } else {
            tmp = cpu.load(addr);
            add = cpu.getCarry() ? 1 : 0;
            cpu.setCarry(((tmp >> 7) & 1) == 1);
            tmp = ((tmp << 1) & 0xff) + add;
            cpu.write(addr, tmp);
        }
        cpu.setNegative(((tmp >> 7) & 1) == 1);
        cpu.setZero(tmp == 0);
        return 0;
    }

    ROL(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}
