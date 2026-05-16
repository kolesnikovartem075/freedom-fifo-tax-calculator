# Freedom FIFO Tax Calculator

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Tax calculator for Freedom Finance using FIFO method. Tracks each share individually for accurate profit calculation in UAH according to Ukrainian tax standards. Supports multi-currency trades and dividend calculation.

Built for personal use to simplify tax reporting. Even in beta, it can save time on repetitive calculations and help you avoid unnecessary manual work, especially if you have many trades to track.

⚠️ **Beta version.** May contain bugs. Please compare results with your previous reports before relying on this tool. Found a bug? [Open an issue](https://github.com/kolesnikovartem075/freedom-fifo-tax-calculator/issues)

## What is FIFO?

**FIFO (First In, First Out)** is an accounting method where shares purchased first are sold first. This is the standard method for tax calculation in Ukraine.
**Example:**
- Jan 15: Buy 10 AAPL shares at \$100
- Mar 10: Buy 5 AAPL shares at \$120
- Jun 20: Sell 12 AAPL shares at \$150

Using FIFO: first sell 10 shares at \$100 cost, then 2 shares at \$120 cost. Each transaction is matched according to the FIFO method.

## How it calculates

**Step 1: Split trades into units**

When you buy/sell multiple shares in a single order, the calculator splits them into individual records. Each share keeps its own price and commission.
For implementation details, see [TradeStore](https://github.com/kolesnikovartem075/freedom-fifo-tax-calculator/blob/main/src/main/java/com/tax/calculator/trade/unit/TradeStore.java).

**Step 2: Match buy/sell using FIFO**

When you sell shares, the calculator automatically takes shares from the earliest purchases first. It continues in order until the sell quantity is fully matched. For implementation details, see [TaxReportBuilder](https://github.com/kolesnikovartem075/freedom-fifo-tax-calculator/blob/main/src/main/java/com/tax/calculator/TaxReportBuilder.java).

**Step 3: Calculate income/expense/profit per share**

The calculator determines profit for each closed position using the following formulas. All calculations are done **without rounding**.

```
Profit USD = Sell Price - Buy Price - Buy Commission - Sell Commission

Income UAH  = Sell Price × Sell Rate
Expense UAH = (Buy Price × Buy Rate) + (Buy Commission × Buy Commission Rate) + (Sell Commission × Sell Commission Rate)
Profit UAH  = Income UAH - Expense UAH
```
**Note:**
- **Buy/Sell Rate** — official NBU exchange rate on the trade date for the trade currency
- **Buy/Sell Commission Rate** — official NBU rate for the commission currency (same as trade rate if commission currency matches)
- For the exact implementation and logic, refer to the [PositionCalculator](https://github.com/kolesnikovartem075/freedom-fifo-tax-calculator/blob/main/src/main/java/com/tax/calculator/position/PositionCalculator.java) class in the source code

**Example:**
- Buy: \$100, commission \$0.50, rate 37.5 UAH/$
- Sell: \$150, commission \$0.50, rate 41.0 UAH/$
```
Profit USD  = 150 - 100 - 0.50 - 0.50 = $49.00

Income UAH  = 150 × 41.0 = 6150
Expense UAH = (100 × 37.5) + (0.50 × 37.5) + (0.50 × 41.0) = 3789.25
Profit UAH  = 6150 - 3789.25 = ₴2360.75
```

## Requirements

- Git (only if cloning the repository)
- Java 25 (Gradle is included via wrapper)

## Input Files

### 1. Broker Report (`-Dbroker.report=path/to/broker-report.xlsx`)
Excel file exported from Freedom Finance with trade history.

**How to export:**
1. Go to [Freedom24 Reports](https://freedom24.com/cabinet?tabId=reports)
2. Select **Broker report / Звіт брокера** → **Over a period / За період**
3. Set the date range **from your very first trade** to today
4. Download the Excel file

**Important:** export the complete trade history from the beginning — FIFO calculations rely on all previous buy transactions. Partial exports will produce wrong results.

### 2. Exchange Rates (`-Drates.file=path/to/rates.json`)
JSON file(s) with official NBU (National Bank of Ukraine) exchange rates. Supports multiple currencies — pass several files separated by a comma.

**How to get:**
- Download from [NBU Open Data](https://bank.gov.ua/en/markets/exchangerate-chart?cn%5B%5D=USD)
- Must cover all dates when trades and dividends occurred

```bash
# Single currency (USD only)
-Drates.file=reports/rates-usd.json

# Multi-currency (USD + EUR)
-Drates.file=reports/rates-usd.json,reports/rates-eur.json
```

## Installation & Usage
```bash
# Clone repository
git clone https://github.com/kolesnikovartem075/freedom-fifo-tax-calculator.git
cd freedom-fifo-tax-calculator

# Run on Unix/macOS
./gradlew run -Dbroker.report=reports/broker-report.xlsx -Drates.file=reports/rates-usd.json

# Run on Windows (PowerShell)
gradlew.bat run -Dbroker.report=reports\broker-report.xlsx -Drates.file=reports\rates-usd.json
```

The output file is saved to `reports/tax-report-YYYY-MM-DD_HH-mm-ss.xlsx`.

## Output

After running the calculator, it generates an Excel file with the following sheets:

### Detail Sheet
Each closed position with full details (per share):

| Ticker   | Quantity | Buy Date            | Sell Date           | Buy Price | Buy Commission | Sell Price | Sell Commission | Profit (USD) | Income (UAH) | Expense (UAH) | Profit (UAH) |
|----------|----------|---------------------|---------------------|-----------|----------------|------------|-----------------|--------------|--------------|---------------|--------------|
| AAPL.US  | 1        | 2024-01-10 16:37:47 | 2024-05-28 16:48:03 | 184.61    | 1.54           | 191.25     | 1.57            | 3.55         | 7,707.69     | 7,178.37      | 529.32       |

### Year Sheet (Summary by ticker)
| Ticker   | Quantity | Profit (USD) | Income (UAH) | Expense (UAH) | Profit (UAH) |
|----------|----------|--------------|--------------|---------------|--------------|
| AAPL.US  | 2        | 41.10        | 16,388.06    | 14,035.46     | 2,352.60     |
| MSFT.US  | 1        | 8.00         | 6,600.00     | 6,300.00      | 300.00       |
| **Total**| 4        | 95.10        | 52,488.06    | 48,995.46     | 3,492.60     |

### Dividends Sheet
Per-payment breakdown with yearly subtotals:

| Ticker   | Payment Date | Currency | Gross  | Foreign Tax | Net    | Income (UAH) | Foreign Tax (UAH) | Net (UAH) |
|----------|--------------|----------|--------|-------------|--------|--------------|-------------------|-----------|
| AAPL.US  | 2024-08-15   | USD      | 0.25   | 0.04        | 0.21   | 10.43        | 1.67              | 8.76      |

## How to file the tax declaration

See [TAX_GUIDE.md](TAX_GUIDE.md) for step-by-step instructions on how to report investment profit and dividends in the Ukrainian ДПС declaration (Додаток ІН).

## Example

![Report Example](docs/report-example-1.png)
![Report Example](docs/report-example-2.png)

## License

MIT
