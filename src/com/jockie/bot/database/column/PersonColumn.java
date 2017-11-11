package com.jockie.bot.database.column;

import com.jockie.bot.database.Database;
import com.jockie.sql.base.Column;
import com.jockie.sql.base.Table;

public enum PersonColumn implements Column {
	USER_ID("USER_ID"),
	MARRIED("MARRIED"),
	PROPOSE("PROPOSE"),
	PARTNER("PARTNER"),
	MARRIAGE_DATE("MARRIAGE_DATE"),
	CAN_DIVORCE("CAN_DIVORCE");
	
	private String value;
	
	private PersonColumn(String value) {
		this.value = value;
	}
	
	public Table getTable() {
		return Database.PERSON;
	}
	
	public String getValue() {
		return value;
	}
}