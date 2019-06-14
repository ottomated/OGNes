package net.ottomated.OGNes;

public class Register {
    public int[] reg = new int[8];
    public int value = 0;

    public Register(int size){
        this.reg = new int[size];
        this.value = 0;
    }
}
