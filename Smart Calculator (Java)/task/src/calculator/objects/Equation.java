package calculator.objects;

import calculator.interfaces.Calcable;

import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Equation extends EQUATION_PATTERN implements Calcable {
    private final String fullEquation;

    public Equation(String equation) {
        equation = removeParenthesis(equation);
        this.fullEquation = equation;
    }

    private String removeParenthesis(String equation) {
        Matcher matcher = EQUATION_PARENTHESIS.matcher(equation);
        if (matcher.matches()) {
            int endIndex = equation.length() - 1;
            equation = equation.substring(1, endIndex);
        }
        return equation;
    }

    public String getEquation() {
        return fullEquation;
    }

    @Override
    public int calc() {
        int result = 0;
        String equation = fullEquation;

        Matcher paranthesisMatcher = EQUATION_PARENTHESIS.matcher(equation);
        while (paranthesisMatcher.find()) {
            String string = paranthesisMatcher.group(1);
            int number = new Equation(string).calc();
            equation = equation.replace(paranthesisMatcher.group(), String.valueOf(number));
            paranthesisMatcher.reset(equation);
        }

        Matcher multiplyDivisionMatcher = MUL_DIV_EQUATION.matcher(equation);
        Matcher addMinusMatcher = ADD_MINUS_EQUATION.matcher(equation);

        if (multiplyDivisionMatcher.matches()) {
            DissectedEquation dissected = dissectEquation(equation, MUL_DIV_EQUATION);
            result = arithmeticOperation(dissected.left, dissected.middle, dissected.right);
        } else if (addMinusMatcher.matches()) {
            DissectedEquation dissected = dissectEquation(equation, ADD_MINUS_SYMBOL);
            result = arithmeticOperation(dissected.left, dissected.middle, dissected.right);
        } else {
            while (multiplyDivisionMatcher.find()) {
                String subEquationString = multiplyDivisionMatcher.group();
                result = new Equation(subEquationString).calc();
                equation = equation.replace(multiplyDivisionMatcher.group(), String.valueOf(result));
                multiplyDivisionMatcher.reset(equation);
                addMinusMatcher.reset(equation);
            }
            while (addMinusMatcher.find()) {
                String subEquationString = addMinusMatcher.group();
                result = new Equation(subEquationString).calc();
                equation = equation.replace(addMinusMatcher.group(), String.valueOf(result));
                addMinusMatcher.reset(equation);
            }
        }

        return result;
    }

    private DissectedEquation dissectEquation(String equation, Pattern pattern) {
        Matcher symbolMatcher = pattern.matcher(equation);
        symbolMatcher.find();
        String[] strings = pattern.split(equation);

        String left = strings[0].trim();
        String middle = symbolMatcher.toMatchResult().group().trim();
        String right = strings[1].trim();

        int leftInt = Integer.parseInt(left);
        String symbol = parseSymbol(middle);
        int rightInt = Integer.parseInt(right);

        return new DissectedEquation(leftInt, symbol, rightInt);
    }

    private int arithmeticOperation(int leftInt, String symbol, int rightInt) {
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
        if (Pattern.matches(INCORRECT_MULTI_SYMBOLS.pattern(), symbol)) {
            throw new RuntimeException("Invalid expression");
        }
        return symbol.length() <= 1
                ? symbol
                : Arrays.stream(symbol.split("")).reduce("", SYMBOLS_REDUCER);
    }

    record DissectedEquation(int left, String middle, int right) {
    }
}


