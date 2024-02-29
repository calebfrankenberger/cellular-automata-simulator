package com.calebfrankenberger.casim;

import com.calebfrankenberger.casim.model.CellularAutomata;
import com.calebfrankenberger.casim.model.CellularAutomata1D;
import com.calebfrankenberger.casim.model.CellularAutomata2D;
import com.calebfrankenberger.casim.model.CellularAutomataCave;

import javax.swing.*;
import java.util.Scanner;

public class Main {

    public static final int width = 1000; // Width of the simulation canvas
    public static final int height = 1000; // Height of the simulation canvas
    public static final int cellSize = 10; // Number of pixels wide and tall a single cell should take up
    public static final int fps = 4; // Number of times per second the game should repaint
    public static final int ups = 4; // Number of times per second the model should update

    public static JFrame frame;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String type = scanner.next();

        CellularAutomata cellularAutomata = null;

        // Validate user input
        switch(type) {
            case "one-dimensional":
                int[] ruleset = {0, 0, 1, 1, 1, 0, 0, 1};
                cellularAutomata = new CellularAutomata1D(width, height, cellSize, ruleset);
                break;
            case "two-dimensional":
                 cellularAutomata = new CellularAutomata2D(width, height, cellSize);
                 break;
            case "cave":
                cellularAutomata = new CellularAutomataCave(width, height, cellSize);
                break;
            default:
                invalidInput();
        }

        // Once input is validated, create a new Simulator to simulate the CA specified
        Simulator simulator = new Simulator(fps, ups, cellularAutomata);

        // From Swing documentation
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setupGUI();
                frame.add(simulator);
            }
        });

        simulator.startSimulation();
    }

    // Sets up the JFrame window to display the simulation
    public static void setupGUI() {
        // Set up the frame
        frame = new JFrame();
        frame.setSize(width, height);
        frame.setTitle("Cellular Automata - Caleb Frankenberger");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Change frame icon
        ImageIcon icon = new ImageIcon("src/icon.png");
        frame.setIconImage(icon.getImage());

        frame.setLocationRelativeTo(null); // Center window on screen
        frame.setVisible(true); // Show frame
    }

    // If an invalid CA type is input, exit the application
    public static void invalidInput() {
        System.out.println("Invalid cellular automata type input! Valid types are: ");
        System.out.println(" * one-dimensional");
        System.out.println(" * two-dimensional");
        System.out.println(" * cave");
        System.exit(0);
    }

    // 1D CA rulesets:
    //int[] ruleset = {1, 0, 0, 1, 0, 1, 1, 0} // Rule 150
    //int[] ruleset = {0,1,1,0,1,1,1,0};   // Rule 90
    //int[] ruleset = {0,0,0,1,1,1,1,0};   // Rule 30
    //int[] ruleset = {0,1,1,0,1,1,1,0};   // Rule 110

}
