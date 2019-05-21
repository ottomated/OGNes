package net.ottomated.OGNes;

import net.ottomated.OGNes.instructions.Instruction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Cpu {

    private int[] memory; // 0x10000 bytes

    public int pc; // Program Counter
    public int sp; // Stack Pointer

    private Instruction instruction;

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

    public void reset() {
        memory = new int[0x10000];
        status = 0b00101000;

        int i;

        // RAM
        for (i = 0; i <= 0x2000; i++) {
            this.memory[i] = 0xFF;
        }

        // Clear memory
        for (i = 0x2001; i < memory.length; i++) {
            this.memory[i] = 0;
        }
        pc = 0x8000;
        sp = 0x01ff;
    }

    public void loadProgram(int[] data) {
        System.arraycopy(data, 0, memory, 0x8000, data.length);
    }

    public void loadINES(File f) throws IOException {

        byte[] b = Files.readAllBytes(f.toPath());
        for (int i = 16; i < b.length; i++) {
            memory[i - 16 + 0x8000] = b[i] & 0xFF;
        }
    }

    public void cycle() {
        if (instruction == null || instruction.done) {
            instruction = Instruction.parse(this);
        } else {
            instruction.cycle();
        }
    }

    public void pushStack(int b) {
        this.memory[sp] = b;
        sp--;
        if (sp < 0x0100) sp = 0x01ff;
        else if (sp > 0x01ff) sp = 0x0100;
    }

    public int popStack() {
        sp++;
        if (sp < 0x0100) sp = 0x01ff;
        else if (sp > 0x01ff) sp = 0x0100;
        return this.memory[sp];
    }

    public int pop() {
        pc++;
        return this.memory[pc - 1];
    }

    public int peek(int loc) {
        return this.memory[loc];
    }

    public void set(int loc, int b) {
        this.memory[loc] = b;
    }

    @Override
    public String toString() {
        return "===== CPU =====\n" +
                "PC: $" + Integer.toHexString(pc) +
                "  memory[pc]: $" + Integer.toHexString(memory[pc]) + "\n" +
                "SP: $" + Integer.toHexString(sp) + "\n" +
                "X:  $" + Integer.toHexString(x) + "\n" +
                "Y:  $" + Integer.toHexString(y) + "\n" +
                "A:  $" + Integer.toHexString(a) + "\n" +
                "STATUS: 0b" + Integer.toBinaryString(status) + "\n" +
                "Current intruction: " + instruction;
    }
}
