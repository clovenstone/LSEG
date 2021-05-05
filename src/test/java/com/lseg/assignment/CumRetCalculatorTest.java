package com.lseg.assignment;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class CumRetCalculatorTest {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static Map<Date, Double> MOCK_DAILY_RETURNS;

    static {
        try {
            MOCK_DAILY_RETURNS = Map.of(
                    DATE_FORMAT.parse("2015-01-10"), 0.10d,
                    DATE_FORMAT.parse("2015-02-10"), 0.05d,
                    DATE_FORMAT.parse("2015-04-10"), 0.15d,
                    DATE_FORMAT.parse("2015-04-15"), -0.10d,
                    DATE_FORMAT.parse("2015-06-10"), -0.12d
            );
        } catch (Exception e) {
            //Do nothing here
        }
    }

    private CumRetCalculator cumRetCalculator;

    @BeforeEach
    void setup() {
        cumRetCalculator = new CumRetCalculator(MOCK_DAILY_RETURNS);
    }

    @Test
    void test_cumulativeDailyReturnDataIsNull() throws Exception {
        cumRetCalculator = new CumRetCalculator(null);
        assertThat(cumRetCalculator.findCumReturn(DATE_FORMAT.parse("2015-05-08"), DATE_FORMAT.parse("2015-02-01"))).isEqualTo(0.0);
    }

    @Test
    void test_cumulativeAsOfDateIsNull() {
        assertThrows(NullPointerException.class, () -> cumRetCalculator.findCumReturn(null, DATE_FORMAT.parse("2015-01-31")));
    }

    @Test
    void test_cumulativeBaseDateIsNull() {
        assertThrows(NullPointerException.class, () -> cumRetCalculator.findCumReturn(DATE_FORMAT.parse("2015-01-31"), null));
    }

    @Test
    void test_cumulativeAsOfDateBeforeBaseDateReturns() throws Exception {
        assertThat(cumRetCalculator.findCumReturn(DATE_FORMAT.parse("2015-01-31"), DATE_FORMAT.parse("2015-02-01"))).isEqualTo(0.0);
    }

    @Test
    void test_cumulativeAsOfDateIsLaterThanBaseDate_1() throws Exception {
        assertThat(cumRetCalculator.findCumReturn(DATE_FORMAT.parse("2015-02-28"), DATE_FORMAT.parse("2015-02-01"))).isEqualTo(0.05d);
    }

    @Test
    void test_cumulativeAsOfDateIsLaterThanBaseDate_2() throws Exception {
        assertThat(cumRetCalculator.findCumReturn(DATE_FORMAT.parse("2015-03-13"), DATE_FORMAT.parse("2015-02-01"))).isEqualTo(0.05d);
    }

    @Test
    void test_cumulativeAsOfDateIsLaterThanBaseDate_3() throws Exception {
        assertThat(cumRetCalculator.findCumReturn(DATE_FORMAT.parse("2015-04-30"), DATE_FORMAT.parse("2015-02-01"))).isEqualTo(0.08675);
    }

    @Test
    void test_cumulativeAsOfDateIsLaterThanBaseDate_4() throws Exception {
        assertThat(cumRetCalculator.findCumReturn(DATE_FORMAT.parse("2015-05-08"), DATE_FORMAT.parse("2015-02-01"))).isEqualTo(0.08675);
    }

    @Test
    void test_cumulativeAsOfDateIsLaterThanBaseDate_5() throws Exception {
        assertThat(cumRetCalculator.findCumReturn(DATE_FORMAT.parse("2015-06-30"), DATE_FORMAT.parse("2015-02-01"))).isEqualTo(-0.04366);
    }

    @Test
    void test_cumulativeAsOfDateIsLaterThanBaseDate_6() throws Exception {
        assertThat(cumRetCalculator.findCumReturn(DATE_FORMAT.parse("2015-02-01"), DATE_FORMAT.parse("2015-02-01"))).isEqualTo(0.0);
    }

    @SneakyThrows
    @Test
    void test_cumulativeMethodGetsCalled_1mill_times() {
        final Date startInclusive = DATE_FORMAT.parse("2015-02-01");
        final Date endInclusive = DATE_FORMAT.parse("2015-12-31");
        Instant start = Instant.now();
        log.info("Started at: {}", LocalDateTime.now());
        IntStream.range(0, 100_000_0)
                .parallel()
                .forEach(i -> {
                    Date asOf = mockDate(startInclusive, endInclusive);
                    Date base = mockDate(startInclusive, endInclusive);
                    cumRetCalculator.findCumReturn(asOf, base);
                });
        Instant end = Instant.now();
        log.info("Elapsed Time: " + Duration.between(start, end).getSeconds());
    }

    private Date mockDate(Date startInclusive, Date endExclusive) {
        long startMillis = startInclusive.getTime();
        long endMillis = endExclusive.getTime();
        long randomMillisSinceEpoch = ThreadLocalRandom
                .current()
                .nextLong(startMillis, endMillis);
        return new Date(randomMillisSinceEpoch);
    }
}