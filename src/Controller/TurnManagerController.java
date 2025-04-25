/*
 * CSCI 234: Intro to Software Engineering
 * Group: Giovanny, Jamell, Matt, Deborah
 * Purpose: Controller for managing game turns
 * Team Member(s) responsible: Matt
 * */

package Controller;

import Model.Board.Dice;
import Model.Board.Player;
import Model.Board.TurnManager;
import Model.Exceptions.PlayerNotFoundException;
import View.Gui;

import javax.swing.SwingUtilities;
import java.util.List;

/**
 * Controller class for managing game turns.
 * This class handles the turn-based gameplay logic.
 */
public class TurnManagerController {
    private TurnManager turnManager;
    private Dice dice;
    private Gui gui;
    private boolean diceRolled;
    private int doubleCount;
    
    /**
     * Constructor for TurnManagerController.
     * 
     * @param turnManager The turn manager
     */
    public TurnManagerController(TurnManager turnManager) {
        this.turnManager = turnManager;
        this.dice = Dice.getInstance();
        this.diceRolled = false;
        this.doubleCount = 0;
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
     * Roll the dice for the current player.
     * 
     * @return An array containing the two dice values
     */
    public int[] rollDice() {
        if (diceRolled) {
            return new int[] {dice.getDie1(), dice.getDie2()};
        }
        
        // Roll the dice
        dice.roll();
        int[] diceValues = {dice.getDie1(), dice.getDie2()};
        boolean isDoubles = dice.isDouble();
        
        // Get the current player
        Player currentPlayer = turnManager.getCurrentPlayer();
        if (currentPlayer == null) {
            System.err.println("Error: Current player is null");
            return new int[] {1, 1};
        }
        
        try {
            int totalSpaces = dice.getSum();
            int oldPosition = currentPlayer.getPosition();
            
            // Move the player
            currentPlayer.move(currentPlayer, totalSpaces);
            
            // Check if player passed GO (but wasn't sent to jail)
            if (currentPlayer.getPosition() < oldPosition && !currentPlayer.isInJail()) {
                if (gui != null) {
                    gui.displayMessage(currentPlayer.getName() + " passed GO and collected $200!");
                }
            }
            
            // Handle doubles
            if (isDoubles) {
                doubleCount++;
                if (doubleCount == 3) {
                    // Three doubles in a row sends player to jail
                    currentPlayer.setPosition(10); // Jail position
                    currentPlayer.setInJail(true);
                    doubleCount = 0;
                    
                    if (gui != null) {
                        gui.displayMessage(currentPlayer.getName() + " rolled 3 doubles and was sent to jail!");
                        // Ensure GUI position is updated after being sent to jail
                        gui.updatePlayerPosition(currentPlayer, 10);
                    }
                } else {
                    if (gui != null) {
                        gui.displayMessage(currentPlayer.getName() + " rolled doubles! Roll again after this turn.");
                    }
                }
            } else {
                doubleCount = 0;
            }
            
            // Update GUI - ensure this happens on the Event Dispatch Thread
            if (gui != null) {
                if (SwingUtilities.isEventDispatchThread()) {
                    gui.updateDice(diceValues[0], diceValues[1]);
                    gui.updatePlayerPosition(currentPlayer, currentPlayer.getPosition());
                } else {
                    final int die1 = diceValues[0];
                    final int die2 = diceValues[1];
                    final Player player = currentPlayer;
                    final int position = currentPlayer.getPosition();
                    
                    SwingUtilities.invokeLater(() -> {
                        gui.updateDice(die1, die2);
                        gui.updatePlayerPosition(player, position);
                    });
                }
            }
            
            diceRolled = true;
            return diceValues;
            
        } catch (PlayerNotFoundException e) {
            System.err.println("Error moving player: " + e.getMessage());
            if (gui != null) {
                gui.displayMessage("Error: " + e.getMessage());
            }
            return new int[] {1, 1}; // Default values in case of error
        }
    }
    
    /**
     * End the current player's turn.
     * 
     * @return True if the turn was ended successfully
     */
    public boolean endTurn() {
        if (!diceRolled) {
            if (gui != null) {
                gui.displayMessage("You must roll the dice before ending your turn.");
            }
            return false;
        }
        
        Player currentPlayer = turnManager.getCurrentPlayer();
        if (currentPlayer == null) {
            System.err.println("Error: Current player is null");
            return false;
        }
        
        // Check if player rolled doubles and isn't in jail
        if (doubleCount > 0 && !currentPlayer.isInJail()) {
            if (gui != null) {
                gui.displayMessage(currentPlayer.getName() + " rolled doubles and gets another turn!");
            }
            diceRolled = false;
            return true;
        }
        
        // Move to next player
        turnManager.nextTurn();
        Player nextPlayer = turnManager.getCurrentPlayer();
        if (nextPlayer == null) {
            System.err.println("Error: Next player is null");
            return false;
        }
        
        // Reset diceRolled for next player
        diceRolled = false;
        
        // Update GUI for next player - ensure this happens on the Event Dispatch Thread
        if (gui != null) {
            final Player player = nextPlayer;
            final String playerName = nextPlayer.getName();
            
            if (SwingUtilities.isEventDispatchThread()) {
                gui.updateCurrentPlayer(player);
                gui.displayMessage("It's " + playerName + "'s turn!");
            } else {
                SwingUtilities.invokeLater(() -> {
                    gui.updateCurrentPlayer(player);
                    gui.displayMessage("It's " + playerName + "'s turn!");
                });
            }
        }
        
        return true;
    }
    
    /**
     * Get the current player.
     * 
     * @return The current player
     */
    public Player getCurrentPlayer() {
        return turnManager.getCurrentPlayer();
    }
    
    /**
     * Get the turn order.
     * 
     * @return The list of players in turn order
     */
    public List<Player> getTurnOrder() {
        return turnManager.getTurnOrder();
    }
    
    /**
     * Check if dice have been rolled this turn.
     * 
     * @return True if dice have been rolled
     */
    public boolean isDiceRolled() {
        return diceRolled;
    }
}
