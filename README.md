# E-commerce UI test automation

[![UI tests](https://github.com/Nevena-tfStudio/Ecomm_Website_Automation/actions/workflows/tests.yml/badge.svg?branch=main)](https://github.com/Nevena-tfStudio/Ecomm_Website_Automation/actions/workflows/tests.yml)

Automated UI tests for the [SauceDemo](https://www.saucedemo.com/) practice shop, written in Java with Selenium WebDriver, TestNG and Maven.

## Project layout

```text
src/test/java/com/nevena/ecommerce/
├── config/Config.java        # Reads config.properties; -D flags override it
├── support/Screenshots.java  # Saves a screenshot when a test fails
├── data/ErrorMessages.java   # Expected error texts shared by tests
├── data/Products.java        # Product names
├── data/PageTexts.java       # Expected titles and confirmation texts
├── data/Customer.java        # Valid checkout customer details
├── pages/                    # Page objects: HOW to use each page
│   ├── BasePage.java         # Shared driver, wait, cart badge helpers
│   ├── LoginPage.java
│   ├── Money.java            # Parses "$29.99" into BigDecimal
│   ├── ItemList.java         # Reads item rows shared by cart and checkout overview
│   ├── InventoryPage.java
│   ├── CartPage.java
│   ├── CheckoutInformationPage.java
│   ├── CheckoutOverviewPage.java
│   └── CheckoutCompletePage.java
└── tests/                    # Tests: WHAT should happen
    ├── BaseTest.java         # Starts/stops Chrome for every test
    ├── ShopBaseTest.java     # BaseTest + login, starts on the inventory page
    ├── LoginTest.java
    ├── CartTest.java
    └── CheckoutTest.java
src/test/resources/
├── config.properties         # URL, demo credentials, headless, timeout
├── testng.xml                # Regression suite (default)
└── smoke.xml                 # Smoke suite
.github/workflows/tests.yml   # CI on GitHub Actions
```

Rules of thumb:
- Selenium calls (`By`, `findElement`, waits) live only in page objects, never in tests.
- A page action that moves to another page returns that page object (`loginPage.loginAs(...)` returns `InventoryPage`).
- Every test class extends `BaseTest` and tags each test with `groups = "regression"`; the most important ones also get `"smoke"`.

## Running tests

Needs a JDK 25+ and Google Chrome (Selenium Manager downloads the driver).

```bash
mvn test                                              # regression suite, visible browser
mvn test -Dheadless=true                              # no browser window (as on CI)
mvn test -DsuiteXml=src/test/resources/smoke.xml      # smoke suite only
mvn test -Dbase.url=https://other-env.example.com/    # any config value can be overridden
```

In IntelliJ you can also click the green run icon next to a test class or method.

Reports are written to `target/surefire-reports/` (open `index.html`). When a test fails, a screenshot of the browser at that moment is saved to `target/screenshots/` and its path is printed in the test output. On GitHub Actions, reports and screenshots are uploaded together as the `test-reports` artifact of each run.

## Configuration

`src/test/resources/config.properties` holds the defaults. Any key can be overridden with `-D<key>=<value>`.

| Key | Meaning |
|---|---|
| `base.url` | Site under test |
| `username` / `password` | Valid login (SauceDemo's public demo account) |
| `headless` | `true` runs Chrome without a window |
| `timeout.seconds` | Maximum wait for page elements |
