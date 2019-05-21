package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class RTS extends Instruction {

    private int pcl;

    RTS(Cpu cpu, AddressingMode mode) {
        this.cpu = cpu;
        done = false;
        this.mode = mode;
        length = 5;

        steps = new Step[]{
                () -> {
                },
                () -> {
                },
                () -> pcl = cpu.popStack(),
                () -> {
                    int pch = cpu.popStack();
                    cpu.pc = pcl + (pch << 2);
                },
                () -> {
                    cpu.pc++;
                }
        };
    }
}

