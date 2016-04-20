package edu.gvsu.cis.radeckia.python;

import java.util.ArrayList;
import edu.gvsu.cis.radeckia.python.Cell;

/**
 * Created by Alex on 4/19/2016.
 */
public class GameLogic {

    int[][] gameBoard;

    public ArrayList<Cell> getNonEmptyTiles() {

        ArrayList<Cell> nonEmptyTiles = new ArrayList<>();

        for(int r = 0; r < gameBoard.length; r++) {
            for(int c = 0; c < gameBoard[r].length; c++) {
                if(gameBoard[r][c] != 0) {
                    Cell nonZeroCell = new Cell(r, c, gameBoard[r][c]);
                    nonEmptyTiles.add(nonZeroCell);
                }
            }
        }
        return nonEmptyTiles;
    }
}
