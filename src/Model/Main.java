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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    System.err.println("Could not set system look and feel: " + e.getMessage());
                }
                
                BoardController controller = new BoardController();
                controller.initializeGui();
                JOptionPane.showMessageDialog(null,
                        "Welcome to Monopoly\n\n" +
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
