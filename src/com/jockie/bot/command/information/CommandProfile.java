package com.jockie.bot.command.information;

import com.jockie.bot.command.core.impl.Arguments.ArgumentUser;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.database.Database;
import com.jockie.bot.database.column.UserInformationColumn;
import com.jockie.bot.main.JockieBot;
import com.jockie.bot.utility.Utility;
import com.jockie.sql.action.ActionGet;
import com.jockie.sql.base.GlobalColumn;
import com.jockie.sql.base.Result;
import com.jockie.sql.base.Row;
import com.jockie.sql.base.Where.Operator;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandProfile extends CommandImpl {
	
	public CommandProfile() {
		super("profile", new ArgumentUser(true).setBotTriggerable(false));
		super.setCommandDescription("Get a profile made from user provided information");
		super.setBeta(true);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		User user = (User) arguments[0];
		
		ActionGet get = JockieBot.getDatabase().get(Database.USER_INFORMATION);
		get.getSelect().select(GlobalColumn.ALL);
		get.getWhere().where(UserInformationColumn.USER_ID, Operator.EQUAL, user.getId());
		
		Result result = get.execute();
		
		if(result.next()) {
			Row row = result.getRows().get(0);
			
			EmbedBuilder embed_builder = new EmbedBuilder();
			
			embed_builder.addField("User", user.getName(), true);
			embed_builder.addField("User Id", user.getId(), true);
			embed_builder.addField("Birthday", row.getColumn(UserInformationColumn.BIRTHDAY.getValue()) + "", true);
			embed_builder.addField("Country", row.getColumn(UserInformationColumn.COUNTRY.getValue()) + "", true);
			
			embed_builder.setThumbnail(user.getEffectiveAvatarUrl());
			
			embed_builder.setDescription("**Biography**\n" + row.getColumn(UserInformationColumn.BIO.getValue()) + "");
			
			event.getChannel().sendMessage(embed_builder.build()).queue();
		}else{
			Utility.getUser(event.getJDA(), event.getGuild(), user.getId(), user_name -> {
				event.getChannel().sendMessage(user_name + " has yet to add information about themself").queue();
			});
		}
	}
}