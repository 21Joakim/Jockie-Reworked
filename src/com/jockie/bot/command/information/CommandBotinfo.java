package com.jockie.bot.command.information;

import java.awt.Color;

import com.jockie.bot.Statistics;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.main.CombindJDA;
import com.jockie.bot.main.JockieBot;
import com.jockie.bot.utility.Utility;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandBotinfo extends CommandImpl {

	public CommandBotinfo() {
		super("botinfo");
		super.setCommandDescription("Jockie information & data");
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		EmbedBuilder embed_builder = new EmbedBuilder();

		embed_builder.setColor(Color.CYAN);

		embed_builder.setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl());

		embed_builder.addField("Bot Name", event.getJDA().getSelfUser().getName(), true);
		embed_builder.addField("Bot Id", event.getJDA().getSelfUser().getId(), true);
		embed_builder.addField("Bot Owner", JockieBot.BOT_OWNER_USER.getName() + "#" + JockieBot.BOT_OWNER_USER.getDiscriminator(), true);
		embed_builder.addField("Owner Id", JockieBot.BOT_OWNER_USER.getId(), true);
		embed_builder.addField("Library", "JDA " + JDAInfo.VERSION, true);
		embed_builder.addField("Bot Version", JockieBot.VERSION, true);
		embed_builder.addField("Servers", CombindJDA.getGuilds().size() + "", true);
		embed_builder.addField("Users", CombindJDA.getUsers().size() + " whereof " + CombindJDA.getUsers().stream().filter(u -> u.isBot()).count() + " bot(s)", true);
		embed_builder.addField("Text Channels", CombindJDA.getTextChannels().size() + "", true);
		embed_builder.addField("Voice Channels", CombindJDA.getVoiceChannels().size() + "", true);
		embed_builder.addField("Uptime", Utility.formattedUptime(), true);
		embed_builder.addField("Successful Commands", Statistics.getTotalSuccessfulCommands() + "", true);

		embed_builder.addField("Invitation Link", "**[Click here to invite the bot]"
				+ "(https://discordapp.com/oauth2/authorize?client_id=" + event.getJDA().getSelfUser().getId() + "&scope=bot&permissions=805760254)**", false);

		event.getChannel().sendMessage(embed_builder.build()).queue();	
	}
}