package com.jockie.bot.APIs.intel;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

public class Processor extends IntelProduct {
	
	private String processor_brand_id;
	
	public String getProcessorBrandId() { return this.processor_brand_id; }
	public void setProcessorBrandId(String processor_bran_id) { this.processor_brand_id = processor_bran_id; }
	
	private String max_tdp;
	
	public String getMaxTDP() { return this.max_tdp; }
	public void setMaxTDP(String max_tdp) { this.max_tdp = max_tdp; }
	
	private String core_count;
	
	public String getCoreCount() { return this.core_count; }
	public void setCoreCount(String core_count) { this.core_count = core_count; }
	
	private String clock_speed;
	
	public String getClockSpeed() { return this.clock_speed; }
	public void setClockSpeed(String clock_speed) { this.clock_speed = clock_speed; }
	
	private String clock_speed_max;
	
	public String getTurboSpeed() { return this.clock_speed_max; }
	public void setTurboSpeed(String clock_speed_max) { this.clock_speed_max = clock_speed_max; }
	
	private String thread_count;
	
	public String getThreadCount() { return this.thread_count; }
	public void setThreadCount(String thread_count) { this.thread_count = thread_count; }
	
	private String cache;
	
	public String getCache() { return this.cache; }
	public void setCache(String cache) { this.cache = cache; }
	
	private String cache_type;
	
	public String getCacheType() { return this.cache_type; }
	public void setCacheType(String cache_type) { this.cache_type = cache_type; }
	
	private String bus_type;
	
	public String getBusType() { return this.bus_type; }
	public void setBusType(String bus_type) { this.bus_type = bus_type; }
	
	private String bus_bandwitdh;
	
	public String getBusBandwidth() { return this.bus_bandwitdh; }
	public void setBusBandwidth(String bus_bandwitdh) { this.bus_bandwitdh = bus_bandwitdh; }
	
	private String bus_type_units;
	
	public String getBusTypeUnits() { return this.bus_type_units; }
	public void setBusTypeUnits(String bus_type_units) { this.bus_type_units = bus_type_units; }
	
	public EmbedBuilder getInformationEmbed() {
		EmbedBuilder embed_builder = new EmbedBuilder();
		
		embed_builder.addField("Processor", this.getProductName(), true);
		embed_builder.addField(getPerformanceField("Performance", false));
		
		return embed_builder;
	}
	
	public Field getPerformanceField(String title, boolean inline) {
		String performance_data = "";
		performance_data = performance_data + "Number of cores : " + this.getCoreCount() + "\n";
		performance_data = performance_data + "Number of threads : " + this.getThreadCount() + "\n";
		performance_data = performance_data + "Base Frequency : " + this.getClockSpeed() + "\n";
		
		if(!this.getTurboSpeed().equals("null"))
			performance_data = performance_data + "Max Turbo Frequency : " + this.getTurboSpeed();
		
		performance_data = performance_data + "\n";
		
		if(!this.getCache().equals("null")) {
			performance_data = performance_data + "Cache : " + this.getCache();
			if(!this.getCacheType().equals("null"))
				performance_data = performance_data + " " + this.getCacheType();
		}
		
		performance_data = performance_data + "\n";
		
		if(!this.getBusBandwidth().equals("null")) {
			performance_data = performance_data + "Bus Speed : " + this.getBusBandwidth();
			if(!this.getBusTypeUnits().equals("null"))
				performance_data = performance_data + " " + this.getBusTypeUnits();
			if(!this.getBusType().equals("null"))
				performance_data = performance_data + " " + this.getBusType();
		}
		
		performance_data = performance_data + "\n";
		
		performance_data = performance_data + "TDP : " + this.getMaxTDP() + "W";
		
		return new Field(title, performance_data, inline);
	}
}