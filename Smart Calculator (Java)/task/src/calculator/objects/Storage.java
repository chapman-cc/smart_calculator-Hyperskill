package calculator.objects;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static calculator.objects.Equation.NUMBER;

public class Storage {
    public static final Pattern VAR_NAME_PATTERN = Pattern.compile("[a-zA-Z]+");

    public final Map<String, Integer> store = new HashMap<>();

    public void save(String input) {
        String[] split = input.split("=");
        String assigner = split[0].trim();
        String assignee = split[1].trim();

        save(assigner, assignee);
    }

    public void save(String identifier, String assignment) {
        // left hand side is not valid name
        if (!VAR_NAME_PATTERN.matcher(identifier).matches()) {
            throw new InputMismatchException("Invalid identifier");
        }

        if (NUMBER.matcher(assignment).matches()) {
            store.put(identifier, Integer.parseInt(assignment));
            return;
        }
        // right hand side is not valid name
        if (!VAR_NAME_PATTERN.matcher(assignment).matches()) {
            throw new InputMismatchException("Invalid assignment");
        }
        // right hand side is not key
        if (!store.containsKey(assignment)) {
            throw new InputMismatchException("Unknown variable");
        }
        if (VAR_NAME_PATTERN.matcher(assignment).matches()) {
            int temp = store.get(assignment);
            store.put(identifier, temp);
        }
    }

    public int get(String identifier) {
        if (!store.containsKey(identifier)) {
            throw new InputMismatchException("Unknown variable");
        }
        return store.get(identifier);
    }

    public String replaceVars(String input) {
        Matcher matcher = Storage.VAR_NAME_PATTERN.matcher(input);
        while (matcher.find()) {
            String varName = matcher.group();
            int value = store.get(varName);
            input = input.replace(varName, String.valueOf(value));
        }
        return input;
    }
}
