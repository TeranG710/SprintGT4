/*
 * CSCI 234: Intro to Software Engineering
 * Group: Giovanny, Jamell, Matt, Deborah
 * Purpose: This class is a database for the chance cards in the game.
 * Team Member(s) responsible: Jamell
 * */


package Model.Cards;

import Model.Board.Banker;
import Model.Board.GameBoard;
import Model.Board.Player;
import Model.Exceptions.PlayerNotFoundException;
import Model.Spaces.Railroad;

import java.util.ArrayList;
import java.util.Collections;

public class ChanceCard extends Card {

    private static ChanceCard instance;
    private Banker banker;
    private ArrayList<String> chanceCardsDeck;

    private ChanceCard() {
        super("Chance Card");
        chanceCardsDeck = new ArrayList<>();
        banker = Banker.getInstance();
        preloadCards();
    }

    /**
     * Singleton instance of ChanceCard
     * Team member(s) responsible: Jamell
     */
    public static ChanceCard getInstance() {
        if (instance == null) {
            instance = new ChanceCard();
        }
        return instance;
    }


    /**
     * @return the type of the card
     * Team member(s) responsible: Jamell
     */
    @Override
    public CardType getCardType() {
        return CardType.Chance;
    }


    /**
     * @return the deck of the cards
     * Team member(s) responsible: Jamell
     */
    @Override
    public ArrayList<String> getCardDeck() {
        return chanceCardsDeck;
    }

    /**
     * Preload the cards in the deck
     * Team member(s) responsible: Jamell
     */
    private void preloadCards() {
        chanceCardsDeck.add("Advance to Boardwalk.");
        chanceCardsDeck.add("Advance to Go (Collect $200).");
        chanceCardsDeck.add("Advance to Illinois Avenue. If you pass Go, collect $200.");
        chanceCardsDeck.add("Advance to St. Charles Place. If you pass Go, collect $200.");
        chanceCardsDeck.add("Advance to the nearest Railroad. If unowned, you may buy it from the Bank. If owned, pay owner twice the rental to which they are otherwise entitled.");
        chanceCardsDeck.add("Advance to the nearest Railroad. If unowned, you may buy it from the Bank. If owned, pay owner twice the rental to which they are otherwise entitled.");
        chanceCardsDeck.add("Advance token to nearest Utility. If unowned, you may buy it from the Bank. If owned, throw dice and pay owner a total ten times amount thrown.");
        chanceCardsDeck.add("Bank pays you dividend of $50.");
        chanceCardsDeck.add("Get Out of Jail Free.");
        chanceCardsDeck.add("Go Back 3 Spaces.");
        chanceCardsDeck.add("Go to Jail. Go directly to Jail, do not pass Go, do not collect $200.");
        chanceCardsDeck.add("Make general repairs on all your property. For each house pay $25. For each hotel pay $100.");
        chanceCardsDeck.add("Speeding fine $15.");
        chanceCardsDeck.add("Take a trip to Reading Railroad. If you pass Go, collect $200.");
        chanceCardsDeck.add("You have been elected Chairman of the Board. Pay each player $50.");
        chanceCardsDeck.add("Your building loan matures. Collect $150.");
    }

    /**
     * This method draws a card from the chance deck. Checks if card deck is empty first.
     *
     * @return String
     * Team member(s) responsible: Jamell
     */
    public String drawCard() {
        if (!chanceCardsDeck.isEmpty()) {
            return chanceCardsDeck.remove(0);
        }
        return "No more cards in the deck.";
    }

    /**
     * This method restores the chance deck to its original state.
     * Team member(s) responsible: Jamell
     */
    public void cardRestore() {
        chanceCardsDeck = new ArrayList<>();
        preloadCards();
        shuffleDeck();
    }


    /**
     * This method shuffles the chance deck.
     * Team member(s) responsible: Jamell
     */
    public void shuffleDeck() {
        Collections.shuffle(chanceCardsDeck);
    }

    /**
     * This method is used to use the card.
     * Team member(s) responsible: Jamell
     */
    public void useCard(String message, Player player) throws PlayerNotFoundException {
        switch (message) {
            case "Advance to Boardwalk.":
                int boardwalkSteps = calculateSteps(player.getPosition(),39);
                player.move(player,boardwalkSteps);
                break;

            case "Advance to Go (Collect $200).":
                int goSteps = calculateSteps(player.getPosition(), 0);
                player.move(player,goSteps);
                banker.deposit(player, 200);
                break;

            case "Advance to Illinois Avenue. If you pass Go, collect $200.":
                int illinoisSteps = calculateSteps(player.getPosition(), 24);
                if (passesGo(player.getPosition(), illinoisSteps)) {
                    banker.deposit(player, 200);
                }
                player.move(player,illinoisSteps);
                break;

            case "Advance to St. Charles Place. If you pass Go, collect $200.":
                int stCharlesSteps = calculateSteps(player.getPosition(), 11);
                if (passesGo(player.getPosition(), stCharlesSteps)) {
                    banker.deposit(player, 200);
                }
                player.move(player,stCharlesSteps);
                break;

//            case "Advance to the nearest Railroad. If unowned, you may buy it from the Bank. If owned, pay owner twice the rental to which they are otherwise entitled.":
//                int nearestRailroadPos = findNearestRailroad(player.getPosition());
//                int rrSteps = calculateSteps(player.getPosition(), nearestRailroadPos);
//                player.move(player,rrSteps);
//                Railroad railroad = gameBoard.getBoardElements(player.getPosition());
//                if (railroad.getOwner() == null) {
//                    railroad.onPassing(player, railroad);
//                } else {
//                    Player owner = railroad.getOwner();
//                    int rent = railroad.getRent() * 2;
//                    banker.transferMoney(player, owner, rent);
//                }
//                break;

//            case "Advance token to nearest Utility. If unowned, you may buy it from the Bank. If owned, throw dice and pay owner a total ten times amount thrown.":
//                int nearestUtilityPos = findNearestUtility(player.getPosition());
//                int utilSteps = calculateSteps(player.getPosition(), nearestUtilityPos);
//                player.move(utilSteps);
//                Property utility = board.getProperty(player.getPosition());
//                if (utility.isUnowned()) {
//                    offerPropertyPurchase(player, utility);
//                } else {
//                    int diceRoll = rollDice();
//                    Player owner = utility.getOwner();
//                    banker.transferMoney(player, owner, diceRoll * 10);
//                }
//                break;

            case "Bank pays you dividend of $50.":
                banker.deposit(player, 50);
                break;

            case "Get Out of Jail Free.":
                player.addGetOutOfJailFreeCard();
                break;

//            case "Go Back 3 Spaces.":
//                player.move(player,-3);
//                break;

            case "Go to Jail. Go directly to Jail, do not pass Go, do not collect $200.":
                int jailSteps = calculateSteps(player.getPosition(), 10);
                player.move(player,jailSteps);
                player.setInJail(true);
                break;

//            case "Make general repairs on all your property. For each house pay $25. For each hotel pay $100.":
//                int houses = player.getHouseCount();
//                int hotels = player.getHotelCount();
//                banker.withdraw(player, (houses * 25) + (hotels * 100));
//                break;

            case "Speeding fine $15.":
                banker.withdraw(player, 15);
                break;

            case "Take a trip to Reading Railroad. If you pass Go, collect $200.":
                int readingSteps = calculateSteps(player.getPosition(), 5);
                if (passesGo(player.getPosition(), readingSteps)) {
                    banker.deposit(player, 200);
                }
                player.move(player,readingSteps);
                break;

//            case "You have been elected Chairman of the Board. Pay each player $50.":
//                ArrayList<Player> others = Game.
//                for (Player p : others) {
//                    banker.transferMoney(player, p, 50);
//                }
//                break;

            case "Your building loan matures. Collect $150.":
                banker.deposit(player, 150);
                break;

            default:
                System.out.println("Unknown card: " + message);
                break;
        }
    }



    /**
     * This method is used to reset the instance of ChanceCard.
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




























