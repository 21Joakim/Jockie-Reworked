package com.jockie.bot.APIs.weather;

public class WeatherLocation {
	
	private String location_name;
	
	public String getLocationName() {
		return this.location_name;
	}
	
	private String location_code;
	
	public String getLocationCode() {
		return this.location_code;
	}
	
	private String measurement_system;
	
	public String getMeasurementSystem() {
		return this.measurement_system;
	}
	
	public WeatherLocation(String location_name, String location_code, String measurement_system) {
		this.location_name = location_name;
		this.location_code = location_code;
		this.measurement_system = measurement_system;
	}
}