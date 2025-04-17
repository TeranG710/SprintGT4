package Model.Property;

import Model.Board.Banker;
import Model.Board.GameBoard;
import Model.Board.HumanPlayer;
import Model.Exceptions.PlayerNotFoundException;
import Model.Board.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PropertyTest {

    private Property property;
    private ColorGroup colorGroup;
    private Player owner;
    private Player otherPlayer;
    private Banker banker;
    private Property boardwalk;
    private Property parkPlace;

    @BeforeEach
    public void setUp() throws PlayerNotFoundException {
        GameBoard.resetInstance();
        Banker.reset();
        banker = Banker.getInstance();
        GameBoard board = GameBoard.getInstance();
        owner = new HumanPlayer("TestOwner", board);
        otherPlayer = new HumanPlayer("TestPlayer", board);
        banker.addPlayer(owner);
        banker.addPlayer(otherPlayer);
        colorGroup = new ColorGroup(PropertyColor.DARK_BLUE, 2);
        boardwalk = new Property(
                "Boardwalk",
                39,
                400,
                50,
                new int[]{200, 600, 1400, 1700},
                2000,
                200,
                PropertyColor.DARK_BLUE,
                colorGroup);
        parkPlace = new Property(
                "Park Place",
                37,
                350,
                35,
                new int[]{175, 500, 1100, 1300},
                1500,
                175,
                PropertyColor.DARK_BLUE,
                colorGroup
        );
    }

    @Test
    public void testConstructor() {
        assertEquals("Boardwalk", boardwalk.getName());
        assertEquals(39, boardwalk.getPosition());
        assertEquals(400, boardwalk.getPurchasePrice());
        assertNull(boardwalk.getOwner());
        assertFalse(boardwalk.isMortgaged());
        assertEquals(0, boardwalk.getNumHouses());
        assertFalse(boardwalk.hasHotel());
    }

    @Test
    public void testHousePriceByColor() {
        assertEquals(200, boardwalk.getHousePrice());
        ColorGroup brownGroup = new ColorGroup(PropertyColor.BROWN, 2);
        Property brownProperty = new Property(
                "Brown Property",
                1,
                100,
                10,
                new int[]{50, 150, 300, 450},
                500,
                50,
                PropertyColor.BROWN,
                brownGroup
        );
        assertEquals(50, brownProperty.getHousePrice());
    }


    @Test
    public void testPropertyOwnership() {
        assertNull(boardwalk.getOwner());
        boardwalk.setOwner(owner);
        assertEquals(owner, boardwalk.getOwner());
    }

    @Test
    public void testRentCalculationNoHouses() throws PlayerNotFoundException {
        boardwalk.setOwner(owner);
        assertEquals(50, boardwalk.calculateRent(otherPlayer));
    }

    @Test
    public void testRentCalculationWithMonopoly() throws PlayerNotFoundException {
        boardwalk.setOwner(owner);
        parkPlace.setOwner(owner);
        assertTrue(colorGroup.hasMonopoly(owner));
        assertEquals(100, boardwalk.calculateRent(otherPlayer));
    }

    @Test
    public void testCanBuyHouse() throws PlayerNotFoundException {
        boardwalk.setOwner(owner);
        assertFalse(boardwalk.canBuyHouse(banker));
        parkPlace.setOwner(owner);
        assertTrue(boardwalk.canBuyHouse(banker));
    }
    @Test
    public void testBuyHouse() throws PlayerNotFoundException {
        boardwalk.setOwner(owner);
        assertFalse(boardwalk.canBuyHouse(banker));
        parkPlace.setOwner(owner);
        assertTrue(boardwalk.canBuyHouse(banker));
        int initialBalance = banker.getBalance(owner);
        assertTrue(boardwalk.buyHouse(banker));
        assertEquals(1, boardwalk.getNumHouses());
        assertEquals(initialBalance - boardwalk.getHousePrice(), banker.getBalance(owner));
    }

    @Test
    public void testCannotBuyHouseWithoutOwner() {
        assertFalse(boardwalk.canBuyHouse(banker));
    }

    @Test
    public void testCannotBuyHouseWhenMortgaged() throws PlayerNotFoundException {
        boardwalk.setOwner(owner);
        parkPlace.setOwner(owner);
        boardwalk.setMortgaged(true);
        assertFalse(boardwalk.canBuyHouse(banker));
    }
    @Test
    public void testBuyHotel() throws PlayerNotFoundException {
        boardwalk.setOwner(owner);
        parkPlace.setOwner(owner);
        banker.deposit(owner, 10000);
        for (int i = 0; i < 4; i++) {
            if (boardwalk.canBuyHouse(banker)) {
                assertTrue(boardwalk.buyHouse(banker));
            }
            if (parkPlace.canBuyHouse(banker)) {
                assertTrue(parkPlace.buyHouse(banker));
            }
        }
        assertEquals(4, boardwalk.getNumHouses());
        assertEquals(4, parkPlace.getNumHouses());
        assertTrue(boardwalk.canBuyHotel(banker));
        assertTrue(boardwalk.buyHotel(banker));
        assertTrue(boardwalk.hasHotel());
        assertEquals(0, boardwalk.getNumHouses());
        assertEquals(1, boardwalk.getNumHotels());
        assertTrue(parkPlace.canBuyHotel(banker));
        assertTrue(parkPlace.buyHotel(banker));
        assertTrue(parkPlace.hasHotel());
        assertEquals(0, parkPlace.getNumHouses());
        assertEquals(1, parkPlace.getNumHotels());
    }
    @Test
    public void testCannotBuyHotelWithoutOwner() {
        assertFalse(boardwalk.canBuyHotel(banker));
    }
    @Test
    public void testCannotBuyHotelWithLessThan4Houses() throws PlayerNotFoundException {
        boardwalk.setOwner(owner);
        parkPlace.setOwner(owner);
        banker.deposit(owner, 10000);
        for (int i = 0; i < 3; i++) {
            if (boardwalk.canBuyHouse(banker)) {
                assertTrue(boardwalk.buyHouse(banker));
            }
            if (parkPlace.canBuyHouse(banker)) {
                assertTrue(parkPlace.buyHouse(banker));
            }
        }
        assertEquals(3, boardwalk.getNumHouses());
        assertEquals(3, parkPlace.getNumHouses());
        assertFalse(boardwalk.canBuyHotel(banker));
        assertFalse(boardwalk.buyHotel(banker));
        assertFalse(boardwalk.hasHotel());
        assertEquals(0, boardwalk.getNumHotels());
    }
    @Test
    public void testSellHotel() throws PlayerNotFoundException {
        boardwalk.setOwner(owner);
        parkPlace.setOwner(owner);
        banker.deposit(owner, 10000);
        for (int i = 0; i < 4; i++) {
            if (boardwalk.canBuyHouse(banker)) {
                assertTrue(boardwalk.buyHouse(banker));
            }
            if (parkPlace.canBuyHouse(banker)) {
                assertTrue(parkPlace.buyHouse(banker));
            }
        }
        assertEquals(4, boardwalk.getNumHouses());
        assertEquals(4, parkPlace.getNumHouses());
        assertTrue(boardwalk.canBuyHotel(banker));
        assertTrue(boardwalk.buyHotel(banker));
        assertTrue(boardwalk.hasHotel());
        assertEquals(0, boardwalk.getNumHouses());
        assertTrue(boardwalk.sellHotel(banker));
        assertFalse(boardwalk.hasHotel());
        assertEquals(4, boardwalk.getNumHouses());
    }
    @Test
    public void testSellHouse() throws PlayerNotFoundException {
        boardwalk.setOwner(owner);
        parkPlace.setOwner(owner);
        banker.deposit(owner, 10000);
        assertTrue(boardwalk.buyHouse(banker));
        assertEquals(1, boardwalk.getNumHouses());
        assertTrue(boardwalk.sellHouse(banker));
        assertEquals(0, boardwalk.getNumHouses());
    }
    @Test
    public void testMortgageCondition() throws PlayerNotFoundException {
        boardwalk.setOwner(owner);
        assertFalse(boardwalk.isMortgaged());
        assertTrue(boardwalk.mortgage());
        assertTrue(boardwalk.isMortgaged());
    }
    @Test
    public void testCannotMortgageWithHouses() throws PlayerNotFoundException {
        boardwalk.setOwner(owner);
        parkPlace.setOwner(owner);
        banker.deposit(owner, 10000);
        for (int i = 0; i < 4; i++) {
            if (boardwalk.canBuyHouse(banker)) {
                assertTrue(boardwalk.buyHouse(banker));
            }
            if (parkPlace.canBuyHouse(banker)) {
                assertTrue(parkPlace.buyHouse(banker));
            }
        }
        assertFalse(boardwalk.canMortgage());
    }
    @Test
    public void testUnmortgage() throws PlayerNotFoundException {
        boardwalk.setOwner(owner);
        assertEquals(200, boardwalk.getMortgageValue());
        assertFalse(boardwalk.isMortgaged());
        assertTrue(boardwalk.mortgage());
        assertEquals(1700, banker.getBalance(owner));
        assertTrue(boardwalk.isMortgaged());
        assertTrue(boardwalk.unmortgage());
        assertEquals(1480, banker.getBalance(owner));
        assertFalse(boardwalk.isMortgaged());
    }
    @Test
    public void testSellHousesEvenly() throws PlayerNotFoundException {
        boardwalk.setOwner(owner);
        parkPlace.setOwner(owner);
        banker.deposit(owner, 1000);
        for (int i = 0; i < 4; i++) {
            boardwalk.buyHouse(banker);
            parkPlace.buyHouse(banker);
        }
        assertTrue(boardwalk.sellHouse(banker));
        assertEquals(3, boardwalk.getNumHouses());
        assertFalse(boardwalk.sellHouse(banker));
    }

    @Test
    public void testSellHotelEvenly() throws PlayerNotFoundException {
        boardwalk.setOwner(owner);
        parkPlace.setOwner(owner);
        banker.deposit(owner, 10000);
        for (int i = 0; i < 4; i++) {
            if (boardwalk.canBuyHouse(banker)) {
                assertTrue(boardwalk.buyHouse(banker));
            }
            if (parkPlace.canBuyHouse(banker)) {
                assertTrue(parkPlace.buyHouse(banker));
            }
        }
        assertTrue(boardwalk.buyHotel(banker));
        assertTrue(parkPlace.buyHotel(banker));
        assertTrue(boardwalk.sellHotel(banker));
        assertFalse(boardwalk.hasHotel());
        assertEquals(4, boardwalk.getNumHouses());
    }

    @Test
    public void testOnlandingWithoutOwner() throws PlayerNotFoundException {
        int intialBalance = banker.getBalance(otherPlayer);
        boardwalk.onLanding(otherPlayer);
    }

    @Test
    public void testOnLandingWithOwner() throws PlayerNotFoundException {
        boardwalk.setOwner(owner);
        int initialBalance = banker.getBalance(owner);
        int otherPlayerBalance = banker.getBalance(otherPlayer);
        boardwalk.onLanding(otherPlayer);
        assertEquals(otherPlayerBalance - boardwalk.calculateRent(otherPlayer), banker.getBalance(otherPlayer));
        assertEquals(initialBalance + boardwalk.calculateRent(otherPlayer), banker.getBalance(owner));
    }

    @Test
    public void testOnLandingOnOwnProperty() throws PlayerNotFoundException {
        boardwalk.setOwner(owner);
        int initialBalance = banker.getBalance(owner);
        boardwalk.onLanding(owner);
        assertEquals(initialBalance, banker.getBalance(owner));
    }
    @Test
    public void testLandingOnMortgagedProperty() throws PlayerNotFoundException {
        boardwalk.setOwner(owner);
        int initialBalance = banker.getBalance(owner);
        int otherPlayerBalance = banker.getBalance(otherPlayer);
        boardwalk.mortgage();
        boardwalk.onLanding(otherPlayer);
        assertEquals(initialBalance + boardwalk.getMortgageValue(), banker.getBalance(owner));
        assertEquals(otherPlayerBalance, banker.getBalance(otherPlayer));
    }

    @Test
    public void testCantUnmortgageWithoutEnoughMoney() throws PlayerNotFoundException {
        boardwalk.setOwner(owner);
        boardwalk.mortgage();
        banker.withdraw(owner, banker.getBalance(owner));
        assertFalse(boardwalk.unmortgage());
        assertTrue(boardwalk.isMortgaged());
    }

    @Test
    public void testCantUnmortgageWithoutOwner() throws PlayerNotFoundException {
        assertFalse(boardwalk.canMortgage());
        assertFalse(boardwalk.mortgage());
        boardwalk.mortgage();
        assertFalse(boardwalk.isMortgaged());
    }

    @Test
    public void testCantMortgageMortgagedProperty() throws PlayerNotFoundException {
        boardwalk.setOwner(owner);
        boardwalk.mortgage();
        assertFalse(boardwalk.canMortgage());
        assertFalse(boardwalk.mortgage());
        assertTrue(boardwalk.isMortgaged());
    }







}