package io.github.iamanjali1003.datacheck.rules;

import io.github.iamanjali1003.datacheck.core.Dataset;
import io.github.iamanjali1003.datacheck.core.Rule;
import io.github.iamanjali1003.datacheck.core.RuleResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Checks that numeric values only move in one direction.
 *
 * Useful for timestamps and counters, where a step backwards usually means
 * rows arrived out of order.
 */
public final class MonotonicRule implements Rule {

    @Override
    public String name() {
        return "monotonic";
    }

    @Override
    public RuleResult check(Dataset data, String column, Map<String, Object> config) {
        String direction = config.getOrDefault("direction", "increasing").toString();
        if (!direction.equals("increasing") && !direction.equals("decreasing")) {
            return new RuleResult(name(), column,
                    List.of("direction must be increasing or decreasing, got: " + direction));
        }
        boolean increasing = direction.equals("increasing");

        List<String> failures = new ArrayList<>();
        List<String> values = data.column(column);
        Double previous = null;
        int previousRow = 0;

        for (int i = 0; i < values.size(); i++) {
            String raw = values.get(i);
            if (raw.isEmpty()) {
                continue;
            }

            double current;
            try {
                current = Double.parseDouble(raw);
            } catch (NumberFormatException e) {
                failures.add("row " + (i + 1) + ": not a number: " + raw);
                continue;
            }

            if (previous != null) {
                boolean ok = increasing ? current >= previous : current <= previous;
                if (!ok) {
                    failures.add("row " + (i + 1) + ": " + current
                            + " is not " + direction + " after " + previous
                            + " at row " + previousRow);
                }
            }
            previous = current;
            previousRow = i + 1;
        }
        return new RuleResult(name(), column, failures);
    }
}
