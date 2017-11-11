package com.jockie.bot.command.intro;

import com.jockie.bot.command.core.impl.CommandImpl;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandTermsOfService extends CommandImpl {
	
	public CommandTermsOfService() {
		super("tos");
		super.setCommandDescription("Jockie terms of service!");
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		String tos = ""
			+ "By using Jockie and its service you agree that when necessary and only then we can store data about your Discord account (Such as user id and name) "
			+ "and any actions you may proceed to do (Such as sending and reaction to messages) in a database with proper security for further and future use, with the limitation of commands and diagnostics. "
			+ "You do also agree that this data can be displayed and provided (In form of commands) on request by other users without any limitations.\n"
			+ "Additionally because Discord is not intended and should not be used by an individuel under 13 years of age you agree that when using certain commands that would collect information "
			+ "such as your birthday that you are over 13 years of age."
			+ "\n\n**If you do not agree to these terms then we kindly advise you to not use our bot!**";
		
		event.getChannel().sendMessage(tos).queue();
	}
}