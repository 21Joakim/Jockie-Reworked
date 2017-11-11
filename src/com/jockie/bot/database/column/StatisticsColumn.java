package com.jockie.bot.database.column;

import com.jockie.bot.database.Database;
import com.jockie.sql.base.Column;
import com.jockie.sql.base.Table;

public enum StatisticsColumn implements Column {
	NAME("NAME"),
	VALUE("VALUE"),
	DATE_TIME_FROM("DATE_TIME_FROM"),
	DATE_TIME_TO("DATE_TIME_TO");
	
	private String value;
	
	private StatisticsColumn(String value) {
		this.value = value;
	}
	
	public Table getTable() {
		return Database.STATISTICS;
	}
	
	public String getValue() {
		return value;
	}
}