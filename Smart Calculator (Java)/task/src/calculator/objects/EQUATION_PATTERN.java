package calculator.objects;

import java.util.regex.Pattern;

public class EQUATION_PATTERN {
    public static final Pattern ADD_MINUS_SYMBOL = Pattern.compile("[+-]+\\D");
    public static final Pattern MUL_DIV_SYMBOL = Pattern.compile("[*\\/]");
    public static final Pattern NUMBER = Pattern.compile("-?\\d+");
    public static final Pattern ADD_MINUS_EQUATION = Pattern.compile("(%s\\s*%s\\s*%s)".formatted(NUMBER, ADD_MINUS_SYMBOL, NUMBER));
    public static final Pattern MUL_DIV_EQUATION = Pattern.compile("(%s\\s*%s\\s*%s)".formatted(NUMBER, MUL_DIV_SYMBOL, NUMBER));
    public static final Pattern EQUATION_PARENTHESIS = Pattern.compile("(\\(%s(\\s*[\\W\\D]\\s*%s)+\\))".formatted(NUMBER, NUMBER));
    public static final Pattern INCORRECT_MULTI_SYMBOLS = Pattern.compile("[*|/]{2,}");
}
