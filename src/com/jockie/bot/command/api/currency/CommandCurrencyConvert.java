package com.jockie.bot.command.api.currency;

import java.awt.Color;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jockie.bot.command.core.impl.Arguments.ArgumentNumber;
import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.CommandImpl;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandCurrencyConvert extends CommandImpl {

	public CommandCurrencyConvert() {
		super("convert currency", 
			new ArgumentString("Currency From (ISO 4217 Code)").setSpaceSeperated(true), 
			new ArgumentString("Currency To (ISO 4217 Code)").setSpaceSeperated(true),
			new ArgumentNumber("Amount of money to convert"));
		super.setCommandDescription("Convert an amount of money from one currency to another.");
		super.setBeta(true);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		String convert_from = (String) arguments[0];
		String convert_to = (String) arguments[1];
		long amount = (Long) arguments[2];
		
		if(amount <= 0)
			event.getChannel().sendMessage("Amount may not be less than 1").queue();
		
		JSONArray array = CommandCurrencyLatest.getRates(new String[]{convert_from.toUpperCase() + convert_to.toUpperCase()});
		if(array != null) {
			JSONObject entry = array.getJSONObject(0);
			
			if(!entry.getString("Rate").equals("N/A")) {
				double rate = Double.parseDouble(entry.getString("Rate"));
				
				String text = "**" + amount + " " + convert_from.toUpperCase() + "** is equal to **" + (amount * rate) + " " + convert_to.toUpperCase() + "**";
				
				event.getChannel().sendMessage(new EmbedBuilder().setDescription(text).setColor(Color.CYAN).setFooter("Updated : " + entry.getString("Date") /* Time-zone is not apparent + " " + entry.getString("Time")*/, null).build()).queue();
			}else{
				event.getChannel().sendMessage("No rates found").queue();
			}
		}else{
			event.getChannel().sendMessage("Something went wrong!").queue();
		}
	}
}