package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;

import model.Location;
import view.ChangeColorFrame;
import view.LocationIncidentFrame;
import view.SelectInformationFrame;

//Angel Zhan
// This class creates the action listener for each button on the Location Incident Frame 
public class LocationIncidentController {
	private JButton changeColorButton = LocationIncidentFrame.getChangeColorButton();
	@SuppressWarnings("unused")
	private JLabel higlightLocationLabel = LocationIncidentFrame.getHiglightLocationLabel();
	@SuppressWarnings("rawtypes")
	private JComboBox highlightLocationCombobox = LocationIncidentFrame.getHighlightLocationCombobox();
	private JButton selectInformationButton = LocationIncidentFrame.getSelectInformationButton();
	@SuppressWarnings("unused")
	private JFreeChart chart = LocationIncidentFrame.getPieChart();
	@SuppressWarnings("unused")
	private Location[] dataArray = LocationIncidentFrame.getDataArray();
	private PiePlot plot = LocationIncidentFrame.getPlot();

	public LocationIncidentController() {

/////////////// change color action listener 
		changeColorButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// opens change color frame 
				new ChangeColorFrame(LocationIncidentFrame.getPieChart(), LocationIncidentFrame.getDataArray());

			}

		});

/////////////// select information button action listener 	
		selectInformationButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				// opens seclect information frame 
				new SelectInformationFrame(LocationIncidentFrame.getPieChart(), LocationIncidentFrame.getDataArray());
				
			}
			
		});
		
/////////////// location combox box action listener 
		highlightLocationCombobox.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        int selectedIndex = highlightLocationCombobox.getSelectedIndex();

		        // Prevent out-of-bounds if nothing selected
		        if (selectedIndex < 0) return;

		        // Use updated currentData instead of dataArray
		        ArrayList<Location> workingData = LocationIncidentFrame.getCurrentData();

		        if (workingData == null || workingData.isEmpty()) return;

		        // Remove all explosion first
		        for (int i = 0; i < workingData.size(); i++) {
		            plot.setExplodePercent(workingData.get(i).getLocation(), 0);
		        }

		        // Skip "No Location Selected" at index 0
		        if (selectedIndex > 0 && selectedIndex <= workingData.size()) {
		            plot.setExplodePercent(workingData.get(selectedIndex - 1).getLocation(), 0.5);
		        }
		    }
		});


	}
}