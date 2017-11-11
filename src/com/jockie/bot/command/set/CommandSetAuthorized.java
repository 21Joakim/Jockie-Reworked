package com.jockie.bot.command.set;

import java.util.List;
import java.util.stream.Collectors;

import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue.ArgumentEntry;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.command.core.impl.CommandListener;
import com.jockie.bot.command.core.non_command.ExecutableNonCommandTriggerPoint;
import com.jockie.bot.command.core.non_command.PagedResult;
import com.jockie.bot.database.Database;
import com.jockie.bot.database.column.GuildColumn;
import com.jockie.bot.main.JockieBot;
import com.jockie.sql.action.ActionSet;
import com.jockie.sql.base.Where.Operator;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.MessageBuilder.Formatting;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandSetAuthorized extends CommandImpl {

	public CommandSetAuthorized() {
		super("set", new ArgumentTypeValue("Type to set", null, 
			new ArgumentEntry("Prefix, sequence of characters that is written before the command. Character Limit is 5", "PREFIX", "PREFIX"),
			new ArgumentEntry("Should mention user(s) in command (true/false)", "MENTION", "MENTION"),
			new ArgumentEntry("Automatically assigned role when a person joins (Name/Id)", "AUTOROLE", "AUTOROLE")), new ArgumentString("Value to set"));
		super.setAuthorDiscordPermissionsNeeded(Permission.MANAGE_SERVER);
		super.setPMTriggerable(false);
		super.setBotTriggerable(false);
		super.setCommandDescription("Set server settings, requires authority (Permission : Manage Server)");
	}
	
	/*
	 * Auto-role is chaotic.
	 */
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		String entry = (String) arguments[0];
		String value = (String) arguments[1];
		
		if(entry.equals("PREFIX")) {
			if(value.length() <= 5) {
				if(!JockieBot.getGuildProperties().get(event.getGuild().getId()).getPrefix().equals(value)) {
					JockieBot.getGuildProperties().get(event.getGuild().getId()).setPrefix(value);
					
					ActionSet set = JockieBot.getDatabase().set(Database.GUILD);
					set.getWhere().where(GuildColumn.GUILD_ID, Operator.EQUAL, event.getGuild().getId());
					set.getSet().set(GuildColumn.PREFIX, value);
					set.execute();
				}
				
				event.getChannel().sendMessage(new MessageBuilder().append("Prefix has been set to ").append(value, Formatting.BOLD).build()).queue();
			}else{
				event.getChannel().sendMessage("Prefix may not be longer than 5 characters").queue();
			}
		}else if(entry.equals("MENTION")) {
			boolean mention;
			
			if(value.toLowerCase().equals("true"))
				mention = true;
			else if(value.toLowerCase().equals("false"))
				mention = false;
			else {
				event.getChannel().sendMessage("Incorrect value, correct values are **true** and **false**").queue();
				return;
			}
			
			if(JockieBot.getGuildProperties().get(event.getGuild().getId()).shouldMentionUsers() != mention) {
				JockieBot.getGuildProperties().get(event.getGuild().getId()).setMentionUsers(mention);
				
				ActionSet set = JockieBot.getDatabase().set(Database.GUILD);
				set.getWhere().where(GuildColumn.GUILD_ID, Operator.EQUAL, event.getGuild().getId());
				set.getSet().set(GuildColumn.MENTION_USERS, mention + "");
				set.execute();
			}
			
			event.getChannel().sendMessage((!mention) ? "I will no longer mention users in commands (With the exception of the one that triggered the command)" : "I will now mention user in my commands").queue();
		}else if(entry.equals("AUTOROLE")) {
			if(!event.getGuild().getMember(event.getJDA().getSelfUser()).hasPermission(Permission.MANAGE_ROLES)) {
				event.getChannel().sendMessage("Missing " + Permission.MANAGE_ROLES.getName() + " permission").queue();
				return;
			}
				
			Role role = null;
			
			String role_id = value;
			if(role_id.length() >= 15) {
				if(role_id.startsWith("<@&"))
					role_id = role_id.substring(3);
				
				if(role_id.charAt(role_id.length() - 1) == '>')
					role_id = role_id.substring(0, role_id.length() - 1);
				
				try {
					role = event.getGuild().getRoleById(role_id);
				}catch(Exception e) {}
			}
			
			if(role != null) {
				this.setAutoRole(event.getGuild(), role.getId());
				event.getChannel().sendMessage("Auto Role has now been set to " + role.getName() + " (" + role.getId() + ")").queue();
				return;
			}
				
			List<Role> roles = event.getGuild().getRoles().stream().filter(r -> r.getName().toLowerCase().contains(value.toLowerCase()) && !r.isPublicRole()).collect(Collectors.toList());
			
			if(roles.size() > 1) {
				PagedResult<Role> paged_result = new PagedResult<Role>(roles, "%s (%s)", Role::getName, Role::getId);
				
				CommandListener.doPagedResult(event, this, paged_result, new ExecutableNonCommandTriggerPoint("auto_role", paged_result) {
					public boolean execute(MessageReceivedEvent event) {
						int number = -1;
						
						try {
							number = Integer.parseInt(event.getMessage().getRawContent());
						}catch(Exception e) {}
						
						if(number != -1) {
							PagedResult<?> roles = (PagedResult<?>) this.getObject();
							
							if(number > 0 && number <= roles.getEntriesPerPage()) {
								Role role = (Role) roles.getCurrentPageEntries().get(number - 1);
								setAutoRole(event.getGuild(), role.getId());
								event.getChannel().sendMessage("Auto Role has now been set to " + role.getName() + " (" + role.getId() + ")").queue();
								return true;
							}
						}
						return false;
					}
				});
			}else if(roles.size() == 1) {
				setAutoRole(event.getGuild(), roles.get(0).getId());
				event.getChannel().sendMessage("Auto Role has now been set to " + roles.get(0).getName() + " (" + roles.get(0).getId() + ")").queue();
			}else{
				event.getChannel().sendMessage(event.getAuthor().getAsMention() + " could not find any role by that name or id").queue();
			}
		}
	}
	
	private void setAutoRole(Guild guild, String role_id) {
		String current_auto_role = JockieBot.getGuildProperties().get(guild.getId()).getAutoRole();
		if(current_auto_role == null || !current_auto_role.equals(role_id)) {
			JockieBot.getGuildProperties().get(guild.getId()).setAutoRole(role_id);
			
			ActionSet set = JockieBot.getDatabase().set(Database.GUILD);
			set.getWhere().where(GuildColumn.GUILD_ID, Operator.EQUAL, guild.getId());
			set.getSet().set(GuildColumn.AUTO_ROLE, role_id);
			set.execute();
		}
	}
}