package com.jockie.bot.command.api.currency;

import java.util.ArrayList;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jockie.bot.APIs.APIHelper;
import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.utility.Utility;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandCurrencyLatest extends CommandImpl {
	
	private static final String[] TOP_20_CURRENCIES = {"USD","EUR","JPY","GBP","AUD","CAD","CHF","CNY","SEK","NZD","MXN","SGD","HKD","NOK","KRW","TRY","RUB","INR","BRL","ZAR"};

	public CommandCurrencyLatest() {
		super("currency latest", new ArgumentString("Base for currency data as ISO 4217 code, example EUR for euro", "EUR"));
		super.setCommandDescription("Provides currency rates. Be aware that this might not work for every currency in the world.");
		super.setBeta(true);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		String base = (String) arguments[0];
		
		if(base.length() == 3) {
			base = base.toUpperCase();
			
			ArrayList<String> rates = new ArrayList<String>();
			
			for(int i = 0; i < TOP_20_CURRENCIES.length; i++)
				if(!base.equals(TOP_20_CURRENCIES[i]))
					rates.add(base + TOP_20_CURRENCIES[i]);
			
			JSONArray array = CommandCurrencyLatest.getRates(rates.toArray(new String[0]));
			if(array != null) {
				String text = "";
				for(int i = 0; i < array.length(); i++) {
					JSONObject entry = array.getJSONObject(i);
					if(!entry.getString("Rate").equals("N/A")) {
						text = text + "**" + entry.getString("Name") + "** : " + entry.getString("Rate") + "\n";
					}
				}
				
				if(text.length() == 0) {
					event.getChannel().sendMessage("No rates found! The currency code provided might not be a valid currency.").queue();
					return;
				}
				
				event.getChannel().sendMessage(new EmbedBuilder().setDescription(text).build()).queue();
			}else{
				event.getChannel().sendMessage("Something went wrong!").queue();
			}
		}else{
			event.getChannel().sendMessage("Incorrect ISO 639-2 code").queue();
		}
	}
	
	public static JSONArray getRates(String[] rate_ids) {
		URIBuilder ub = new URIBuilder();
		ub.setScheme("https");
		ub.setHost("query.yahooapis.com/");
		ub.setPath("v1/public/yql");
		ub.addParameter("q", "select Name, Rate, Date, Time from yahoo.finance.xchange where pair in (\"" + Utility.toString(rate_ids, "\", \"") + "\")");
		ub.addParameter("env", "store://datatables.org/alltableswithkeys");
		ub.addParameter("format", "json");
		
		JSONObject object = new JSONObject(APIHelper.getJSON(ub.toString()));
		if(object.has("query")) {
			object = object.getJSONObject("query").getJSONObject("results");
			
			if(object.get("rate") instanceof JSONArray) {
				return object.getJSONArray("rate");
			}else{
				return new JSONArray("[" + object.getJSONObject("rate").toString() + "]");
			}
		}else return null;
	}
}