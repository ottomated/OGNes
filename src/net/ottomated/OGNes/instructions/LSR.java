package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class LSR extends Instruction {

    public int run(int addr, int cycleAdd) {
        int tmp;
        if (mode == AddressingMode.ACCUMULATOR) {
            tmp = cpu.a & 0xff;
            cpu.setCarry((tmp & 1) == 1);
            tmp >>= 1;
            cpu.a = tmp;
        } else {
            tmp = cpu.load(addr) & 0xff;
            cpu.setCarry((tmp & 1) == 1);
            tmp >>= 1;
            cpu.write(addr, tmp);
        }
        cpu.setNegative(false);
        cpu.setZero(tmp == 0);
        return 0;
    }

    LSR(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }
}
