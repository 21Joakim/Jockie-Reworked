package com.jockie.bot.database.column;

import com.jockie.bot.database.Database;
import com.jockie.sql.base.Column;
import com.jockie.sql.base.Table;

public enum BetaColumn implements Column {
	APPLIED_GUILD_ID("APPLIED_GUILD_ID"),
	APPLIED_USER_ID("APPLIED_USER_ID"),
	APPLIED_REASON("APPLIED_REASON"),
	ACCEPTED("ACCEPTED"),
	DATE_APPLIED("DATE_APPLIED"),
	DATE_ACCEPTED("DATE_ACCEPTED");
	
	private String value;
	
	private BetaColumn(String value) {
		this.value = value;
	}
	
	public Table getTable() {
		return Database.BETA;
	}
	
	public String getValue() {
		return this.value;
	}
}