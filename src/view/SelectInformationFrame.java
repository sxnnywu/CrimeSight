package view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JComboBox;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import model.Location;

// Angel Zhan 

// This class creates a new Jframe to allow the user to select information they want shown on the Pie chart in ocationIncidentFrame
public class SelectInformationFrame {

	private static JFrame selectInformatoinFrame;

	@SuppressWarnings("unused")
	private static JFreeChart chart;

	@SuppressWarnings("unused")
	private final static int SPACING = 40; // the spacing in between each JLabel

	private static Border spacing = BorderFactory.createLineBorder(Color.WHITE, 10);
	private static Location[] dataArray;

	private static JRadioButton[] radioButtons;

	private static JPanel namePanel;

	private static JScrollPane scrollPane;

	private static ArrayList<Location> newData;

	private static PieDataset newDataSet;

	private static boolean[] selectedIndex;

	private static JButton selectAllButton;
	private static JButton updateInformation;

	private static PiePlot plot = LocationIncidentFrame.getPlot();

	@SuppressWarnings("rawtypes")
	private static JComboBox comboBox = LocationIncidentFrame.getHighlightLocationCombobox();

	private static ArrayList<String> newLocations;

	@SuppressWarnings("static-access")
	public SelectInformationFrame(JFreeChart chart, Location[] dataArray) {

		this.chart = chart;
		this.dataArray = dataArray;
		selectInformatoinFrame = new JFrame("Select Informatoin");
		selectInformatoinFrame.setSize(600, 700);

		selectInformatoinFrame.setVisible(true);
		selectInformatoinFrame.setLayout(null);
		selectInformatoinFrame.getContentPane().setBackground(Color.WHITE);

		selectedIndex = new boolean[dataArray.length];

		newData = new ArrayList<Location>();
		newLocations = new ArrayList<String>();

		createPanels();

	}

	private void createPanels() {

		selectAllButton = new JButton("Select All");
		updateInformation = new JButton("Update Infromation");
		namePanel = new JPanel();

		scrollPane = new JScrollPane(namePanel);

		namePanel.setBackground(Color.WHITE);

		createRadioButtons(); // creates all the radio buttons for user

		namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
		// add the panels to the JFrame
		scrollPane.setBounds(120, 50, 400, 500);
		scrollPane.setBorder(spacing);
		scrollPane.setBackground(Color.WHITE);
		selectAllButton.setBounds(240, 550, 130, 30);
		updateInformation.setBounds(200, 600, 200, 30);
		selectInformatoinFrame.add(selectAllButton);
		selectInformatoinFrame.add(updateInformation);
		selectInformatoinFrame.add(scrollPane);

		selectAllButton.setFont(new Font("Calibri", Font.PLAIN, 14));
		selectAllButton.setBackground(LocationIncidentFrame.getNavyBlue());
		selectAllButton.setForeground(Color.WHITE);

		updateInformation.setFont(new Font("Calibri", Font.PLAIN, 14));
		updateInformation.setBackground(LocationIncidentFrame.getNavyBlue());
		updateInformation.setForeground(Color.WHITE);

		createActionListeners(); // creates action listeners for the radio buttons

	}

	// this method creates action listeners for all components on the JPanel

	public static void createActionListeners() {

		// button to select all or deselect all radio buttons
		selectAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				boolean selected;

				// if the select all button as been clicked, all radiobuttons are selected
				// then the button changes into deselect all

				if (selectAllButton.getText().equals("Select All")) {
					selected = true;
					selectAllButton.setText("Unselect All");
				} else {
					selected = false;
					selectAllButton.setText("Select All");
				}

				for (int i = 0; i < radioButtons.length; i++) {
					radioButtons[i].setSelected(selected);
					selectedIndex[i] = selected;

				}
			}

		});

		// this button updates the chart on the main JFrame and then closes the Select
		// information JFrame

		updateInformation.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				newData.clear(); // Clear old selections
				newLocations.clear();

				// if the data in selected index is true, add it to the new data arraylist and
				// the new locations array list
				for (int i = 0; i < selectedIndex.length; i++) {
					if (selectedIndex[i]) {
						newData.add(dataArray[i]);
						newLocations.add(dataArray[i].getLocation());

					}
				}

				// create the new data set

				newDataSet = createDataset();

				// update the JFree Chart
				JFreeChart updatedChart = ChartFactory.createPieChart("Location Incident Summary (2023)", newDataSet,
						true, true, false);

				// remove all items from the combo box
				comboBox.removeAllItems();
				comboBox.addItem("No Location Selected"); // Add default selection

				// add the new locations to the combo box
				for (String location : newLocations) {
					comboBox.addItem(location);
				}
				comboBox.setSelectedIndex(0); // select the default

				// Add action listener to the changed combo box
				comboBox.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						int selectedComboIndex = comboBox.getSelectedIndex();

						// clear out all exploded values
						for (int i = 0; i < newData.size(); i++) {
							plot.setExplodePercent(newData.get(i).getLocation(), 0);
						}

						// explode value that is seleced
						if (selectedComboIndex > 0 && selectedComboIndex <= newData.size()) {

							plot.setExplodePercent(newData.get(selectedComboIndex - 1).getLocation(), 0.5);
						}
					}
				});

				// update the pie chart
				LocationIncidentFrame.setCurrentData(newData);
				plot = (PiePlot) updatedChart.getPlot();

				LocationIncidentFrame.setPieChart(updatedChart);
				LocationIncidentFrame.setPlot(plot);

				LocationIncidentFrame.getPieChartPanel().setChart(updatedChart);
				LocationIncidentFrame.getPieChartPanel().revalidate();
				LocationIncidentFrame.getPieChartPanel().repaint();

				selectInformatoinFrame.dispose();
			}
		});

	}

	// creates all the radiobuttons
	public static void createRadioButtons() {

		radioButtons = new JRadioButton[dataArray.length]; // initialize the JLabel array

		for (int i = 0; i < dataArray.length; i++) {

			radioButtons[i] = new JRadioButton(dataArray[i].getLocation());

			radioButtons[i].setOpaque(false);
			radioButtons[i].setBorder(spacing);
			namePanel.add(radioButtons[i]);

			radioButtons[i].setFont(new Font("Calibri", Font.PLAIN, 14));

			// add action listener
			radioButtons[i].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					int clickedIndex = Arrays.asList(radioButtons).indexOf(e.getSource());
					if (radioButtons[clickedIndex].isSelected() == false)
						selectedIndex[clickedIndex] = false;
					else
						selectedIndex[clickedIndex] = true;

				}

			});

		}

	}

	public static PieDataset createDataset() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		for (Location location : newData) {
			dataset.setValue(location.getLocation(), location.getTimes());
		}
		return dataset;
	}

}