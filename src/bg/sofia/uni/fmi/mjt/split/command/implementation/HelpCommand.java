package bg.sofia.uni.fmi.mjt.split.command.implementation;

import bg.sofia.uni.fmi.mjt.split.command.Command;

public class HelpCommand implements Command {

    private static final String HELP_MESSAGE = """
        Commands:
        register <username> <password> <first name> <last name>
        log-in <username> <password>
        log-out
        add-friend <friend username>
        create-group <group name> <username> <username> ... <username> at least 3 members including you
        split <amount> <username> <reason for payment>
        split-group <amount> <group name> <reason for payment>
        payed <amount> <username>
        payed-group <amount> <group name> <username>""";

    public HelpCommand() {
    }

    @Override
    public String execute() {
        return HELP_MESSAGE;
    }
}
