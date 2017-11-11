package com.jockie.bot.APIs.intel;

import java.util.ArrayList;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.jockie.bot.Storage;
import com.jockie.bot.APIs.APIHelper;
import com.jockie.bot.safe.Safe;

public class APIIntel {
	
	public static final String API_KEY = Safe.API_INTEL;
	
	public static final String BASE = "odata.intel.com/API/v1_0/Products/";
	public static final String PROCESSOR = "Processors()";
	
	public static ArrayList<IntelProduct> getIntelProductsFromJSON(JSONTokener tokener) {
		ArrayList<IntelProduct> products = new ArrayList<IntelProduct>();
		
		JSONObject initialData = new JSONObject(tokener);
		
		if(initialData.has("d")) {
			JSONArray data_array = initialData.getJSONArray("d");
			data_array.forEach(o -> {
				JSONObject object = (JSONObject) o;
				
				IntelProduct product = new IntelProduct();
				
				product.setProductId(object.get("ProductId").toString());
				product.setProductName(object.get("ProductName").toString());
				product.setProductFamilyId(object.get("ProductFamilyId").toString());
				product.setProductSeriesId(object.get("ProductSeriesId").toString());
				product.setMarketSegment(object.get("MarketSegment").toString());
				
				products.add(product);
			});
		}
		
		return products;
	}
	
	public static ArrayList<Processor> getProcessorsFromJSON(JSONTokener tokener) {
		ArrayList<Processor> processors = new ArrayList<Processor>();
		
		JSONObject initialData = new JSONObject(tokener);
		
		if(initialData.has("d")) {
			JSONArray data_array = initialData.getJSONArray("d");
			data_array.forEach(o -> {
				JSONObject object = (JSONObject) o;
				
				Processor product = new Processor();
				product.setProductId(object.get("ProductId").toString());
				product.setProductName(object.get("ProductName").toString());
				product.setProductFamilyId(object.get("ProductFamilyId").toString());
				product.setProductSeriesId(object.get("ProductSeriesId").toString());
				product.setMarketSegment(object.get("MarketSegment").toString());
				
				product.setProcessorBrandId(object.get("ProcessorBrandId").toString());
				product.setMaxTDP(object.get("MaxTDP").toString());
				product.setCoreCount(object.get("CoreCount").toString());
				product.setBusType(object.get("BusType").toString());
				product.setBusBandwidth(object.get("BusBandwidth").toString());
				product.setBusTypeUnits(object.get("BusTypeUnits").toString());
				product.setCache(object.get("Cache").toString());
				product.setCacheType(object.get("CacheType").toString());
				product.setClockSpeed(object.get("ClockSpeed").toString());
				product.setTurboSpeed(object.get("ClockSpeedMax").toString());
				product.setThreadCount(object.get("ThreadCount").toString());
				
				processors.add(product);
			});
		}
		
		return processors;
	}
	
	public static ArrayList<Processor> getProcessorsByNameCache(String name) {
		ArrayList<Processor> processors = new ArrayList<Processor>();
		for(Processor processor : Storage.getProcessorCache().getProcessors())
			if(processor.getProductName().toLowerCase().contains(name.toLowerCase()) || processor.getProductName().toLowerCase().replaceAll("[^\\x00-\\x7F]", "").contains(name.toLowerCase()))
				processors.add(processor);
		return processors;
	}
	
	@Deprecated
	public static ArrayList<IntelProduct> getProcessorsByName(String name) {
		URIBuilder ub = new URIBuilder();
		ub.setScheme("https");
		ub.setHost(BASE);
		ub.setPath(PROCESSOR);
		ub.addParameter("api_key", API_KEY);
		ub.addParameter("$format", "json");
		ub.addParameter("$select", "ProductId,ProductName,ProductFamilyId,ProductSeriesId,MarketSegment");
		ub.addParameter("$filter", "substringof('" + name + "',ProductName)");
		
		JSONTokener tokener = APIHelper.getJSON(ub.toString());
		return APIIntel.getIntelProductsFromJSON(tokener);
	}
}