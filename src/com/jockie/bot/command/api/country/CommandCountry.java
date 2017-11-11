package com.jockie.bot.command.api.country;

import java.awt.Color;
import java.util.ArrayList;

import com.jockie.bot.APIs.country.APICountry;
import com.jockie.bot.APIs.country.Country;
import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue.ArgumentEntry;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.command.core.impl.CommandListener;
import com.jockie.bot.command.core.non_command.ExecutableNonCommandTriggerPoint;
import com.jockie.bot.command.core.non_command.PagedResult;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandCountry extends CommandImpl {

	public CommandCountry() {
		super("country", new ArgumentTypeValue(
			"The type you want to search by", 
			new ArgumentEntry("A combination of Alpha Code, Numeric Code and Name", "UNIQUE", "UNIQUE"),
			new ArgumentEntry("Country unique alpha 2 & 3 code", "ALPHACODE", "ALPHA"),
			new ArgumentEntry("Capital City", "CAPITAL", "CAPITAL"),
			new ArgumentEntry("Calling code", "CALLINGCODE", "CALLING"),
			new ArgumentEntry("Country unique numeric code", "NUMERICCODE", "NUMERIC"),
			new ArgumentEntry("Language name", "LANGUAGE", "LANGUAGE"),
			new ArgumentEntry("Language unique iso639_1 & 2 code", "LANGUAGECODE", "LANGUAGECODE"),
			new ArgumentEntry("Currency name", "CURRENCY", "CURRENCY"),
			new ArgumentEntry("Currency unique code", "CURRENCYCODE", "CURRENCYCODE"),
			new ArgumentEntry("Currency symbol", "CURRENCYSYMBOL", "CURRENCYSYMBOL")), new ArgumentString("Text to search by"));
		super.setCommandDescription("Get country information and data");
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		String countryStr = (String) arguments[1];
		
		ArrayList<Country> countries = null;
		
		if(arguments[0].equals("UNIQUE")) {
			countries = APICountry.searchByUnique(countryStr);
		}else if(arguments[0].equals("NAME")) {
			countries = APICountry.searchByName(countryStr);
		}else if(arguments[0].equals("ALPHACODE")) {
			countries = APICountry.searchByAlphaCode(countryStr);
		}else if(arguments[0].equals("CAPITAL")) {
			countries = APICountry.searchByCapitalCity(countryStr);
		}else if(arguments[0].equals("CALLINGCODE")) {
			countries = APICountry.searchByCallingCode(countryStr);
		}else if(arguments[0].equals("NUMERICCODE")) {
			countries = APICountry.searchByNumericCode(countryStr);
		}else if(arguments[0].equals("LANGUAGE")) {
			countries = APICountry.searchByLanguageName(countryStr);
		}else if(arguments[0].equals("LANGUAGECODE")) {
			countries = APICountry.searchByLanguageCode(countryStr);
		}else if(arguments[0].equals("CURRENCY")) {
			countries = APICountry.searchByCurrencyName(countryStr);
		}else if(arguments[0].equals("CURRENCYCODE")) {
			countries = APICountry.searchByCurrencyCode(countryStr);
		}else if(arguments[0].equals("CURRENCYSYMBOL")) {
			countries = APICountry.searchByCurrencySymbol(countryStr);
		}
		
		if(countries == null)
			return;
		
		if(countries.size() == 1) {
			event.getChannel().sendMessage(countries.get(0).getInformationEmbed().setColor(Color.CYAN).build()).queue();
		}else if(countries.size() > 1) {
			PagedResult<Country> paged_result = new PagedResult<Country>(countries, Country::getName);
			
			CommandListener.doPagedResult(event, this, paged_result, new ExecutableNonCommandTriggerPoint(this.getCommand(), paged_result) {
				public boolean execute(MessageReceivedEvent event) {
					int number = -1;
					
					try {
						number = Integer.parseInt(event.getMessage().getRawContent());
					}catch(Exception e) {}
					
					if(number != -1) {
						PagedResult<?> countries = (PagedResult<?>) this.getObject();
						
						if(number > 0 && number <= countries.getEntriesPerPage()) {
							EmbedBuilder embed_builder = ((Country) countries.getCurrentPageEntries().get(number - 1)).getInformationEmbed();
							
							embed_builder.setColor(Color.cyan);
							
							event.getChannel().sendMessage(embed_builder.build()).queue();
							
							return true;
						}
					}
					return false;
				}
			});
		}else{
			String description = "No country by that " + arguments[0];
			event.getChannel().sendMessage(new EmbedBuilder().setDescription(description).setColor(Color.RED).build()).queue();
		}
	}
}