package calculator.objects;

import calculator.interfaces.ResultAsNumber;

import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Equation implements ResultAsNumber {
    public static final Pattern SYMBOL_PATTERN = Pattern.compile("[+-]+\\D");
    public static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+");
    public static final Pattern EQUATION_PATTERN = Pattern.compile("%s\\s*%s\\s*%s".formatted(NUMBER_PATTERN, SYMBOL_PATTERN, NUMBER_PATTERN));
    public static final Pattern EQUATION_PARANTHESIS_PATTERN = Pattern.compile("\\((.+)\\)");
    private final String originalEquation;
    private String equation;

    public Equation(String equation) {
        this.originalEquation = equation;
        this.equation = equation;

    }

    public String getOriginalEquation() {
        return originalEquation;
    }

    public int calc() {
        int result = 0;
        Matcher matcher = EQUATION_PARANTHESIS_PATTERN.matcher(equation);
        while (matcher.find()) {
            String subEquationString = matcher.group(1);
            int number = new Equation(subEquationString).calc();
            equation = equation.replace(matcher.group(), String.valueOf(number));
            matcher.reset(equation);
        }
        matcher = EQUATION_PATTERN.matcher(equation);

        if (EQUATION_PATTERN.matcher(equation).matches()) {
            Matcher symbolMatcher = SYMBOL_PATTERN.matcher(equation);
            symbolMatcher.find();
            String[] strings = SYMBOL_PATTERN.split(equation);
            int leftInt = Integer.parseInt(strings[0].trim());
            String symbol = parseSymbol(symbolMatcher.toMatchResult().group().trim());
            int rightInt = Integer.parseInt(strings[1].trim());
            result = simpleEquation(leftInt, symbol, rightInt);
        } else {
            while (matcher.find()) {
                String subEquationString = matcher.group();
                result = new Equation(subEquationString).calc();
                equation = equation.replace(matcher.group(), String.valueOf(result));
                matcher.reset(equation);
            }
        }

        return result;
    }

    private int simpleEquation(int leftInt, String symbol, int rightInt) {
        return switch (symbol) {
            case "+" -> leftInt + rightInt;
            case "-" -> leftInt - rightInt;
            case "*" -> leftInt * rightInt;
            case "/" -> leftInt / rightInt;
            default -> 0;
        };
    }

    public static final BinaryOperator<String> SYMBOLS_REDUCER = (result, symbol) -> {
        if (result.isEmpty()) {
            return symbol;
        }
        if (result.equals("+") && symbol.equals("+") || result.equals("-") && symbol.equals("-")) {
            return "+";
        }
        return "-";
    };

    private static String parseSymbol(String symbol) {
        return symbol.length() <= 1
                ? symbol
                : Arrays.stream(symbol.split("")).reduce("", SYMBOLS_REDUCER);
    }
}
