package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class PHA extends Instruction {

    PHA(Cpu cpu, AddressingMode mode) {
        this.cpu = cpu;
        done = false;
        this.mode = mode;
        length = 2;

        steps = new Step[]{
                () -> {
                },
                () -> cpu.pushStack(cpu.a),
        };
    }
}

