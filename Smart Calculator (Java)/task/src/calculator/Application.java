package calculator;

import calculator.objects.Command;
import calculator.objects.Equation;
import calculator.objects.Storage;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Application {
    public final Storage storage;

    public Application() {
        this.storage = new Storage();
    }

    public void run() {
        // put your code here
        try (Scanner scanner = new Scanner(System.in);) {
            while (true) {
                try {
                    String input = scanner.nextLine();

                    if (input.isEmpty()) {
                        continue;
                    }
                    if (input.startsWith("/")) {
                        Command.execute(input);
                        continue;
                    }
                    if (input.contains("=")) {
                        storage.save(input);
                        continue;
                    }
                    if (Storage.VAR_NAME_PATTERN.matcher(input).matches()) {
                        System.out.println(storage.get(input));
                        continue;
                    }

                    if (Equation.SYMBOL_PATTERN.matcher(input).find()) {
                        input = storage.replaceVars(input);
                        int calculation = new Equation(input).calc();
                        System.out.println(calculation);
                        continue;
                    }


                    throw new InputMismatchException("Invalid expression");

                } catch (Exception e) {
                    System.out.println(e.getMessage());

                }
            }
        }
    }
}
