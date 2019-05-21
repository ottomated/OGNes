package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class TYA extends Instruction {

    TYA(Cpu cpu, AddressingMode mode) {
        this.cpu = cpu;
        done = false;
        this.mode = mode;
        length = 1;

        steps = new Step[]{
                () -> {
                    cpu.setZero(cpu.y == 0);
                    cpu.setNegative(((cpu.y >> 7) & 1) == 1); // If the 7th bit is 1
                    cpu.a = cpu.y;
                }
        };
    }
}