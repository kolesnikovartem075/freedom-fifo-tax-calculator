# Freedom FIFO Tax Calculator

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Tax calculator for Freedom Finance broker using FIFO method. Each share is tracked individually for precise profit calculation in USD and UAH, following Ukrainian tax reporting standards.

Built for personal use to automate tax reporting. If you have dozens of trades per year — this saves hours of manual work. Hundreds of trades per day? This saves you from hiring an accountant.

⚠️ **Beta version.** May contain bugs. Please compare results with your previous reports before relying on this tool. Found a bug? [Open an issue](https://github.com/kolesnikovartem075/freedom-fifo-tax-calculator/issues)

## What is FIFO?

**FIFO (First In, First Out)** is an accounting method where shares purchased first are sold first. This is the standard method for tax calculation in Ukraine.

**Example:**
- Jan 15: Buy 10 AAPL shares at \$100 (rate: 37.5 UAH)
- Mar 10: Buy 5 AAPL shares at \$120 (rate: 38.2 UAH)
- Jun 20: Sell 12 AAPL shares at \$150 (rate: 41.0 UAH)

Using FIFO: first sell 10 shares at \$100 cost (rate: 37.5), then 2 shares at $120 cost (rate: 38.2). Each transaction uses its own NBU exchange rate for UAH conversion.

## How it calculates

**Step 1: Split trades into units**

If you buy 10 shares in one order — we split into 10 individual records, each with its own price and commission per share.

**Step 2: Match buy/sell using FIFO**

For each sell, take the oldest available buy. One by one.

**Step 3: Calculate profit per share**

*USD:*
```
Profit USD = Sell Price - Buy Price - Buy Commission - Sell Commission
```

*UAH:*
```
Profit UAH = (Sell Price × Sell Rate) - (Buy Price × Buy Rate) - (Buy Commission × Buy Rate) - (Sell Commission × Sell Rate)
```

**Example:**
- Buy: $100, commission $0.50, rate 37.5 UAH/$
- Sell: $150, commission $0.50, rate 41.0 UAH/$
```
Profit USD = 150 - 100 - 0.50 - 0.50 = $49.00
Profit UAH = (150 × 41.0) - (100 × 37.5) - (0.50 × 37.5) - (0.50 × 41.0) = 6150 - 3750 - 18.75 - 20.50 = ₴2360.75
```
## Requirements

- Git
- Java 17+
- Gradle 8+

## Input Files

### 1. Broker Report
Excel file exported from Freedom Finance with trade history.

**How to export:**
1. Go to [Freedom24 Reports](https://freedom24.com/cabinet?tabId=reports)
2. Select **Broker report / Звіт брокера** -> **Over a period / За період**
3. Set date range from your first trade to today
4. Download Excel file

**Important:**
- Export complete trade history from the beginning — FIFO requires all previous buy transactions
- Missing years or gaps will cause incorrect profit calculations
- If you need specific years only, ensure all related buy transactions are included in the report

### 2. Exchange Rates
JSON file with official NBU (National Bank of Ukraine) exchange rates.

**How to get:**
- Download from [NBU Open Data](https://bank.gov.ua/en/markets/exchangerate-chart?cn%5B%5D=USD)
- Must cover all dates when trades occurred

## Installation & Usage
```bash
# Clone repository
git clone https://github.com/kolesnikovartem075/freedom-fifo-tax-calculator.git
cd freedom-fifo-tax-calculator

# Build project
./gradlew build

# Run
./gradlew run --args="path/to/broker-report.xlsx path/to/rates.json"
```

## Output

Generates Excel file `tax-report-YYYY-MM-DD_HH-mm-ss.xlsx` with multiple sheets:

### Detail Sheet
Each closed position with full details:

| Ticker | Buy Date | Buy Price | Sell Date | Sell Price | Profit USD | Profit UAH |
|--------|----------|-----------|-----------|------------|------------|------------|
| AAPL   | 2024-01-15 | $150.00 | 2024-03-20 | $175.00 | $24.50 | ₴905.25 |
| MSFT   | 2024-06-10 | $280.00 | 2024-12-15 | $310.00 | $29.20 | ₴1,073.12 |

### Year Sheets (2023, 2024, ...)
Summary by ticker for each year:

**Sheet: 2023**

| Ticker | Quantity | Profit USD | Profit UAH |
|--------|----------|------------|------------|
| STLD.US | 2 | $18.28 | ₴597.21 |
| SQQQ.US | 4 | -$3.54 | -₴127.80 |
| **Total** |  | **$14.74** | **₴469.41** |

**Sheet: 2024**

| Ticker | Quantity | Profit USD | Profit UAH |
|--------|----------|------------|------------|
| AAPL | 10 | $245.00 | ₴9,052.50 |
| MSFT | 5 | $146.00 | ₴5,365.60 |
| **Total** |  | **$391.00** | **₴14,418.10** |

## Example

![Report Example](docs/report-example.png)

## License

MIT