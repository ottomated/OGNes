package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class PLA extends Instruction {

    PLA(Cpu cpu, AddressingMode mode) {
        this.cpu = cpu;
        done = false;
        this.mode = mode;
        length = 3;

        steps = new Step[]{
                () -> {
                },
                () -> {},
                () -> {

                    cpu.a = cpu.popStack();
                    cpu.setZero(cpu.a == 0);
                    cpu.setNegative(((cpu.a >> 7) & 1) == 1); // If the 7th bit is 1
                },
        };
    }
}

