package com.jockie.bot.command.utility;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue.ArgumentEntry;
import com.jockie.bot.command.core.impl.CommandImpl;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandBase64 extends CommandImpl {

	public CommandBase64() {
		super("base64", 
			new ArgumentTypeValue("Base64 operation mode", null, 
				new ArgumentEntry("Base64 Encode", "ENCODE", "ENCODE"), 
				new ArgumentEntry("Base64 Decode", "DECODE", "DECODE")
			), new ArgumentString("Text"));
		super.setCommandDescription("Base64 encode/decode a message");
		super.setBeta(true);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		String action = (String) arguments[0];
		
		byte[] data = ((String) arguments[1]).getBytes(StandardCharsets.UTF_8);
		if(action.equals("ENCODE")) {
			event.getChannel().sendMessage(Base64.getMimeEncoder().encodeToString(data)).queue();
		}else if(action.equals("DECODE")) {
			String decoded = null;
			
			try {
				decoded = new String(Base64.getMimeDecoder().decode(data), "UTF-8");
			}catch(Exception e1) {}
			
			if(decoded != null && decoded.length() > 0) {
				event.getChannel().sendMessage(decoded).queue();
			}else{
				event.getChannel().sendMessage("Provided text was not Base64 encoded").queue();
			}
		}
	}
}