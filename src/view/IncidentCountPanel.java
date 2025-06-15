package view;

//	IMPORTS
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.table.*;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;

import java.io.IOException;
import controller.IncidentCountDataController;
import model.Institution;

@SuppressWarnings("serial")
public class IncidentCountPanel extends JPanel {

//	FIELDS --------------------------------------------------------------------------------------------------------

//	Constants
	private static final int PANEL_WIDTH = 1366;
	private static final int PANEL_HEIGHT = 768;
	private static final int TOOLBOX_WIDTH = 395;

// 	Colors
	private final Color pink = Color.decode("#F8D0C7");
	private final Color turquoise = Color.decode("#83ABBC");
	private final Color navy = Color.decode("#000080");
	private final Color darkNavy = Color.decode("#060644");

// 	Components
	private IncidentCountDataController dataController = new IncidentCountDataController();
	private JLabel titleLabel;
	private JLabel toolLabel;
	private SearchField searchField;
	private JComboBox<String> sortComboBox;
	private JComboBox<String> viewComboBox;
	private RoundedButton colourButton;
	private RoundedButton exportButton;

// 	Chart components
	private ChartPanel chartPanel;
	private JFreeChart overlaidChart;
	private JFreeChart combinedChart;
	private JTable table;
	private JScrollPane scrollPane;

// 	CONSTRUCTOR ---------------------------------------------------------------------------------------------------
	public IncidentCountPanel() {
		initializePanel();
		createTitle();
		createToolbox();
		createCharts();
	}

// 	INITIALIZE PANEL ----------------------------------------------------------------------------------------------
	private void initializePanel() {
		setBounds(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
		setLayout(null);
		setBackground(pink);
		setOpaque(true);
	}

// 	CREATE TITLE --------------------------------------------------------------------------------------------------
	private void createTitle() {
		titleLabel = new JLabel("Incident Count");
		titleLabel.setFont(new Font("Cooper Black", Font.BOLD, 45));
		titleLabel.setBounds(300, 20, 800, 50);
		add(titleLabel);
	}

// 	CREATE TOOLBOX ------------------------------------------------------------------------------------------------
	private void createToolbox() {
		int toolboxX = PANEL_WIDTH - TOOLBOX_WIDTH - 100;

//     	Toolbox label
		toolLabel = new JLabel("TOOLBOX");
		toolLabel.setFont(new Font("Cooper Black", Font.BOLD, 34));
		toolLabel.setBounds(toolboxX, 90, TOOLBOX_WIDTH, 40);
		add(toolLabel);

//      Search field
		searchField = new SearchField();
		searchField.setBounds(toolboxX, 170, TOOLBOX_WIDTH, 40);
		searchField.setPlaceholder("Search institution...");
		searchField.setSearchAction(searchText -> {
			if (!searchText.isEmpty() && !searchText.equals("Search institution...")) {
				updateSuggestions();
				displaySearchResult(searchText);
			}
		});

		// Add focus listener to handle suggestion display
		searchField.getTextField().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (!searchField.getSuggestionListModel().isEmpty()) {
					searchField.showSuggestions();
				}
			}
		});

		add(searchField);

//      Sort combo box
		sortComboBox = new JComboBox<>(new String[] { "Institution -- Alphabetical", "Number of Incidents -- Ascending",
				"Number of Incidents -- Descending", "Use of Force Rate -- Ascending",
				"Use of Force Rate -- Descending" });
		styleComboBox(sortComboBox);
		sortComboBox.setBounds(toolboxX, 230, TOOLBOX_WIDTH, 35);
		sortComboBox.addActionListener(e -> updateChart());
		add(sortComboBox);

//    	View combo box
		viewComboBox = new JComboBox<>(new String[] { "Overlaid Chart", "Combined Chart", "Table" });
		styleComboBox(viewComboBox);
		viewComboBox.setBounds(toolboxX, 280, TOOLBOX_WIDTH, 35);
		viewComboBox.addActionListener(e -> updateChart());
		add(viewComboBox);

//      Colour button
		colourButton = new RoundedButton("Change Colors", navy, darkNavy);
		colourButton.setFont(new Font("Calibri", Font.BOLD, 16));
		colourButton.setBounds(toolboxX, 350, 180, 40);
		add(colourButton);

//     	Export Button
		exportButton = new RoundedButton("Export as PNG", navy, darkNavy);
		exportButton.setFont(new Font("Calibri", Font.BOLD, 16));
		exportButton.setBounds(toolboxX + 210, 350, 180, 40);
		add(exportButton);
	}

// 	STYLE COMBO BOX -----------------------------------------------------------------------------------------------
	private void styleComboBox(JComboBox<String> comboBox) {
		comboBox.setFont(new Font("Calibri", Font.PLAIN, 14));
		comboBox.setBackground(Color.WHITE);
	}

//	CREATE CHARTS -------------------------------------------------------------------------------------------------
	private void createCharts() {
		buildOverlaidChart();
	}

// 	UPDATE SUGGESTIONS --------------------------------------------------------------------------------------------
	public void updateSuggestions() {
		String input = searchField.getText().toLowerCase();

		// Skip if empty or placeholder text
		if (input.isEmpty() || input.equals("search institution...")) {
			searchField.setSuggestions(new ArrayList<>());
			return;
		}

		// Add matching institutions
		@SuppressWarnings("static-access")
		java.util.List<String> suggestions = dataController.getDataList().stream().map(Institution::getName)
				.filter(name -> name.toLowerCase().contains(input)).collect(Collectors.toList());

		searchField.setSuggestions(suggestions);

		// Immediately show suggestions if the field has focus
		if (searchField.getTextField().hasFocus() && !suggestions.isEmpty()) {
			searchField.showSuggestions();
		}
	}

//	DISPLAY SEARCH RESULTS ----------------------------------------------------------------------------------------
	@SuppressWarnings("static-access")
	public void displaySearchResult(String name) {
		Institution selectedInstitution = dataController.getDataList().stream()
				.filter(institution -> institution.getName().equalsIgnoreCase(name)).findFirst().orElse(null);

		if (selectedInstitution != null) {
			updateChartWithHighlight(selectedInstitution);
		}
	}

// 	UPDATE CHART WITH HIGHLIGHT -----------------------------------------------------------------------------------
	private void updateChartWithHighlight(Institution institution) {
		String selectedView = (String) viewComboBox.getSelectedItem();

		switch (selectedView) {
		case "Overlaid Chart":
			highlightInOverlaidChart(institution);
			break;
		case "Combined Chart":
			highlightInCombinedChart(institution);
			break;
		case "Table":
			highlightInTable(institution);
			break;
		}
	}

//	UPDATE CHART --------------------------------------------------------------------------------------------------
	public void updateChart() {

//		Remove current chart or table display from the panel
		if (chartPanel != null) {
			remove(chartPanel);
			chartPanel = null;
		} else if (scrollPane != null) {
			remove(scrollPane);
			scrollPane = null;
		}
//		Get the selected view from the combo box
		String selectedView = (String) viewComboBox.getSelectedItem();

//		Call the appropriate method based on selected view
		if (selectedView.equals("Overlaid Chart")) {
			buildOverlaidChart();
		} else if (selectedView.equals("Combined Chart")) {
			buildCombinedChart();
		} else if (selectedView.equals("Table")) {
			buildTable();
		}

//		Refresh the panel
		revalidate();
		repaint();
	}

//	BUILD OVERLAID CHART ------------------------------------------------------------------------------------------
	public void buildOverlaidChart() {

//		Create a new plot with incidentDataset as the base (bar chart)
		CategoryPlot plot = new CategoryPlot();
		plot.setDataset(0, dataController.getIncidentDataset());
		plot.setRenderer(0, new BarRenderer());
		plot.getRenderer().setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
		plot.getRenderer().setSeriesPaint(0, navy);

//	    Overlay the forceRateDataset as a line chart
		plot.setDataset(1, dataController.getForceRateDataset());
		plot.setRenderer(1, new LineAndShapeRenderer());
		plot.getRenderer().setSeriesPaint(1, turquoise);

//	    Domain
		CategoryAxis domainAxis = new CategoryAxis("Institution");
		domainAxis.setLabelFont(new Font("Calibri", Font.PLAIN, 14));
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 3.0) // 60
																													// degrees
		);
		plot.setDomainAxis(domainAxis);
		plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

//	   	Range
		NumberAxis rangeAxis = new NumberAxis("Values");
		rangeAxis.setLabelFont(new Font("Calibri", Font.PLAIN, 14));
		plot.setRangeAxis(rangeAxis);
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

//	    Make both datasets use the same axes
		plot.mapDatasetToRangeAxis(0, 0); // incidentDataset → range axis 0
		plot.mapDatasetToRangeAxis(1, 0); // forceRateDataset → same range axis

//	    Set rendering order to make sure line is on top
		plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

//	    Create the chart
		overlaidChart = new JFreeChart(" ", JFreeChart.DEFAULT_TITLE_FONT, plot, true);

//	    Remove old chart panel if needed
		if (chartPanel != null)
			remove(chartPanel);

//	    Create and add new chart panel
		chartPanel = new ChartPanel(overlaidChart);
		chartPanel.setBounds(20, 90, 800, 540);
		chartPanel.setDisplayToolTips(true);
		add(chartPanel);
		revalidate();
		repaint();
	}

//	HIGHLIGHT IN OVERLAID CHART -----------------------------------------------------------------------------------
	public void highlightInOverlaidChart(Institution institution) {

//		Rebuild the chart with highlighting
		CategoryPlot plot = new CategoryPlot();

//	    Set datasets
		plot.setDataset(0, dataController.getIncidentDataset());
		plot.setDataset(1, dataController.getForceRateDataset());

//	    Create custom renderers with highlighting
		BarRenderer barRenderer = new BarRenderer() {
			@Override
			public Paint getItemPaint(int row, int column) {
				String category = (String) getPlot().getDataset().getColumnKey(column);
				if (category.equals(institution.getName())) {
					return Color.RED; // Highlight color
				}
				return navy; // Default color
			}
		};

		LineAndShapeRenderer lineRenderer = new LineAndShapeRenderer() {
			@Override
			public Paint getItemPaint(int row, int column) {
				String category = (String) getPlot().getDataset().getColumnKey(column);
				if (category.equals(institution.getName())) {
					return Color.YELLOW; // Highlight color
				}
				return turquoise; // Default color
			}

			@Override
			public Stroke getItemStroke(int row, int column) {
				String category = (String) getPlot().getDataset().getColumnKey(column);
				if (category.equals(institution.getName())) {
					return new BasicStroke(3.0f); // Thicker line for highlighted item
				}
				return super.getItemStroke(row, column);
			}
		};

		plot.setRenderer(0, barRenderer);
		plot.setRenderer(1, lineRenderer);

//	    Rest of chart setup code
		CategoryAxis domainAxis = new CategoryAxis("Institution");
		domainAxis.setLabelFont(new Font("Calibri", Font.PLAIN, 14));
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 3.0));
		plot.setDomainAxis(domainAxis);

		NumberAxis rangeAxis = new NumberAxis("Values");
		rangeAxis.setLabelFont(new Font("Calibri", Font.PLAIN, 14));
		plot.setRangeAxis(rangeAxis);

		plot.mapDatasetToRangeAxis(0, 0);
		plot.mapDatasetToRangeAxis(1, 0);
		plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

		overlaidChart = new JFreeChart(" ", JFreeChart.DEFAULT_TITLE_FONT, plot, true);

//	    Update chart panel
		if (chartPanel != null) {
			remove(chartPanel);
		}
		chartPanel = new ChartPanel(overlaidChart);
		chartPanel.setBounds(20, 90, 800, 540);
		add(chartPanel);

		revalidate();
		repaint();
	}

//	BUILD COMBINED CHART ------------------------------------------------------------------------------------------
	@SuppressWarnings("deprecation")
	public void buildCombinedChart() {

//		Top plot: Use of force rate (line chart)
		CategoryPlot forceRatePlot = new CategoryPlot();
		forceRatePlot.setDataset(dataController.getForceRateDataset());
		forceRatePlot.setRenderer(new LineAndShapeRenderer());
		forceRatePlot.setRangeAxis(new NumberAxis("Use of Force Rate"));
		forceRatePlot.getRangeAxis().setLabelFont(new Font("Calibri", Font.PLAIN, 14));
		forceRatePlot.getRenderer().setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
		forceRatePlot.getRenderer().setPaint(turquoise);

//	    Bottom plot: Incident count (bar chart)
		CategoryPlot incidentPlot = new CategoryPlot();
		incidentPlot.setDataset(dataController.getIncidentDataset());
		incidentPlot.setRenderer(new BarRenderer());
		incidentPlot.setRangeAxis(new NumberAxis("Number of Incidents"));
		incidentPlot.getRangeAxis().setLabelFont(new Font("Calibri", Font.PLAIN, 14));
		incidentPlot.getRenderer().setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
		incidentPlot.getRenderer().setPaint(navy);

//	    Shared domain axis (institution names)
		CategoryAxis domainAxis = new CategoryAxis("Institution");
		domainAxis.setLabelFont(new Font("Calibri", Font.PLAIN, 14));
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 3.0) // 60
																													// degrees
		);
		forceRatePlot.setDomainAxis(domainAxis);
		incidentPlot.setDomainAxis(domainAxis);

//	    Combined plot: stacked vertically (orientation = VERTICAL)
		CombinedDomainCategoryPlot combinedPlot = new CombinedDomainCategoryPlot(domainAxis);
		combinedPlot.add(forceRatePlot, 1); // top
		combinedPlot.add(incidentPlot, 2); // bottom

//	    Create chart
		combinedChart = new JFreeChart(" ", JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, true);

//	    Remove old chart panel if it exists
		if (chartPanel != null)
			remove(chartPanel);

//	    Create and add new chart panel
		chartPanel = new ChartPanel(combinedChart);
		chartPanel.setBounds(20, 90, 800, 540);
		chartPanel.setDisplayToolTips(true);
		add(chartPanel);
		revalidate();
		repaint();
	}

//	HIGHLIGHT IN COMBINED CHART -----------------------------------------------------------------------------------
	public void highlightInCombinedChart(Institution institution) {

//		Top plot: Use of force rate (line chart)
		CategoryPlot forceRatePlot = new CategoryPlot();
		forceRatePlot.setDataset(dataController.getForceRateDataset());

		LineAndShapeRenderer forceRateRenderer = new LineAndShapeRenderer() {
			@Override
			public Paint getItemPaint(int row, int column) {
				String category = (String) getPlot().getDataset().getColumnKey(column);
				if (category.equals(institution.getName())) {
					return Color.RED;
				}
				return turquoise;
			}

			@Override
			public Stroke getItemStroke(int row, int column) {
				String category = (String) getPlot().getDataset().getColumnKey(column);
				if (category.equals(institution.getName())) {
					return new BasicStroke(3.0f);
				}
				return super.getItemStroke(row, column);
			}
		};
		forceRatePlot.setRenderer(forceRateRenderer);
		forceRatePlot.setRangeAxis(new NumberAxis("Use of Force Rate"));

//	    Bottom plot: Incident count (bar chart)
		CategoryPlot incidentPlot = new CategoryPlot();
		incidentPlot.setDataset(dataController.getIncidentDataset());

		BarRenderer incidentRenderer = new BarRenderer() {
			@Override
			public Paint getItemPaint(int row, int column) {
				String category = (String) getPlot().getDataset().getColumnKey(column);
				if (category.equals(institution.getName())) {
					return Color.YELLOW;
				}
				return navy;
			}
		};
		incidentPlot.setRenderer(incidentRenderer);
		incidentPlot.setRangeAxis(new NumberAxis("Number of Incidents"));

//	    Shared domain axis
		CategoryAxis domainAxis = new CategoryAxis("Institution");
		domainAxis.setLabelFont(new Font("Calibri", Font.PLAIN, 14));
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 3.0));

//	    Combined plot
		CombinedDomainCategoryPlot combinedPlot = new CombinedDomainCategoryPlot(domainAxis);
		combinedPlot.add(forceRatePlot, 1);
		combinedPlot.add(incidentPlot, 2);

		combinedChart = new JFreeChart(" ", JFreeChart.DEFAULT_TITLE_FONT, combinedPlot, true);

//	    Update chart panel
		if (chartPanel != null) {
			remove(chartPanel);
		}
		chartPanel = new ChartPanel(combinedChart);
		chartPanel.setBounds(20, 90, 800, 540);
		add(chartPanel);

		revalidate();
		repaint();
	}

//	BUILD TABLE VIEW ----------------------------------------------------------------------------------------------
//	https://www.geeksforgeeks.org/java-swing-jtable/
	public void buildTable() {

		String[] columnNames = { "Institution", "Number of Incidents", "Use of Force Rate" };
		@SuppressWarnings("static-access")
		ArrayList<Institution> list = dataController.getDataList();

//	    Create 2D array from data list
		Object[][] data = new Object[list.size()][3];
		for (int i = 0; i < list.size(); i++) {
			Institution inst = list.get(i);
			data[i][0] = inst.getName();
			data[i][1] = inst.getIncidents();
			data[i][2] = inst.getForceRate();
		}

//	    Create table with a non-editable model
		DefaultTableModel model = new DefaultTableModel(data, columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table = new JTable(model);

//	    Customize the table 
		table.setRowHeight(30);
		table.setFont(new Font("Calibri", Font.PLAIN, 14));
		table.setGridColor(Color.WHITE);
		table.setShowVerticalLines(false);
		table.setFillsViewportHeight(true);
		table.setIntercellSpacing(new Dimension(10, 5));

//	    Header
		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Calibri", Font.BOLD, 16));
		((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
		header.setPreferredSize(new Dimension(header.getWidth(), 40));

//	    Data cells
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

//	    Zebra striping
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			private final Color evenColor = pink;
			private final Color oddColor = Color.WHITE;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (isSelected) {
					c.setBackground(pink); // highlight color
					c.setForeground(Color.BLACK); // optional: ensures text is readable
				} else {
					c.setBackground(row % 2 == 0 ? evenColor : oddColor);
					c.setForeground(Color.BLACK); // reset to default
				}
				return c;

			}
		});

//	  	Adjust column widths
		table.getColumnModel().getColumn(0).setPreferredWidth(400); // Institution
		table.getColumnModel().getColumn(1).setPreferredWidth(120); // Number of Incidents
		table.getColumnModel().getColumn(2).setPreferredWidth(100); // Use of Force Rate

//	    ScrollPane styling
		scrollPane = new JScrollPane(table);
		scrollPane.setBounds(20, 90, 800, 540);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

//	    Add and repaint
		add(scrollPane);
		revalidate();
		repaint();
	}

//	HIGHLIGHT IN TABLE --------------------------------------------------------------------------------------------
	public void highlightInTable(Institution institution) {

//	    Find the row index of the selected institution
		int rowToHighlight = -1;
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getValueAt(i, 0).equals(institution.getName())) {
				rowToHighlight = i;
				break;
			}
		}
//    	Highlight the row
		if (rowToHighlight >= 0) {
			table.setRowSelectionInterval(rowToHighlight, rowToHighlight);
			table.scrollRectToVisible(table.getCellRect(rowToHighlight, 0, true));
		}
	}

//	SAVE CHART AS PNG ---------------------------------------------------------------------------------------------
//	SAVE CHART AS PNG ---------------------------------------------------------------------------------------------
public void saveChart() {
    // Create file chooser dialog
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save Chart as PNG");
    
    // Show save dialog
    int userSelection = fileChooser.showSaveDialog(null);
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File fileToSave = fileChooser.getSelectedFile();
        
        // Ensure file has .png extension
        if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
            fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
        }

        try {
            // Check which chart is currently displayed
            String selectedView = (String) viewComboBox.getSelectedItem();
            JFreeChart chart = null;
            
            if (selectedView.equals("Overlaid Chart")) {
                chart = getOverlaidChart();
            } else if (selectedView.equals("Combined Chart")) {
                chart = getCombinedChart();
            } else if (selectedView.equals("Table")) {
                JOptionPane.showMessageDialog(null, "Cannot export table");
                return;
            }

            // Save chart as PNG (800x600 pixels)
            if (chart != null) {
                ChartUtilities.saveChartAsPNG(fileToSave, chart, 800, 600);
                JOptionPane.showMessageDialog(null, "Chart exported successfully!");
            }
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error saving chart: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
//	GETTERS -------------------------------------------------------------------------------------------------------
	public IncidentCountDataController getDataController() {
		return dataController;
	}

	public void setDataController(IncidentCountDataController dataController) {
		this.dataController = dataController;
	}

	public JLabel getTitleLabel() {
		return titleLabel;
	}

	@SuppressWarnings("rawtypes")
	public JComboBox getSortComboBox() {
		return sortComboBox;
	}

	@SuppressWarnings("rawtypes")
	public JComboBox getViewComboBox() {
		return viewComboBox;
	}

	public RoundedButton getColourButton() {
		return colourButton;
	}

	public RoundedButton getExportButton() {
		return exportButton;
	}

	public ChartPanel getChartPanel() {
		return chartPanel;
	}

	public JFreeChart getOverlaidChart() {
		return overlaidChart;
	}

	public JFreeChart getCombinedChart() {
		return combinedChart;
	}

	public JTable getTable() {
		return table;
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}
}