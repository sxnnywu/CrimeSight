package controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import model.CrimeSeverityIndexModel;
import view.CrimeSeverityIndexPanel;

public class CrimeSeverityIndexController {
    private CrimeSeverityIndexPanel view;
    private static List<CrimeSeverityIndexModel> crimeSeverityIndexDataList = new ArrayList<>();

    public CrimeSeverityIndexController(CrimeSeverityIndexPanel view) {
        this.view = view;
        new CrimeSeverityIndexFileReader(); // This will load the data
        updateViewWithData();
    }
    
    /**
     * Handles the export functionality for the chart
     */
    public void handleExport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Chart as PNG");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
        
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }
            
            try {
                JFreeChart chart = view.getCurrentChart();
                if (chart != null) {
                    BufferedImage image = chart.createBufferedImage(800, 600);
                    ImageIO.write(image, "png", file);
                    JOptionPane.showMessageDialog(null, 
                        "Chart exported successfully to:\n" + file.getAbsolutePath(), 
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, 
                        "No chart available to export", 
                        "Export Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, 
                    "Error exporting chart: " + e.getMessage(), 
                    "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles color change functionality for the chart
     */
    public void handleColorChange() {
        CategoryPlot plot = (CategoryPlot) view.getCurrentChart().getPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        
        // Create color selection dialog
        JPanel colorPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        colorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Store color buttons and current colors
        JButton[] colorButtons = new JButton[5];
        Color[] currentColors = new Color[5];
        
        // Get current colors
        for (int i = 0; i < 5; i++) {
            currentColors[i] = (Color) renderer.getSeriesPaint(i);
            if (currentColors[i] == null) {
                // Default colors if not set
                currentColors[i] = new Color[] {
                    new Color(79, 129, 189),   // 2008
                    new Color(192, 80, 77),    // 2009
                    new Color(155, 187, 89),   // 2010
                    new Color(128, 100, 162),  // 2011
                    new Color(247, 150, 70)    // 2012
                }[i];
            }
            
            JLabel yearLabel = new JLabel("Year " + (2008 + i) + " color:");
            colorPanel.add(yearLabel);
            
            colorButtons[i] = new JButton();
            colorButtons[i].setBackground(currentColors[i]);
            colorButtons[i].setPreferredSize(new Dimension(25, 25));
            final int index = i;
            colorButtons[i].addActionListener(e -> {
                Color newColor = JColorChooser.showDialog(
                    colorPanel, 
                    "Choose Color for Year " + (2008 + index),
                    currentColors[index]
                );
                if (newColor != null) {
                    currentColors[index] = newColor;
                    colorButtons[index].setBackground(newColor);
                }
            });
            colorPanel.add(colorButtons[i]);
        }
        
        int result = JOptionPane.showConfirmDialog(
            null, 
            colorPanel, 
            "Change Chart Colors",
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            // Apply new colors
            for (int i = 0; i < 5; i++) {
                renderer.setSeriesPaint(i, currentColors[i]);
            }
            view.refreshChart();
        }
    }

    /**
     * Handles the chart summary functionality
     */
    public void handleSummary() {
        String summaryText = "<html><div style='width:400px;'><h2>Crime Severity Index Summary</h2>";
        
        // Calculate average severity by year
        Map<Integer, Double> yearAverages = new HashMap<>();
        Map<Integer, Integer> yearCounts = new HashMap<>();
        
        for (CrimeSeverityIndexModel item : crimeSeverityIndexDataList) {
            int year = item.getYear();
            yearAverages.put(year, yearAverages.getOrDefault(year, 0.0) + item.getSeverityIndex());
            yearCounts.put(year, yearCounts.getOrDefault(year, 0) + 1);
        }
        
        summaryText += "<h3>Yearly Averages:</h3><ul>";
        for (int year = 2008; year <= 2012; year++) {
            if (yearAverages.containsKey(year)) {
                double avg = yearAverages.get(year) / yearCounts.get(year);
                summaryText += String.format("<li>%d: %.2f</li>", year, avg);
            }
        }
        summaryText += "</ul>";
        
        // Find highest and lowest severity provinces
        Map<String, Double> provinceTotals = new HashMap<>();
        for (CrimeSeverityIndexModel item : crimeSeverityIndexDataList) {
            provinceTotals.put(item.getGeography(), 
                provinceTotals.getOrDefault(item.getGeography(), 0.0) + item.getSeverityIndex());
        }
        
        if (!provinceTotals.isEmpty()) {
            String maxProvince = Collections.max(provinceTotals.entrySet(), 
                Comparator.comparingDouble(Map.Entry::getValue)).getKey();
            String minProvince = Collections.min(provinceTotals.entrySet(), 
                Comparator.comparingDouble(Map.Entry::getValue)).getKey();
            
            summaryText += String.format(
                "<h3>Extremes:</h3><ul>" +
                "<li>Highest Severity: %s (%.2f)</li>" +
                "<li>Lowest Severity: %s (%.2f)</li></ul>",
                maxProvince, provinceTotals.get(maxProvince),
                minProvince, provinceTotals.get(minProvince)
            );
        }
        
        summaryText += "</div></html>";
        
        JOptionPane.showMessageDialog(
            null, 
            summaryText, 
            "Chart Summary", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    
    public static void setCrimeDataList(List<CrimeSeverityIndexModel> crimeDataList) {
        crimeSeverityIndexDataList = new ArrayList<>(crimeDataList);
    }

    public void updateViewWithData() {
        view.updateChart(createDataset(crimeSeverityIndexDataList));
    }

    private DefaultCategoryDataset createDataset(List<CrimeSeverityIndexModel> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Group data by geography first
        Map<String, Map<Integer, Double>> geoData = new LinkedHashMap<>();
        for (CrimeSeverityIndexModel item : data) {
            geoData.putIfAbsent(item.getGeography(), new TreeMap<>());
            geoData.get(item.getGeography()).put(item.getYear(), item.getSeverityIndex());
        }
        
        // Add data with years as series and geographies as categories
        for (String geography : geoData.keySet()) {
            Map<Integer, Double> yearValues = geoData.get(geography);
            for (Integer year : yearValues.keySet()) {
                dataset.addValue(yearValues.get(year), 
                              year.toString(),
                              geography);
            }
        }
        
        return dataset;
    }

    public void handleProvinceFilter(String selectedProvince) {
        if (selectedProvince.equals("Find Province") || selectedProvince.equals("None")) {
            updateViewWithData();
            return;
        }

        List<CrimeSeverityIndexModel> filteredData = new ArrayList<>();
        for (CrimeSeverityIndexModel item : crimeSeverityIndexDataList) {
            if (item.getGeography().equals(selectedProvince)) {
                filteredData.add(item);
            }
        }
        view.updateChart(createDataset(filteredData));
    }

    public void handleSortSelection(String sortOption) {
        List<CrimeSeverityIndexModel> sortedData = new ArrayList<>(crimeSeverityIndexDataList);

        switch (sortOption) {
            case "Geography (A-Z)":
                Collections.sort(sortedData, Comparator.comparing(CrimeSeverityIndexModel::getGeography));
                view.updateChart(createDataset(sortedData));
                break;

            case "Geography (Z-A)":
                Collections.sort(sortedData, Comparator.comparing(CrimeSeverityIndexModel::getGeography).reversed());
                view.updateChart(createDataset(sortedData));
                break;

            case "Severity Index (High to Low)":
            case "Severity Index (Low to High)":
                // 1. Group and sum severity index values by geography
                Map<String, Double> geoTotals = new HashMap<>();
                for (CrimeSeverityIndexModel item : sortedData) {
                    geoTotals.put(item.getGeography(),
                        geoTotals.getOrDefault(item.getGeography(), 0.0) + item.getSeverityIndex());
                }

                // 2. Sort geographies by total severity index
                List<String> sortedGeographies = new ArrayList<>(geoTotals.keySet());
                sortedGeographies.sort((g1, g2) -> {
                    return sortOption.equals("Severity Index (High to Low)") ?
                            Double.compare(geoTotals.get(g2), geoTotals.get(g1)) :
                            Double.compare(geoTotals.get(g1), geoTotals.get(g2));
                });

                // 3. Reorder original data by sorted geography order
                List<CrimeSeverityIndexModel> reorderedBySeverity = new ArrayList<>();
                for (String geo : sortedGeographies) {
                    for (CrimeSeverityIndexModel item : sortedData) {
                        if (item.getGeography().equals(geo)) {
                            reorderedBySeverity.add(item);
                        }
                    }
                }

                view.updateChart(createDataset(reorderedBySeverity));
                break;

            case "None":
            default:
                view.updateChart(createDataset(crimeSeverityIndexDataList));
                break;
        }
    }

    public JComboBox<String> getProvinceComboBox() {
        return view.getProvinceComboBox();
    }

    public JComboBox<String> getSortComboBox() {
        return view.getSortComboBox();
    }

    public List<CrimeSeverityIndexModel> getCrimeDataList() {
        return crimeSeverityIndexDataList;
    }
}