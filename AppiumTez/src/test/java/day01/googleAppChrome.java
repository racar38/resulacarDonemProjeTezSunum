package day01;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class googleAppChrome {

    @Test
    public void hesapMak() throws MalformedURLException {

        DesiredCapabilities caps = new DesiredCapabilities();

        caps.setCapability("platformName", "Android");
        caps.setCapability("appium:platformVersion", "16.0");
        caps.setCapability("appium:deviceName", "Pixel 4 H");
        caps.setCapability("appium:automationName", "UiAutomator2");
        caps.setCapability("appium:app", "C:\\Users\\resul.acar\\IdeaProjects\\AppiumTez\\Apps\\Google Chrome_138.0.7204.179_APKPure.xapk");

        AndroidDriver driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), caps);
    }
}

