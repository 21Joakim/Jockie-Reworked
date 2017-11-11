package com.jockie.bot.command.utility;

import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.CommandImpl;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandWords extends CommandImpl {
	
	public CommandWords() {
		super("words", new ArgumentString("Text"));
		super.setCommandDescription("Simple word-counter");
		super.setBeta(true);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		String text = (String) arguments[0];
		
		int total_characters = 0;
		int total_words = 0;
		
		int letters_since_last_seperator = 0;
		
		char[] characters = text.toCharArray();
		for(char character : characters) {
			if(Character.isLetterOrDigit(character)) {
				letters_since_last_seperator = letters_since_last_seperator + 1;
				
				if(total_characters == characters.length - 1) {
					letters_since_last_seperator = 0;
					total_words = total_words + 1;
				}
			}else{
				if(letters_since_last_seperator > 0) {
					letters_since_last_seperator = 0;
					total_words = total_words + 1;
				}
			}
			
			total_characters = total_characters + 1;
		}
		
		String message = "";
		message = message + "There is a total of " + total_characters + " **character(s)** ";
		message = message + "and " + total_words + " **word(s)**.\n\n";
		
		event.getChannel().sendMessage(message).queue();
	}
}