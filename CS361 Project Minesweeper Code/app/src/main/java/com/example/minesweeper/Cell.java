package com.example.minesweeper;

public class Cell {

    private final int value;
    private final int ID;

    private boolean isReveal;
    private boolean isFlag;

    public Cell(int value, int id){
        //setting value from parameter and default
        this.value = value;
        this.ID = id;
        this.isReveal = false;
        this.isFlag = false;
    }

    //return value (bomb = -1, another is cell that can click [0, 1, 2, 3, 4 5 6, 7, 8])
    public int getValue() {
        return value;
    }

    //return that cell is reveal?
    public boolean isReveal() {
        return isReveal;
    }

    //set reveal of cell
    public void setReveal(boolean reveal) {
        isReveal = reveal;
    }

    //return cell is flag or not
    public boolean isFlag() {
        return isFlag;
    }

    //set cell to flag or not
    public void setFlag(boolean flag) {
        isFlag = flag;
    }

    //return id of cell
    public int getID(){
        return ID;
    }
}
