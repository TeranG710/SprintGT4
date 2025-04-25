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
    public static void main(String[] args) throws PlayerNotFoundException {
        // Use SwingUtilities to ensure GUI is created on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Set up look and feel
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    System.err.println("Could not set system look and feel: " + e.getMessage());
                }
                
                // Create the board controller
                BoardController controller = new BoardController();
                
                // Initialize the GUI through the controller
                controller.initializeGui();
                
                // Display welcome message or instructions
                JOptionPane.showMessageDialog(null,
                        "Welcome to Monopoly Advanced GUI!\n\n" +
                        "Click 'New Game' to start a new game.\n" +
                        "You can choose the number of human and computer players.\n\n" +
                        "Features:\n" +
                        "- Property auctions\n" +
                        "- Computer players\n" +
                        "- Graphical tokens and cards",
                        "Monopoly Advanced GUI", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
}
