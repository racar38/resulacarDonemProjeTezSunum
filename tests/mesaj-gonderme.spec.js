const { test, expect } = require('@playwright/test');
const pidusage = require('pidusage');
const fs = require('fs');
const path = require('path');

let logInterval;
let logCounter = 0;

function startLogger(logName) {
  const filePath = path.join(__dirname, 'loglar', `log_${logName}.txt`);
  fs.mkdirSync(path.dirname(filePath), { recursive: true });

  const pid = process.pid; // 👉 KENDİ Node.js süreci

  logInterval = setInterval(async () => {
    try {
      const stats = await pidusage(pid);
      const ramMB = (stats.memory / 1024 / 1024).toFixed(2);
      const cpu = stats.cpu.toFixed(2);
      const logEntry = `${new Date().toISOString()} | RAM (MB): ${ramMB} | CPU (%): ${cpu}\n`;
      fs.appendFileSync(filePath, logEntry);
    } catch (err) {
      console.error('Log yazılamadı:', err);
    }
  }, 1000);
}

function stopLogger() {
  if (logInterval) {
    clearInterval(logInterval);
  }
}

test.describe('30 Kez Çalışan Test Grubu', () => {
  test('Mesaj Gönderme Hata Senaryosu', async ({ page }) => {
    logCounter++;
    startLogger(logCounter);

    await page.setViewportSize({ width: 1920, height: 1080 });

    await page.goto('https://oys.yesevi.edu.tr');
    await page.waitForTimeout(1000);
    await page.fill('input[name="kullanici_adi"]', '26204569464');
    await page.waitForTimeout(1000);
    await page.fill('input[name="sifre"]', '935AAB18');
    await page.waitForTimeout(1000);
    await page.click('text=Giriş Yap');
    await page.waitForTimeout(1000);
    await page.waitForSelector('text=RESUL ACAR', { timeout: 15000 });
    await page.waitForTimeout(1000);
    await page.click('text=MESAJLAR');
    await page.waitForTimeout(1000);
    await page.click('button#new_input:has-text("YENİ MESAJ")');
    await page.waitForTimeout(1000);
    await page.fill('input[name="kime"]', 'acar-resul-38@hotmail.com');
    await page.waitForTimeout(1000);
    await page.fill('input[name="konu"]', 'Test Deneme');
    await page.waitForTimeout(1000);

    const frame = page.frameLocator('iframe.cke_wysiwyg_frame');
    await frame.locator('body.cke_editable')
      .fill('Mobil ve Web otomasyonu için yazılan senaryolar...');
    await page.waitForTimeout(1000);

    await page.locator('button.btn.green:has-text("Gönder")')
      .first()
      .click();
    await page.waitForTimeout(1000);

    const hataMesaji = await page
      .locator('text=Mesaj Gönderiminde Hata Oluştu')
      .textContent();

    expect(hataMesaji).toContain('Hata');

    stopLogger();
  });
});
