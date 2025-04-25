/*
 * CSCI 234: Intro to Software Engineering
 * Group: Giovanny, Jamell, Matt, Deborah
 * Purpose: Controller for human player interactions
 * Team Member(s) responsible: Matt
 * */

package Controller;

import Model.Board.Banker;
import Model.Board.HumanPlayer;
import Model.Board.Player;
import Model.Exceptions.InsufficientFundsException;
import Model.Exceptions.PlayerNotFoundException;
import Model.Property.Property;
import View.Gui;

import javax.swing.*;

/**
 * Controller class for managing human player interactions.
 * This class handles all user inputs and decision making for human players.
 */
public class HumanPlayerController {
    private HumanPlayer player;
    private Banker banker;
    private Gui gui;
    
    /**
     * Constructor for HumanPlayerController.
     * 
     * @param player The human player
     */
    public HumanPlayerController(HumanPlayer player) {
        this.player = player;
        this.banker = Banker.getInstance();
    }
    
    /**
     * Set the GUI reference.
     * 
     * @param gui The GUI reference
     */
    public void setGui(Gui gui) {
        this.gui = gui;
    }
    
    /**
     * Handle property purchase decision using the enhanced property purchase dialog.
     * 
     * @param property The property to potentially purchase
     * @return True if the player decided to buy, false otherwise
     */
    public boolean handlePropertyPurchase(Property property) {
        if (gui == null) {
            return false;
        }
        
        try {
            int balance = banker.getBalance(player);
            int price = property.getPurchasePrice();
            if (balance < price) {
                JOptionPane.showMessageDialog(gui.getMainFrame(),
                        "You don't have enough money to buy " + property.getName() + ".\n" +
                        "It costs $" + price + " but you only have $" + balance + ".",
                        "Insufficient Funds", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            boolean wantsToBuy = gui.showPropertyPurchaseDialog(property, balance);
            
            if (wantsToBuy) {
                try {
                    banker.sellProperty(property, player);
                    gui.updatePlayerInfo(null);
                    gui.updatePropertyOwnership(property, player);
                    gui.displayMessage(player.getName() + " purchased " + property.getName() + " for $" + price);
                    
                    return true;
                } catch (InsufficientFundsException | PlayerNotFoundException e) {
                    JOptionPane.showMessageDialog(gui.getMainFrame(),
                            "Purchase failed: " + e.getMessage(),
                            "Purchase Failed", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                gui.displayMessage(player.getName() + " declined to purchase " + property.getName());
            }
        } catch (PlayerNotFoundException e) {
            System.err.println("Error getting player balance: " + e.getMessage());
            JOptionPane.showMessageDialog(gui.getMainFrame(),
                    "Error: " + e.getMessage(),
                    "Player Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    /**
     * Handle getting out of jail.
     * 
     * @return True if the player got out of jail
     */
    public boolean handleJailRelease() {
        if (!player.isInJail() || gui == null) {
            return false;
        }
        
        String[] options = {"Pay $50", "Use Get Out of Jail Free Card", "Roll for Doubles"};
        
        int choice = JOptionPane.showOptionDialog(gui.getMainFrame(),
                "You are in jail. How would you like to get out?",
                "Jail Options", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        
        switch (choice) {
            case 0: // Pay $50
                try {
                    int balance = banker.getBalance(player);
                    if (balance >= 50) {
                        banker.withdraw(player, 50);
                        player.setInJail(false);
                        player.resetTurnsInJail();
                        
                        JOptionPane.showMessageDialog(gui.getMainFrame(),
                                "You paid $50 and got out of jail!",
                                "Out of Jail", JOptionPane.INFORMATION_MESSAGE);
                        
                        gui.updatePlayerInfo(null);
                        return true;
                    } else {
                        JOptionPane.showMessageDialog(gui.getMainFrame(),
                                "You don't have enough money to pay the fine.",
                                "Insufficient Funds", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (PlayerNotFoundException | InsufficientFundsException e) {
                    JOptionPane.showMessageDialog(gui.getMainFrame(),
                            "Error: " + e.getMessage(),
                            "Payment Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
                
            case 1: // Use Get Out of Jail Free Card
                if (player.hasGetOutOfJailFreeCard()) {
                    player.useGetOutOfJailFreeCard();
                    player.setInJail(false);
                    player.resetTurnsInJail();
                    
                    JOptionPane.showMessageDialog(gui.getMainFrame(),
                            "You used a Get Out of Jail Free card!",
                            "Out of Jail", JOptionPane.INFORMATION_MESSAGE);
                    
                    gui.updatePlayerInfo(null);
                    return true;
                } else {
                    JOptionPane.showMessageDialog(gui.getMainFrame(),
                            "You don't have a Get Out of Jail Free card.",
                            "No Card", JOptionPane.WARNING_MESSAGE);
                }
                break;
                
            case 2: // Roll for doubles (handled by dice roll in turn manager)
                JOptionPane.showMessageDialog(gui.getMainFrame(),
                        "Roll the dice. If you get doubles, you'll get out of jail!",
                        "Roll for Doubles", JOptionPane.INFORMATION_MESSAGE);
                break;
                
            default:
                break;
        }
        
        return false;
    }
    
    /**
     * Get the human player.
     * 
     * @return The human player
     */
    public Player getPlayer() {
        return player;
    }
}
