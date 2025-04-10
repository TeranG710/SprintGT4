package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Test implementation of the Monopoly game GUI.
 * This class represents a prototype GUI for the Monopoly board game using Java Swing.
 */
public class TestMonopolyGUI {
    // Main frame and panels
    private JFrame mainFrame;
    private JPanel gamePanel;
    private JPanel boardPanel;
    private JPanel controlPanel;
    private JPanel playerInfoPanel;
    
    // Board dimensions
    private static final int BOARD_SIZE = 700;
    private static final int SPACE_WIDTH = BOARD_SIZE / 11;
    private static final int SPACE_HEIGHT = BOARD_SIZE / 11;
    
    // Colors based on Monopoly property colors
    private static final Color BROWN = new Color(150, 75, 0);
    private static final Color LIGHT_BLUE = new Color(170, 224, 250);
    private static final Color PINK = new Color(217, 58, 150);
    private static final Color ORANGE = new Color(242, 142, 43);
    private static final Color RED = new Color(227, 34, 25);
    private static final Color YELLOW = new Color(255, 239, 0);
    private static final Color GREEN = new Color(31, 164, 73);
    private static final Color DARK_BLUE = new Color(0, 114, 187);
    
    // Property color mapping
    private Map<String, Color> propertyColors;
    
    // Sample player data for testing
    private String[] playerNames = {"Player 1", "Player 2", "Player 3", "Player 4"};
    private Color[] playerColors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
    private int[] playerPositions = {0, 10, 20, 30};
    private int[] playerMoney = {1500, 1500, 1500, 1500};
    
    /**
     * Constructor that initializes and displays the GUI.
     */
    public TestMonopolyGUI() {
        initializeColors();
        createMainFrame();
        createBoardPanel();
        createControlPanel();
        createPlayerInfoPanel();
        
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
    
    /**
     * Initialize property colors for the board.
     */
    private void initializeColors() {
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
    }
    
    /**
     * Create and configure the main application frame.
     */
    private void createMainFrame() {
        mainFrame = new JFrame("Monopoly Game - Test GUI");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        
        gamePanel = new JPanel(new BorderLayout());
        mainFrame.add(gamePanel);
    }
    
    /**
     * Create the board panel with all spaces.
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
        boardPanel.setBackground(new Color(226, 240, 217)); // Light green background
        
        gamePanel.add(boardPanel, BorderLayout.CENTER);
    }
    
    /**
     * Draw the Monopoly board with all spaces.
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
    }
    
    /**
     * Draw the four corner spaces of the board.
     */
    private void drawCornerSpaces(Graphics2D g2d) {
        // GO (bottom right)
        g2d.setColor(Color.WHITE);
        g2d.fillRect(BOARD_SIZE - SPACE_HEIGHT, BOARD_SIZE - SPACE_WIDTH, SPACE_HEIGHT, SPACE_WIDTH);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(BOARD_SIZE - SPACE_HEIGHT, BOARD_SIZE - SPACE_WIDTH, SPACE_HEIGHT, SPACE_WIDTH);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("GO", BOARD_SIZE - SPACE_HEIGHT/2 - 15, BOARD_SIZE - SPACE_WIDTH/2 + 5);
        
        // Jail (bottom left)
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, BOARD_SIZE - SPACE_WIDTH, SPACE_HEIGHT, SPACE_WIDTH);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, BOARD_SIZE - SPACE_WIDTH, SPACE_HEIGHT, SPACE_WIDTH);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("JAIL", SPACE_HEIGHT/2 - 15, BOARD_SIZE - SPACE_WIDTH/2 + 5);
        
        // Free Parking (top left)
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, SPACE_HEIGHT, SPACE_WIDTH);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, SPACE_HEIGHT, SPACE_WIDTH);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("FREE", SPACE_HEIGHT/2 - 20, SPACE_WIDTH/2);
        g2d.drawString("PARKING", SPACE_HEIGHT/2 - 30, SPACE_WIDTH/2 + 15);
        
        // Go To Jail (top right)
        g2d.setColor(Color.WHITE);
        g2d.fillRect(BOARD_SIZE - SPACE_HEIGHT, 0, SPACE_HEIGHT, SPACE_WIDTH);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(BOARD_SIZE - SPACE_HEIGHT, 0, SPACE_HEIGHT, SPACE_WIDTH);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("GO TO", BOARD_SIZE - SPACE_HEIGHT/2 - 20, SPACE_WIDTH/2);
        g2d.drawString("JAIL", BOARD_SIZE - SPACE_HEIGHT/2 - 15, SPACE_WIDTH/2 + 15);
    }
    
    /**
     * Draw horizontal spaces (top and bottom rows).
     */
    private void drawHorizontalSpaces(Graphics2D g2d, boolean bottom) {
        int y = bottom ? BOARD_SIZE - SPACE_WIDTH : 0;
        
        for (int i = 1; i < 10; i++) {
            int x = BOARD_SIZE - SPACE_HEIGHT - i * SPACE_HEIGHT;
            if (bottom && (i == 2 || i == 4 || i == 7 || i == 9)) {
                // Community Chest and Chance spaces
                g2d.setColor(new Color(255, 245, 213)); // Light yellow
            } else if (!bottom && (i == 2 || i == 5 || i == 7 || i == 8)) {
                // Community Chest and Chance spaces
                g2d.setColor(new Color(255, 245, 213)); // Light yellow
            } else {
                // Get property color or use default white
                String propertyName = getPropertyNameForPosition(bottom ? i : i + 20);
                g2d.setColor(propertyColors.containsKey(propertyName) ? 
                        propertyColors.get(propertyName) : Color.WHITE);
            }
            
            // Draw the space
            g2d.fillRect(x, y, SPACE_HEIGHT, SPACE_WIDTH);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, SPACE_HEIGHT, SPACE_WIDTH);
            
            // Add color bar for properties
            if (propertyColors.containsKey(getPropertyNameForPosition(bottom ? i : i + 20))) {
                int barHeight = SPACE_WIDTH / 5;
                if (bottom) {
                    g2d.setColor(g2d.getColor().darker());
                    g2d.fillRect(x, y, SPACE_HEIGHT, barHeight);
                } else {
                    g2d.setColor(g2d.getColor().darker());
                    g2d.fillRect(x, y + SPACE_WIDTH - barHeight, SPACE_HEIGHT, barHeight);
                }
            }
        }
    }
    
    /**
     * Draw vertical spaces (left and right columns).
     */
    private void drawVerticalSpaces(Graphics2D g2d, boolean left) {
        int x = left ? 0 : BOARD_SIZE - SPACE_HEIGHT;
        
        for (int i = 1; i < 10; i++) {
            int y = BOARD_SIZE - SPACE_WIDTH - i * SPACE_WIDTH;
            if (left && (i == 2 || i == 5 || i == 7 || i == 8)) {
                // Community Chest and Chance spaces
                g2d.setColor(new Color(255, 245, 213)); // Light yellow
            } else if (!left && (i == 2 || i == 4 || i == 7 || i == 9)) {
                // Community Chest and Chance spaces
                g2d.setColor(new Color(255, 245, 213)); // Light yellow
            } else {
                // Get property color or use default white
                String propertyName = getPropertyNameForPosition(left ? i + 10 : i + 30);
                g2d.setColor(propertyColors.containsKey(propertyName) ? 
                        propertyColors.get(propertyName) : Color.WHITE);
            }
            
            // Draw the space
            g2d.fillRect(x, y, SPACE_HEIGHT, SPACE_WIDTH);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, SPACE_HEIGHT, SPACE_WIDTH);
            
            // Add color bar for properties
            if (propertyColors.containsKey(getPropertyNameForPosition(left ? i + 10 : i + 30))) {
                int barWidth = SPACE_HEIGHT / 5;
                if (left) {
                    g2d.setColor(g2d.getColor().darker());
                    g2d.fillRect(x + SPACE_HEIGHT - barWidth, y, barWidth, SPACE_WIDTH);
                } else {
                    g2d.setColor(g2d.getColor().darker());
                    g2d.fillRect(x, y, barWidth, SPACE_WIDTH);
                }
            }
        }
    }
    
    /**
     * Draw player tokens on the board.
     */
    private void drawPlayers(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (int i = 0; i < playerNames.length; i++) {
            // Calculate player position coordinates
            int[] coordinates = getCoordinatesForPosition(playerPositions[i], i);
            
            // Draw player token
            g2d.setColor(playerColors[i]);
            g2d.fillOval(coordinates[0], coordinates[1], 20, 20);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(coordinates[0], coordinates[1], 20, 20);
        }
    }
    
    /**
     * Create the control panel with game action buttons.
     */
    private void createControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(1, 4, 10, 0));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Roll dice button
        JButton rollDiceButton = new JButton("Roll Dice");
        rollDiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Simulate rolling dice
                int dice1 = (int)(Math.random() * 6) + 1;
                int dice2 = (int)(Math.random() * 6) + 1;
                JOptionPane.showMessageDialog(mainFrame, 
                        "You rolled: " + dice1 + " and " + dice2 + " = " + (dice1 + dice2),
                        "Dice Roll", JOptionPane.INFORMATION_MESSAGE);
                
                // Move active player (just for demonstration)
                playerPositions[0] = (playerPositions[0] + dice1 + dice2) % 40;
                boardPanel.repaint();
            }
        });
        
        // End turn button
        JButton endTurnButton = new JButton("End Turn");
        endTurnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(mainFrame, "Turn ended", 
                        "End Turn", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // Buy property button
        JButton buyPropertyButton = new JButton("Buy Property");
        buyPropertyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(mainFrame, 
                        "Property purchased!", "Purchase", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // Trade button
        JButton tradeButton = new JButton("Trade");
        tradeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(mainFrame, 
                        "Trade initiated!", "Trade", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        controlPanel.add(rollDiceButton);
        controlPanel.add(endTurnButton);
        controlPanel.add(buyPropertyButton);
        controlPanel.add(tradeButton);
        
        gamePanel.add(controlPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create the player info panel to display player stats.
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
        
        // Add player info
        for (int i = 0; i < playerNames.length; i++) {
            JPanel playerPanel = new JPanel();
            playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
            playerPanel.setBorder(BorderFactory.createLineBorder(playerColors[i], 2));
            playerPanel.setMaximumSize(new Dimension(200, 100));
            
            JLabel nameLabel = new JLabel(playerNames[i]);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            nameLabel.setForeground(playerColors[i]);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel moneyLabel = new JLabel("Money: $" + playerMoney[i]);
            moneyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel posLabel = new JLabel("Position: " + playerPositions[i]);
            posLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            playerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            playerPanel.add(nameLabel);
            playerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            playerPanel.add(moneyLabel);
            playerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            playerPanel.add(posLabel);
            playerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            
            playerInfoPanel.add(playerPanel);
            playerInfoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        gamePanel.add(playerInfoPanel, BorderLayout.EAST);
    }
    
    /**
     * Helper method to get coordinates for a player token based on their position.
     */
    private int[] getCoordinatesForPosition(int position, int playerOffset) {
        int[] coordinates = new int[2];
        
        // Calculate the base coordinates for each position
        if (position >= 0 && position < 10) {
            // Bottom row
            coordinates[0] = BOARD_SIZE - SPACE_HEIGHT - (position + 1) * SPACE_HEIGHT + SPACE_HEIGHT/2 - 10;
            coordinates[1] = BOARD_SIZE - SPACE_WIDTH/2 - 10;
        } else if (position >= 10 && position < 20) {
            // Left column
            coordinates[0] = SPACE_HEIGHT/2 - 10;
            coordinates[1] = BOARD_SIZE - SPACE_WIDTH - (position - 9) * SPACE_WIDTH + SPACE_WIDTH/2 - 10;
        } else if (position >= 20 && position < 30) {
            // Top row
            coordinates[0] = SPACE_HEIGHT + (position - 20) * SPACE_HEIGHT + SPACE_HEIGHT/2 - 10;
            coordinates[1] = SPACE_WIDTH/2 - 10;
        } else {
            // Right column
            coordinates[0] = BOARD_SIZE - SPACE_HEIGHT/2 - 10;
            coordinates[1] = SPACE_WIDTH + (position - 30) * SPACE_WIDTH + SPACE_WIDTH/2 - 10;
        }
        
        // Add small offset based on player number to prevent overlap
        coordinates[0] += (playerOffset % 2) * 10;
        coordinates[1] += (playerOffset / 2) * 10;
        
        return coordinates;
    }
    
    /**
     * Helper method to get property name for a position.
     * This is a simplified version for testing.
     */
    private String getPropertyNameForPosition(int position) {
        switch (position) {
            case 1: return "Mediterranean Avenue";
            case 3: return "Baltic Avenue";
            case 6: return "Oriental Avenue";
            case 8: return "Vermont Avenue";
            case 9: return "Connecticut Avenue";
            case 11: return "St. Charles Place";
            case 13: return "States Avenue";
            case 14: return "Virginia Avenue";
            case 16: return "St. James Place";
            case 18: return "Tennessee Avenue";
            case 19: return "New York Avenue";
            case 21: return "Kentucky Avenue";
            case 23: return "Indiana Avenue";
            case 24: return "Illinois Avenue";
            case 26: return "Atlantic Avenue";
            case 27: return "Ventnor Avenue";
            case 29: return "Marvin Gardens";
            case 31: return "Pacific Avenue";
            case 32: return "North Carolina Avenue";
            case 34: return "Pennsylvania Avenue";
            case 37: return "Park Place";
            case 39: return "Boardwalk";
            default: return "";
        }
    }
    
    /**
     * Main method to start the application.
     */
    public static void main(String[] args) {
        // Use the Event Dispatch Thread for Swing applications
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TestMonopolyGUI();
            }
        });
    }
}