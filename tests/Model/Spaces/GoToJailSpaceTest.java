package Model.Spaces;

import Model.Board.Banker;
import Model.Board.GameBoard;
import Model.Board.HumanPlayer;
import Model.Board.Player;
import Model.Exceptions.PlayerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GoToJailSpaceTest {

    private GoToJailSpace goToJailSpace;
    private Player player;
    private GameBoard gameBoard;
    private Banker banker;

    @BeforeEach
    void setUp() {
        Banker.reset();
        GameBoard.resetInstance();
        gameBoard = GameBoard.getInstance();
        banker = Banker.getInstance();
        goToJailSpace = new GoToJailSpace(30);
        player = new HumanPlayer("TestPlayer", gameBoard);
        banker.addPlayer(player);
    }

    @Test
    void getPurchasePrice() {
        assertEquals(0, goToJailSpace.getPurchasePrice());
    }

    @Test
    void setOwner() {
        goToJailSpace.setOwner(player);
        assertNull(goToJailSpace.getOwner());
    }

    @Test
    void calculateRent() {
        assertEquals(0, goToJailSpace.calculateRent(player));
    }

    @Test
    void getOwner() {
        assertNull(goToJailSpace.getOwner());
    }

    @Test
    void onLanding() throws PlayerNotFoundException {
        player.setPosition(30);
        goToJailSpace.onLanding(player);
        assertEquals(10, player.getPosition());
        assertTrue(player.isInJail());
    }

    @Test
    void onPassing() {
        int initialPosition = player.getPosition();
        boolean initialJailStatus = player.isInJail();

        goToJailSpace.onPassing(player);

        assertEquals(initialPosition, player.getPosition());
        assertEquals(initialJailStatus, player.isInJail());
    }
}