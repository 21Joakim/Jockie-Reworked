package com.jockie.bot.command.developer;

import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue.ArgumentEntry;
import com.jockie.bot.command.core.impl.Arguments.ArgumentUser;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.command.core.impl.CommandListener;
import com.jockie.bot.command.core.impl.CommandManager.Authority;
import com.jockie.bot.database.Database;
import com.jockie.bot.database.column.UserInformationColumn;
import com.jockie.bot.main.JockieBot;
import com.jockie.sql.action.ActionGet;
import com.jockie.sql.action.ActionInsert;
import com.jockie.sql.action.ActionSet;
import com.jockie.sql.base.Where.Operator;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandSetAuthority extends CommandImpl {

	public CommandSetAuthority() {
		super("set authority", new ArgumentUser(), 
			new ArgumentTypeValue("Authority type",
				new ArgumentEntry("No permissions", "NONE", "NONE"),
				new ArgumentEntry("Beta command permissions", "BETA", "BETA"),
				new ArgumentEntry("Developer permissions", "DEVELOPER", "DEVELOPER")
			)
		);
		super.setCommandDescription("Add authority to users");
		super.setDeveloperCommand(true);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		User user = (User) arguments[0];
		Authority authority = Authority.valueOf((String) arguments[1]);
		
		if(!CommandListener.getCommandManager().getAuthority(user.getId()).equals(authority)) {
			ActionGet get = JockieBot.getDatabase().get(Database.USER_INFORMATION);
			get.getSelect().select(UserInformationColumn.USER_ID);
			get.getWhere().where(UserInformationColumn.USER_ID, Operator.EQUAL, user.getId());
			
			if(get.execute().next()) {
				ActionSet set = JockieBot.getDatabase().set(Database.USER_INFORMATION);
				set.getSet().set(UserInformationColumn.AUTHORITY, authority.toString());
				set.getWhere().where(UserInformationColumn.USER_ID, Operator.EQUAL, user.getId());
				set.execute();
			}else{
				ActionInsert insert = JockieBot.getDatabase().insert(Database.USER_INFORMATION);
				insert.getInsert().insert(UserInformationColumn.USER_ID, user.getId());
				insert.getInsert().insert(UserInformationColumn.AUTHORITY, authority.toString());
				insert.execute();
			}
			
			CommandListener.getCommandManager().addAuthorithy(user.getId(), authority);
		}
		
		event.getChannel().sendMessage("Authority of " + user.getAsMention() + " has been set to " + authority).queue();
	}
}