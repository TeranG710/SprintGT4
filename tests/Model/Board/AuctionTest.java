package Model.Board;
import Model.Exceptions.PlayerNotFoundException;
import Model.Property.ColorGroup;
import Model.Property.Property;
import Model.Property.PropertyColor;
import Model.Spaces.BoardSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the auction functionality in the Banker class
 * Team member(s) responsible: Giovanny Teran
 */
public class AuctionTest {

    private Banker banker;
    private GameBoard gameBoard;
    private Player player1;
    private Player player2;
    private Player player3;
    private BoardSpace property;
    private ArrayList<Player> playerList;

    @BeforeEach
    public void setUp() {
        // Reset singletons
        Banker.reset();
        GameBoard.resetInstance();

        // Set up test objects
        banker = Banker.getInstance();
        gameBoard = GameBoard.getInstance();

        // Create test players
        player1 = new HumanPlayer("Player1", gameBoard);
        player2 = new HumanPlayer("Player2", gameBoard);
        player3 = new HumanPlayer("Player3", gameBoard);

        // Add players to banker
        banker.addPlayer(player1);
        banker.addPlayer(player2);
        banker.addPlayer(player3);

        // Create a test property
        ColorGroup colorGroup = new ColorGroup(PropertyColor.BROWN, 2);
        property = new Property("Test Property", 1, 100, 10,
                new int[]{30, 90, 270, 450}, 550, 50,
                PropertyColor.BROWN, colorGroup);
        banker.addAvailableProperty(property);

        // Create player list for auction
        playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);
        playerList.add(player3);
    }

    @Test
    public void testStartAuction_NullProperty() {
        // Test with null property
        Player winner = banker.startAuction(null, playerList);
        assertNull(winner, "Auction with null property should return null");
    }

    @Test
    public void testStartAuction_NullPlayerList() {
        // Test with null player list
        Player winner = banker.startAuction(property, null);
        assertNull(winner, "Auction with null player list should return null");
    }

    @Test
    public void testStartAuction_EmptyPlayerList() {
        // Test with empty player list
        Player winner = banker.startAuction(property, new ArrayList<>());
        assertNull(winner, "Auction with empty player list should return null");
    }

    @Test
    public void testStartAuction_PropertyNotAvailable() {
        // Create a property that's not in the available properties list
        BoardSpace unavailableProperty = new Property("Unavailable", 2, 150, 15,
                new int[]{30, 90, 270, 450}, 550, 50,
                PropertyColor.BROWN, new ColorGroup(PropertyColor.BROWN, 2));

        // Test auction for unavailable property
        Player winner = banker.startAuction(unavailableProperty, playerList);
        assertNull(winner, "Auction for unavailable property should return null");
    }

    @Test
    public void testAuction_BasicFunctionality() throws PlayerNotFoundException {
        // Run an auction
        Player winner = banker.startAuction(property, playerList);

        // The result is somewhat unpredictable due to the random bids
        // But we can verify some expected behaviors:

        // If there's a winner, the property should belong to them
        if (winner != null) {
            try {
                ArrayList<BoardSpace> playerProperties = banker.getPlayerProperties(winner);
                assertTrue(playerProperties.contains(property),
                        "If auction has a winner, they should own the property");

                // Property should no longer be available
                assertFalse(banker.getAvailableProperties().contains(property),
                        "Property should no longer be available after auction");

                // Winner should have less money than initial amount
                int balance = banker.getBalance(winner);
                assertTrue(balance < 1500,
                        "Winner's balance should be reduced after auction");
            } catch (PlayerNotFoundException e) {
                fail("Player not found: " + e.getMessage());
            }
        } else {
            // If no winner, property should still be available
            assertTrue(banker.getAvailableProperties().contains(property),
                    "Property should still be available if no auction winner");
        }
    }

    @Test
    public void testAuction_OnePlayerWithFunds() throws PlayerNotFoundException {
        // Only player1 has enough money
        banker.withdraw(player2, 1490); // Player2 has $10
        banker.withdraw(player3, 1490); // Player3 has $10

        // Run auction
        Player winner = banker.startAuction(property, playerList);

        // In this scenario, the result is more predictable
        // Either player1 wins or no one wins if player1 chose not to bid

        if (winner != null) {
            assertEquals(player1, winner, "Only player with funds should win");

            // Check property ownership
            ArrayList<BoardSpace> playerProperties = banker.getPlayerProperties(player1);
            assertTrue(playerProperties.contains(property),
                    "Winner should own the property");
        } else {
            // If no winner (player1 chose not to bid), property remains available
            assertTrue(banker.getAvailableProperties().contains(property),
                    "Property should remain available if no one bids");
        }
    }
}