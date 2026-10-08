package io.github.iamanjali1003.datacheck.rules;

import io.github.iamanjali1003.datacheck.core.Dataset;
import io.github.iamanjali1003.datacheck.core.Rule;
import io.github.iamanjali1003.datacheck.core.RuleResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Checks that a column has no empty cells. */
public final class NotNullRule implements Rule {

    @Override
    public String name() {
        return "notNull";
    }

    @Override
    public RuleResult check(Dataset data, String column, Map<String, Object> config) {
        List<String> failures = new ArrayList<>();
        List<String> values = data.column(column);

        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).isEmpty()) {
                failures.add("row " + (i + 1) + ": empty");
            }
        }
        return new RuleResult(name(), column, failures);
    }
}
