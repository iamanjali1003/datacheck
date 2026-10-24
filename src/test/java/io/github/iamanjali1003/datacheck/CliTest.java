package io.github.iamanjali1003.datacheck;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class CliTest {

    private static final String SPEC = """
            columns:
              speed:
                range:
                  min: 0
                  max: 5
            """;

    @TempDir
    Path dir;

    private final StringWriter err = new StringWriter();

    private int run(String... args) {
        CommandLine cli = Cli.commandLine();
        cli.setErr(new PrintWriter(err, true));
        return cli.execute(args);
    }

    private String write(String name, String content) throws IOException {
        return Files.writeString(dir.resolve(name), content).toString();
    }

    @Test
    void exitsZeroWhenEveryCheckPasses() throws IOException {
        assertThat(run(write("data.csv", "speed\n1\n4\n"), "--spec", write("rules.yaml", SPEC))).isZero();
    }

    @Test
    void exitsOneWhenACheckFails() throws IOException {
        assertThat(run(write("data.csv", "speed\n1\n9\n"), "--spec", write("rules.yaml", SPEC))).isEqualTo(1);
    }

    @Test
    void exitsTwoWhenTheDataFileIsMissing() throws IOException {
        String missing = dir.resolve("missing.csv").toString();

        assertThat(run(missing, "--spec", write("rules.yaml", SPEC))).isEqualTo(Cli.CANNOT_RUN);
        assertThat(err.toString()).startsWith("datacheck: no such file:");
    }

    @Test
    void exitsTwoForAnUnknownFormat() throws IOException {
        String data = write("data.csv", "speed\n1\n");

        assertThat(run(data, "--spec", write("rules.yaml", SPEC), "--report", "xml")).isEqualTo(Cli.CANNOT_RUN);
    }
}
