/*
* CSCI 234: Intro to Software Engineering
* Group: Giovanny, Jamell, Matt, Deborah
* Purpose: This class is responsible for creating the game board, players, and running the game loop.
* Team Member(s) responsible: Giovanny, Jamell, Matt, Deborah
* */

package Model;
import Controller.BoardController;
import Model.Exceptions.PlayerNotFoundException;
import javax.swing.*;

/**
 * Main entry point for the Monopoly game application.
 * Sets up the game controller and GUI.
 * Team member(s) responsible: Matt
 */
public class Main {
    public static void main(String[] args) {
        // Always use invokeAndWait to ensure proper sequencing
        try {
            // Set the look and feel first
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Initialize the game on the EDT
            SwingUtilities.invokeAndWait(() -> {
                try {
                    // Create and initialize the controller
                    BoardController controller = new BoardController();

                    // Initialize the GUI
                    controller.initializeGui();

                    // Show welcome message
                    JOptionPane.showMessageDialog(null,
                            "Welcome to Monopoly\n\n" +
                                    "Click 'New Game' to start a new game.\n" +
                                    "You can choose the number of human and computer players.\n\n" +
                                    "How to play:\n" +
                                    "1. Roll the dice to move\n" +
                                    "2. Buy properties when you land on them\n" +
                                    "3. Collect money when passing GO\n" +
                                    "4. End your turn when finished",
                            "Monopoly Game", JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception e) {
                    // Catch and display any errors during startup
                    System.err.println("Error starting game: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "Error starting game: " + e.getMessage(),
                            "Startup Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        } catch (Exception e) {
            // Handle any errors during UI setup
            System.err.println("Error during UI initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
}