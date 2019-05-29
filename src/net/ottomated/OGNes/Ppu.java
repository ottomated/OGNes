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
    public int ctrl;
    public int mask;
    public int stat;
    public int oamAddr;
    public int scrl;
    public int addr;

    public int oamRead() {
      return memory[0x0200 + oamAddr];
    }
    
    public void oamWrite(int val) {
      memory[oamAddr] = val;
      oamAddr++;
    }
    
    public int vramRead() {
      return memory[addr];
    }
    
    public void vramWrite(int val) {
      memory[addr] = val;
      addr += getVRAMIncrement();
    }
    
    public void oamDMAWrite(int highByte){
      for(int i = 0; i < 256; i++){
         this.memory[0x0200 + i] = cpu.memory[highByte * 256 + i];
         cnt++;
      }
    
    }

    public void setNMI(boolean b) {
        ctrl = setStatusAt(b, 7, ctrl);
    }
    public void setMasterSlave(boolean b) {
        ctrl = setStatusAt(b, 6, ctrl);
    }
    public void setSpriteSize(boolean b) {
        ctrl = setStatusAt(b, 5, ctrl);
    }
    public void setBackgroundPattern(boolean b) {
        ctrl = setStatusAt(b, 4, ctrl);
    }
    public void setSpritePattern(boolean b) {
        ctrl = setStatusAt(b, 3, ctrl);
    }
    public void setVRAMIncrement(boolean b) {
        ctrl = setStatusAt(b, 2, ctrl);
    }
    public void setNametableBig(boolean b) {
        ctrl = setStatusAt(b, 1, ctrl);
    }
    public void setNametableSmall(boolean b) {
        ctrl = setStatusAt(b, 0, ctrl);
    }
    public boolean getNMI() {
        return getStatusAt(7, ctrl);
    }
    public boolean getMasterSlave() {
        return getStatusAt(6, ctrl);
    }
    public boolean getSpriteSize() {
        return getStatusAt(5, ctrl);
    }
    public boolean getBackgroundPattern() {
        return getStatusAt(4, ctrl);
    }
    public boolean getSpritePattern() {
        return getStatusAt(3, ctrl);
    }
    public boolean getVRAMIncrement() {
        return getStatusAt(2, ctrl);
    }
    public boolean getNametableBig() {
        return getStatusAt(1, ctrl);
    }
    public void getNametableSmall() {
        return getStatusAt(0, ctrl);
    }
    
    
    
    public void setClrEmphB(boolean b) {
        mask = setStatusAt(b, 7, mask);
    }
    public void setClrEmphG(boolean b) {
        mask = setStatusAt(b, 6, mask);
    }
    public void setClrEmphR(boolean b) {
        mask = setStatusAt(b, 5, mask);
    }
    public void setSpriteEnable(boolean b) {
        mask = setStatusAt(b, 4, mask);
    }
    public void setBackgroundEnable(boolean b) {
        mask = setStatusAt(b, 3, mask);
    }
    public void setSpriteLeftEnable(boolean b) {
        mask = setStatusAt(b, 2, mask);
    }
    public void setBackgroundLeftEnable(boolean b) {
        mask = setStatusAt(b, 1, mask);
    }
    public void setGray(boolean b) {
        mask = setStatusAt(b, 0, mask);
    }
    public boolean getClrEmphB() {
        return getStatusAt(7, mask);
    }
    public boolean getClrEmphG() {
        return getStatusAt(6, mask);
    }
    public boolean getClrEmphR() {
        return getStatusAt(5, mask);
    }
    public boolean getSpriteEnable() {
        return getStatusAt(4, mask);
    }
    public boolean getBackgroundEnable() {
        return getStatusAt(3, mask);
    }
    public boolean getSpriteLeftEnable() {
        return getStatusAt(2, mask);
    }
    public boolean getBackgroundLeftEnable() {
        return getStatusAt(1, mask);
    }
    public void getGray(boolean b) {
        return getStatusAt(0, mask);
    }
    
    
    public void setVBlank(boolean b) {
        stat = setStatusAt(b, 7, stat);
    }
    public void setSpriteHit(boolean b) {
        stat = setStatusAt(b, 6, stat);
    }
    public void setSpriteOverflow(boolean b) {
        stat = setStatusAt(b, 5, stat);
    }
    public boolean getVBlank() {
        return getStatusAt(7, stat);
    }
    public boolean getSpriteHit() {
        return getStatusAt(6, stat);
    }
    public boolean getSpriteOverflow() {
        return getStatusAt(5, stat);
    }

    

    private int setStatusAt(boolean b, int i, int register) {
        return b ? register | (1 << i)
                : register & ~(1 << i);
    }
    private boolean getStatusAt(int i, int register) {
        return ((register >> i) & 1) == 1;
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
