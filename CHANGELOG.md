# Changelog

## v1.1.0

### New Features
- **Multi-currency support** — exchange rates looked up by currency + date; supports multiple NBU rate files separated by comma: `-Drates.file=usd.json,eur.json`
- **Dividend calculation** — reads the `Corpactions` sheet from the broker report and generates a `Dividends` sheet in the output with yearly subtotals
- **Tax declaration guide** — added `TAX_GUIDE.md` with step-by-step instructions for filing investment profit and dividends in the Ukrainian ДПС declaration

### Bug Fixes
- Commission is now converted using its own currency rate, not the trade currency rate

### Changes
- Report output moved to `reports/tax-report-YYYY-MM-DD_HH-mm-ss.xlsx`
- Replaced SLF4J Simple with Log4j2 — colored, formatted console output
- `CalculatorFactory` removed — construction inlined into `TaxReportRunner`

## v1.0.0

Initial release — FIFO tax calculation for Freedom Finance broker reports.
