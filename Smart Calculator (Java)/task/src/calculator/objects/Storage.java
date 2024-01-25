package calculator.objects;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static calculator.objects.Equation.NUMBER;

public class Storage<T> {
    public static final Pattern VAR_NAME_PATTERN = Pattern.compile("[a-zA-Z]+");

    public final Map<String, T> store = new HashMap<>();

    public void save(String expression, Function<String, T> assigneeConverter) {
        String[] split = expression.split("=");
        String assigner = split[0].trim();
        String assigneeString = split[1].trim();
        if (Pattern.matches(NUMBER.pattern(), assigneeString)) {
            T assignee = assigneeConverter.apply(assigneeString);
            saveValue(assigner, assignee);
        } else {
            saveRefValue(assigner, assigneeString);
        }
    }

    /*
     * Save stored value to storage
     * e.g. n = c
     * */
    public void saveRefValue(String identifier, String assignee) {
        if (!VAR_NAME_PATTERN.matcher(identifier).matches()) {
            throw new InputMismatchException("Invalid identifier");
        }

        // right hand side is not valid name
        if (!VAR_NAME_PATTERN.matcher(assignee).matches()) {
            throw new InputMismatchException("Invalid assignment");
        }
        // right hand side is not key
        if (!store.containsKey(assignee)) {
            throw new InputMismatchException("Unknown variable");
        }

        T temp = store.get(assignee);
        store.put(identifier, temp);
    }

    /*
     * Save value T to storage
     * e.g. n = 32
     * */
    private void saveValue(String identifier, T assignment) {
        // left hand side is not valid name
        String assignmentString = assignment.toString();

        if (!VAR_NAME_PATTERN.matcher(identifier).matches()) {
            throw new InputMismatchException("Invalid identifier");
        }

        if (NUMBER.matcher(assignmentString).matches()) {
            store.put(identifier, assignment);
        }
    }

    public T get(String identifier) {
        if (!store.containsKey(identifier)) {
            throw new InputMismatchException("Unknown variable");
        }
        return store.get(identifier);
    }

    public String replaceVars(String input) {
        Matcher matcher = Storage.VAR_NAME_PATTERN.matcher(input);
        while (matcher.find()) {
            String varName = matcher.group();
            T value = store.get(varName);
            input = input.replace(varName, String.valueOf(value));
        }
        return input;
    }
}
