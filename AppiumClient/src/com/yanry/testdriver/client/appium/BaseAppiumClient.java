package com.yanry.testdriver.client.appium;

import com.yanry.testdriver.ui.mobile.model.base.StateToCheck;
import com.yanry.testdriver.ui.mobile.model.base.process.StartProcess;
import com.yanry.testdriver.ui.mobile.model.base.window.Window;
import com.yanry.testdriver.ui.mobile.model.distribute.HttpClientReception;
import com.yanry.testdriver.ui.mobile.model.extend.action.Click;
import com.yanry.testdriver.ui.mobile.model.extend.action.EnterText;
import com.yanry.testdriver.ui.mobile.model.extend.expectation.GeneralExpectation;
import com.yanry.testdriver.ui.mobile.model.extend.expectation.Toast;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import lib.common.model.json.JSONArray;
import lib.common.model.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by rongyu.yan on 4/13/2017.
 */
public abstract class BaseAppiumClient extends HttpClientReception {
    protected AppiumDriver<MobileElement> driver;

    public BaseAppiumClient(String baseUrl, String charset, AppiumDriver<MobileElement> driver) {
        super(baseUrl, charset);
        this.driver = driver;
    }

    protected abstract boolean isWindowPresent(String windowTag, boolean isWithin, int second);

    protected abstract int checkState(JSONObject property, JSONArray options, boolean isWithin, int second);

    protected abstract MobileElement findView(JSONObject view);

    protected abstract boolean verifyExpectation(String tag, boolean isWithin, int second, int durationMillis);

    protected abstract int handleCustomInstruction(JSONObject instruction);

    @Override
    public void start() throws Exception {
        driver.closeApp();
        super.start();
        driver.quit();
    }

    @Override
    protected int handleInstruction(int repeat, JSONObject instruction) {
        if (repeat == 0) {
            String type = instruction.getString(".");
            if (type.equals(StateToCheck.class.getSimpleName())) {
                JSONObject timing = instruction.getJSONObject("timing");
                JSONObject property = instruction.getJSONObject("property");
                boolean isWithin = timing.getBoolean("isWithin");
                int second = timing.getInt("second");
                if (property.getString(".").equals(Window.WindowState.class.getSimpleName())) {
                    return isWindowPresent(property.getJSONObject("window").getString("tag"), isWithin, second) ? 1
                            : 0;
                }
                return checkState(property, instruction.getJSONArray("options"), isWithin, second);
            }
            if (type.equals(GeneralExpectation.class.getSimpleName())) {
                JSONObject timing = instruction.getJSONObject("timing");
                return verifyExpectation(instruction.getString("tag"), timing.getBoolean("isWithin"), timing.getInt
                        ("second"), instruction.getInt("duration")) ? 1 : 0;
            }
            if (type.equals(Toast.class.getSimpleName())) {
                // TODO
                // {".":"Toast","message":"无网络连接","duration":2000,"timing":{".":"Timing","second":0,"isWithin":false}}
                WebDriverWait wait = new WebDriverWait(driver,2);
                Assert.assertNotNull(wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(".//*[contains" +
                        "(@text,'"+ instruction.getString("message") + "')]"))));
            }
            if (type.equals(StartProcess.class.getSimpleName())) {
                driver.launchApp();
                return 1;
            }
            if (type.equals(Click.class.getSimpleName())) {
                findElement(instruction).click();
                return 1;
            }
            if (type.equals(EnterText.class.getSimpleName())) {
                findElement(instruction).sendKeys(instruction.getString("text"));
                return 1;
            }

            return handleCustomInstruction(instruction);
        }
        throw new RuntimeException("invalid feedback for instruction: " + instruction);
    }

    private MobileElement findElement(JSONObject instruction) {
        JSONObject view = instruction.getJSONObject("view");
        MobileElement element = findView(view);
        if (element == null) {
            throw new RuntimeException("can't find element: " + view);
        }
        return element;
    }
}
