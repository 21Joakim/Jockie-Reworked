package com.jockie.bot.main;

public class GuildProperties {
	
	private String prefix;
	
	private boolean mention_users_in_commands;
	
	private boolean beta_server;
	
	private String auto_role;
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void setMentionUsers(boolean mention_users) {
		this.mention_users_in_commands = mention_users;
	}
	
	public void setBetaServer(boolean beta_server) {
		this.beta_server = beta_server;
	}
	
	public void setAutoRole(String auto_role) {
		this.auto_role = auto_role;
	}
	
	public String getPrefix() {
		return this.prefix;
	}
	
	public boolean shouldMentionUsers() {
		return this.mention_users_in_commands;
	}
	
	public boolean isBetaServer() {
		return this.beta_server;
	}
	
	public String getAutoRole() {
		return this.auto_role;
	}
}