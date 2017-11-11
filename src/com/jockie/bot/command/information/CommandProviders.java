package com.jockie.bot.command.information;

import com.jockie.bot.command.core.impl.CommandImpl;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandProviders extends CommandImpl {
	
	public CommandProviders() {
		super("providers");
		super.setCommandDescription("Get data providers for this bot (APIs)");
		super.setBeta(true);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		EmbedBuilder embed_builder = new EmbedBuilder();
		
		embed_builder.setDescription(
				"**Countries**\n     restcountries.eu\n\n"
				+ "**Weather**\n     weather.com (wxdata.weather.com)\n\n"
				+ "**Intel Products**\n     intel.com (odata.intel.com)\n\n"
				+ "**OSU**\n     osu.ppy.sh\n\n"
				+ "**Quotes**\n     forismatic.com\n\n"
				+ "**Currency Rates**\n    yahoo.com (query.yahooapis.com)");
		
		event.getChannel().sendMessage(embed_builder.build()).queue();
	}
}