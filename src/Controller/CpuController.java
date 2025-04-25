package Controller;

import Model.Board.Banker;
import Model.Board.ComputerPlayer;
import Model.Board.GameBoard;
import Model.Board.Player;
import Model.Exceptions.PlayerNotFoundException;
import Model.Property.Property;
import Model.Spaces.BoardSpace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Controller class for managing computer player behavior.
 * This handles the AI decision-making for computer players in the Monopoly game.
 * Team member(s) responsible: Matt
 */
public class CpuController {
    private ComputerPlayer computerPlayer;
    private Banker banker;
    private GameBoard gameBoard;
    private Random random;
    
    private double buyPropertyThreshold = 0.7; // 70% chance to buy property when landing
    private double mortgageThreshold = 0.5; // 50% chance to mortgage properties when low on cash
    private double bankruptcyThreshold = 300; // Consider selling properties if balance below $300
    private Map<BoardSpace, Integer> propertyValueEstimates;
    private double auctionBidChance = 0.6; // 60% chance to bid in auction
    private double maxBidPercentOfValue = 1.1; // Max bid as percentage of property price
    
    /**
     * Constructor for CpuController.
     * 
     * @param computerPlayer The computer player to control
     */
    public CpuController(ComputerPlayer computerPlayer) {
        this.computerPlayer = computerPlayer;
        this.banker = Banker.getInstance();
        this.gameBoard = GameBoard.getInstance();
        this.random = new Random();
        this.propertyValueEstimates = new HashMap<>();
    }
    
    /**
     * Handle CPU turn. This is the main method that controls a computer player's actions during their turn.
     * 
     * @return True if the turn is complete, false if the player needs to take more actions
     */
    public boolean handleTurn() {
        // The actual turn and dice rolling are handled by the BoardController
        // The CPU's decisions about property purchases are delegated to the decidePropertyPurchase method
        // This is intentionally simplified since we're using the model's built-in logic for most operations
        return true;
    }
    
    /**
     * Decide whether the computer player should buy a property.
     * 
     * @param property The property to consider buying
     * @return True if the computer decides to buy, false otherwise
     */
    public boolean decidePropertyPurchase(Property property) {
        try {
            int playerMoney = banker.getBalance(computerPlayer);
            int propertyPrice = property.getPurchasePrice();
            
            // Basic affordability check - ensure CPU has at least $100 buffer
            if (playerMoney < propertyPrice + 100) {
                return false;
            }

            return random.nextDouble() < buyPropertyThreshold;
        } catch (PlayerNotFoundException e) {
            System.err.println("Error getting player balance: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get the computer player for this controller.
     * 
     * @return The ComputerPlayer instance
     */
    public ComputerPlayer getComputerPlayer() {
        return computerPlayer;
    }
    
    /**
     * Handle bidding in an auction.
     * 
     * @param property The property being auctioned
     * @param currentHighestBid The current highest bid
     * @return The bid amount, or 0 if passing
     */
    public int decideBidAmount(Property property, int currentHighestBid) {
        try {
            int playerMoney = banker.getBalance(computerPlayer);
            int propertyPrice = property.getPurchasePrice();
            
            // Check if CPU has enough money for minimum bid
            int minBid = currentHighestBid + 5; // Minimum increment of $5
            if (playerMoney < minBid + 50) { // Keep $50 buffer
                return 0; // Pass if can't afford
            }
            
            // Simple decision - random chance to bid or pass
            if (random.nextDouble() > auctionBidChance) {
                return 0; // Pass
            }
            
            // Maximum amount CPU is willing to pay
            int maxBid = (int)(propertyPrice * maxBidPercentOfValue);
            maxBid = Math.min(maxBid, playerMoney - 50); // Keep $50 buffer
            
            if (maxBid <= currentHighestBid) {
                return 0; // Pass if can't afford to outbid
            }
            
            // Generate a bid between minBid and maxBid
            return minBid + random.nextInt(maxBid - minBid + 1);
        } catch (PlayerNotFoundException e) {
            System.err.println("Error getting player balance: " + e.getMessage());
            return 0; // Pass if there's an error
        }
    }
}
