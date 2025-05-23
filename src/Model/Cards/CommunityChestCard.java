/*
 * CSCI 234: Intro to Software Engineering
 * Group: Giovanny, Jamell, Matt, Deborah
 * Purpose: This class is responsible for creating the Community Chest card deck
 * and preloading the cards
 * Team Member(s) responsible: Jamell
 * */

package Model.Cards;

import Model.Board.Banker;
import Model.Board.Player;
import Model.Exceptions.PlayerNotFoundException;

import java.util.ArrayList;
import java.util.Collections;

public class CommunityChestCard extends Card {

    private static CommunityChestCard instance;
    private ArrayList<String> communityChanceDeck;
    private Banker banker;

    private CommunityChestCard() {
        super("Community Chest Card");
        communityChanceDeck = new ArrayList<>();
        banker = Banker.getInstance();
        preloadCards();
    }

   /**
     * This method is used to get the instance of the CommunityChestCard class
     *
     * @return CommunityChestCard
     * Team member(s) responsible: Jamell
     */
    public static CommunityChestCard getInstance() {
        if (instance == null) {
            instance = new CommunityChestCard();
        }
        return instance;
    }

    /**
     * This method returns the card type
     *
     * @return CardType
     * Team member(s) responsible: Jamell
     */
    @Override
    public CardType getCardType() {
        return CardType.Community_Chest;
    }

    /**
     * This method returns the card deck
     *
     * @return ArrayList<String>
     * Team member(s) responsible: Jamell
     */
    @Override
    public ArrayList<String> getCardDeck() {
        return communityChanceDeck;
    }

    /**
     * This method preloads the cards
     * Team member(s) responsible: Jamell
     */
    private void preloadCards() {
        communityChanceDeck.add("Advance to Go (Collect $200)");
        communityChanceDeck.add("Bank error in your favor. Collect $200");
        communityChanceDeck.add("Doctor’s fee. Pay $50");
        communityChanceDeck.add("From sale of stock you get $50");
        communityChanceDeck.add("Get Out of Jail Free");
        communityChanceDeck.add("Go to Jail. Go directly to jail, do not pass Go, do not collect $200");
        communityChanceDeck.add("Holiday fund matures. Receive $100");
        communityChanceDeck.add("Income tax refund. Collect $20");
        communityChanceDeck.add("It is your birthday. Collect $10 from every player");
        communityChanceDeck.add("Life insurance matures. Collect $100");
        communityChanceDeck.add("Pay hospital fees of $100");
        communityChanceDeck.add("Pay school fees of $50");
        communityChanceDeck.add("Receive $25 consultancy fee");
        communityChanceDeck.add("You are assessed for street repair. $40 per house. $115 per hotel");
        communityChanceDeck.add("You have won second prize in a beauty contest. Collect $10");
        communityChanceDeck.add("You inherit $100");
    }

    /**
     * This method is used to draw a card from the community chest deck. Checks if the deck is empty first.
     *
     * @return String
     * Team member(s) responsible: Jamell
     */
    public String drawCard() {
        if (!communityChanceDeck.isEmpty()) {
            return communityChanceDeck.remove(0);
        }
        return "No more cards in the deck.";
    }

    /**
     * This method is used to shuffle the community chest deck.
     * Team member(s) responsible: Jamell
     */
    public void shuffleDeck() {
        Collections.shuffle(communityChanceDeck);
    }

    /**
     * This method is used to restore the community chest deck to its original state.
     * Team member(s) responsible: Jamell
     */
    public void cardRestore() {
        preloadCards();
        shuffleDeck();
    }

    /**
     * This method is used to use a card. It is not implemented yet.
     * Team member(s) responsible: Jamell
     */
    public void useCard(String message, Player player) throws PlayerNotFoundException {
        switch (message) {
            case "Advance to Go (Collect $200)":
                int goSteps = calculateSteps(player.getPosition(), 0);
                player.move(player, goSteps);
                banker.deposit(player,200);
                break;

            case "Bank error in your favor. Collect $200":
                banker.deposit(player, 200);
                break;

            case "Doctor's fee. Pay $50":
                banker.withdraw(player, 50);
                break;

            case "From sale of stock you get $50":
                banker.deposit(player, 50);
                break;

            case "Get Out of Jail Free":
                player.addGetOutOfJailFreeCard();
                break;

            case "Go to Jail. Go directly to jail, do not pass Go, do not collect $200":
                int jailSteps = calculateSteps(player.getPosition(), 10);
                player.move(player, jailSteps);
                player.setInJail(true);
                break;

            case "Holiday fund matures. Receive $100":
                banker.deposit(player, 100);
                break;

            case "Income tax refund. Collect $20":
                banker.deposit(player, 20);
                break;

//            case "It is your birthday. Collect $10 from every player":
//                List<Player> otherPlayers = game.getOtherPlayers(player);
//                for (Player otherPlayer : otherPlayers) {
//                    banker.transferMoney(otherPlayer, player, 10);
//                }
//                break;

            case "Life insurance matures. Collect $100":
                banker.deposit(player, 100);
                break;

            case "Pay hospital fees of $100":
                banker.withdraw(player, 100);
                break;

            case "Pay school fees of $50":
                banker.withdraw(player, 50);
                break;

            case "Receive $25 consultancy fee":
                banker.deposit(player, 25);
                break;

//            case "You are assessed for street repair. $40 per house. $115 per hotel":
//                int houses = banker.getPlayerProperties(player).getHouseCount();
//                int hotels = banker.getPlayerProperties(player).getHotelCount();
//                int repairCost = (houses * 40) + (hotels * 115);
//                banker.withdraw(player, repairCost);
//                break;

            case "You have won second prize in a beauty contest. Collect $10":
                banker.deposit(player, 10);
                break;

            case "You inherit $100":
                banker.deposit(player, 100);
                break;

            default:
                System.out.println("Unknown Community Chest card: " + message);
                break;
        }
    }

    /**
     * This method is used to reset the CommunityChestCard class.
     * Team member(s) responsible: Jamell
     */
    public static void reset() {
        instance = null;
    }

    /**
     * This method is used to calculate the steps to move.
     * Team member(s) responsible: Jamell
     */
    private int calculateSteps(int current, int target) {
        return (target - current + 40) % 40;
    }

    /**
     * This method is used to figure out if the player passes Go.
     * Team member(s) responsible: Jamell
     */
    private boolean passesGo(int current, int steps) {
        return (current + steps) >= 40;
    }

}
