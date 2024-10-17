package org.pragadeesh.astnode.service;

import org.pragadeesh.astnode.entity.AstNode;
import org.pragadeesh.astnode.entity.Rule;
import org.pragadeesh.astnode.repository.RuleRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RuleService {

    private static final Logger logger = LoggerFactory.getLogger(RuleService.class);

    private final RuleRepository ruleRepository;

    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public Rule createRule(String ruleString, String ruleName) {
        AstNode rootNode = parseRule(ruleString); // converting ruleString into AST
        Rule rule = new Rule();
        rule.setName(ruleName);
        rule.setRootNode(rootNode);
        return ruleRepository.save(rule);
    }

    public AstNode parseRule(String ruleString) {

        ruleString = preprocessRule(ruleString);

        try {
            validateParentheses(ruleString);
        } catch (IllegalArgumentException e) {
            // Log the original error
            logger.warn("Invalid rule detected: {}", e.getMessage());

            // Attempt to balance parentheses
            String balancedRule = balanceParentheses(ruleString);
            logger.info("Attempted to balance parentheses. Original rule: '{}', New rule: '{}'", ruleString, balancedRule);

            // Validate again
            validateParentheses(balancedRule);
            ruleString = balancedRule;
        }

        // Base case: If the rule is a simple condition with no AND/OR parentheses
        if (!ruleString.contains("AND") && !ruleString.contains("OR") && !ruleString.contains("(")) {
            return new AstNode("operand", ruleString.trim());  // It's a simple condition
        }

        // Remove outermost parentheses if the rule starts and ends with parentheses
        if (ruleString.startsWith("(") && ruleString.endsWith(")")) {
            ruleString = ruleString.substring(1, ruleString.length() - 1).trim();
        }

        // Recursively split based on AND/OR operators outside parentheses
        int andIndex = findOperatorIndex(ruleString, "AND");
        if (andIndex != -1) {
            String leftPart = ruleString.substring(0, andIndex).trim();
            String rightPart = ruleString.substring(andIndex + 3).trim(); // Skip "AND"
            return new AstNode("operator", "AND", parseRule(leftPart), parseRule(rightPart));
        }

        int orIndex = findOperatorIndex(ruleString, "OR");
        if (orIndex != -1) {
            String leftPart = ruleString.substring(0, orIndex).trim();
            String rightPart = ruleString.substring(orIndex + 2).trim(); // Skip "OR"
            return new AstNode("operator", "OR", parseRule(leftPart), parseRule(rightPart));
        }

        throw new IllegalArgumentException("Invalid rule format: " + ruleString);
    }

    /**
     * Helper method to find the index of an operator (AND/OR) that is outside of parentheses.
     */
    private int findOperatorIndex(String ruleString, String operator) {
        int depth = 0;
        for (int i = 0; i < ruleString.length() - operator.length(); i++) {
            char c = ruleString.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            }
            if (depth == 0 && ruleString.startsWith(operator, i)) {
                return i;
            }
        }
        return -1;  // Operator didn't find outside parentheses
    }

    public boolean evaluateRule(UUID ruleId, Map<String, Object> userData) {
        // Fetch the rule from the database
        Rule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new IllegalArgumentException("Rule not found with id: " + ruleId));

        // Get the root node of the AST
        AstNode rootNode = rule.getRootNode();

        // Evaluate the AST using the user data
        return evaluateAst(rootNode, userData);
    }

    public boolean evaluateAst(AstNode node, Map<String, Object> userData) {

        if (node == null) {
            return false;
        }

        // If the node is an operand, evaluate the condition
        if (node.getType().equals("operand")) {
            return evaluateCondition(node.getValues(), userData);
        }

        // Recursively evaluate the left and right children
        boolean leftResult = evaluateAst(node.getLeft(), userData);
        boolean rightResult = evaluateAst(node.getRight(), userData);

        // Apply the logical operator (AND/OR)
        if (node.getValues().equals("AND")) {
            return leftResult && rightResult;
        } else if (node.getValues().equals("OR")) {
            return leftResult || rightResult;
        }

        return false;
    }

    private boolean evaluateCondition(String condition, Map<String, Object> userData) {
        String[] parts = condition.split(" ");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid condition format: " + condition);
        }
        String attribute = parts[0];
        String operator = parts[1];
        String value = parts[2];

        Object userValue = userData.get(attribute);

        if (userValue == null) {
            throw new IllegalArgumentException("Missing attribute: " + attribute);
        }

        final boolean equals = userValue.toString().equals(value.replace("'", ""));
        return switch (operator) {
            case ">" -> Integer.parseInt(userValue.toString()) > Integer.parseInt(value);
            case ">=" -> Integer.parseInt(userValue.toString()) >= Integer.parseInt(value);
            case "<" -> Integer.parseInt(userValue.toString()) < Integer.parseInt(value);
            case "<=" -> Integer.parseInt(userValue.toString()) <= Integer.parseInt(value);
            case "=", "==" -> equals;
            case "!=" -> !equals;
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }

    private void validateParentheses(String ruleString) {
        int openParenthesisCount = 0;
        StringBuilder errorDetail = new StringBuilder();

        for (int i = 0; i < ruleString.length(); i++) {
            char c = ruleString.charAt(i);
            if (c == '(') {
                openParenthesisCount++;
            } else if (c == ')') {
                if (openParenthesisCount == 0) {
                    errorDetail.append("Extra closing parenthesis at position ").append(i).append(". ");
                } else {
                    openParenthesisCount--;
                }
            }
        }

        if (openParenthesisCount > 0) {
            errorDetail.append("Missing ").append(openParenthesisCount).append(" closing parenthesis. ");
        }

        if (errorDetail.length() > 0) {
            throw new IllegalArgumentException("Unbalanced parentheses in rule: " + ruleString + ". " + errorDetail.toString());
        }
    }

    private String preprocessRule(String ruleString) {
        return ruleString.replaceAll("\\s*\\(\\s*", "(")
                .replaceAll("\\s*\\)\\s*", ")")
                .trim();
    }

    private String balanceParentheses(String ruleString) {
        int openCount = 0;
        StringBuilder balanced = new StringBuilder();

        for (char c : ruleString.toCharArray()) {
            if (c == '(') {
                openCount++;
            } else if (c == ')') {
                if (openCount > 0) {
                    openCount--;
                } else {
                    continue; // Skip extra closing parentheses
                }
            }
            balanced.append(c);
        }

        // Add any missing closing parentheses
        balanced.append(")".repeat(openCount));

        return balanced.toString();
    }
}
