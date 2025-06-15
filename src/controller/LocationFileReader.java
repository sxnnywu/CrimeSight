package controller;

import java.io.BufferedReader;

// Angel Zhan 
// Reads the Location Incident Summary file and converts it to an array 
import java.io.FileNotFoundException;
import java.io.IOException;

import model.Location;
import view.LocationIncidentFrame;

public class LocationFileReader {
	private Location[] dataArray = LocationIncidentFrame.getDataArray();

	@SuppressWarnings("resource")
	public LocationFileReader() throws IOException {

		try {

			// buffer reader to read the file
			BufferedReader br = new BufferedReader(new java.io.FileReader("data/Location Incident Summary.csv"));
			String line; // stores the line read
			int lineNumber = 0; // keeps track of the line number currently on 
			while ((line = br.readLine()) != null) { // check if the current line if null 
				lineNumber++; // increase the line number
				if (lineNumber == 1) // skip the first line 
					continue;

				String[] values = line.split(","); // keeps track of the data, splits it by comma

				
				// adds it to the data array 
				String location = values[2].trim();
				int times = Integer.parseInt(values[3].trim());

				dataArray[lineNumber - 2] = new Location(location, times);
				
				
				// change the final location as "other" since it is null on the data sheet
				if (lineNumber == 20) {
					dataArray[lineNumber - 2] = new Location("Other", times);
				}

			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
//		printArray();
		
	}

//	public void printArray() {
//		System.out.println(dataArray.length);
//		for (int i = 0; i < dataArray.length; i++) {
//			System.out.println(dataArray[i].toString());
//		}
//
//	}

}