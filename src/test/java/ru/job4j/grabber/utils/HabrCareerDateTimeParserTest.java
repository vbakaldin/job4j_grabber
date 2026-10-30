package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class HabrCareerDateTimeParserTest {
    private final DateTimeParser parser = new HabrCareerDateTimeParser();

    @Test
    void whenDateHasPositiveOffsetThenLocalTimeIsPreserved() {
        assertThat(parser.parse("2026-10-30T12:34:56+03:00"))
                .isEqualTo(LocalDateTime.of(2026, 10, 30, 12, 34, 56));
    }

    @Test
    void whenDateHasFractionalSecondsThenPreserveThem() {
        assertThat(parser.parse("2026-10-30T12:34:56.123+05:00"))
                .isEqualTo(LocalDateTime.of(2026, 10, 30, 12, 34, 56, 123000000));
    }
}