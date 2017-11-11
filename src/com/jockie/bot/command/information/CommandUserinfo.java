package com.jockie.bot.command.information;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.jockie.bot.command.core.impl.Arguments.ArgumentUser;
import com.jockie.bot.command.core.impl.CommandImpl;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandUserinfo extends CommandImpl {

	public CommandUserinfo() {
		super("userinfo", new ArgumentUser(true));
		super.setCommandDescription("User information");
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		User user = (User) arguments[0];
		
		boolean onServer = false;
		
		if(event.getChannelType().isGuild()) {
			if(event.getGuild().getMemberById(user.getId()) != null) {
				onServer = true;
			}
		}
		
		EmbedBuilder embedBuilder = new EmbedBuilder();

		embedBuilder.setThumbnail(user.getEffectiveAvatarUrl());

		embedBuilder.addField("User", user.getName() + "#" + user.getDiscriminator(), true);
		embedBuilder.addField("User Id", user.getId(), true);
		embedBuilder.addField("Bot", (user.isBot()) ? "Yes" : "No", true);
		if(onServer) {
			embedBuilder.setColor(event.getGuild().getMember(user).getColor());
			
			embedBuilder.addField("In this server", "Yes", true);
			embedBuilder.addField("Status", event.getGuild().getMember(user).getOnlineStatus().getKey(), true);
			embedBuilder.addField("Roles", event.getGuild().getMember(user).getRoles().size() + "", true);
			embedBuilder.addField("Name on Server", event.getGuild().getMember(user).getEffectiveName(), false);
			
			if(event.getGuild().getMember(user).getGame() != null)
				embedBuilder.addField("Playing", event.getGuild().getMember(user).getGame().getName(), false);
			
			embedBuilder.addField("Joined Server (Last time they joined this server)", event.getGuild().getMember(user).getJoinDate().atZoneSameInstant(ZoneId.of("GMT+0")).format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss")) + " (GMT + 0)", false);
		}

		embedBuilder.addField("Account Created", user.getCreationTime().atZoneSameInstant(ZoneId.of("GMT+0")).format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss")) + " (GMT + 0)", false);

		embedBuilder.setFooter("Requested by " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + " at " + OffsetDateTime.now(ZoneId.of("GMT+0")).format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss")) + " (GMT + 0)", event.getAuthor().getEffectiveAvatarUrl());

		event.getChannel().sendMessage(embedBuilder.build()).queue();
	}
}