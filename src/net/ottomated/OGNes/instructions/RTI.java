package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class RTI extends Instruction {

    private int pcl;

    RTI(Cpu cpu, AddressingMode mode) {
        this.cpu = cpu;
        done = false;
        this.mode = mode;
        length = 5;

        steps = new Step[]{
                () -> {
                },
                () -> {
                },
                () -> cpu.status = cpu.popStack(),
                () -> pcl = cpu.popStack(),
                () -> {
                    int pch = cpu.popStack();
                    cpu.pc = pcl + (pch << 2);
                },
        };
    }
}

