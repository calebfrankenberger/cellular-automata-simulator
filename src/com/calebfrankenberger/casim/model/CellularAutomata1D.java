/**
 * This class is used to model a one-dimensional cellular automata
 *
 * @author Caleb Frankenberger
 * @date 02/24/2024
 */
package com.calebfrankenberger.casim.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class CellularAutomata1D implements CellularAutomata {

    private final int[] ruleset; // The ruleset to use when calculating a cells next state

    private final int pixelWidth, pixelHeight; // The width and height of the entire canvas, in pixels
    private final int cellSize; // Size, in pixels, of each cell
    private final int width, height; // The width and height of the grid of cells

    private int[] cells; // This array stores the single current generation of the CA
    private int[][] displayGrid; // This grid will store multiple generations over time

    private int generationCount; // Total number of generations that have been simulated

    /**
     *
     * @param width width of canvas in pixels
     * @param height height of canvas in pixels
     * @param cellSize size of each square cells in pixels
     * @param ruleset ruleset to use in state calculations
     * @param initialGeneration starting generationCount
     */
    public CellularAutomata1D(int width, int height, int cellSize, int[] ruleset, int[] initialGeneration) {
        this.pixelWidth = width;
        this.pixelHeight = height;
        this.cellSize = cellSize;
        this.width = pixelWidth/cellSize;
        this.height = pixelHeight/cellSize;

        this.displayGrid = new int[width][height];

        int[] initGen = new int[width];
        for(int i = 0; i < width; i++) {
            initGen[i] = initialGeneration[i];
        }

        this.cells = initGen;
        this.ruleset = ruleset;
        this.generationCount = 0;
    }

    // If no initial generationCount is given, all cells default to 0 except the middle
    public CellularAutomata1D(int width, int height, int cellSize, int[] ruleset) {
        this.pixelWidth = width;
        this.pixelHeight = height;
        this.cellSize = cellSize;
        this.width = pixelWidth/cellSize;
        this.height = pixelHeight/cellSize;

        this.displayGrid = new int[this.width][this.height];
        this.cells = new int[this.width];

        for(int i = 0; i < this.width; i++)
            this.cells[i] = 0;
        this.cells[this.width/2] = 1;
        this.displayGrid[this.width/2][0] = 1;

        this.ruleset = ruleset;
        this.generationCount = 0;
    }

    // Simulate one generationCount of the CA
    public void simulateGeneration() {
        // Create a new array to store the next generationCount
        int[] nextGeneration = new int[cells.length];
        int row = generationCount;

        // For every cell, determine its next state by looking at its neighbors
        for(int i = 0; i < cells.length; i++) {
            int newState = calculateNextState(i);
            nextGeneration[i] = newState;

            if(this.generationCount >= this.height)
                row = this.height-1;

            displayGrid[i][row] = newState;
        }

        // If at the bottom of the screen, scroll the display grid
        if(this.generationCount >= this.height-1) {
            scrollDisplayGrid();
        }

        this.cells = nextGeneration;
        this.generationCount++;
    }

    // Simulate n generations at a time
    public void simulateGenerations(int n) {
        for(int i = 0; i < n; i++)
            simulateGeneration();
    }

    // Calculates next state for a given cell index
    private int calculateNextState(int i) {
        int leftState, rightState, currState;

        // If the cell being looked at is on an edge, wrap around to opposite side
        if(i == 0) {
            leftState = cells[cells.length-1];
        } else {
            leftState = cells[i -1];
        }

        if(i == cells.length-1) {
            rightState = cells[0];
        } else {
            rightState = cells[i +1];
        }

        currState = cells[i];

        // Determine the next state using the ruleset
        return applyRuleset(leftState, rightState, currState);
    }

    // Takes a cell and its adjacent neighbors, and outputs what the cell's next state should be
    private int applyRuleset(int left, int right, int middle) {
        if(left==1 && right == 1 && middle==1) return ruleset[0];
        if(left==1 && right == 1 && middle==0) return ruleset[1];
        if(left==1 && right == 0 && middle==1) return ruleset[2];
        if(left==1 && right == 0 && middle==0) return ruleset[3];
        if(left==0 && right == 1 && middle==1) return ruleset[4];
        if(left==0 && right == 1 && middle==0) return ruleset[5];
        if(left==0 && right == 0 && middle==1) return ruleset[6];
        if(left==0 && right == 0 && middle==0) return ruleset[7];
        return 0;
    }

    // Shifts every row in the display grid up by one, freeing up the last row for a new generation
    private void scrollDisplayGrid() {
        int[][] newGrid = new int[width][height];

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width-1; j++)
                newGrid[i][j] = displayGrid[i][(j+1) % width];
            newGrid[i][width-1] = 0;
        }
        displayGrid = newGrid;
    }

    // Draws the grid
    public void drawModel(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics;

        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                // Create a new rectangle at its correct location in the grid, of width and height cellSize
                Rectangle2D.Double cell = new Rectangle2D.Double(x*cellSize, y*cellSize, cellSize, cellSize);
                // Determine the rectangle color based off its value in the grid
                if(displayGrid[x][y] == 1) {
                    g2d.setColor(Color.BLACK);
                } else {
                    g2d.setColor(Color.WHITE);
                }
                // Fill the rectangle with correct color
                g2d.fill(cell);
            }
        }
    }

    public int[] getCurrentGeneration() { return this.cells; };

    public int[] getRules() { return this.ruleset; };

    public int getGenerationCount() { return this.generationCount; }

}
