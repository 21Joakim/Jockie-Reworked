package com.jockie.bot.APIs.intel;

public class IntelProduct {
	
	private String product_id;
	
	public String getProductId() { return product_id; }
	public void setProductId(String product_id) { this.product_id = product_id; }
	
	private String product_name;
	
	public String getProductName() { return product_name; }
	public void setProductName(String productName) { this.product_name = productName; }
	
	private String product_family_id;
	
	public String getProductFamilyId() { return product_family_id; }
	public void setProductFamilyId(String product_family_id) { this.product_family_id = product_family_id; }
	
	private String product_series_id;
	
	public String getProductSeriesId() { return product_series_id; }
	public void setProductSeriesId(String product_series_id) { this.product_series_id = product_series_id; }
	
	private String market_segment;
	
	public String getMarketSegment() { return market_segment; }
	public void setMarketSegment(String market_segment) { this.market_segment = market_segment; }
}