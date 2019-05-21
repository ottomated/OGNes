package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class PLP extends Instruction {

    PLP(Cpu cpu, AddressingMode mode) {
        this.cpu = cpu;
        done = false;
        this.mode = mode;
        length = 3;

        steps = new Step[]{
                () -> {
                },
                () -> {},
                () -> cpu.status = cpu.popStack(),
        };
    }
}

