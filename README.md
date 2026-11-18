# E-commerce UI test automation

[![UI tests](https://github.com/Nevena-tfStudio/Ecomm_Website_Automation/actions/workflows/tests.yml/badge.svg?branch=main)](https://github.com/Nevena-tfStudio/Ecomm_Website_Automation/actions/workflows/tests.yml)

Automated UI tests for the [SauceDemo](https://www.saucedemo.com/) practice shop, written in Java with Selenium WebDriver, TestNG and Maven.

## Project layout

```text
src/test/java/com/nevena/ecommerce/
├── config/Config.java        # Reads config.properties; -D flags override it
├── support/DriverFactory.java # Creates Chrome or Firefox
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
    ├── BaseTest.java         # Starts/stops a browser for every test (one per thread)
    ├── ShopBaseTest.java     # BaseTest + login, starts on the inventory page
    ├── LoginTest.java
    ├── CartTest.java
    └── CheckoutTest.java
src/test/resources/
├── config.properties         # URL, demo credentials, headless, timeout
├── allure.properties         # Where Allure writes its results
├── testng.xml                # Regression suite (default), runs in parallel
└── smoke.xml                 # Smoke suite
.github/workflows/tests.yml   # CI on GitHub Actions
```

Rules of thumb:
- Selenium calls (`By`, `findElement`, waits) live only in page objects, never in tests.
- A page action that moves to another page returns that page object (`loginPage.loginAs(...)` returns `InventoryPage`).
- Every test class extends `BaseTest` and tags each test with `groups = "regression"`; the most important ones also get `"smoke"`.
- Tests run in parallel and TestNG shares one test-class instance between threads. Never keep the browser or a page object in a plain field: use `driver()`, or a `ThreadLocal` like `ShopBaseTest.inventory()`.

## Running tests

Needs a JDK 25+ and Google Chrome or Firefox (Selenium Manager downloads the driver).

```bash
mvn test                                              # regression suite, visible browser
mvn test -Dheadless=true                              # no browser window (as on CI)
mvn test -DsuiteXml=src/test/resources/smoke.xml      # smoke suite only
mvn test -Dbrowser=firefox                            # run in Firefox instead of Chrome
mvn test -DthreadCount=1                              # one test at a time (default: 4 in parallel)
mvn test -Dbase.url=https://other-env.example.com/    # any config value can be overridden
```

In IntelliJ you can also click the green run icon next to a test class or method.

## Reports

- **Allure report:** after `mvn test`, run `mvn allure:report` and open `target/site/allure-maven-plugin/index.html` (a single file; double-click it). The first run downloads the Allure tool into `.allure/` (git-ignored).
- **Screenshots:** when a test fails, a screenshot of the browser at that moment is saved to `target/screenshots/`, attached to the test in the Allure report, and its path is printed in the test output.
- **TestNG report:** `target/surefire-reports/index.html`.

On GitHub Actions the suite runs on Chrome and Firefox in parallel jobs. Each job uploads a `test-reports-<browser>` artifact with the Allure report, TestNG reports and any screenshots.

## Configuration

`src/test/resources/config.properties` holds the defaults. Any key can be overridden with `-D<key>=<value>`.

| Key | Meaning |
|---|---|
| `base.url` | Site under test |
| `username` / `password` | Valid login (SauceDemo's public demo account) |
| `browser` | `chrome` (default) or `firefox` |
| `headless` | `true` runs the browser without a window |
| `timeout.seconds` | Maximum wait for page elements |
