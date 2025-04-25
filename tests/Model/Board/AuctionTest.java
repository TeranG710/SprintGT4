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
        Banker.reset();
        GameBoard.resetInstance();
        banker = Banker.getInstance();
        gameBoard = GameBoard.getInstance();
        player1 = new HumanPlayer("Player1", gameBoard);
        player2 = new HumanPlayer("Player2", gameBoard);
        player3 = new HumanPlayer("Player3", gameBoard);

        banker.addPlayer(player1);
        banker.addPlayer(player2);
        banker.addPlayer(player3);
        ColorGroup colorGroup = new ColorGroup(PropertyColor.BROWN, 2);
        property = new Property("Test Property", 1, 100, 10,
                new int[]{30, 90, 270, 450}, 550, 50,
                PropertyColor.BROWN, colorGroup);
        banker.addAvailableProperty(property);

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
        BoardSpace unavailableProperty = new Property("Unavailable", 2, 150, 15,
                new int[]{30, 90, 270, 450}, 550, 50,
                PropertyColor.BROWN, new ColorGroup(PropertyColor.BROWN, 2));
        Player winner = banker.startAuction(unavailableProperty, playerList);
        assertNull(winner, "Auction for unavailable property should return null");
    }

    @Test
    public void testAuction_BasicFunctionality() throws PlayerNotFoundException {
        Player winner = banker.startAuction(property, playerList);

        if (winner != null) {
            try {
                ArrayList<BoardSpace> playerProperties = banker.getPlayerProperties(winner);
                assertTrue(playerProperties.contains(property),
                        "If auction has a winner, they should own the property");

                assertFalse(banker.getAvailableProperties().contains(property),
                        "Property should no longer be available after auction");
                int balance = banker.getBalance(winner);
                assertTrue(balance < 1500,
                        "Winner's balance should be reduced after auction");
            } catch (PlayerNotFoundException e) {
                fail("Player not found: " + e.getMessage());
            }
        } else {
            assertTrue(banker.getAvailableProperties().contains(property),
                    "Property should still be available if no auction winner");
        }
    }

}