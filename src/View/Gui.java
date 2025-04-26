/*
 * CSCI 234: Intro to Software Engineering
 * Group: Giovanny, Jamell, Matt, Deborah
 * Purpose: This class creates the graphical user interface for the Monopoly game.
 * Team Member(s) responsible: Matt
 * */

package View;

import Controller.BoardController;
import Model.Board.Banker;
import Model.Board.Player;
import Model.Exceptions.PlayerNotFoundException;
import Model.Property.Property;
import Model.Spaces.BoardSpace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class Gui {
    // Controller reference
    private BoardController controller;
    
    // Main frame and panels
    private JFrame mainFrame;
    private JPanel gamePanel;
    private JPanel boardPanel;
    private JPanel controlPanel;
    private JPanel playerInfoPanel;
    private JPanel dicePanel;
    private JPanel propertyPanel;
    
    // Message display
    private JLabel messageLabel;

    // Board dimensions
    private static final int BOARD_SIZE = 740;
    private static final int SPACE_WIDTH = BOARD_SIZE / 11;
    private static final int SPACE_HEIGHT = BOARD_SIZE / 11;

    // Property colors
    private static final Color BOARD_BACKGROUND_COLOR = new Color(226, 240, 217);
    private static final Color BROWN = new Color(150, 75, 0);
    private static final Color LIGHT_BLUE = new Color(170, 224, 250);
    private static final Color PINK = new Color(217, 58, 150);
    private static final Color ORANGE = new Color(242, 142, 43);
    private static final Color RED = new Color(227, 34, 25);
    private static final Color YELLOW = new Color(255, 239, 0);
    private static final Color GREEN = new Color(31, 164, 73);
    private static final Color DARK_BLUE = new Color(0, 114, 187);

    // Colors for railroad and utilities
    private static final Color RAILROAD_COLOR = Color.DARK_GRAY;
    private static final Color UTILITY_COLOR = Color.LIGHT_GRAY;

    // Game data structures
    private Map<String, Color> propertyColors;
    private Map<Integer, String> positionToName;
    private Map<Integer, Integer> positionToPrice;
    
    // Special space images
    private Map<String, Image> spaceImages;

    // Sample player data for testing
    private List<PlayerData> players;
    private int currentPlayerIndex = 0;

    // Dice
    private int dice1Value = 1;
    private int dice2Value = 1;
    private boolean diceRolled = false;
    
    // Game statistics
    private int totalTurns = 0;
    private int totalDiceRolls = 0;
    private int totalDoubles = 0;
    private int totalGoPasses = 0;
    private int totalJailVisits = 0;
    private long gameStartTime = System.currentTimeMillis();
    
    // UI elements for statistics
    private JLabel turnsLabel;
    private JLabel timeLabel;
    private JLabel diceRollsLabel;
    private JLabel doublesLabel;
    private JLabel goPassesLabel;
    private JLabel jailVisitsLabel;
    private JTable playerStatsTable;
    private javax.swing.table.DefaultTableModel playerStatsModel;
    
    // UI elements for Property Management
    private JLabel nameLabel;
    private JLabel priceLabel;
    private JLabel rentLabel;
    private JLabel housesLabel;
    private JLabel hotelLabel;
    private JLabel mortgageLabel;
    private JButton buyHouseButton;
    private JButton sellHouseButton;
    private JButton mortgageButton;
    private JButton unmortgageButton;
    private JList<String> propertiesList;
    private Map<Integer, Boolean> mortgagedProperties = new HashMap<>();

    /**
     * PlayerData class to store player information.
     * This includes the player's name, color, position on the board,
     * Team member(s) responsible: Matt
     */
    private class PlayerData {
        private String name;
        private Color color;
        private int position;
        private int money;
        private List<Integer> ownedProperties;
        private String tokenName; // Added for token images

        public PlayerData(String name, Color color, int position, int money) {
            this.name = name;
            this.color = color;
            this.position = position;
            this.money = money;
            this.ownedProperties = new ArrayList<>();
            this.tokenName = "hat"; // Default token
        }
    }

    /**
     * Constructor that initializes and displays the GUI.
     * Team member(s) responsible: Matt
     */
    public Gui() {
        initializeGameData();
        createMainFrame();
        
        // Create the tabbed pane for different views
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Create the game board panel (main game view)
        JPanel gameBoardPanel = new JPanel(new BorderLayout());
        createBoardPanel();
        createDicePanel();
        createControlPanel();
        createPlayerInfoPanel();
        createPropertyPanel();
        
        // Add components to the game board panel
        gameBoardPanel.add(boardPanel, BorderLayout.CENTER);
        gameBoardPanel.add(dicePanel, BorderLayout.WEST);
        gameBoardPanel.add(playerInfoPanel, BorderLayout.EAST);
        gameBoardPanel.add(controlPanel, BorderLayout.SOUTH);
        
        // Create the property management panel
        JPanel propertyManagementPanel = createPropertyManagementPanel();
        
        // Create the trade panel
        JPanel tradePanel = createTradePanel();
        
        // Create the statistics panel
        JPanel statisticsPanel = createStatisticsPanel();
        
        // Add the panels to the tabbed pane
        tabbedPane.addTab("Game Board", new ImageIcon(), gameBoardPanel, "Main game board view");
        tabbedPane.addTab("Property Management", new ImageIcon(), propertyManagementPanel, "Manage your properties");
        tabbedPane.addTab("Trade", new ImageIcon(), tradePanel, "Trade with other players");
        tabbedPane.addTab("Statistics", new ImageIcon(), statisticsPanel, "Game statistics");
        
        // Add tab change listener to update the selected tab's data
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            switch (selectedIndex) {
                case 0: // Game Board
                    // Nothing special needed when returning to game board
                    break;
                case 1: // Property Management
                    // Refresh property data when tab is selected
                    refreshPropertyManagementPanel(null);
                    break;
                case 2: // Trade
                    // Update trade panel when selected (not implemented yet)
                    break;
                case 3: // Statistics
                    // Update statistics when tab is selected
                    updateGameStatistics();
                    break;
            }
        });
        
        // Add the tabbed pane to the main frame
        gamePanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Add a message area at the top
        messageLabel = new JLabel("Welcome to Monopoly!");
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        gamePanel.add(messageLabel, BorderLayout.NORTH);

        mainFrame.setPreferredSize(new Dimension(1200, 900));

        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
    
    /**
     * Set the controller for this GUI.
     * 
     * @param controller The BoardController to use
     */
    public void setController(BoardController controller) {
        this.controller = controller;
    }
    
    /**
     * Get the main frame of the GUI.
     * 
     * @return The main JFrame
     */
    public JFrame getMainFrame() {
        return mainFrame;
    }
    
    /**
     * Initialize a new game with the given players.
     * 
     * @param players The list of players in the game
     */
    public void initializeGame(ArrayList<Player> players) {
        // Clear existing data
        this.players.clear();
        
        // Convert Player model objects to PlayerData view objects
        Color[] playerColors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, 
                         Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK};
        
        System.out.println("GUI Initialization: Setting up new game with " + players.size() + " players");
        
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            
            // Force all players to position 0 (GO)
            int currentPos = player.getPosition();
            if (currentPos != 0) {
                System.out.println("GUI: Player " + player.getName() + " was at position " + currentPos +
                                 ", correcting to position 0 (GO)");
            }
            
            // Always set position to 0 regardless of current value
            player.setPosition(0);
            System.out.println("GUI: Player " + player.getName() + " position set to 0 (GO)");
            
            Color color = playerColors[i % playerColors.length];
            // Create PlayerData with explicit position 0
            PlayerData playerData = new PlayerData(player.getName(), color, 0, 1500);
            
            // Double check position is correct
            if (playerData.position != 0) {
                System.out.println("GUI ERROR: PlayerData position was not 0, fixing...");
                playerData.position = 0;
            }
            
            // Set the token name from the player model
            if (player.getTokenName() != null) {
                playerData.tokenName = player.getTokenName();
            }
            
            this.players.add(playerData);
        }
        
        // Reset current player index
        currentPlayerIndex = 0;
        
        // Reset dice values
        dice1Value = 1;
        dice2Value = 1;
        diceRolled = false;
        
        // Update all panels
        boardPanel.repaint();
        updatePlayerInfoPanel();
        updatePropertyPanel();
        dicePanel.repaint();
        
        // Display welcome message
        displayMessage("Game started! It's " + this.players.get(0).name + "'s turn.");
    }
    
    /**
     * Update the position of a player token on the board.
     * Thread-safe implementation that ensures UI updates happen
     * on the Event Dispatch Thread.
     * 
     * @param player The player to update
     * @param position The new position
     */
    public void updatePlayerPosition(Player player, int position) {
        // Check for null parameter
        if (player == null) {
            System.err.println("Error: Null player in updatePlayerPosition");
            return;
        }
        
        // Find the matching player in our players list
        boolean playerFound = false;
        int oldPosition = -1;
        
        for (PlayerData playerData : players) {
            if (playerData.name.equals(player.getName())) {
                oldPosition = playerData.position;
                playerData.position = position;
                playerFound = true;
                break;
            }
        }
        
        if (!playerFound) {
            System.err.println("Error: Player not found in updatePlayerPosition: " + player.getName());
            return;
        }
        
        // Track statistics based on position changes
        if (oldPosition != -1) {
            // Check if player passed GO (older position is higher than new position, indicating a wrap-around)
            // We check if the old position was at least 35 to ensure we don't count backward movement
            if (oldPosition > 35 && position < 10) {
                totalGoPasses++;
                System.out.println("STATISTICS: " + player.getName() + " passed GO! Total GO passes: " + totalGoPasses);
            }
            
            // Check if player landed on "Go To Jail"
            if (position == 30) {
                totalJailVisits++;
                System.out.println("STATISTICS: " + player.getName() + " went to jail! Total jail visits: " + totalJailVisits);
            }
            
            // Update statistics panel if it's visible
            updateGameStatistics();
        }
        
        // Repaint the board on EDT if needed
        if (SwingUtilities.isEventDispatchThread()) {
            boardPanel.repaint();
        } else {
            SwingUtilities.invokeLater(() -> boardPanel.repaint());
        }
    }
    private boolean isRailroad(String name) {
        return name.contains("Railroad") || name.contains("Short Line");
    }


    /**
     * Update the dice display with new values.
     * Thread-safe implementation that ensures UI updates happen
     * on the Event Dispatch Thread.
     * 
     * @param dice1 The value of the first die
     * @param dice2 The value of the second die
     */
    public void updateDice(int dice1, int dice2) {
        // Validate dice values
        if (dice1 < 1 || dice1 > 6 || dice2 < 1 || dice2 > 6) {
            System.err.println("Warning: Invalid dice values in updateDice: " + dice1 + ", " + dice2);
            // Continue anyway with the provided values
        }
        
        // Update dice values
        dice1Value = dice1;
        dice2Value = dice2;
        
        // Track dice statistics
        totalDiceRolls++;
        
        // Check for doubles
        if (dice1 == dice2) {
            totalDoubles++;
            System.out.println("STATISTICS: Doubles rolled! (" + dice1 + "," + dice2 + ") Total doubles: " + totalDoubles);
        }
        
        // Update statistics
        updateGameStatistics();
        
        // Repaint dice panel on EDT if needed
        if (SwingUtilities.isEventDispatchThread()) {
            dicePanel.repaint();
        } else {
            SwingUtilities.invokeLater(() -> dicePanel.repaint());
        }
    }
    
    /**
     * Update all player information.
     * 
     * @param gamePlayers The list of players from the model. If null, all players will be refreshed from the banker.
     */
    public void updatePlayerInfo(ArrayList<Player> gamePlayers) {
        Banker banker = Banker.getInstance();
        
        // If gamePlayers is null, get all players from the banker
        if (gamePlayers == null) {
            try {
                gamePlayers = banker.getAllPlayers();
            } catch (Exception e) {
                System.err.println("Error getting players from banker: " + e.getMessage());
                gamePlayers = new ArrayList<>(); // Create empty list to avoid NPE
            }
        }
        
        // Update player data from model
        for (Player gamePlayer : gamePlayers) {
            // Skip null players
            if (gamePlayer == null) continue;
            
            for (PlayerData playerData : players) {
                if (playerData.name.equals(gamePlayer.getName())) {
                    // Update player data
                    playerData.position = gamePlayer.getPosition();
                    
                    // Update money from banker
                    try {
                        playerData.money = banker.getBalance(gamePlayer);
                    } catch (PlayerNotFoundException e) {
                        System.err.println("Error getting balance for " + gamePlayer.getName() + ": " + e.getMessage());
                    }
                    
                    // Update owned properties
                    try {
                        // Try to get player properties from model
                        ArrayList<BoardSpace> playerProperties = gamePlayer.getProperties();
                        playerData.ownedProperties.clear();
                        
                        if (playerProperties != null) {
                            for (BoardSpace property : playerProperties) {
                                if (property == null) continue;
                                
                                // Find the property position based on name
                                for (Map.Entry<Integer, String> entry : positionToName.entrySet()) {
                                    if (entry.getValue().equals(property.getName())) {
                                        playerData.ownedProperties.add(entry.getKey());
                                        break;
                                    }
                                }
                            }
                        }
                    } catch (PlayerNotFoundException e) {
                        // Don't show error, this is expected when properties haven't been initialized yet
                        // Just leave the ownedProperties list as is
                    }
                    break;
                }
            }
        }
        
        // Update panels on EDT if needed
        if (SwingUtilities.isEventDispatchThread()) {
            updatePlayerInfoPanel();
            updatePropertyPanel();
            boardPanel.repaint();
        } else {
            SwingUtilities.invokeLater(() -> {
                updatePlayerInfoPanel();
                updatePropertyPanel();
                boardPanel.repaint();
            });
        }
    }
    
    /**
     * Update property ownership.
     * 
     * @param property The property being updated
     * @param owner The new owner of the property
     */
    public void updatePropertyOwnership(Property property, Player owner) {
        // Check for null parameters
        if (property == null || owner == null) {
            System.err.println("Error: Null property or owner in updatePropertyOwnership");
            return;
        }
        
        // Find the position of the property
        int position = -1;
        for (Map.Entry<Integer, String> entry : positionToName.entrySet()) {
            if (entry.getValue().equals(property.getName())) {
                position = entry.getKey();
                break;
            }
        }
        
        if (position == -1) {
            System.err.println("Property not found in position map: " + property.getName());
            return; // Property not found
        }
        
        // Remove property from all other players' ownedProperties
        for (PlayerData playerData : players) {
            if (!playerData.name.equals(owner.getName())) {
                playerData.ownedProperties.remove(Integer.valueOf(position));
            }
        }
        
        // Update ownership in owner's player data
        for (PlayerData playerData : players) {
            if (playerData.name.equals(owner.getName())) {
                if (!playerData.ownedProperties.contains(position)) {
                    playerData.ownedProperties.add(position);
                }
                break;
            }
        }
        
        // Update panels on EDT if needed
        if (SwingUtilities.isEventDispatchThread()) {
            updatePlayerInfoPanel();
            updatePropertyPanel();
            boardPanel.repaint();
        } else {
            SwingUtilities.invokeLater(() -> {
                updatePlayerInfoPanel();
                updatePropertyPanel();
                boardPanel.repaint();
            });
        }
    }
    
    /**
     * Display a message to the user.
     * Thread-safe implementation that ensures the message is displayed
     * on the Event Dispatch Thread.
     * 
     * @param message The message to display
     */
    public void displayMessage(String message) {
        if (message == null) {
            return;
        }
        
        if (SwingUtilities.isEventDispatchThread()) {
            messageLabel.setText(message);
        } else {
            SwingUtilities.invokeLater(() -> messageLabel.setText(message));
        }
    }
    
    /**
     * Update the current player.
     * Thread-safe implementation that ensures UI updates happen
     * on the Event Dispatch Thread.
     * 
     * @param player The new current player
     */
    public void updateCurrentPlayer(Player player) {
        // Check for null parameter
        if (player == null) {
            System.err.println("Error: Null player in updateCurrentPlayer");
            return;
        }
        
        // Find the matching player in our data
        int newIndex = -1;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).name.equals(player.getName())) {
                newIndex = i;
                break;
            }
        }
        
        if (newIndex == -1) {
            System.err.println("Error: Player not found in updateCurrentPlayer: " + player.getName());
            return;
        }
        
        // If the current player is changing (not just refresh), increment turn counter
        if (currentPlayerIndex != newIndex) {
            totalTurns++;
            System.out.println("STATISTICS: New turn for " + player.getName() + ". Total turns: " + totalTurns);
            updateGameStatistics();
        }
        
        // Update current player index
        currentPlayerIndex = newIndex;
        
        // Update panels on EDT if needed
        if (SwingUtilities.isEventDispatchThread()) {
            updatePlayerInfoPanel();
            updatePropertyPanel();
            // Also refresh the property management panel if it exists
            refreshPropertyManagementPanel(null);
            boardPanel.repaint();
        } else {
            SwingUtilities.invokeLater(() -> {
                updatePlayerInfoPanel();
                updatePropertyPanel();
                boardPanel.repaint();
            });
        }
    }
    
    /**
     * Show a dialog for purchasing a property.
     * This is a legacy method that uses the simpler JOptionPane dialog style.
     * BoardController now uses the more detailed PropertyPurchaseDialog instead.
     * 
     * @param property The property to buy
     * @param player The player who may buy it
     * @deprecated Use PropertyPurchaseDialog instead for better user experience
     */
    @Deprecated
    public void showBuyPropertyDialog(Property property, Player player) {
        System.out.println("WARNING: Using deprecated showBuyPropertyDialog. Consider using PropertyPurchaseDialog instead.");
        
        int price = property.getPurchasePrice();
        String propertyName = property.getName();
        
        int choice = JOptionPane.showConfirmDialog(mainFrame,
                "Do you want to buy " + propertyName + " for $" + price + "?",
                "Buy Property", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            // Player wants to buy
            if (controller != null) {
                boolean success = controller.buyProperty(property, player);
                if (success) {
                    JOptionPane.showMessageDialog(mainFrame,
                            "You now own " + propertyName + "!",
                            "Property Purchased", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Purchase failed. You may not have enough money.",
                            "Purchase Failed", JOptionPane.WARNING_MESSAGE);
                }
            }
        } else {
            // Player declined, property goes to auction
            if (controller != null) {
                controller.auctionProperty(property);
            }
        }
    }

    /**
     * Initialize game data, including property colors, positions, and player information.
     * Team member(s) responsible: Matt
     */
    private void initializeGameData() {
        // Initialize property colors
        propertyColors = new HashMap<>();

        // Brown properties
        propertyColors.put("Mediterranean Avenue", BROWN);
        propertyColors.put("Baltic Avenue", BROWN);

        // Light blue properties
        propertyColors.put("Oriental Avenue", LIGHT_BLUE);
        propertyColors.put("Vermont Avenue", LIGHT_BLUE);
        propertyColors.put("Connecticut Avenue", LIGHT_BLUE);

        // Pink properties
        propertyColors.put("St. Charles Place", PINK);
        propertyColors.put("States Avenue", PINK);
        propertyColors.put("Virginia Avenue", PINK);

        // Orange properties
        propertyColors.put("St. James Place", ORANGE);
        propertyColors.put("Tennessee Avenue", ORANGE);
        propertyColors.put("New York Avenue", ORANGE);

        // Red properties
        propertyColors.put("Kentucky Avenue", RED);
        propertyColors.put("Indiana Avenue", RED);
        propertyColors.put("Illinois Avenue", RED);

        // Yellow properties
        propertyColors.put("Atlantic Avenue", YELLOW);
        propertyColors.put("Ventnor Avenue", YELLOW);
        propertyColors.put("Marvin Gardens", YELLOW);

        // Green properties
        propertyColors.put("Pacific Avenue", GREEN);
        propertyColors.put("North Carolina Avenue", GREEN);
        propertyColors.put("Pennsylvania Avenue", GREEN);

        // Dark blue properties
        propertyColors.put("Park Place", DARK_BLUE);
        propertyColors.put("Boardwalk", DARK_BLUE);

        // Railroad properties
        propertyColors.put("Reading Railroad", RAILROAD_COLOR);
        propertyColors.put("Pennsylvania Railroad", RAILROAD_COLOR);
        propertyColors.put("B. & O. Railroad", RAILROAD_COLOR);
        propertyColors.put("Short Line", RAILROAD_COLOR);

        // Utility properties
        propertyColors.put("Electric Company", UTILITY_COLOR);
        propertyColors.put("Water Works", UTILITY_COLOR);

        // Initialize position to property name mapping
        positionToName = new HashMap<>();
        positionToName.put(0, "GO");
        positionToName.put(1, "Mediterranean Avenue");
        positionToName.put(2, "Community Chest");
        positionToName.put(3, "Baltic Avenue");
        positionToName.put(4, "Income Tax");
        positionToName.put(5, "Reading Railroad");
        positionToName.put(6, "Oriental Avenue");
        positionToName.put(7, "Chance");
        positionToName.put(8, "Vermont Avenue");
        positionToName.put(9, "Connecticut Avenue");
        positionToName.put(10, "Jail");
        positionToName.put(11, "St. Charles Place");
        positionToName.put(12, "Electric Company");
        positionToName.put(13, "States Avenue");
        positionToName.put(14, "Virginia Avenue");
        positionToName.put(15, "Pennsylvania Railroad");
        positionToName.put(16, "St. James Place");
        positionToName.put(17, "Community Chest");
        positionToName.put(18, "Tennessee Avenue");
        positionToName.put(19, "New York Avenue");
        positionToName.put(20, "Free Parking");
        positionToName.put(21, "Kentucky Avenue");
        positionToName.put(22, "Chance");
        positionToName.put(23, "Indiana Avenue");
        positionToName.put(24, "Illinois Avenue");
        positionToName.put(25, "B. & O. Railroad");
        positionToName.put(26, "Atlantic Avenue");
        positionToName.put(27, "Ventnor Avenue");
        positionToName.put(28, "Water Works");
        positionToName.put(29, "Marvin Gardens");
        positionToName.put(30, "Go To Jail");
        positionToName.put(31, "Pacific Avenue");
        positionToName.put(32, "North Carolina Avenue");
        positionToName.put(33, "Community Chest");
        positionToName.put(34, "Pennsylvania Avenue");
        positionToName.put(35, "Short Line");
        positionToName.put(36, "Chance");
        positionToName.put(37, "Park Place");
        positionToName.put(38, "Luxury Tax");
        positionToName.put(39, "Boardwalk");

        // Initialize position to price mapping
        positionToPrice = new HashMap<>();
        positionToPrice.put(1, 60);
        positionToPrice.put(3, 60);
        positionToPrice.put(5, 200);
        positionToPrice.put(6, 100);
        positionToPrice.put(8, 100);
        positionToPrice.put(9, 120);
        positionToPrice.put(11, 140);
        positionToPrice.put(12, 150);
        positionToPrice.put(13, 140);
        positionToPrice.put(14, 160);
        positionToPrice.put(15, 200);
        positionToPrice.put(16, 180);
        positionToPrice.put(18, 180);
        positionToPrice.put(19, 200);
        positionToPrice.put(21, 220);
        positionToPrice.put(23, 220);
        positionToPrice.put(24, 240);
        positionToPrice.put(25, 200);
        positionToPrice.put(26, 260);
        positionToPrice.put(27, 260);
        positionToPrice.put(28, 150);
        positionToPrice.put(29, 280);
        positionToPrice.put(31, 300);
        positionToPrice.put(32, 300);
        positionToPrice.put(34, 320);
        positionToPrice.put(35, 200);
        positionToPrice.put(37, 350);
        positionToPrice.put(39, 400);

        // Initialize special space images
        spaceImages = new HashMap<>();
        try {
            // Load card images from resources
            ImageIcon chanceIcon = new ImageIcon(getClass().getClassLoader().getResource("cards/chance.png"));
            ImageIcon communityChestIcon = new ImageIcon(getClass().getClassLoader().getResource("cards/community_chest.png"));
            
            // Resize the images to fit in the board spaces
            Image chanceImage = chanceIcon.getImage().getScaledInstance(SPACE_HEIGHT - 10, SPACE_WIDTH / 2, Image.SCALE_SMOOTH);
            Image communityChestImage = communityChestIcon.getImage().getScaledInstance(SPACE_HEIGHT - 10, SPACE_WIDTH / 2, Image.SCALE_SMOOTH);
            
            // Store the images in the map
            spaceImages.put("Chance", chanceImage);
            spaceImages.put("Community Chest", communityChestImage);
        } catch (Exception e) {
            System.err.println("Error loading space images: " + e.getMessage());
        }
        
        // Initialize players
        players = new ArrayList<>();
        players.add(new PlayerData("Player 1", Color.RED, 0, 1500));
        players.add(new PlayerData("Player 2", Color.BLUE, 0, 1500));
        players.add(new PlayerData("Player 3", Color.GREEN, 0, 1500));
        players.add(new PlayerData("Player 4", Color.YELLOW, 0, 1500));

        // Give some properties to players for testing
        players.get(0).ownedProperties.addAll(Arrays.asList(1, 3));
        players.get(1).ownedProperties.addAll(Arrays.asList(6, 8, 9));
        players.get(2).ownedProperties.addAll(Arrays.asList(11, 13, 14));
        players.get(3).ownedProperties.addAll(Arrays.asList(16, 18, 19));
    }

    /**
     * Create and configure the main application frame.
     * Team member(s) responsible: Matt
     */
    private void createMainFrame() {
        mainFrame = new JFrame("Monopoly Advanced GUI");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(true);
        mainFrame.setPreferredSize(new Dimension(1200, 800));

        gamePanel = new JPanel(new BorderLayout());
        mainFrame.add(gamePanel);
    }

    /**
     * Create the board panel with all spaces.
     * Team member(s) responsible: Matt
     */
    private void createBoardPanel() {
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
                drawPlayers(g);
            }
        };

        boardPanel.setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
        boardPanel.setBackground(BOARD_BACKGROUND_COLOR); // Light green background

        // Add mouse listener to show property info on hover
        boardPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int position = getPositionFromCoordinates(e.getX(), e.getY());
                if (position != -1) {
                    String propertyName = positionToName.get(position);
                    int price = positionToPrice.getOrDefault(position, 0);

                    String ownerInfo = "";
                    for (PlayerData player : players) {
                        if (player.ownedProperties.contains(position)) {
                            ownerInfo = " (Owned by " + player.name + ")";
                            break;
                        }
                    }

                    String tooltip = propertyName;
                    if (price > 0) {
                        tooltip += " - $" + price + ownerInfo;
                    }

                    boardPanel.setToolTipText(tooltip);
                } else {
                    boardPanel.setToolTipText(null);
                }
            }
        });
    }

    /**
     * Draw the Monopoly board with all spaces.
     * Team member(s) responsible: Matt
     */
    private void drawBoard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw board outline
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(0, 0, BOARD_SIZE - 1, BOARD_SIZE - 1);

        // Draw spaces
        drawCornerSpaces(g2d);
        drawHorizontalSpaces(g2d, true);  // Bottom row
        drawVerticalSpaces(g2d, true);    // Left column
        drawHorizontalSpaces(g2d, false); // Top row
        drawVerticalSpaces(g2d, false);   // Right column

        // Draw center
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        g2d.setColor(new Color(213, 15, 37)); // Monopoly red
        g2d.drawString("MONOPOLY", BOARD_SIZE/2 - 120, BOARD_SIZE/2);

        // Draw community chest and chance cards
        g2d.setFont(new Font("Arial", Font.ITALIC, 14));
        g2d.setColor(Color.BLUE);
        g2d.drawString("Community Chest", BOARD_SIZE/2 - 70, BOARD_SIZE/2 - 50);
        g2d.drawString("Chance", BOARD_SIZE/2 - 30, BOARD_SIZE/2 + 70);
    }

    /**
     * Draw the four corner spaces of the board.
     * Team member(s) responsible: Matt
     */
    private void drawCornerSpaces(Graphics2D g2d) {
        // GO (bottom right)
        g2d.setColor(Color.WHITE);
        g2d.fillRect(BOARD_SIZE - SPACE_HEIGHT, BOARD_SIZE - SPACE_WIDTH, SPACE_HEIGHT, SPACE_WIDTH);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(BOARD_SIZE - SPACE_HEIGHT, BOARD_SIZE - SPACE_WIDTH, SPACE_HEIGHT, SPACE_WIDTH);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("GO", BOARD_SIZE - SPACE_HEIGHT/2 - 15, BOARD_SIZE - SPACE_WIDTH/2 + 5);

        // Draw GO arrow
        g2d.setColor(Color.RED);
        int[] xPoints = {BOARD_SIZE - SPACE_HEIGHT/2, BOARD_SIZE - SPACE_HEIGHT/2 - 15, BOARD_SIZE - SPACE_HEIGHT/2 + 15};
        int[] yPoints = {BOARD_SIZE - SPACE_WIDTH/2 + 20, BOARD_SIZE - SPACE_WIDTH/2 + 40, BOARD_SIZE - SPACE_WIDTH/2 + 40};
        g2d.fillPolygon(xPoints, yPoints, 3);

        // Jail (bottom left)
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, BOARD_SIZE - SPACE_WIDTH, SPACE_HEIGHT, SPACE_WIDTH);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, BOARD_SIZE - SPACE_WIDTH, SPACE_HEIGHT, SPACE_WIDTH);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("JAIL", SPACE_HEIGHT/2 - 15, BOARD_SIZE - SPACE_WIDTH/2 + 5);

        // Draw jail bars
        g2d.setStroke(new BasicStroke(2));
        int jailSize = Math.min(SPACE_HEIGHT, SPACE_WIDTH) / 2;
        int jailX = SPACE_HEIGHT/2 - jailSize/2;
        int jailY = BOARD_SIZE - SPACE_WIDTH/2 - jailSize/2 + 10;
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(jailX, jailY, jailSize, jailSize);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(jailX, jailY, jailSize, jailSize);

        // Draw jail bars
        for (int i = 1; i < 3; i++) {
            g2d.drawLine(jailX + i * jailSize/3, jailY, jailX + i * jailSize/3, jailY + jailSize);
        }
        for (int i = 1; i < 3; i++) {
            g2d.drawLine(jailX, jailY + i * jailSize/3, jailX + jailSize, jailY + i * jailSize/3);
        }

        // Free Parking (top left)
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, SPACE_HEIGHT, SPACE_WIDTH);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, SPACE_HEIGHT, SPACE_WIDTH);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("FREE", SPACE_HEIGHT/2 - 20, SPACE_WIDTH/2);
        g2d.drawString("PARKING", SPACE_HEIGHT/2 - 30, SPACE_WIDTH/2 + 15);

        // Draw parking symbol (car emoji)
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Dialog", Font.PLAIN, 24));
        String carEmoji = "🚗"; // Car emoji
        FontMetrics fm = g2d.getFontMetrics();
        int emojiWidth = fm.stringWidth(carEmoji);
        int emojiX = SPACE_HEIGHT/2 - emojiWidth/2;
        int emojiY = SPACE_WIDTH/2 + 30;
        g2d.drawString(carEmoji, emojiX, emojiY);

        // Go To Jail (top right)
        g2d.setColor(Color.WHITE);
        g2d.fillRect(BOARD_SIZE - SPACE_HEIGHT, 0, SPACE_HEIGHT, SPACE_WIDTH);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(BOARD_SIZE - SPACE_HEIGHT, 0, SPACE_HEIGHT, SPACE_WIDTH);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("GO TO", BOARD_SIZE - SPACE_HEIGHT/2 - 20, SPACE_WIDTH/2);
        g2d.drawString("JAIL", BOARD_SIZE - SPACE_HEIGHT/2 - 15, SPACE_WIDTH/2 + 15);

        // Draw handcuffs emoji
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Dialog", Font.PLAIN, 24));
        String handcuffsEmoji = "⛓️"; // Handcuffs/chains emoji
        FontMetrics fm2 = g2d.getFontMetrics();
        int emojiWidth2 = fm2.stringWidth(handcuffsEmoji);
        int emojiX2 = BOARD_SIZE - SPACE_HEIGHT/2 - emojiWidth2/2;
        int emojiY2 = SPACE_WIDTH/2 + 30;
        g2d.drawString(handcuffsEmoji, emojiX2, emojiY2);
    }

    /**
     * Draw horizontal spaces (top and bottom rows).
     * Team member(s) responsible: Matt and Deborah
     */
    private void drawHorizontalSpaces(Graphics2D g2d, boolean bottom) {
        int y = bottom ? BOARD_SIZE - SPACE_WIDTH : 0;

        for (int i = 1; i < 10; i++) {
            int x;
            int position;

            if (bottom) {
                x = BOARD_SIZE - SPACE_HEIGHT - i * SPACE_HEIGHT;
                position = i;
            } else {
                x = SPACE_HEIGHT + (i - 1) * SPACE_HEIGHT;
                position = 20 + i;
            }

            // Always fill white background first
            g2d.setColor(BOARD_BACKGROUND_COLOR);
            g2d.fillRect(x, y, SPACE_HEIGHT, SPACE_WIDTH);

            String spaceName = positionToName.get(position);

            // Set color if special spaces (Chance, Chest, Tax)
            if (spaceName.contains("Chance")) {
                g2d.setColor(new Color(255, 222, 173)); // Light orange
                g2d.fillRect(x, y, SPACE_HEIGHT, SPACE_WIDTH);
            } else if (spaceName.contains("Community Chest")) {
                g2d.setColor(new Color(230, 230, 250)); // Lavender
                g2d.fillRect(x, y, SPACE_HEIGHT, SPACE_WIDTH);
            } else if (spaceName.contains("Tax")) {
                g2d.setColor(new Color(192, 192, 192)); // Silver
                g2d.fillRect(x, y, SPACE_HEIGHT, SPACE_WIDTH);
            } else {
                // Regular properties (NOT Railroad, NOT Utility)
                if (propertyColors.containsKey(spaceName) &&
                        !isRailroad(spaceName) &&
                        !spaceName.contains("Company") &&
                        !spaceName.contains("Works")) {

                    Color stripeColor = propertyColors.get(spaceName);
                    g2d.setColor(stripeColor);

                    int stripeHeight = SPACE_WIDTH / 5; // Thin stripe
                    if (bottom) {
                        g2d.fillRect(x, y, SPACE_HEIGHT, stripeHeight);
                    } else {
                        g2d.fillRect(x, y + SPACE_WIDTH - stripeHeight, SPACE_HEIGHT, stripeHeight);
                    }
                }
            }

            // Draw the black border
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, SPACE_HEIGHT, SPACE_WIDTH);

            // Draw space special icons or labels
            if (spaceName.contains("Chance") && spaceImages.containsKey("Chance")) {
                Image img = spaceImages.get("Chance");
                int imgX = x + (SPACE_HEIGHT - img.getWidth(null)) / 2;
                int imgY = y + 5;
                g2d.drawImage(img, imgX, imgY, null);
            } else if (spaceName.contains("Community Chest") && spaceImages.containsKey("Community Chest")) {
                Image img = spaceImages.get("Community Chest");
                int imgX = x + (SPACE_HEIGHT - img.getWidth(null)) / 2;
                int imgY = y + 5;
                g2d.drawImage(img, imgX, imgY, null);
            } else if (spaceName.contains("Electric Company")) {
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Dialog", Font.PLAIN, 24));
                String emoji = "💡"; // Lightbulb
                FontMetrics fm = g2d.getFontMetrics();
                int emojiWidth = fm.stringWidth(emoji);
                int emojiX = x + (SPACE_HEIGHT - emojiWidth) / 2;
                int emojiY = y + SPACE_WIDTH / 2 + 10;
                g2d.drawString(emoji, emojiX, emojiY);
            } else if (spaceName.contains("Income Tax")) {
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 9));
                g2d.drawString("Pay ₩200", x + SPACE_HEIGHT / 4, y + SPACE_WIDTH / 3);
                g2d.drawString("or 10%", x + SPACE_HEIGHT / 4, y + SPACE_WIDTH / 2);
                g2d.setFont(new Font("Dialog", Font.PLAIN, 24));
                g2d.drawString("💰", x + SPACE_HEIGHT / 3, y + SPACE_WIDTH / 2 + 20);
            } else if (spaceName.contains("Luxury Tax")) {
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 9));
                g2d.drawString("Pay ₩75", x + SPACE_HEIGHT / 4, y + SPACE_WIDTH / 3);
                g2d.setFont(new Font("Dialog", Font.PLAIN, 24));
                g2d.drawString("💎", x + SPACE_HEIGHT / 3, y + SPACE_WIDTH / 2 + 20);
            }
            if (propertyColors.containsKey(spaceName) &&
                    !isRailroad(spaceName) &&
                    !spaceName.contains("Company") &&
                    !spaceName.contains("Works")) {

                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 8));
                FontMetrics fm = g2d.getFontMetrics();

                String[] words = spaceName.split(" ");
                int yOffset = bottom ? y + SPACE_WIDTH / 3 : y + SPACE_WIDTH / 2;

                for (int w = 0; w < words.length; w++) {
                    int textWidth = fm.stringWidth(words[w]);
                    int textX = x + (SPACE_HEIGHT - textWidth) / 2;
                    int textY = yOffset + (w * 10);
                    g2d.drawString(words[w], textX, textY);
                }

                if (positionToPrice.containsKey(position)) {
                    g2d.setFont(new Font("Arial", Font.BOLD, 8));
                    String price = "₩" + positionToPrice.get(position);
                    int priceWidth = fm.stringWidth(price);
                    int priceX = x + (SPACE_HEIGHT - priceWidth) / 2;
                    int priceY = bottom ? y + SPACE_WIDTH - 5 : y + 12;
                    g2d.drawString(price, priceX, priceY);
                }
            }

            // Railroads abbreviation (draw separately, no overlap)
            if (isRailroad(spaceName)) {
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 7));
                String abbreviation = spaceName.replace("Railroad", "RR").trim();
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(abbreviation);
                int textX = x + (SPACE_HEIGHT - textWidth) / 2;
                int textY = y + SPACE_WIDTH / 2;
                g2d.drawString(abbreviation, textX, textY);

                // Draw railroad price too
                if (positionToPrice.containsKey(position)) {
                    String price = "₩" + positionToPrice.get(position);
                    int priceWidth = fm.stringWidth(price);
                    int priceX = x + (SPACE_HEIGHT - priceWidth) / 2;
                    int priceY = textY + 12;
                    g2d.drawString(price, priceX, priceY);
                }
            }

            // Ownership mark (small dot)
            for (PlayerData player : players) {
                if (player.ownedProperties.contains(position)) {
                    g2d.setColor(player.color);
                    g2d.fillOval(x + 5, y + 5, 8, 8);
                    break;
                }
            }
        }
    }


    /**
     * Draw vertical spaces (left and right columns).
     * Team member(s) responsible: Matt and Deborah
     */
    private void drawVerticalSpaces(Graphics2D g2d, boolean left) {
        int x = left ? 0 : BOARD_SIZE - SPACE_HEIGHT;

        for (int i = 1; i < 10; i++) {
            int y = BOARD_SIZE - SPACE_WIDTH - i * SPACE_WIDTH;
            int position = left ? 10 + i : (40 - i);

            // Always fill background white first
            g2d.setColor(BOARD_BACKGROUND_COLOR);
            g2d.fillRect(x, y, SPACE_HEIGHT, SPACE_WIDTH);

            String spaceName = positionToName.get(position);

            // Special spaces: Chance, Chest, Tax
            if (spaceName.contains("Chance")) {
                g2d.setColor(new Color(255, 222, 173));
                g2d.fillRect(x, y, SPACE_HEIGHT, SPACE_WIDTH);
            } else if (spaceName.contains("Community Chest")) {
                g2d.setColor(new Color(230, 230, 250));
                g2d.fillRect(x, y, SPACE_HEIGHT, SPACE_WIDTH);
            } else if (spaceName.contains("Tax")) {
                g2d.setColor(new Color(192, 192, 192));
                g2d.fillRect(x, y, SPACE_HEIGHT, SPACE_WIDTH);
            } else {
                // Regular property (not Railroad or Utility)
                if (propertyColors.containsKey(spaceName) &&
                        !isRailroad(spaceName) &&
                        !spaceName.contains("Company") &&
                        !spaceName.contains("Works")) {

                    Color stripeColor = propertyColors.get(spaceName);
                    g2d.setColor(stripeColor);

                    int stripeWidth = SPACE_HEIGHT / 5;
                    if (left) {
                        g2d.fillRect(x + SPACE_HEIGHT - stripeWidth, y, stripeWidth, SPACE_WIDTH);
                    } else {
                        g2d.fillRect(x, y, stripeWidth, SPACE_WIDTH);
                    }
                }
            }

            // Black border
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, SPACE_HEIGHT, SPACE_WIDTH);

            // Draw special space images
            if (spaceName.contains("Chance") && spaceImages.containsKey("Chance")) {
                Image img = spaceImages.get("Chance");
                int imgX = x + (SPACE_HEIGHT - img.getWidth(null)) / 2;
                int imgY = y + 5;
                g2d.drawImage(img, imgX, imgY, null);
            } else if (spaceName.contains("Community Chest") && spaceImages.containsKey("Community Chest")) {
                Image img = spaceImages.get("Community Chest");
                int imgX = x + (SPACE_HEIGHT - img.getWidth(null)) / 2;
                int imgY = y + 5;
                g2d.drawImage(img, imgX, imgY, null);
            } else if (spaceName.contains("Electric Company")) {
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Dialog", Font.PLAIN, 24));
                String emoji = "💡";
                FontMetrics fm = g2d.getFontMetrics();
                int emojiWidth = fm.stringWidth(emoji);
                int emojiX = x + (SPACE_HEIGHT - emojiWidth) / 2;
                int emojiY = y + SPACE_WIDTH / 2 + 10;
                g2d.drawString(emoji, emojiX, emojiY);
            } else if (spaceName.contains("Income Tax")) {
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 9));
                g2d.drawString("Pay ₩200", x + SPACE_HEIGHT / 4, y + SPACE_WIDTH / 3);
                g2d.drawString("or 10%", x + SPACE_HEIGHT / 4, y + SPACE_WIDTH / 2);
                g2d.setFont(new Font("Dialog", Font.PLAIN, 24));
                g2d.drawString("💰", x + SPACE_HEIGHT / 3, y + SPACE_WIDTH / 2 + 20);
            } else if (spaceName.contains("Luxury Tax")) {
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 9));
                g2d.drawString("Pay ₩75", x + SPACE_HEIGHT / 4, y + SPACE_WIDTH / 3);
                g2d.setFont(new Font("Dialog", Font.PLAIN, 24));
                g2d.drawString("💎", x + SPACE_HEIGHT / 3, y + SPACE_WIDTH / 2 + 20);
            }

            // Add name and price for property spaces (skip Railroads and Utilities)
            if (propertyColors.containsKey(spaceName) &&
                    !isRailroad(spaceName) &&
                    !spaceName.contains("Company") &&
                    !spaceName.contains("Works")) {
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 8));
                FontMetrics fm = g2d.getFontMetrics();

                String[] words = spaceName.split(" ");
                int xOffset = left ? x + 5 : x + SPACE_HEIGHT / 5 + 5;

                for (int w = 0; w < words.length; w++) {
                    int textWidth = fm.stringWidth(words[w]);
                    int textX = Math.min(xOffset, x + SPACE_HEIGHT - textWidth - 2);
                    int textY = y + 15 + (w * 10);
                    if (textY < y + SPACE_WIDTH - 5) {
                        g2d.drawString(words[w], textX, textY);
                    }
                }

                if (positionToPrice.containsKey(position)) {
                    g2d.setFont(new Font("Arial", Font.BOLD, 8));
                    String price = "$" + positionToPrice.get(position);
                    int priceWidth = fm.stringWidth(price);
                    int priceX = Math.min(xOffset, x + SPACE_HEIGHT - priceWidth - 2);
                    int priceY = y + SPACE_WIDTH - 5;
                    g2d.drawString(price, priceX, priceY);
                }
            }

            // Railroads abbreviation (draw separately)
            if (isRailroad(spaceName)) {
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 7));

                String abbreviation;
                if (spaceName.contains("Reading")) {
                    abbreviation = "Reading RR";
                } else if (spaceName.contains("Pennsylvania")) {
                    abbreviation = "Penn RR";
                } else if (spaceName.contains("B. & O.")) {
                    abbreviation = "B&O RR";
                } else if (spaceName.contains("Short Line")) {
                    abbreviation = "Short RR";
                } else {
                    abbreviation = "RR"; // fallback
                }

                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(abbreviation);
                int textX = x + (SPACE_HEIGHT - textWidth) / 2;
                int textY = y + SPACE_WIDTH / 2;
                g2d.drawString(abbreviation, textX, textY);

                if (positionToPrice.containsKey(position)) {
                    String price = "₩" + positionToPrice.get(position);
                    int priceWidth = fm.stringWidth(price);
                    int priceX = x + (SPACE_HEIGHT - priceWidth) / 2;
                    int priceY = textY + 12;
                    g2d.drawString(price, priceX, priceY);
                }
            }

            // Mark ownership (small colored dot)
            for (PlayerData player : players) {
                if (player.ownedProperties.contains(position)) {
                    g2d.setColor(player.color);
                    if (left) {
                        g2d.fillOval(x + SPACE_HEIGHT - 10, y + 5, 8, 8);
                    } else {
                        g2d.fillOval(x + 5, y + 5, 8, 8);
                    }
                    break;
                }
            }
        }
    }


    /**
     * Draw player tokens on the board.
     * Team member(s) responsible: Matt
     */
    private void drawPlayers(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 0; i < players.size(); i++) {
            PlayerData player = players.get(i);
            // Calculate player position coordinates
            int[] coordinates = getCoordinatesForPosition(player.position, i);

            // Highlight current player
            if (i == currentPlayerIndex) {
                g2d.setColor(new Color(255, 255, 0, 100));
                g2d.fillOval(coordinates[0] - 2, coordinates[1] - 2, 34, 34);
            }

            // Check if there's a token image from the controller
            ImageIcon tokenIcon = null;
            if (controller != null && player.tokenName != null) {
                tokenIcon = controller.getTokenImage(player.tokenName);
                
                // Resize the token image to make sure it fits on the board space
                if (tokenIcon != null) {
                    Image img = tokenIcon.getImage();
                    // Resize to be a bit smaller than a property space
                    Image resizedImg = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
                    tokenIcon = new ImageIcon(resizedImg);
                }
            }
            
            if (tokenIcon != null) {
                // Draw the token icon
                tokenIcon.paintIcon(boardPanel, g2d, coordinates[0], coordinates[1]);
            } else {
                // Fallback: Draw a simple colored circle
                g2d.setColor(player.color);
                g2d.fillOval(coordinates[0], coordinates[1], 30, 30);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(coordinates[0], coordinates[1], 30, 30);
                
                // Draw player number
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                g2d.drawString(String.valueOf(i + 1), coordinates[0] + 12, coordinates[1] + 20);
            }
        }
    }

    /**
     * Create the dice panel that shows dice rolls.
     */
    private void createDicePanel() {
        dicePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawDice(g);
            }
        };

        dicePanel.setPreferredSize(new Dimension(211, BOARD_SIZE));
        dicePanel.setBorder(BorderFactory.createTitledBorder("Dice"));

        gamePanel.add(dicePanel, BorderLayout.WEST);
    }

    /**
     * Draw the dice with current values.
     * Team member(s) responsible: Matt and Deborah
     */
    private void drawDice(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int diceSize = 90;
        int x1 = 15;
        int y1 = 30;
        int x2 = 110;
        int y2 = 30;

        // Draw first die
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(x1, y1, diceSize, diceSize, 5, 5);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(x1, y1, diceSize, diceSize, 5, 5);

        // Draw second die
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(x2, y2, diceSize, diceSize, 5, 5);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(x2, y2, diceSize, diceSize, 5, 5);

        // Draw dots on first die
        drawDiceDots(g2d, x1, y1, diceSize, dice1Value);

        // Draw dots on second die
        drawDiceDots(g2d, x2, y2, diceSize, dice2Value);

        // Draw total
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));

        String totalText = "Total: " + (dice1Value + dice2Value);

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(totalText);

        int totalDiceWidth = (x2 + diceSize) - x1;
        int centerX = x1 + totalDiceWidth / 2;
        int textX = centerX - (textWidth / 2);
        int textY = y1 + diceSize + 20;
        g2d.drawString(totalText, textX, textY);
    }

    /**
     * Draw the dots on a die based on its value.
     * Team member(s) responsible: Matt
     */
    private void drawDiceDots(Graphics2D g2d, int x, int y, int size, int value) {
        g2d.setColor(Color.BLACK);
        int dotSize = 5;
        int space = size / 4;

        // Center dot (for values 1, 3, 5)
        if (value == 1 || value == 3 || value == 5) {
            g2d.fillOval(x + size/2 - dotSize/2, y + size/2 - dotSize/2, dotSize, dotSize);
        }

        // Top-left and bottom-right dots (for values 2, 3, 4, 5, 6)
        if (value >= 2) {
            g2d.fillOval(x + space - dotSize/2, y + space - dotSize/2, dotSize, dotSize);
            g2d.fillOval(x + 3*space - dotSize/2, y + 3*space - dotSize/2, dotSize, dotSize);
        }

        // Bottom-left and top-right dots (for values 4, 5, 6)
        if (value >= 4) {
            g2d.fillOval(x + space - dotSize/2, y + 3*space - dotSize/2, dotSize, dotSize);
            g2d.fillOval(x + 3*space - dotSize/2, y + space - dotSize/2, dotSize, dotSize);
        }

        // Middle-left and middle-right dots (for value 6)
        if (value == 6) {
            g2d.fillOval(x + space - dotSize/2, y + 2*space - dotSize/2, dotSize, dotSize);
            g2d.fillOval(x + 3*space - dotSize/2, y + 2*space - dotSize/2, dotSize, dotSize);
        }
    }

    // Control buttons
    private JButton rollDiceButton;
    private JButton endTurnButton;
    private JButton newGameButton;
    // Buy Property button and field removed - property purchase is now handled via showPropertyPurchaseDialog
    
    /**
     * Create the control panel with game action buttons.
     * Team member(s) responsible: Matt
     */
    private void createControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(1, 4, 10, 0));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Roll dice button
        rollDiceButton = new JButton("Roll Dice");
        rollDiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (controller != null && controller.isGameInProgress()) {
                    // Controller will handle this via the listener we set up
                } else {
                    // Legacy behavior for testing without controller
                    if (!diceRolled) {
                        // Simulate rolling dice
                        dice1Value = (int)(Math.random() * 6) + 1;
                        dice2Value = (int)(Math.random() * 6) + 1;
                        dicePanel.repaint();
                        
                        // Update statistics
                        totalDiceRolls++;
                        if (dice1Value == dice2Value) {
                            totalDoubles++;
                        }
                        
                        // Update statistics display
                        updateGameStatistics();

                        // Move active player
                        PlayerData currentPlayer = players.get(currentPlayerIndex);
                        int oldPosition = currentPlayer.position;
                        currentPlayer.position = (currentPlayer.position + dice1Value + dice2Value) % 40;
                        
                        // Check if player passed GO
                        if (currentPlayer.position < oldPosition && oldPosition != 30) { // not coming from Go To Jail
                            totalGoPasses++;
                            updateGameStatistics();
                        }

                        // Handle landing on property
                        handleLandOnProperty(currentPlayer);

                        boardPanel.repaint();
                        updatePlayerInfoPanel();

                        diceRolled = true;
                    } else {
                        JOptionPane.showMessageDialog(mainFrame,
                                "You've already rolled the dice. End your turn first.",
                                "Dice Already Rolled", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });

        // End turn button
        endTurnButton = new JButton("End Turn");
        endTurnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (controller != null && controller.isGameInProgress()) {
                    // Controller will handle this via the listener we set up
                } else {
                    // Legacy behavior for testing without controller
                    if (diceRolled) {
                        // Update turns count
                        totalTurns++;
                        
                        // Move to next player
                        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
                        diceRolled = false;
                        
                        // Update UI
                        updatePlayerInfoPanel();
                        boardPanel.repaint();
                        updateGameStatistics();
                    } else {
                        JOptionPane.showMessageDialog(mainFrame,
                                "You must roll the dice before ending your turn.",
                                "Roll Dice First", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });

        // Buy property button removed - property purchases now handled through popups when landing on properties

        // Add a new game button
        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (controller != null) {
                    // Controller will handle this via the listener we set up
                }
            }
        });

        // Add buttons to the control panel
        controlPanel.add(rollDiceButton);
        controlPanel.add(endTurnButton);
        controlPanel.add(newGameButton);
        // Buy Property button removed - property purchases are handled by the PropertyPurchaseDialog when landing on properties
    }
    
    /**
     * Add a listener to the roll dice button.
     * 
     * @param listener The action listener to add
     */
    public void addRollDiceListener(ActionListener listener) {
        if (rollDiceButton != null) {
            // Remove existing listeners to prevent duplicates
            for (ActionListener al : rollDiceButton.getActionListeners()) {
                rollDiceButton.removeActionListener(al);
            }
            rollDiceButton.addActionListener(listener);
        }
    }
    
    /**
     * Add a listener to the end turn button.
     * 
     * @param listener The action listener to add
     */
    public void addEndTurnListener(ActionListener listener) {
        if (endTurnButton != null) {
            // Remove existing listeners to prevent duplicates
            for (ActionListener al : endTurnButton.getActionListeners()) {
                endTurnButton.removeActionListener(al);
            }
            endTurnButton.addActionListener(listener);
        }
    }
    
    /**
     * Add a listener to the new game button.
     * 
     * @param listener The action listener to add
     */
    public void addNewGameListener(ActionListener listener) {
        if (newGameButton != null) {
            // Remove existing listeners to prevent duplicates
            for (ActionListener al : newGameButton.getActionListeners()) {
                newGameButton.removeActionListener(al);
            }
            newGameButton.addActionListener(listener);
        }
    }
    
    /**
     * Add a listener to the buy property button.
     * Note: Buy Property button has been removed as property purchases are now handled
     * through popups when landing on properties.
     * 
     * @param listener The action listener to add
     */
    @Deprecated
    public void addBuyPropertyListener(ActionListener listener) {
        // Buy Property button has been removed
        // This method is kept for backward compatibility
    }

    /**
     * Create the player info panel to display player stats.
     * Team member(s) responsible: Matt
     */
    private void createPlayerInfoPanel() {
        playerInfoPanel = new JPanel();
        playerInfoPanel.setLayout(new BoxLayout(playerInfoPanel, BoxLayout.Y_AXIS));
        playerInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        playerInfoPanel.setPreferredSize(new Dimension(200, BOARD_SIZE));

        JLabel titleLabel = new JLabel("Players");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerInfoPanel.add(titleLabel);
        playerInfoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        updatePlayerInfoPanel();

        gamePanel.add(playerInfoPanel, BorderLayout.EAST);
    }

    /**
     * Update the player info panel with current stats.
     * Team member(s) responsible: Matt
     */
    private void updatePlayerInfoPanel() {
        // Clear existing player info
        playerInfoPanel.removeAll();

        JLabel titleLabel = new JLabel("Players");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerInfoPanel.add(titleLabel);
        playerInfoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Add updated player info
        for (int i = 0; i < players.size(); i++) {
            PlayerData player = players.get(i);
            JPanel playerPanel = new JPanel();
            playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));

            // Highlight current player
            if (i == currentPlayerIndex) {
                playerPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(player.color, 2),
                        BorderFactory.createLineBorder(Color.YELLOW, 2)));
            } else {
                playerPanel.setBorder(BorderFactory.createLineBorder(player.color, 2));
            }

            playerPanel.setMaximumSize(new Dimension(200, 100));

            JLabel nameLabel = new JLabel(player.name + (i == currentPlayerIndex ? " (Current)" : ""));
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            nameLabel.setForeground(player.color);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel moneyLabel = new JLabel("Money: $" + player.money);
            moneyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel posLabel = new JLabel("Position: " + player.position +
                    " (" + positionToName.get(player.position) + ")");
            posLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel propertiesLabel = new JLabel("Properties: " + player.ownedProperties.size());
            propertiesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            playerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            playerPanel.add(nameLabel);
            playerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            playerPanel.add(moneyLabel);
            playerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            playerPanel.add(posLabel);
            playerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            playerPanel.add(propertiesLabel);
            playerPanel.add(Box.createRigidArea(new Dimension(0, 5)));

            playerInfoPanel.add(playerPanel);
            playerInfoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        playerInfoPanel.revalidate();
        playerInfoPanel.repaint();
    }

    /**
     * Create the property panel to show property details.
     * Team member(s) responsible: Matt
     */
    private void createPropertyPanel() {
        propertyPanel = new JPanel();
        propertyPanel.setLayout(new BoxLayout(propertyPanel, BoxLayout.Y_AXIS));
        propertyPanel.setBorder(BorderFactory.createTitledBorder("Property Details"));
        propertyPanel.setPreferredSize(new Dimension(200, 150));

        updatePropertyPanel();

        dicePanel.setLayout(new BorderLayout());
        dicePanel.add(propertyPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create the property management panel for the tabbed interface.
     * Team member(s) responsible: matt
     */
    private JPanel createPropertyManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add title
        JLabel titleLabel = new JLabel("Property Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Create the main content panel with properties list and details
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Left side - List of properties
        JPanel propertiesListPanel = new JPanel(new BorderLayout());
        propertiesListPanel.setBorder(BorderFactory.createTitledBorder("Your Properties"));
        
        // Create list model for properties owned by current player
        DefaultListModel<String> propertiesModel = new DefaultListModel<>();
        
        // Initialize mortgaged properties map for this session
        
        // Add current player's properties to the model - this will be updated when tab is selected
        refreshPropertyManagementPanel(propertiesModel);
        
        
        // Create a custom cell renderer to show mortgaged properties differently
        propertiesList = new JList<>(propertiesModel);
        propertiesList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                
                // Get property position from name
                String propertyName = (String) value;
                int position = -1;
                for (Map.Entry<Integer, String> entry : positionToName.entrySet()) {
                    if (entry.getValue().equals(propertyName)) {
                        position = entry.getKey();
                        break;
                    }
                }
                
                // Check if property is mortgaged
                if (position != -1 && mortgagedProperties.containsKey(position)) {
                    boolean isMortgaged = mortgagedProperties.get(position);
                    if (isMortgaged) {
                        label.setText(propertyName + " (Mortgaged)");
                        if (!isSelected) {
                            label.setForeground(Color.GRAY);
                        }
                    }
                }
                
                return label;
            }
        });
        
        propertiesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane propertiesScroll = new JScrollPane(propertiesList);
        propertiesListPanel.add(propertiesScroll, BorderLayout.CENTER);
        
        // These JLabels are already declared final above
        
        // Add selection listener
        propertiesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedProperty = propertiesList.getSelectedValue();
                if (selectedProperty != null) {
                    // Find property position
                    int position = -1;
                    for (Map.Entry<Integer, String> entry : positionToName.entrySet()) {
                        if (entry.getValue().equals(selectedProperty)) {
                            position = entry.getKey();
                            break;
                        }
                    }
                    
                    if (position != -1) {
                        // Update property details
                        nameLabel.setText(selectedProperty);
                        
                        int price = positionToPrice.getOrDefault(position, 0);
                        priceLabel.setText("Purchase price: $" + price);
                        
                        // Check if property is mortgaged
                        boolean isMortgaged = mortgagedProperties.containsKey(position) && 
                                mortgagedProperties.get(position);
                        
                        if (isMortgaged) {
                            // Mortgaged properties have no rent
                            rentLabel.setText("Current rent: $0 (Mortgaged)");
                            
                            // No houses on mortgaged properties
                            housesLabel.setText("Houses: 0");
                            hotelLabel.setText("Hotel: No");
                            
                            // Enable/disable buttons based on mortgaged state
                            buyHouseButton.setEnabled(false);
                            sellHouseButton.setEnabled(false);
                            mortgageButton.setEnabled(false);
                            unmortgageButton.setEnabled(true);
                        } else {
                            // Calculate basic rent
                            int rent = price / 10;
                            rentLabel.setText("Current rent: $" + rent);
                            
                            // For this simple implementation, assume no houses or hotels initially
                            housesLabel.setText("Houses: 0");
                            hotelLabel.setText("Hotel: No");
                            
                            // Enable all buttons for unmortgaged properties
                            buyHouseButton.setEnabled(true);
                            sellHouseButton.setEnabled(false); // Initially no houses to sell
                            mortgageButton.setEnabled(true);
                            unmortgageButton.setEnabled(false);
                        }
                        
                        // Calculate mortgage value (typically half the purchase price)
                        int mortgageValue = price / 2;
                        mortgageLabel.setText("Mortgage value: $" + mortgageValue);
                    }
                } else {
                    // No selection - reset to default values
                    nameLabel.setText("Select a property");
                    priceLabel.setText("Purchase price: $0");
                    rentLabel.setText("Current rent: $0");
                    housesLabel.setText("Houses: 0");
                    hotelLabel.setText("Hotel: No");
                    mortgageLabel.setText("Mortgage value: $0");
                    
                    // Disable buttons when no property is selected
                    buyHouseButton.setEnabled(false);
                    sellHouseButton.setEnabled(false);
                    mortgageButton.setEnabled(false);
                    unmortgageButton.setEnabled(false);
                }
            }
        });
        
        // Right side - Property details and actions
        JPanel propertyDetailsPanel = new JPanel();
        propertyDetailsPanel.setLayout(new BoxLayout(propertyDetailsPanel, BoxLayout.Y_AXIS));
        propertyDetailsPanel.setBorder(BorderFactory.createTitledBorder("Details"));
        
        // Property details components
        nameLabel = new JLabel("Select a property");
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        priceLabel = new JLabel("Purchase price: $0");
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        rentLabel = new JLabel("Current rent: $0");
        rentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        housesLabel = new JLabel("Houses: 0");
        housesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        hotelLabel = new JLabel("Hotel: No");
        hotelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        mortgageLabel = new JLabel("Mortgage value: $0");
        mortgageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add components to property details panel
        propertyDetailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        propertyDetailsPanel.add(nameLabel);
        propertyDetailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        propertyDetailsPanel.add(priceLabel);
        propertyDetailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        propertyDetailsPanel.add(rentLabel);
        propertyDetailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        propertyDetailsPanel.add(housesLabel);
        propertyDetailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        propertyDetailsPanel.add(hotelLabel);
        propertyDetailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        propertyDetailsPanel.add(mortgageLabel);
        propertyDetailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(300, 100));
        
        buyHouseButton = new JButton("Buy House");
        buyHouseButton.setEnabled(false); // Initially disabled until property is selected
        buyHouseButton.addActionListener(event -> {
            String selectedProperty = propertiesList.getSelectedValue();
            if (selectedProperty != null) {
                // Find property position and get house price (typically 1/2 of property price)
                int position = -1;
                for (Map.Entry<Integer, String> entry : positionToName.entrySet()) {
                    if (entry.getValue().equals(selectedProperty)) {
                        position = entry.getKey();
                        break;
                    }
                }
                
                if (position != -1) {
                    int propertyPrice = positionToPrice.getOrDefault(position, 0);
                    int housePrice = propertyPrice / 2;
                    
                    // Get current player
                    PlayerData currentPlayer = players.get(currentPlayerIndex);
                    
                    // Check if player has enough money
                    if (currentPlayer.money >= housePrice) {
                        // Update houses display
                        int currentHouses = Integer.parseInt(housesLabel.getText().replaceAll("\\D+", ""));
                        if (currentHouses < 4) {
                            // Buy a house
                            currentPlayer.money -= housePrice;
                            housesLabel.setText("Houses: " + (currentHouses + 1));
                            
                            // Enable sell house button now that we have houses
                            sellHouseButton.setEnabled(true);
                            
                            JOptionPane.showMessageDialog(mainFrame,
                                    "Bought a house for " + selectedProperty + " for $" + housePrice,
                                    "Buy House", JOptionPane.INFORMATION_MESSAGE);
                            
                            // Update rent based on new house count
                            int newRent = calculateRentWithHouses(propertyPrice, currentHouses + 1);
                            rentLabel.setText("Current rent: $" + newRent);
                        } else {
                            // Buy a hotel
                            int hotelPrice = housePrice * 2; // Hotels cost more
                            
                            if (currentPlayer.money >= hotelPrice) {
                                currentPlayer.money -= hotelPrice;
                                housesLabel.setText("Houses: 0");
                                hotelLabel.setText("Hotel: Yes");
                                buyHouseButton.setEnabled(false); // Can't buy more than a hotel
                                
                                // Update rent for hotel
                                int hotelRent = calculateRentWithHotel(propertyPrice);
                                rentLabel.setText("Current rent: $" + hotelRent);
                                
                                JOptionPane.showMessageDialog(mainFrame,
                                        "Bought a hotel for " + selectedProperty + " for $" + hotelPrice,
                                        "Buy Hotel", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(mainFrame,
                                        "Not enough money to buy a hotel. You need $" + hotelPrice,
                                        "Insufficient Funds", JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        }
                        
                        // Update UI
                        updatePlayerInfoPanel();
                        updateGameStatistics();
                    } else {
                        JOptionPane.showMessageDialog(mainFrame,
                                "Not enough money to buy a house. You need $" + housePrice,
                                "Insufficient Funds", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        
        sellHouseButton = new JButton("Sell House");
        sellHouseButton.setEnabled(false); // Initially disabled until property is selected
        sellHouseButton.addActionListener(event -> {
            String selectedProperty = propertiesList.getSelectedValue();
            if (selectedProperty != null) {
                // Find property position
                int position = -1;
                for (Map.Entry<Integer, String> entry : positionToName.entrySet()) {
                    if (entry.getValue().equals(selectedProperty)) {
                        position = entry.getKey();
                        break;
                    }
                }
                
                if (position != -1) {
                    // Get property price for calculations
                    int propertyPrice = positionToPrice.getOrDefault(position, 0);
                    
                    // Get current houses/hotel status
                    int currentHouses = Integer.parseInt(housesLabel.getText().replaceAll("\\D+", ""));
                    boolean hasHotel = hotelLabel.getText().contains("Yes");
                    
                    // Get current player
                    PlayerData currentPlayer = players.get(currentPlayerIndex);
                    
                    if (hasHotel) {
                        // Sell hotel (get half the purchase price back)
                        int hotelPrice = propertyPrice;
                        int sellPrice = hotelPrice / 2;
                        
                        // Update money and UI
                        currentPlayer.money += sellPrice;
                        hotelLabel.setText("Hotel: No");
                        housesLabel.setText("Houses: 4");
                        buyHouseButton.setEnabled(true); // Can buy houses again
                        
                        // Update rent to reflect 4 houses
                        int newRent = calculateRentWithHouses(propertyPrice, 4);
                        rentLabel.setText("Current rent: $" + newRent);
                        
                        JOptionPane.showMessageDialog(mainFrame,
                                "Sold hotel on " + selectedProperty + " for $" + sellPrice,
                                "Sell Hotel", JOptionPane.INFORMATION_MESSAGE);
                    } else if (currentHouses > 0) {
                        // Sell a house (get half the purchase price back)
                        int housePrice = propertyPrice / 2;
                        int sellPrice = housePrice / 2;
                        
                        // Update money and UI
                        currentPlayer.money += sellPrice;
                        housesLabel.setText("Houses: " + (currentHouses - 1));
                        
                        // Update rent to reflect fewer houses
                        int newRent = calculateRentWithHouses(propertyPrice, currentHouses - 1);
                        rentLabel.setText("Current rent: $" + newRent);
                        
                        JOptionPane.showMessageDialog(mainFrame,
                                "Sold house on " + selectedProperty + " for $" + sellPrice,
                                "Sell House", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(mainFrame,
                                "No houses or hotels to sell on " + selectedProperty,
                                "Sell House", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    
                    // Update UI
                    updatePlayerInfoPanel();
                    updateGameStatistics();
                }
            }
        });
        
        mortgageButton = new JButton("Mortgage");
        mortgageButton.setEnabled(false); // Initially disabled until property is selected
        mortgageButton.addActionListener(event -> {
            String selectedProperty = propertiesList.getSelectedValue();
            if (selectedProperty != null) {
                // Find property position and get mortgage value
                int position = -1;
                for (Map.Entry<Integer, String> entry : positionToName.entrySet()) {
                    if (entry.getValue().equals(selectedProperty)) {
                        position = entry.getKey();
                        break;
                    }
                }
                
                if (position != -1) {
                    int price = positionToPrice.getOrDefault(position, 0);
                    int mortgageValue = price / 2;
                    
                    // Get current houses/hotel status
                    int currentHouses = Integer.parseInt(housesLabel.getText().replaceAll("\\D+", ""));
                    boolean hasHotel = hotelLabel.getText().contains("Yes");
                    
                    // Check if there are houses or hotels on the property
                    if (currentHouses > 0 || hasHotel) {
                        JOptionPane.showMessageDialog(mainFrame,
                                "You must sell all houses and hotels before mortgaging this property.",
                                "Cannot Mortgage", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Update player's money
                    PlayerData currentPlayer = players.get(currentPlayerIndex);
                    currentPlayer.money += mortgageValue;
                    
                    // Update rent to show it's mortgaged
                    rentLabel.setText("Current rent: $0 (Mortgaged)");
                    
                    // Mark property as mortgaged
                    mortgagedProperties.put(position, true);
                    
                    // Update the list to show the property as mortgaged
                    propertiesList.repaint();
                    
                    JOptionPane.showMessageDialog(mainFrame,
                            "Mortgaging " + selectedProperty + " for $" + mortgageValue,
                            "Mortgage Property", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Update UI
                    updatePlayerInfoPanel();
                    updateGameStatistics();
                    
                    // Disable mortgage button and enable unmortgage
                    mortgageButton.setEnabled(false);
                    unmortgageButton.setEnabled(true);
                    
                    // Disable house buying for mortgaged properties
                    buyHouseButton.setEnabled(false);
                    sellHouseButton.setEnabled(false);
                }
            }
        });
        
        unmortgageButton = new JButton("Unmortgage");
        unmortgageButton.setEnabled(false); // Initially disabled until property is selected
        unmortgageButton.addActionListener(event -> {
            String selectedProperty = propertiesList.getSelectedValue();
            if (selectedProperty != null) {
                // Find property position and calculate unmortgage value
                int position = -1;
                for (Map.Entry<Integer, String> entry : positionToName.entrySet()) {
                    if (entry.getValue().equals(selectedProperty)) {
                        position = entry.getKey();
                        break;
                    }
                }
                
                if (position != -1) {
                    int price = positionToPrice.getOrDefault(position, 0);
                    int mortgageValue = price / 2;
                    int unmortgageValue = (int)(mortgageValue * 1.1); // 10% interest
                    
                    // Check if player has enough money
                    PlayerData currentPlayer = players.get(currentPlayerIndex);
                    if (currentPlayer.money >= unmortgageValue) {
                        // Update player's money
                        currentPlayer.money -= unmortgageValue;
                        
                        // Update rent display to show base rent
                        int baseRent = price / 10;
                        rentLabel.setText("Current rent: $" + baseRent);
                        
                        // Mark property as unmortgaged
                        mortgagedProperties.put(position, false);
                        
                        // Update the list to show the property as unmortgaged
                        propertiesList.repaint();
                        
                        JOptionPane.showMessageDialog(mainFrame,
                                "Unmortgaging " + selectedProperty + " for $" + unmortgageValue,
                                "Unmortgage Property", JOptionPane.INFORMATION_MESSAGE);
                        
                        // Update UI
                        updatePlayerInfoPanel();
                        updateGameStatistics();
                        
                        // Enable mortgage button and disable unmortgage
                        mortgageButton.setEnabled(true);
                        unmortgageButton.setEnabled(false);
                        
                        // Re-enable house buying
                        buyHouseButton.setEnabled(true);
                        sellHouseButton.setEnabled(true);
                    } else {
                        JOptionPane.showMessageDialog(mainFrame,
                                "Not enough money to unmortgage this property. You need $" + unmortgageValue,
                                "Insufficient Funds", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        
        buttonPanel.add(buyHouseButton);
        buttonPanel.add(sellHouseButton);
        buttonPanel.add(mortgageButton);
        buttonPanel.add(unmortgageButton);
        
        propertyDetailsPanel.add(buttonPanel);
        propertyDetailsPanel.add(Box.createVerticalGlue());
        
        // Add list and details to content panel
        contentPanel.add(propertiesListPanel);
        contentPanel.add(propertyDetailsPanel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Custom dialog for property purchase decisions.
     * Shows property details and gives the player options to buy or send to auction.
     * Team member(s) responsible: matt
     */
    public class PropertyPurchaseDialog extends JDialog {
        private boolean wantsToBuy = false;
        private JLabel propertyNameLabel;
        private JLabel propertyPriceLabel;
        private JLabel propertyRentLabel;
        private JLabel propertyGroupLabel;
        private JLabel playerMoneyLabel;
        private JButton buyButton;
        private JButton auctionButton;
        
        // Debug flag - will print debug info if true
        private final boolean DEBUG = true;
        
        public PropertyPurchaseDialog(JFrame parent, Property property, int playerMoney) {
            super(parent, "Property Purchase", true);
            setSize(400, 300);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());
            
            // Property information panel
            JPanel propertyInfoPanel = new JPanel();
            propertyInfoPanel.setLayout(new BoxLayout(propertyInfoPanel, BoxLayout.Y_AXIS));
            propertyInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Property name with colored background based on property color
            propertyNameLabel = new JLabel(property.getName());
            propertyNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
            propertyNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JPanel colorPanel = new JPanel();
            colorPanel.setPreferredSize(new Dimension(380, 40));
            colorPanel.setMaximumSize(new Dimension(380, 40));
            
            // Set background color based on property color
            String propertyType = property.getName();
            if (propertyType.contains("Railroad")) {
                colorPanel.setBackground(Color.BLACK);
                propertyNameLabel.setForeground(Color.WHITE);
            } else if (propertyType.contains("Electric") || propertyType.contains("Water")) {
                colorPanel.setBackground(Color.LIGHT_GRAY);
                propertyNameLabel.setForeground(Color.BLACK);
            } else {
                // Get property color from existing map
                Color color = propertyColors.getOrDefault(property.getName(), Color.WHITE);
                colorPanel.setBackground(color);
                
                // Set text color for readability based on background
                if (color.equals(Color.BLUE) || color.equals(new Color(0, 100, 0)) || 
                    color.equals(new Color(128, 0, 128))) {
                    propertyNameLabel.setForeground(Color.WHITE);
                } else {
                    propertyNameLabel.setForeground(Color.BLACK);
                }
            }
            
            colorPanel.setLayout(new BorderLayout());
            colorPanel.add(propertyNameLabel, BorderLayout.CENTER);
            
            // Property details
            propertyPriceLabel = new JLabel("Price: $" + property.getPurchasePrice());
            propertyPriceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            propertyPriceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            int baseRent = property.getPurchasePrice() / 10;
            propertyRentLabel = new JLabel("Rent: $" + baseRent);
            propertyRentLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            propertyRentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Add group information if it's a color property
            String groupInfo = "Type: ";
            if (property.getName().contains("Railroad")) {
                groupInfo += "Railroad";
            } else if (property.getName().contains("Electric") || property.getName().contains("Water")) {
                groupInfo += "Utility";
            } else {
                // Check for color group
                for (Map.Entry<String, Color> entry : propertyColors.entrySet()) {
                    if (entry.getKey().equals(property.getName())) {
                        // Use the color name based on its RGB value
                        Color color = entry.getValue();
                        if (color.equals(Color.RED)) groupInfo += "Red Group";
                        else if (color.equals(Color.BLUE)) groupInfo += "Blue Group";
                        else if (color.equals(Color.GREEN) || color.equals(new Color(0, 100, 0))) 
                            groupInfo += "Green Group";
                        else if (color.equals(Color.YELLOW)) groupInfo += "Yellow Group";
                        else if (color.equals(new Color(128, 0, 128))) groupInfo += "Purple Group";
                        else if (color.equals(new Color(255, 160, 122))) groupInfo += "Orange Group";
                        else if (color.equals(new Color(255, 192, 203))) groupInfo += "Pink Group";
                        else if (color.equals(new Color(160, 82, 45))) groupInfo += "Brown Group";
                        break;
                    }
                }
            }
            
            propertyGroupLabel = new JLabel(groupInfo);
            propertyGroupLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            propertyGroupLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Player money
            playerMoneyLabel = new JLabel("Your money: $" + playerMoney);
            playerMoneyLabel.setFont(new Font("Arial", Font.BOLD, 14));
            playerMoneyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            propertyInfoPanel.add(colorPanel);
            propertyInfoPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            propertyInfoPanel.add(propertyPriceLabel);
            propertyInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            propertyInfoPanel.add(propertyRentLabel);
            propertyInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            propertyInfoPanel.add(propertyGroupLabel);
            propertyInfoPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            propertyInfoPanel.add(playerMoneyLabel);
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            
            buyButton = new JButton("Buy Property");
            buyButton.setFont(new Font("Arial", Font.BOLD, 14));
            
            // If player doesn't have enough money, disable the buy button
            if (playerMoney < property.getPurchasePrice()) {
                buyButton.setEnabled(false);
                buyButton.setText("Not Enough Money");
            }
            
            buyButton.addActionListener(e -> {
                wantsToBuy = true;
                dispose();
            });
            
            auctionButton = new JButton("Auction Property");
            auctionButton.setFont(new Font("Arial", Font.BOLD, 14));
            auctionButton.addActionListener(e -> {
                wantsToBuy = false;
                dispose();
            });
            
            buttonPanel.add(buyButton);
            buttonPanel.add(auctionButton);
            
            // Add panels to dialog
            add(propertyInfoPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
        /**
         * Show the dialog and return true if the player wants to buy, false otherwise.
         * 
         * IMPORTANT: This method should only be called from the Event Dispatch Thread,
         * typically using SwingUtilities.invokeAndWait() from another thread.
         */
        public boolean showDialog() {
            if (DEBUG) {
                System.out.println("DEBUG: PropertyPurchaseDialog.showDialog() - About to show dialog");
            }
            
            // Ensure we're on the EDT to avoid threading issues
            if (!SwingUtilities.isEventDispatchThread()) {
                System.err.println("WARNING: PropertyPurchaseDialog.showDialog() called from non-EDT thread!");
                System.err.println("This may cause thread safety issues. Call through SwingUtilities.invokeAndWait() instead.");
            }
            
            // Show the modal dialog - this will block until dialog is closed
            setVisible(true);
            
            if (DEBUG) {
                System.out.println("DEBUG: PropertyPurchaseDialog.showDialog() - Dialog closed, result: " + 
                                 (wantsToBuy ? "BUY" : "AUCTION"));
            }
            
            // When dialog closes, wantsToBuy will be set based on which button was clicked
            return wantsToBuy;
        }
    }
    
    /**
     * Show the property purchase dialog to prompt the player whether they want to buy a property.
     * This is the preferred method to show property purchase dialogs.
     * 
     * @param property The property to potentially purchase
     * @param playerMoney The amount of money the player has
     * @return true if the player wants to buy, false to auction
     */
    public boolean showPropertyPurchaseDialog(Property property, int playerMoney) {
        // Debug message to ensure we're reaching this method
        System.out.println("*** SHOWING PROPERTY PURCHASE DIALOG FOR: " + property.getName() + " ***");
        
        try {
            // Log important property information
            System.out.println("Property: " + property.getName() + 
                              " | Price: $" + property.getPurchasePrice() + 
                              " | Position: " + property.getPosition() + 
                              " | Player Money: $" + playerMoney);
            
            // Create and show the dialog on the event dispatch thread
            final boolean[] result = new boolean[1];
            
            // Force dialog to run on the event dispatch thread for thread safety
            javax.swing.SwingUtilities.invokeAndWait(() -> {
                try {
                    PropertyPurchaseDialog dialog = new PropertyPurchaseDialog(mainFrame, property, playerMoney);
                    result[0] = dialog.showDialog();
                } catch (Exception e) {
                    System.err.println("Error showing property dialog: " + e.getMessage());
                    e.printStackTrace();
                    result[0] = false; // Default to auction if there's an error
                }
            });
            
            // If the property was purchased, update the management tab
            if (result[0]) {
                // Update property management panel if a purchase happened
                SwingUtilities.invokeLater(() -> {
                    refreshPropertyManagementPanel(null);
                    updateGameStatistics(); // Update statistics after purchase
                });
            }
            
            System.out.println("Property purchase dialog result: " + (result[0] ? "BUY" : "AUCTION"));
            return result[0];
        } catch (Exception e) {
            System.err.println("ERROR in showPropertyPurchaseDialog: " + e.getMessage());
            e.printStackTrace();
            return false; // Default to auction if there's an error
        }
    }
    
    /**
     * Create the trade panel for the tabbed interface.
     * Team member(s) responsible: matt
     */
    private JPanel createTradePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add title
        JLabel titleLabel = new JLabel("Trade Properties and Money");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Create the main content panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        
        // Left side - Your assets
        JPanel yourAssetsPanel = new JPanel(new BorderLayout());
        yourAssetsPanel.setBorder(BorderFactory.createTitledBorder("Your Offer"));
        
        JPanel yourOfferPanel = new JPanel();
        yourOfferPanel.setLayout(new BoxLayout(yourOfferPanel, BoxLayout.Y_AXIS));
        
        JLabel yourMoneyLabel = new JLabel("Your money: $0");
        yourMoneyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel offerMoneyLabel = new JLabel("Money to offer:");
        offerMoneyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField offerMoneyField = new JTextField("0");
        offerMoneyField.setMaximumSize(new Dimension(200, 25));
        offerMoneyField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel yourPropertiesLabel = new JLabel("Your properties:");
        yourPropertiesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        DefaultListModel<String> yourPropertiesModel = new DefaultListModel<>();
        JList<String> yourPropertiesList = new JList<>(yourPropertiesModel);
        yourPropertiesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane yourPropertiesScroll = new JScrollPane(yourPropertiesList);
        yourPropertiesScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        yourPropertiesScroll.setPreferredSize(new Dimension(200, 200));
        
        yourOfferPanel.add(yourMoneyLabel);
        yourOfferPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        yourOfferPanel.add(offerMoneyLabel);
        yourOfferPanel.add(offerMoneyField);
        yourOfferPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        yourOfferPanel.add(yourPropertiesLabel);
        yourOfferPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        yourOfferPanel.add(yourPropertiesScroll);
        
        yourAssetsPanel.add(yourOfferPanel, BorderLayout.CENTER);
        
        // Center - Player selection and controls
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Trade With"));
        
        JLabel selectPlayerLabel = new JLabel("Select player:");
        selectPlayerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        String[] playerNames = {"Select a player", "Player 2", "Player 3", "Player 4"};
        JComboBox<String> playerSelector = new JComboBox<>(playerNames);
        playerSelector.setMaximumSize(new Dimension(200, 25));
        playerSelector.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton proposeTradeButton = new JButton("Propose Trade");
        proposeTradeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        proposeTradeButton.setMaximumSize(new Dimension(200, 30));
        
        JButton cancelTradeButton = new JButton("Cancel");
        cancelTradeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelTradeButton.setMaximumSize(new Dimension(200, 30));
        
        controlsPanel.add(Box.createVerticalGlue());
        controlsPanel.add(selectPlayerLabel);
        controlsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        controlsPanel.add(playerSelector);
        controlsPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        controlsPanel.add(proposeTradeButton);
        controlsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlsPanel.add(cancelTradeButton);
        controlsPanel.add(Box.createVerticalGlue());
        
        // Right side - Their assets
        JPanel theirAssetsPanel = new JPanel(new BorderLayout());
        theirAssetsPanel.setBorder(BorderFactory.createTitledBorder("Their Offer"));
        
        JPanel theirOfferPanel = new JPanel();
        theirOfferPanel.setLayout(new BoxLayout(theirOfferPanel, BoxLayout.Y_AXIS));
        
        JLabel theirMoneyLabel = new JLabel("Their money: $0");
        theirMoneyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel requestMoneyLabel = new JLabel("Money to request:");
        requestMoneyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField requestMoneyField = new JTextField("0");
        requestMoneyField.setMaximumSize(new Dimension(200, 25));
        requestMoneyField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel theirPropertiesLabel = new JLabel("Their properties:");
        theirPropertiesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        DefaultListModel<String> theirPropertiesModel = new DefaultListModel<>();
        JList<String> theirPropertiesList = new JList<>(theirPropertiesModel);
        theirPropertiesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane theirPropertiesScroll = new JScrollPane(theirPropertiesList);
        theirPropertiesScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        theirPropertiesScroll.setPreferredSize(new Dimension(200, 200));
        
        theirOfferPanel.add(theirMoneyLabel);
        theirOfferPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        theirOfferPanel.add(requestMoneyLabel);
        theirOfferPanel.add(requestMoneyField);
        theirOfferPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        theirOfferPanel.add(theirPropertiesLabel);
        theirOfferPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        theirOfferPanel.add(theirPropertiesScroll);
        
        theirAssetsPanel.add(theirOfferPanel, BorderLayout.CENTER);
        
        // Add panels to content panel
        contentPanel.add(yourAssetsPanel);
        contentPanel.add(controlsPanel);
        contentPanel.add(theirAssetsPanel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create the statistics panel for the tabbed interface.
     * Team member(s) responsible: matt
     */
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add title
        JLabel titleLabel = new JLabel("Game Statistics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Create the main content panel with two sections
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        
        // Top section - Player statistics
        JPanel playerStatsPanel = new JPanel(new BorderLayout());
        playerStatsPanel.setBorder(BorderFactory.createTitledBorder("Player Statistics"));
        
        String[] columnNames = {"Player", "Money", "Properties", "Total Worth", "Position"};
        playerStatsModel = new javax.swing.table.DefaultTableModel(columnNames, 0);
        
        // Add initial data for all players
        for (PlayerData player : players) {
            int totalWorth = player.money;
            
            // Add property values to total worth
            for (Integer propertyPos : player.ownedProperties) {
                if (positionToPrice.containsKey(propertyPos)) {
                    totalWorth += positionToPrice.get(propertyPos);
                }
            }
            
            Object[] row = {
                player.name,
                "$" + player.money,
                String.valueOf(player.ownedProperties.size()),
                "$" + totalWorth,
                positionToName.get(player.position) + " (" + player.position + ")"
            };
            playerStatsModel.addRow(row);
        }
        
        playerStatsTable = new JTable(playerStatsModel);
        playerStatsTable.setFillsViewportHeight(true);
        JScrollPane playerStatsScroll = new JScrollPane(playerStatsTable);
        playerStatsPanel.add(playerStatsScroll, BorderLayout.CENTER);
        
        // Bottom section - Game statistics
        JPanel gameStatsPanel = new JPanel();
        gameStatsPanel.setLayout(new BoxLayout(gameStatsPanel, BoxLayout.Y_AXIS));
        gameStatsPanel.setBorder(BorderFactory.createTitledBorder("Game Information"));
        
        // Initialize the labels with current stats values
        turnsLabel = new JLabel("Turns played: " + totalTurns);
        turnsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Calculate and format game time
        long currentTime = System.currentTimeMillis();
        long gameTimeMs = currentTime - gameStartTime;
        String formattedTime = formatGameTime(gameTimeMs);
        timeLabel = new JLabel("Game time: " + formattedTime);
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        diceRollsLabel = new JLabel("Total dice rolls: " + totalDiceRolls);
        diceRollsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        doublesLabel = new JLabel("Doubles rolled: " + totalDoubles);
        doublesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        goPassesLabel = new JLabel("Times passed GO: " + totalGoPasses);
        goPassesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        jailVisitsLabel = new JLabel("Jail visits: " + totalJailVisits);
        jailVisitsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Start a timer to update the game time every second
        javax.swing.Timer gameTimeTimer = new javax.swing.Timer(1000, e -> updateGameTime());
        gameTimeTimer.setRepeats(true);
        gameTimeTimer.start();
        
        gameStatsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        gameStatsPanel.add(turnsLabel);
        gameStatsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        gameStatsPanel.add(timeLabel);
        gameStatsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        gameStatsPanel.add(diceRollsLabel);
        gameStatsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        gameStatsPanel.add(doublesLabel);
        gameStatsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        gameStatsPanel.add(goPassesLabel);
        gameStatsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        gameStatsPanel.add(jailVisitsLabel);
        gameStatsPanel.add(Box.createVerticalGlue());
        
        // Add panels to content panel
        contentPanel.add(playerStatsPanel);
        contentPanel.add(gameStatsPanel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Update the property panel with current property information.
     * Team member(s) responsible: Matt
     */
    private void updatePropertyPanel() {
        // Clear existing property info
        propertyPanel.removeAll();

        // Get current player's properties
        PlayerData currentPlayer = players.get(currentPlayerIndex);

        JLabel titleLabel = new JLabel(currentPlayer.name + "'s Properties");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        propertyPanel.add(titleLabel);
        propertyPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Add properties
        if (currentPlayer.ownedProperties.isEmpty()) {
            JLabel noneLabel = new JLabel("No properties owned");
            noneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            propertyPanel.add(noneLabel);
        } else {
            JPanel propertiesPanel = new JPanel();
            propertiesPanel.setLayout(new GridLayout(0, 1));

            for (Integer position : currentPlayer.ownedProperties) {
                String propertyName = positionToName.get(position);
                Color propertyColor = propertyColors.containsKey(propertyName) ?
                        propertyColors.get(propertyName) : Color.WHITE;

                JPanel propertyItem = new JPanel(new BorderLayout());
                propertyItem.setBorder(BorderFactory.createLineBorder(propertyColor, 2));

                JLabel propertyLabel = new JLabel(propertyName);
                propertyLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                propertyItem.add(propertyLabel, BorderLayout.CENTER);

                JLabel priceLabel = new JLabel("$" + positionToPrice.get(position));
                priceLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                propertyItem.add(priceLabel, BorderLayout.EAST);

                propertiesPanel.add(propertyItem);
            }

            JScrollPane scrollPane = new JScrollPane(propertiesPanel);
            scrollPane.setPreferredSize(new Dimension(180, 100));
            scrollPane.setBorder(null);
            propertyPanel.add(scrollPane);
        }

        propertyPanel.revalidate();
        propertyPanel.repaint();
    }

    /**
     * Handle actions when a player lands on a property.
     * Team member(s) responsible: Matt
     */
    /**
     * Helper method to format game time in minutes:seconds
     * 
     * @param milliseconds The elapsed time in milliseconds
     * @return Formatted time string (MM:SS)
     */
    private String formatGameTime(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    /**
     * Update the game time display
     */
    private void updateGameTime() {
        long currentTime = System.currentTimeMillis();
        long gameTimeMs = currentTime - gameStartTime;
        String formattedTime = formatGameTime(gameTimeMs);
        
        if (timeLabel != null) {
            timeLabel.setText("Game time: " + formattedTime);
        }
    }
    
    /**
     * Update the game statistics
     */
    private void updateGameStatistics() {
        // Update statistics labels if they exist
        if (turnsLabel != null) {
            turnsLabel.setText("Turns played: " + totalTurns);
        }
        
        if (diceRollsLabel != null) {
            diceRollsLabel.setText("Total dice rolls: " + totalDiceRolls);
        }
        
        if (doublesLabel != null) {
            doublesLabel.setText("Doubles rolled: " + totalDoubles);
        }
        
        if (goPassesLabel != null) {
            goPassesLabel.setText("Times passed GO: " + totalGoPasses);
        }
        
        if (jailVisitsLabel != null) {
            jailVisitsLabel.setText("Jail visits: " + totalJailVisits);
        }
        
        // Update player statistics table
        if (playerStatsModel != null && players != null) {
            // Clear existing rows
            while (playerStatsModel.getRowCount() > 0) {
                playerStatsModel.removeRow(0);
            }
            
            // Add updated rows
            for (PlayerData player : players) {
                int totalWorth = player.money;
                int propertyCount = player.ownedProperties.size();
                int housesCount = 0;
                int hotelsCount = 0;
                
                // Calculate property worth including houses and hotels if available
                for (Integer propertyPos : player.ownedProperties) {
                    if (positionToPrice.containsKey(propertyPos)) {
                        int propertyPrice = positionToPrice.get(propertyPos);
                        totalWorth += propertyPrice;
                        
                        // Add value of houses and hotels (if we're tracking them)
                        String propertyName = positionToName.get(propertyPos);
                        // Only count houses/hotels for standard properties, not railroads or utilities
                        if (propertyName != null && 
                            !propertyName.contains("Railroad") && 
                            !propertyName.contains("Electric") && 
                            !propertyName.contains("Water")) {
                            
                            // Try to check if property has houses or hotel - for future implementation
                            // This is a placeholder for actual house tracking in the game model
                        }
                    }
                }
                
                Object[] row = {
                    player.name,
                    "$" + player.money,
                    propertyCount + " props",
                    "$" + totalWorth,
                    positionToName.get(player.position) + " (" + player.position + ")"
                };
                playerStatsModel.addRow(row);
            }
        }
        
        // Force repaint of the statistics panel if it's currently visible
        if (playerStatsTable != null) {
            playerStatsTable.repaint();
        }
    }
    
    private void handleLandOnProperty(PlayerData player) {
        int position = player.position;
        String spaceName = positionToName.get(position);

        // Handle special spaces
        if (position == 0) {
            // GO
            player.money += 200;
            JOptionPane.showMessageDialog(mainFrame,
                    player.name + " passed GO and collected $200!",
                    "GO Space", JOptionPane.INFORMATION_MESSAGE);
        } else if (position == 4 || position == 38) {
            // Tax spaces
            if (position == 4) {  // Income Tax
                // Calculate 10% of player's money
                int percentageTax = (int) (player.money * 0.1);
                int flatTax = 200;
                
                // Let the player choose between flat tax or percentage
                String[] options = {"Pay $200 (flat tax)", "Pay $" + percentageTax + " (10% of assets)"};
                int choice = JOptionPane.showOptionDialog(mainFrame,
                        player.name + " landed on Income Tax.\nChoose how to pay:",
                        "Income Tax", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, options, options[0]);
                
                // Apply the chosen tax (default to flat tax if dialog is closed)
                int taxAmount = (choice == 1) ? percentageTax : flatTax;
                player.money -= taxAmount;
                
                JOptionPane.showMessageDialog(mainFrame,
                        player.name + " paid $" + taxAmount + " in Income Tax!",
                        "Income Tax", JOptionPane.INFORMATION_MESSAGE);
            } else {  // Luxury Tax
                int taxAmount = 75;
                player.money -= taxAmount;
                JOptionPane.showMessageDialog(mainFrame,
                        player.name + " paid $" + taxAmount + " in Luxury Tax!",
                        "Luxury Tax", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (position == 30) {
            // Go to jail
            player.position = 10;
            totalJailVisits++;
            updateGameStatistics();
            JOptionPane.showMessageDialog(mainFrame,
                    player.name + " went to jail!",
                    "Go To Jail", JOptionPane.INFORMATION_MESSAGE);
        } else if (position == 2 || position == 17 || position == 33) {
            // Community Chest
            JOptionPane.showMessageDialog(mainFrame,
                    "Community Chest: Draw a card!",
                    "Community Chest", JOptionPane.INFORMATION_MESSAGE);
        } else if (position == 7 || position == 22 || position == 36) {
            // Chance
            JOptionPane.showMessageDialog(mainFrame,
                    "Chance: Draw a card!",
                    "Chance", JOptionPane.INFORMATION_MESSAGE);
        } else if (positionToPrice.containsKey(position)) {
            // Check if property is owned
            for (PlayerData otherPlayer : players) {
                if (otherPlayer != player && otherPlayer.ownedProperties.contains(position)) {
                    // Pay rent
                    int rent = calculateRent(position, otherPlayer);
                    player.money -= rent;
                    otherPlayer.money += rent;
                    JOptionPane.showMessageDialog(mainFrame,
                            player.name + " paid $" + rent + " rent to " + otherPlayer.name + "!",
                            "Pay Rent", JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
            }
        }
    }

    /**
     * Calculate rent for a property based on ownership.
     * Team member(s) responsible: Matt
     */
    private int calculateRent(int position, PlayerData owner) {
        String propertyName = positionToName.get(position);
        int baseRent = positionToPrice.get(position) / 10;

        // Railroad rent calculation
        if (propertyName.contains("Railroad")) {
            int railroadCount = 0;
            for (Integer pos : owner.ownedProperties) {
                if (positionToName.get(pos).contains("Railroad")) {
                    railroadCount++;
                }
            }
            return 25 * (int)Math.pow(2, railroadCount - 1);
        }

        // Utility rent calculation
        if (propertyName.contains("Electric") || propertyName.contains("Water")) {
            int utilityCount = 0;
            for (Integer pos : owner.ownedProperties) {
                if (positionToName.get(pos).contains("Electric") ||
                        positionToName.get(pos).contains("Water")) {
                    utilityCount++;
                }
            }
            int multiplier = utilityCount == 1 ? 4 : 10;
            return multiplier * (dice1Value + dice2Value);
        }

        return baseRent;
    }
    
    /**
     * Calculate rent for a property with houses.
     * Team member(s) responsible: matt
     * 
     * @param propertyPrice The base purchase price of the property
     * @param houses The number of houses on the property (0-4)
     * @return The rent amount
     */
    private int calculateRentWithHouses(int propertyPrice, int houses) {
        int baseRent = propertyPrice / 10;
        
        // Increase rent based on number of houses
        switch (houses) {
            case 0:
                return baseRent;
            case 1:
                return baseRent * 5;    // 5 times the base rent
            case 2:
                return baseRent * 15;   // 15 times the base rent
            case 3:
                return baseRent * 30;   // 30 times the base rent
            case 4:
                return baseRent * 40;   // 40 times the base rent
            default:
                return baseRent;
        }
    }
    
    /**
     * Calculate rent for a property with a hotel.
     * Team member(s) responsible: matt
     * 
     * @param propertyPrice The base purchase price of the property
     * @return The rent amount
     */
    private int calculateRentWithHotel(int propertyPrice) {
        int baseRent = propertyPrice / 10;
        return baseRent * 50;  // 50 times the base rent
    }

    /**
     * Helper method to get coordinates for a player token based on their position.
     * Team member(s) responsible: Matt
     */
    private int[] getCoordinatesForPosition(int position, int playerOffset) {
        int[] coordinates = new int[2];

        // CRITICAL FIX: Extensive logging for coordinate calculation
        System.out.println("===> CALCULATING COORDINATES FOR POSITION: " + position);

        // Handle out-of-bounds positions
        if (position < 0 || position > 39) {
            System.out.println("SEVERE WARNING: Invalid position " + position + ", clamping to valid range!");
            position = Math.max(0, Math.min(position, 39));
        }

        // Calculate the base coordinates for each position - completely rewritten for clarity and correctness
        if (position >= 0 && position < 10) {
            // Bottom row (right to left)
            if (position == 0) {
                // GO position (bottom right corner) - special case
                coordinates[0] = BOARD_SIZE - SPACE_HEIGHT/2 - 10;
                coordinates[1] = BOARD_SIZE - SPACE_WIDTH/2 - 10;
                System.out.println("POSITION 0 (GO): Using special corner coordinates");
            } else {
                // Positions 1-9: Other bottom row positions from right to left
                int offsetFromRight = position;
                coordinates[0] = BOARD_SIZE - SPACE_HEIGHT - offsetFromRight * SPACE_HEIGHT + SPACE_HEIGHT/2 - 10;
                coordinates[1] = BOARD_SIZE - SPACE_WIDTH/2 - 10;
                System.out.println("BOTTOM ROW POSITION " + position + ": " + offsetFromRight + " spaces from right");
            }
        } else if (position >= 10 && position < 20) {
            // Left column (bottom to top)
            // Left column (bottom to top) - positions 10-19
            int offsetFromBottom = position - 10;
            coordinates[0] = SPACE_HEIGHT/2 - 10;
            coordinates[1] = BOARD_SIZE - SPACE_WIDTH - offsetFromBottom * SPACE_WIDTH - SPACE_WIDTH/2 - 10;
            System.out.println("LEFT COLUMN POSITION " + position + ": " + offsetFromBottom + " spaces from bottom");
        } else if (position >= 20 && position < 30) {
            // Top row (left to right) - positions 20-29
            int offsetFromLeft = position - 20;
            coordinates[0] = SPACE_HEIGHT + offsetFromLeft * SPACE_HEIGHT + SPACE_HEIGHT/2 - 10;
            coordinates[1] = SPACE_WIDTH/2 - 10;
            System.out.println("TOP ROW POSITION " + position + ": " + offsetFromLeft + " spaces from left");
        } else {
            // Right column (top to bottom) - positions 30-39
            int offsetFromTop = position - 30;
            coordinates[0] = BOARD_SIZE - SPACE_HEIGHT/2 - 10;
            coordinates[1] = SPACE_WIDTH + offsetFromTop * SPACE_WIDTH + SPACE_WIDTH/2 - 10;
            System.out.println("RIGHT COLUMN POSITION " + position + ": " + offsetFromTop + " spaces from top");
        }
        
        // Log the calculated coordinates with clear markers
        System.out.println("===> POSITION " + position + " MAPPED TO COORDINATES: (" + 
                            coordinates[0] + ", " + coordinates[1] + ")" + 
                            " with player offset " + playerOffset);

        // Add small offset based on player number to prevent overlap
        coordinates[0] += (playerOffset % 2) * 15;
        coordinates[1] += (playerOffset / 2) * 15;

        return coordinates;
    }

    /**
     * Refreshes the property management panel with the current player's properties.
     * This should be called whenever the tab is selected or a property is bought/sold.
     */
    private void refreshPropertyManagementPanel(DefaultListModel<String> propertiesModel) {
        if (propertiesModel == null && propertiesList != null) {
            propertiesModel = (DefaultListModel<String>) propertiesList.getModel();
        }
        
        if (propertiesModel != null) {
            // Clear existing properties
            propertiesModel.clear();
            
            // Add current player's properties to the model
            if (currentPlayerIndex < players.size()) {
                PlayerData currentPlayer = players.get(currentPlayerIndex);
                for (Integer propertyPos : currentPlayer.ownedProperties) {
                    String propertyName = positionToName.get(propertyPos);
                    
                    // For simplicity, initialize properties as not mortgaged if they're not already tracked
                    if (!mortgagedProperties.containsKey(propertyPos)) {
                        mortgagedProperties.put(propertyPos, false);
                    }
                    
                    // Add property to list model
                    propertiesModel.addElement(propertyName);
                }
            }
            
            // If the JList exists, make sure it's updated
            if (propertiesList != null) {
                propertiesList.repaint();
                
                // Reset property details if no properties are selected
                if (propertiesList.getSelectedIndex() == -1) {
                    // Reset property details to default values
                    if (nameLabel != null) nameLabel.setText("Select a property");
                    if (priceLabel != null) priceLabel.setText("Purchase price: $0");
                    if (rentLabel != null) rentLabel.setText("Current rent: $0");
                    if (housesLabel != null) housesLabel.setText("Houses: 0");
                    if (hotelLabel != null) hotelLabel.setText("Hotel: No");
                    if (mortgageLabel != null) mortgageLabel.setText("Mortgage value: $0");
                    
                    // Disable action buttons
                    if (buyHouseButton != null) buyHouseButton.setEnabled(false);
                    if (sellHouseButton != null) sellHouseButton.setEnabled(false);
                    if (mortgageButton != null) mortgageButton.setEnabled(false);
                    if (unmortgageButton != null) unmortgageButton.setEnabled(false);
                }
            }
        }
    }
    
    /**
     * Helper method to get position from mouse coordinates.
     * Team member(s) responsible: Matt
     */
    private int getPositionFromCoordinates(int x, int y) {
        // Corner spaces
        if (x >= BOARD_SIZE - SPACE_HEIGHT && y >= BOARD_SIZE - SPACE_WIDTH) {
            return 0; // GO
        } else if (x <= SPACE_HEIGHT && y >= BOARD_SIZE - SPACE_WIDTH) {
            return 10; // Jail
        } else if (x <= SPACE_HEIGHT && y <= SPACE_WIDTH) {
            return 20; // Free Parking
        } else if (x >= BOARD_SIZE - SPACE_HEIGHT && y <= SPACE_WIDTH) {
            return 30; // Go To Jail
        }

        // Bottom row
        if (y >= BOARD_SIZE - SPACE_WIDTH) {
            // Convert x coordinate to board position (1-9)
            // Note: The positions on the bottom row go from right to left
            int spaceIndex = (BOARD_SIZE - SPACE_HEIGHT - x) / SPACE_HEIGHT;
            if (spaceIndex >= 0 && spaceIndex < 9) {
                return spaceIndex + 1; // Positions 1-9
            }
        }

        // Left column
        if (x <= SPACE_HEIGHT) {
            int spaceY = BOARD_SIZE - SPACE_WIDTH - (int)(y / SPACE_WIDTH) * SPACE_WIDTH;
            if (spaceY > 0 && spaceY < BOARD_SIZE - SPACE_WIDTH) {
                return 19 - (spaceY / SPACE_WIDTH);
            }
        }

        // Top row
        if (y <= SPACE_WIDTH) {
            int spaceX = (int)(x / SPACE_HEIGHT) * SPACE_HEIGHT;
            if (spaceX > 0 && spaceX < BOARD_SIZE - SPACE_HEIGHT) {
                return 20 + (spaceX / SPACE_HEIGHT);
            }
        }

        // Right column
        if (x >= BOARD_SIZE - SPACE_HEIGHT) {
            int spaceY = (int)(y / SPACE_WIDTH) * SPACE_WIDTH;
            if (spaceY > 0 && spaceY < BOARD_SIZE - SPACE_WIDTH) {
                return 30 + (spaceY / SPACE_WIDTH);
            }
        }

        return -1; // Not on a space
    }

}






