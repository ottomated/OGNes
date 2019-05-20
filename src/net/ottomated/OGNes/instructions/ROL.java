package net.ottomated.OGNes.instructions;

import net.ottomated.OGNes.Cpu;

public class ROL extends ReadModWriteInstruction {

    private int doOp(int initial) {
        int res = initial << 1 | cpu.getCarry();

        cpu.setCarry(initial >> 7 & 1);
        cpu.setZero(res == 0);
        cpu.setNegative(((res >> 7) & 1) == 1); // If the 7th bit is 1
        return res & 255;
    }
    @Override
    void finalStep() {
        cpu.set(loc, doOp(m));
    }

    @Override
    void finalAccumulatorStep() {
        cpu.a = doOp(cpu.a);
    }

    ROL(Cpu cpu, AddressingMode mode) {
        super(mode);
        this.cpu = cpu;
        done = false;
        this.mode = mode;
    }
}