package com.nevena.ecommerce.tests;

import java.time.Duration;

import com.nevena.ecommerce.config.Config;
import com.nevena.ecommerce.data.Limits;
import com.nevena.ecommerce.data.Users;
import com.nevena.ecommerce.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/** The performance_glitch_user bug. The test is in "known-defect" and fails until the site is fixed. */
public class PerformanceGlitchUserTest extends UserBaseTest {
    /*
     * KNOWN DEFECT (found 2026-11-19): after clicking Login, performance_glitch_user waits about 5 seconds
     * before the inventory appears (other users: well under 1 second). The measured time includes the
     * page object's own wait, so the page-object timeout (10 s) does not hide the slowness; only a login
     * slower than that timeout would fail with a TimeoutException instead of the assertion below.
     */
    @Test(groups = "known-defect",
            description = "KNOWN DEFECT: performance_glitch_user reaches the inventory within the login time limit")
    public void loginIsFast() {
        LoginPage loginPage = openLogin();

        long start = System.nanoTime();
        loginPage.loginAs(Users.PERFORMANCE_GLITCH, Config.password());
        Duration elapsed = Duration.ofNanos(System.nanoTime() - start);

        Assert.assertTrue(elapsed.compareTo(Limits.MAX_LOGIN_TIME) <= 0,
                "Login took " + elapsed.toMillis() + " ms, limit is " + Limits.MAX_LOGIN_TIME.toMillis() + " ms");
    }
}
