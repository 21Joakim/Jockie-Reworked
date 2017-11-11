package com.jockie.bot.command.information;

import java.util.List;
import java.util.stream.Collectors;

import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.database.Database;
import com.jockie.bot.database.column.UserInformationColumn;
import com.jockie.bot.main.JockieBot;
import com.jockie.sql.action.ActionGet;
import com.jockie.sql.base.Function;
import com.jockie.sql.base.Result;
import com.jockie.sql.base.Row;
import com.jockie.sql.base.Where.Operator;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandUpcomingBirthdays extends CommandImpl {

	public CommandUpcomingBirthdays() {
		super("upcoming birthdays");
		super.setPMTriggerable(false);
		super.setBeta(true);
		super.setCommandDescription("Get the upcoming birthdays for the next 30 days");
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		ActionGet get = JockieBot.getDatabase().get(Database.USER_INFORMATION);
		get.getSelect().select(UserInformationColumn.USER_ID);
		get.getSelect().select(UserInformationColumn.BIRTHDAY);
		
		Function birthday_function = Function.of("STR_TO_DATE(%s,%s)", UserInformationColumn.BIRTHDAY.getValue(), "'%d/%m'");
		
		get.getWhere().where(birthday_function, Operator.GREATER_THAN_OR_EQUAL, Function.of("STR_TO_DATE(DATE_FORMAT(%s,%s),%s)", "NOW()", "'%d/%m'", "'%d/%m'"));
		get.getWhere().and();
		get.getWhere().where(birthday_function, Operator.LESS_THAN_OR_EQUAL, Function.of("STR_TO_DATE(DATE_FORMAT(%s,%s),%s)", "DATE_ADD(NOW(), INTERVAL 30 DAY)", "'%d/%m'", "'%d/%m'"));
		
		String text = "";
		
		Result result = get.execute();
		if(result.next()) {
			List<String> members_id = event.getGuild().getMembers().stream().map(Member::getUser).map(User::getId).collect(Collectors.toList());
			for(Row row : result.getRows()) {
				if(members_id.contains((String) row.getColumn(UserInformationColumn.USER_ID.getValue()))) {
					text = text + event.getGuild().getMemberById((String) row.getColumn(UserInformationColumn.USER_ID.getValue())).getEffectiveName() + " - " + (String) row.getColumn(UserInformationColumn.BIRTHDAY.getValue()) + "\n";
				}
			}
		}
		
		if(text.equals(""))
			event.getChannel().sendMessage("There are no upcoming birthdays for the users on this server for the next 30 days!").queue();
		else
			event.getChannel().sendMessage("Upcoming birthdays for the users on this server for the next 30 days :\n\n" + text).queue();
	}
}