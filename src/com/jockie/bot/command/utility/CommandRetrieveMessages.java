package com.jockie.bot.command.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.jockie.bot.utility.Utility;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.command.core.impl.Arguments.ArgumentNumber;
import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.main.JockieBot;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandRetrieveMessages extends CommandImpl {

	public CommandRetrieveMessages() {
		super("retrieve history", new ArgumentNumber("Amount of messages", 50), new ArgumentString("Message to start from (Message ID)"));
		super.setDeprecated(true);
		super.setHidden(true);
		super.setBeta(true);
		super.setCommandDescription("Extract given amount of messages to a text file");
		super.setBotDiscordPermissionsNeeded(Permission.MESSAGE_ATTACH_FILES);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		String last_message_id = (String) arguments[1];
		MessageHistory history = null;
		
		if((long) arguments[0] > 300) {
			event.getChannel().sendMessage("You may not retrieve more than 100 messages").queue();
			return;
		}else if((long) arguments[0] < 1) {
			event.getChannel().sendMessage("You have to retrieve at least one message").queue();
		}
		
		try {
			event.getChannel().getMessageById(last_message_id).complete();
		}catch(Exception e) {
			event.getChannel().sendMessage("Message was incorrect/deleted or not a message of this channel").queue();
			return;
		}
		
		int total_messages_to_retrieve = (int) ((long) arguments[0]);
		
		List<Message> allMessages = new ArrayList<Message>();
		
		while(allMessages.size() < total_messages_to_retrieve) {
			int messages_to_get = 0;
			
			if(total_messages_to_retrieve - allMessages.size() >= 100)
				messages_to_get = 100;
			else messages_to_get = (total_messages_to_retrieve - allMessages.size());
			
			history = event.getChannel().getHistoryAround(last_message_id, 1).complete();
			
			for(Message message : history.getRetrievedHistory())
				allMessages.add(message);
			
			if(messages_to_get - 1 > 0) {
				List<Message> messages = history.retrievePast(messages_to_get - 1).complete();
				
				for(int i2 = 0; i2 < messages.size() - 1; i2++)
					allMessages.add(messages.get(i2));
				
				last_message_id = messages.get(messages.size() - 1).getId();
			}
		}
		
		Collections.reverse(allMessages);
		
		String content = 
			"Collected from " + event.getGuild().getName() + " (" + event.getGuild().getId() + ")"
			+ " in " + event.getChannel().getName() + "(" + event.getChannel().getId() + ")"
			+ " on " + new Date().toString()
			+ " by :"
			+ "\n   Current Name : " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() 
			+ "\n   ID : " + event.getAuthor().getId();
		
		for(Message message : allMessages) {
			content += "\n\n";
			content += message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator() + " (" + message.getAuthor().getId() + ")\n";
			content += message.getRawContent();
			if(message.getAttachments().size() > 0) {
				content += "\nMessage contains attachment(s) :\n";
				for(int i = 0; i < message.getAttachments().size(); i++) {
					content += "   " + message.getAttachments().get(i).getUrl();
				}
			}
		}
		
		FileOutputStream outputStream = null;
		
		File file = new File(JockieBot.FILE_STORAGE_PATH + Base64.encode((allMessages.size() + "." + event.getChannel().getId() + "." + arguments[1] + "." + Utility.randomNumber(6)).getBytes()) + ".txt");
		
		try {
			outputStream = new FileOutputStream(file);
			
			outputStream.write(content.getBytes("UTF-8"));
			
			event.getChannel().sendFile(file, null).complete();
		}catch(IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(outputStream != null)
					outputStream.close();
				
				if(file.exists())
					Files.delete(file.toPath());
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}