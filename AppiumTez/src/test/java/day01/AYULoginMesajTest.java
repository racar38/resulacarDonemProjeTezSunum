package day01;

import io.appium.java_client.android.AndroidDriver;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.*;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

public class AYULoginMesajTest {

    static AndroidDriver driver;

    // CPU ve RAM logunu alan method
    public void logCpuRamUsage(int iteration) {
        try {
            Process cpuProcess = Runtime.getRuntime().exec("adb shell dumpsys cpuinfo | grep com.android.chrome");
            Process memProcess = Runtime.getRuntime().exec("adb shell dumpsys meminfo com.android.chrome");

            BufferedReader cpuReader = new BufferedReader(new InputStreamReader(cpuProcess.getInputStream()));
            BufferedReader memReader = new BufferedReader(new InputStreamReader(memProcess.getInputStream()));

            String cpuLine = cpuReader.readLine();
            String memLine = memReader.readLine();

            String logLine = LocalDateTime.now() + " | " + (cpuLine != null ? cpuLine : "") + " | " + (memLine != null ? memLine : "");

            FileWriter fw = new FileWriter("appium_kaynak_log.txt", true);
            fw.write("Koşum #" + iteration + ": " + logLine + "\n");
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMesaj30Kez() throws Exception {
        for (int i = 1; i <= 30; i++) {
            System.out.println("🔁 Test koşumu: " + i);
            setupDriver();
            runTest(i);
            driver.quit();
        }
    }

    public void setupDriver() throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();

        caps.setCapability("platformName", "Android");
        caps.setCapability("appium:platformVersion", "16.0");
        caps.setCapability("appium:deviceName", "Pixel 4 H");
        caps.setCapability("appium:automationName", "UiAutomator2");
        caps.setCapability("browserName", "Chrome");
        caps.setCapability("appium:chromedriverExecutable", "C:\\Tools\\chromedriver-win64\\chromedriver.exe");

        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), caps);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    public void runTest(int iteration) throws InterruptedException {
        driver.get("https://oys.yesevi.edu.tr");
        Thread.sleep(3000);

        Set<String> contextler = driver.getContextHandles();
        for (String context : contextler) {
            if (context.toLowerCase().contains("chrom")) {
                driver.context(context);
                break;
            }
        }

        try {
            driver.findElement(By.name("kullanici_adi")).sendKeys("26204569464");
            driver.findElement(By.name("sifre")).sendKeys("935AAB18");

            WebElement girisButonu = driver.findElement(By.xpath("//*[text()='Giriş Yap']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", girisButonu);
            Thread.sleep(3000);

            driver.findElement(By.linkText("MESAJLAR")).click();
            Thread.sleep(1000);

            driver.findElement(By.xpath("//button[@id='new_input' and contains(., 'YENİ MESAJ')]")).click();
            Thread.sleep(1000);

            driver.findElement(By.name("kime")).sendKeys("acar-resul-38@hotmail.com");
            driver.findElement(By.name("konu")).sendKeys("Test Senaryosu");

            driver.switchTo().frame(driver.findElement(By.cssSelector("iframe.cke_wysiwyg_frame")));
            driver.findElement(By.cssSelector("body.cke_editable")).sendKeys("Mobil ve Web otomasyonu için yazılan senaryolar...");
            driver.switchTo().defaultContent();

            driver.findElement(By.xpath("//button[text()='Gönder']")).click();

            WebElement hataMesaji = driver.findElement(By.xpath("//*[contains(text(),'Mesaj Gönderiminde Hata Oluştu')]"));
            assert hataMesaji.getText().contains("Hata");
            System.out.println("✅ Hata mesajı bulundu. Test başarılı.");

        } catch (Exception e) {
            System.out.println("❌ Test sırasında hata: " + e.getMessage());
        }

        // Performans logla
        logCpuRamUsage(iteration);
    }
}
