package Model.Exceptions;

import Model.Board.Dice;
import Model.Game;
import Model.Board.Banker;
import Model.Board.HumanPlayer;
import Model.Board.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class GameExceptionTest {
    private Game game;
    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;
    private Player player5;

    @BeforeEach
    public void setUp() {
        Banker.reset();
        Game.resetInstance();
        Dice.reset();
        game = Game.getInstance();
        player1 = new HumanPlayer("Alice",game.getBoard());
        player2 = new HumanPlayer("Bob",game.getBoard());
        player3 = new HumanPlayer("Charlie",game.getBoard());
        player4 = new HumanPlayer("Dave",game.getBoard());
        player5 = new HumanPlayer("Eve",game.getBoard());
    }

    @Test
    public void addPlayerThrowsMaximumPlayerReachedException() throws Exception {
        Game game = Game.getInstance();
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addPlayer(player3);
        game.addPlayer(player4);
        assertThrows(MaximumPlayerReachedException.class, () -> game.addPlayer(player5));
    }

    @Test
    public void addPlayerThrowsPlayerAlreadyExistsException() throws Exception {
        Game game = Game.getInstance();
        game.addPlayer(player1);
        assertThrows(PlayerAlreadyExistsException.class, () -> game.addPlayer(player1));
    }

    @Test
    public void startGameThrowsNotEnoughPlayersException() {
        Game game = Game.getInstance();
        game.addPlayer(player1);
        assertThrows(NotEnoughPlayersException.class, game::startGame);
    }

    @Test
    public void startGameThrowsGameInProgressException() throws Exception {
        Game game = Game.getInstance();
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.startGame();
        assertThrows(GameInProgressException.class, game::startGame);
    }

    @Test
    public void winnerThrowsGameNotInProgressException() {
        Game game = Game.getInstance();
        assertThrows(GameNotInProgressException.class, game::winner);
    }

    @Test
    public void bankerThrowsPlayerNotFoundException() {
        Game game = Game.getInstance();
        Banker banker = Banker.getInstance();
        assertThrows(PlayerNotFoundException.class, () -> banker.getBalance(new HumanPlayer("Nonexistent", game.getBoard())));
    }

    @Test
    public void resetGameThrowsGameNotInProgressException() {
        Game game = Game.getInstance();
        assertThrows(GameNotInProgressException.class, game::resetGame);
    }

    @Test
    public void endGameThrowsGameNotInProgressException() {
        Game game = Game.getInstance();
        assertThrows(GameNotInProgressException.class, game::endGame);
    }
}