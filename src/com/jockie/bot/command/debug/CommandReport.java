package com.jockie.bot.command.debug;

import java.time.ZoneId;

import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.database.Database;
import com.jockie.bot.database.column.RequestColumn;
import com.jockie.bot.main.JockieBot;
import com.jockie.bot.utility.Utility;
import com.jockie.sql.action.ActionInsert;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandReport extends CommandImpl {

	public CommandReport() {
		super("report", new ArgumentString("Problem"));
		super.setCommandDescription("Report a problem with the bot/any command (Provide as much information as possible)");
		super.setBotTriggerable(false);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		ActionInsert insert = JockieBot.getDatabase().insert(Database.COMMAND_REQUEST);
		insert.getInsert()
			.insert(RequestColumn.REQUEST_TYPE, "PROBLEM")
			.insert(RequestColumn.REQUEST_USER_ID, event.getAuthor().getId())
			.insert(RequestColumn.REQUEST, (String) arguments[0])
			.insert(RequestColumn.REQUEST_DATE, event.getMessage().getCreationTime().atZoneSameInstant(ZoneId.of("GMT+0")).format(Utility.getDateTimeFormatter()));
		insert.execute();
		
		event.getChannel().sendMessage(event.getAuthor().getAsMention() + " thank you for showing your support by reporting problems!").queue();
	}
}