import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GrafikCizRAM {

    public static void main(String[] args) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (BufferedReader br = new BufferedReader(new FileReader("kaynak_guncel_log.txt"))) {
            String line;
            int count = 1;

            while ((line = br.readLine()) != null) {
                if (line.contains("RAM (MB):")) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 2) {
                        String ramPart = parts[1].trim(); // "RAM (MB): 36"
                        String ramStr = ramPart.replace("RAM (MB):", "").trim();
                        try {
                            double ramValue = Double.parseDouble(ramStr);
                            dataset.addValue(ramValue, "RAM (MB)", "Test " + count);
                            count++;
                        } catch (NumberFormatException e) {
                            System.out.println("Hatalı RAM değeri: " + ramStr);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Test Sonuçları - RAM Kullanımı",
                "Test Tekrarı",
                "RAM (MB)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        JFrame frame = new JFrame("Grafik");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }
}
