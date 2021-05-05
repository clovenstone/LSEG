## Assumption

- The daily returns data can be null.
- The daily returns data does not store null key
- The daily returns data can be in random order.
- The signature param can be null. 

## Design

- Use the base date and As of date to calculate a period. (LocalDate datesUntil Java 9 +)  
- Extract the daily return data falls into this period. o(n) of the list of period 
- Calculate the cumulative rate from this period.

## Consideration

- Check the validity of the parameters, throw NPE when any of them is null.
- Return an empty collection of daily returns data.
- If asOf date is before base date, return 0.
- Use BigDecimal with 5 scale Half_up rounding for numeric values.
- Use parallel stream where possible to collect and reduce the final value.
- Reduce the logging where possible. 

## Test coverage

- All possible edge cases are covered.
 

