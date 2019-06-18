package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class ISC extends Instruction {

    public int run(int addr, int addCycles) {
        int res = (cpu.load(addr) + 1) & 0xff;
        cpu.write(addr, res);

        res = cpu.a - res - (1 - (cpu.getCarry() ? 1 : 0));
        cpu.setCarry(res >= 0);
        cpu.setNegative(((res >> 7) & 1) == 1);
        cpu.setZero((res & 0xff) == 0);

        if (
                ((cpu.a ^ res) & 0x80) != 0 &&
                        ((cpu.a ^ cpu.load(addr)) & 0x80) != 0
        ) {
            cpu.setOverflow(true);
        } else {
            cpu.setOverflow(false);
        }
        cpu.a = res & 0xff;
        if (mode != AddressingMode.INDEXED_INDIRECT) return addCycles;
        else return 0;
    }

    ISC(Cpu cpu, AddressingMode mode, int size, int cycles) {
        this.cpu = cpu;
        this.mode = mode;
        this.size = size;
        this.cycles = cycles;
    }

}