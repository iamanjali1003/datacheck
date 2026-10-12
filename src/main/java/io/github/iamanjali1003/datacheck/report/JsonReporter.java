package io.github.iamanjali1003.datacheck.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.iamanjali1003.datacheck.core.Report;

import java.io.IOException;

/** Machine readable output, for use in a pipeline. */
public final class JsonReporter implements Reporter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String format() {
        return "json";
    }

    @Override
    public void write(Report report, Appendable out) throws IOException {
        out.append(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(report));
        out.append("\n");
    }
}
