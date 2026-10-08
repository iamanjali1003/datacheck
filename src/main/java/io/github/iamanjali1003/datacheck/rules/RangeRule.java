package io.github.iamanjali1003.datacheck.rules;

import io.github.iamanjali1003.datacheck.core.Dataset;
import io.github.iamanjali1003.datacheck.core.Rule;
import io.github.iamanjali1003.datacheck.core.RuleResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Checks that every numeric value in a column falls between min and max. */
public final class RangeRule implements Rule {

    @Override
    public String name() {
        return "range";
    }

    @Override
    public RuleResult check(Dataset data, String column, Map<String, Object> config) {
        double min = toDouble(config.get("min"), Double.NEGATIVE_INFINITY);
        double max = toDouble(config.get("max"), Double.POSITIVE_INFINITY);

        List<String> failures = new ArrayList<>();
        List<String> values = data.column(column);

        for (int i = 0; i < values.size(); i++) {
            String raw = values.get(i);
            if (raw.isEmpty()) {
                continue;
            }
            try {
                double value = Double.parseDouble(raw);
                if (value < min || value > max) {
                    failures.add("row " + (i + 1) + ": " + value + " outside [" + min + ", " + max + "]");
                }
            } catch (NumberFormatException e) {
                failures.add("row " + (i + 1) + ": not a number: " + raw);
            }
        }
        return new RuleResult(name(), column, failures);
    }

    private static double toDouble(Object value, Double fallback) {
        if (value == null) {
            return fallback;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return Double.parseDouble(value.toString());
    }
}
