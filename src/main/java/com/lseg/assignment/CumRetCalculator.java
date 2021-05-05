package com.lseg.assignment;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.math.RoundingMode.HALF_UP;
import static java.time.ZoneId.systemDefault;
import static java.util.Date.from;
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

        Map<Date, Double> periodDailyReturns = new HashMap<>();

        var asOfLocal = new java.sql.Date(asOf.getTime()).toLocalDate();
        var baseLocal = new java.sql.Date(base.getTime()).toLocalDate();

        //build the period that has daily return value
        var period = baseLocal.datesUntil(asOfLocal)
                .map(d -> from(d.atStartOfDay(systemDefault()).toInstant()))
                .filter(dailyReturns::containsKey)
                .collect(toList());

        //build a period based daily returns collection
        period.forEach(d -> periodDailyReturns.put(d, dailyReturns.get(d)));

        //reduce the value of each daily return with sum *=(1+v) and finally minus 1
        return BigDecimal.valueOf(
                periodDailyReturns.values()
                        .parallelStream()
                        .map(d -> d + 1)
                        .mapToDouble(rate -> rate)
                        .reduce(1, (a, b) -> a * b) - 1
        ).setScale(5, HALF_UP)
                .doubleValue();
    }


}
