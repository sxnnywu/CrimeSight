package controller;

import java.io.BufferedReader;
import java.io.IOException;

import model.Institution;

public class IncidentCountFileReader {
	
//	CONSTRUCTOR 
	public IncidentCountFileReader() {
		
		IncidentCountDataController.getDataList().clear();

        try (BufferedReader reader = new BufferedReader(new java.io.FileReader("data/Institution Incident Summary.csv"))) {
            String line;
            int lineCount = 0;

//			Read the file line by line
            while ((line = reader.readLine()) != null) {
                lineCount++;

//				Skip header row
                if (lineCount == 1) continue;

//           	Split the line by commas, make an array of data fields within current line (each column) 
                String[] columns = line.split(",");
//        		Verify there are 7 columns, otherwise skip this row
                if (columns.length < 7) continue;
//            	Extract the 3 required data fields from columns
                try {
                    String name = columns[2].trim(); // Column 3 (name)
                    int incidents = Integer.parseInt(columns[3].trim()); // Column 4 (# of incidents) 
                    double forceRate = Double.parseDouble(columns[6].trim()); // Column 7 (use of force rate)
//                 	Construct current Institution object
                    Institution institution = new Institution(name, incidents, forceRate);

//                 	Add current institution to dataList
                    IncidentCountDataController.getDataList().add(institution);
                    
//          	If numeric conversion fails, skip this row
                } catch (NumberFormatException e) {
                    continue;
                }
            }
            // If no data was added, print error message
            if (IncidentCountDataController.getDataList().isEmpty()) {
                System.out.println("No data available to display");
            }
//   	If the file could not be read, print error message
        } catch (IOException e) {
        	System.out.println("Error reading the data file.");
        }
    }
}