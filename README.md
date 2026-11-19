# E-commerce UI test automation

[![UI tests](https://github.com/Nevena-tfStudio/Ecomm_Website_Automation/actions/workflows/tests.yml/badge.svg?branch=main)](https://github.com/Nevena-tfStudio/Ecomm_Website_Automation/actions/workflows/tests.yml)

Automated UI tests for the [SauceDemo](https://www.saucedemo.com/) practice shop, written in Java with Selenium WebDriver, TestNG and Maven.

**[Latest test reports](https://nevena-tfstudio.github.io/Ecomm_Website_Automation/)**: Allure reports from the latest CI run (Chrome, Firefox and known defects), viewable in the browser.

## Highlights

- **44 regression tests** covering login, inventory and sorting, product details, cart, checkout (with tax and total calculations), the side menu, SauceDemo's special users and accessibility.
- **16 real site bugs found and documented** as `known-defect` tests. Each one asserts the correct behaviour, so it starts passing when the bug is fixed (see [Known defects](#known-defects)).
- **Page Object Model** with no Selenium calls in the tests, explicit waits only (no `Thread.sleep`), and test data kept in constants.
- **Parallel and cross-browser:** tests run 4 at a time, thread-safely (a `ThreadLocal` WebDriver), on Chrome and Firefox.
- **CI on GitHub Actions:** runs on every push and pull request, plus a nightly run that also re-checks the known bugs.
- **Reports:** [Allure, published online](https://nevena-tfstudio.github.io/Ecomm_Website_Automation/) after every run, with readable named steps, screenshots attached on failure, and axe accessibility findings as JSON.
- **Beyond functional tests:** a layout check that compares element positions at three screen sizes, a login-time performance check, and WCAG A/AA scans with axe-core.

## Project layout

```text
src/test/java/com/nevena/ecommerce/
├── config/Config.java        # Reads config.properties; -D flags override it
├── support/DriverFactory.java # Creates Chrome or Firefox
├── support/Screenshots.java  # Saves a screenshot when a test fails
├── support/Accessibility.java # Runs axe-core on the current page, attaches violations to Allure
├── data/ErrorMessages.java   # Expected error texts shared by tests
├── data/Products.java        # Product names
├── data/PageTexts.java       # Expected titles and confirmation texts
├── data/Customer.java        # Valid checkout customer details
├── data/Users.java           # Special usernames (locked_out_user, problem_user, ...)
├── data/Limits.java          # Time limits tests hold the site to (login time)
├── pages/                    # Page objects: HOW to use each page
│   ├── BasePage.java         # Shared driver, wait, cart badge helpers
│   ├── LoginPage.java
│   ├── Money.java            # Parses "$29.99" into BigDecimal
│   ├── ItemList.java         # Reads item rows shared by cart and checkout overview
│   ├── InventoryPage.java    # Product list, sorting, opens product details
│   ├── ProductPage.java      # Product detail page
│   ├── Menu.java             # Burger menu (open it with openMenu() on any page after login)
│   ├── CartPage.java
│   ├── CheckoutInformationPage.java
│   ├── CheckoutOverviewPage.java
│   └── CheckoutCompletePage.java
└── tests/                    # Tests: WHAT should happen
    ├── BaseTest.java         # Starts/stops a browser for every test (one per thread)
    ├── ShopBaseTest.java     # BaseTest + login, starts on the inventory page
    ├── UserBaseTest.java     # BaseTest + loginAs(username), for tests that log in as a special user
    ├── SpecialUsersTest.java # Every special user can log in (regression)
    ├── ProblemUserTest.java  # problem_user bugs (all known-defect)
    ├── ErrorUserTest.java    # error_user bugs (all known-defect)
    ├── VisualUserTest.java   # visual_user bugs (all known-defect)
    ├── PerformanceGlitchUserTest.java # performance_glitch_user slow login (known-defect)
    ├── AccessibilityTest.java # axe-core checks of the login, inventory and cart pages
    ├── LoginTest.java
    ├── LoginVisualTest.java  # Layout checks (known defect: clipped error box)
    ├── CartTest.java
    ├── SortingTest.java
    ├── ProductDetailsTest.java
    ├── MenuTest.java         # Menu checks (known defect: Reset App State leaves "Remove" buttons)
    └── CheckoutTest.java
src/test/resources/
├── config.properties         # URL, demo credentials, headless, timeout
├── allure.properties         # Where Allure writes its results
├── testng.xml                # Regression suite (default), runs in parallel
├── smoke.xml                 # Smoke suite
└── known-defects.xml         # Tests for bugs the site still has (expected to fail)
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
mvn test -DsuiteXml=src/test/resources/known-defects.xml  # known bugs (expected to fail)
mvn test -Dbrowser=firefox                            # run in Firefox instead of Chrome
mvn test -DthreadCount=1                              # one test at a time (default: 4 in parallel)
mvn test -Dbase.url=https://other-env.example.com/    # any config value can be overridden
```

In IntelliJ you can also click the green run icon next to a test class or method.

## Reports

- **Allure report:** after `mvn test`, run `mvn allure:report` and open `target/site/allure-maven-plugin/index.html` (a single file; double-click it). The first run downloads the Allure tool into `.allure/` (git-ignored).
- **Named steps:** page-object actions are annotated with Allure's `@Step`, so every test in the report shows readable steps such as "Add Sauce Labs Backpack to the cart". `@Step` only works with AspectJ weaving, which `pom.xml` sets up as a `-javaagent` (aspectjweaver) in the surefire `argLine`; when adding a page action, annotate it with `@Step("... {parameterName} ...")`. Passwords are hidden with `@Param(mode = Parameter.Mode.HIDDEN)`. If you run tests from IntelliJ, steps only appear when the same `-javaagent` is added to the run configuration's VM options.
- **Screenshots:** when a test fails, a screenshot of the browser at that moment is saved to `target/screenshots/`, attached to the test in the Allure report, and its path is printed in the test output.
- **TestNG report:** `target/surefire-reports/index.html`.

On GitHub Actions the suite runs on Chrome and Firefox in parallel jobs. Each job uploads a `test-reports-<browser>` artifact with the Allure report, TestNG reports and any screenshots.

CI also runs every night at 03:00 UTC, so changes on the live site are caught even when nobody pushes. Every run except pull requests also has a **Known defects** job that runs the `known-defect` suite. It is allowed to fail, so it doesn't turn the run red, and its report shows which bugs are still there.

After each run on `main`, the three Allure reports are published to GitHub Pages: [https://nevena-tfstudio.github.io/Ecomm_Website_Automation/](https://nevena-tfstudio.github.io/Ecomm_Website_Automation/).

## Known defects

Tests in the `known-defect` group describe the **correct** behaviour for bugs the site still has, so they fail until the bug is fixed. The regression and smoke suites (and push/PR CI runs) skip them, so the build stays green. When one starts passing, the bug is fixed: move the test into the `regression` group.

| Defect | Test | Details |
|---|---|---|
| Checkout is allowed with an empty cart | `CheckoutTest.checkoutIsBlockedWithEmptyCart` | With nothing in the cart, Checkout still opens the information step, and the user can continue to an overview with no items and a $0.00 total. |
| Invalid-login error text is cut off | `LoginVisualTest.errorMessageFitsInsideErrorBox` | The red box has a fixed 45px height, but the message wraps to 3 lines (60px). Fails at 1920×1080 and 1366×768 in Chrome and Firefox, and at 390×844 in Chrome. |
| Reset App State leaves "Remove" buttons | `MenuTest.resetAppStateRestoresAddToCartButtons` | After adding a product and choosing Reset App State, the cart badge disappears but the product's button on the inventory page still says "Remove" (it only changes after a page reload). |
| Login page has accessibility gaps | `AccessibilityTest.loginPageHasNoBestPracticeViolations` | axe reports `page-has-heading-one` (the page has no h1) and `region` (content is not inside a landmark). |
| Inventory page has no h1 | `AccessibilityTest.inventoryPageHasNoBestPracticeViolations` | axe reports `page-has-heading-one`. |
| Cart page has no h1 | `AccessibilityTest.cartPageHasNoBestPracticeViolations` | axe reports `page-has-heading-one`. |
| problem_user: all product pictures are the same | `ProblemUserTest.productImagesAreDistinct` | Every product shows the same "sl-404" placeholder picture. |
| problem_user: sorting does nothing | `ProblemUserTest.sortingByNameReordersProducts` | Choosing "Name (Z to A)" leaves the products in A-to-Z order. |
| problem_user: some Add to cart buttons are dead | `ProblemUserTest.addToCartWorksForEveryProduct` | For the Bolt T-Shirt, Fleece Jacket and Red T-Shirt the button stays "Add to cart" and the badge does not change. |
| problem_user: Last Name does not keep its value | `ProblemUserTest.checkoutFieldsKeepTypedValues` | On checkout step one the Last Name stays empty and the typed text ends up in First Name (Continue then says "Last Name is required"). |
| error_user: sorting raises an alert | `ErrorUserTest.sortingDoesNotRaiseAlert` | Any sort option shows the alert "Sorting is broken! This error has been reported to Backtrace." and the order does not change. |
| error_user: some add/remove buttons are dead | `ErrorUserTest.addAndRemoveWorkForEveryProduct` | Add to cart does nothing for the Bolt T-Shirt, Fleece Jacket and Red T-Shirt; Remove does nothing for the Backpack, Bike Light and Onesie. |
| error_user: Finish does not place the order | `ErrorUserTest.finishPlacesTheOrder` | Finish on the checkout overview does nothing; the confirmation page never opens. (The Last Name field also loses its text for this user.) |
| visual_user: a product shows the placeholder picture | `VisualUserTest.noProductShowsThePlaceholderImage` | The Backpack shows the "sl-404" placeholder instead of its photo. |
| visual_user: wrong, changing prices | `VisualUserTest.listPriceMatchesDetailPrice` | Inventory prices are random and change on every load (the Backpack shows e.g. $83.73), while the detail page shows the real $29.99. |
| performance_glitch_user: slow login | `PerformanceGlitchUserTest.loginIsFast` | The inventory appears about 5 s after clicking Login; the test requires 2 s or less (`Limits.MAX_LOGIN_TIME`). |

## Accessibility

`AccessibilityTest` scans the login, inventory and cart pages with [axe-core](https://github.com/dequelabs/axe-core-maven-html) (`support/Accessibility.java`). Each scan attaches the violations as JSON to the Allure report.

- **WCAG 2 A/AA rules** (regression): axe finds no violations on any of the three pages today, so the tests assert the list is empty. Any new A/AA violation, of any impact, fails the build.
- **Best-practice rules** (known-defect): axe's non-WCAG "best-practice" rules do find gaps, listed below. The tests assert there are none and fail until the site is fixed.

axe cannot judge everything (for example colour contrast on some elements is reported as "needs review", and keyboard or screen-reader use is not covered), so a clean scan is a safety net, not proof of accessibility.

## Special users

SauceDemo's special users (`problem_user`, `error_user`, `performance_glitch_user`, `visual_user`, all with the normal password) are built to misbehave. `SpecialUsersTest` checks that all four can log in (regression). Their bugs are covered by known-defect tests that each log in as their own user (via `UserBaseTest`), listed in the table above.

## Configuration

`src/test/resources/config.properties` holds the defaults. Any key can be overridden with `-D<key>=<value>`.

| Key | Meaning |
|---|---|
| `base.url` | Site under test |
| `username` / `password` | Valid login (SauceDemo's public demo account) |
| `browser` | `chrome` (default) or `firefox` |
| `headless` | `true` runs the browser without a window |
| `timeout.seconds` | Maximum wait for page elements |
