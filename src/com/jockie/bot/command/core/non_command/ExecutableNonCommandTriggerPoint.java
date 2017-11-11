package com.jockie.bot.command.core.non_command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class ExecutableNonCommandTriggerPoint extends NonCommandTriggerPoint {
	
	public ExecutableNonCommandTriggerPoint(String initial_message_id, String command, Object object) {
		super(initial_message_id, command, object);
	}
	
	public ExecutableNonCommandTriggerPoint(String command, Object object) {
		super(command, object);
	}
	
	public abstract boolean execute(MessageReceivedEvent event);
}