package com.jockie.bot.APIs.cache;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.jockie.bot.APIs.APIHelper;
import com.jockie.bot.APIs.intel.APIIntel;
import com.jockie.bot.APIs.intel.Processor;
import com.jockie.bot.main.JockieBot;

public class ProcessorCache implements DataCache {
	
	private LocalDate expires;
	
	private final File FILE = new File(JockieBot.FILE_STORAGE_PATH + getAPIName() + "_" + getSubName() + ".cache");
	
	private ArrayList<Processor> processors = new ArrayList<Processor>();
	
	public ProcessorCache() {
		if(FILE.exists()) {
			JSONObject intel_cache = DataCache.readData(this);
			this.expires = LocalDate.parse(intel_cache.getString("expires"));
			this.processors = APIIntel.getProcessorsFromJSON(new JSONTokener(intel_cache.getJSONObject("data").toString()));
			this.processors.sort(new Comparator<Processor>() {
				public int compare(Processor processor_1, Processor processor_2) {
					return processor_1.getProductName().compareTo(processor_2.getProductName());
				}
			});
		}else this.updateData();
	}
	
	public ArrayList<Processor> getProcessors() {
		return this.processors;
	}
	
	public String getAPIName() {
		return "processor";
	}
	
	public String getSubName() {
		return "product_name";
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
		ub.setHost(APIIntel.BASE);
		ub.setPath(APIIntel.PROCESSOR);
		ub.addParameter("api_key", APIIntel.API_KEY);
		ub.addParameter("$format", "json");
		ub.addParameter("$select", 
			"ProductId,ProductName,"
			+ "ProductFamilyId,ProductSeriesId,"
			+ "MarketSegment,ProcessorBrandId,"
			+ "MaxTDP,CoreCount,BusType,"
			+ "BusBandwidth,BusTypeUnits,"
			+ "Cache,CacheType,"
			+ "ClockSpeed,ClockSpeedMax,"
			+ "ThreadCount");
		
		JSONTokener tokener = APIHelper.getJSON(ub.toString());
		
		if(tokener != null) {
			JSONObject object = new JSONObject(tokener);
			
			JSONArray array = new JSONArray();
			
			object.getJSONArray("d").forEach(o -> {
				JSONObject obj = (JSONObject) o;
				obj.remove("__metadata");
				obj.remove("__deferred");
				array.put(obj);
			});
			
			String data = new JSONObject("{\"d\":" + array.toString() + "}").toString();
			
			this.processors = APIIntel.getProcessorsFromJSON(new JSONTokener(data));
			this.processors.sort(new Comparator<Processor>() {
				public int compare(Processor processor_1, Processor processor_2) {
					return processor_1.getProductName().compareTo(processor_2.getProductName());
				}
			});
			this.expires = LocalDate.now().plusDays(7);
			
			DataCache.writeData(this, new JSONTokener(data));
		}
	}
}