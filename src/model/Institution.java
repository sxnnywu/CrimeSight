package model;

public class Institution {
	
//	FIELDS --------------------------------------------------------------------------------------------------------
	private String name;
	private int incidents;
	private double forceRate;
	
//	CONSTRUCTOR ---------------------------------------------------------------------------------------------------
	public Institution(String name, int incidents, double forceRate) {
		super();
		this.name = name;
		setIncidents(incidents);
		this.forceRate = forceRate;
	}

//	GETTERS + SETTERS ---------------------------------------------------------------------------------------------
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIncidents() {
		return incidents;
	}
	public void setIncidents(int incidents) {
		if(incidents >= 0)
			this.incidents = incidents;
		else {
			System.out.println("Cannot have negative value. Incidents field set to default 0.");
			this.incidents = 0;
		}
	}
	public double getForceRate() {
		return forceRate;
	}
	public void setForceRate(double forceRate) {
		this.forceRate = forceRate;
	}

//	TO STRING -----------------------------------------------------------------------------------------------------
	@Override
	public String toString() {
		return "Institution [name=" + name + ", incidents=" + incidents + ", forceRate=" + forceRate + "]";
	}
}