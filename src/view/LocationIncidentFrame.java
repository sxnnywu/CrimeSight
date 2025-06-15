package view;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import controller.LocationFileReader;
import controller.LocationIncidentController;
import model.Location;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;



// Angel Zhan
// This class creates the Pie chart and filter options for the user 
public class LocationIncidentFrame {

	private static JPanel locationIncidentPanel;

	private static JPanel filterPanel;

/////////// original data, unchanged 
	private static Location[] dataArray = new Location[19]; 

////////// changed data 
	private static ArrayList<Location> currentData = new ArrayList<Location>();
	private static ArrayList<String> currentLocations = new ArrayList<String>();
	private static ArrayList<Integer> currentNumbers = new ArrayList<Integer>();
	
	
	
	private static PieDataset locationDataset;
	private static JFreeChart pieChart;
	private static PiePlot plot;
	
	// filter components 
	private static JButton changeColorButton;
	private static JLabel higlightLocationLabel;
	@SuppressWarnings("rawtypes")
	private static JComboBox highlightLocationCombobox;
	private static JButton selectInformationButton;
	private static ChartPanel pieChartPanel;
	
	private static Color navyBlue = Color.decode("#1C3F60");
    private Color pink = Color.decode("#f8d0c7");

	private static LocationFileReader fileReader;
	private static LocationIncidentController controller;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LocationIncidentFrame() {

		// read the files
		try {
			fileReader = new LocationFileReader();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
		
		
		// create separate arrays for the location names and data
		createArrays();
		
		
		
		currentData =  new ArrayList<Location> (Arrays.asList(dataArray));
		
		
		// set up charts
		locationDataset = createDataset(); // create dataset
		// create chart
		pieChart = ChartFactory.createPieChart("Location Incident Summary (2023)", locationDataset, true, true, false);
		//get plot from chart
		plot = (PiePlot) pieChart.getPlot();
		// create chart Panel
		pieChartPanel = new ChartPanel(pieChart);
		
		
		
		filterPanel = new JPanel();
		
		//create components 
		locationIncidentPanel = new JPanel();
		
		filterPanel.setLayout(null);
		
		changeColorButton = new JButton("Change Colours");
		higlightLocationLabel = new JLabel("Highlight Location");
		highlightLocationCombobox = new JComboBox (currentLocations.toArray());
		selectInformationButton = new JButton("Select Information");
		highlightLocationCombobox.addItem("No Location Selected"); // add an option for no location to be highlighted
		highlightLocationCombobox.setSelectedIndex(19);
		
		
		
		filterPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Filters"));
		// add all components to the frame
		filterPanel.add(changeColorButton);
		filterPanel.add(higlightLocationLabel);
		filterPanel.add(highlightLocationCombobox);
		filterPanel.add(selectInformationButton);
		filterPanel.add(selectInformationButton);

		
		changeColorButton.setFont(new Font("Calibri", Font.PLAIN, 16));
		higlightLocationLabel.setFont(new Font("Calibri", Font.PLAIN, 16));
		selectInformationButton.setFont(new Font("Calibri", Font.PLAIN, 16));
		
		
		// style the chart
		pieChart.getLegend().setItemFont(new Font("Calibri", Font.PLAIN, 14));
		pieChart.getTitle().setFont(new Font("Cooper Black", Font.PLAIN, 17));
		
		
		locationIncidentPanel.setBackground(pink);
		pieChartPanel.setBackground(Color.white);
		pieChartPanel.setForeground(Color.white);
		filterPanel.setBackground(Color.white);
		changeColorButton.setBackground(Color.white);
		selectInformationButton.setBackground(Color.white);
		highlightLocationCombobox.setBackground(Color.white);
		changeColorButton.setBackground(getNavyBlue());
		changeColorButton.setForeground(Color.WHITE);
		higlightLocationLabel.setBackground(getNavyBlue());
		higlightLocationLabel.setForeground(Color.WHITE);
		selectInformationButton.setBackground(getNavyBlue());
		selectInformationButton.setForeground(Color.WHITE);
		
		
		

		changeColorButton.setBounds(100,120,180,40);
		higlightLocationLabel.setBounds(100,220,180,40);
		highlightLocationCombobox.setBounds(100,270,200,40);
		selectInformationButton.setBounds(100,400,180,40);
		pieChartPanel.setBounds(20, 20, 800, 600);
		filterPanel.setBounds(850, 20, 400, 600);
		
		locationIncidentPanel.setLayout(null);
		locationIncidentPanel.add(pieChartPanel);
		locationIncidentPanel.add(filterPanel);
		
		controller = new LocationIncidentController();
		

	}

	// creates the initial pie chart without any modifications
	public static PieDataset createDataset() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		for (int i = 0; i < currentData.size(); i++) {
			dataset.setValue(currentData.get(i).getLocation(), currentData.get(i).getTimes());
		}
		return dataset;

	}
	
	public static void createArrays() { // creates an array just for the locations and the data values (for the combo box)
		for (int i =0; i < dataArray.length;i++) {
			
			currentLocations.add(dataArray[i].getLocation());	
			currentNumbers.add(dataArray[i].getTimes());
			
		}
		
	}
	
	
	


	// setters and getters
	public static JPanel getLocationIncidentPanel() {
		return locationIncidentPanel;
	}

	@SuppressWarnings("static-access")
	public void setLocationIncidentPanel(JPanel locationIncidentPanel) {
		this.locationIncidentPanel = locationIncidentPanel;
	}

	public static JPanel getFilterPanel() {
		return filterPanel;
	}

	public static void setFilterPanel(JPanel filterPanel) {
		LocationIncidentFrame.filterPanel = filterPanel;
	}

	public static LocationFileReader getFileReader() {
		return fileReader;
	}

	public static void setFileReader(LocationFileReader fileReader) {
		LocationIncidentFrame.fileReader = fileReader;
	}

	public static Location[] getDataArray() {
		return dataArray;
	}

	public static void setDataArray(Location[] dataArray) {
		LocationIncidentFrame.dataArray = dataArray;
	}




	public static PieDataset getLocationDataset() {
		return locationDataset;
	}

	public static void setLocationDataset(PieDataset locationDataset) {
		LocationIncidentFrame.locationDataset = locationDataset;
	}

	public static JFreeChart getPieChart() {
		return pieChart;
	}

	public static void setPieChart(JFreeChart pieChart) {
		LocationIncidentFrame.pieChart = pieChart;
	}

	public static JButton getChangeColorButton() {
		return changeColorButton;
	}

	@SuppressWarnings("static-access")
	public void setChangeColorButton(JButton changeColorButton) {
		this.changeColorButton = changeColorButton;
	}

	public static JLabel getHiglightLocationLabel() {
		return higlightLocationLabel;
	}

	@SuppressWarnings("static-access")
	public void setHiglightLocationLabel(JLabel higlightLocationLabel) {
		this.higlightLocationLabel = higlightLocationLabel;
	}

	@SuppressWarnings("rawtypes")
	public static JComboBox getHighlightLocationCombobox() {
		return highlightLocationCombobox;
	}

	@SuppressWarnings("rawtypes")
	public static void setHighlightLocationCombobox(JComboBox highlightLocationCombobox) {
	LocationIncidentFrame.highlightLocationCombobox = highlightLocationCombobox;
	}

	public static JButton getSelectInformationButton() {
		return selectInformationButton;
	}

	@SuppressWarnings("static-access")
	public void setSelectInformationButton(JButton selectInformationButton) {
		this.selectInformationButton = selectInformationButton;
	}



	public static PiePlot getPlot() {
		return plot;
	}

	public static void setPlot(Plot plot) {
		LocationIncidentFrame.plot = (PiePlot) plot;
	}

	public static LocationIncidentController getController() {
		return controller;
	}

	public static void setController(LocationIncidentController controller) {
		LocationIncidentFrame.controller = controller;
	}

	public static ChartPanel getPieChartPanel() {
		return pieChartPanel;
	}

	public static void setPieChartPanel(ChartPanel pieChartPanel) {
		LocationIncidentFrame.pieChartPanel = pieChartPanel;
	}

	public static void setPlot(PiePlot plot) {
		LocationIncidentFrame.plot = plot;
	}

	public static ArrayList<Location> getCurrentData() {
		return currentData;
	}

	public static void setCurrentData(ArrayList<Location> currentData) {
		LocationIncidentFrame.currentData = currentData;
	}

	public static ArrayList<String> getCurrentLocations() {
		return currentLocations;
	}

	public static void setCurrentLocations(ArrayList<String> currentLocations) {
		LocationIncidentFrame.currentLocations = currentLocations;
	}

	public static ArrayList<Integer> getCurrentNumbers() {
		return currentNumbers;
	}

	public static void setCurrentNumbers(ArrayList<Integer> currentNumbers) {
		LocationIncidentFrame.currentNumbers = currentNumbers;
	}

	public static Color getNavyBlue() {
		return navyBlue;
	}

	@SuppressWarnings("static-access")
	public void setNavyBlue(Color navyBlue) {
		this.navyBlue = navyBlue;
	}


}