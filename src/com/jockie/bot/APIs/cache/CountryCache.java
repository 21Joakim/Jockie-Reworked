package com.jockie.bot.APIs.cache;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.jockie.bot.APIs.APIHelper;
import com.jockie.bot.APIs.country.APICountry;
import com.jockie.bot.APIs.country.Country;
import com.jockie.bot.main.JockieBot;

public class CountryCache implements DataCache {
	
	private static final String BASE = "restcountries.eu/rest/v2/";
	
	private LocalDate expires;
	
	private final File FILE = new File(JockieBot.FILE_STORAGE_PATH + getAPIName() + "_" + getSubName() + ".cache");
	
	private ArrayList<Country> countries;
	
	public ArrayList<Country> getCountries() {
		return this.countries;
	}
	
	public CountryCache() {
		if(FILE.exists()) {
			JSONObject country_cache = DataCache.readData(this);
			this.expires = LocalDate.parse(country_cache.getString("expires"));
			this.countries = APICountry.fromJson(new JSONTokener(country_cache.getJSONArray("data").toString()));
		}else this.updateData();
	}
	
	public String getAPIName() {
		return "country";
	}
	
	public String getSubName() {
		return "all";
	}
	
	public File getFile() {
		return this.FILE;
	}
	
	public LocalDate getExpires() {
		return this.expires;
	}
	
	public JSONTokener getData() {
		return new JSONTokener(DataCache.readData(this).get("data").toString());
	}
	
	public void updateData() {
		URIBuilder ub = new URIBuilder();
		ub.setScheme("https");
		ub.setHost(BASE);
		ub.setPath("all/");
		
		JSONTokener tokener = APIHelper.getJSON(ub.toString());
		
		if(tokener != null) {
			JSONArray array = new JSONArray(tokener);
			
			this.countries = APICountry.fromJson(new JSONTokener(array.toString()));
			
			this.expires = LocalDate.now().plusDays(7);
			
			DataCache.writeData(this, new JSONTokener(array.toString()));
		}
	}
}