package controller;

import java.util.*;
import java.util.stream.Collectors;

import org.jfree.data.category.DefaultCategoryDataset;

import model.Institution;

public class IncidentCountDataController {
	
//	FIELDS --------------------------------------------------------------------------------------------------------
	private static ArrayList<Institution> dataList = new ArrayList<Institution>();
	private DefaultCategoryDataset incidentDataset = new DefaultCategoryDataset();
    private DefaultCategoryDataset forceRateDataset = new DefaultCategoryDataset();
    
// 	CONSTRUCTOR ---------------------------------------------------------------------------------------------------
	public IncidentCountDataController() {
		super();
		
//		Set up datasets
		setUpIncidentDataset();
		setUpForceRateDataset();
	}

//	GETTERS + SETTERS ---------------------------------------------------------------------------------------------
	public static ArrayList<Institution> getDataList() {
		return dataList;
	}
	public static void setDataList(ArrayList<Institution> dataList) {
		IncidentCountDataController.dataList = dataList;
	}
	public DefaultCategoryDataset getIncidentDataset() {
		return incidentDataset;
	}
	public void setIncidentDataset(DefaultCategoryDataset incidentDataset) {
		this.incidentDataset = incidentDataset;
	}
	public DefaultCategoryDataset getForceRateDataset() {
		return forceRateDataset;
	}
	public void setForceRateDataset(DefaultCategoryDataset forceRateDataset) {
		this.forceRateDataset = forceRateDataset;
	}

//	TO STRING -----------------------------------------------------------------------------------------------------
	@Override
	public String toString() {
		return "DataController [getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}

//	SET UP INCIDENT DATA SET --------------------------------------------------------------------------------------
	public void setUpIncidentDataset() {	
		for(Institution current : dataList) {
			incidentDataset.addValue(current.getIncidents(), "Number of Incidents", current.getName());
		}
	}
	
//	SET UP FORCE RATE DATA SET ------------------------------------------------------------------------------------
	public void setUpForceRateDataset() {
		for(Institution current : dataList) {
			forceRateDataset.addValue(current.getForceRate(), "Use of Force Rate", current.getName());
		}
	}
	
// 	SORT ----------------------------------------------------------------------------------------------------------
	public void sort(Comparator<Institution> comparator) {
		
//		Sort data list
        Collections.sort(dataList, comparator);
        
//     	Clear existing datasets
        incidentDataset.clear();
        forceRateDataset.clear();
        
//    	Rebuild the datasets
        setUpIncidentDataset();
        setUpForceRateDataset();
	}

//	SEARCH --------------------------------------------------------------------------------------------------------
	public ArrayList<String> search(String input) {
		
//     	Trim input + set to lower case
		String processedInput = input.trim().toLowerCase();
		
//		Initialize an empty String array list
        ArrayList<String> matchList = new ArrayList<>();

//     	Create a stream from data list, filter the stream to include only institutions whose names contain the input
//     	Collect the results institutions’ names into matchList
        matchList = dataList.stream()
            .filter(i -> i.getName().toLowerCase().contains(processedInput))
            .map(Institution::getName)
            .collect(Collectors.toCollection(ArrayList::new));
        
//     	If the size of matchList > 5, only take the first 5 elements
        if (matchList.size() > 5) 
            matchList = new ArrayList<>(matchList.subList(0, 5));
//     	If the match list is empty
        if (matchList.isEmpty()) 
            matchList.add("No results found.");
        return matchList;
    }
	
//	BUILD DATASETS ------------------------------------------------------------------------------------------------
    public void buildDatasets() {
    	
//    	Clear both datasets
        incidentDataset.clear();
        forceRateDataset.clear();

//     	Loop through datasets
        for (Institution i : dataList) {
        	
//        	Fields
            String name = i.getName();
            int incidents = i.getIncidents();
            double forceRate = i.getForceRate();

//         	Add new objects to datasets
            incidentDataset.addValue(incidents, "Number of Incidents", name);
            forceRateDataset.addValue(forceRate, "Use of Force Rate", name);
        }
    }	
}