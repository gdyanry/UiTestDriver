package com.yanry.driver.mobile.client.appium;

import com.yanry.driver.core.distribute.HttpClientReception;
import com.yanry.driver.mobile.action.Click;
import com.yanry.driver.mobile.action.EnterText;
import com.yanry.driver.mobile.expectation.Toast;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import lib.common.model.json.JSONArray;
import lib.common.model.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
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

    protected abstract int checkState(JSONObject property, JSONArray options);

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
            if (type.equals(Select.class.getSimpleName())) {
                JSONObject property = instruction.getJSONObject("property");
                return checkState(property, instruction.getJSONArray("options"));
            }
            if (type.equals(Toast.class.getSimpleName())) {
                // TODO
                // {".":"Toast","message":"无网络连接","duration":2000,"timing":{".":"Timing","second":0,"isWithin":false}}
                WebDriverWait wait = new WebDriverWait(driver,2);
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(".//*[contains" +
                        "(@text,'"+ instruction.getString("message") + "')]")));
            }
//            if (type.equals(StartProcess.class.getSimpleName())) {
//                driver.launchApp();
//                return 1;
//            }
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
