package com.jockie.bot.command.developer;

import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue.ArgumentEntry;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.database.Database;
import com.jockie.bot.database.column.GuildColumn;
import com.jockie.bot.main.GuildProperties;
import com.jockie.bot.main.JockieBot;
import com.jockie.sql.action.ActionSet;
import com.jockie.sql.base.Where.Operator;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandSetServerSettings extends CommandImpl {

	public CommandSetServerSettings() {
		super("set setting", new ArgumentTypeValue("Setting", new ArgumentEntry("Is BETA-Test server", "BETA", "BETA")), new ArgumentString("Setting value"));
		super.setCommandDescription("Set developer-level settings for a server");
		super.setPMTriggerable(false);
		super.setBotTriggerable(false);
		super.setBeta(true);
		super.setDeveloperCommand(true);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		String setting = (String) arguments[0];
		String value = (String) arguments[1];
		
		if(setting.equals("BETA")) {
			if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
				boolean beta = Boolean.parseBoolean(value);
				
				GuildProperties properties = JockieBot.getGuildProperties().get(event.getGuild().getId());
				if(properties.isBetaServer() != beta) {
					properties.setBetaServer(beta);
					
					ActionSet set = JockieBot.getDatabase().set(Database.GUILD);
					set.getSet().set(GuildColumn.BETA_SERVER, beta + "");
					set.getWhere().where(GuildColumn.GUILD_ID, Operator.EQUAL, event.getGuild().getId());
					set.execute();
				}
				event.getChannel().sendMessage(event.getAuthor().getAsMention() + " server has now been set to a " + ((beta) ? "" : "non-") + "beta server!").queue();
			}else event.getChannel().sendMessage("Incorrect setting value, correct values are **true**/**false**").queue();
		}
	}
}