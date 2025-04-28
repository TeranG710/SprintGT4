/*
 * CSCI 234: Intro to Software Engineering
 * Group: Giovanny, Jamell, Matt, Deborah
 * Purpose: This class will be responsible for controlling the board
 * Team Member(s) responsible: Jamell, Matt
 * */

package Controller;

import Model.Board.*;
import Model.Exceptions.InsufficientFundsException;
import Model.Exceptions.PlayerNotFoundException;
import Model.Game;
import Model.Property.Property;
import Model.Spaces.BoardSpace;
import View.Gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller class for the main game board.
 * This class integrates the GUI with the model and other controllers.
 * Team member(s) responsible: Matt
 */
public class BoardController {
    private Gui gui;
    private Game game;
    private GameBoard gameBoard;
    private Banker banker;
    private TurnManager turnManager;
    private Dice dice;
    private ArrayList<Player> players;
    private Map<ComputerPlayer, CpuController> cpuControllers;
    private AuctionController auctionController;
    private Map<String, ImageIcon> tokenImages;
    private Map<String, ImageIcon> cardImages;
    
    private Player currentPlayer;
    private boolean gameInProgress;
    private int doubleCount;
    
    /**
     * Constructor for BoardController.
     */
    public BoardController() {
        this.game = Game.getInstance();
        this.gameBoard = game.getBoard();
        this.banker = Banker.getInstance();
        this.turnManager = null;
        this.dice = Dice.getInstance();
        this.players = new ArrayList<>();
        this.cpuControllers = new HashMap<>();
        this.auctionController = new AuctionController();
        this.tokenImages = new HashMap<>();
        this.cardImages = new HashMap<>();
        this.gameInProgress = false;
        this.doubleCount = 0;
        loadTokenImages();
        loadCardImages();
    }
    
    /**
     * Initialize the GUI.
     */
    public void initializeGui() {
        SwingUtilities.invokeLater(() -> {
            gui = new Gui();
            gui.setController(this);
            setupActionListeners();
        });
    }
    
    /**
     * Set up action listeners for GUI components.
     */
    private void setupActionListeners() {
        if (gui == null) return;
        gui.addRollDiceListener(event -> {
            if (gameInProgress && currentPlayer != null) {
                int[] diceValues = rollDice();
                gui.updateDice(diceValues[0], diceValues[1]);
                gui.displayMessage(currentPlayer.getName() + " rolled a " + 
                                  (diceValues[0] + diceValues[1]) + " (" + 
                                  diceValues[0] + " and " + diceValues[1] + ")");
            } else {
                JOptionPane.showMessageDialog(gui.getMainFrame(),
                        "Game is not in progress or no current player.",
                        "Cannot Roll Dice", JOptionPane.WARNING_MESSAGE);
            }
        });
        gui.addEndTurnListener(event -> {
            if (gameInProgress) {
                endTurn();
            } else {
                JOptionPane.showMessageDialog(gui.getMainFrame(),
                        "Game is not in progress.",
                        "Cannot End Turn", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        gui.addNewGameListener(event -> {
            JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
            
            panel.add(new JLabel("Human Players (1-4):"));
            JSpinner humanSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 4, 1));
            panel.add(humanSpinner);
            
            panel.add(new JLabel("Computer Players (0-3):"));
            JSpinner cpuSpinner = new JSpinner(new SpinnerNumberModel(2, 0, 3, 1));
            panel.add(cpuSpinner);
            
            int result = JOptionPane.showConfirmDialog(gui.getMainFrame(), panel, 
                    "New Game", JOptionPane.OK_CANCEL_OPTION);
            
            if (result == JOptionPane.OK_OPTION) {
                int humanCount = (Integer) humanSpinner.getValue();
                int cpuCount = (Integer) cpuSpinner.getValue();
                ArrayList<String> humanNames = new ArrayList<>();
                for (int i = 0; i < humanCount; i++) {
                    String name = JOptionPane.showInputDialog(gui.getMainFrame(), 
                            "Enter name for Human Player " + (i + 1) + ":", 
                            "Player " + (i + 1));
                    
                    if (name == null || name.trim().isEmpty()) {
                        name = "Player " + (i + 1);
                    }
                    
                    humanNames.add(name);
                }
                startGame(humanNames, cpuCount);
            }
        });
    }
    
    /**
     * Load token images for players.
     */
    private void loadTokenImages() {
        String[] tokenNames = {"hat", "car", "dog", "shoe", "ship", "thimble", "wheelbarrow", "iron", "battleship", "boot"};
        Color[] tokenColors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, 
                              Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.PINK,
                              Color.DARK_GRAY, new Color(101, 67, 33)};
        File tokenDir = new File("resources/tokens");
        if (!tokenDir.exists()) {
            tokenDir.mkdirs();
            System.out.println("Created tokens directory");
        }
        for (String tokenName : tokenNames) {
            boolean imageLoaded = false;
            String[] filenameVariations = {
                tokenName + ".png",
                tokenName.toLowerCase() + ".png",
                tokenName.toUpperCase() + ".png",
                tokenName.equals("ship") ? "battleship.png" : null
            };
            
            for (String filename : filenameVariations) {
                if (filename == null) continue;
                
                File rootTokenFile = new File(filename);
                if (rootTokenFile.exists()) {
                    try {
                        BufferedImage image = ImageIO.read(rootTokenFile);
                        BufferedImage resizedImage = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2 = resizedImage.createGraphics();
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g2.drawImage(image, 0, 0, 40, 40, null);
                        g2.dispose();
                        
                        tokenImages.put(tokenName, new ImageIcon(resizedImage));
                        System.out.println("Loaded token image from root directory: " + rootTokenFile.getAbsolutePath());
                        imageLoaded = true;
                        break;
                    } catch (IOException e) {
                        System.err.println("Error loading token image from root: " + rootTokenFile.getAbsolutePath());
                    }
                }
            }
            if (imageLoaded) continue;
            String tokenPath = "resources/tokens/" + tokenName + ".png";
            File tokenFile = new File(tokenPath);
            
            if (tokenFile.exists()) {
                try {
                    BufferedImage image = ImageIO.read(tokenFile);
                    BufferedImage resizedImage = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2 = resizedImage.createGraphics();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2.drawImage(image, 0, 0, 40, 40, null);
                    g2.dispose();
                    
                    tokenImages.put(tokenName, new ImageIcon(resizedImage));
                    System.out.println("Loaded token image from resources: " + tokenPath);
                    continue;
                } catch (IOException e) {
                    System.err.println("Error loading token image from resources: " + tokenPath);
                }
            }
            
            int index = Arrays.asList(tokenNames).indexOf(tokenName);
            Color tokenColor = (index >= 0) ? tokenColors[index] : Color.GRAY;
            int size = 40;
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setComposite(AlphaComposite.Clear);
            g2.fillRect(0, 0, size, size);
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setColor(tokenColor);
            g2.fillOval(4, 4, size - 8, size - 8);
            

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(4, 4, size - 8, size - 8);
            
            // Add the first letter of the token name
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics fm = g2.getFontMetrics();
            String letter = tokenName.substring(0, 1).toUpperCase();
            int letterWidth = fm.stringWidth(letter);
            int letterHeight = fm.getAscent();
            g2.drawString(letter, (size - letterWidth) / 2, (size + letterHeight) / 2 - 2);
            
            g2.dispose();
            
            try {
                // Save the image to resources directory
                tokenFile = new File(tokenPath);
                ImageIO.write(image, "PNG", tokenFile);
                System.out.println("Created default token image: " + tokenPath);
                
                // Add to memory cache
                tokenImages.put(tokenName, new ImageIcon(image));
            } catch (IOException e) {
                System.err.println("Error creating default token image: " + tokenName);
                tokenImages.put(tokenName, createTokenIcon(tokenColor));
            }
        }
        
        System.out.println("Token loading complete. Images available: " + tokenImages.keySet());
    }
    
    /**
     * Create a token icon with the specified color.
     * 
     * @param color The color for the token
     * @return An ImageIcon representing the token
     */
    private ImageIcon createTokenIcon(Color color) {
        // Create a simple colored circle image
        int size = 30;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillOval(2, 2, size - 4, size - 4);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(2, 2, size - 4, size - 4);
        g2.dispose();
        
        return new ImageIcon(image);
    }
    
    
    /**
     * Load card images for Chance and Community Chest.
     */
    private void loadCardImages() {
        // Define card types and colors
        String[] cardTypes = {"chance", "community_chest"};
        String[] cardLabels = {"CHANCE", "COMMUNITY CHEST"};
        Color[] cardColors = {new Color(255, 222, 173), new Color(230, 230, 250)};
        
        // Check if card directory exists
        File cardDir = new File("resources/cards");
        if (!cardDir.exists()) {
            cardDir.mkdirs();
            System.out.println("Created cards directory");
        }
        
        // Create card files if they don't exist yet
        boolean allImagesCreated = true;
        for (int i = 0; i < cardTypes.length; i++) {
            String cardPath = "resources/cards/" + cardTypes[i] + ".png";
            File cardFile = new File(cardPath);
            
            if (!cardFile.exists()) {
                // Create a more detailed card image
                try {
                    int width = 100;
                    int height = 150;
                    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2 = image.createGraphics();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    if (cardTypes[i].equals("chance")) {
                        drawChanceCard(g2, width, height);
                    } else {
                        drawCommunityChestCard(g2, width, height);
                    }
                    
                    g2.dispose();
                    
                    // Save the image
                    ImageIO.write(image, "PNG", cardFile);
                    System.out.println("Created card image: " + cardPath);
                } catch (IOException e) {
                    System.err.println("Error creating card image: " + cardPath);
                    allImagesCreated = false;
                }
            }
        }
        
        // Now load all card images from the files
        for (int i = 0; i < cardTypes.length; i++) {
            String cardPath = "resources/cards/" + cardTypes[i] + ".png";
            File cardFile = new File(cardPath);
            
            try {
                if (cardFile.exists()) {
                    BufferedImage image = ImageIO.read(cardFile);
                    cardImages.put(cardTypes[i], new ImageIcon(image));
                    System.out.println("Loaded card image: " + cardPath);
                } else {
                    throw new IOException("Card file doesn't exist");
                }
            } catch (IOException e) {
                System.err.println("Error loading card image: " + cardPath);
                // Fall back to generated icon
                cardImages.put(cardTypes[i], createCardIcon(cardColors[i], cardLabels[i]));
            }
        }
        
        if (allImagesCreated) {
            System.out.println("All card images were successfully created or loaded");
        }
    }
    
    /**
     * Create a card icon with the specified color and text.
     * 
     * @param color The background color for the card
     * @param text The text to display on the card
     * @return An ImageIcon representing the card
     */
    private ImageIcon createCardIcon(Color color, String text) {
        // Create a simple colored rectangle with text
        int width = 80;
        int height = 120;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillRect(0, 0, width, height);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(0, 0, width - 1, height - 1);
        
        // Add text
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g2.drawString(text, (width - textWidth) / 2, height / 2);
        g2.dispose();
        
        return new ImageIcon(image);
    }
    
    /**
     * Draw a detailed Chance card
     * 
     * @param g2 Graphics context
     * @param width Card width
     * @param height Card height
     */
    private void drawChanceCard(Graphics2D g2, int width, int height) {
        // Card background - orange/yellow
        g2.setColor(new Color(255, 222, 173));
        g2.fillRoundRect(0, 0, width, height, 10, 10);
        
        // Card border
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(2, 2, width - 4, height - 4, 10, 10);
        
        // Draw "CHANCE" text at top
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g2.getFontMetrics();
        String title = "CHANCE";
        int textWidth = fm.stringWidth(title);
        g2.drawString(title, (width - textWidth) / 2, 25);
        
        // Draw a large question mark in the center
        g2.setFont(new Font("Arial", Font.BOLD, 60));
        fm = g2.getFontMetrics();
        String symbol = "?";
        textWidth = fm.stringWidth(symbol);
        g2.drawString(symbol, (width - textWidth) / 2, height / 2 + 15);
        
        // Draw decorative border inside
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(10, 10, width - 20, height - 20, 5, 5);
    }
    
    /**
     * Draw a detailed Community Chest card
     * 
     * @param g2 Graphics context
     * @param width Card width
     * @param height Card height
     */
    private void drawCommunityChestCard(Graphics2D g2, int width, int height) {
        // Card background - light blue
        g2.setColor(new Color(230, 230, 250));
        g2.fillRoundRect(0, 0, width, height, 10, 10);
        
        // Card border
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(2, 2, width - 4, height - 4, 10, 10);
        
        // Draw "COMMUNITY CHEST" text at top (on two lines)
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2.getFontMetrics();
        String title1 = "COMMUNITY";
        String title2 = "CHEST";
        int textWidth = fm.stringWidth(title1);
        g2.drawString(title1, (width - textWidth) / 2, 25);
        textWidth = fm.stringWidth(title2);
        g2.drawString(title2, (width - textWidth) / 2, 45);
        
        // Draw a treasure chest icon
        g2.setColor(new Color(139, 69, 19)); // Brown
        g2.fillRect(width/2 - 20, height/2 - 10, 40, 30);
        g2.setColor(new Color(255, 215, 0)); // Gold
        g2.fillRect(width/2 - 18, height/2 - 8, 36, 10);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(width/2 - 20, height/2 - 10, 40, 30);
        
        // Draw decorative border inside
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(10, 10, width - 20, height - 20, 5, 5);
    }
    
    /**
     * Ensure all players are at position 0 (GO).
     * This is an emergency fix to prevent players from starting at wrong positions.
     */
    private void ensureAllPlayersAtGo() {
        System.out.println("Setting up player positions at GO");
        
        for (Player player : players) {
            // Set position to GO (0)
            player.setPosition(0);
            System.out.println("Setting " + player.getName() + " to GO (position 0)");
            
            // Update GUI if available
            if (gui != null) {
                gui.updatePlayerPosition(player, 0);
            }
        }
    }
    
    /**
     * Start a new game with the specified players.
     * 
     * @param playerNames List of human player names
     * @param computerPlayerCount Number of computer players to add
     */
    public void startGame(ArrayList<String> playerNames, int computerPlayerCount) {
        // Reset the game state
        Game.resetInstance();
        Banker.reset();
        GameBoard.resetInstance();
        this.game = Game.getInstance();
        this.gameBoard = game.getBoard();
        this.banker = Banker.getInstance();
        
        // Initialize banker's properties
        banker.initializeProperties();
        
        // Clear our local tracking
        players.clear();
        cpuControllers.clear();
        
        try {
            // Assign token names for players (both human and computer)
            String[] tokenNames = {"hat", "car", "dog", "shoe", "ship", "thimble", "wheelbarrow", "iron"};
            int tokenIndex = 0;
            
            // Add human players to the game
            for (String name : playerNames) {
                HumanPlayer player = new HumanPlayer(name, gameBoard);
                
                // Assign a token to the player
                if (tokenIndex < tokenNames.length) {
                    player.setTokenName(tokenNames[tokenIndex++]);
                }
                
                // Explicitly set starting position to GO (0)
                player.setPosition(0);
                System.out.println("*** SETTING " + player.getName() + " TO POSITION 0 (GO) ***");
                
                // Force position update in the GUI
                if (gui != null) {
                    gui.updatePlayerPosition(player, 0);
                }
                
                game.addPlayer(player);
                players.add(player);
            }
            
            // Add computer players to the game
            for (int i = 0; i < computerPlayerCount; i++) {
                ComputerPlayer cpu = new ComputerPlayer("CPU " + (i + 1), gameBoard);
                
                // Assign a token to the CPU player
                if (tokenIndex < tokenNames.length) {
                    cpu.setTokenName(tokenNames[tokenIndex++]);
                }
                
                // Explicitly set starting position to GO (0)
                cpu.setPosition(0);
                System.out.println("*** SETTING " + cpu.getName() + " TO POSITION 0 (GO) ***");
                
                // Force position update in the GUI
                if (gui != null) {
                    gui.updatePlayerPosition(cpu, 0);
                }
                
                game.addPlayer(cpu);
                players.add(cpu);
                
                // Create controller for this CPU
                CpuController cpuController = new CpuController(cpu);
                cpuControllers.put(cpu, cpuController);
            }
            
            // EMERGENCY FIX: Ensure all players start at position 0 (GO)
            ensureAllPlayersAtGo();
            
            // Start the game in the model
            game.startGame();
            
            // Set up turn manager
            turnManager = new TurnManager(players);
            currentPlayer = turnManager.getCurrentPlayer();
            
            // Initialize controller game state
            gameInProgress = true;
            doubleCount = 0;
            
            // Initialize the GUI with the new game state
            if (gui != null) {
                gui.initializeGame(players);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                    "Error starting game: " + e.getMessage(), 
                    "Game Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Roll the dice and move the current player.
     * 
     * @return An array containing the two dice values
     */
    public int[] rollDice() {
        if (!game.gameInProgress() || currentPlayer == null) {
            return new int[]{1, 1}; // Default values if game not in progress
        }
        
        // CRITICAL FIX: Check initial player position before rolling
        int startingPosition = currentPlayer.getPosition();
        System.out.println("********************************************");
        System.out.println("POSITION CHECK: " + currentPlayer.getName() + 
                         " is at position " + startingPosition + " before rolling");
        
        // Reset problematic positions
        if (startingPosition >= 40 || startingPosition < 0) {
            System.out.println("SEVERE ERROR: " + currentPlayer.getName() + 
                             " has invalid position " + startingPosition + " - resetting to GO");
            currentPlayer.setPosition(0);
            startingPosition = 0;
            
            // Update GUI if available
            if (gui != null) {
                gui.updatePlayerPosition(currentPlayer, 0);
            }
        }
        
        // Roll the dice
        dice.roll();
        int[] diceValues = {dice.getDie1(), dice.getDie2()};
        boolean isDoubles = dice.isDouble();
        int totalSpaces = diceValues[0] + diceValues[1]; // Use actual dice values
        
        // For debug inspection
        System.out.println("*** DICE ROLLS: " + diceValues[0] + " and " + diceValues[1]);
        System.out.println("*** TOTAL: " + totalSpaces + " spaces");
        
        // Move the player
        try {
            // CRITICAL FIX: ABSOLUTE CONTROL over position calculation
            int newPosition = (startingPosition + totalSpaces) % 40;
            System.out.println("POSITION CALCULATION: " + startingPosition + 
                             " + " + totalSpaces + " = " + newPosition);
            
            // BYPASS all other movement methods which might be causing issues
            // The Model's move method might have side effects - don't use it
            
            // Set position DIRECTLY - this is the authoritative position
            currentPlayer.setPosition(newPosition);
            System.out.println("======> " + currentPlayer.getName() + 
                             " MOVED FROM POSITION " + startingPosition + 
                             " TO POSITION " + newPosition + 
                             " (DICE: " + diceValues[0] + "+" + diceValues[1] + "=" + totalSpaces + ")");
            
            // Triple-check the position was set correctly
            int verifiedPosition = currentPlayer.getPosition();
            if (verifiedPosition != newPosition) {
                System.out.println("CRITICAL POSITION ERROR: Expected position " + newPosition + 
                                 " but actual position is " + verifiedPosition + " - fixing");
                currentPlayer.setPosition(newPosition);
                
                // Force another check
                if (currentPlayer.getPosition() != newPosition) {
                    System.out.println("SEVERE POSITION ERROR: Cannot set player position correctly!");
                }
            }
            
            // First update the dice display
            if (gui != null) {
                try {
                    // Update dice immediately
                    if (SwingUtilities.isEventDispatchThread()) {
                        gui.updateDice(diceValues[0], diceValues[1]);
                    } else {
                        SwingUtilities.invokeAndWait(() -> gui.updateDice(diceValues[0], diceValues[1]));
                    }
                    
                    // Small delay to ensure sequenced operations
                    Thread.sleep(100);
                    
                    // Then update player position separately
                    if (SwingUtilities.isEventDispatchThread()) {
                        gui.updatePlayerPosition(currentPlayer, newPosition);
                    } else {
                        SwingUtilities.invokeAndWait(() -> gui.updatePlayerPosition(currentPlayer, newPosition));
                    }
                } catch (Exception e) {
                    System.err.println("Error updating GUI: " + e.getMessage());
                    // Fallback with basic updates
                    SwingUtilities.invokeLater(() -> {
                        gui.updateDice(diceValues[0], diceValues[1]);
                        gui.updatePlayerPosition(currentPlayer, newPosition);
                    });
                }
            }
            
            // Handle doubles
            if (isDoubles) {
                doubleCount++;
                if (doubleCount == 3) {
                    // Three doubles in a row sends player to jail
                    sendPlayerToJail(currentPlayer);
                    doubleCount = 0;
                    endTurn();
                }
            } else {
                doubleCount = 0;
            }
            
            // Check the space the player landed on
            handleLandedSpace(currentPlayer, newPosition);
            
            System.out.println("********************************************");
            return diceValues;
            
        } catch (Exception e) {
            System.err.println("Error moving player: " + e.getMessage());
            e.printStackTrace();
            return new int[]{1, 1};
        }
    }
    
    /**
     * Handle the space that a player has landed on.
     * Central coordination method for all space landing logic.
     * 
     * @param player The player who landed on the space
     * @param position The position on the board
     */
    private void handleLandedSpace(Player player, int position) {
        // EMERGENCY FIX: Check for valid position
        if (position < 0 || position >= gameBoard.getBoardElements().length) {
            System.err.println("ERROR: Invalid position " + position + " for " + player.getName());
            System.out.println("EMERGENCY FIX: Resetting to GO (position 0)");
            position = 0;
            player.setPosition(0);
            
            // Update GUI with corrected position
            if (gui != null) {
                gui.updatePlayerPosition(player, 0);
            }
        }
        
        BoardSpace space = gameBoard.getBoardElements()[position];
        System.out.println("PLAYER " + player.getName() + " LANDED ON POSITION " + position + 
                         " (" + space.getName() + ")");
        
        // Choose handler based on space type
        if (space instanceof Property) {
            handlePropertySpace(player, (Property) space);
        } else if (position == 0) { // GO
            handleGoSpace(player);
        } else if (position == 10) { // Jail
            handleJailSpace(player);
        } else if (position == 20) { // Free Parking
            handleFreeParkingSpace(player);
        } else if (position == 30) { // Go To Jail
            handleGoToJailSpace(player);
        } else if (position == 4 || position == 38) { // Income Tax or Luxury Tax
            handleTaxSpace(player, position);
        } else if (position == 2 || position == 17 || position == 33) { // Community Chest
            handleCommunityChestSpace(player, position);
        } else if (position == 7 || position == 22 || position == 36) { // Chance
            handleChanceSpace(player, position);
        } else {
            // Default handling for other spaces
            handleOtherSpace(player, space);
        }
        
        // EMERGENCY FIX: Double-check player position after handling space
        int newPosition = player.getPosition();
        if (newPosition < 0 || newPosition >= gameBoard.getBoardElements().length) {
            System.err.println("ERROR: After handling space, player " + player.getName() + 
                              " ended up at invalid position " + newPosition);
            System.out.println("EMERGENCY FIX: Resetting to GO (position 0)");
            player.setPosition(0);
            
            // Update GUI with corrected position
            if (gui != null) {
                gui.updatePlayerPosition(player, 0);
            }
        }
        
        // Update GUI with player's new financial state
        if (gui != null) {
            gui.updatePlayerInfo(players);
        }
    }
    
    /**
     * Handle player landing on a property space.
     * 
     * @param player The player who landed on the space
     * @param property The property that the player landed on
     */
    private void handlePropertySpace(Player player, Property property) {
        System.out.println("CRITICAL PROPERTY HANDLING: Player " + player.getName() +
                " landed on property " + property.getName());

        // Check if property is null (defensive coding)
        if (property == null) {
            System.err.println("ERROR: Null property in handlePropertySpace");
            return;
        }

        // First check property ownership
        Player owner = property.getOwner();

        if (owner == null) {
            // UNOWNED PROPERTY - OFFER TO BUY
            System.out.println("PURCHASE OPPORTUNITY: Property " + property.getName() +
                    " is unowned. Offering to " + player.getName());

            if (player instanceof ComputerPlayer) {
                // Computer player logic
                handleComputerPlayerOnProperty((ComputerPlayer) player, property);
            } else {
                // HUMAN PLAYER - Show purchase dialog
                try {
                    int balance = banker.getBalance(player);
                    int price = property.getPurchasePrice();

                    // Check if player can afford it
                    if (balance < price) {
                        System.out.println("CANNOT AFFORD: Player only has $" + balance +
                                " but property costs $" + price);
                        gui.displayMessage(player.getName() + " can't afford to buy " +
                                property.getName() + ". Starting auction...");
                        auctionProperty(property);
                        return;
                    }

                    // Direct purchase dialog with simple confirmation
                    int choice = JOptionPane.showConfirmDialog(gui.getMainFrame(),
                            "Do you want to buy " + property.getName() + " for $" + price + "?",
                            "Buy Property", JOptionPane.YES_NO_OPTION);

                    if (choice == JOptionPane.YES_OPTION) {
                        // BUY PROPERTY using direct banker method
                        try {
                            banker.sellProperty(property, player);
                            System.out.println("PURCHASE SUCCESSFUL: " + player.getName() +
                                    " bought " + property.getName() + " for $" + price);

                            // Update UI
                            gui.updatePlayerInfo(players);
                            gui.updatePropertyOwnership(property, player);
                            gui.displayMessage(player.getName() + " purchased " +
                                    property.getName() + " for $" + price);
                        } catch (Exception e) {
                            System.err.println("ERROR during purchase: " + e.getMessage());
                            JOptionPane.showMessageDialog(gui.getMainFrame(),
                                    "Error during purchase: " + e.getMessage(),
                                    "Purchase Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        // AUCTION
                        System.out.println("PURCHASE DECLINED: Starting auction for " +
                                property.getName());
                        gui.displayMessage(player.getName() + " declined to buy " +
                                property.getName() + ". Starting auction...");
                        auctionProperty(property);
                    }
                } catch (PlayerNotFoundException e) {
                    System.err.println("Error finding player: " + e.getMessage());
                }
            }
        }
        else if (owner == player) {
            // PLAYER ALREADY OWNS THIS PROPERTY
            System.out.println("OWNER LANDED: " + player.getName() + " already owns " +
                    property.getName());
            gui.displayMessage(player.getName() + " landed on their own property: " +
                    property.getName());
        }
        else {
            // PROPERTY OWNED BY ANOTHER PLAYER - PAY RENT
            System.out.println("RENT PAYMENT: " + player.getName() + " must pay rent to " +
                    owner.getName() + " for " + property.getName());
            handleRentPayment(player, property);
        }
    }
    
    /**
     * Direct purchase dialog method with minimal code path
     */
    private void offerPropertyToHuman(HumanPlayer player, Property property) {
        try {
            // CRITICAL: Get banker instance
            Banker banker = Banker.getInstance();
            
            // Ensure property is available
            if (!banker.getAvailableProperties().contains(property)) {
                System.out.println("FIXING PROPERTY AVAILABILITY: Adding " + property.getName() + 
                                 " to available properties list");
                banker.addAvailableProperty(property);
            }
            
            // Get important values
            int price = property.getPurchasePrice();
            int balance = banker.getBalance(player);
            
            System.out.println("PURCHASE INFO: Price=$" + price + ", Player balance=$" + balance);
            
            // Check if player can afford it
            if (balance < price) {
                System.out.println("CANNOT AFFORD: Player only has $" + balance + 
                                 " but property costs $" + price);
                JOptionPane.showMessageDialog(gui.getMainFrame(),
                    "You don't have enough money to buy " + property.getName() + 
                    ". It costs $" + price + " but you only have $" + balance + ".",
                    "Cannot Afford Property", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Show simple purchase dialog - MOST DIRECT PATH
            String message = "Do you want to buy " + property.getName() + " for $" + price + "?";
            int choice = JOptionPane.showConfirmDialog(gui.getMainFrame(), message, 
                                                     "Buy Property", JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                // BUY PROPERTY
                System.out.println("PLAYER CHOSE TO BUY: " + property.getName());
                banker.sellProperty(property, player);
                
                // Update UI
                if (gui != null) {
                    gui.updatePlayerInfo(new ArrayList<>(Arrays.asList(player)));
                    gui.displayMessage(player.getName() + " purchased " + property.getName() + 
                                     " for $" + price);
                }
            } else {
                // DECLINE PURCHASE
                System.out.println("PLAYER DECLINED: " + property.getName() + " - Not buying");
                gui.displayMessage(player.getName() + " declined to purchase " + property.getName());
            }
        } catch (Exception e) {
            System.err.println("ERROR in purchase dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle computer player landing on a property.
     */
    private void handleComputerPlayerOnProperty(ComputerPlayer cpu, Property property) {
        // First check if property is available for purchase
        boolean isAvailable = banker.getAvailableProperties().contains(property);
        
        // If property is not in the available list but has no owner, add it
        if (!isAvailable && property.getOwner() == null) {
            System.out.println("CPU PROPERTY FIX: Property " + property.getName() + " has no owner but wasn't available - fixing");
            banker.addAvailableProperty(property);
            isAvailable = true;
        }
        
        if (isAvailable) {
            CpuController controller = cpuControllers.get(cpu);
            if (controller != null) {
                boolean shouldBuy = controller.decidePropertyPurchase(property);
                System.out.println("CPU " + cpu.getName() + " decided " + (shouldBuy ? "TO BUY" : "NOT TO BUY") + 
                                 " property " + property.getName());
                
                if (shouldBuy) {
                    try {
                        // Check if CPU can afford the property
                        int propertyPrice = property.getPurchasePrice();
                        int cpuBalance = banker.getBalance(cpu);
                        
                        if (cpuBalance >= propertyPrice) {
                            // CPU decided to buy
                            banker.sellProperty(property, cpu);
                            System.out.println("CPU PURCHASE: " + cpu.getName() + " purchased " + 
                                             property.getName() + " for $" + property.getPurchasePrice());
                            
                            if (gui != null) {
                                gui.displayMessage(cpu.getName() + " purchased " + property.getName() + 
                                                 " for $" + property.getPurchasePrice());
                                gui.updatePlayerInfo(players);
                                gui.updatePropertyOwnership(property, cpu);
                            }
                        } else {
                            System.out.println("CPU can't afford property: $" + propertyPrice + 
                                             " costs more than CPU balance of $" + cpuBalance);
                            
                            // CPU can't afford - auction property
                            if (gui != null) {
                                gui.displayMessage(cpu.getName() + " can't afford " + 
                                                 property.getName() + ". Starting auction...");
                            }
                            auctionProperty(property);
                        }
                    } catch (Exception e) {
                        System.err.println("Error during CPU property purchase: " + e.getMessage());
                    }
                } else {
                    // CPU declined to buy - auction
                    if (gui != null) {
                        gui.displayMessage(cpu.getName() + " declined to purchase " + 
                                         property.getName() + ". Starting auction...");
                    }
                    auctionProperty(property);
                }
            }
        } else {
            // Let the property handle other cases like rent payment
            try {
                System.out.println("Property " + property.getName() + " is not available for purchase. " +
                                 "Owner: " + (property.getOwner() != null ? property.getOwner().getName() : "None"));
                property.onLanding(cpu);
            } catch (PlayerNotFoundException e) {
                System.err.println("Error in property landing: " + e.getMessage());
            }
        }
    }
    
    /**
     * Handle human player landing on an unowned property.
     */
    private void handleUnownedProperty(HumanPlayer player, Property property) {
        if (gui == null) return;
        
        // First ensure the property is actually in the available list
        if (!banker.getAvailableProperties().contains(property) && property.getOwner() == null) {
            System.out.println("PROPERTY FIX: Property " + property.getName() + " has no owner but wasn't available - fixing");
            banker.addAvailableProperty(property);
        }
        
        // Show a clear message about landing on unowned property
        SwingUtilities.invokeLater(() -> {
            gui.displayMessage(player.getName() + " landed on " + property.getName() + 
                            " - Price: $" + property.getPurchasePrice());
        });
        
        try {
            int balance = banker.getBalance(player);
            int price = property.getPurchasePrice();
            
            System.out.println("HUMAN PLAYER LANDED ON UNOWNED PROPERTY: " + 
                            player.getName() + " -> " + property.getName());
            System.out.println("PLAYER BALANCE: $" + balance + ", PROPERTY PRICE: $" + price);
            
            if (balance >= price) {
                // Use a brief delay to ensure the message is seen before showing the dialog
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // Ignore interruption
                }
                
                // IMPORTANT: Ensure we're not on the EDT already before showing the dialog
                if (SwingUtilities.isEventDispatchThread()) {
                    // Directly show the dialog
                    System.out.println("SHOWING PURCHASE DIALOG DIRECTLY FOR " + property.getName());
                    boolean wantsToBuy = showEmergencyPurchaseDialog(property, player);
                    handlePurchaseDecision(player, property, wantsToBuy);
                } else {
                    // Use invokeLater to show the dialog on the EDT
                    System.out.println("SCHEDULING PURCHASE DIALOG FOR " + property.getName());
                    SwingUtilities.invokeLater(() -> {
                        boolean wantsToBuy = showEmergencyPurchaseDialog(property, player);
                        handlePurchaseDecision(player, property, wantsToBuy);
                    });
                }
            } else {
                // Can't afford - show message and auction
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(gui.getMainFrame(),
                            "You don't have enough money to buy " + property.getName() + 
                            ". It costs $" + price + " but you only have $" + balance + ".",
                            "Insufficient Funds", JOptionPane.WARNING_MESSAGE);
                    gui.displayMessage("Property will be auctioned since " + 
                                    player.getName() + " can't afford to buy it.");
                });
                auctionProperty(property);
            }
        } catch (PlayerNotFoundException e) {
            System.err.println("Error finding player: " + e.getMessage());
        }
    }
    
    /**
     * Handle the player's decision to buy or not buy a property
     */
    private void handlePurchaseDecision(Player player, Property property, boolean wantsToBuy) {
        if (wantsToBuy) {
            // Buy the property
            purchaseProperty(player, property);
        } else {
            // Auction the property
            SwingUtilities.invokeLater(() -> {
                gui.displayMessage(player.getName() + " declined to purchase " + 
                                property.getName() + ". Starting auction...");
            });
            auctionProperty(property);
        }
    }
    
    /**
     * Emergency method for showing a simple property purchase dialog.
     * Uses direct JOptionPane for maximum reliability.
     */
    private boolean showEmergencyPurchaseDialog(Property property, Player player) {
        System.out.println("EMERGENCY: Showing direct purchase dialog for " + property.getName());
        
        // Get the price and player's money
        int price = property.getPurchasePrice();
        int playerMoney;
        try {
            playerMoney = banker.getBalance(player);
        } catch (Exception e) {
            System.err.println("Error getting balance: " + e.getMessage());
            return false;
        }
        
        // If player can't afford it, don't show dialog
        if (playerMoney < price) {
            System.out.println("Player can't afford property - skipping dialog");
            return false;
        }
        
        // Check if we're already on the EDT
        if (SwingUtilities.isEventDispatchThread()) {
            // We're already on the EDT, so just show the dialog directly
            String message = "Do you want to buy " + property.getName() + 
                             " for $" + price + "?\n\n" +
                             "Your balance: $" + playerMoney;
            int choice = JOptionPane.showConfirmDialog(
                gui.getMainFrame(),
                message,
                "Buy Property - " + property.getName(),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            boolean result = (choice == JOptionPane.YES_OPTION);
            System.out.println("EMERGENCY: Purchase dialog result: " + (result ? "YES" : "NO"));
            return result;
        } else {
            // We're not on the EDT, so use invokeAndWait
            final boolean[] result = new boolean[1];
            try {
                SwingUtilities.invokeAndWait(() -> {
                    String message = "Do you want to buy " + property.getName() + 
                                     " for $" + price + "?\n\n" +
                                     "Your balance: $" + playerMoney;
                    int choice = JOptionPane.showConfirmDialog(
                        gui.getMainFrame(),
                        message,
                        "Buy Property - " + property.getName(),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );
                    result[0] = (choice == JOptionPane.YES_OPTION);
                    System.out.println("EMERGENCY: Purchase dialog result: " + (result[0] ? "YES" : "NO"));
                });
            } catch (Exception e) {
                System.err.println("Error showing dialog: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
            return result[0];
        }
    }
    
    /**
     * Show dialog asking if player wants to buy property.
     * Returns true if player wants to buy, false otherwise.
     */
    private boolean showPropertyPurchaseDialog(Property property, int price) {
        if (gui == null) return false;
        
        String message = "Do you want to buy " + property.getName() + " for $" + price + "?";
        
        System.out.println("**************** SHOWING PURCHASE DIALOG ****************");
        System.out.println("Asking whether to buy " + property.getName() + " for $" + price);
        
        // Use JOptionPane.showConfirmDialog directly on the AWT Event Dispatch Thread
        // to ensure that it blocks the thread until a response is given
        final boolean[] result = new boolean[1];
        
        try {
            SwingUtilities.invokeAndWait(() -> {
                int choice = JOptionPane.showConfirmDialog(
                    gui.getMainFrame(),
                    message,
                    "Buy Property - " + property.getName(),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                result[0] = (choice == JOptionPane.YES_OPTION);
            });
        } catch (Exception e) {
            System.err.println("Error showing property purchase dialog: " + e.getMessage());
            return false;
        }
        
        System.out.println("PURCHASE DIALOG RESULT: " + (result[0] ? "YES" : "NO"));
        return result[0];
    }
    
    /**
     * Purchase a property for a player.
     */
    private void purchaseProperty(Player player, Property property) {
        try {
            // First check if property is available to be purchased
            if (!banker.getAvailableProperties().contains(property)) {
                System.out.println("*** CRITICAL: Property " + property.getName() + " is not available for purchase!");
                
                // Make it available if possible
                if (property.getOwner() == null) {
                    banker.addAvailableProperty(property);
                    System.out.println("*** FIXED: Property " + property.getName() + " is now available for purchase");
                } else {
                    System.out.println("*** ERROR: Property " + property.getName() + " is already owned by " + 
                                    property.getOwner().getName());
                    if (gui != null) {
                        JOptionPane.showMessageDialog(gui.getMainFrame(),
                            "Property " + property.getName() + " is already owned by " + property.getOwner().getName(),
                            "Cannot Purchase", JOptionPane.ERROR_MESSAGE);
                    }
                    return;
                }
            }
            
            // Verify player has enough money
            int price = property.getPurchasePrice();
            int balance;
            try {
                balance = banker.getBalance(player);
                if (balance < price) {
                    JOptionPane.showMessageDialog(gui.getMainFrame(),
                            "Not enough money to buy " + property.getName() + 
                            ". Cost: $" + price + ", Balance: $" + balance,
                            "Insufficient Funds", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (PlayerNotFoundException pnfe) {
                System.err.println("Player not found: " + pnfe.getMessage());
                return;
            }
            
            // Now try to sell the property
            System.out.println("Attempting to sell " + property.getName() + " to " + player.getName());
            banker.sellProperty(property, player);
            System.out.println("Property sale successful!");
            
            // Update GUI
            if (gui != null) {
                SwingUtilities.invokeLater(() -> {
                    gui.displayMessage(player.getName() + " purchased " + 
                                     property.getName() + " for $" + property.getPurchasePrice());
                    gui.updatePlayerInfo(players);
                    gui.updatePropertyOwnership(property, player);
                    
                    // Show success message
                    JOptionPane.showMessageDialog(gui.getMainFrame(),
                            player.getName() + " now owns " + property.getName() + "!",
                            "Property Purchased", JOptionPane.INFORMATION_MESSAGE);
                });
            }
        } catch (Exception e) {
            // Handle any other unexpected errors
            System.err.println("Error during property purchase: " + e.getMessage());
            e.printStackTrace();
            
            if (gui != null) {
                JOptionPane.showMessageDialog(gui.getMainFrame(),
                        "Error during purchase: " + e.getMessage(),
                        "Purchase Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Handle rent payment when a player lands on someone else's property.
     */
    private void handleRentPayment(Player player, Property property) {
        try {
            if (!property.isMortgaged()) {
                int rent = property.calculateRent(player);
                banker.withdraw(player, rent);
                banker.deposit(property.getOwner(), rent);
                
                if (gui != null) {
                    String ownerName = property.getOwner().getName();
                    gui.displayMessage(player.getName() + " landed on " + property.getName() + 
                                    " (owned by " + ownerName + ") and paid $" + rent + " in rent");
                    gui.updatePlayerInfo(players);
                }
            } else if (gui != null) {
                gui.displayMessage(property.getName() + " is mortgaged. No rent due!");
            }
        } catch (Exception e) {
            System.err.println("Error handling rent payment: " + e.getMessage());
        }
    }
    
    /**
     * Handle player landing on GO space.
     */
    private void handleGoSpace(Player player) {
        if (gui != null) {
            gui.displayMessage(player.getName() + " landed on GO! Collect $200");
        }
        
        try {
            banker.deposit(player, 200); // Pay GO money
        } catch (PlayerNotFoundException e) {
            System.err.println("Error paying GO money: " + e.getMessage());
        }
    }
    
    /**
     * Handle player landing on Jail space.
     */
    private void handleJailSpace(Player player) {
        if (gui != null) {
            if (player.isInJail()) {
                gui.displayMessage(player.getName() + " is in jail");
            } else {
                gui.displayMessage(player.getName() + " is just visiting jail");
            }
        }
    }
    
    /**
     * Handle player landing on Free Parking space.
     */
    private void handleFreeParkingSpace(Player player) {
        if (gui != null) {
            gui.displayMessage(player.getName() + " landed on Free Parking");
        }
    }
    
    /**
     * Handle player landing on Go To Jail space.
     */
    private void handleGoToJailSpace(Player player) {
        if (gui != null) {
            gui.displayMessage(player.getName() + " is being sent to jail!");
        }
        sendPlayerToJail(player);
    }
    
    /**
     * Handle player landing on a Tax space.
     */
    private void handleTaxSpace(Player player, int position) {
        String taxName = (position == 4) ? "Income Tax" : "Luxury Tax";
        int amount = (position == 4) ? 200 : 75; // Corrected Luxury Tax to $75
        
        if (gui != null) {
            gui.displayMessage(player.getName() + " paid $" + amount + " for " + taxName);
        }
        
        try {
            banker.withdraw(player, amount);
        } catch (Exception e) {
            System.err.println("Error paying tax: " + e.getMessage());
        }
    }
    
    /**
     * Handle player landing on a Community Chest space.
     */
    private void handleCommunityChestSpace(Player player, int position) {
        if (gui == null) return;
        
        gui.displayMessage(player.getName() + " landed on Community Chest");
        System.out.println("CRITICAL CARD SPACE: " + player.getName() + " landed on Community Chest at position " + position);
        
        try {
            // Draw card
            Model.Cards.CommunityChestCard communityChest = Model.Cards.CommunityChestCard.getInstance();
            String cardText = communityChest.drawCard();
            gui.displayMessage(player.getName() + " drew a Community Chest card: " + cardText);
            System.out.println("CARD TEXT: " + cardText);
            
            // Store original position
            int originalPosition = player.getPosition();
            System.out.println("POSITION BEFORE CARD: " + originalPosition);
            
            // CRITICAL FIX: Check for problematic cards that might cause incorrect movement
            if (cardText.contains("Advance to GO")) {
                // Handle "Advance to Go" manually instead of using card effect
                System.out.println("CRITICAL CARD FIX: Handling 'Advance to GO' card directly");
                
                // Set position directly to 0 (GO)
                player.setPosition(0);
                
                // Update GUI
                gui.updatePlayerPosition(player, 0);
                gui.displayMessage(player.getName() + " advances to GO and collects $200");
                
                // Give player $200 for passing GO
                try {
                    banker.deposit(player, 200);
                } catch (Exception e) {
                    System.err.println("Error paying GO money: " + e.getMessage());
                }
                
                // Handle GO space directly
                handleGoSpace(player);
            } 
            else {
                // Apply regular card effect for non-movement cards
                communityChest.useCard(cardText, player);
                
                // Update position in GUI
                gui.updatePlayerPosition(player, player.getPosition());
                
                // Handle new space if position changed
                if (player.getPosition() != originalPosition) {
                    int newPosition = player.getPosition();
                    System.out.println("POSITION AFTER CARD: " + newPosition);
                    gui.displayMessage(player.getName() + " moved to " + 
                                    gameBoard.getBoardElements()[newPosition].getName());
                    
                    // Double check that the new position is valid
                    if (newPosition < 0 || newPosition >= 40) {
                        System.out.println("CRITICAL ERROR: Invalid position " + newPosition + " after card effect");
                        newPosition = originalPosition; // Revert to original position if invalid
                        player.setPosition(originalPosition);
                        gui.updatePlayerPosition(player, originalPosition);
                    } else {
                        // Handle new space normally if position is valid
                        handleLandedSpace(player, newPosition);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error with Community Chest card: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle player landing on a Chance space.
     */
    private void handleChanceSpace(Player player, int position) {
        if (gui == null) return;
        
        gui.displayMessage(player.getName() + " landed on Chance");
        System.out.println("CRITICAL CARD SPACE: " + player.getName() + " landed on Chance at position " + position);
        
        try {
            // Draw card
            Model.Cards.ChanceCard chance = Model.Cards.ChanceCard.getInstance();
            String cardText = chance.drawCard();
            gui.displayMessage(player.getName() + " drew a Chance card: " + cardText);
            System.out.println("CARD TEXT: " + cardText);
            
            // Store original position
            int originalPosition = player.getPosition();
            System.out.println("POSITION BEFORE CARD: " + originalPosition);
            
            // CRITICAL FIX: Handle movement cards directly to prevent position errors
            if (cardText.contains("Advance to GO")) {
                // Handle "Advance to Go" manually
                System.out.println("CRITICAL CARD FIX: Handling 'Advance to GO' card directly");
                
                // Set position directly to 0 (GO)
                player.setPosition(0);
                
                // Update GUI
                gui.updatePlayerPosition(player, 0);
                gui.displayMessage(player.getName() + " advances to GO and collects $200");
                
                // Give player $200 for passing GO
                try {
                    banker.deposit(player, 200);
                } catch (Exception e) {
                    System.err.println("Error paying GO money: " + e.getMessage());
                }
                
                // Handle GO space directly
                handleGoSpace(player);
            }
            else if (cardText.contains("Advance to Boardwalk")) {
                System.out.println("CRITICAL CARD FIX: Handling 'Advance to Boardwalk' card directly");
                
                // Set position directly to 39 (Boardwalk)
                player.setPosition(39);
                
                // Update GUI
                gui.updatePlayerPosition(player, 39);
                gui.displayMessage(player.getName() + " advances to Boardwalk");
                
                // Handle Boardwalk space directly
                handleLandedSpace(player, 39);
            }
            else if (cardText.contains("Advance to Illinois Avenue")) {
                System.out.println("CRITICAL CARD FIX: Handling 'Advance to Illinois Avenue' card directly");
                
                // Set position directly to 24 (Illinois Avenue)
                player.setPosition(24);
                
                // Update GUI
                gui.updatePlayerPosition(player, 24);
                gui.displayMessage(player.getName() + " advances to Illinois Avenue");
                
                // Handle Illinois Avenue space directly
                handleLandedSpace(player, 24);
            }
            else if (cardText.contains("Advance to St. Charles Place")) {
                System.out.println("CRITICAL CARD FIX: Handling 'Advance to St. Charles Place' card directly");
                
                // Set position directly to 11 (St. Charles Place)
                player.setPosition(11);
                
                // Update GUI
                gui.updatePlayerPosition(player, 11);
                gui.displayMessage(player.getName() + " advances to St. Charles Place");
                
                // Handle St. Charles Place space directly
                handleLandedSpace(player, 11);
            }
            else if (cardText.contains("Go to Jail")) {
                System.out.println("CRITICAL CARD FIX: Handling 'Go to Jail' card directly");
                
                // Set position directly to 10 (Jail) and mark as in jail
                player.setPosition(10);
                player.setInJail(true);
                
                // Update GUI
                gui.updatePlayerPosition(player, 10);
                gui.displayMessage(player.getName() + " is sent to Jail");
            }
            else if (cardText.contains("Go back 3 spaces")) {
                System.out.println("CRITICAL CARD FIX: Handling 'Go back 3 spaces' card directly");
                
                // Calculate new position (going back 3 spaces)
                int newPosition = originalPosition - 3;
                if (newPosition < 0) newPosition += 40; // Wrap around the board
                
                // Set position directly
                player.setPosition(newPosition);
                
                // Update GUI
                gui.updatePlayerPosition(player, newPosition);
                gui.displayMessage(player.getName() + " moved back 3 spaces to " + 
                                  gameBoard.getBoardElements()[newPosition].getName());
                
                // Handle the new space
                handleLandedSpace(player, newPosition);
            }
            else if (cardText.contains("nearest Railroad")) {
                System.out.println("CRITICAL CARD FIX: Handling 'nearest Railroad' card directly");
                
                // Calculate nearest railroad position
                int currentPos = originalPosition;
                int newPosition;
                
                if (currentPos < 5) newPosition = 5; // Reading Railroad
                else if (currentPos < 15) newPosition = 15; // Pennsylvania Railroad
                else if (currentPos < 25) newPosition = 25; // B&O Railroad
                else if (currentPos < 35) newPosition = 35; // Short Line
                else newPosition = 5; // Reading Railroad (wrap around)
                
                // Set position directly
                player.setPosition(newPosition);
                
                // Update GUI
                gui.updatePlayerPosition(player, newPosition);
                gui.displayMessage(player.getName() + " advanced to " + 
                                  gameBoard.getBoardElements()[newPosition].getName());
                
                // Handle the railroad space
                handleLandedSpace(player, newPosition);
            }
            else if (cardText.contains("nearest Utility")) {
                System.out.println("CRITICAL CARD FIX: Handling 'nearest Utility' card directly");
                
                // Calculate nearest utility position
                int currentPos = originalPosition;
                int newPosition;
                
                if (currentPos < 12 || currentPos > 28) newPosition = 12; // Electric Company
                else newPosition = 28; // Water Works
                
                // Set position directly
                player.setPosition(newPosition);
                
                // Update GUI
                gui.updatePlayerPosition(player, newPosition);
                gui.displayMessage(player.getName() + " advanced to " + 
                                  gameBoard.getBoardElements()[newPosition].getName());
                
                // Handle the utility space
                handleLandedSpace(player, newPosition);
            }
            else {
                // Apply regular card effect for non-movement cards
                chance.useCard(cardText, player);
                
                // Update position in GUI
                gui.updatePlayerPosition(player, player.getPosition());
                
                // Handle new space if position changed
                if (player.getPosition() != originalPosition) {
                    int newPosition = player.getPosition();
                    System.out.println("POSITION AFTER CARD: " + newPosition);
                    gui.displayMessage(player.getName() + " moved to " + 
                                      gameBoard.getBoardElements()[newPosition].getName());
                    
                    // Double check that the new position is valid
                    if (newPosition < 0 || newPosition >= 40) {
                        System.out.println("CRITICAL ERROR: Invalid position " + newPosition + 
                                          " after card effect");
                        // Revert to original position if invalid
                        newPosition = originalPosition;
                        player.setPosition(originalPosition);
                        gui.updatePlayerPosition(player, originalPosition);
                    } else {
                        // Handle new space normally if position is valid
                        handleLandedSpace(player, newPosition);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error with Chance card: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle player landing on any other type of space.
     */
    private void handleOtherSpace(Player player, BoardSpace space) {
        try {
            space.onLanding(player);
        } catch (PlayerNotFoundException e) {
            System.err.println("Error on space landing: " + e.getMessage());
        }
    }
    
    /**
     * Buy a property for a player.
     * 
     * @param property The property to buy
     * @param player The player buying the property
     * @return True if the purchase was successful, false otherwise
     */
    public boolean buyProperty(Property property, Player player) {
        try {
            // Use banker to handle the transaction
            banker.sellProperty(property, player);
            
            // Update GUI
            if (gui != null) {
                gui.updatePlayerInfo(players);
                gui.updatePropertyOwnership(property, player);
                gui.displayMessage(player.getName() + " purchased " + property.getName() + " for $" + property.getPurchasePrice());
            }
            
            return true;
        } catch (Exception e) {
            // This catches PlayerNotFoundException, InsufficientFundsException, etc.
            System.err.println("Error during property purchase: " + e.getMessage());
            if (gui != null) {
                gui.displayMessage("Purchase failed: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * Auction a property when a player declines to buy it.
     * 
     * @param property The property to auction
     */
    public void auctionProperty(Property property) {
        if (gui != null) {
            gui.displayMessage("Starting auction for " + property.getName());

            // Use a simpler auction mechanism for the MVP
            Player winner = null;
            int highestBid = 0;

            // Simple auction: Let each player make one bid based on their computer logic
            // or for humans, a simple dialog
            for (Player player : players) {
                try {
                    int balance = banker.getBalance(player);
                    int maxBid = Math.min(balance, property.getPurchasePrice());

                    if (maxBid <= highestBid) {
                        continue; // Can't afford to outbid
                    }

                    int bid = 0;
                    if (player instanceof ComputerPlayer) {
                        // Computer player bids automatically
                        CpuController cpu = cpuControllers.get((ComputerPlayer)player);
                        bid = cpu.decideBidAmount(property, highestBid);
                    } else {
                        // Human player bids via dialog
                        String bidStr = JOptionPane.showInputDialog(gui.getMainFrame(),
                                player.getName() + ", enter your bid for " + property.getName() +
                                        " (minimum: $" + (highestBid + 1) + ", maximum: $" + maxBid + ")",
                                "Auction", JOptionPane.QUESTION_MESSAGE);

                        if (bidStr != null && !bidStr.isEmpty()) {
                            try {
                                bid = Integer.parseInt(bidStr);
                                // Ensure bid is valid
                                if (bid <= highestBid || bid > maxBid) {
                                    JOptionPane.showMessageDialog(gui.getMainFrame(),
                                            "Invalid bid amount. Must be between $" + (highestBid + 1) +
                                                    " and $" + maxBid,
                                            "Invalid Bid", JOptionPane.WARNING_MESSAGE);
                                    bid = 0;
                                }
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(gui.getMainFrame(),
                                        "Please enter a valid number",
                                        "Invalid Bid", JOptionPane.WARNING_MESSAGE);
                                bid = 0;
                            }
                        }
                    }

                    // Update highest bid if this player's bid is higher
                    if (bid > highestBid) {
                        highestBid = bid;
                        winner = player;
                        gui.displayMessage(player.getName() + " bids $" + bid);
                    }

                } catch (PlayerNotFoundException e) {
                    System.err.println("Error getting player balance: " + e.getMessage());
                }
            }

            // Complete the auction
            if (winner != null && highestBid > 0) {
                try {
                    banker.withdraw(winner, highestBid);
                    banker.addTitleDeed(winner, property);
                    property.setOwner(winner);

                    gui.displayMessage(winner.getName() + " won the auction for " +
                            property.getName() + " with a bid of $" + highestBid);
                    gui.updatePlayerInfo(players);
                    gui.updatePropertyOwnership(property, winner);
                } catch (Exception e) {
                    System.err.println("Error completing auction: " + e.getMessage());
                }
            } else {
                // No bids, property remains with bank
                gui.displayMessage("No one bid on " + property.getName() + ". It remains with the bank.");
            }
        }
    }
    
    /**
     * Send a player to jail.
     * 
     * @param player The player to send to jail
     */
    private void sendPlayerToJail(Player player) {
        player.setPosition(10); // Jail is at position 10
        player.setInJail(true);
        player.resetTurnsInJail(); // Reset jail turn counter
        
        // Update GUI
        if (gui != null) {
            gui.updatePlayerPosition(player, 10);
            gui.displayMessage(player.getName() + " has been sent to jail!");
        }
    }
    
    /**
     * End the current player's turn and move to the next player.
     */
    public void endTurn() {
        // If we rolled doubles and aren't in jail, player gets another turn
        if (doubleCount > 0 && !currentPlayer.isInJail()) {
            // Same player's turn again
            if (gui != null) {
                gui.displayMessage(currentPlayer.getName() + " rolled doubles and gets another turn!");
            }
            return;
        }
        
        // Move to next player
        turnManager.nextTurn();
        currentPlayer = turnManager.getCurrentPlayer();
        doubleCount = 0;
        
        // Update GUI for next player
        if (gui != null) {
            gui.updateCurrentPlayer(currentPlayer);
            gui.displayMessage("It's " + currentPlayer.getName() + "'s turn!");
        }
        
        // If next player is a computer, handle their turn automatically
        if (currentPlayer instanceof ComputerPlayer) {
            handleComputerPlayerTurn((ComputerPlayer) currentPlayer);
        }
    }
    
    /**
     * Handle a computer player's turn automatically.
     * 
     * @param cpu The computer player whose turn it is
     */
    private void handleComputerPlayerTurn(ComputerPlayer cpu) {
        CpuController controller = cpuControllers.get(cpu);
        if (controller == null) {
            // This shouldn't happen, but just in case
            endTurn();
            return;
        }
        
        // Add a delay to make the CPU's turn visible to the player
        Timer timer = new Timer(1500, event -> {
            // CRITICAL POSITION CHECK
            int originalPosition = cpu.getPosition();
            System.out.println("********************************************");
            System.out.println("CPU " + cpu.getName() + " is at position " + originalPosition + " before rolling");
            
            // Reset problematic positions
            if (originalPosition >= 40 || originalPosition < 0) {
                System.out.println("SEVERE ERROR: CPU " + cpu.getName() + 
                                 " has invalid position " + originalPosition + " - resetting to GO");
                cpu.setPosition(0);
                originalPosition = 0;
                
                // Update GUI if available
                if (gui != null) {
                    gui.updatePlayerPosition(cpu, 0);
                }
            }
            
            // Roll dice - we'll handle the movement ourselves for CPU to ensure accuracy
            dice.roll();
            int[] diceValues = {dice.getDie1(), dice.getDie2()};
            int diceSum = diceValues[0] + diceValues[1];
            boolean isDoubles = diceValues[0] == diceValues[1];
            
            // For clear debug output
            System.out.println("*** CPU DICE ROLLS: " + diceValues[0] + " and " + diceValues[1]);
            System.out.println("*** CPU TOTAL: " + diceSum + " spaces");
            
            // If CPU is in jail, handle jail differently
            if (cpu.isInJail()) {
                handleCpuInJail(cpu, isDoubles);
            } else {
                try {
                    // CRITICAL FIX: ABSOLUTE CONTROL over position calculation
                    int newPosition = (originalPosition + diceSum) % 40;
                    System.out.println("CPU POSITION CALCULATION: " + originalPosition + 
                                     " + " + diceSum + " = " + newPosition);
                    
                    // BYPASS all other movement methods which might be causing issues
                    // Set position DIRECTLY - this is the authoritative position
                    cpu.setPosition(newPosition);
                    System.out.println("======> CPU " + cpu.getName() + 
                                     " MOVED FROM POSITION " + originalPosition + 
                                     " TO POSITION " + newPosition + 
                                     " (DICE: " + diceValues[0] + "+" + 
                                     diceValues[1] + "=" + diceSum + ")");
                    
                    // Triple-check position update worked
                    int verifiedPosition = cpu.getPosition();
                    if (verifiedPosition != newPosition) {
                        System.out.println("CRITICAL CPU POSITION ERROR: Expected position " + newPosition + 
                                         " but actual position is " + verifiedPosition + " - fixing");
                        cpu.setPosition(newPosition);
                        
                        // Force another check
                        if (cpu.getPosition() != newPosition) {
                            System.out.println("SEVERE CPU POSITION ERROR: Cannot set CPU position correctly!");
                        }
                    }
                    
                    // Update GUI with dice first, then position - with explicit sequencing
                    if (gui != null) {
                        // Step 1: Update dice display first
                        SwingUtilities.invokeLater(() -> {
                            gui.updateDice(diceValues[0], diceValues[1]);
                        });
                        
                        // Small delay to ensure operations happen in sequence
                        Thread.sleep(300);
                        
                        // Step 2: Update player position with a separate operation
                        SwingUtilities.invokeLater(() -> {
                            // Update the token position
                            gui.updatePlayerPosition(cpu, newPosition);
                            
                            // Display message about CPU movement
                            String spaceName = gameBoard.getBoardElements()[newPosition].getName();
                            gui.displayMessage("CPU " + cpu.getName() + " rolled " + diceSum + 
                                              " and moved to " + spaceName);
                        });
                        
                        // Another small delay before handling the space effects
                        Thread.sleep(200);
                    }
                    
                    // Handle the space the CPU landed on
                    handleLandedSpace(cpu, newPosition);
                    
                } catch (InterruptedException e) {
                    System.err.println("Error updating CPU movement GUI: " + e.getMessage());
                }
            }
            
            // Verify position after all operations, just as a safety check
            int finalPosition = cpu.getPosition();
            if (!cpu.isInJail() && finalPosition != (originalPosition + diceSum) % 40 && 
                finalPosition != 10) { // Not in jail or sent to jail
                System.out.println("CPU POSITION DISCREPANCY DETECTED: CPU should be at " + 
                                ((originalPosition + diceSum) % 40) + " but is at " + finalPosition);
            }
            
            // Process other CPU actions like trading or building houses
            // (not implemented in this version)
            
            // Check if CPU gets another turn due to doubles
            if (isDoubles && !cpu.isInJail() && doubleCount < 3) {
                // Schedule another turn for this CPU
                Timer doublesTimer = new Timer(1500, actionEvent -> handleComputerPlayerTurn(cpu));
                doublesTimer.setRepeats(false);
                doublesTimer.start();
            } else {
                // End CPU's turn
                Timer endTurnTimer = new Timer(1000, actionEvent -> endTurn());
                endTurnTimer.setRepeats(false);
                endTurnTimer.start();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Handle a CPU player's actions when in jail.
     * 
     * @param cpu The CPU player in jail
     * @param isDoubles Whether the CPU rolled doubles
     */
    private void handleCpuInJail(ComputerPlayer cpu, boolean isDoubles) {
        // Get CPU's jail turn count from the Player model
        int jailTurns = cpu.getTurnsInJail();
        
        // If CPU rolled doubles, they get out of jail
        if (isDoubles) {
            cpu.setInJail(false);
            cpu.resetTurnsInJail();
            if (gui != null) {
                gui.displayMessage(cpu.getName() + " rolled doubles and got out of jail!");
            }
            return;
        }
        
        // Increment turns in jail
        cpu.incrementTurnsInJail();
        
        // If CPU has a Get Out of Jail Free card, use it
        if (cpu.hasGetOutOfJailFreeCard()) {
            cpu.useGetOutOfJailFreeCard();
            cpu.setInJail(false);
            cpu.resetTurnsInJail();
            if (gui != null) {
                gui.displayMessage(cpu.getName() + " used a Get Out of Jail Free card!");
                gui.updatePlayerInfo(players);
            }
            return;
        }
        
        // On third turn in jail, CPU must pay $50 to get out
        if (jailTurns >= 2) {
            try {
                banker.withdraw(cpu, 50);
                cpu.setInJail(false);
                cpu.resetTurnsInJail();
                if (gui != null) {
                    gui.displayMessage(cpu.getName() + " paid $50 to get out of jail!");
                    gui.updatePlayerInfo(players);
                }
            } catch (Exception e) {
                // CPU can't afford it - would need to sell/mortgage properties
                // For now, we'll just let them out anyway
                cpu.setInJail(false);
                cpu.resetTurnsInJail();
                if (gui != null) {
                    gui.displayMessage(cpu.getName() + " was released from jail due to lack of funds!");
                }
            }
        } else {
            if (gui != null) {
                gui.displayMessage(cpu.getName() + " is in jail! Turn " + (jailTurns + 1) + " of 3");
            }
        }
    }
    
    /**
     * Get the image for a player token.
     * 
     * @param tokenName The name of the token
     * @return The ImageIcon for the token, or null if not found
     */
    public ImageIcon getTokenImage(String tokenName) {
        return tokenImages.get(tokenName);
    }
    
    /**
     * Get the image for a card type.
     * 
     * @param cardType The type of card ("chance" or "community_chest")
     * @return The ImageIcon for the card, or null if not found
     */
    public ImageIcon getCardImage(String cardType) {
        return cardImages.get(cardType);
    }
    
    /**
     * Get the current player.
     * 
     * @return The current player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    /**
     * Check if the game is in progress.
     * 
     * @return True if the game is in progress, false otherwise
     */
    public boolean isGameInProgress() {
        return game.gameInProgress();
    }
}
