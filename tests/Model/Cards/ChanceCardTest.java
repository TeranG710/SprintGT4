package Model.Cards;

import Model.Board.Banker;
import Model.Board.GameBoard;
import Model.Board.HumanPlayer;
import Model.Exceptions.PlayerNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ChanceCardTest {

    @BeforeEach
    public void setUp() {
        Banker.reset();
        ChanceCard.reset();
        GameBoard.resetInstance();
    }

    @Test
    public void TestChanceCardDatabaseCardType() {
        ChanceCard chanceCard = ChanceCard.getInstance();
        Assertions.assertEquals(CardType.Chance, chanceCard.getCardType());
    }

    @Test
    public void TestChanceCardDatabaseDeck() {
        ArrayList<String> testDescriptions = new ArrayList<>();
        testDescriptions.add("Advance to Boardwalk.");
        testDescriptions.add("Your building loan matures. Collect $150.");
        ChanceCard chanceCard = ChanceCard.getInstance();
        assertEquals(testDescriptions.get(0), chanceCard.getCardDeck().get(0));
        assertEquals(testDescriptions.get(testDescriptions.size() - 1), chanceCard.getCardDeck().get(chanceCard.getCardDeck().size() - 1));        assertEquals(16, chanceCard.getCardDeck().size());
    }

    @Test
    public void TestChanceCardDatabaseDeckSize() {
        ChanceCard chanceCard = ChanceCard.getInstance();
        assertEquals(16, chanceCard.getCardDeck().size());
    }


    @Test
    public void testDrawCardFromDeck(){
        ChanceCard chanceCard = ChanceCard.getInstance();
        chanceCard.drawCard();
        assertEquals(15, chanceCard.getCardDeck().size());
    }


    @Test
    public void testDrawAllCardsAndRestore(){
        ChanceCard chanceCard = ChanceCard.getInstance();
        for (int i = 0; i < 16; i++) {
            chanceCard.drawCard();
        }
        assertEquals(0, chanceCard.getCardDeck().size());
        chanceCard.cardRestore();
        assertEquals(16, chanceCard.getCardDeck().size());
    }


    @Test
    public void testDrawCardAndLandOnBoardWalk() throws PlayerNotFoundException {
        ChanceCard chanceCard = ChanceCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        String card = "Advance to Boardwalk.";
        assertEquals("Advance to Boardwalk.", card);
        HumanPlayer player = new HumanPlayer("Player 1", gameBoard);
        banker.addPlayer(player);
        assertEquals(0, player.getPosition());
        chanceCard.useCard(card,player);
        assertEquals(39, player.getPosition());
        assertEquals(1, player.getProperties().size());
    }

   @Test
    public void testDrawCardAndLandOnGo() throws PlayerNotFoundException {
        ChanceCard chanceCard = ChanceCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        String card = "Advance to Go (Collect $200).";
        assertEquals("Advance to Go (Collect $200).", card);
        HumanPlayer player = new HumanPlayer("Player 1", gameBoard);
        banker.addPlayer(player);
        assertEquals(0, player.getPosition());
        chanceCard.useCard(card,player);
        assertEquals(0, player.getPosition());
    }

    @Test
    public void testDrawCardAndLandOnIllinois() throws PlayerNotFoundException {
        ChanceCard chanceCard = ChanceCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        String card = "Advance to Illinois Avenue. If you pass Go, collect $200.";
        assertEquals("Advance to Illinois Avenue. If you pass Go, collect $200.", card);
        HumanPlayer player = new HumanPlayer("Player 1", gameBoard);
        banker.addPlayer(player);
        assertEquals(0, player.getPosition());
        chanceCard.useCard(card,player);
        assertEquals(24, player.getPosition());
        assertEquals(1, player.getProperties().size());
    }

    @Test
    public void testDrawCardAndLandOnStCharles() throws PlayerNotFoundException {
        ChanceCard chanceCard = ChanceCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        String card = "Advance to St. Charles Place. If you pass Go, collect $200.";
        assertEquals("Advance to St. Charles Place. If you pass Go, collect $200.", card);
        HumanPlayer player = new HumanPlayer("Player 1", gameBoard);
        banker.addPlayer(player);
        assertEquals(0, player.getPosition());
        chanceCard.useCard(card,player);
        assertEquals(11, player.getPosition());
        assertEquals(1, player.getProperties().size());
    }

    @Test
    public void testDrawCardAndBankerPaysDividend() throws PlayerNotFoundException {
        ChanceCard chanceCard = ChanceCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        String card = "Bank pays you dividend of $50.";
        assertEquals("Bank pays you dividend of $50.", card);
        HumanPlayer player = new HumanPlayer("Player 1", gameBoard);
        banker.addPlayer(player);
        assertEquals(0, player.getPosition());
        chanceCard.useCard(card,player);
        assertEquals(1550, banker.getBalance(player));
    }

    @Test
    public void testDrawCardAndGetOutOfJailCard() throws PlayerNotFoundException {
        ChanceCard chanceCard = ChanceCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        String card = "Get Out of Jail Free.";
        assertEquals("Get Out of Jail Free.", card);
        HumanPlayer player = new HumanPlayer("Player 1", gameBoard);
        banker.addPlayer(player);
        assertEquals(0, player.getPosition());
        chanceCard.useCard(card,player);
        assertEquals(1, player.getGetOutOfJailFreeCard());
    }

    @Test
    public void testDrawCardAndGoToJail() throws PlayerNotFoundException {
        ChanceCard chanceCard = ChanceCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        String card = "Go to Jail. Go directly to Jail, do not pass Go, do not collect $200.";
        assertEquals("Go to Jail. Go directly to Jail, do not pass Go, do not collect $200.", card);
        HumanPlayer player = new HumanPlayer("Player 1", gameBoard);
        banker.addPlayer(player);
        assertEquals(0, player.getPosition());
        chanceCard.useCard(card,player);
        assertEquals(10, player.getPosition());
        assertTrue(player.isInJail());
    }

    @Test
    public void testDrawCardAndUseSpeedingTicket() throws PlayerNotFoundException {
        ChanceCard chanceCard = ChanceCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        String card = "Speeding fine $15.";
        assertEquals("Speeding fine $15.", card);
        HumanPlayer player = new HumanPlayer("Player 1", gameBoard);
        banker.addPlayer(player);
        assertEquals(0, player.getPosition());
        chanceCard.useCard(card,player);
        assertEquals(1485, banker.getBalance(player));
    }

    @Test
    public void testDrawCardAndGoToReadingRailroad() throws PlayerNotFoundException {
        ChanceCard chanceCard = ChanceCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        String card = "Take a trip to Reading Railroad. If you pass Go, collect $200.";
        assertEquals("Take a trip to Reading Railroad. If you pass Go, collect $200.",card);
        HumanPlayer player = new HumanPlayer("Player 1", gameBoard);
        banker.addPlayer(player);
        assertEquals(0, player.getPosition());
        chanceCard.useCard(card,player);
        assertEquals(5, player.getPosition());
        assertEquals(1, player.getProperties().size());
    }

    @Test
    public void testBuildingLoans() throws PlayerNotFoundException {
        ChanceCard chanceCard = ChanceCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        String card = "Your building loan matures. Collect $150.";
        assertEquals("Your building loan matures. Collect $150.", card);
        HumanPlayer player = new HumanPlayer("Player 1", gameBoard);
        banker.addPlayer(player);
        assertEquals(0, player.getPosition());
        chanceCard.useCard(card,player);
        assertEquals(1650, banker.getBalance(player));
    }

    @Test
    public void testUnknownCards() throws PlayerNotFoundException {
        ChanceCard chanceCard = ChanceCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        String card = "Unknown Card";
        assertEquals("Unknown Card", card);
        HumanPlayer player = new HumanPlayer("Player 1", gameBoard);
        banker.addPlayer(player);
        assertEquals(0, player.getPosition());
        chanceCard.useCard(card,player);
        assertEquals(0, player.getPosition());
    }
}