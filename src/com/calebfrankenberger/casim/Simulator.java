/**
 * Simulator is an extension of JComponent which will hold a
 * CellularAutomata. It will update the CA and repaint on an
 * interval.
 *
 * @author Caleb Frankenberger
 * @date 02/24/2024
 */

package com.calebfrankenberger.casim;

import com.calebfrankenberger.casim.model.CellularAutomata;

import javax.swing.*;
import java.awt.*;

public class Simulator extends JComponent {

    private final long paintThreshold; // How often to update the image
    private final long updateThreshold; // How often to update the cellular automata model

    private final CellularAutomata cellularAutomata;

    private boolean running;

    /**
     * @param fps desired frames per second
     * @param ups desired updates per second
     * @param cellularAutomata the model to simulate
     */
    public Simulator(int fps, int ups, CellularAutomata cellularAutomata) {
        this.paintThreshold = 1000000000/fps; // 1 second in nanoseconds divided by target FPS
        this.updateThreshold = 1000000000/ups; // 1 second in nanoseconds divided by target UPS

        this.cellularAutomata = cellularAutomata;
        this.running = false;
    }

    // Gets called every repaint
    @Override
    protected void paintComponent(Graphics graphics) {
        cellularAutomata.drawModel(graphics);
    }

    // Begin the simulation
    public void startSimulation() {
        this.running = true;

        long lastFPS = 0, lastUPS = 0, lastFPSUPSOutput = 0;
        int fps = 0, ups = 0;

        while(this.running) {
            // Outputs the FPS and UPS every seconds
            if((System.nanoTime() - lastFPSUPSOutput) > 1000000000) {
                System.out.print(" * FPS: " + (double)fps);
                System.out.println(" UPS: " + (double)ups);

                fps = 0;
                ups = 0;

                lastFPSUPSOutput = System.nanoTime();
            }

            // Simulator update happens here
            if((System.nanoTime() - lastUPS) > updateThreshold) {
                lastUPS = System.nanoTime();

                // Simulate one new generation
                cellularAutomata.simulateGeneration();

                ups++;
            }

            // Graphics repaint happens here
            if((System.nanoTime() - lastFPS) > paintThreshold) {
                lastFPS = System.nanoTime();
                repaint(); // Redraws the model
                fps++;
            }

            // Calculate next frame, or skip if running behind
            if(!((System.nanoTime() - lastUPS) > updateThreshold || (System.nanoTime() - lastFPS) > paintThreshold)) {
                long nextScheduledUP = lastUPS + updateThreshold;
                long nextScheduledDraw = lastFPS + paintThreshold;

                long minScheduled = Math.min(nextScheduledUP, nextScheduledDraw);

                long nanosToWait = minScheduled - System.nanoTime();

                // For safety
                if(nanosToWait <= 0)
                    continue;

                try { Thread.sleep(nanosToWait / 1000000); }
                catch (InterruptedException exception) { exception.printStackTrace();}
            }
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    public void toggleRunning() {
        this.running = !this.running;
    }

}
