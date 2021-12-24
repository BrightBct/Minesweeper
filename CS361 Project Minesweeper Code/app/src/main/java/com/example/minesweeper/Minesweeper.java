package com.example.minesweeper;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Minesweeper {

    private final int bomb_number, grid_size_x, grid_size_y;
    private int flag_count;
    private boolean game_over, mode;
    private final List<Cell> cells;
    private Thread thread;

    public Minesweeper(int bomb_number, int grid_size_x, int grid_size_y){
        //setting value from parameter
        this.bomb_number = bomb_number;
        this.grid_size_x = grid_size_x;
        this.grid_size_y = grid_size_y;
        this.game_over = false;
        this.mode = true;
        this.flag_count = 0;
        this.cells = new ArrayList<>();
        for(int round = 0 ; round < grid_size_x ; round++){
            for(int round2 = 0 ; round2 < grid_size_y ; round2++){
                cells.add(new Cell(0, (round * grid_size_y) + round2));
            }
        }
    }

    public void generateGrid(int bomb_number, int position){
        int bomb_place = 0;
        //loop until bomb in grid is equal to number of bomb
        while(bomb_place < bomb_number){
            //random x and y to set xy position to bomb
            int x = new Random().nextInt(grid_size_y);
            int y = new Random().nextInt(grid_size_x);

            //if xy position has not been assigned (getValue == 0) it will set to bomb
            //and xy position must not equal position (cell that user click)
            if(cellAt(x, y).getValue() == 0 && (x + (y * grid_size_y)) != position) {
                cells.set(x + (y * grid_size_y), new Cell(-1, x + (y * grid_size_y)));
                bomb_place++;
            }
        }

        //loop for set value next to bomb cell
        for (int x = 0; x < grid_size_y; x++) {
            for (int y = 0; y < grid_size_x; y++) {
                //cell at position xy must not bomb cell
                if(cellAt(x, y).getValue() != -1){
                    List<Cell> adjacentCells = adjacentCells(x, y);

                    //loop for look around that cell to count bomb
                    //after that set value cell = count bomb
                    int count_bombs = 0;
                    for (Cell cell: adjacentCells) {
                        if (cell.getValue() == -1) {
                            count_bombs++;
                        }
                    }
                    if (count_bombs > 0) {
                        cells.set(x + (y * grid_size_y), new Cell(count_bombs, x + (y * grid_size_y)));
                    }

                }
            }
        }
    }

    //check cell at position xy is out of bound or less than 0
    //if not then return cell position
    public Cell cellAt(int x, int y) {
        if (x < 0 || x >= grid_size_y || y < 0 || y >= grid_size_x) {
            return null;
        }
        return cells.get(x + (y * grid_size_y));
    }

    //loop for check cell around cell at position xy
    public List<Cell> adjacentCells(int x, int y) {
        List<Cell> adjacentCells = new ArrayList<>();

        List<Cell> cellsList = new ArrayList<>();
        cellsList.add(cellAt(x-1, y-1));
        cellsList.add(cellAt(x-1, y));
        cellsList.add(cellAt(x-1, y+1));
        cellsList.add(cellAt(x, y-1));
        cellsList.add(cellAt(x, y+1));
        cellsList.add(cellAt(x+1, y-1));
        cellsList.add(cellAt(x+1, y));
        cellsList.add(cellAt(x+1, y+1));

        for (Cell cell: cellsList) {
            if (cell != null) {
                adjacentCells.add(cell);
            }
        }

        return adjacentCells;
    }

    //return is Game won?
    public boolean isGameWon() {
        int numbers_unrevealed = 0;
        for (Cell c: cells) {
            if (c.getValue() != -1 && c.getValue() != 0 && !c.isReveal()) {
                numbers_unrevealed++;
            }
        }

        return numbers_unrevealed == 0;
    }

    //if user is normal mode then do method clear for cell that has been click
    public void clear(@NonNull Cell cell) {
        try {
            thread = new Thread(() -> {
                if (!cell.isFlag()) {
                    int index = cells.indexOf(cell);
                    cells.get(index).setReveal(true);

                    if (cell.getValue() == -1) {
                        game_over = true;
                    } else if (cell.getValue() == 0) {
                        try {
                            Thread thread2 = new Thread(() -> {
                                List<Cell> toClear = new ArrayList<>();
                                List<Cell> toCheckAdjacent = new ArrayList<>();

                                toCheckAdjacent.add(cell);

                                //loop for find cell that can be reveal
                                while (toCheckAdjacent.size() > 0) {
                                    Cell c = toCheckAdjacent.get(0);
                                    int cellIndex = cells.indexOf(c);
                                    int[] cellPos = toXY(cellIndex);
                                    for (Cell adjacent : adjacentCells(cellPos[0], cellPos[1])) {
                                        if (adjacent.getValue() == 0) {
                                            if (!toClear.contains(adjacent)) {
                                                if (!toCheckAdjacent.contains(adjacent)) {
                                                    toCheckAdjacent.add(adjacent);
                                                }
                                            }
                                        } else {
                                            if (!toClear.contains(adjacent)) {
                                                toClear.add(adjacent);
                                            }
                                        }
                                    }
                                    toCheckAdjacent.remove(c);
                                    toClear.add(c);
                                }

                                for (Cell c : toClear) {
                                    if (c.isFlag()) {
                                        c.setFlag(false);
                                        flag_count--;
                                    }
                                    c.setReveal(true);
                                }
                            });
                            thread2.start();
                            thread2.join();
                        } catch (InterruptedException ignored){

                        }
                    }
                }
            });

            thread.start();
            thread.join();

        } catch (InterruptedException ignored) {

        }
    }

    //if user is flag mode then do method flag for cell that has been click
    public void flag(Cell cell) {
        try{
            thread = new Thread(() -> {
                if(flag_count < bomb_number){
                    cell.setFlag(!cell.isFlag());
                }else if(cell.isFlag()){
                    cell.setFlag(!cell.isFlag());
                }

                int count = 0;
                for (Cell c: cells) {
                    if (c.isFlag()) {
                        count++;
                    }
                }
                flag_count = count;
            });
            thread.start();
            thread.join();
        } catch (InterruptedException ignored) {

        }

    }

    //if user click cell
    public void handleCellClick(Cell cell) {
        try {
            thread = new Thread(() -> {
                if (!game_over && !isGameWon() && !cell.isReveal()) {
                    if (mode) {
                        clear(cell);
                    } else {
                        flag(cell);
                    }
                }
            });
            thread.start();
            thread.join();
        } catch (InterruptedException ignored) {

        }
    }

    //set mode
    public void setMode(boolean mode) {
        this.mode = mode;
    }

    //method for reveal all bomb
    public void revealAllBombs() {
        for (Cell c: cells) {
            if (c.getValue() == -1) {
                c.setReveal(true);
            } if (c.isFlag()){
                c.setFlag(!c.isFlag());
            }
        }
    }

    //return position xy
    public int[] toXY(int index) {
        int y = index / grid_size_y;
        int x = index - (y*grid_size_y);
        return new int[]{x, y};
    }

    //return number of bomb
    public int getBomb_number() {
        return bomb_number;
    }

    //return is game over?
    public boolean isGame_over(){
        return game_over;
    }

    //return number of flag cell
    public int getFlag_count() {
        return flag_count;
    }

    //return all cell
    public List<Cell> getCells() {
        return cells;
    }

    public Cell getCell(int position){
        return cells.get(position);
    }
}
