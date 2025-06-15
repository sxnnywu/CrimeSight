package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import controller.CrimeSeverityIndexController;
import model.CrimeSeverityIndexModel;

@SuppressWarnings("serial")
public class CrimeSeverityIndexPanel extends JPanel implements ActionListener {
    private String[] provincess = {"Find Province", "Canada", "Newfoundland and Labrador",
            "Prince Edward Island", "Nova Scotia", "New Brunswick", "Quebec", "Ontario", 
            "Ottawa-Gatineau, Ontario/Quebec", "Ottawa-Gatineau, Ontario", "Kingston, Ontario", 
            "Peterborough, Ontario", "Toronto, Ontario", "Hamilton, Ontario", 
            "St.Catharines-Niagara, Ontario", "Kitchener-Cambridge-Waterloo, Ontario", 
            "Brantford, Ontario", "Guelph, Ontario", "London, Ontario", "Windsor, Ontario", 
            "Barrie, Ontario", "Sudbury, Ontario", "Thunder Bay, Ontario", "Manitoba", 
            "Saskatchewan", "Alberta", "British Columbia", "Yukon", "Northwest Territories", 
            "Nunavut", "None"};
    
    private String[] Sorting = {"Sort By:", "Geography (A-Z)", "Geography (Z-A)", "Severity Index (High to Low)", "Severity Index (Low to High)", "None"};

    
    private JLabel title = new JLabel();    
    private JButton changeColors = new JButton();
    private JButton png = new JButton();
    private JButton summary = new JButton();
    
    private JComboBox<String> provinces = new JComboBox<>(provincess);
    private JComboBox<String> sortBy = new JComboBox<>(Sorting);
    
    private JFreeChart currentChart;
    private ChartPanel chartPanel;
    private JPanel chartContainer;
    private CrimeSeverityIndexController controller;

    public CrimeSeverityIndexPanel() {
        setName("Crime Severity Index");
        setSize(1280, 768);
        setBackground(new Color(248,208,199));
        setLayout(null);
        
        filterSection();
        setChart();
        
        // Initialize controller
        controller = new CrimeSeverityIndexController(this);
        
        // Add action listeners
        provinces.addActionListener(this);
        sortBy.addActionListener(this);
        changeColors.addActionListener(this);
        png.addActionListener(this);
        summary.addActionListener(this);
    }

	//working on the title, external buttons & comboboxes (filters)
	private void filterSection() {
		//title
		title.setText("Crime Severity Index");
		title.setFont(new Font("Cooper Black", Font.PLAIN, 20));
		title.setBounds(250,10,350,50);
		this.add(title);
		
		//change colors button
		changeColors.setBounds(1105,500,200,50);
		changeColors.setBackground(new Color(0,0,128));
		changeColors.setForeground(Color.WHITE);
		changeColors.setText("Change Colors");
		changeColors.setFont(new Font ("Calibri", Font.PLAIN, 20));
		this.add(changeColors);

		//export as png button
		png.setBounds(835,500,200,50);
		png.setBackground(new Color(0,0,128));
		png.setForeground(Color.WHITE);
		png.setText("Export to PNG ");
		png.setFont(new Font ("Calibri", Font.PLAIN, 20));
		this.add(png);

		//summary button
		summary.setBounds(965,600,200,50);
		summary.setBackground(new Color(0,0,128));
		summary.setForeground(Color.WHITE);
		summary.setText("Chart Summary");
		summary.setFont(new Font("Calibri", Font.PLAIN, 20));;
		this.add(summary);
		
		sortBy.setBounds(835,50,475,75);
		sortBy.setFont(new Font("Calibri", Font.PLAIN, 20));
		this.add(sortBy);
		
		provinces.setBounds(835,325,475,75);
		provinces.setFont(new Font("Calibri", Font.PLAIN, 20));
		this.add(provinces);

	}

	private void setChart() {
	    // Initialize chart container
	    chartContainer = new JPanel(new BorderLayout());
	    chartContainer.setBounds(110, 50, 600, 600);
	    chartContainer.setBackground(new Color(0,0,125));
	    chartContainer.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
	    this.add(chartContainer);

	    // Create empty dataset initially
	    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	    
	    // Create chart with empty dataset
	    currentChart = ChartFactory.createStackedBarChart(
	        "Crime Severity Index by Geography",
	        "Geography",
	        "Severity Index",
	        dataset,
	        PlotOrientation.VERTICAL,
	        true,
	        true,
	        false
	    );

	    // Configure chart appearance
	    CategoryPlot plot = currentChart.getCategoryPlot();
	    CategoryAxis domainAxis = plot.getDomainAxis();
	    domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4)); // 45-degree rotation
	    domainAxis.setMaximumCategoryLabelWidthRatio(0.8f); // Allow more space for label text

	    BarRenderer renderer = (BarRenderer) plot.getRenderer();
	    
	    // Set colors for each year
	    Paint[] colors = {
	        new Color(79, 129, 189),   // 2008
	        new Color(192, 80, 77),    // 2009
	        new Color(155, 187, 89),   // 2010
	        new Color(128, 100, 162),  // 2011
	        new Color(247, 150, 70)    // 2012
	    };
	    
	    for (int i = 0; i < colors.length; i++) {
	        renderer.setSeriesPaint(i, colors[i]);
	    }
	    
	    plot.setBackgroundPaint(Color.WHITE);
	    plot.setRangeGridlinePaint(Color.GRAY);
	    
	    // Initial update with empty chart
	    updateChart(dataset);
	    
	    // Load data after UI is set up
	    SwingUtilities.invokeLater(() -> {
	        if (controller != null) {
	            List<CrimeSeverityIndexModel> data = controller.getCrimeDataList();
	            if (data != null && !data.isEmpty()) {
	                DefaultCategoryDataset updatedDataset = createDataset(data);
	                updateChart(updatedDataset);
	            }
	        }
	    });
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
	
	
    public void updateChart(DefaultCategoryDataset dataset) {
        if (chartPanel != null) {
            chartContainer.remove(chartPanel);
        }
        
        if (currentChart != null) {
            currentChart.getCategoryPlot().setDataset(dataset);
            chartPanel = new ChartPanel(currentChart) {
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(800, 500);
                }
            };
            chartPanel.setMouseZoomable(true);
            chartContainer.add(chartPanel, BorderLayout.CENTER);
        } else {
            JLabel errorLabel = new JLabel("No chart data available", SwingConstants.CENTER);
            errorLabel.setFont(new Font("Arial", Font.BOLD, 16));
            chartContainer.add(errorLabel, BorderLayout.CENTER);
        }
        chartContainer.revalidate();
        chartContainer.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == provinces) {
            String selectedProvince = (String) provinces.getSelectedItem();
            controller.handleProvinceFilter(selectedProvince);
        } else if (e.getSource() == sortBy) {
            String selectedSort = (String) sortBy.getSelectedItem();
            controller.handleSortSelection(selectedSort);
        } else if (e.getSource() == png) {
            controller.handleExport();
        } else if (e.getSource() == changeColors) {
            controller.handleColorChange();
        } else if (e.getSource() == summary) {
            controller.handleSummary();
        }
    }
    
    public JFreeChart getCurrentChart() {
        return currentChart;
    }

    public void refreshChart() {
        chartPanel.repaint();
    }

    public JComboBox<String> getProvinceComboBox() {
        return provinces;
    }

    public JComboBox<String> getSortComboBox() {
        return sortBy;
    }
}