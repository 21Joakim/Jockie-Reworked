package com.jockie.bot.command.information;

import java.awt.Color;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import com.jockie.bot.Statistics;
import com.jockie.bot.Statistics.CommandDiagnostic;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.utility.Utility;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandStatistics extends CommandImpl {
	public CommandStatistics() {
		super("statistics");
		super.setCommandDescription("Basic Jockie statistics");
		super.setBeta(true);
		super.setDeveloperCommand(true);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		String information = "Statistics for the past " + Utility.formattedTime(ZonedDateTime.now(ZoneId.of("GMT+0")).toEpochSecond() - Statistics.getLastStatisticsInsert().toEpochSecond()) + "\n\n";
		
		long guild_messages = Statistics.getActionCount(Statistics.GUILD_MESSAGE);
		long pm_messages = Statistics.getActionCount(Statistics.PM_MESSAGE);
		long successful_commands = Statistics.getTotalSuccessfulCommands();
		
		information = information + "Guild messages : " + guild_messages + "\n";
		information = information + "Private messages : " + pm_messages + "\n";
		information = information + "Successful commands : " +  successful_commands + "\n\n";
		
		long new_guilds = Statistics.getActionCount(Statistics.NEW_GUILD);
		long lost_guilds = Statistics.getActionCount(Statistics.LOST_GUILD);
		
		information = information + "New Guilds : " + new_guilds + "\n";
		information = information + "Lost Guilds : " + lost_guilds + "\n\nCommands :\n";
		
		List<CommandDiagnostic> commands = Statistics.getCommandDiagnostic();
		
		for(int i = 0; i < commands.size(); i++) {
			CommandDiagnostic command_diagnostic = commands.get(i);
			information = information 
				+ command_diagnostic.getCommand()
				+ " : " + command_diagnostic.getExecutionTimes()
				+ "\nAverage Execution Time " + new BigDecimal((command_diagnostic.getAverageExecutionTime()/Math.pow(10, 9))) + " second(s)\n";
		}
		
		EmbedBuilder embed_builder = new EmbedBuilder();
		embed_builder.setDescription(information);
		embed_builder.setColor(Color.CYAN);
		
		event.getChannel().sendMessage(embed_builder.build()).queue();
	}
}