package view;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.DefaultCategoryDataset;

import controller.IncidentBasedCrimesController;
import model.IncidentBasedCrimesModel;

/**
 * The IncidentBasedCrimesView class represents the graphical user interface for
 * displaying and interacting with incident-based crime statistics. It includes
 * components for data selection, visualization using JFreeChart, and
 * statistical analysis features.
 */
public class IncidentBasedCrimesView {
	// UI Components
	private JPanel incidentBasedCrimePanel; // Main container panel for all components
	private JPanel contentPanel; // Panel containing selection controls and options
	private JScrollPane scrollPane; // Scroll pane to make content panel scrollable

	// Data selection components
	private JComboBox<String>[] barComboBoxes; // Array of combo boxes for crime category selection (5 bars max)
	private JLabel[] barLabels; // Labels for each crime category combo box

	// Year range selection components
	private JSlider startYearSlider; // Slider for selecting start year (2008-2012)
	private JSlider endYearSlider; // Slider for selecting end year (2008-2012)
	private JLabel startYearLabel; // Label for start year slider
	private JLabel endYearLabel; // Label for end year slider
	private JTextField startYearTextField; // Text field displaying selected start year
	private JTextField endYearTextField; // Text field displaying selected end year

	// Action buttons
	private JButton applyButton; // Button to apply selected filters and update chart
	private JButton changeColorsButton; // Button to change chart color scheme
	private CircleButton helpButton; // Custom circular help button
	private JButton exportPngButton; // Button to export chart as PNG image

	// Chart display components
	private JFreeChart currentChart; // Reference to the currently displayed chart
	private ChartPanel chartPanel; // Panel that holds and displays the chart
	private JPanel chartContainer; // Container panel for the chart display

	// Chart display options
	private JLabel chartDisplayLabel; // Label for chart display type selection
	private JRadioButton twoDRadioButton; // Option for 2D bar chart display
	private JRadioButton threeDRadioButton; // Option for 3D bar chart display
	private ButtonGroup chartDisplayGroup; // Group to manage chart type radio buttons

	// Future trends prediction components
	private JLabel futureTrendsLabel; // Label for future trends section
	private JRadioButton futureTrendsRadioButton; // Toggle for enabling/disabling trend projection
	private JTextField projectionYearsTextField; // Input for number of years to project

	// Statistical calculation components
	private JLabel calculateMeanLabel; // Label for mean calculation section
	private JRadioButton[] meanRadioButtons; // Radio buttons to select bars for mean calculation
	private JLabel[] meanResultLabels; // Labels to display calculated mean values

	// Color scheme constants
	private Color pink = Color.decode("#f8d0c7"); // Background color for main panel
	private Color navyBlue = Color.decode("#1C3F60"); // Background color for control panel
	private Color white = Color.WHITE; // Text color for contrast with navyBlue

	// Crime categories constant array
	private final String[] CRIME_CATEGORIES = { "None", "Total, all violations",
			"Total, all Criminal Code violations (including traffic)",
			"Total, all Criminal Code violations (excluding traffic)", "Total violent Criminal Code violations",
			"Homicide", "Total other violations causing death", "Attempted murder",
			"Sexual assault, level 3, aggravated", "Sexual assault, level 2, weapon or bodily harm",
			"Sexual assault, level 1", "Total sexual violations against children", "Assault, level 3, aggravated",
			"Assault, level 2, weapon or bodily harm", "Assault, level 1", "Total Assaults (Level 1, 2 and 3)",
			"Total assaults against a peace officer", "Total other assaults",
			"Total firearms; use of, discharge, pointing", "Total robbery", "Total forcible confinement or kidnapping",
			"Total abduction", "Extortion", "Criminal harassment", "Uttering threats",
			"Threatening or harassing phone calls", "Total other violent violations", "Total property crime violations",
			"Total breaking and entering", "Total possession of stolen property",
			"Total trafficking in stolen property", "Total theft of motor vehicle",
			"Total theft over $5,000 (non-motor vehicle)", "Total theft under $5,000 (non-motor vehicle)", "Fraud",
			"Identity theft", "Identity fraud", "Total mischief", "Arson",
			"Altering, removing or destroying Vehicle Identification Number (VIN)",
			"Total other Criminal Code violations", "Counterfeiting", "Total weapons violations", "Child pornography",
			"Total prostitution", "Disturb the peace", "Total administration of justice violations",
			"Total other violations", "Total Criminal Code traffic violations", "Total impaired driving",
			"Impaired operation, causing death", "Impaired operation (drugs), causing death",
			"Impaired operation, causing bodily harm", "Impaired operation (drugs), causing bodily harm",
			"Impaired operation of motor vehicle, vessel or aircraft",
			"Impaired operation (drugs) vehicle, vessel, aircraft",
			"Impaired operation, failure to provide breath sample", "Failure to comply or refusal (drugs)",
			"Impaired operation, failure to provide blood sample", "Failure to provide blood sample (drugs)",
			"Total other Criminal Code traffic violations", "Dangerous operation, causing death",
			"Dangerous operation, causing bodily harm", "Dangerous operation of motor vehicle, vessel or aircraft",
			"Dangerous operation evading police, causing death",
			"Dangerous operation evading police, causing bodily harm",
			"Dangerous operation of motor vehicle evading police", "Total fail to stop or remain",
			"Driving while prohibited", "Other Criminal Code traffic violations",
			"Causing death by criminal negligence while street racing",
			"Causing bodily harm by criminal negligence while street racing",
			"Dangerous operation causing death while street racing",
			"Dangerous operation causing bodily harm while street racing",
			"Dangerous operation of motor vehicle while street racing", "Total Federal Statute violations",
			"Total drug violations", "Possession, cannabis", "Possession, cocaine",
			"Total, possession, other Controlled Drugs and Substances Act drugs", "Heroin, possession",
			"Other Controlled Drugs and Substances Act, possession", "Methamphetamines (crystal meth), possession",
			"Methylenedioxyamphetamine (ecstasy), possession",
			"Total cannabis, trafficking, production or distribution", "Cannabis, trafficking",
			"Cannabis, importation and exportation", "Cannabis, production",
			"Total cocaine, trafficking, production or distribution", "Cocaine, trafficking",
			"Cocaine, importation and exportation", "Cocaine, production",
			"Total other Controlled Drugs and Substances Act drugs, trafficking, production or distribution",
			"Heroin, trafficking", "Other Controlled Drugs and Substances Act, trafficking",
			"Methamphetamines (crystal meth), trafficking", "Methylenedioxyamphetamine (ecstasy), trafficking",
			"Heroin, importation and exportation",
			"Other Controlled Drugs and Substances Act, importation and exportation",
			"Methamphetamines (crystal meth), importation and exportation",
			"Methylenedioxyamphetamine (ecstasy), importation and exportation", "Heroin, production",
			"Other Controlled Drugs and Substances Act, production", "Methamphetamines (crystal meth), production",
			"Methylenedioxyamphetamine (ecstasy), production", "Precursor or equipment (crystal meth, ecstasy)",
			"Youth Criminal Justice Act", "Total other Federal Statutes", "Bankruptcy Act", "Income Tax Act",
			"Canada Shipping Act", "Canada Health Act", "Customs Act", "Competition Act", "Excise Act",
			"Total Immigration and Refugee Protection Act", "Firearms Act", "National Defense Act",
			"Other federal statutes" };

	/**
	 * Constructs the IncidentBasedCrimesView and initializes all UI components.
	 */
	public IncidentBasedCrimesView() {
		// Main panel setup
		incidentBasedCrimePanel = new JPanel();
		incidentBasedCrimePanel.setLayout(null);
		incidentBasedCrimePanel.setBackground(pink);

		// Create content panel for selection controls
		contentPanel = new JPanel();
		contentPanel.setLayout(null);
		contentPanel.setBackground(navyBlue);
		contentPanel.setPreferredSize(new Dimension(400, 975));

		// Initialize all UI components
		createBarSelectionComponents();
		createYearSliders();
		createChartDisplayOptions();
		createFutureTrendsComponents();
		createMeanCalculationComponents();
		createApplyButton();
		createChartContainer();

		// Create scroll pane for content panel
		scrollPane = new JScrollPane(contentPanel);
		scrollPane.setBounds(900, 10, 400, 550);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		incidentBasedCrimePanel.add(scrollPane);

		// Add action buttons to main panel
		createMainPanelButtons();

		incidentBasedCrimePanel.setPreferredSize(new Dimension(1280, 768));
	}

	// [Rest of the methods remain unchanged but would have similar detailed
	// comments]
	// ... (createBarSelectionComponents, createYearSliders, etc.)

	/**
	 * Creates the category selection combo boxes and their labels.
	 */
	@SuppressWarnings("unchecked")
	private void createBarSelectionComponents() {
		barLabels = new JLabel[5];
		barComboBoxes = new JComboBox[5];

		int yPosition = 20;
		int labelHeight = 20;
		int comboBoxHeight = 30;
		int verticalSpacing = 30;
		int labelWidth = 60;
		int comboBoxWidth = 350;

		for (int i = 0; i < 5; i++) {
			// Create label for each category selector
			barLabels[i] = new JLabel("Bar " + (i + 1));
			barLabels[i].setForeground(white);
			barLabels[i].setFont(new Font("Arial", Font.BOLD, 14));
			barLabels[i].setBounds(20, yPosition, labelWidth, labelHeight);

			// Create combo box for crime category selection
			barComboBoxes[i] = new JComboBox<>();
			barComboBoxes[i].setBounds(20, yPosition + labelHeight + 5, comboBoxWidth, comboBoxHeight);
			barComboBoxes[i].setBackground(Color.WHITE);

			// Populate combo box with crime categories
			for (String category : CRIME_CATEGORIES) {
				barComboBoxes[i].addItem(category);
			}

			// Set default selections
			barComboBoxes[i].setSelectedItem("None");

			// Add components to content panel
			contentPanel.add(barLabels[i]);
			contentPanel.add(barComboBoxes[i]);

			// Update position for next components
			yPosition += labelHeight + comboBoxHeight + verticalSpacing;
		}
	}

	/**
	 * Creates and configures the year range selection sliders and text fields.
	 */
	private void createYearSliders() {
		int yPosition = 450;
		int labelHeight = 20;
		int sliderHeight = 50;
		int verticalSpacing = 30;
		int labelWidth = 100;
		int sliderWidth = 300;
		int textFieldWidth = 50;

		// Start Year components
		startYearLabel = new JLabel("Start Year :");
		startYearLabel.setForeground(white);
		startYearLabel.setFont(new Font("Arial", Font.BOLD, 14));
		startYearLabel.setBounds(20, yPosition, labelWidth, labelHeight);

		startYearSlider = new JSlider(JSlider.HORIZONTAL, 2008, 2012, 2008);
		startYearSlider.setBounds(20, yPosition + labelHeight + 5, sliderWidth, sliderHeight);
		startYearSlider.setMajorTickSpacing(1);
		startYearSlider.setPaintTicks(true);
		startYearSlider.setPaintLabels(true);
		startYearSlider.setBackground(navyBlue);
		startYearSlider.setForeground(white);

		startYearTextField = new JTextField("2008");
		startYearTextField.setBounds(20 + labelWidth + 10, yPosition, textFieldWidth, 25);
		startYearTextField.setHorizontalAlignment(JTextField.CENTER);

		// End Year components
		endYearLabel = new JLabel("End Year :");
		endYearLabel.setForeground(white);
		endYearLabel.setFont(new Font("Arial", Font.BOLD, 14));
		endYearLabel.setBounds(20, yPosition + labelHeight + sliderHeight + verticalSpacing, labelWidth, labelHeight);

		endYearSlider = new JSlider(JSlider.HORIZONTAL, 2008, 2012, 2012);
		endYearSlider.setBounds(20, yPosition + 2 * (labelHeight + 5) + sliderHeight + verticalSpacing, sliderWidth,
				sliderHeight);
		endYearSlider.setMajorTickSpacing(1);
		endYearSlider.setPaintTicks(true);
		endYearSlider.setPaintLabels(true);
		endYearSlider.setBackground(navyBlue);
		endYearSlider.setForeground(white);

		endYearTextField = new JTextField("2012");
		endYearTextField.setBounds(20 + labelWidth + 10, yPosition + labelHeight + sliderHeight + verticalSpacing,
				textFieldWidth, 25);
		endYearTextField.setHorizontalAlignment(JTextField.CENTER);

		// Add components to content panel
		contentPanel.add(startYearLabel);
		contentPanel.add(startYearSlider);
		contentPanel.add(startYearTextField);
		contentPanel.add(endYearLabel);
		contentPanel.add(endYearSlider);
		contentPanel.add(endYearTextField);
	}

	/**
	 * Creates the chart display mode radio buttons (2D/3D)
	 */
	private void createChartDisplayOptions() {
		// Create the label
		chartDisplayLabel = new JLabel("Select Chart Display:");
		chartDisplayLabel.setForeground(white);
		chartDisplayLabel.setFont(new Font("Arial", Font.BOLD, 14));
		chartDisplayLabel.setBounds(20, 650, 150, 20);
		contentPanel.add(chartDisplayLabel);

		// Create radio buttons
		twoDRadioButton = new JRadioButton("2D");
		twoDRadioButton.setSelected(true); // Default to 2D
		twoDRadioButton.setForeground(white);
		twoDRadioButton.setBackground(navyBlue);
		twoDRadioButton.setBounds(20, 685, 60, 20);

		threeDRadioButton = new JRadioButton("3D");
		threeDRadioButton.setForeground(white);
		threeDRadioButton.setBackground(navyBlue);
		threeDRadioButton.setBounds(90, 685, 60, 20);

		// Group the radio buttons
		chartDisplayGroup = new ButtonGroup();
		chartDisplayGroup.add(twoDRadioButton);
		chartDisplayGroup.add(threeDRadioButton);

		// Add to content panel
		contentPanel.add(twoDRadioButton);
		contentPanel.add(threeDRadioButton);

	}

	/**
	 * Creates components for future trends prediction feature
	 */
	private void createFutureTrendsComponents() {
		int yPosition = 750; // Position below the chart display options

		futureTrendsLabel = new JLabel("Display Future Trends:");
		futureTrendsLabel.setFont(new Font("Arial", Font.BOLD, 14));
		futureTrendsLabel.setForeground(white);
		futureTrendsLabel.setBackground(navyBlue);
		futureTrendsLabel.setBounds(20, 720, 180, 20);

		// Future Trends Radio Button
		futureTrendsRadioButton = new JRadioButton("Yes");
		futureTrendsRadioButton.setForeground(white);
		futureTrendsRadioButton.setBackground(navyBlue);
		futureTrendsRadioButton.setBounds(20, yPosition, 180, 20);
		futureTrendsRadioButton.addActionListener(e -> {
			boolean selected = futureTrendsRadioButton.isSelected();
			projectionYearsTextField.setVisible(selected);
			projectionYearsTextField.setEnabled(selected);
		});

		// Projection Years Text Field
		projectionYearsTextField = new JTextField("Enter years to project (e.g. 3)");
		projectionYearsTextField.setBounds(200, yPosition, 170, 20);
		projectionYearsTextField.setVisible(false); // Initially hidden
		projectionYearsTextField.setEnabled(false);
		projectionYearsTextField.setHorizontalAlignment(JTextField.CENTER);

		// Add focus listener to clear default text
		projectionYearsTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (projectionYearsTextField.getText().equals("Enter years to project (e.g. 3)")) {
					projectionYearsTextField.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (projectionYearsTextField.getText().isEmpty()) {
					projectionYearsTextField.setText("Enter years to project (e.g. 3)");
				}
			}
		});

		// Add components to content panel
		contentPanel.add(futureTrendsLabel);
		contentPanel.add(futureTrendsRadioButton);
		contentPanel.add(projectionYearsTextField);
	}

	/**
	 * Creates components for mean calculation feature
	 */
	private void createMeanCalculationComponents() {
		int startY = 780; // Starting Y position below future trends
		int radioButtonWidth = 60;
		int labelWidth = 100;
		int verticalSpacing = 10; // Space between each row
		int rowHeight = 20; // Height of each radio button + label

		// Calculate Mean label (header)
		calculateMeanLabel = new JLabel("Calculate Mean:");
		calculateMeanLabel.setForeground(white);
		calculateMeanLabel.setFont(new Font("Arial", Font.BOLD, 14));
		calculateMeanLabel.setBounds(20, startY, 120, rowHeight);
		contentPanel.add(calculateMeanLabel);

		// Create radio buttons and result labels in vertical layout
		meanRadioButtons = new JRadioButton[5];
		meanResultLabels = new JLabel[5];

		for (int i = 0; i < 5; i++) {
			int yPos = startY + (i + 1) * (rowHeight + verticalSpacing);

			// Radio Button (left side)
			meanRadioButtons[i] = new JRadioButton("Bar " + (i + 1));
			meanRadioButtons[i].setForeground(white);
			meanRadioButtons[i].setBackground(navyBlue);
			meanRadioButtons[i].setBounds(20, yPos, radioButtonWidth, rowHeight);
			contentPanel.add(meanRadioButtons[i]);

			// Result Label (right side, aligned with radio button)
			meanResultLabels[i] = new JLabel("");
			meanResultLabels[i].setForeground(Color.YELLOW);
			meanResultLabels[i].setFont(new Font("Arial", Font.PLAIN, 12));
			meanResultLabels[i].setBounds(20 + radioButtonWidth + 10, // 10px spacing from radio button
					yPos, labelWidth, rowHeight);
			meanResultLabels[i].setVisible(false);
			contentPanel.add(meanResultLabels[i]);
		}
	}

	/**
	 * Creates and positions the main buttons (Change Colors and Export PNG).
	 */
	private void createMainPanelButtons() {
		// Change Colors button
		changeColorsButton = new JButton("Change Colors");
		changeColorsButton.setBounds(225, 580, 195, 40);
		changeColorsButton.setBackground(navyBlue);
		changeColorsButton.setForeground(white);
		changeColorsButton.setFont(new Font("Arial", Font.BOLD, 14));
		changeColorsButton.setBorder(BorderFactory.createLineBorder(white));
		incidentBasedCrimePanel.add(changeColorsButton);
		
		// Help button
	    helpButton = new CircleButton("?"); // Custom circular button with question mark
	    Border blackBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
	    helpButton.setBounds(430, 580, 40, 40); // Positioned between the other buttons
	    helpButton.setBorder(blackBorder); // Consistent border style
	    helpButton.setBackground(navyBlue); // Match other buttons' color scheme
	    helpButton.setForeground(white); // White question mark
	    helpButton.setFont(new Font("Arial", Font.BOLD, 20)); // Larger font for visibility
	    incidentBasedCrimePanel.add(helpButton);

		// Export PNG button
		exportPngButton = new JButton("Export as PNG");
		exportPngButton.setBounds(480, 580, 195, 40);
		exportPngButton.setBackground(navyBlue);
		exportPngButton.setForeground(white);
		exportPngButton.setFont(new Font("Arial", Font.BOLD, 14));
		exportPngButton.setBorder(BorderFactory.createLineBorder(white));
		incidentBasedCrimePanel.add(exportPngButton);
	}

	/**
	 * Creates and configures the Apply button.
	 */
	private void createApplyButton() {
		applyButton = new JButton("Apply");
		applyButton.setBounds(1000, 580, 195, 40);
		applyButton.setBackground(navyBlue);
		applyButton.setForeground(white);
		applyButton.setFont(new Font("Arial", Font.BOLD, 14));
		applyButton.setBorder(BorderFactory.createLineBorder(white));
		incidentBasedCrimePanel.add(applyButton);
	}

	/**
	 * Creates the container panel for the chart visualization.
	 */
	private void createChartContainer() {
		chartContainer = new JPanel(new BorderLayout());
		chartContainer.setBounds(20, 20, 850, 550);
		chartContainer.setBackground(new Color(240, 240, 240)); // Light gray background for modern look
		chartContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		incidentBasedCrimePanel.add(chartContainer);
	}

	/**
	 * Updates the chart visualization based on selected categories and year range.
	 * 
	 * @param selectedCategories The crime categories to display
	 * @param startYear          The starting year of the range
	 * @param endYear            The ending year of the range
	 */
	public void updateChart(String[] selectedCategories, int startYear, int endYear) {
		// Verify data exists
		List<IncidentBasedCrimesModel> allData = IncidentBasedCrimesController.getIncidentDataList();
		if (allData == null || allData.isEmpty()) {
			showErrorMessage("No crime data available");
			return;
		}

		// Create dataset for chart
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		boolean hasValidData = false;

		// Check if we should project future trends
		boolean projectFuture = futureTrendsRadioButton.isSelected();
		int projectionYears = 0;

		if (projectFuture) {
			try {
				String yearsText = projectionYearsTextField.getText();
				if (!yearsText.equals("Enter years to project (e.g. 3)")) {
					projectionYears = Integer.parseInt(yearsText);
				}
			} catch (NumberFormatException e) {
				showErrorMessage("Invalid projection years");
				return;
			}
		}

		// Collect years to display (actual + projected)
		List<String> yearsToDisplay = new ArrayList<>();
		for (int year = startYear; year <= endYear; year++) {
			yearsToDisplay.add(String.valueOf(year)); // Actual years
		}

		// Add projected years if enabled
		if (projectFuture && projectionYears > 0) {
			for (int i = 1; i <= projectionYears; i++) {
				yearsToDisplay.add((endYear + i) + " (Projection)"); // Projected years with label
			}
		}

		// Add data for each selected category
		for (String category : selectedCategories) {
			if (category == null || category.equals("None")) {
				continue;
			}

			// Add data points for each year
			for (String yearLabel : yearsToDisplay) {
				double rate;
				int year;

				// Check if this is a projected year
				boolean isProjection = yearLabel.contains("Projection");

				if (isProjection) {
					year = Integer.parseInt(yearLabel.split(" ")[0]); // Extract year number
					// This is a projected year - calculate trend
					rate = calculateProjectedRate(allData, category, startYear, endYear, year);
				} else {
					year = Integer.parseInt(yearLabel);
					// This is an actual data year
					rate = allData.stream()
							.filter(data -> data.getViolationType().equals(category) && data.getYear() == year)
							.mapToDouble(IncidentBasedCrimesModel::getRatePer100000).findFirst().orElse(0.0);
				}

				dataset.addValue(rate, category, yearLabel);

				if (rate > 0) {
					hasValidData = true;
				}
			}
		}

		// Check if we have valid data to display
		if (!hasValidData) {
			showErrorMessage("No data for selected categories/years");
			return;
		}

		// Create the appropriate chart based on selection
		if (is3DSelected()) {
			currentChart = create3DChart(dataset, startYear, endYear + (projectFuture ? projectionYears : 0));
		} else {
			currentChart = create2DChart(dataset, startYear, endYear + (projectFuture ? projectionYears : 0));
		}

		// Update the display
		updateChartDisplay();
		calculateAndDisplayMean(dataset);
	}

	/**
	 * Creates a 2D bar chart with the given dataset
	 * 
	 * @param dataset   The data to display in the chart
	 * @param startYear The starting year for the chart title
	 * @param endYear   The ending year for the chart title
	 * @return The configured JFreeChart object
	 */
	private JFreeChart create2DChart(DefaultCategoryDataset dataset, int startYear, int endYear) {
		JFreeChart chart = ChartFactory.createBarChart("Crime Rates (" + startYear + "-" + endYear + ")", "Year",
				"Rate per 100,000 Population", dataset, PlotOrientation.VERTICAL, true, true, true);

		CategoryPlot plot = chart.getCategoryPlot();
		BarRenderer renderer = (BarRenderer) plot.getRenderer();

		// Configure 2D appearance
		renderer.setItemMargin(0.2);
		plot.getDomainAxis().setCategoryMargin(0.3);
		renderer.setMaximumBarWidth(0.1);
		plot.setBackgroundPaint(new Color(240, 240, 240));
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		plot.getRangeAxis().setLowerBound(0);

		// Rotate labels for projected years
		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));

		return chart;
	}

	/**
	 * Creates a 3D bar chart with the given dataset
	 * 
	 * @param dataset   The data to display in the chart
	 * @param startYear The starting year for the chart title
	 * @param endYear   The ending year for the chart title
	 * @return The configured JFreeChart object
	 */
	private JFreeChart create3DChart(DefaultCategoryDataset dataset, int startYear, int endYear) {
		JFreeChart chart = ChartFactory.createBarChart3D("Crime Rates (" + startYear + "-" + endYear + ")", "Year",
				"Rate per 100,000 Population", dataset, PlotOrientation.VERTICAL, true, true, true);

		CategoryPlot plot = chart.getCategoryPlot();
		BarRenderer3D renderer = (BarRenderer3D) plot.getRenderer();

		// Configure 3D appearance
		renderer.setItemMargin(0.2);
		plot.getDomainAxis().setCategoryMargin(0.3);
		renderer.setMaximumBarWidth(0.1);
		plot.setBackgroundPaint(new Color(240, 240, 240));
		plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		plot.getRangeAxis().setLowerBound(0);

		// Rotate labels for projected years
		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));

		return chart;
	}

	/**
	 * Calculates projected crime rates using linear regression
	 * 
	 * @param allData    The complete dataset of crime statistics
	 * @param category   The crime category to project
	 * @param startYear  The first year of actual data
	 * @param endYear    The last year of actual data
	 * @param targetYear The year to project rates for
	 * @return The projected crime rate for the target year
	 */
	private double calculateProjectedRate(List<IncidentBasedCrimesModel> allData, String category, int startYear,
			int endYear, int targetYear) {
		// Get all available data points for this category
		List<Double> rates = new ArrayList<>();
		for (int year = startYear; year <= endYear; year++) {
			final int y = year;
			double rate = allData.stream()
					.filter(data -> data.getViolationType().equals(category) && data.getYear() == y)
					.mapToDouble(IncidentBasedCrimesModel::getRatePer100000).findFirst().orElse(0.0);
			rates.add(rate);
		}

		// Simple linear regression for projection
		if (rates.size() < 2) {
			return rates.isEmpty() ? 0 : rates.get(0);
		}

		// Calculate slope (simple linear regression)
		double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;
		int n = rates.size();

		for (int i = 0; i < n; i++) {
			sumX += i;
			sumY += rates.get(i);
			sumXY += i * rates.get(i);
			sumXX += i * i;
		}

		double slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
		double intercept = (sumY - slope * sumX) / n;

		// Project for the target year (years after endYear)
		int yearsAfterEnd = targetYear - endYear;
		return intercept + slope * (n - 1 + yearsAfterEnd);
	}

	/**
	 * Displays an error message in the chart container.
	 * 
	 * @param message The error message to display
	 */
	public void showErrorMessage(String message) {
		chartContainer.removeAll();
		JLabel errorLabel = new JLabel(message, SwingConstants.CENTER);
		errorLabel.setFont(new Font("Arial", Font.BOLD, 16));
		chartContainer.add(errorLabel);
		chartContainer.revalidate();
		chartContainer.repaint();
	}

	/**
	 * Updates the chart display in the container panel.
	 */
	@SuppressWarnings("serial")
	private void updateChartDisplay() {
		chartContainer.removeAll();

		if (currentChart != null) {
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

	/**
	 * Calculates and displays mean values for selected bars
	 * 
	 * @param dataset The dataset containing the chart data
	 */
	private void calculateAndDisplayMean(DefaultCategoryDataset dataset) {
		for (int i = 0; i < meanRadioButtons.length; i++) {
			if (meanRadioButtons[i].isSelected() && i < dataset.getRowCount()) {
				// Calculate mean for this specific bar
				String category = (String) dataset.getRowKey(i);
				double sum = 0;
				int count = 0;

				for (int col = 0; col < dataset.getColumnCount(); col++) {
					Number value = dataset.getValue(category, dataset.getColumnKey(col));
					if (value != null) {
						sum += value.doubleValue();
						count++;
					}
				}

				if (count > 0) {
					double mean = sum / count;
					meanResultLabels[i].setText(String.format("Mean: %.2f", mean));
					meanResultLabels[i].setVisible(true);
				} else {
					meanResultLabels[i].setVisible(false);
				}
			} else {
				meanResultLabels[i].setVisible(false);
			}
		}
	}

	// GETTER METHODS ----------------------------------------------------------

	public ChartPanel getChartPanel() {
		return chartPanel;
	}

	public JPanel getPanel() {
		return incidentBasedCrimePanel;
	}

	public JComboBox<String> getBarComboBox1() {
		return barComboBoxes[0];
	}

	public JComboBox<String> getBarComboBox2() {
		return barComboBoxes[1];
	}

	public JComboBox<String> getBarComboBox3() {
		return barComboBoxes[2];
	}

	public JComboBox<String> getBarComboBox4() {
		return barComboBoxes[3];
	}

	public JComboBox<String> getBarComboBox5() {
		return barComboBoxes[4];
	}

	public String[] getSelectedCategories() {
		return new String[] { (String) barComboBoxes[0].getSelectedItem(), (String) barComboBoxes[1].getSelectedItem(),
				(String) barComboBoxes[2].getSelectedItem(), (String) barComboBoxes[3].getSelectedItem(),
				(String) barComboBoxes[4].getSelectedItem() };
	}

	public JTextField getStartYearTextField() {
		return startYearTextField;
	}

	public JTextField getEndYearTextField() {
		return endYearTextField;
	}

	public JSlider getStartYearSlider() {
		return startYearSlider;
	}

	public JSlider getEndYearSlider() {
		return endYearSlider;
	}

	public int[] getSelectedYears() {
		return new int[] { startYearSlider.getValue(), endYearSlider.getValue() };
	}

	public boolean isFutureTrendsEnabled() {
		return futureTrendsRadioButton.isSelected();
	}

	public int getProjectionYears() throws NumberFormatException {
		if (projectionYearsTextField.getText().equals("Enter years to project (e.g. 3)")) {
			return 0;
		}
		return Integer.parseInt(projectionYearsTextField.getText());
	}

	public boolean isMeanCalculationEnabled() {
		for (JRadioButton rb : meanRadioButtons) {
			if (rb.isSelected()) {
				return true;
			}
		}
		return false;
	}

	public boolean is3DSelected() {
		return threeDRadioButton.isSelected();
	}

	public JButton getApplyButton() {
		return applyButton;
	}

	public JButton getChangeColorsButton() {
		return changeColorsButton;
	}

	public CircleButton getHelpButton() {
		return helpButton;
	}

	public JButton getExportPngButton() {
		return exportPngButton;
	}
}