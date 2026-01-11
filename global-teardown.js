const os = require('os');
const fs = require('fs');

module.exports = async () => {
  const totalMemMB = os.totalmem() / 1024 / 1024;
  const freeMemMB = os.freemem() / 1024 / 1024;
  const usedMemMB = totalMemMB - freeMemMB;

  fs.appendFileSync('kaynak_kullanimi_log.txt', `Kullanılan RAM (MB): ${usedMemMB.toFixed(2)}\n`);
  fs.appendFileSync('kaynak_kullanimi_log.txt', `=== TEST BİTTİ === ${new Date().toLocaleString()}\n`);
};
