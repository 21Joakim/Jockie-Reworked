package com.jockie.bot.events;

import com.jockie.bot.Statistics;
import com.jockie.bot.command.core.impl.CommandListener;
import com.jockie.bot.database.Database;
import com.jockie.bot.database.column.GuildColumn;
import com.jockie.bot.main.GuildProperties;
import com.jockie.bot.main.JockieBot;
import com.jockie.sql.action.ActionGet;
import com.jockie.sql.action.ActionInsert;
import com.jockie.sql.action.ActionSet;
import com.jockie.sql.base.Result;
import com.jockie.sql.base.Row;
import com.jockie.sql.base.Where.Operator;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class GeneralEvents extends ListenerAdapter {
	
	public void onGuildJoin(GuildJoinEvent event) {
//		if(!JockieBot.getGuildProperties().containsKey(event.getGuild().getId())) {
//			GuildProperties guild_properties = new GuildProperties();
//			guild_properties.setBetaServer(false);
//			guild_properties.setMentionUsers(true);
//			guild_properties.setPrefix(CommandListener.getDefaultPrefix());
//			
//			JockieBot.addGuildProperties(event.getGuild().getId(), guild_properties);
//		}
		
		if(!JockieBot.getGuildProperties().containsKey(event.getGuild().getId())) {
			ActionGet get = JockieBot.getDatabase().get(Database.GUILD);
			get.getSelect()
				.select(GuildColumn.PREFIX)
				.select(GuildColumn.MENTION_USERS);
			get.getWhere().where(GuildColumn.GUILD_ID, Operator.EQUAL, event.getGuild().getId());
			
			Result result = get.execute();
			result.next();
			
			GuildProperties guild_properties;
			
			if(result.getRows().size() > 0) {
				Row row = result.getRows().get(0);
				
				guild_properties = new GuildProperties();
				
				guild_properties.setPrefix((String) row.getColumn(GuildColumn.PREFIX.getValue()));
				guild_properties.setMentionUsers(Boolean.parseBoolean((String) row.getColumn(GuildColumn.MENTION_USERS.getValue())));
				
				JockieBot.addGuildProperties(event.getGuild().getId(), guild_properties);
			}else{
				guild_properties = new GuildProperties();
				
				guild_properties.setPrefix(CommandListener.getDefaultPrefix());
				guild_properties.setMentionUsers(true);
				
				JockieBot.addGuildProperties(event.getGuild().getId(), guild_properties);
				
				ActionInsert insert = JockieBot.getDatabase().insert(Database.GUILD);
				insert.getInsert()
					.insert(GuildColumn.GUILD_ID, event.getGuild().getId())
					.insert(GuildColumn.PREFIX, guild_properties.getPrefix())
					.insert(GuildColumn.MENTION_USERS, ((Boolean) guild_properties.shouldMentionUsers()).toString());
				insert.execute();
			}
		}
		
		Statistics.addAction(Statistics.NEW_GUILD);
	}
	
	public void onGuildLeave(GuildLeaveEvent event) {
		if(JockieBot.getGuildProperties().containsKey(event.getGuild().getId()))
			JockieBot.removeGuildProperties(event.getGuild().getId());
		
		Statistics.addAction(Statistics.LOST_GUILD);
	}
	
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		String auto_role = JockieBot.getGuildProperties().get(event.getGuild().getId()).getAutoRole();
		if(auto_role != null) {
			if(!event.getGuild().getMember(event.getJDA().getSelfUser()).hasPermission(Permission.MANAGE_ROLES)) {
				event.getGuild().getOwner().getUser().openPrivateChannel().complete().sendMessage("I can not perform auto-role!\nMissing " + Permission.MANAGE_ROLES.getName() + " permission!").queue();
				return;
			}
			
			Role role = event.getGuild().getRoleById(auto_role);
			if(role != null)
				event.getGuild().getController().addRolesToMember(event.getMember(), role).queue();
			else {
				JockieBot.getGuildProperties().get(event.getGuild().getId()).setAutoRole(null);
				
				ActionSet set = JockieBot.getDatabase().set(Database.GUILD);
				set.getWhere().where(GuildColumn.GUILD_ID, Operator.EQUAL, event.getGuild().getId());
				set.getSet().set(GuildColumn.AUTO_ROLE, null);
				set.execute();
			}
		}
		
		/* BETA
		String welcome_message = JockieBot.getGuildProperties().get(event.getGuild().getId()).getWelcomeMessage();
		if(welcome_message != null) {
			String welcome_channel_id = JockieBot.getGuildProperties().get(event.getGuild().getId()).getWelcomeChannel();
			if(welcome_channel_id != null) {
				TextChannel channel = event.getGuild().getTextChannelById(welcome_channel_id);
				if(channel != null) {
					if(!event.getGuild().getMember(event.getJDA().getSelfUser()).hasPermission(channel, Permission.MESSAGE_WRITE)) {
						event.getGuild().getOwner().getUser().openPrivateChannel().complete().sendMessage("I can not send welcome message!\nMissing " + Permission.MESSAGE_WRITE.getName() + " permission in channel " + channel.getName()).queue();
						return;
					}
					
					channel.sendMessage(event.getMember().getAsMention() + " " + welcome_message).queue();
				}else{
					//Remove
				}
			}
		}
		*/
	}
}