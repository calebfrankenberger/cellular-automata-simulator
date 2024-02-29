/**
 * This is a variation on the 2D CA with rules tweaked so
 * that the model generates an output resembling a cave system
 *
 * @author Caleb Frankenberger
 * @date 02/25/2024
 */

package com.calebfrankenberger.casim.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class CellularAutomataCave implements CellularAutomata {

    private final int pixelWidth, pixelHeight; // The width and height of the entire canvas, in pixels
    private final int cellSize; // Size, in pixels, of each cell
    private final int width, height; // The width and height of the grid of cells

    /**
     *  This 2D array will store the values for every cell in the CA.
     *  Any cell with a value of 0 is dead, and a number greater than zero
     *  is the number of generations that cell has been alive for
     */
    private int[][] cells;

    private int generationCount; // Total number of generations that have been simulated

    /**
     * @param width width of canvas in pixels
     * @param height height of canvas in pixels
     * @param cellSize size of each square cells in pixels
     * @param initialGeneration starting generationCount
     */
    public CellularAutomataCave(int width, int height, int cellSize, int[][] initialGeneration) {
        this.pixelWidth = width;
        this.pixelHeight = height;
        this.cellSize = cellSize;
        this.width = pixelWidth/cellSize;
        this.height = pixelHeight/cellSize;

        // Copy the provided initial generationCount into current generationCount array
        int[][] initGen = new int[width][height];
        for(int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                initGen[x][y] = initialGeneration[x][y];
            }
        }

        this.cells = initGen;
        this.generationCount = 0;
    }

    // If no initial generationCount is given, randomly make some of the cells alive
    public CellularAutomataCave(int width, int height, int cellSize) {
        this.pixelWidth = width;
        this.pixelHeight = height;
        this.cellSize = cellSize;
        this.width = pixelWidth/cellSize;
        this.height = pixelHeight/cellSize;

        Random random = new Random();

        this.cells = new int[width][height];
        for(int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if(random.nextInt(2) == 1)
                    this.cells[x][y] = 1;
                else
                    this.cells[x][y] = 0;
            }
        }

        this.generationCount = 0;
    }

    // Simulate one generationCount of the CA
    public void simulateGeneration() {
        // Create a new array to store the next generationCount
        int[][] nextGeneration = new int[width][height];

        // For every cell on the grid, calculate its next state by looking at how many alive neighbors it hass
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                int nextState = calculateNextState(x, y);
                nextGeneration[x][y] = nextState;
            }
        }

        this.generationCount++;
        cells = nextGeneration;
    }

    // Simulate n generations at a time
    public void simulateGenerations(int n) {
        for(int i = 0; i < n; i++)
            simulateGeneration();
    }

    // Calculates a cells next state by checking all neighbors and applying rules
    private int calculateNextState(int x, int y) {
        int neighbors = getAliveNeighbors(x, y);
        int currentState = cells[x][y];
        boolean stayingAlive = false;

        int birthThreshold = 5;
        int survivalThreshold = 4;

        if(currentState <= 0) {
            if(neighbors < birthThreshold) // Not enough living neighbors for birth
                return 0;
        }

        if(currentState >= 1) {
            if(neighbors < survivalThreshold) // Too many living neighbors, must die
                return 0;
        }

        return (currentState+1); // Survival
    }

    // Returns the sum of all alive neighbors. If neighbor is out of bounds, it counts as not alive
    private int getAliveNeighbors(int x, int y) {
        int sum = 0;
        int neighborX, neighborY;

        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                neighborX = x+i;
                neighborY = y+j;

                // Edge cases (X)
                if(neighborX < 0)
                    continue;
                else if(neighborX >= width)
                    continue;

                // Edge cases (Y)
                if(neighborY < 0)
                    neighborY = height-1;
                else if(neighborY >= height)
                    neighborY = 0;

                // Only sum the live cells
                if(cells[neighborX][neighborY] >= 1)
                    sum+=1;
            }
        }
        if(cells[x][y] >= 1)
            sum = sum - 1; // Don't count the current cell as a live neighbor
        return sum;
    }

    // Draws the grid
    public void drawModel(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics;
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                // Create a new rectangle at its correct location in the grid, of width and height cellSize
                Rectangle2D.Double cell = new Rectangle2D.Double(x*cellSize, y*cellSize, cellSize, cellSize);
                // Determine the rectangle color based off its value in the grid
                int age = getCurrentGeneration()[x][y]; // 0 is a dead cell, any other number is how many generations that cell has been alive
                if(age > 0) {
                    g2d.setColor(new Color(116, 85, 52));
                } else {
                    g2d.setColor(new Color(49, 35, 26));
                }
                // Fill the rectangle with correct color
                g2d.fill(cell);
            }
        }
    }

    public int[][] getCurrentGeneration() { return this.cells; };

    public int getGenerationCount() { return this.generationCount; }

}
