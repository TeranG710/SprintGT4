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
    private BoardSpace propertyForAuction;
    private Player highestBidder;
    private int currentHighestBid;
    private boolean auctionComplete;

    /**
     * Constructor for AuctionController.
     */
    public AuctionController() {
        this.banker = Banker.getInstance();
        this.currentBids = new HashMap<>();
        this.auctionComplete = false;
    }

    /**
     * Start an auction for a property.
     *
     * @param property The property being auctioned
     * @param players List of players participating in the auction
     * @param parentFrame The parent frame for the auction dialog
     * @return The player who won the auction, or null if nobody bid
     */
    public Player startAuction(BoardSpace property, ArrayList<Player> players, JFrame parentFrame) {
        if (property == null || players == null || players.isEmpty()) {
            return null;
        }

        // Set up auction variables
        propertyForAuction = property;
        currentHighestBid = 0;
        highestBidder = null;
        auctionComplete = false;

        // Reset bids
        currentBids.clear();
        for (Player player : players) {
            currentBids.put(player, 0);
        }

        // Create and show the auction dialog synchronously
        try {
            SwingUtilities.invokeAndWait(() -> {
                showSimpleAuctionDialog(players, parentFrame);
            });
        } catch (Exception e) {
            System.err.println("Error showing auction dialog: " + e.getMessage());
        }

        // If we have a winner, complete the transaction
        if (highestBidder != null && currentHighestBid > 0) {
            try {
                // Transfer money and property
                banker.withdraw(highestBidder, currentHighestBid);
                banker.addTitleDeed(highestBidder, property);
                property.setOwner(highestBidder);

                System.out.println("AUCTION COMPLETE: " + highestBidder.getName() +
                        " won " + property.getName() + " for $" + currentHighestBid);

                return highestBidder;
            } catch (Exception e) {
                System.err.println("Error completing auction transaction: " + e.getMessage());
            }
        }

        return null;
    }

    /**
     * Show a simple auction dialog that lets players enter bids.
     */
    private void showSimpleAuctionDialog(ArrayList<Player> players, JFrame parentFrame) {
        auctionDialog = new JDialog(parentFrame, "Property Auction", true);
        auctionDialog.setLayout(new BorderLayout());
        auctionDialog.setSize(400, 300);
        auctionDialog.setLocationRelativeTo(parentFrame);

        // Property info panel
        JPanel propertyPanel = new JPanel(new BorderLayout());
        propertyPanel.setBorder(BorderFactory.createTitledBorder("Property for Auction"));

        String propertyName = propertyForAuction.getName();
        int propertyPrice = 0;
        if (propertyForAuction instanceof Model.Property.Property) {
            propertyPrice = ((Model.Property.Property) propertyForAuction).getPurchasePrice();
        }

        JLabel propertyLabel = new JLabel("<html><h3>" + propertyName + "</h3>" +
                "Starting bid: $" + (propertyPrice / 2) + "<br>" +
                "Market value: $" + propertyPrice + "</html>");
        propertyLabel.setHorizontalAlignment(JLabel.CENTER);
        propertyPanel.add(propertyLabel, BorderLayout.CENTER);

        // Bidding panel
        JPanel biddingPanel = new JPanel();
        biddingPanel.setLayout(new BoxLayout(biddingPanel, BoxLayout.Y_AXIS));
        biddingPanel.setBorder(BorderFactory.createTitledBorder("Current Bids"));

        Map<Player, JLabel> bidLabels = new HashMap<>();

        for (Player player : players) {
            JPanel playerPanel = new JPanel(new BorderLayout());
            playerPanel.setBorder(BorderFactory.createEtchedBorder());

            JLabel nameLabel = new JLabel(player.getName());
            JLabel bidLabel = new JLabel("Bid: $0");
            bidLabels.put(player, bidLabel);

            playerPanel.add(nameLabel, BorderLayout.WEST);
            playerPanel.add(bidLabel, BorderLayout.EAST);

            biddingPanel.add(playerPanel);
        }

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout());

        JLabel currentPlayerLabel = new JLabel("Current player: " + players.get(0).getName());
        JTextField bidField = new JTextField(10);
        JButton bidButton = new JButton("Place Bid");
        JButton passButton = new JButton("Pass");
        JLabel statusLabel = new JLabel("Enter bid amount");

        controlPanel.add(currentPlayerLabel);
        controlPanel.add(new JLabel("Bid: $"));
        controlPanel.add(bidField);
        controlPanel.add(bidButton);
        controlPanel.add(passButton);

        // Add components to dialog
        auctionDialog.add(propertyPanel, BorderLayout.NORTH);
        auctionDialog.add(new JScrollPane(biddingPanel), BorderLayout.CENTER);
        auctionDialog.add(controlPanel, BorderLayout.SOUTH);
        auctionDialog.add(statusLabel, BorderLayout.NORTH);

        // Set up a simple turn-based auction for each player
        final int[] currentPlayerIndex = {0};

        // Update the current player label
        updateCurrentPlayer(currentPlayerLabel, players.get(currentPlayerIndex[0]));

        // Bid button action
        bidButton.addActionListener(e -> {
            try {
                Player currentPlayer = players.get(currentPlayerIndex[0]);
                int bidAmount = Integer.parseInt(bidField.getText());

                // Check if bid is valid
                if (bidAmount <= currentHighestBid) {
                    statusLabel.setText("Bid must be higher than current highest bid ($" + currentHighestBid + ")");
                    return;
                }

                // Check if player has enough money
                int playerMoney = banker.getBalance(currentPlayer);
                if (bidAmount > playerMoney) {
                    statusLabel.setText("You don't have enough money for this bid. Your balance: $" + playerMoney);
                    return;
                }

                // Accept the bid
                currentBids.put(currentPlayer, bidAmount);
                bidLabels.get(currentPlayer).setText("Bid: $" + bidAmount);

                // Update highest bid
                currentHighestBid = bidAmount;
                highestBidder = currentPlayer;
                statusLabel.setText(currentPlayer.getName() + " bid $" + bidAmount);

                // Move to next player
                moveToNextPlayer(currentPlayerIndex, players, currentPlayerLabel);
                bidField.setText("");

            } catch (NumberFormatException ex) {
                statusLabel.setText("Please enter a valid number for your bid");
            } catch (PlayerNotFoundException ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        // Pass button action
        passButton.addActionListener(e -> {
            Player currentPlayer = players.get(currentPlayerIndex[0]);
            statusLabel.setText(currentPlayer.getName() + " passed");

            // Move to next player
            moveToNextPlayer(currentPlayerIndex, players, currentPlayerLabel);
            bidField.setText("");
        });

        // Show the dialog
        auctionDialog.setVisible(true);
    }

    /**
     * Move to the next player in the auction
     */
    private void moveToNextPlayer(int[] currentIndex, ArrayList<Player> players, JLabel playerLabel) {
        currentIndex[0] = (currentIndex[0] + 1) % players.size();

        // Update player label
        updateCurrentPlayer(playerLabel, players.get(currentIndex[0]));

        // Check if we've gone around the table and all players have had a chance to bid
        // If we come back to a player with a current bid, end the auction
        if (currentIndex[0] == 0 && highestBidder != null) {
            auctionComplete = true;
            auctionDialog.dispose();
        }
    }

    /**
     * Update the current player label
     */
    private void updateCurrentPlayer(JLabel label, Player player) {
        label.setText("Current player: " + player.getName());
    }
}