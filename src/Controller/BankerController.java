/*
 * CSCI 234: Intro to Software Engineering
 * Group: Giovanny, Jamell, Matt, Deborah
 * Purpose: Controller for the Banker functionality
 * Team Member(s) responsible: Matt
 * */

package Controller;

import Model.Board.Banker;
import Model.Board.Player;
import Model.Exceptions.InsufficientFundsException;
import Model.Exceptions.PlayerNotFoundException;
import Model.Property.Property;
import View.Gui;

import javax.swing.SwingUtilities;
import java.util.ArrayList;

/**
 * Controller class for managing banker operations.
 * This class handles all banking transactions and property management.
 */
public class BankerController {
    private Banker banker;
    private Gui gui;
    
    /**
     * Constructor for BankerController.
     */
    public BankerController() {
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
     * Get the balance for a player.
     * 
     * @param player The player
     * @return The player's balance
     */
    public int getBalance(Player player) {
        try {
            return banker.getBalance(player);
        } catch (PlayerNotFoundException e) {
            System.err.println("Player not found: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Sell a property to a player.
     * 
     * @param property The property to sell
     * @param player The player buying the property
     * @return True if the sale was successful
     */
    public boolean sellProperty(Property property, Player player) {
        try {
            // Check for null arguments
            if (property == null || player == null) {
                System.err.println("Error: Null property or player in sellProperty");
                return false;
            }
            
            // Execute the transaction
            banker.sellProperty(property, player);
            
            // Update GUI if needed - ensure thread safety
            if (gui != null) {
                // Get all players to update
                ArrayList<Player> allPlayers = new ArrayList<>();
                try {
                    allPlayers.addAll(banker.getAllPlayers());
                } catch (Exception e) {
                    System.err.println("Error getting players for GUI update: " + e.getMessage());
                }
                
                // Use final variables for lambda
                final ArrayList<Player> players = allPlayers;
                final Property prop = property;
                final Player buyer = player;
                final int price = property.getPurchasePrice();
                
                // Perform GUI updates on the EDT
                if (SwingUtilities.isEventDispatchThread()) {
                    gui.updatePlayerInfo(players);
                    gui.updatePropertyOwnership(prop, buyer);
                    gui.displayMessage(buyer.getName() + " purchased " + prop.getName() + 
                            " for $" + price);
                } else {
                    SwingUtilities.invokeLater(() -> {
                        gui.updatePlayerInfo(players);
                        gui.updatePropertyOwnership(prop, buyer);
                        gui.displayMessage(buyer.getName() + " purchased " + prop.getName() + 
                                " for $" + price);
                    });
                }
            }
            
            return true;
        } catch (PlayerNotFoundException | InsufficientFundsException e) {
            System.err.println("Property purchase error: " + e.getMessage());
            if (gui != null) {
                gui.displayMessage("Purchase failed: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * Collect rent from a player for a property.
     * 
     * @param property The property
     * @param player The player paying rent
     * @return True if rent was collected successfully
     */
    public boolean collectRent(Property property, Player player) {
        try {
            banker.collectRent(property, player);
            
            if (gui != null) {
                String ownerName = property.getOwner() != null ? property.getOwner().getName() : "Bank";
                gui.displayMessage(player.getName() + " paid rent to " + ownerName);
                gui.updatePlayerInfo(null);
            }
            
            return true;
        } catch (PlayerNotFoundException | InsufficientFundsException e) {
            if (gui != null) {
                gui.displayMessage("Rent payment failed: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * Mortgage a property.
     * 
     * @param property The property to mortgage
     * @return True if the property was successfully mortgaged
     */
    public boolean mortgageProperty(Property property) {
        try {
            if (property.canMortgage()) {
                property.mortgage();
                
                if (gui != null) {
                    gui.displayMessage(property.getName() + " was mortgaged for $" + 
                            property.getMortgageValue());
                    gui.updatePlayerInfo(null);
                }
                
                return true;
            }
        } catch (PlayerNotFoundException e) {
            if (gui != null) {
                gui.displayMessage("Mortgage failed: " + e.getMessage());
            }
        }
        
        return false;
    }
    
    /**
     * Unmortgage a property.
     * 
     * @param property The property to unmortgage
     * @return True if the property was successfully unmortgaged
     */
    public boolean unmortgageProperty(Property property) {
        try {
            if (property.unmortgage()) {
                if (gui != null) {
                    gui.displayMessage(property.getName() + " was unmortgaged");
                    gui.updatePlayerInfo(null);
                }
                
                return true;
            }
        } catch (PlayerNotFoundException e) {
            if (gui != null) {
                gui.displayMessage("Unmortgage failed: " + e.getMessage());
            }
        }
        
        return false;
    }
}
