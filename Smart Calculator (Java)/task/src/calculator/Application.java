package calculator;

import calculator.objects.Command;
import calculator.objects.PostFixEquation;
import calculator.objects.Storage;

import java.math.BigInteger;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Application {
    public final Storage<BigInteger> storage;

    public Application() {
        this.storage = new Storage<>();
    }

    public void run() {
        // put your code here
        try (Scanner scanner = new Scanner(System.in);) {
            while (true) {
                try {
                    String input = scanner.nextLine().trim();

                    if (input.isEmpty()) {
                        continue;
                    }
                    if (input.startsWith("/")) {
                        Command.execute(input);
                        continue;
                    }
                    if (input.contains("=")) {
                        storage.save(input, BigInteger::new);
                        continue;
                    }
                    if (Storage.VAR_NAME_PATTERN.matcher(input).matches()) {
                        System.out.println(storage.get(input));
                        continue;
                    }

                    if (Pattern.compile("[+-/*]").matcher(input).find()) {
                        input = storage.replaceVars(input);
                        BigInteger calculation = PostFixEquation.calc(input);
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
