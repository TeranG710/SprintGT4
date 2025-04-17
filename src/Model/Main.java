/*
* CSCI 234: Intro to Software Engineering
* Group: Giovanny, Jamell, Matt, Deborah
* Purpose: This class is responsible for creating the game board, players, and running the game loop.
* Team Member(s) responsible: Giovanny, Jamell, Matt, Deborah
* */

package Model;
import Model.Exceptions.PlayerNotFoundException;
import View.Gui;
import javax.swing.*;


public class Main {
    public static void main(String[] args) throws PlayerNotFoundException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                    new Gui();
            }
        });
    }
}
