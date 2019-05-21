package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class TAY extends Instruction {

    TAY(Cpu cpu, AddressingMode mode) {
        this.cpu = cpu;
        done = false;
        this.mode = mode;
        length = 1;

        steps = new Step[] {
                () -> {
                  cpu.setZero(cpu.a == 0);
                  cpu.setNegative(((cpu.a >> 7) & 1) == 1); // If the 7th bit is 1
                  cpu.y = cpu.a;
                }
        };
    }
}