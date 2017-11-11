package com.jockie.bot.command.utility;

import java.util.List;

import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.Arguments.ArgumentUser;
import com.jockie.bot.command.core.impl.CommandImpl;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandHasRole extends CommandImpl {

	public CommandHasRole() {
		super("has role", new ArgumentUser("user", true), new ArgumentString("role name"));
		super.setCommandDescription("Check if a user has a role (Can also be used to get the id of a role)");
		super.setDeprecated(true);
		super.setBeta(true);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		List<Role> roles = event.getGuild().getRolesByName((String) arguments[1], true);
		
		if(roles.size() > 0) {
			User user = (User) arguments[0];
			
			if(event.getGuild().isMember(user)) {
				Member member = event.getGuild().getMember(user);
				
				EmbedBuilder embed_builder = new EmbedBuilder();
				
				for(int i = 0; i < roles.size(); i++) {
					if(member.getRoles().contains(roles.get(i))) {
						embed_builder.appendDescription("Does have " + roles.get(i).getName() + " (" + roles.get(i).getId() + ")\n");
					}else embed_builder.appendDescription("Does not have " + roles.get(i).getName() + " (" + roles.get(i).getId() + ")\n");
				}
				
				event.getChannel().sendMessage(embed_builder.build()).queue();
			}else{
				event.getChannel().sendMessage("User is not a member of this server").queue();
			}
		}else{
			event.getChannel().sendMessage("There are no roles by that name").queue();
		}
	}
}