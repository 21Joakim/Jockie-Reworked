package com.jockie.bot.command.utility;

import java.util.List;

import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue.ArgumentEntry;
import com.jockie.bot.command.core.impl.Arguments.ArgumentUser;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.command.core.impl.CommandListener;
import com.jockie.bot.command.core.non_command.PagedResult;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandPermissions extends CommandImpl {

	public CommandPermissions() {
		super("permissions", 
			new ArgumentTypeValue("Where to get the permissions from",
				new ArgumentEntry("User's permission on the server", "SERVER", "SERVER"),
				new ArgumentEntry("User's permission in the current channel", "CHANNEL", "CHANNEL")
			), 
			new ArgumentUser(true));
		super.setCommandDescription("Get user permissions");
		super.setPMTriggerable(false);
		super.setBeta(true);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		List<Permission> permissions = null;
		
		if(arguments[0].equals("SERVER"))
			permissions = event.getGuild().getMember((User) arguments[1]).getPermissions();
		else if(arguments[0].equals("CHANNEL"))
			permissions = event.getGuild().getMember((User) arguments[1]).getPermissions(event.getTextChannel());
		
		if(permissions != null)
			CommandListener.doPagedResult(event, this, new PagedResult<Permission>(permissions, 10, false, "%s", Permission::getName));
	}
}