package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class BRK extends Instruction {

    private int pcl;

    BRK(Cpu cpu, AddressingMode mode) {
        this.cpu = cpu;
        done = false;
        this.mode = mode;
        length = 6;

        steps = new Step[] {
                () -> cpu.pc++,
                () -> cpu.pushStack(cpu.pc & 0xff00),
                () -> cpu.pushStack(cpu.pc & 0x00ff),
                () -> {
                    int oldStatus = cpu.status;
                    cpu.setBreak(true);
                    cpu.pushStack(cpu.status);
                    cpu.status = oldStatus;
                },
                () -> pcl = cpu.peek(0xFFFE),
                () -> cpu.pc = pcl + (cpu.peek(0xFFFF) << 2)
        };
    }
}

