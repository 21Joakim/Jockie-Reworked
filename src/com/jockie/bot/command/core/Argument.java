package com.jockie.bot.command.core;

import com.jockie.bot.command.core.impl.Arguments.VerifiedValue;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface Argument<T> {
	
	/**
	 * This should be the default value for the verified variable when {@link #verify(Command, String)} has yet not been called
	 */
	public final int NOT_TESTED = -1;
	
	/**
	 * This should be returned by {@link #verify(Command, String)} if the argument is incorrect.
	 */
	public final int ARGUMENT_INCORRECT = 0;
	
	/**
	 * This should be returned by {@link #verify(Command, String)} if the argument is correct.
	 */
	public final int ARGUMENT_CORRECT = 1;
	
	/**
	 * This is an extension of {@link #ARGUMENT_CORRECT}.<br>
	 * This should be returned by {@link #verify(Command, String)} if the argument's {@link #hasToBeLast()} is equal to true 
	 * and the argument is correct but also if you have any reason to believe that the command will not continue after this point.
	 */
	public final int COMMAND_END = 2;
	
	/**
	 * @return a boolean to prove if the argument requires text, this will mostly return true but in a few rare cases such as attachments this will return false.
	 */
	public boolean requiresText();
	
	/**
	 * @return a boolean to prove if this argument has a default value.
	 */
	public boolean hasDefault();
	
	/**
	 * @return a boolean to prove if this argument can be a so called endless argument.
	 */
	public boolean canBeEndless();
	
	/**
	 * This should only be true if the argument requires more than one word of content and no other separator is apparent.
	 * @return a boolean to prove if this argument has to be the last argument.
	 */
	public boolean hasToBeLast();
	
	/**
	 * @param event this parameter is required because in some cases such as the user argument the default value can not be set prematurely.
	 * @return a default value of this argument if it has been set. See {@link #hasDefault()}
	 */
	public T getDefault(MessageReceivedEvent event);
	
	/**
	 * Used to display the default value in help commands
	 * 
	 * @param event
	 * @return a displayable version of {@link #getDefault(MessageReceivedEvent)}
	 */
	public String getDisplayableDefault(MessageReceivedEvent event);
	
	/**
	 * @return information about the value.
	 * <br> Example - <strong>The person you wish to kick</strong> for a user argument.
	 */
	public String getValueInformation();
	
	/**
	 * @return the displayable name of the argument.
	 * <br> Example - <strong>[TEXT]</strong> for a String.
	 */
	public String getDisplayableName();
	
	/**
	 * @param event this parameter is required because in some cases such as the user argument the value can not be verified without access to the event.
	 * @param command to check if the argument is correctly fit in to the standards of the command.
	 * @param value argument sub-stringed from the discord message.
	 * @return a {@link VerifiedValue} that holds an Integer which shows the next operation of the argument handler and the "verified value"
	 */
	public VerifiedValue<T> verify(MessageReceivedEvent event, Command command, String value);
}