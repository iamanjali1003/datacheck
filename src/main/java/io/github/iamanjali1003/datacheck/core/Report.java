package io.github.iamanjali1003.datacheck.core;

import java.util.List;

/** Every rule result from one run. */
public record Report(String source, List<RuleResult> results) {

    public boolean ok() {
        return results.stream().allMatch(RuleResult::ok);
    }

    public long failureCount() {
        return results.stream().filter(r -> !r.ok()).count();
    }
}
