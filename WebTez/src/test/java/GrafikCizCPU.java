import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class GrafikCizCPU {
    public static void main(String[] args) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (BufferedReader br = new BufferedReader(new FileReader("kaynak_guncel_log.txt"))) {
            int index = 1;
            String line;

            while ((line = br.readLine()) != null) {
                if (line.contains("CPU (%):")) {
                    String[] parts = line.split("CPU \\(%\\): ");
                    if (parts.length > 1) {
                        String cpuStr = parts[1].trim().replace(",", "."); // bölgesel ayrım için
                        try {
                            double cpuDeger = Double.parseDouble(cpuStr);
                            dataset.addValue(cpuDeger, "CPU Kullanımı", "Test " + index++);
                        } catch (NumberFormatException e) {
                            System.err.println("❗ Sayıya çevrilemeyen değer: " + cpuStr);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Test Sonuçları - CPU Kullanımı",
                "Test Tekrarı",
                "CPU (%)",
                dataset
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame frame = new JFrame("Grafik");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(chartPanel);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
