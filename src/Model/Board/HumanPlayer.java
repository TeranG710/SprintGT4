package Model.Board;

import Model.Exceptions.PlayerNotFoundException;
import Model.Property.Property;
import Model.Spaces.BoardSpace;

/**
 * Represents a human player in a Monopoly game.
 * Inherits from the abstract Player class.
 * Team member(s) responsible: Matt
 */
public class HumanPlayer extends Player {
    private Banker banker;

    /**
     * Constructor for HumanPlayer.
     *
     * @param name  Player's name
     * @param board The game board
     * Team member(s) responsible: Giovanny
     */
    public HumanPlayer(String name, GameBoard board) {
        super(name, board);
        banker = Banker.getInstance();
    }

    /**
     * Buy a property from the bank.
     * @return Player's position
     * Team member(s) responsible: Jamell
     */
    @Override
    public void buyProperty(BoardSpace space, Player player) throws PlayerNotFoundException {
        banker.sellProperty(space, player);
    }

    /**
     * Sell a house on the specified space.
     * Team member(s) responsible: Jamell
     */
    @Override
    public void buyHouse(Property property, Player player) throws PlayerNotFoundException {
        banker.sellHouse(property, player);
    }


    @Override
    public void buyHotel(Property property, Player player) throws PlayerNotFoundException{
        banker.sellHotel(property, player);
    }

    /***
     * Move the player on the board.
     * This method is abstract and should be implemented by subclasses.
     * Team member(s) responsible: Jamell
     */
    // In the move method of the Player classes (both HumanPlayer and ComputerPlayer):
    @Override
    public void move(Player player, int spaces) throws PlayerNotFoundException {
        if (player == null) {
            throw new PlayerNotFoundException();
        }

        // Calculate new position with careful bounds checking
        int currentPosition = getPosition();
        int newPosition = (currentPosition + spaces) % 40;

        // Ensure position is valid
        if (newPosition < 0) {
            newPosition += 40;
        }

        // Log the move for debugging
        System.out.println(getName() + " moved from position " + currentPosition +
                " to position " + newPosition + " (+" + spaces + " spaces)");

        // Set the new position
        setPosition(newPosition);

        try {
            // Handle landing on the space
            getBoard().getBoardElements()[newPosition].onLanding(player);
        } catch (Exception e) {
            System.err.println("Error in space landing handler: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
