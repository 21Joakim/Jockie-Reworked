package com.jockie.bot.database;

import com.jockie.sql.base.Table;

public enum Database implements Table {
	PERSON("PERSON"),
	GUILD("GUILD_PROPERTIES"),
	USER_INFORMATION("USER_INFORMATION"),
	STATISTICS("STATISTICS"),
	BETA("BETA"),
	COMMAND_REQUEST("COMMAND_REQUEST");
	
	private String value;
	
	private Database(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}