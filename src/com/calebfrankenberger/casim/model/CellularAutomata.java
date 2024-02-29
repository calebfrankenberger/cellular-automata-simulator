/**
 * Outlines required methods that every cellular automata model
 * will need to have at the minimum.
 *
 * @author Caleb Frankenberger
 * @date 02/24/24
 */
package com.calebfrankenberger.casim.model;

import java.awt.*;

public interface CellularAutomata {

    public void simulateGeneration();
    public int getGenerationCount();
    public void drawModel(Graphics graphics);

}
