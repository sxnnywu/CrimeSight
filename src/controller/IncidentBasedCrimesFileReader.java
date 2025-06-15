package controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import model.IncidentBasedCrimesModel;

public class IncidentBasedCrimesFileReader {
    
    // Pattern to handle quoted fields containing commas
    private static final Pattern CSV_PATTERN = Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    
    public IncidentBasedCrimesFileReader() {
        List<IncidentBasedCrimesModel> crimeDataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("data/IncidentBasedCrimeStatistics.csv"))) {
            String line;
            int lineCount = 0;
            int[] years = null;

            while ((line = reader.readLine()) != null) {
                lineCount++;
                line = line.trim();

                // Skip empty lines
                if (line.isEmpty()) continue;

                // First non-empty line contains the years (header)
                if (lineCount == 1) {
                    // Use the special pattern to split while respecting quotes
                    String[] headers = CSV_PATTERN.split(line);
                    
                    // Verify we have enough columns
                    if (headers.length < 3) {
                        throw new IOException("Invalid header - expected at least 3 columns");
                    }
                    
                    years = new int[headers.length - 2];
                    for (int i = 0; i < years.length; i++) {
                        try {
                            // Remove quotes if present
                            String yearStr = headers[i + 2].replace("\"", "").trim();
                            years[i] = Integer.parseInt(yearStr);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid year format in header: " + headers[i + 2]);
                            years[i] = 0; // Default value if parsing fails
                        }
                    }
                    continue;
                }

                // Split data lines using our special pattern
                String[] columns = CSV_PATTERN.split(line);
                
                // Verify we have enough columns (geography + violation + years)
                if (columns.length < 3) {
                    System.err.println("Skipping line " + lineCount + " - insufficient columns");
                    continue;
                }

                // Clean and trim all fields
                String geography = columns[0].replace("\"", "").trim();
                String violation = columns[1].replace("\"", "").trim();

                // Process each year's data
                for (int i = 0; i < years.length && (i + 2) < columns.length; i++) {
                    try {
                        int year = years[i];
                        // Clean and parse the rate value
                        String rateStr = columns[i + 2].replace("\"", "").trim();
                        if (rateStr.isEmpty()) {
                            System.err.println("Skipping empty rate at line " + lineCount + ", year " + year);
                            continue;
                        }
                        double rate = Double.parseDouble(rateStr);

                        // Create crime data object
						IncidentBasedCrimesModel crimeData = new IncidentBasedCrimesModel(geography, violation, year,
								rate
                        );

                        crimeDataList.add(crimeData);
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid data at line " + lineCount + 
                                         ", year " + years[i] + ": " + e.getMessage());
                    }
                }
            }

            // Store the loaded data
            IncidentBasedCrimesController.setIncidentDataList(crimeDataList);

            if (crimeDataList.isEmpty()) {
                System.out.println("No incident data was loaded from file");
            }

        } catch (IOException e) {
            System.err.println("Error reading incident crime data file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}