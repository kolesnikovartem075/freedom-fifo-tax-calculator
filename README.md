# freedom-fifo-tax-calculator
Tax calculator for Freedom Finance broker trades using FIFO (First In, First Out) method in USD and UAH.

## Features

- Parses Freedom Finance broker Excel reports
- Applies NBU (National Bank of Ukraine) exchange rates
- Calculates profit in USD and UAH
- Generates Excel tax reports

## Usage
Build
```bash
./gradlew build
```
Run
```bash
java -jar freedom-fifo-tax-calculator.jar <broker-report.xlsx> <exchange-rates.json>
```

## Input Files

### Broker Report
Excel file exported from Freedom Finance with trade history.

### Exchange Rates
JSON file with NBU official exchange rates. Download from [NBU website](https://bank.gov.ua/en/markets/exchangerates).

## Output

Excel report with:
- **Detail sheet**: Each closed position with buy/sell details and profit
- **Summary sheet**: Total profit in USD and UAH

## License

MIT