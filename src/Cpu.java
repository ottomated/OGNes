class Cpu {

    private int[] memory; // 0x10000 bytes

    private int pc; // Program Counter
    private int sp; // Stack Pointer

    private int a; // Accumulator
    private int x; // Index Register X
    private int y; // Index Register Y

    // Processor Status
    // 0 => Carry (if last instruction resulted in under/overflow)
    // 1 => Zero (if last instruction's result was 0)
    // 2 => Interrupt Disable (Enable to prevent system from responding to interrupts)
    // 3 => Decimal mode (unsupported on this chip variant)
    // 4 => Empty
    // 5 => Empty
    // 6 => Overflow (if previous instruction resulted in an invalid two's complement)
    // 7 => Negative
    private int status;
    private boolean carry;
    private boolean zero;
    private boolean interruptDisable;
    private boolean decimalMode;
    private boolean overflow;
    private boolean negative;

    private enum Interrupt {IRQ, NMI, RESET}

    ;
    private Interrupt interrupt;

    private enum AddressingMode {
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
    };

    private void reset() {
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

    }

    @CpuInstruction(name = "AND", description = "Performs a logical AND")
    private int and(int a, int b) {
        return a & b;
    }
}
