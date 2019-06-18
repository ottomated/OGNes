package net.ottomated.OGNes;

import net.ottomated.OGNes.instructions.Instruction;

import java.io.BufferedWriter;
import java.io.File;

public class Cpu {

    private Nes nes;
    public int[] memory; // 0x10000 bytes

    public int pc; // Program Counter
    public int pc_new;
    public int sp; // Stack Pointer

    private Instruction instruction;


    public int a; // Accumulator
    public int x; // Index Register X
    public int y; // Index Register Y

    public int cyclesToHalt;

    public Cpu(Nes nes) {
        this.nes = nes;
    }

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

    public enum Interrupt {IRQ, NMI, RESET}

    private boolean interruptRequested;
    private Interrupt interrupt;

    void reset() {
        int i;
        memory = new int[0x10000];
        status = 0x34;


        // RAM
        for (i = 0; i < 0x2000; i++) {
            this.memory[i] = 0xFF;
        }

        for (int p = 0; p < 4; p++) {
            int j = p * 0x800;
            memory[j + 0x008] = 0xf7;
            memory[j + 0x009] = 0xef;
            memory[j + 0x00a] = 0xdf;
            memory[j + 0x00f] = 0xbf;
        }
        cyclesToHalt = 0;
        // Clear memory
        for (int k = 0x2001; k < memory.length; k++) {
            this.memory[k] = 0;
        }
        pc = 0x8000 - 1;
        pc_new = 0x8000 - 1;
        sp = 0x01ff;

        interrupt = null;
        interruptRequested = false;
    }

    private String hex(int i) {
        return Integer.toHexString(i).toUpperCase();
    }
    private void log(Instruction instruction, int addr) {
        System.out.print(hex(pc) + "  ");
        String bytes = hex(nes.mapper.read(pc + 1)) + " " + hex(nes.mapper.read(pc + 2)) + " " + hex(nes.mapper.read(pc + 3)) + "  ";
        System.out.print(bytes);
        System.out.print(new String(new char[10 - bytes.length()]).replace('\0', ' '));
        System.out.print(instruction.getClass().getSimpleName() + " ");
        System.out.print(hex(addr) + "                       ");
        System.out.print("A:" + hex(a) + " X:" + hex(x) + " Y:" + hex(y) + " P:" + hex(status) + " SP:" + hex(sp));
        System.out.print("PPU: " + nes.ppu.curX + ", " + nes.ppu.scanline);

        System.out.println();
    }

    int cycle() throws Exception {
        if (!nes.ready) return 0;
        //System.out.print("A: " + a + " Status: " + Integer.toBinaryString(status) + " X: " + x + " Y: " + y + " PC: " + pc + " SP: " + sp + " instr: " + nes.mapper.read(pc + 1));
        if (interruptRequested) {
            pc_new = pc;
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
            pc = pc_new;
            interruptRequested = false;
        }
        instruction = Instruction.parse(this, nes.mapper.read(pc + 1));
        //System.out.print("0x" + Integer.toHexString(pc));
        if (instruction == null) throw new Exception("Invalid instruction");
        int addr = 0;
        int opaddr = pc;
        pc += instruction.size;
        int cycleAdd = 0;
        int cycleCount = instruction.cycles;

        switch (instruction.mode) {
            case ZERO_PAGE:
                addr = load(opaddr + 2);
                break;
            case RELATIVE:
                addr = load(opaddr + 2);
                if (addr < 0x80) {
                    addr += pc;
                } else {
                    addr += pc - 256;
                }
                break;
            case IMPLIED:
                break;
            case ABSOLUTE:
                addr = load16Bit(opaddr + 2);
                break;
            case ACCUMULATOR:
                addr = a;
                break;
            case IMMEDIATE:
                addr = pc;
                break;
            case INDEXED_ZERO_PAGE_X:
                addr = (load(opaddr + 2) + x) & 0xff;
                break;
            case INDEXED_ZERO_PAGE_Y:
                addr = (load(opaddr + 2) + y) & 0xff;
                break;
            case INDEXED_ABSOLUTE_X:

                addr = load16Bit(opaddr + 2);
                if ((addr & 0xff00) != ((addr + x) & 0xff00)) {
                    cycleAdd = 1;
                }
                addr += x;
                break;
            case INDEXED_ABSOLUTE_Y:

                addr = load16Bit(opaddr + 2);
                if ((addr & 0xff00) != ((addr + y) & 0xff00)) {
                    cycleAdd = 1;
                }
                addr += y;
                break;
            case INDEXED_INDIRECT:

                addr = load(opaddr + 2);
                if ((addr & 0xff00) != ((addr + x) & 0xff00)) {
                    cycleAdd = 1;
                }
                addr += x;
                addr &= 0xff;
                addr = load16Bit(addr);
                break;
            case INDIRECT_INDEXED:

                addr = load16Bit(load(opaddr + 2));
                if ((addr & 0xff00) != ((addr + y) & 0xff00)) {
                    cycleAdd = 1;
                }
                addr += y;
                break;
            case INDIRECT:
                addr = load16Bit(opaddr + 2);
                if (addr < 0x1fff) {
                    addr = memory[addr] + (memory[(addr & 0xff00) | (((addr & 0xff) + 1) & 0xff)] << 8);
                } else {
                    addr = nes.mapper.read(addr) + (nes.mapper.read((addr & 0xff00) | (((addr & 0xff) + 1) & 0xff)) << 8);
                }
                break;
        }
        addr &= 0xffff;
        //log(instruction, addr);
        //System.out.println("  addr: " + addr);
        //System.out.println(": "+ instruction + " 0x" + Integer.toHexString(addr));
        //System.out.print(nes.mapper.read(pc + 1) + " ");
        cycleCount += instruction.run(addr, cycleAdd);
        //System.out.println("Status: " + Integer.toBinaryString(status));
        //System.out.println(cycleCount);
        return cycleCount;
    }

    public void requestIrq(Interrupt type) {
        if (interruptRequested) {
            if (type == Interrupt.IRQ)
                return;
        }
        interruptRequested = true;
        interrupt = type;
    }

    private void doIrq(int status) {
        pc_new++;
        pushStack((pc_new >> 8) & 0xff);
        pushStack(pc_new & 0xff);
        pushStack(status);
        setInterruptDisable(true);
        setBreak(false);
        pc_new = load(0xfffe) | (load(0xffff) << 8);
        pc_new--;
    }

    private void doNmi(int status) {
        if ((load(0x2000) & 128) != 0) {
            pc_new++;
            pushStack((pc_new >> 8) & 0xff);
            pushStack(pc_new & 0xff);
            pushStack(status);
            pc_new = load(0xfffa) | (load(0xfffb) << 8);
            pc_new--;
        }
    }

    private void doResetInt() {
        //System.out.println(load(0xfffc) + " -- " + load(0xfffd));
        pc_new = load(0xfffc) | (load(0xfffd) << 8);
        pc_new--;
    }

    public void pushStack(int b) {
        nes.mapper.write(sp, b);
        sp--;
        if (sp < 0x0100) sp = 0x01ff;
        else if (sp > 0x01ff) sp = 0x0100;
    }

    public int popStack() {
        sp++;
        if (sp < 0x0100) sp = 0x01ff;
        else if (sp > 0x01ff) sp = 0x0100;
        return nes.mapper.read(sp);
    }

    public int pop() {
        pc++;
        return nes.mapper.read(pc - 1);
    }

    public int load(int addr) {
        if (addr < 0x2000) {
            return memory[addr & 0x7ff];
        } else {
            return nes.mapper.read(addr);
        }
    }

    public int load16Bit(int addr) {
        if (addr < 0x1fff) {
            return memory[addr & 0x7ff] | (memory[(addr + 1) & 0x7ff] << 8);
        } else {
            return nes.mapper.read(addr) | (nes.mapper.read(addr + 1) << 8);
        }
    }

    public void write(int loc, int b) {
        nes.mapper.write(loc, b);
    }

    @Override
    public String toString() {
        return "===== CPU =====\n" +
                "PC: $" + pc +
                "  memory[pc]: $" + nes.mapper.read(pc) + "\n" +
                "SP: $" + sp + "\n" +
                "X:  $" + x + "\n" +
                "Y:  $" + y + "\n" +
                "A:  $" + a + "\n" +
                "STATUS: 0b" + Integer.toBinaryString(status) + "\n" +
                "Current instruction: " + instruction;
    }
}
