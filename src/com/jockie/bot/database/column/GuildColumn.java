package com.jockie.bot.database.column;

import com.jockie.bot.database.Database;
import com.jockie.sql.base.Column;
import com.jockie.sql.base.Table;

public enum GuildColumn implements Column {
	GUILD_ID("GUILD_ID"),
	PREFIX("PREFIX"),
	LOG("LOG"),
	MENTION_USERS("MENTION_USERS_IN_COMMANDS"),
	BETA_SERVER("BETA_SERVER"),
	AUTO_ROLE("AUTO_ROLE");
	
	private String value;
	
	private GuildColumn(String value) {
		this.value = value;
	}
	
	public Table getTable() {
		return Database.GUILD;
	}
	
	public String getValue() {
		return value;
	}
}