package model;

import java.awt.Color;

public class Location {
	
	private String location;
	private int times;
	private Color color;
	public Location(String location, int times) {
		super();
		this.location = location;
		this.times = times;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	@Override
	public String toString() {
		return "Location [location=" + location + ", times=" + times + ", color=" + color + "]";
	}
	
	

}