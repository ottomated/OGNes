package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class ROR extends Instruction {

    public int run(int addr, int cycleAdd) {
        int tmp, add;
        if (mode == AddressingMode.ACCUMULATOR) {
            add = (cpu.getCarry() ? 1 : 0) << 7;
            cpu.setCarry((cpu.a & 1) == 1);
            tmp = (cpu.a >> 1) + add;
            cpu.a = tmp;
        } else {
            tmp = cpu.load(addr);
            add = (cpu.getCarry() ? 1 : 0) << 7;
            cpu.setCarry((tmp & 1) == 1);
            tmp = (tmp >> 1) + add;
            cpu.write(addr, tmp);
        }
        cpu.setNegative(((tmp >> 7) & 1) == 1);
        cpu.setZero(tmp == 0);
        return 0;
    }

    ROR(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}
