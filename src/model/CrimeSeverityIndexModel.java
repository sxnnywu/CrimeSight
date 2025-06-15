package model;

public class CrimeSeverityIndexModel {
    private String geography;
    private int year;
    private double severityIndex;
    
    public CrimeSeverityIndexModel(String geography, int year, double severityIndex) {
        setGeography(geography);
        setYear(year);
        setSeverityIndex(severityIndex);
    }

    public String getGeography() {
        return geography;
    }

    public void setGeography(String geography) {
        if (geography == null || geography.trim().isEmpty()) {
            throw new IllegalArgumentException("Geography cannot be null or empty");
        }
        this.geography = geography.trim();
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        if (year < 2000 || year > 2020) {
            throw new IllegalArgumentException("Invalid Year: " + year + ". Year must be between 2000-2020");
        }
        this.year = year;
    }

    public double getSeverityIndex() {
        return severityIndex;
    }

    public void setSeverityIndex(double severityIndex) {
        if (severityIndex < 0) {
            throw new IllegalArgumentException("Invalid Severity Index: " + severityIndex + ". Must be a positive number");
        }
        this.severityIndex = severityIndex;
    }

    @Override
    public String toString() {
        return String.format("%s (%d): %.2f", geography, year, severityIndex);
    }
}