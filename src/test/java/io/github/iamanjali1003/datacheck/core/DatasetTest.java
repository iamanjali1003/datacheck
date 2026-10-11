package io.github.iamanjali1003.datacheck.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DatasetTest {

    @TempDir
    Path dir;

    private Path csv(String content) throws IOException {
        Path file = dir.resolve("data.csv");
        Files.writeString(file, content);
        return file;
    }

    @Test
    void readsColumnsAndRows() throws IOException {
        Dataset data = Dataset.fromCsv(csv("a,b\n1,2\n3,4\n"));

        assertThat(data.rowCount()).isEqualTo(2);
        assertThat(data.column("a")).containsExactly("1", "3");
        assertThat(data.column("b")).containsExactly("2", "4");
    }

    @Test
    void trimsWhitespaceAroundCells() throws IOException {
        Dataset data = Dataset.fromCsv(csv("a, b\n1 , 2\n"));

        assertThat(data.column("b")).containsExactly("2");
    }

    @Test
    void keepsEmptyCellsRatherThanDroppingThem() throws IOException {
        Dataset data = Dataset.fromCsv(csv("a,b\n1,\n"));

        assertThat(data.column("b")).containsExactly("");
    }

    @Test
    void rejectsARowWithTheWrongNumberOfCells() throws IOException {
        assertThatThrownBy(() -> Dataset.fromCsv(csv("a,b\n1,2,3\n")))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("line 2");
    }

    @Test
    void rejectsAnEmptyFile() throws IOException {
        assertThatThrownBy(() -> Dataset.fromCsv(csv("")))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("empty");
    }
}
