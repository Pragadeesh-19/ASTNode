package org.pragadeesh.astnode.service;

import org.pragadeesh.astnode.entity.AstNode;
import org.pragadeesh.astnode.entity.Rule;
import org.pragadeesh.astnode.repository.RuleRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class RuleService {

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

        if (ruleString.contains("AND")) {
            String[] parts = ruleString.split("AND");
            AstNode left = parseRule(parts[0].trim());
            AstNode right = parseRule(parts[1].trim());
            return new AstNode("operator", "AND", left, right);
        } else if (ruleString.contains("OR")) {
            String[] parts = ruleString.split("OR");
            AstNode left = parseRule(parts[0].trim());
            AstNode right = parseRule(parts[1].trim());
            return new AstNode("operator", "OR", left, right);
        } else {
            return new AstNode("operand", ruleString.trim());
        }
    }

    public boolean evaluateRule(UUID ruleId, Map<String, Object> userData) {
        Rule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Rule not found with id: " + ruleId));
        return evaluateAst(rule.getRootNode(), userData);
    }

    public boolean evaluateAst(AstNode node, Map<String, Object> userData) {
        if (node.getType().equals("operand")) {
            return evaluateCondition(node.getValues(), userData);
        }

        boolean leftResult = evaluateAst(node.getLeft(), userData);
        boolean rightResult = evaluateAst(node.getRight(), userData);

        return node.getValues().equals("AND") ? leftResult && rightResult : leftResult || rightResult;
    }

    private boolean evaluateCondition(String condition, Map<String, Object> userData) {
        String[] parts = condition.split(" ");
        String attribute = parts[0];
        String operator = parts[1];
        String value = parts[2];

        Object userValue = userData.get(attribute);

        switch (operator) {
            case ">":
                return Integer.parseInt(userValue.toString()) > Integer.parseInt(value);
            case "=":
                return userValue.toString().equals(value.replace("'", ""));
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }
}
