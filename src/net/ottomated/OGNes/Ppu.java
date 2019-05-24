package net.ottomated.OGNes;

import net.ottomated.OGNes.instructions.Instruction;

public class Ppu {

    private int[] memory; // 0x10000 bytes

    public int pc; // Program Counter
    public int sp; // Stack Pointer



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

    public void setVBlank(boolean b) {
        setStatusAt(b, 0);
    }
    public void setMasterSlave(boolean b) {
        setStatusAt(b, 1);
    }
    public void setSpriteSize(boolean b) {
        setStatusAt(b, 2);
    }
    public void setBackgroundPattern(boolean b) {
        setStatusAt(b, 3);
    }
    public void setSpritePattern(boolean b) {
        setStatusAt(b, 4);
    }
    public void setVRAMIncrement(boolean b) {
        setStatusAt(b, 6);
    }
    public void setNametable(boolean b) {
        setStatusAt(b, 7);
    }
    public boolean getVBlank() {
        return getStatusAt(0);
    }
    public boolean getMasterSlave() {
        return getStatusAt(1);
    }
    public boolean getSpriteSize() {
        return getStatusAt(2);
    }
    public boolean getBackgroundPattern() {
        return getStatusAt(3);
    }
    public boolean getSpritePattern() {
        return getStatusAt(4);
    }
    public boolean getVRAMIncrement() {
        return getStatusAt(6);
    }
    public boolean getNametable() {
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
