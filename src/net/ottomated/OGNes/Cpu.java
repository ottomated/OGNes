package net.ottomated.OGNes;

import net.ottomated.OGNes.instructions.Instruction;
import net.ottomated.OGNes.mappers.Mapper;

public class Cpu {

    public int[] memory; // 0x10000 bytes

    public int pc; // Program Counter
    public int sp; // Stack Pointer

    private Instruction instruction;
    private Mapper mapper;

    public int a; // Accumulator
    public int x; // Index Register X
    public int y; // Index Register Y

    // Processor Status
    // 0 => Carry (if last instruction resulted in under/overflow)
    // 1 => Zero (if last instruction's result was 0)
    // 2 => Interrupt Disable (Enable to prevent system from responding to interrupts)
    // 3 => Decimal mode (unsupported on this chip variant)
    // 4 => Empty
    // 5 => Empty
    // 6 => Overflow (if previous instruction resulted in an invalid two's complement)
    // 7 => Negative
    public int status;

    public void setCarry(boolean b) {
        setStatusAt(b, 0);
    }

    public void setZero(boolean b) {
        setStatusAt(b, 1);
    }

    public void setInterruptDisable(boolean b) {
        setStatusAt(b, 2);
    }

    public void setDecimalMode(boolean b) {
        setStatusAt(b, 3);
    }

    public void setBreak(boolean b) {
        setStatusAt(b, 4);
    }

    public void setOverflow(boolean b) {
        setStatusAt(b, 6);
    }

    public void setNegative(boolean b) {
        setStatusAt(b, 7);
    }

    public boolean getCarry() {
        return getStatusAt(0);
    }

    public boolean getZero() {
        return getStatusAt(1);
    }

    public boolean getInterruptDisable() {
        return getStatusAt(2);
    }

    public boolean getDecimalMode() {
        return getStatusAt(3);
    }

    public boolean getBreak() {
        return getStatusAt(4);
    }

    public boolean getOverflow() {
        return getStatusAt(6);
    }

    public boolean getNegative() {
        return getStatusAt(7);
    }

    private void setStatusAt(boolean b, int i) {
        status = b ? status | (1 << i)
                : status & ~(1 << i);
    }

    private boolean getStatusAt(int i) {
        return ((status >> i) & 1) == 1;
    }

    private enum Interrupt {IRQ, NMI, RESET}

    private Interrupt interrupt;

    void reset() {
        memory = new int[0x10000];
        status = 0b00101000;

        int i;

        // RAM
        for (i = 0; i <= 0x2000; i++) {
            this.memory[i] = 0xFF;
        }

        for (int p = 0; p < 4; p++) {
            int j = p * 0x800;
            memory[j + 0x008] = 0xf7;
            memory[j + 0x009] = 0xef;
            memory[j + 0x00a] = 0xdf;
            memory[j + 0x00f] = 0xbf;
        }
        // Clear memory
        for (i = 0x2001; i < memory.length; i++) {
            this.memory[i] = 0;
        }
        pc = 0x8000;
        sp = 0x01ff;

        interrupt = null;
    }

    void loadRom(Rom rom) {
        mapper = rom.mapper;
    }

    public void cycle() {
        if (interrupt != null) {
            switch (interrupt) {
                case IRQ:
                    if (getInterruptDisable())
                        break;
                    doIrq(status);
                    break;
                case NMI:
                    doNmi(status);
                    break;
                case RESET:
                    doResetInt();
                    break;
            }
        }
        if (instruction == null || instruction.done) {
            instruction = Instruction.parse(this);
        } else {
            instruction.cycle();
        }
    }

    public void requestIrq(Interrupt type) {
        if (interrupt != null) {
            if (type == Interrupt.IRQ)
                return;
        }
        interrupt = type;
    }

    private void doIrq(int status) {
        pc++;
        pushStack((pc >> 8) & 0xff);
        pushStack(pc & 0xff);
        pushStack(status);
        setInterruptDisable(true);
        setBreak(false);
        pc = peek(0xfffe) | (peek(0xffff) << 8);
        pc--;
    }

    private void doNmi(int status) {
        if ((peek(0x2000) & 128) != 0) {
            pc++;
            pushStack((pc >> 8) & 0xff);
            pushStack(pc & 0xff);
            pushStack(status);
            pc = peek(0xfffa) | (peek(0xfffb) << 8);
            pc--;
        }
    }

    private void doResetInt() {
        pc = peek(0xfffc) | (peek(0xfffd) << 8);
        pc--;
    }

    public void pushStack(int b) {
        mapper.write(sp, b);
        sp--;
        if (sp < 0x0100) sp = 0x01ff;
        else if (sp > 0x01ff) sp = 0x0100;
    }

    public int popStack() {
        sp++;
        if (sp < 0x0100) sp = 0x01ff;
        else if (sp > 0x01ff) sp = 0x0100;
        return mapper.read(sp);
    }

    public int pop() {
        pc++;
        return mapper.read(pc - 1);
    }

    public int peek(int loc) {
        return mapper.read(loc);
    }

    public void set(int loc, int b) {
        mapper.write(loc, b);
    }

    @Override
    public String toString() {
        return "===== CPU =====\n" +
                "PC: $" + Integer.toHexString(pc) +
                "  memory[pc]: $" + Integer.toHexString(mapper.read(pc)) + "\n" +
                "SP: $" + Integer.toHexString(sp) + "\n" +
                "X:  $" + Integer.toHexString(x) + "\n" +
                "Y:  $" + Integer.toHexString(y) + "\n" +
                "A:  $" + Integer.toHexString(a) + "\n" +
                "STATUS: 0b" + Integer.toBinaryString(status) + "\n" +
                "Current instruction: " + instruction;
    }
}
