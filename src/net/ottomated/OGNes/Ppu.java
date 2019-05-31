package net.ottomated.OGNes;

import net.ottomated.OGNes.instructions.Instruction;

public class Ppu {

    private int[] vRAM; // 0x10000 bytes
    private int[] oam; // 0x100 bytes

    public int ctrl;
    public int mask;
    public int stat;
    public int oamAddr;
    public int scrl;
    public int addr;

    public int oamRead() {
      return oam[oamAddr];
    }
    
    public void oamWrite(int val) {
      oam[oamAddr] = val;
      oamAddr++;
    }
    
    public int vramRead() {
      return vRAM[addr];
    }
    
    public void vramWrite(int val) {
      vRAM[addr] = val;
      addr += getVRAMIncrement();
    }
    
    public void oamDMAWrite(int highByte){
      for(int i = 0; i < 256; i++){
         this.oam[i] = cpu.memory[highByte * 256 + i];
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
        vRAM = new int[0x10000];
        oam = new int[0x100];
        
        ctrl = 0;
        mask = 0;
        scrl = 0;

        int i;

        // RAM
        for (i = 0; i <= 0x2000; i++) {
            this.vRAM[i] = 0xFF;
        }

        // Clear vRAM
        for (i = 0x2001; i < vRAM.length; i++) {
            this.vRAM[i] = 0;
        }
        pc = 0x8000;
        sp = 0x01ff;
    }
    public void loadProgram(int[] data) {
        System.arraycopy(data, 0, vRAM, 0x8000, data.length);
    }

    public void cycle() {
        if (instruction == null || instruction.done) {
            instruction = Instruction.parse(this);
        } else {
            instruction.cycle();
        }
    }

    public int peek(int loc) {
        return this.vRAM[loc];
    }

    public void set(int loc, int b) {
        this.vRAM[loc] = b;
    }

    @Override
    public String toString() {
        return "===== CPU =====\n" +
                "PC: $" + Integer.toHexString(pc) +
                "  vRAM[pc]: $" + Integer.toHexString(vRAM[pc]) + "\n" +
                "SP: $" + Integer.toHexString(sp) + "\n" +
                "X:  $" + Integer.toHexString(x) + "\n" +
                "Y:  $" + Integer.toHexString(y) + "\n" +
                "A:  $" + Integer.toHexString(a) + "\n" +
                "STATUS: 0b" + Integer.toBinaryString(status) + "\n" +
                "Current intruction: " + instruction;
    }
}
