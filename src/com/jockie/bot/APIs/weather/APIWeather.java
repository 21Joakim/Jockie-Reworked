package com.jockie.bot.APIs.weather;

import java.util.ArrayList;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jockie.bot.APIs.APIHelper;

public class APIWeather {
	
	private static final String BASE = "wxdata.weather.com/wxdata/";
	private static final String SEARCH = "search/search";
	private static final String WEATHER = "weather/local/";
	
	public static ArrayList<WeatherLocation> getLocationsByName(String name, String measurement_system) {
		ArrayList<WeatherLocation> locations = new ArrayList<WeatherLocation>();
		
		URIBuilder ub = new URIBuilder();
		ub.setScheme("https");
		ub.setHost(BASE);
		ub.setPath(SEARCH);
		ub.addParameter("where", name);
		
		JSONObject json_object = APIHelper.getJSONFromXML(ub.toString());
		if(!json_object.has("error")) {
			if(json_object.has("search")) {
				if(json_object.getJSONObject("search").has("loc")) {
					String locations_str = json_object.getJSONObject("search").get("loc").toString();
					if(locations_str.charAt(0) != '[' && locations_str.charAt(locations_str.length() - 1) != ']') {
						locations_str = "[" + locations_str + "]";
					}
						
					JSONArray json_array = new JSONArray(locations_str);
					
					json_array.forEach(o -> {
						JSONObject json_location = (JSONObject) o;
						
						WeatherLocation location = new WeatherLocation(json_location.getString("content"), json_location.getString("id"), measurement_system);
						
						locations.add(location);
					});
				}
			}
		}
		
		return locations;
	}
	
	public static Weather getWeatherFromLocation(String location_code, String measurement_system) {
		Weather weather_obj = null;
		
		URIBuilder ub = new URIBuilder();
		ub.setScheme("https");
		ub.setHost(BASE);
		ub.setPath(WEATHER + location_code);
		ub.addParameter("cc", "*");
		ub.addParameter("unit", (measurement_system.equals("METRIC")) ? "m" : (measurement_system.equals("IMPERIAL")) ? "i" : null);
		
		JSONObject json_object = APIHelper.getJSONFromXML(ub.toString());
		if(!json_object.has("error")) {
			if(json_object.has("weather")) {
				JSONObject object_head = json_object.getJSONObject("weather").getJSONObject("head");
				JSONObject object_loc = json_object.getJSONObject("weather").getJSONObject("loc");
				
				weather_obj = new Weather(object_loc.get("dnam").toString(), location_code, measurement_system);
				
				weather_obj.setTempUnit(object_head.get("ut").toString());
				weather_obj.setDistanceUnit(object_head.get("ud").toString());
				weather_obj.setSpeedUnit(object_head.get("us").toString());
				weather_obj.setPressureUnit(object_head.get("up").toString());
				
				weather_obj.setLat(object_loc.get("lat").toString());
				weather_obj.setLng(object_loc.get("lon").toString());
				weather_obj.setSunrise(object_loc.get("sunr").toString());
				weather_obj.setSunset(object_loc.get("suns").toString());
				
				if(!object_loc.get("zone").toString().contains("-")) {
					weather_obj.setTimeZone("GMT+" + object_loc.get("zone").toString());
				}else{
					weather_obj.setTimeZone("GMT" + object_loc.get("zone").toString());
				}
				
				if(json_object.getJSONObject("weather").has("cc")) {
					JSONObject object_content = json_object.getJSONObject("weather").getJSONObject("cc");
					
					weather_obj.setHasContent(true);
					
					weather_obj.setLastUpdated(object_content.get("lsup").toString());
					weather_obj.setTemperature(object_content.get("tmp").toString());
					weather_obj.setFeelsLike(object_content.get("flik").toString());
					weather_obj.setDescription(object_content.get("t").toString());
					weather_obj.setIcon(object_content.get("icon").toString());
					
					weather_obj.setPressure(object_content.getJSONObject("bar").get("r").toString());
					weather_obj.setWindSpeed(object_content.getJSONObject("wind").get("s").toString());
					weather_obj.setWindDirection(object_content.getJSONObject("wind").get("d").toString());
					weather_obj.setWindDirectionText(object_content.getJSONObject("wind").get("t").toString());
					
					weather_obj.setHumidity(object_content.get("hmid").toString());
					weather_obj.setVisibility(object_content.get("vis").toString());
					
					weather_obj.setUvIndex(object_content.getJSONObject("uv").get("i").toString());
					weather_obj.setUvText(object_content.getJSONObject("uv").get("t").toString());
				}else{
					weather_obj.setHasContent(false);
				}
			}
		}
		
		return weather_obj;
	}
}