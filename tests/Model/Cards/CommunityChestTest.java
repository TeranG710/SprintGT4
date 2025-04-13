package Model.Cards;

import Model.Board.Banker;
import Model.Board.GameBoard;
import Model.Board.HumanPlayer;
import Model.Board.Player;
import Model.Exceptions.PlayerNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class CommunityChestTest {

    @BeforeEach
    public void setUp() {
        Banker.reset();
        CommunityChestCard.reset();
        GameBoard.resetInstance();
    }

    @Test
    public void TestCommunityChestDatabaseCardType() {
        CommunityChestCard communityChestCard = CommunityChestCard.getInstance();
        Assertions.assertEquals(CardType.Community_Chest, communityChestCard.getCardType());
    }

    @Test
    public void TestCommunityChestDatabaseDeck() {
        CommunityChestCard communityChestCard = CommunityChestCard.getInstance();
        ArrayList<String> deckTest = new ArrayList<>();
        deckTest.add("Advance to Go (Collect $200)");
        deckTest.add("You inherit $100");
        assertEquals(deckTest.get(0), communityChestCard.getCardDeck().get(0));
        assertEquals(deckTest.get(deckTest.size() - 1), communityChestCard.getCardDeck().get(communityChestCard.getCardDeck().size() - 1));
        assertEquals(16, communityChestCard.getCardDeck().size());
    }

    @Test
    public void TestCommunityChestCardDatabaseDeckSize() {
        CommunityChestCard communityChestCard = CommunityChestCard.getInstance();
        assertEquals(16, communityChestCard.getCardDeck().size());
    }


    @Test
    public void testDrawCardFromDeck(){
        CommunityChestCard communityChestCard = CommunityChestCard.getInstance();
        communityChestCard.drawCard();
        assertEquals(15, communityChestCard.getCardDeck().size());
    }


    @Test
    public void testDrawAllCardsAndRestore(){
        CommunityChestCard communityChestCard = CommunityChestCard.getInstance();
        for (int i = 0; i < 16; i++) {
            communityChestCard.drawCard();
        }
        assertEquals(0, communityChestCard.getCardDeck().size());
        communityChestCard.cardRestore();
        assertEquals(16, communityChestCard.getCardDeck().size());
    }


    @Test
    public void testAdvancedToGoCardAction() throws PlayerNotFoundException {
        CommunityChestCard communityChestCard = CommunityChestCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        HumanPlayer player = new HumanPlayer("TestPlayer", gameBoard);
        banker.addPlayer(player);
        String card = "Advance to Go (Collect $200)";
        assertEquals("Advance to Go (Collect $200)", card);
        communityChestCard.useCard(card, player);
        assertEquals(1700, banker.getBalance(player));
        assertEquals(0, player.getPosition());
    }

    @Test
    public void testBankErrorCard() throws PlayerNotFoundException {
        CommunityChestCard communityChestCard = CommunityChestCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        HumanPlayer player = new HumanPlayer("TestPlayer", gameBoard);
        banker.addPlayer(player);
        String card = "Bank error in your favor. Collect $200";
        assertEquals("Bank error in your favor. Collect $200", card);
        communityChestCard.useCard(card, player);
        assertEquals(1700, banker.getBalance(player));
    }

    @Test
    public void testDoctorsFee() throws PlayerNotFoundException {
        CommunityChestCard communityChestCard = CommunityChestCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        HumanPlayer player = new HumanPlayer("TestPlayer", gameBoard);
        banker.addPlayer(player);
        String card = "Doctor's fee. Pay $50";
        assertEquals("Doctor's fee. Pay $50", card);
        communityChestCard.useCard(card, player);
        assertEquals(1450, banker.getBalance(player));
    }

    @Test
    public void testCardSaleOfStock() throws PlayerNotFoundException {
        CommunityChestCard communityChestCard = CommunityChestCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        HumanPlayer player = new HumanPlayer("TestPlayer", gameBoard);
        banker.addPlayer(player);
        String card = "From sale of stock you get $50";
        assertEquals("From sale of stock you get $50", card);
        communityChestCard.useCard(card, player);
        assertEquals(1550, banker.getBalance(player));
    }

    @Test
    public void testGetOutOfJailFreeCard() throws PlayerNotFoundException {
        CommunityChestCard communityChestCard = CommunityChestCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        HumanPlayer player = new HumanPlayer("TestPlayer", gameBoard);
        banker.addPlayer(player);
        String card = "Get Out of Jail Free";
        assertEquals("Get Out of Jail Free", card);
        communityChestCard.useCard(card, player);
        assertEquals(1,player.getGetOutOfJailFreeCard());
    }

    @Test
    public void testGoToJail() throws PlayerNotFoundException {
        CommunityChestCard communityChestCard = CommunityChestCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        HumanPlayer player = new HumanPlayer("TestPlayer", gameBoard);
        banker.addPlayer(player);
        String card = "Go to Jail. Go directly to jail, do not pass Go, do not collect $200";
        assertEquals("Go to Jail. Go directly to jail, do not pass Go, do not collect $200", card);
        communityChestCard.useCard(card, player);
        assertEquals(10, player.getPosition());
        assertEquals(1500, banker.getBalance(player));
    }

    @Test
    public void testHolidayFund() throws PlayerNotFoundException {
        CommunityChestCard communityChestCard = CommunityChestCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        HumanPlayer player = new HumanPlayer("TestPlayer", gameBoard);
        banker.addPlayer(player);
        String card = "Holiday fund matures. Receive $100";
        assertEquals("Holiday fund matures. Receive $100", card);
        communityChestCard.useCard(card, player);
        assertEquals(1600, banker.getBalance(player));
    }

    @Test
    public void testIncomeTaxRefund() throws PlayerNotFoundException {
        CommunityChestCard communityChestCard = CommunityChestCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        HumanPlayer player = new HumanPlayer("TestPlayer", gameBoard);
        banker.addPlayer(player);
        String card = "Income tax refund. Collect $20";
        assertEquals("Income tax refund. Collect $20", card);
        communityChestCard.useCard(card, player);
        assertEquals(1520, banker.getBalance(player));
    }

    @Test
    public void testLifeInsurance() throws PlayerNotFoundException {
        CommunityChestCard communityChestCard = CommunityChestCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        HumanPlayer player = new HumanPlayer("TestPlayer", gameBoard);
        banker.addPlayer(player);
        String card = "Life insurance matures. Collect $100";
        assertEquals("Life insurance matures. Collect $100", card);
        communityChestCard.useCard(card, player);
        assertEquals(1600, banker.getBalance(player));
    }

    @Test
    public void testHospitalFees() throws PlayerNotFoundException {
        CommunityChestCard communityChestCard = CommunityChestCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        HumanPlayer player = new HumanPlayer("TestPlayer", gameBoard);
        banker.addPlayer(player);
        String card = "Pay hospital fees of $100";
        assertEquals("Pay hospital fees of $100", card);
        communityChestCard.useCard(card, player);
        assertEquals(1400, banker.getBalance(player));
    }

    @Test
    public void testSchoolFees() throws PlayerNotFoundException {
        CommunityChestCard communityChestCard = CommunityChestCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        HumanPlayer player = new HumanPlayer("TestPlayer", gameBoard);
        banker.addPlayer(player);
        String card = "Pay school fees of $50";
        assertEquals("Pay school fees of $50", card);
        communityChestCard.useCard(card, player);
        assertEquals(1450, banker.getBalance(player));
    }

    @Test
    public void testConsultancyFee() throws PlayerNotFoundException {
        CommunityChestCard communityChestCard = CommunityChestCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        HumanPlayer player = new HumanPlayer("TestPlayer", gameBoard);
        banker.addPlayer(player);
        String card = "Receive $25 consultancy fee";
        assertEquals("Receive $25 consultancy fee", card);
        communityChestCard.useCard(card, player);
        assertEquals(1525, banker.getBalance(player));
    }

    @Test
    public void testBeautySchool() throws PlayerNotFoundException {
        CommunityChestCard communityChestCard = CommunityChestCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        HumanPlayer player = new HumanPlayer("TestPlayer", gameBoard);
        banker.addPlayer(player);
        String card = "You have won second prize in a beauty contest. Collect $10";
        assertEquals("You have won second prize in a beauty contest. Collect $10", card);
        communityChestCard.useCard(card, player);
        assertEquals(1510, banker.getBalance(player));
    }

    @Test
    public void testInheritance() throws PlayerNotFoundException {
        CommunityChestCard communityChestCard = CommunityChestCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        HumanPlayer player = new HumanPlayer("TestPlayer", gameBoard);
        banker.addPlayer(player);
        String card = "You inherit $100";
        assertEquals("You inherit $100", card);
        communityChestCard.useCard(card, player);
        assertEquals(1600, banker.getBalance(player));
    }

    @Test
    public void testUnknownCard() throws PlayerNotFoundException {
        CommunityChestCard communityChestCard = CommunityChestCard.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Banker banker = Banker.getInstance();
        HumanPlayer player = new HumanPlayer("TestPlayer", gameBoard);
        banker.addPlayer(player);
        String card = "Unknown card";
        assertEquals("Unknown card", card);
        communityChestCard.useCard(card, player);
        assertEquals(1500, banker.getBalance(player));
    }







}