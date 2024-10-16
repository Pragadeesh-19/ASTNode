package org.pragadeesh.astnode.controller;

import org.pragadeesh.astnode.entity.Rule;
import org.pragadeesh.astnode.request.RuleRequest;
import org.pragadeesh.astnode.service.RuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/api/rules")
public class RuleController {

    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @PostMapping("/create")
    public ResponseEntity<Rule> createRule(@RequestBody RuleRequest ruleRequest) {
        Rule createdRule = ruleService.createRule(ruleRequest.getRuleString(), ruleRequest.getName());
        return ResponseEntity.ok(createdRule);
    }

    @PostMapping("/evaluate/{id}")
    public ResponseEntity<Boolean> evaluateRule(@PathVariable UUID id,
                                                @RequestBody Map<String, Object> userData) {
        boolean result = ruleService.evaluateRule(id, userData);
        return ResponseEntity.ok(result);
    }
}
