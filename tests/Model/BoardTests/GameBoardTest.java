package Model.BoardTests;

import Model.Board.Banker;
import Model.Board.GameBoard;
import Model.Cards.ChanceCard;
import Model.Cards.CommunityChestCard;
import Model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameBoardTest {

    @BeforeEach
    public void setUp() {
        Banker.reset();
        GameBoard.resetInstance();
        Game.resetInstance();
        ChanceCard.reset();
        CommunityChestCard.reset();

    }

    @Test
    public void testBoardInitialization() {
        Banker.reset();
        Banker banker = Banker.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        assertNotNull(gameBoard);
        assertEquals(40, gameBoard.getBoardElements().length, "The board should have exactly 40 spaces.");
        assertEquals("Go", gameBoard.getBoardElements()[0].getName());
        assertEquals("Boardwalk", gameBoard.getBoardElements()[39].getName());
        assertEquals("Jail / Just Visiting", gameBoard.getBoardElements()[10].getName());
        assertEquals("Chance", gameBoard.getBoardElements()[36].getName());
    }

    @Test
    public void testBanker() {
        GameBoard gameBoard = GameBoard.getInstance();
        assertNotNull(gameBoard.getBanker());
    }

    @Test
    public void testChanceCard() {
        GameBoard gameBoard = GameBoard.getInstance();
        assertNotNull(gameBoard.getChanceCard());
    }

    @Test
    public void testCommunityChestCard() {
        GameBoard gameBoard = GameBoard.getInstance();
        assertNotNull(gameBoard.getCommunityChestCard());
    }

    @Test
    public void testDice() {
        GameBoard gameBoard = GameBoard.getInstance();
        assertNotNull(gameBoard.getDice());
    }

    @Test
    public void testGetSpace() {
        GameBoard gameBoard = GameBoard.getInstance();
        assertEquals("Go", gameBoard.getSpace(0).getName());
    }





    

}
