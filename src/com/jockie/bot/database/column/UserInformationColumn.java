package com.jockie.bot.database.column;

import com.jockie.bot.database.Database;
import com.jockie.sql.base.Column;
import com.jockie.sql.base.Table;

public enum UserInformationColumn implements Column {
	USER_ID("USER_ID"),
	OSU_ID("OSU_ID"),
	BIRTHDAY("BIRTHDAY"),
	COUNTRY("COUNTRY"),
	MEASUREMENT_SYSTEM("MEASUREMENT_SYSTEM"),
	BIO("BIOGRAPHY"),
	AUTHORITY("AUTHORITY");
	
	private String value;
	
	private UserInformationColumn(String value) {
		this.value = value;
	}
	
	public Table getTable() {
		return Database.USER_INFORMATION;
	}
	
	public String getValue() {
		return value;
	}
}