const fs = require('fs');
const { ChartJSNodeCanvas } = require('chartjs-node-canvas');

// Genişlik ve yükseklik (px)
const width = 800;
const height = 600;
const chartJSNodeCanvas = new ChartJSNodeCanvas({ width, height });

// Log dosyasını oku
const logData = fs.readFileSync('tests/loglar/log_1.txt', 'utf8').split('\n');

const cpuValues = [];
const ramValues = [];
const labels = [];

logData.forEach((line, index) => {
    const match = line.match(/RAM \(MB\): (\d+).*CPU \(%\): ([\d.]+)/);
    if (match) {
        const ram = parseInt(match[1]);
        const cpu = parseFloat(match[2]);
        ramValues.push(ram);
        cpuValues.push(cpu);
        labels.push((index + 1).toString());
    }
});

const config = {
    type: 'line',
    data: {
        labels: labels,
        datasets: [
            {
                label: 'CPU (%)',
                data: cpuValues,
                borderColor: 'red',
                tension: 0.3
            },
            {
                label: 'RAM (MB)',
                data: ramValues,
                borderColor: 'blue',
                tension: 0.3
            }
        ]
    },
    options: {
        plugins: {
            title: {
                display: true,
                text: 'Playwright Test - CPU ve RAM Kullanımı'
            }
        },
        scales: {
            y: {
                beginAtZero: true
            }
        }
    }
};

(async () => {
    const buffer = await chartJSNodeCanvas.renderToBuffer(config);
    fs.writeFileSync('tests/grafik.png', buffer);
    console.log('✅ Grafik başarıyla oluşturuldu: tests/grafik.png');
})();
