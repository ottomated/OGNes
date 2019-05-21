package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class JSR extends Instruction {

    private int pcl;

    JSR(Cpu cpu, AddressingMode mode) {
        this.cpu = cpu;
        done = false;
        this.mode = mode;
        length = 5;

        steps = new Step[] {
                () -> pcl = cpu.pop(),
                () -> {},
                () -> cpu.pushStack(cpu.pc & 0xff00),
                () -> cpu.pushStack(cpu.pc & 0x00ff),
                () -> cpu.pc = pcl + (cpu.pop() << 2)
        };
    }
}

