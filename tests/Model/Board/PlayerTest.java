package Model.Board;

import static org.junit.jupiter.api.Assertions.*;

import Model.Exceptions.PlayerNotFoundException;
import Model.Game;
import Model.Property.ColorGroup;
import Model.Property.Property;
import Model.Property.PropertyColor;
import Model.Spaces.Railroad;
import Model.Spaces.TaxSpace;
import Model.Spaces.UtilitySpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class PlayerTest{

    @BeforeEach
    public void setUp() {
        Banker.reset();
        GameBoard.resetInstance();
        Game.resetInstance();
    }


    @Test
    public void testPlayerConstructor() {
        Banker banker = Banker.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Player player = new HumanPlayer("TestPlayer", gameBoard);
        assertEquals("TestPlayer", player.getName());
        assertFalse(player.isInJail());
        assertEquals(0, player.getPosition());
    }

    @Test
    public void testPlayerMoveToProperty() throws PlayerNotFoundException {
        Banker banker = Banker.getInstance();
        GameBoard gameBoard =GameBoard.getInstance();
        Player player = new HumanPlayer("TestPlayer", gameBoard);
        banker.addPlayer(player);
        Token token = new Token("TestToken");
        player.setTokenToPlayer(token);
        player.move(player, 1);
        assertEquals(1, player.getPosition());
        Property property = (Property) gameBoard.getBoardElements()[1];
        banker.addAvailableProperty(property);
        banker.sellProperty(property,player);
        property.onLanding(player);
        assertEquals(1440, banker.getBalance(player));
    }

    @Test
    public void testPlayerMoveToRailRoad() throws PlayerNotFoundException {
        Banker banker = Banker.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Player player = new HumanPlayer("TestPlayer", gameBoard);
        Token token = new Token("TestToken");
        player.setTokenToPlayer(token);
        banker.addPlayer(player);
        assertEquals(1500, banker.getBalance(player));
        assertEquals(0, player.getPosition());
        player.move(player, 5);
        assertEquals(5, player.getPosition());
        Railroad railroad = (Railroad) gameBoard.getBoardElements()[5];
        banker.addAvailableProperty(railroad);
        banker.sellProperty(railroad, player);
        assertEquals(1300, banker.getBalance(player));
    }

    @Test
    public void testPlayerMoveToUtility() throws PlayerNotFoundException {
        Banker banker = Banker.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Player player = new HumanPlayer("TestPlayer", gameBoard);
        Token token = new Token("TestToken");
        player.setTokenToPlayer(token);
        banker.addPlayer(player);
        player.move(player, 12);
        UtilitySpace utilitySpace = (UtilitySpace) gameBoard.getBoardElements()[12];
        banker.addAvailableProperty(utilitySpace);
        banker.sellProperty(utilitySpace, player);
        assertEquals(12, player.getPosition());
        assertEquals(1350, banker.getBalance(player));
    }

    @Test
    public void testPlayerMoveToJailVisiting() throws PlayerNotFoundException {
        Banker banker = Banker.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Player player = new HumanPlayer("TestPlayer", gameBoard);
        Token token = new Token("TestToken");
        player.setTokenToPlayer(token);
        banker.addPlayer(player);
        player.move(player,1);
        assertEquals(1,player.getPosition());
        Property property = (Property) gameBoard.getBoardElements()[1];
        banker.addAvailableProperty(property);
        banker.sellProperty(property,player);
        assertEquals(1440,banker.getBalance(player));
        player.move(player,9);
        assertEquals(10,player.getPosition());
        assertEquals("Jail / Just Visiting", gameBoard.getBoardElements()[10].getName());
    }


    //luxury tax
    @Test
    public void testPlayerMoveToTaxSpace() throws PlayerNotFoundException {
        Banker banker = Banker.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Player player = new HumanPlayer("TestPlayer", gameBoard);
        Token token = new Token("TestToken");
        player.setTokenToPlayer(token);
        banker.addPlayer(player);
        player.move(player, 38);
        TaxSpace taxSpace = (TaxSpace) gameBoard.getBoardElements()[38];
        taxSpace.onLanding(player);
        assertEquals(38, player.getPosition());
        assertEquals(1425, banker.getBalance(player));
    }

    @Test
    public void testComputerPlayerName(){
        GameBoard gameBoard = GameBoard.getInstance();
        Player player = new ComputerPlayer("TestPlayer", gameBoard);
        assertEquals("Cpu", player.getName());
    }

    @Test
    public void testCpuBuyProperty() throws PlayerNotFoundException {
        Banker banker = Banker.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Player player = new ComputerPlayer("TestPlayer", gameBoard);
        Token token = new Token("TestToken");
        player.setTokenToPlayer(token);
        banker.addPlayer(player);
        Property property = new Property("TestProperty", 1, 100, 10, new int[]{50, 100, 150, 200}, 250, 50, PropertyColor.BROWN, new ColorGroup(PropertyColor.BROWN, 2));
        Property property2 = new Property("TestProperty2", 2, 100, 10, new int[]{50, 100, 150, 200}, 250, 50, PropertyColor.BROWN, new ColorGroup(PropertyColor.BROWN, 2));
        banker.addAvailableProperty(property);
        banker.addAvailableProperty(property2);
        banker.addTitleDeed(player, property);
        while(player.getProperties().size() < 2){
            player.buyProperty(property2, player);
            if(player.getProperties().size() == 2){
                break;
            }}
        assertEquals(1400, banker.getBalance(player));
        assertEquals(2, player.getProperties().size());
    }


    @Test
    public void testPlayerBuyHouse() throws PlayerNotFoundException {
        Banker banker = Banker.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Player player = new HumanPlayer("TestPlayer", gameBoard);
        Token token = new Token("TestToken");
        player.setTokenToPlayer(token);
        banker.addPlayer(player);
        player.move(player, 1);
        assertEquals(1, player.getPosition());
        Property property1 = (Property) gameBoard.getBoardElements()[1];
        banker.addAvailableProperty(property1);
        banker.sellProperty(property1, player);
        assertEquals(1440, banker.getBalance(player));
        assertEquals(1,player.getProperties().size());
        Property property = (Property) player.getProperties().get(0);
        Property matchingProperty = null;
        for (Property groupProperty : property.getColorGroup().getProperties()) {
            if (groupProperty != property) {
                groupProperty.setOwner(player);
                banker.addTitleDeed(player, groupProperty);
                matchingProperty = groupProperty;
                break;
            }
        }
        player.buyHouse(property, player);
        assertEquals(31, banker.getAvailableHouses());
    }
    @Test
    public void testPlayerBuyHouses() throws PlayerNotFoundException {
        Banker banker = Banker.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Player player = new HumanPlayer("TestPlayer", gameBoard);
        Token token = new Token("TestToken");
        player.setTokenToPlayer(token);
        banker.addPlayer(player);
        player.move(player, 1);
        Property property1 = (Property) gameBoard.getBoardElements()[1];
        banker.addAvailableProperty(property1);
        banker.sellProperty(property1, player);
        Property mediterraneanAvenue = (Property) player.getProperties().get(0);
        Property balticAvenue = null;
        for (Property property : mediterraneanAvenue.getColorGroup().getProperties()) {
            if (property != mediterraneanAvenue) {
                property.setOwner(player);
                banker.addTitleDeed(player, property);
                balticAvenue = property;
                break;
            }
        }

        assertNotNull(balticAvenue);
        int initialHouses = banker.getAvailableHouses();
        System.out.println("Initial available houses: " + initialHouses);
        for (int i = 0; i < 8; i++) {
            try {
                Property propertyToBuyHouseOn = (i % 2 == 0) ? mediterraneanAvenue : balticAvenue;
                int beforeHouses = banker.getAvailableHouses();
                player.buyHouse(propertyToBuyHouseOn, player);
                int afterHouses = banker.getAvailableHouses();
                assertEquals(beforeHouses - 1, afterHouses, "Available houses should decrease by 1");
                System.out.println("Bought house on " + propertyToBuyHouseOn.getName() + ". Available houses: " + afterHouses);
            } catch (Exception e) {
                System.out.println("Failed to buy house " + (i + 1) + ": " + e.getMessage());
                e.printStackTrace();
                fail("House purchase failed");
            }
        }
    }


    @Test
    public void testPlayerMoveToIncomeTaxSpace() throws PlayerNotFoundException {
        Banker banker = Banker.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        TaxSpace incomeTaxSpace = new TaxSpace("Income Tax", 4);
        Player player = new HumanPlayer("TestPlayer", gameBoard);
        Token token = new Token("TestToken");
        player.setTokenToPlayer(token);
        banker.addPlayer(player);
        player.move(player, 4);
        assertEquals(4, player.getPosition());
        incomeTaxSpace.onLanding(player);
        assertEquals(1350, banker.getBalance(player));
    }

    @Test
    public void testPropertyBuyProperty() throws PlayerNotFoundException {
        Banker banker = Banker.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Player player = new HumanPlayer("TestPlayer", gameBoard);
        Token token = new Token("TestToken");
        player.setTokenToPlayer(token);
        banker.addPlayer(player);
        ColorGroup colorGroup = new ColorGroup(PropertyColor.DARK_BLUE, 2);
        Property boardwalk = new Property(
                "Boardwalk",
                39,
                400,
                50,
                new int[]{200, 600, 1400, 1700},
                2000,
                200,
                PropertyColor.DARK_BLUE,
                colorGroup);
        banker.addAvailableProperty(boardwalk);
        player.buyProperty(boardwalk, player);
        assertEquals(1, player.getProperties().size());
        assertEquals(1100, banker.getBalance(player));
    }


    @Test
    public void testComputerPlayerMoveToGoSpace() throws PlayerNotFoundException {
        Banker banker = Banker.getInstance();
        GameBoard gameBoard = GameBoard.getInstance();
        Player player = new ComputerPlayer("TestPlayer", gameBoard);
        Token token = new Token("TestToken");
        player.setTokenToPlayer(token);
        banker.addPlayer(player);
        player.move(player, 3);
        Property property = (Property) gameBoard.getBoardElements()[3];
        banker.addAvailableProperty(property);
        banker.sellProperty(property, player);
        assertEquals(3, player.getPosition());
        assertEquals(1440, banker.getBalance(player));
    }


}