package model;

/**
 * The IncidentBasedCrimesModel class represents a single record of crime
 * statistics, containing geographic information, violation type, year, and
 * crime rate data. This model enforces data validation rules through its setter
 * methods.
 */
public class IncidentBasedCrimesModel {

	// FIELDS
	// --------------------------------------------------------------------------------------------------------
	private String geography; // Geographic location of the crime data (e.g., city, province)
	private String violationType; // Type of criminal violation/offense
	private int year; // Year when the crime data was recorded
	private double ratePer100000; // Crime rate per 100,000 population

	// CONSTRUCTOR
	// ---------------------------------------------------------------------------------------------------
	/**
	 * Constructs a new IncidentBasedCrimesModel with validated data.
	 * 
	 * @param geography     The geographic location (will be trimmed and validated)
	 * @param violationType The type of violation (will be trimmed and validated)
	 * @param year          The recorded year (must be between 2000-2030)
	 * @param ratePer100000 The crime rate (must be non-negative)
	 */
	public IncidentBasedCrimesModel(String geography, String violationType, int year, double ratePer100000) {
		// Use setters to ensure validation
		setGeography(geography);
		setViolationType(violationType);
		setYear(year);
		setRatePer100000(ratePer100000);
	}

	// GETTERS + SETTERS
	// ---------------------------------------------------------------------------------------------
	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		if (geography == null || geography.trim().isEmpty()) {
			System.out.println("Geography cannot be null or empty. Set to 'Unknown'.");
			this.geography = "Unknown";
		} else {
			this.geography = geography.trim();
		}
	}

	public String getViolationType() {
		return violationType;
	}

	public void setViolationType(String violationType) {
		if (violationType == null || violationType.trim().isEmpty()) {
			System.out.println("Violation type cannot be null or empty. Set to 'Unspecified'.");
			this.violationType = "Unspecified";
		} else {
			this.violationType = violationType.trim();
		}
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		if (year >= 2000 && year <= 2030) { // Adjust year range as needed
			this.year = year;
		} else {
			System.out.println("Invalid year " + year + ". Year must be between 2000-2030. Set to 2000.");
			this.year = 2000;
		}
	}

	public double getRatePer100000() {
		return ratePer100000;
	}

	public void setRatePer100000(double ratePer100000) {
		if (ratePer100000 >= 0) {
			this.ratePer100000 = ratePer100000;
		} else {
			System.out.println("Rate cannot be negative. Set to 0.");
			this.ratePer100000 = 0;
		}
	}

	// TO STRING
	// -----------------------------------------------------------------------------------------------------
	/**
	 * Returns a detailed string representation of the crime record.
	 * 
	 * @return Formatted string with all fields
	 */
	@Override
	public String toString() {
		return String.format("IncidentCrime [geography=%s, violationType=%s, year=%d, ratePer100000=%.2f]", geography,
				violationType, year, ratePer100000);
	}

	/**
	 * Returns a more user-friendly display version of the crime record.
	 * 
	 * @return Simplified formatted string for display purposes
	 */
	public String toDisplayString() {
		return String.format("%s (%d): %s - %.2f per 100,000", geography, year, violationType, ratePer100000);
	}
}