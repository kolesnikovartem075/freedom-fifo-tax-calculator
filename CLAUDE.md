# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.tax.calculator.position.PositionCalculatorTest"

# Run the application
./gradlew run -Dbroker.report=path/to/broker-report.xlsx -Drates.file=path/to/rates.json
```

## Architecture

The application is a Java 25 / Gradle CLI tool that reads a Freedom Finance broker report (Excel) and NBU exchange rates (JSON), then generates a tax report Excel file using the FIFO method.

**End-to-end flow (`TaxReportRunner.main`):**

1. **Parse inputs** — `BrokerReportParser` reads the `.xlsx` broker report (sheet with "Trades" prefix) into `TradeRow` objects; `ExchangeRateParser` reads the NBU rates JSON.
2. **Build `TradeBook`** — `TradeBook.fromRows()` groups `TradeRow`s by ticker into per-ticker buy/sell `LinkedList` queues preserving chronological order.
3. **Split into unit trades** — `TradeStore.from(tradeBook)` calls `UnitTradeFactory.splitAll()` to explode multi-share orders into individual `UnitTrade` records (one per share), each carrying per-unit price and commission.
4. **FIFO matching** — `TaxReportBuilder.collectPositions()` iterates each ticker, polling buys and sells in FIFO order and pairing them via `PositionCalculator.getProfit()`.
5. **Calculate profit** — `PositionCalculator` looks up the NBU rate for each trade date and computes USD profit and UAH income/expense/profit using:
   ```
   Profit USD = Sell Price - Buy Price - Buy Commission - Sell Commission
   Income UAH = Sell Price × Sell Rate
   Expense UAH = (Buy Price × Buy Rate) + (Buy Commission × Buy Rate) + (Sell Commission × Sell Rate)
   Profit UAH = Income UAH - Expense UAH
   ```
   Where Buy/Sell Rate is the official NBU USD/UAH rate on the respective trade date.
6. **Write report** — `ReportWriter` produces `tax-report-YYYY-MM-DD_HH-mm-ss.xlsx` with a Detail sheet (one row per closed position) and a Year/Summary sheet (aggregated by ticker with totals).

**Key classes by package:**

| Package | Purpose |
|---|---|
| `com.tax.calculator` | Entry point (`TaxReportRunner`) and FIFO orchestrator (`TaxReportBuilder`) |
| `com.tax.calculator.trade` | Parses broker Excel (`BrokerReportParser`), models raw rows (`TradeRow`, `TradeOperation`), groups them (`TradeBook`, `TradeQueues`) |
| `com.tax.calculator.trade.unit` | Splits multi-share rows into unit trades (`UnitTradeFactory`), stores them (`TradeStore`, `UnitTradeQueues`) |
| `com.tax.calculator.exchange.rate` | Parses NBU JSON rates (`ExchangeRateParser`), looks up rates by date (`ExchangeRates`) |
| `com.tax.calculator.position` | Profit calculation per closed position (`PositionCalculator`), result entities (`ClosedPosition`, `ProfitSummary`, `TradeDetail`) |
| `com.tax.calculator.report` | Excel output (`ReportWriter`, `DetailSheetWriter`, `SummarySheetWriter`, `CellFormatFactory`) |
| `com.tax.calculator.utils` | System property helpers and file loading (`FileReportLoader`, `SystemPropertyUtils`) |

**Key design constraints:**
- All monetary calculations use `BigDecimal` with no intermediate rounding.
- Buy/sell queues are `LinkedList` (insertion-order) — FIFO correctness requires the broker report to be exported from the **very first ever trade**, not just the current tax year. Partial exports will produce wrong results for tickers where earlier buys are missing.
- Lombok is used heavily (`@Getter`, `@RequiredArgsConstructor`, `@Slf4j`).
- Logging via SLF4J simple (configured in `src/main/resources/simplelogger.properties`).
- Test resources include a sample broker report (`stocks-1.xlsx`) and rates file (`rates-1.json`) under `src/main/resources` (used by integration tests).
