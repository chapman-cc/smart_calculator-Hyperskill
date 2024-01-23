package calculator.objects;

import java.util.InputMismatchException;

public class Command {
    public static void execute(String command) {
        Command c = new Command(command);
        c.run();
    }

    private String command;

    public Command(String command) {
        this.command = command;

    }

    private void run() {
        switch (command) {
            case "/help", "help" -> printHelp();
            case "/exit", "exit" -> handleExit();
            default -> throw new InputMismatchException("Unknown command");
        }
    }

    private void printHelp() {
        System.out.println("The program calculates the sum of numbers");
    }

    private void handleExit() {
        System.out.println("Bye!");
        System.exit(0);
    }
}
