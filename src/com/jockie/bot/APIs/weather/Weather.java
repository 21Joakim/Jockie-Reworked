package com.jockie.bot.APIs.weather;

import org.apache.commons.lang3.StringUtils;

import net.dv8tion.jda.core.EmbedBuilder;

public class Weather extends WeatherLocation {
	
	public Weather(String location, String code, String measurement_system) {
		super(location, code, measurement_system);
	}
	
	private String tempUnit;
	public String getTempUnit() { return tempUnit; }
	public void setTempUnit(String tempUnit) { this.tempUnit = tempUnit; }
	private String distanceUnit;
	public String getDistanceUnit() { return distanceUnit; }
	public void setDistanceUnit(String distanceUnit) { this.distanceUnit = distanceUnit; }
	private String speedUnit;
	public String getSpeedUnit() { return speedUnit; }
	public void setSpeedUnit(String speedUnit) { this.speedUnit = speedUnit; }
	private String pressureUnit;
	public String getPressureUnit() { return pressureUnit; }
	public void setPressureUnit(String pressureUnit) { this.pressureUnit = pressureUnit; }
	
	private String lat;
	public String getLat() { return lat; }
	public void setLat(String lat) { this.lat = lat; }
	private String lng;
	public String getLng() { return lng; }
	public void setLng(String lng) { this.lng = lng; }
	private String sunrise;
	public String getSunrise() { return sunrise; }
	public void setSunrise(String sunrise) { this.sunrise = sunrise; }
	private String sunset;
	public String getSunset() { return sunset; }
	public void setSunset(String sunset) { this.sunset = sunset; }
	
	private String timeZone;
	public String getTimeZone() { return timeZone; }
	public void setTimeZone(String timeZone) { this.timeZone = timeZone; }
	
	private boolean hasContent;
	public boolean hasContent() { return hasContent; }
	public void setHasContent(boolean hasContent) { this.hasContent = hasContent; }
	
	private String lastUpdated;
	public String getLastUpdated() { return lastUpdated; }
	public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
	
	private String temperature;
	public String getTemperature() { return temperature; }
	public void setTemperature(String temperature) { this.temperature = temperature; }
	
	private String feelsLike;
	public String getFeelsLike() { return feelsLike; }
	public void setFeelsLike(String feelsLike) { this.feelsLike = feelsLike; }
	private String description;
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	
	private String pressure;
	public String getPressure() { return pressure; }
	public void setPressure(String pressure) { this.pressure = pressure; }
	
	private String windSpeed;
	public String getWindSpeed() { return windSpeed; }
	public void setWindSpeed(String speed) { this.windSpeed = speed; }
	private String windDirection;
	public String getWindDirection() { return windDirection; }
	public void setWindDirection(String windDirection) { this.windDirection = windDirection; }
	private String windDirectionText;
	public String getWindDirectionText() { return windDirectionText; }
	public void setWindDirectionText(String windDirectionText) { this.windDirectionText = windDirectionText; }
	
	private String uvIndex;
	public String getUvIndex() { return uvIndex; }
	public void setUvIndex(String uvIndex) { this.uvIndex = uvIndex; }
	private String uvText;
	public String getUvText() { return uvText; }
	public void setUvText(String uvText) { this.uvText = uvText; }
	
	private String humidity;
	public String getHumidity() { return humidity; }
	public void setHumidity(String humidity) { this.humidity = humidity; }
	private String visibility;
	public String getVisibility() { return visibility; }
	public void setVisibility(String visibility) { this.visibility = visibility; }
	
	private String icon;
	public String getIcon() { return icon; }
	public void setIcon(String icon) { this.icon = icon; }
	
	public EmbedBuilder getInformationEmbed() {
		EmbedBuilder embed_builder = new EmbedBuilder();
		String information = "";
		
		information += this.getLocationName() + "\n\n";
		information += "Lat " + this.getLat() + ", Lng " + this.getLng() + "\n";
		information += "Sunrise : " + this.getSunrise() + " (" + this.getTimeZone() + ")\n";
		information += "Sunset : " + this.getSunset() + " (" + this.getTimeZone() + ")\n\n";
		
		if(this.hasContent()) {
			if(!this.getDescription().isEmpty()) {
				information += "Description : " + this.getDescription() + "\n";
			}
			
			information += "Temperature : " + this.getTemperature() + " " + this.getTempUnit() + "\n";
			information += "Feels like : " +  this.getFeelsLike() + " " + this.getTempUnit() + "\n\n";
			
			if(StringUtils.isNumeric(this.getWindSpeed())) {
				information += "Wind : " + this.getWindSpeed() + this.getSpeedUnit() + ", Direction " + this.getWindDirection() + " (" + this.getWindDirectionText() +")\n";
			}else{ 
				information += "Wind : " + this.getWindSpeed() + "\n";
			}
			
			information += "UV Index (0-10) : " + this.getUvIndex() + " (" + this.getUvText() + ")\n";
			
			if(!this.getPressure().isEmpty()) {
				information += "Pressure : " + this.getPressure() + " " + this.getPressureUnit() + "\n";
			}
			
			information += "Humidity : " + this.getHumidity() + "%\n";
			
			if(!this.getVisibility().isEmpty()) {
				information += "Visibility : " + this.getVisibility() + " " + this.getDistanceUnit() + "\n\n";
			}else{
				information += "\n";
			}
			
			information += "Last updated : " + this.getLastUpdated();
		}else{
			information += "No weather information was found";
		}
		
		embed_builder.setDescription(information);
		
		embed_builder.setImage("http://jockie.ddns.net:8080/Weather/" + this.getIcon() + ".png");
		
		return embed_builder;
	}
}