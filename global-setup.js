const fs = require('fs');

module.exports = async () => {
  fs.appendFileSync('kaynak_kullanimi_log.txt', `\n=== TEST BAŞLADI === ${new Date().toLocaleString()}\n`);
};
