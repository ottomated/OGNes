package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class TXA extends Instruction {

    TXA(Cpu cpu, AddressingMode mode) {
        this.cpu = cpu;
        done = false;
        this.mode = mode;
        length = 1;

        steps = new Step[]{
                () -> {
                    cpu.setZero(cpu.x == 0);
                    cpu.setNegative(((cpu.x >> 7) & 1) == 1); // If the 7th bit is 1
                    cpu.a = cpu.x;
                }
        };
    }
}