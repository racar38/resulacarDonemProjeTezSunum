import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class MesajGondermeTesti {

    WebDriver driver;
    WebDriverWait wait;
    PerformanceLogger logger; // Artık static değil!

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Tarayıcı başlatıldıktan sonra PID al
        logger = new PerformanceLogger();
        logger.start();
    }

    @Test(invocationCount = 30)
    public void mesajGonder() throws InterruptedException {
        driver.get("https://oys.yesevi.edu.tr");
        Thread.sleep(1000);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("kullanici_adi")))
                .sendKeys("26204569464");
        Thread.sleep(1000);

        driver.findElement(By.name("sifre")).sendKeys("935AAB18");
        driver.findElement(By.xpath("//*[text()='Giriş Yap']")).click();
        Thread.sleep(1000);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'RESUL ACAR')]")));

        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("MESAJLAR"))).click();
        Thread.sleep(1000);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@id='new_input' and contains(., 'YENİ MESAJ')]"))).click();
        Thread.sleep(1000);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("kime")))
                .sendKeys("acar-resul-38@hotmail.com");
        Thread.sleep(1000);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("konu")))
                .sendKeys("Test Deneme");
        Thread.sleep(1000);

        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(
                By.cssSelector("iframe.cke_wysiwyg_frame")));
        WebElement mesajIcerik = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("body.cke_editable")));
        mesajIcerik.sendKeys("Mobil ve Web otomasyonu için yazılan senaryolar...");

        driver.switchTo().defaultContent();
        driver.findElement(By.xpath("//button[text()='Gönder']")).click();
        Thread.sleep(1000);

        WebElement hataMesaji = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Mesaj Gönderiminde Hata Oluştu')]")));
        assert hataMesaji.getText().contains("Hata") : "Hata mesajı bekleniyordu ama bulunamadı!";
        System.out.println("❗ Mesaj gönderimi başarısız: Beklenen hata görüntülendi.");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        if (logger != null) {
            logger.stop();
        }
    }

    // --- Chrome.exe RAM / CPU Logger ---
    static class PerformanceLogger {
        private Timer timer = new Timer();
        private final File logFile = new File("kaynak_kullanimi_log.txt");
        private int chromePid = -1;

        public void start() {
            chromePid = getLastStartedChromePID();
            if (chromePid == -1) {
                System.err.println("Chrome.exe PID bulunamadı, loglama yapılamayacak.");
                return;
            }

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                        long ramBytes = executeWmicForValue(
                                "wmic process where ProcessId=" + chromePid + " get WorkingSetSize"
                        );
                        long ramMB = ramBytes / (1024 * 1024);

                        long cpu = executeWmicForValue(
                                "wmic path Win32_PerfFormattedData_PerfProc_Process where IDProcess=" + chromePid + " get PercentProcessorTime"
                        );

                        writer.write(LocalDateTime.now() +
                                " | RAM (MB): " + ramMB +
                                " | CPU (%): " + cpu + "\n");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 1000);
        }

        public void stop() {
            timer.cancel();
        }

        private int getLastStartedChromePID() {
            try {
                Process process = Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq chrome.exe\" /FO CSV /NH");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                int lastPid = -1;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.replace("\"", "").split(",");
                    if (parts.length > 1) {
                        try {
                            lastPid = Integer.parseInt(parts[1].trim());
                        } catch (NumberFormatException ignored) {}
                    }
                }
                return lastPid;
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        }

        private long executeWmicForValue(String command) {
            try {
                Process process = Runtime.getRuntime().exec(command);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                reader.readLine(); // başlık
                String valueLine = reader.readLine();
                if (valueLine != null && !valueLine.trim().isEmpty()) {
                    return Long.parseLong(valueLine.trim());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}
