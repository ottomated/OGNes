package net.ottomated.OGNes;

import net.ottomated.OGNes.instructions.Instruction;

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
    public boolean carry;
    public boolean zero;
    public boolean interruptDisable;
    public boolean decimalMode;
    public boolean overflow;
    public boolean negative;

    private enum Interrupt {IRQ, NMI, RESET}

    private Interrupt interrupt;

    public enum AddressingMode {
        ZERO_PAGE,
        INDEXED_ZERO_PAGE_X,
        INDEXED_ZERO_PAGE_Y,
        ABSOLUTE,
        INDEXED_ABSOLUTE_X,
        INDEXED_ABSOLUTE_Y,
        IMPLIED,
        ACCUMULATOR,
        IMMEDIATE,
        RELATIVE,
        INDEXED_INDIRECT,
        INDIRECT_INDEXED,
        INDIRECT
    }

    public void reset() {
        memory = new int[0x10000];
        carry = false;
        zero = false;
        interruptDisable = false;
        decimalMode = false;
        overflow = false;
        negative = false;

        int i;

        // RAM
        for (i = 0; i <= 0x2000; i++) {
            this.memory[i] = 0xFF;
        }

        // Clear memory
        for (i = 0x2000; i <= 0x8000; i++) {
            this.memory[i] = 0;
        }

        // Load program
        memory[0x5757] = 0x69;
        memory[0x5758] = 0x57;
        pc = 0x5757;
    }

    public void cycle() {
        if (instruction == null || instruction.done) {
            instruction = Instruction.parse(this);
        } else {
            instruction.cycle();
        }
    }

    public int pop() {
        pc++;
        return this.memory[pc - 1];
    }

    public int peek(int loc) {
        return this.memory[loc];
    }
}
