package io.github.iamanjali1003.datacheck.report;

import io.github.iamanjali1003.datacheck.core.Report;
import io.github.iamanjali1003.datacheck.core.RuleResult;

import java.io.IOException;

/** Plain text, meant for a terminal. */
public final class TextReporter implements Reporter {

    @Override
    public String format() {
        return "text";
    }

    @Override
    public void write(Report report, Appendable out) throws IOException {
        out.append("datacheck ").append(report.source()).append("\n\n");

        for (RuleResult result : report.results()) {
            String status = result.ok() ? "ok  " : "FAIL";
            out.append(status).append("  ")
               .append(result.column()).append(" / ").append(result.rule())
               .append("\n");

            for (String failure : result.failures()) {
                out.append("        ").append(failure).append("\n");
            }
        }

        out.append("\n");
        if (report.ok()) {
            out.append("all checks passed\n");
        } else {
            out.append(String.valueOf(report.failureCount())).append(" check(s) failed\n");
        }
    }
}
