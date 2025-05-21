package bg.sofia.uni.fmi.mjt.split.command;

import bg.sofia.uni.fmi.mjt.split.exception.user.InvalidCommandFormatException;

public interface Command {
    String INVALID_FORMAT_MESSAGE = "Invalid command format. Type \"help\" to check the correct format";
    String LOG_IN_REQUEST_MESSAGE = "Please log in first";
    String INVALID_AMOUNT_MESSAGE = "Amount has to be a number";

    String execute() throws InvalidCommandFormatException;
}
