package io.github.iamanjali1003.datacheck.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/** Applies the rules named in a spec to a dataset. */
public final class Engine {

    private final Map<String, Rule> rules = new HashMap<>();

    public Engine() {
        for (Rule rule : ServiceLoader.load(Rule.class)) {
            rules.put(rule.name(), rule);
        }
    }

    public Report run(Dataset data, Spec spec, String source) {
        List<RuleResult> results = new ArrayList<>();

        for (var columnEntry : spec.columns().entrySet()) {
            String column = columnEntry.getKey();

            if (!data.has(column)) {
                results.add(new RuleResult("column", column,
                        List.of("column is missing from the data")));
                continue;
            }

            for (var ruleEntry : columnEntry.getValue().entrySet()) {
                String ruleName = ruleEntry.getKey();
                Rule rule = rules.get(ruleName);

                if (rule == null) {
                    results.add(new RuleResult(ruleName, column,
                            List.of("unknown rule: " + ruleName)));
                    continue;
                }

                Map<String, Object> config = ruleEntry.getValue();
                results.add(rule.check(data, column, config == null ? Map.of() : config));
            }
        }
        return new Report(source, results);
    }

    public List<String> availableRules() {
        return rules.keySet().stream().sorted().toList();
    }
}
