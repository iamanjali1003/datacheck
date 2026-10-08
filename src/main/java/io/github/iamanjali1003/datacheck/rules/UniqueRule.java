package io.github.iamanjali1003.datacheck.rules;

import io.github.iamanjali1003.datacheck.core.Dataset;
import io.github.iamanjali1003.datacheck.core.Rule;
import io.github.iamanjali1003.datacheck.core.RuleResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Checks that no value appears twice in a column. */
public final class UniqueRule implements Rule {

    @Override
    public String name() {
        return "unique";
    }

    @Override
    public RuleResult check(Dataset data, String column, Map<String, Object> config) {
        Map<String, Integer> firstSeen = new HashMap<>();
        List<String> failures = new ArrayList<>();
        List<String> values = data.column(column);

        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            if (value.isEmpty()) {
                continue;
            }
            Integer earlier = firstSeen.putIfAbsent(value, i + 1);
            if (earlier != null) {
                failures.add("row " + (i + 1) + ": " + value + " already seen at row " + earlier);
            }
        }
        return new RuleResult(name(), column, failures);
    }
}
