package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class PHP extends Instruction {

    PHP(Cpu cpu, AddressingMode mode) {
        this.cpu = cpu;
        done = false;
        this.mode = mode;
        length = 2;

        steps = new Step[]{
                () -> {
                },
                () -> cpu.pushStack(cpu.status),
        };
    }
}

