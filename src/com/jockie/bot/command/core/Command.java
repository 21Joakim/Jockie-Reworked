package com.jockie.bot.command.core;

import com.jockie.bot.command.core.impl.Arguments;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface Command {
	
	/**
	 * @return the command which the Listener should look for.
	 */
	public String getCommand();
	
	/**
	 * @return how this command should be used.
	 */
	public String getUsage();
	
	/**
	 * @return a short description of what this command does.
	 */
	public String getDescription();
	
	/**
	 * @return the arguments the Listener should look for when validating if this command is correct.
	 */
	public Argument<?>[] getArguments();
	
	/**
	 * @return the discord permissions required for this command to function correctly.
	 */
	public Permission[] getBotDiscordPermissionsNeeded();
	
	/**
	 * @return the discord permissions the author is required to have to trigger this command.
	 */
	public Permission[] getAuthorDiscordPermissionsNeeded();
	
	/**
	 * @deprecated Not correctly implemented in {@link com.jockie.bot.command.core.impl.CommandImpl CommandImpl} and {@link com.jockie.bot.command.core.impl.CommandListener CommandListener}
	 * 
	 * @return a boolean that will prove if a this command has a so called <strong>endless argument</strong> meaning that the amount of arguments is not specified because it could possibly be endless
	 * (Though not in practice considering the 2000 character limit on Discord)
	 */
	@Deprecated
	public boolean hasEndlessArgument();
	
	/**
	 * @return a boolean that will prove if this command is deprecated for whatever reason.
	 */
	public boolean isDeprecated();
	
	/**
	 * @return a boolean that will prove if this command is hidden and will therefore not be shown in help command(s)
	 */
	public boolean isHidden();
	
	/**
	 * @return a boolean that will prove if this command is beta, if beta the commands can only be triggered by beta allowed users
	 */
	
	public boolean isBeta();
	
	/**
	 * @return a boolean that will prove if this command is a <strong>developer</strong> command, if it is a developer command it can only be triggered by developers/authorized users
	 */
	public boolean isDeveloperCommand();
	
	/**
	 * @return a boolean that will prove if this command can be triggered by a bot {@link net.dv8tion.jda.core.entities.User#isBot() User.isBot()}
	 */
	public boolean isBotTriggerable();
	
	/**
	 * @return a boolean that will prove if this command is case sensitive.<p>
	 * For instance if {@link com.jockie.bot.command.core.Command#getCommand() Command.getCommand()} 
	 * is equal to <strong>ping</strong> and {@link com.jockie.bot.command.core.Command#isCaseSensitive() Command.isCaseSensitive()} 
	 * is <strong>false</strong> then the command could be triggered by any message that {@link String#toLowerCase()} would be equal to <strong>ping</strong>.<br>
	 * On the other hand if {@link com.jockie.bot.command.core.Command#isCaseSensitive() Command.isCaseSensitive()} is <strong>true</strong> and
	 * {@link com.jockie.bot.command.core.Command#getCommand() Command.getCommand()} is equal to <strong>PiNg</strong> 
	 * then the command could only be triggered if the message is equal to <strong>PiNg</strong> 
	 */
	public boolean isCaseSensitive();
	
	/**
	 * @return a boolean that will prove if this command should be able to be triggered by private messages.
	 */
	public boolean isPMTriggerable();
	
	/**
	 * @return a boolean that will prove if this command should be able to be triggered by guild messages.
	 */
	public boolean isGuildTriggerable();
	
	/**
	 * This is what should be executed when this command is considered to be valid.
	 * 
	 * @param event the event which triggered the command.
	 * @param prefix the prefix which triggered the command.
	 * @param arguments the arguments which triggered the command.
	 */
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments);
	
	/**
	 * Based of provided arguments in {@link Arguments}
	 * 
	 * @return provided arguments in a readable format
	 */
	public static String getDisplayableArguments(Argument<?>... arguments) {
		String arguments_str = "";
		
		for(Argument<?> argument : arguments) {
			arguments_str = arguments_str + "[" + argument.getDisplayableName();
			if(argument instanceof ArgumentTypeValue) {
				ArgumentTypeValue type_argument = (ArgumentTypeValue) argument;
				
				arguments_str = arguments_str + " {";
				for(String str : type_argument.getPossibleValues())
					arguments_str = arguments_str + str + ", ";
				
				arguments_str = arguments_str.substring(0, arguments_str.length() - 2) + "}";
			}
			arguments_str = arguments_str + "], ";
		}
		
		if(arguments_str.length() > 2)
			arguments_str = arguments_str.substring(0, arguments_str.length() - 2);
		
		return arguments_str;
	}
}