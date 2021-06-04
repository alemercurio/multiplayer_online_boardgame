package it.polimi.ingsw.controller;

public enum Error {

    UNKNOWN_ERROR("Something bad happened..."),
    UNABLE_TO_START_A_NEW_GAME("Something went wrong..."),
    NICKNAME_TAKEN("Name already taken!"),
    INVALID_NICKNAME("Please, choose another name!"),
    INVALID_NUMBER_OF_PLAYER("Invalid number of player..."),
    UNABLE_TO_PLAY_ACTION("This action cannot be performed due to some unknown error..."),
    INVALID_CARD_SELECTION("It's not possible to buy this card, please choose another one."),
    INVALID_POSITION("Please, choose another position!"),
    FAIL_TO_GET_RESOURCES("Failed to get resources..."),
    NOT_ENOUGH_RESOURCES("Seems that you do not have enough resources..."),
    CANNOT_CONVERT_WHITE("It seems that your selection was not correct..."),
    WRONG_CONFIGURATION("It seems that you're configuration was somehow wrong..."),
    INVALID_SELECTION("Your selection does not seem correct... please try again!"),
    INVALID_ROW("Please choose a row between 1 and 3."),
    INVALID_COLUMN("Please choose a column between 1 and 4."),
    INVALID_ROW_OR_COLUMN("Please type 'row' or 'column', or 'back' to change action."),
    UNABLE_TO_PLAY_LEADER("You cannot play this Leader!");


    private final String message;

    Error(String message)
    {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
