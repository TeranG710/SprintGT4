//package Controller;
//
//import Model.Board.Banker;
//import Model.Board.Player;
//import Model.Exceptions.InsufficientFundsException;
//import Model.Exceptions.PlayerNotFoundException;
//import Model.Property.Property;
//import View.Gui;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockedStatic;
//import java.util.ArrayList;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class BankerControllerTest {
//    private BankerController controller;
//    private Banker bankerMock;
//    private Gui guiMock;
//    private Player playerMock;
//    private Property propertyMock;
//
//    @BeforeEach
//    public void setup() {
//        bankerMock = mock(Banker.class);
//        guiMock = mock(Gui.class);
//        playerMock = mock(Player.class);
//        propertyMock = mock(Property.class);
//
//        try (MockedStatic<Banker> mockedStatic = mockStatic(Banker.class)) {
//            mockedStatic.when(Banker::getInstance).thenReturn(bankerMock);
//            controller = new BankerController();
//        }
//
//        controller.setGui(guiMock);
//    }
////
////    @Test
////    public void testGetBalance_success() throws PlayerNotFoundException {
////        when(bankerMock.getBalance(playerMock)).thenReturn(1000);
////        int balance = controller.getBalance(playerMock);
////        assertEquals(1000, balance);
////    }
////
////    @Test
////    public void testGetBalance_playerNotFound() throws PlayerNotFoundException {
////        when(bankerMock.getBalance(playerMock)).thenThrow(new PlayerNotFoundException());
////        int balance = controller.getBalance(playerMock);
////        assertEquals(0, balance);
////    }
////
////    @Test
////    public void testSellProperty_success() throws Exception {
////        when(propertyMock.getPurchasePrice()).thenReturn(500);
////        when(propertyMock.getName()).thenReturn("Boardwalk");
////        when(playerMock.getName()).thenReturn("Player");
////
////        ArrayList<Player> players = new ArrayList<>();
////        players.add(playerMock);
////        when(bankerMock.getAllPlayers()).thenReturn(players);
////
////        boolean result = controller.sellProperty(propertyMock, playerMock);
////        assertTrue(result);
////
////        verify(guiMock).displayMessage(contains("Player purchased Boardwalk"));
////    }
////
////    @Test
////    public void testSellProperty_nullArgs() {
////        assertFalse(controller.sellProperty(null, playerMock));
////        assertFalse(controller.sellProperty(propertyMock, null));
////    }
////
////    @Test
////    public void testSellProperty_failure() throws Exception {
////        doThrow(new InsufficientFundsException()).when(bankerMock).sellProperty(propertyMock, playerMock);
////
////        boolean result = controller.sellProperty(propertyMock, playerMock);
////        assertFalse(result);
////        verify(guiMock).displayMessage(contains("Purchase failed"));
////    }
////
////    @Test
////    public void testCollectRent_success() throws Exception {
////        when(propertyMock.getOwner()).thenReturn(playerMock);
////        when(playerMock.getName()).thenReturn("Player");
////
////        boolean result = controller.collectRent(propertyMock, playerMock);
////        assertTrue(result);
////        verify(guiMock).displayMessage(contains("paid rent to Player"));
////    }
////
////    @Test
////    public void testCollectRent_failure() throws Exception {
////        doThrow(new InsufficientFundsException()).when(bankerMock).collectRent(propertyMock, playerMock);
////
////        boolean result = controller.collectRent(propertyMock, playerMock);
////        assertFalse(result);
////        verify(guiMock).displayMessage(contains("Rent payment failed"));
////    }
////
////    @Test
////    public void testMortgageProperty_success() throws Exception {
////        when(propertyMock.canMortgage()).thenReturn(true);
////        when(propertyMock.getName()).thenReturn("Park Place");
////        when(propertyMock.getMortgageValue()).thenReturn(300);
////
////        boolean result = controller.mortgageProperty(propertyMock);
////        assertTrue(result);
////        verify(propertyMock).mortgage();
////        verify(guiMock).displayMessage(contains("Park Place was mortgaged"));
////    }
////
////    @Test
////    public void testMortgageProperty_failure() throws Exception {
////        when(propertyMock.canMortgage()).thenReturn(true);
////        doThrow(new PlayerNotFoundException()).when(propertyMock).mortgage();
////
////        boolean result = controller.mortgageProperty(propertyMock);
////        assertFalse(result);
////        verify(guiMock).displayMessage(contains("Mortgage failed"));
////    }
////
////    @Test
////    public void testUnmortgageProperty_success() throws Exception {
////        when(propertyMock.unmortgage()).thenReturn(true);
////        when(propertyMock.getName()).thenReturn("Park Place");
////
////        boolean result = controller.unmortgageProperty(propertyMock);
////        assertTrue(result);
////        verify(guiMock).displayMessage(contains("unmortgaged"));
////    }
////
////    @Test
////    public void testUnmortgageProperty_failure() throws Exception {
////        doThrow(new PlayerNotFoundException()).when(propertyMock).unmortgage();
////
////        boolean result = controller.unmortgageProperty(propertyMock);
////        assertFalse(result);
////        verify(guiMock).displayMessage(contains("Unmortgage failed"));
////    }
//}
