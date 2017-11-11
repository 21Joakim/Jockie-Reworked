package com.jockie.bot.command.api.osu;

import java.awt.Color;

import com.jockie.bot.APIs.osu.Osu;
import com.jockie.bot.APIs.osu.OsuUser;
import com.jockie.bot.command.core.impl.Arguments.ArgumentUser;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.database.Database;
import com.jockie.bot.database.column.UserInformationColumn;
import com.jockie.bot.main.JockieBot;
import com.jockie.bot.utility.Utility;
import com.jockie.sql.action.ActionGet;
import com.jockie.sql.base.Result;
import com.jockie.sql.base.Where.Operator;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandOsuUserMention extends CommandImpl {
	
	public CommandOsuUserMention() {
		super("osu", CommandOsuUser.ARGUMENT_OSU_MODE, new ArgumentUser(true));
		super.setCommandDescription("Get osu user information and data");
		super.setBeta(true);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		User mentioned_user = (User) arguments[1];
		
		ActionGet get = JockieBot.getDatabase().get(Database.USER_INFORMATION);
		get.getSelect().select(UserInformationColumn.OSU_ID);
		get.getWhere().where(UserInformationColumn.USER_ID, Operator.EQUAL, mentioned_user.getId());
		
		Result result = get.execute();
		result.next();
		
		if(result.getRows().size() > 0) {
			String osu_id = (String) result.getRows().get(0).getColumn(UserInformationColumn.OSU_ID.getValue());
			
			if(osu_id != null) {
				int mode = -1;
				
				if(arguments[0].equals("STANDARD")) {
					mode = 0;
				}else if(arguments[0].equals("TAIKO")) {
					mode = 1;
				}else if(arguments[0].equals("CTB")) {
					mode = 2;
				}else if(arguments[0].equals("MANIA")) {
					mode = 3;
				}
				
				OsuUser osu_user = Osu.getUser(osu_id, mode);
				if(osu_user != null) {
					EmbedBuilder embed_builder = osu_user.getInformationEmbed();
					
					embed_builder.setColor(Color.CYAN);
					
					event.getChannel().sendMessage(embed_builder.build()).queue();
				}else{
					event.getChannel().sendMessage("Could not find anyone with the id " + osu_id).queue();
				}
			}else{
				Utility.getUser(event.getJDA(), event.getGuild(), mentioned_user.getId(), user_name -> {
					event.getChannel().sendMessage(user_name + " has not linked their osu account."/*\nThey can do so by using the " + prefix + "osu set [TEXT] command"*/).queue();
				});
			}
		}else{
			Utility.getUser(event.getJDA(), event.getGuild(), mentioned_user.getId(), user_name -> {
				event.getChannel().sendMessage(user_name + " has not linked their osu account."/*\nThey can do so by using the " + prefix + "osu set [TEXT] command"*/).queue();
			});
		}
	}
}