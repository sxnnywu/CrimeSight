package controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import model.CrimeSeverityIndexModel;

public class CrimeSeverityIndexFileReader {
    
    // Pattern to handle quoted fields containing commas
    private static final Pattern CSV_PATTERN = Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    
    public CrimeSeverityIndexFileReader() {
        List<CrimeSeverityIndexModel> crimeDataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("data/1314-009crime_severity_index_csv_2008-12.csv"))) {
            String line;
            int lineCount = 0;
            int[] years = null;

            while ((line = reader.readLine()) != null) {
                lineCount++;
                line = line.trim();

                // Skip empty lines
                if (line.isEmpty()) continue;

                // First non-empty line contains the header (can skip)
                if (lineCount == 1) continue;

                // Second line contains the years (header)
                if (lineCount == 2) {
                    // Use the special pattern to split while respecting quotes
                    String[] headers = CSV_PATTERN.split(line);
                    
                    // Verify we have enough columns (Geography + years)
                    if (headers.length < 2) {
                        throw new IOException("Invalid header - expected at least 2 columns");
                    }
                    
                    // Extract years from headers (columns 1 to 5)
                    years = new int[headers.length - 1];
                    for (int i = 0; i < years.length; i++) {
                        try {
                            // Remove quotes if present
                            String yearStr = headers[i + 1].replace("\"", "").trim();
                            years[i] = Integer.parseInt(yearStr);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid year format in header: " + headers[i + 1]);
                            years[i] = 0; // Default value if parsing fails
                        }
                    }
                    continue;
                }

                // Split data lines using our special pattern
                String[] columns = CSV_PATTERN.split(line);
                
                // Verify we have enough columns (geography + years)
                if (columns.length < 2) {
                    System.err.println("Skipping line " + lineCount + " - insufficient columns");
                    continue;
                }

                // Clean and trim geography field
                String geography = columns[0].replace("\"", "").trim();
                
                // Remove any footnotes in parentheses from geography
                geography = geography.replaceAll("\\(.*\\)", "").trim();

                // Process each year's data
                for (int i = 0; i < years.length && (i + 1) < columns.length; i++) {
                    try {
                        int year = years[i];
                        // Clean and parse the severity index value
                        String severityStr = columns[i + 1].replace("\"", "").trim();
                        if (severityStr.isEmpty()) {
                            System.err.println("Skipping empty severity at line " + lineCount + ", year " + year);
                            continue;
                        }
                        double severityIndex = Double.parseDouble(severityStr);

                        // Create crime data object
                        CrimeSeverityIndexModel crimeData = new CrimeSeverityIndexModel(
                            geography,
                            year,
                            severityIndex
                        );

                        crimeDataList.add(crimeData);
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid data at line " + lineCount + 
                                         ", year " + years[i] + ": " + e.getMessage());
                    }
                }
            }

            // Store the loaded data (you'll need to create a similar controller)
            CrimeSeverityIndexController.setCrimeDataList(crimeDataList);

            if (crimeDataList.isEmpty()) {
                System.out.println("No crime severity data was loaded from file");
            }

        } catch (IOException e) {
            System.err.println("Error reading crime severity index file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}