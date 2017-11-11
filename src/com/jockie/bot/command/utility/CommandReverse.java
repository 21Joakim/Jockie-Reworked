package com.jockie.bot.command.utility;

import com.jockie.bot.command.core.impl.Arguments.ArgumentBoolean;
import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.CommandImpl;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandReverse extends CommandImpl {

	public CommandReverse() {
		super("reverse", new ArgumentBoolean("Should initial message be deleted? true/false", false), new ArgumentString("Text"));
		super.setCommandDescription("Reverse a text");
		super.setBeta(true);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		if(event.isFromType(ChannelType.TEXT)) {
			if((Boolean) arguments[0]) {
				event.getChannel().deleteMessageById(event.getMessage().getId()).queue();
			}
		}
		event.getChannel().sendMessage(new StringBuilder(((String) arguments[1])).reverse().toString()).queue();
	}
}