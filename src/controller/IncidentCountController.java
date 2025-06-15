package controller;

import java.awt.event.*;
import java.util.Comparator;
import model.Institution;
import view.IncidentCountPanel;

public class IncidentCountController implements ActionListener {

//	FIELDS --------------------------------------------------------------------------------------------------------
	IncidentCountFileReader fileReader = new IncidentCountFileReader();
	IncidentCountDataController dataController = new IncidentCountDataController();
	IncidentCountPanel incidentCountPanel;

//	CONSTRUCTOR ---------------------------------------------------------------------------------------------------
	public IncidentCountController(IncidentCountPanel incidentCountPanel) {

		this.incidentCountPanel = incidentCountPanel;
		incidentCountPanel.setDataController(dataController);
		incidentCountPanel.getSortComboBox().addActionListener(this);
		incidentCountPanel.getViewComboBox().addActionListener(this);
		incidentCountPanel.getColourButton().addActionListener(this);
		incidentCountPanel.getExportButton().addActionListener(this);
	}

//	GETTERS + SETTERS ---------------------------------------------------------------------------------------------
	public IncidentCountFileReader getFileReader() {
		return fileReader;
	}

	public void setFileReader(IncidentCountFileReader fileReader) {
		this.fileReader = fileReader;
	}

	public IncidentCountDataController getDataController() {
		return dataController;
	}

	public void setDataController(IncidentCountDataController dataController) {
		this.dataController = dataController;
	}

	public IncidentCountPanel getIncidentCountPanel() {
		return incidentCountPanel;
	}

	public void setIncidentCountPanel(IncidentCountPanel incidentCountPanel) {
		this.incidentCountPanel = incidentCountPanel;
	}

//	TO STRING -----------------------------------------------------------------------------------------------------
	@Override
	public String toString() {
		return "MainController [fileReader=" + fileReader + ", dataController=" + dataController
				+ ", incidentCountPanel=" + incidentCountPanel + "]";
	}

//	ACTION LISTENER -----------------------------------------------------------------------------------------------
	@Override
	public void actionPerformed(ActionEvent event) {

//		HANDLE SORTING
		if (event.getSource() == incidentCountPanel.getSortComboBox())
			handleSorting();

//		HANDLE VIEW CHANGE
		if (event.getSource() == incidentCountPanel.getViewComboBox())
			incidentCountPanel.updateChart();

//		COLOUR CHANGE
		if (event.getSource() == incidentCountPanel.getColourButton()) {
		}

//		EXPORT AS PNG
		if (event.getSource() == incidentCountPanel.getExportButton()) {
			incidentCountPanel.saveChart();
		}
	}

//	HANDLE SORTING ------------------------------------------------------------------------------------------------
	public void handleSorting() {
//		Use ActionListener to detect chosen sorting option from IncidentCountPanel’s sortComboBox
		String selected = (String) incidentCountPanel.getSortComboBox().getSelectedItem();

//		Call DataController’s Sort method (with the respective comparator)
		Comparator<Institution> comparator = null;
		switch (selected) {
		case "Institution -- Alphabetical":
			comparator = Comparator.comparing(Institution::getName);
			break;
		case "Number of Incidents -- Ascending":
			comparator = Comparator.comparingInt(Institution::getIncidents);
			break;
		case "Number of Incidents -- Descending":
			comparator = Comparator.comparingInt(Institution::getIncidents).reversed();
			break;
		case "Use of Force Rate -- Ascending":
			comparator = Comparator.comparingDouble(Institution::getForceRate);
			break;
		case "Use of Force Rate -- Descending":
			comparator = Comparator.comparingDouble(Institution::getForceRate).reversed();
			break;
		}
		dataController.sort(comparator);

//		Call IncidentCountPanel’s Update Chart method
		incidentCountPanel.updateChart();
	}
}