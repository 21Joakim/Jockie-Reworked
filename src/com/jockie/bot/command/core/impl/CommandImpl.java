package com.jockie.bot.command.core.impl;

import com.jockie.bot.command.core.Argument;
import com.jockie.bot.command.core.Command;

import net.dv8tion.jda.core.Permission;

public abstract class CommandImpl implements Command {
	
	private String command;
	
	private String commandDescription;
	
	private Argument<?>[] arguments;
	
	private Permission[] bot_discord_permissions_needed = {};
	private Permission[] author_discord_permissions_needed = {};
	
	private boolean endlessArgument;
	private boolean caseSensitive;
	
	private boolean botTriggerable;
	
	private boolean pmTriggerable = true;
	private boolean guildTriggerable = true;
	
	private boolean deprecated = false;
	private boolean hidden = false;
	private boolean beta = false;
	private boolean developer_command = false;
	
	public CommandImpl(String command, boolean caseSensitive, boolean botTriggerable, boolean endlessArgument, Argument<?>... arguments) {
		this.command = command;
		this.caseSensitive = caseSensitive;
		this.botTriggerable = botTriggerable;
		this.endlessArgument = endlessArgument;
		this.arguments = arguments;
	}
	
	public CommandImpl(String command, boolean caseSensitive, boolean botTriggerable, Argument<?>...arguments) {
		this(command, caseSensitive, botTriggerable, false, arguments);
	}
	
	public CommandImpl(String command, boolean endlessArgument, Argument<?>... arguments) {
		this(command, false, true, endlessArgument, arguments);
	}
	
	public CommandImpl(String command, Argument<?>... arguments) {
		this(command, false, true, false, arguments);
	}
	
	public String getUsage() {		
		return this.getCommand() + " " + Command.getDisplayableArguments(this.getArguments());
	}
	
	public String getCommand() {
		return this.command;
	}
	
	public String getDescription() {
		return this.commandDescription;
	}
	
	public Argument<?>[] getArguments() {
		return this.arguments;
	}
	
	public Permission[] getBotDiscordPermissionsNeeded() {
		return this.bot_discord_permissions_needed;
	}
	
	public Permission[] getAuthorDiscordPermissionsNeeded() {
		return this.author_discord_permissions_needed;
	}
	
	public boolean hasEndlessArgument() {
		return this.endlessArgument;
	}
	
	public boolean isCaseSensitive() {
		return this.caseSensitive;
	}
	
	public boolean isBotTriggerable() {
		return this.botTriggerable;
	}
	
	public boolean isPMTriggerable() {
		return this.pmTriggerable;
	}
	
	public boolean isGuildTriggerable() {
		return this.guildTriggerable;
	}
	
	public boolean isDeprecated() {
		return this.deprecated;
	}
	
	public boolean isHidden() {
		return this.hidden;
	}
	
	public boolean isBeta() {
		return this.beta;
	}
	
	public boolean isDeveloperCommand() {
		return this.developer_command;
	}
	
	protected void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}
	
	protected void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	protected void setBeta(boolean beta) {
		this.beta = beta;
	}
	
	protected void setDeveloperCommand(boolean developer_command) {
		this.developer_command = developer_command;
	}
	
	protected void setGuildTriggerable(boolean guildTriggerable) {
		this.guildTriggerable = guildTriggerable;
	}
	
	protected void setPMTriggerable(boolean pmTriggerable) {
		this.pmTriggerable = pmTriggerable;
	}
	
	protected void setBotTriggerable(boolean botTriggerable) {
		this.botTriggerable = botTriggerable;
	}
	
	protected void setBotDiscordPermissionsNeeded(Permission... permissions) {
		this.bot_discord_permissions_needed = permissions;
	}
	
	protected void setAuthorDiscordPermissionsNeeded(Permission... permissions) {
		this.author_discord_permissions_needed = permissions;
	}
	
	protected void setCommandDescription(String commandDescription) {
		this.commandDescription = commandDescription;
	}
	
	public String toString() {
		int spaces = (20 - this.getCommand().length());
		String toString = this.getCommand();
		
		for(int i = 0; i < spaces; i++)
			toString = toString + " ";
		
		return toString = toString + Command.getDisplayableArguments(this.getArguments());
	}
}