package com.jockie.bot.command.debug;

import java.time.ZoneId;

import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.database.Database;
import com.jockie.bot.database.column.BetaColumn;
import com.jockie.bot.main.JockieBot;
import com.jockie.bot.utility.Utility;
import com.jockie.sql.action.ActionGet;
import com.jockie.sql.action.ActionInsert;
import com.jockie.sql.base.Result;
import com.jockie.sql.base.Where.Operator;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandApplyBeta extends CommandImpl {

	public CommandApplyBeta() {
		super("apply beta", new ArgumentString("Reason"));
		super.setAuthorDiscordPermissionsNeeded(Permission.ADMINISTRATOR);
		super.setCommandDescription("Apply for beta to get a bunch of cool features before everyone else and help us debug them!");
		super.setBotTriggerable(false);
		super.setPMTriggerable(false);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		ActionGet get = JockieBot.getDatabase().get(Database.BETA);
		get.getSelect().select(BetaColumn.APPLIED_GUILD_ID);
		get.getWhere().where(BetaColumn.APPLIED_GUILD_ID, Operator.EQUAL, event.getGuild().getId());
		Result result = get.execute();
		
		if(result.next()) {
			event.getChannel().sendMessage("This server has already applied for beta features.").queue();
		}else{
			ActionInsert insert = JockieBot.getDatabase().insert(Database.BETA);
			insert.getInsert()
				.insert(BetaColumn.APPLIED_GUILD_ID, event.getGuild().getId())
				.insert(BetaColumn.APPLIED_USER_ID, event.getAuthor().getId())
				.insert(BetaColumn.APPLIED_REASON, (String) arguments[0])
				.insert(BetaColumn.DATE_APPLIED, event.getMessage().getCreationTime().atZoneSameInstant(ZoneId.of("GMT+0")).format(Utility.getDateTimeFormatter()));
			insert.execute();
			
			event.getChannel().sendMessage("This server is now in queue for beta features, we will get back to you as soon as possible!").queue();
		}
	}
}