package com.jockie.bot.command.utility;

import java.util.Random;

import com.jockie.bot.command.core.impl.Arguments.ArgumentBoolean;
import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.CommandImpl;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandScrambleCase extends CommandImpl {

	public CommandScrambleCase() {
		super("scramble case", new ArgumentBoolean("Should initial message be deleted? true/false", false), new ArgumentString("Text"));	
		super.setCommandDescription("Scrambles the case of each letter in a text");
		super.setBeta(true);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		if(event.isFromType(ChannelType.TEXT)) {
			if((Boolean) arguments[0]) {
				event.getChannel().deleteMessageById(event.getMessage().getId()).queue();
			}
		}
		
		String text = (String) arguments[1];
		text = text.toLowerCase();
		
		Random random = new Random();
		
		char[] characters = new char[text.length()];
		for(int i = 0; i < characters.length; i++) {
			characters[i] = text.charAt(i);
			if(random.nextBoolean()) {
				characters[i] = Character.toUpperCase(characters[i]);
			}
		}
		
		text = new String(characters);
		
		event.getChannel().sendMessage(text).queue();
	}
}