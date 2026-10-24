package io.github.iamanjali1003.datacheck;

import io.github.iamanjali1003.datacheck.core.Dataset;
import io.github.iamanjali1003.datacheck.core.Engine;
import io.github.iamanjali1003.datacheck.core.Report;
import io.github.iamanjali1003.datacheck.core.Spec;
import io.github.iamanjali1003.datacheck.report.Reporter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.Callable;

@Command(
        name = "datacheck",
        mixinStandardHelpOptions = true,
        version = "datacheck 0.1.0",
        description = "Validate a CSV file against a YAML spec."
)
public final class Cli implements Callable<Integer> {

    /** Exit code when the tool could not run at all, as opposed to a check failing. */
    static final int CANNOT_RUN = 2;

    @Parameters(index = "0", description = "CSV file to check.")
    private Path data;

    @Option(names = {"-s", "--spec"}, required = true, description = "YAML spec file.")
    private Path spec;

    @Option(names = {"-r", "--report"}, defaultValue = "text",
            description = "Output format: text, json, html, latex.")
    private String report;

    @Override
    public Integer call() throws Exception {
        Dataset dataset = Dataset.fromCsv(data);
        Spec rules = Spec.load(spec);

        Report result = new Engine().run(dataset, rules, data.getFileName().toString());

        Reporter reporter = findReporter(report);
        if (reporter == null) {
            System.err.println("unknown format: " + report);
            System.err.println("available: " + String.join(", ", availableFormats()));
            return CANNOT_RUN;
        }

        StringBuilder out = new StringBuilder();
        reporter.write(result, out);
        System.out.print(out);

        return result.ok() ? 0 : 1;
    }

    private static Reporter findReporter(String format) {
        for (Reporter reporter : ServiceLoader.load(Reporter.class)) {
            if (reporter.format().equals(format)) {
                return reporter;
            }
        }
        return null;
    }

    private static List<String> availableFormats() {
        List<String> formats = new ArrayList<>();
        for (Reporter reporter : ServiceLoader.load(Reporter.class)) {
            formats.add(reporter.format());
        }
        Collections.sort(formats);
        return formats;
    }

    public static void main(String[] args) {
        System.exit(commandLine().execute(args));
    }

    /**
     * Without a handler, picocli exits 1 when the command throws, which a build
     * could not tell apart from a failed check. A file that cannot be read now
     * exits {@link #CANNOT_RUN} with a one-line message instead of a stack trace.
     */
    static CommandLine commandLine() {
        return new CommandLine(new Cli())
                .setExecutionExceptionHandler((error, command, parseResult) -> {
                    String message = error instanceof NoSuchFileException
                            ? "no such file: " + error.getMessage()
                            : error.getMessage();
                    command.getErr().println("datacheck: " + message);
                    return CANNOT_RUN;
                });
    }
}
