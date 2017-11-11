package com.jockie.bot.command.information;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.command.core.impl.Arguments.ArgumentUser;
import com.jockie.bot.database.Database;
import com.jockie.bot.database.column.UserInformationColumn;
import com.jockie.bot.main.JockieBot;
import com.jockie.bot.utility.Utility;
import com.jockie.sql.action.ActionGet;
import com.jockie.sql.base.Result;
import com.jockie.sql.base.Where.Operator;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandBirthday extends CommandImpl {

	public CommandBirthday() {
		super("birthday", new ArgumentUser(true).setBotTriggerable(false));
		super.setCommandDescription("Get a user's birthday");
		super.setBeta(true);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		User user = (User) arguments[0];
		
		ActionGet get = JockieBot.getDatabase().get(Database.USER_INFORMATION);
		get.getSelect().select(UserInformationColumn.BIRTHDAY);
		get.getWhere().where(UserInformationColumn.USER_ID, Operator.EQUAL, user.getId());
		
		Result result = get.execute();
		result.next();
		
		if(result.getRows().size() > 0) {
			if(result.getRows().get(0).getColumn(UserInformationColumn.BIRTHDAY.getValue()) != null) {
				Utility.getUser(event.getJDA(), event.getGuild(), user.getId(), user_name -> {
					LocalDate date = LocalDate.parse((String) result.getRows().get(0).getColumn(UserInformationColumn.BIRTHDAY.getValue()), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
					
					char[] month = date.getMonth().name().toLowerCase().toCharArray();
					month[0] -= 32;
					
					event.getChannel().sendMessage(user_name + "'s birthday is on " + date.getDayOfMonth() + " " + new String(month) + " " + date.getYear()).queue();
				});
				return;
			}
		}
		
		Utility.getUser(event.getJDA(), event.getGuild(), user.getId(), user_name -> {
			
			event.getChannel().sendMessage(user_name + " has not set their birthday").queue();
		});
	}
}