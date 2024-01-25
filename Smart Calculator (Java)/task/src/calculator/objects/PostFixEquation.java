package calculator.objects;

import java.math.BigInteger;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static calculator.objects.EQUATION_PATTERN.INCORRECT_MULTI_SYMBOLS;
import static calculator.objects.EQUATION_PATTERN.NUMBER;

public class PostFixEquation {
    public static String toPostFix(String infix) {
        String equation = normalizeEquation(infix);
        Deque<String> stack = infixToPostfix(equation);
        List<String> list = new ArrayList<>(stack.stream().toList());
        Collections.reverse(list);
        return String.join(" ", list);
    }

    public static BigInteger calc(String equation) {
        equation = normalizeEquation(equation);
        Deque<String> stack = infixToPostfix(equation);
        return evaluatePostfix(stack);
    }

    private static Deque<String> infixToPostfix(String equation) {
        Deque<String> postFix = new ArrayDeque<>();
        Deque<String> operators = new ArrayDeque<>();
        Matcher matcher = Pattern.compile("[+-/*^]\\D|-?\\d+|[()]").matcher(equation);
        try {
            while (matcher.find()) {
                String token = matcher.group().trim();
                if (token.isEmpty()) {
                    continue;
                }
                switch (token) {
                    case "+", "-", "/", "*", "^" -> {
                        while (!operators.isEmpty() && hasLowerImportance(token, operators.peek())) {
                            postFix.push(operators.pop());
                        }
                        operators.push(token);
                    }
                    case "(" -> operators.push(token);
                    case ")" -> {
                        while (!operators.isEmpty() && !operators.peek().equals("(")) {
                            postFix.push(operators.pop());
                        }
                        operators.pop(); // pop (
                    }
                    default -> postFix.push(token);
                }
            }
            while (!operators.isEmpty()) {
                String operator = operators.pop();
                postFix.push(operator);
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid expression");
        }

        return postFix;
    }

    public static int operatorImportance(String operator) {
        return switch (operator) {
            case "(" -> 0;
            case ")" -> 3;
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "^" -> 3;
            default -> 4;
        };
    }

    public static boolean hasLowerImportance(String operator1, String operator2) {
        int op1score = operatorImportance(operator1);
        int op2score = operatorImportance(operator2);
        return op1score <= op2score;
    }

    private static BigInteger evaluatePostfix(Deque<String> stack) {
        Deque<BigInteger> numbers = new ArrayDeque<>();
        while (!stack.isEmpty()) {
            String element = stack.removeLast();
            if (element.matches(NUMBER.pattern())) {
                BigInteger number = new BigInteger(element);
                numbers.push(number);

            } else {
                BigInteger number1 = numbers.pop();
                BigInteger number2 = numbers.pop();

                switch (element) {
                    case "+" -> numbers.push(number2.add(number1));
                    case "-" -> numbers.push(number2.subtract(number1));
                    case "*" -> numbers.push(number2.multiply(number1));
                    case "/" -> numbers.push(number2.divide(number1));
                    case "^" -> numbers.push(number2.pow(number1.intValue()));
                    default -> throw new RuntimeException("Invalid expression");
                }
            }
        }
        return numbers.pop();
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

    private static String normalizeEquation(String equation) {
        Matcher matcher = Pattern.compile("[+-/*]{2,}").matcher(equation);
        while (matcher.find()) {
            String operators = matcher.group();
            String replacement = parseOperator(operators);
            equation = equation.replace(operators, replacement);
            matcher.reset(equation);
        }
        return equation;
    }

    private static String parseOperator(String symbol) {
        if (Pattern.matches(INCORRECT_MULTI_SYMBOLS.pattern(), symbol)) {
            throw new RuntimeException("Invalid expression");
        }

        if (symbol.length() <= 1) {
            return symbol;
        }
        return Arrays.stream(symbol.split("")).reduce("", SYMBOLS_REDUCER);
    }
}


