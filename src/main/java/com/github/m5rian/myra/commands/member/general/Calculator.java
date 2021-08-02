package com.github.m5rian.myra.commands.member.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Calculate a operation with multiple operators.
 *
 * @author Marian
 */
public class Calculator {

    private static final char[] operatorsToEscape = new char[]{'+', '-', '*', '/'};
    private static final List<List<Operator>> operationOrder = new ArrayList<>() {{
        add(new ArrayList<>() {{
            add(Operator.MULTIPLY);
            add(Operator.DIVIDE);
        }});
        add(new ArrayList<>() {{
            add(Operator.ADD);
            add(Operator.SUBTRACT);
        }});
    }};

    public static Calculation calculate(String operation) throws IllegalArgumentException {
        operation = operation.replaceAll("[^\\d," + Operator.getPattern().substring(1), "");

        if (operation.startsWith("-")) {
            operation = "0" + operation;
        }

        final ArrayList<Integer> digits = new ArrayList<>();
        final ArrayList<Character> operators = new ArrayList<>();

        // Get digits
        final Pattern digitPattern = Pattern.compile("\\d+"); // Create a pattern for digits
        final Matcher digitMatcher = digitPattern.matcher(operation); // Find digits in operation
        // Go through all found digits
        while (digitMatcher.find()) {
            final String number = operation.substring(digitMatcher.start(), digitMatcher.end()); // Get number out of the Matching
            digits.add(Integer.parseInt(number));
        }

        // Get operators
        final Pattern operatorPattern = Pattern.compile(Operator.getPattern()); // Create a pattern for operators out of registered operators
        final Matcher operatorMatcher = operatorPattern.matcher(operation);  // Find operators in operation
        // Go through all found digits
        while (operatorMatcher.find()) {
            char operator = operation.charAt(operatorMatcher.start()); // Get operator out of the Matching
            operators.add(operator);
        }


        while (digits.size() > 1) {
            // Do for every operation rule like brackets before multiplication/division before addition/substraction
            for (List<Operator> operatorsOfRule : operationOrder) {
                // For all left operators of the calculation
                for (int i = 0; i < operators.size(); i++) {
                    final Operator operator = Operator.getOperator(operators.get(i)); // Get current operator

                    // Operator is the right according to the roles
                    if (operatorsOfRule.contains(operator)) {
                        applyOperation(operators, digits, i, operator); // Apply current operation
                    }
                }

            }
        }

        return new Calculation(operation, digits.get(0));
    }

    private static void applyOperation(List<Character> operators, List<Integer> digits, int i, Operator operator) throws IllegalArgumentException {
        try {
            operators.remove(i); // Remove used operator
            final int result = operator.operationFunction.apply(digits.get(i), digits.get(i + 1)); // Calculate intermediate result
            digits.set(i, result); // Replace first operator with result
            digits.remove(i + 1); // Remove second operator
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    private enum Operator {
        ADD('+', new char[0], Integer::sum),
        SUBTRACT('-', new char[0], (x, y) -> x - y),
        MULTIPLY('·', new char[]{'x', '*'}, (x, y) -> x * y),
        DIVIDE(':', new char[]{'/'}, (x, y) -> x / y);


        private final char symbol;
        private final char[] aliases;
        private final BiFunction<Integer, Integer, Integer> operationFunction;

        Operator(char symbol, char[] aliases, BiFunction<Integer, Integer, Integer> operationFunction) {
            this.symbol = symbol;
            this.aliases = aliases;
            this.operationFunction = operationFunction;
        }

        /**
         * @param query The search query.
         * @return Returns a {@link Operator} which matches the given pattern. If no operator was found this method returns null.
         */
        public static Operator getOperator(char query) {
            final Optional<Operator> optionalOperator = Arrays.stream(values()).filter(operator -> {
                boolean matches = false;

                if (operator.symbol == query) matches = true; // Check main symbol
                // Check alias symbols
                for (int i = 0; i < operator.aliases.length; i++) {
                    char alias = operator.aliases[i];
                    if (alias == query) matches = true;
                }

                return matches;
            }).findFirst();
            return optionalOperator.orElse(null);
        }

        /**
         * @return Returns a pattern matching the registered operators with their aliases.
         */
        public static String getPattern() {
            final StringBuilder pattern = new StringBuilder("[");
            for (Operator operator : values()) {

                pattern.append(operator.symbol).append(",");
                for (char alias : operator.aliases) {
                    pattern.append(alias).append(",");
                }

            }

            pattern.deleteCharAt(pattern.length() - 1).append("]");
            String finalPattern = pattern.toString();
            for (char operator : operatorsToEscape) {
                finalPattern = finalPattern.replace(String.valueOf(operator), "\\" + operator);
            }
            return finalPattern;
        }
    }

    record Calculation(String calculation, Integer result) {}

}