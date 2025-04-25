package Controller;

import Model.Board.Banker;
import Model.Board.Player;
import Model.Exceptions.PlayerNotFoundException;
import Model.Spaces.BoardSpace;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller class for handling auctions in the Monopoly game.
 * This handles the UI and logic for running property auctions when a player declines to purchase a property.
 * Team member(s) responsible: Matt
 */
public class AuctionController {
    private Banker banker;
    private JDialog auctionDialog;
    private Map<Player, Integer> currentBids;
    private Map<Player, JLabel> bidLabels;
    private Map<Player, JButton> bidButtons;
    private BoardSpace propertyForAuction;
    private ArrayList<Player> participatingPlayers;
    private int currentHighestBid;
    private Player highestBidder;
    private Timer auctionTimer;
    private int timeLeft;
    private JLabel timerLabel;
    private JLabel propertyInfoLabel;
    private JLabel highestBidLabel;
    private boolean auctionInProgress;

    /**
     * Constructor for AuctionController.
     **/
    public AuctionController() {
        this.banker = Banker.getInstance();
        this.currentBids = new HashMap<>();
        this.bidLabels = new HashMap<>();
        this.bidButtons = new HashMap<>();
        this.auctionInProgress = false;
    }

    /**
     * Start an auction for a property.
     * @param property The property being auctioned
     * @param players List of players participating in the auction
     * @param parentFrame The parent frame for the auction dialog
     * @return The player who won the auction, or null if nobody bid
     */
    public Player startAuction(BoardSpace property, ArrayList<Player> players, JFrame parentFrame) {
        if (property == null || players == null || players.isEmpty()) {
            return null;
        }
        propertyForAuction = property;
        participatingPlayers = new ArrayList<>(players);
        currentHighestBid = 0;
        highestBidder = null;
        auctionInProgress = true;
        currentBids.clear();
        for (Player player : players) {
            currentBids.put(player, 0);
        }
        createAuctionDialog(parentFrame);
        auctionDialog.setVisible(true);
        while (auctionInProgress) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return highestBidder;
    }

    /**
     * Create the auction dialog with UI components.
     * 
     * @param parentFrame The parent frame for the dialog
     */
    private void createAuctionDialog(JFrame parentFrame) {
        auctionDialog = new JDialog(parentFrame, "Property Auction", true);
        auctionDialog.setLayout(new BorderLayout());
        auctionDialog.setSize(500, 400);
        auctionDialog.setLocationRelativeTo(parentFrame);

        JPanel propertyPanel = new JPanel(new BorderLayout());
        propertyPanel.setBorder(BorderFactory.createTitledBorder("Property for Auction"));
        String propertyName = propertyForAuction.getName();
        int propertyPrice = 0;
        if (propertyForAuction instanceof Model.Property.Property) {
            propertyPrice = ((Model.Property.Property) propertyForAuction).getPurchasePrice();
        }
        
        propertyInfoLabel = new JLabel("<html><h2>" + propertyName + "</h2>" +
                "Starting bid: $" + (propertyPrice / 2) + "<br>" +
                "Market value: $" + propertyPrice + "</html>");
        propertyInfoLabel.setHorizontalAlignment(JLabel.CENTER);
        propertyPanel.add(propertyInfoLabel, BorderLayout.CENTER);
        JPanel statusPanel = new JPanel(new GridLayout(2, 1));
        highestBidLabel = new JLabel("Current highest bid: $0 (None)");
        highestBidLabel.setHorizontalAlignment(JLabel.CENTER);
        highestBidLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        timeLeft = 30;
        timerLabel = new JLabel("Time remaining: " + timeLeft + " seconds");
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        
        statusPanel.add(highestBidLabel);
        statusPanel.add(timerLabel);
        auctionTimer = new Timer(1000, event -> {
            timeLeft--;
            timerLabel.setText("Time remaining: " + timeLeft + " seconds");
            
            if (timeLeft <= 0) {
                auctionTimer.stop();
                finishAuction();
            }
        });
        
        JPanel playersPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        playersPanel.setBorder(BorderFactory.createTitledBorder("Players"));
        bidLabels.clear();
        bidButtons.clear();
        for (Player player : participatingPlayers) {
            JPanel playerPanel = new JPanel(new BorderLayout());
            playerPanel.setBorder(BorderFactory.createEtchedBorder());
            
            JLabel nameLabel = new JLabel(player.getName());
            nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
            
            JLabel bidLabel = new JLabel("Current bid: $0");
            bidLabels.put(player, bidLabel);
            
            JPanel bidControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JTextField bidField = new JTextField(5);
            JButton bidButton = new JButton("Place Bid");
            bidButtons.put(player, bidButton);
            
            if (player instanceof Model.Board.ComputerPlayer) {
                bidField.setEnabled(false);
                bidButton.setEnabled(false);
            } else {
                bidButton.addActionListener(event -> {
                    try {
                        int bidAmount = Integer.parseInt(bidField.getText());
                        placeBid(player, bidAmount);
                        bidField.setText("");
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(auctionDialog, 
                                "Please enter a valid number for your bid.", 
                                "Invalid Bid", JOptionPane.WARNING_MESSAGE);
                    }
                });
            }
            
            JButton passButton = new JButton("Pass");
            passButton.addActionListener(event -> {
                participatingPlayers.remove(player);
                playerPanel.setEnabled(false);
                nameLabel.setEnabled(false);
                bidLabel.setEnabled(false);
                bidField.setEnabled(false);
                bidButton.setEnabled(false);
                passButton.setEnabled(false);
                
                if (participatingPlayers.size() == 1) {
                    highestBidder = participatingPlayers.get(0);
                    currentHighestBid = Math.max(currentHighestBid, 1); // Minimum $1 bid
                    finishAuction();
                } else if (participatingPlayers.isEmpty()) {
                    finishAuction();
                }
            });
            
            bidControlPanel.add(new JLabel("$"));
            bidControlPanel.add(bidField);
            bidControlPanel.add(bidButton);
            bidControlPanel.add(passButton);
            
            JPanel playerInfoPanel = new JPanel(new GridLayout(2, 1));
            playerInfoPanel.add(nameLabel);
            playerInfoPanel.add(bidLabel);
            
            playerPanel.add(playerInfoPanel, BorderLayout.CENTER);
            playerPanel.add(bidControlPanel, BorderLayout.EAST);
            
            playersPanel.add(playerPanel);
        }
        
        auctionDialog.add(propertyPanel, BorderLayout.NORTH);
        auctionDialog.add(new JScrollPane(playersPanel), BorderLayout.CENTER);
        auctionDialog.add(statusPanel, BorderLayout.SOUTH);
        
        auctionTimer.start();
        
        triggerComputerBids();
    }
    
    /**
     * Have computer players place bids automatically.
     */
    private void triggerComputerBids() {
        for (Player player : participatingPlayers) {
            if (player instanceof Model.Board.ComputerPlayer) {
                Timer cpuTimer = new Timer(2000 + (int)(Math.random() * 3000), event -> {
                    if (participatingPlayers.contains(player)) {
                        int playerMoney = 0;
                        try {
                            playerMoney = banker.getBalance(player);
                        } catch (PlayerNotFoundException ex) {
                            playerMoney = 0;
                            System.err.println("Player not found: " + ex.getMessage());
                        }
                        
                        int basePrice = 0;
                        if (propertyForAuction instanceof Model.Property.Property) {
                            basePrice = ((Model.Property.Property) propertyForAuction).getPurchasePrice();
                        }
                        
                        double bidChance = Math.random();
                        if (bidChance > 0.4) {
                            int maxBid = Math.min(playerMoney, basePrice);
                            int minBid = currentHighestBid + 5; // At least $5 more than current bid
                            
                            int maxWillingToPay = (int)(basePrice * 1.25);
                            maxBid = Math.min(maxBid, maxWillingToPay);
                            
                            if (maxBid >= minBid) {
                                int bidAmount = minBid + (int)(Math.random() * (maxBid - minBid) / 2);
                                placeBid(player, bidAmount);
                            } else {
                                participatingPlayers.remove(player);
                                bidLabels.get(player).setText("Passed");
                            }
                        } else {
                            participatingPlayers.remove(player);
                            bidLabels.get(player).setText("Passed");
                        }
                        
                        if (participatingPlayers.size() == 1) {
                            highestBidder = participatingPlayers.get(0);
                            currentHighestBid = Math.max(currentHighestBid, 1); // Minimum $1 bid
                            finishAuction();
                        } else if (participatingPlayers.isEmpty()) {
                            finishAuction();
                        }
                    }
                });
                cpuTimer.setRepeats(false);
                cpuTimer.start();
            }
        }
    }
    
    /**
     * Place a bid for a player.
     * @param player The player placing the bid
     * @param amount The bid amount
     */
    private void placeBid(Player player, int amount) {
        if (!participatingPlayers.contains(player)) {
            return;
        }
        
        int playerMoney = 0;
        try {
            playerMoney = banker.getBalance(player);
        } catch (PlayerNotFoundException e) {
            JOptionPane.showMessageDialog(auctionDialog, 
                    "Player not found: " + e.getMessage(), 
                    "Player Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (amount > playerMoney) {
            JOptionPane.showMessageDialog(auctionDialog, 
                    "You don't have enough money for this bid.", 
                    "Insufficient Funds", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (amount <= currentHighestBid) {
            JOptionPane.showMessageDialog(auctionDialog, 
                    "Your bid must be higher than the current highest bid of $" + currentHighestBid, 
                    "Bid Too Low", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        currentBids.put(player, amount);
        bidLabels.get(player).setText("Current bid: $" + amount);
        
        currentHighestBid = amount;
        highestBidder = player;
        highestBidLabel.setText("Current highest bid: $" + amount + " (" + player.getName() + ")");
        
        if (timeLeft < 10) {
            timeLeft += 10;
            if (timeLeft > 30) {
                timeLeft = 30;
            }
            timerLabel.setText("Time remaining: " + timeLeft + " seconds");
        }
    }
    
    /**
     * Finish the auction and process the result.
     */
    private void finishAuction() {
        auctionTimer.stop();
        
        if (highestBidder != null) {
            try {
                banker.sellProperty(propertyForAuction, highestBidder);
                
                int difference = currentHighestBid - ((Model.Property.Property) propertyForAuction).getPurchasePrice();
                if (difference > 0) {
                    try {
                        banker.withdraw(highestBidder, difference);
                    } catch (Exception ex) {
                        System.err.println("Error adjusting auction price: " + ex.getMessage());
                    }
                }
                
                JOptionPane.showMessageDialog(auctionDialog, 
                        highestBidder.getName() + " won the auction for " + propertyForAuction.getName() + 
                        " with a bid of $" + currentHighestBid, 
                        "Auction Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                System.err.println("Error during auction transaction: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(auctionDialog,
                    "Nobody bid on " + propertyForAuction.getName() + ". The property remains with the bank.", 
                    "Auction Complete", JOptionPane.INFORMATION_MESSAGE);
        }
        
        auctionDialog.dispose();
        auctionInProgress = false;
    }
}
