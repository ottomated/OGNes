package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

class JMP extends Instruction {

    private int pcl;
    private int pl; // Pointer low
    private int p; // Pointer

    JMP(Cpu cpu, AddressingMode mode) {
        this.cpu = cpu;
        done = false;
        this.mode = mode;
        switch (mode) {
            case ABSOLUTE:
                length = 2;

                steps = new Step[]{
                        () -> pcl = cpu.pop(),
                        () -> cpu.pc = pcl + (cpu.pop() << 2)
                };
            case INDIRECT:
                length = 4;

                steps = new Step[]{
                        () -> pl = cpu.pop(),
                        () -> p = pl + (cpu.pop() << 2),
                        () -> pcl = cpu.peek(p),
                        () -> cpu.pc = pcl + (cpu.peek(p + 1) << 2)
                };
        }
    }
}

