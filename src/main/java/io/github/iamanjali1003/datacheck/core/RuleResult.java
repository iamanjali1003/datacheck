package io.github.iamanjali1003.datacheck.core;

import java.util.List;

/**
 * The outcome of running one rule against one column.
 * A result with no failures passed.
 */
public record RuleResult(String rule, String column, List<String> failures) {

    public static RuleResult passed(String rule, String column) {
        return new RuleResult(rule, column, List.of());
    }

    public boolean ok() {
        return failures.isEmpty();
    }
}
