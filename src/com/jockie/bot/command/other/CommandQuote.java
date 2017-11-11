package com.jockie.bot.command.other;

import java.awt.Color;
import java.util.Random;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.jockie.bot.APIs.APIHelper;
import com.jockie.bot.command.core.impl.CommandImpl;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandQuote extends CommandImpl {
	
	private Random random = new Random();

	public CommandQuote() {
		super("quote");
		super.setCommandDescription("Random quote");
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		JSONTokener tokener = APIHelper.getJSON("https://api.forismatic.com/api/1.0/?method=getQuote&format=json&lang=en&key=" + (this.random.nextInt(999999) + 1));
		
		if(tokener != null) {
			JSONObject obj_quote = new JSONObject(tokener);
			
			String quote = obj_quote.getString("quoteText");
			String author = obj_quote.getString("quoteAuthor").trim();
			
			EmbedBuilder embed_builder = new EmbedBuilder();
			embed_builder.setColor(new Color(this.random.nextInt(255) + 1, this.random.nextInt(255) + 1, this.random.nextInt(255) + 1));
			
			embed_builder.setDescription(quote);
			
			embed_builder.appendDescription("\n\n **-** ");
			
			if(!author.equals("")) {
				embed_builder.appendDescription("*[" + author + "](https://en.wikipedia.org/wiki/" + author.replace(" ", "_") + ")*");
			}else{
				embed_builder.appendDescription("Anonymous");
			}
			
			event.getChannel().sendMessage(embed_builder.build()).queue();
			return;
		}
		
		event.getChannel().sendMessage("Something went wrong!").queue();
	}
}