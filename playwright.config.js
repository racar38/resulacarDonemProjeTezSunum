// playwright.config.js
module.exports = {
  testDir: './tests',

  reporter: 'html',

  use: {
    headless: false,
    viewport: { width: 1920, height: 1080 },
  },

  timeout: 120 * 1000,

  // 🔴 repeat ve serial BURADA olmalı
  repeatEach: 30,
  workers: 1,

  fullyParallel: false,
};
