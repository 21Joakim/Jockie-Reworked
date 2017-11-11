package com.jockie.bot.database.column;

import com.jockie.bot.database.Database;
import com.jockie.sql.base.Column;
import com.jockie.sql.base.Table;

public enum RequestColumn implements Column {
	REQUEST_TYPE("REQUEST_TYPE"),
	REQUEST_USER_ID("REQUEST_USER_ID"),
	REQUEST_DATE("REQUEST_DATE"),
	REQUEST("REQUEST"),
	REQUEST_RESOLVED("REQUEST_RESOLVED"),
	REQUEST_RESOLVED_DATE("REQUEST_RESOLVED_DATE");
	
	private String value;
	
	private RequestColumn(String value) {
		this.value = value;
	}
	
	public Table getTable() {
		return Database.COMMAND_REQUEST;
	}
	
	public String getValue() {
		return this.value;
	}
}