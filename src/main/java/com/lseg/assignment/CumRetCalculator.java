package com.lseg.assignment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

public class CumRetCalculator {

    private final Map<Date, Double> dailyReturns;

    public CumRetCalculator(Map<Date, Double> dailyReturns) {
        this.dailyReturns = nonNull(dailyReturns) ? dailyReturns : new HashMap<>();
    }

    double findCumReturn(Date asOf, Date base) {

        Objects.requireNonNull(asOf, "asOf is null");
        Objects.requireNonNull(base, "base is null");

        if (asOf.before(base)) {
            return 0;
        }

        Map<Date, Double> rangedDate = new HashMap<>();

        LocalDate asOfLocal = new java.sql.Date(asOf.getTime()).toLocalDate();
        LocalDate baseLocal = new java.sql.Date(base.getTime()).toLocalDate();

        List<Date> period = baseLocal.datesUntil(asOfLocal)
                .map(d -> Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .collect(toList());

        period.stream()
                .filter(dailyReturns::containsKey)
                .forEachOrdered(d -> rangedDate.put(d, dailyReturns.get(d)));

        return BigDecimal.valueOf(
                rangedDate
                        .values()
                        .parallelStream()
                        .map(d -> d + 1)
                        .mapToDouble(rate -> rate)
                        .reduce(1, (a, b) -> a * b) - 1
        ).setScale(5, RoundingMode.HALF_UP)
                .doubleValue();
    }


}
