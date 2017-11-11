package com.jockie.bot.command.debug;

import com.jockie.bot.command.core.impl.CommandImpl;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandReports extends CommandImpl {
	
	public CommandReports() {
		super("requests");
		super.setCommandDescription("Get status on your requests/reports");
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		
	}
}