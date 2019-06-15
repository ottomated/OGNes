package net.ottomated.OGNes.mappers;

import net.ottomated.OGNes.Nes;

public abstract class Mapper {
    public static String name(int id) {
        switch (id) {
            case 0:
                return "Direct Access";
            case 1:
                return "Nintendo MMC1";
            case 2:
                return "UNROM";
            case 3:
                return "CNROM";
            case 4:
                return "Nintendo MMC3";
            case 5:
                return "Nintendo MMC5";
            case 6:
                return "FFE F4xxx";
            case 7:
                return "AOROM";
            case 8:
                return "FFE F3xxx";
            case 9:
                return "Nintendo MMC2";
            case 10:
                return "Nintendo MMC4";
            case 11:
                return "Color Dreams Chip";
            case 12:
                return "FFE F6xxx";
            case 15:
                return "100-in-1 switch";
            case 16:
                return "Bandai chip";
            case 17:
                return "FFE F8xxx";
            case 18:
                return "Jaleco SS8806 chip";
            case 19:
                return "Namcot 106 chip";
            case 20:
                return "Famicom Disk System";
            case 21:
                return "Konami VRC4a";
            case 22:
                return "Konami VRC2a";
            case 23:
                return "Konami VRC2a";
            case 24:
                return "Konami VRC6";
            case 25:
                return "Konami VRC4b";
            case 32:
                return "Irem G-101 chip";
            case 33:
                return "Taito TC0190/TC0350";
            case 34:
                return "32kB ROM switch";

            case 64:
                return "Tengen RAMBO-1 chip";
            case 65:
                return "Irem H-3001 chip";
            case 66:
                return "GNROM switch";
            case 67:
                return "SunSoft3 chip";
            case 68:
                return "SunSoft4 chip";
            case 69:
                return "SunSoft5 FME-7 chip";
            case 71:
                return "Camerica chip";
            case 78:
                return "Irem 74HC161/32-based";
            case 91:
                return "Pirate HK-SF3 chip";
            default:
                return "Unknown Mapper";
        }
    }

    public static Mapper fromID(int id) {
        return new DirectAccess();
    }

    public Nes nes;

    public abstract int read(int addr);

    public abstract void latchAccess(int addr); // Does nothing. This is used by the MMC2 mapper.

    public abstract void clockIrqCounter(); // Does nothing. This is used by the MMC3 mapper.

    public abstract void write(int addr, int val);

    public abstract void loadROM();

    public abstract void reset();
}
