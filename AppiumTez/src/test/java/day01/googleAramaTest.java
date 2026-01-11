package day01;

import io.appium.java_client.android.AndroidDriver;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.Keys;


import java.net.MalformedURLException;
import java.net.URL;

public class googleAramaTest {

    @Test
    public void googleAramaTest() throws MalformedURLException, InterruptedException {
        DesiredCapabilities caps = new DesiredCapabilities();

        caps.setCapability("platformName", "Android");
        caps.setCapability("appium:platformVersion", "16.0");
        caps.setCapability("appium:deviceName", "Pixel 4 H");
        caps.setCapability("appium:automationName", "UiAutomator2");
        caps.setCapability("browserName", "Chrome"); // <-- apk gerek yok
        caps.setCapability("appium:chromedriverExecutable", "C:\\Tools\\chromedriver-win64\\chromedriver.exe");


        AndroidDriver driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), caps);

        driver.get("https://www.google.com");
        Thread.sleep(5000);

// Google arama kutusu
        WebElement searchBox = driver.findElement(By.name("q"));
        searchBox.sendKeys("Appium test");
        searchBox.sendKeys(Keys.ENTER);

        Thread.sleep(5000); // Sonuçları gör

        driver.quit();

    }

}
