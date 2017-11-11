package com.jockie.bot.command.information;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

import com.jockie.bot.APIs.APIHelper;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.utility.Utility;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Region;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandServerinfo extends CommandImpl {
	
	public CommandServerinfo() {
		super("serverinfo");
		super.setPMTriggerable(false);
		super.setCommandDescription("Server information & data");
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		String region_str = event.getGuild().getRegion().getName();

		if(region_str.equals(Region.AMSTERDAM.getName()) || region_str.equals(Region.VIP_AMSTERDAM.getName())) {
			region_str += " :flag_nl:";
		}else if(region_str.equals(Region.BRAZIL.getName()) || region_str.equals(Region.VIP_BRAZIL.getName())) {
			region_str += " :flag_br:";
		}else if(region_str.equals(Region.LONDON.getName()) || region_str.equals(Region.VIP_LONDON.getName())) {
			region_str += " :flag_gb:";
		}else if(region_str.equals(Region.FRANKFURT.getName()) || region_str.equals(Region.VIP_FRANKFURT.getName())) {
			region_str += " :flag_de:";
		}else if(region_str.equals(Region.SINGAPORE.getName()) || region_str.equals(Region.VIP_SINGAPORE.getName())) {
			region_str += " :flag_sg:";
		}else if(region_str.equals(Region.SYDNEY.getName()) || region_str.equals(Region.VIP_SYDNEY.getName())) {
			region_str += " :flag_au:";
		}else if(region_str.equals(Region.EU_CENTRAL.getName()) || region_str.equals(Region.EU_WEST.getName()) || region_str.equals(Region.VIP_EU_CENTRAL.getName()) || region_str.equals(Region.VIP_EU_WEST.getName())) {
			region_str += " :flag_eu:";
		}else if(region_str.equals(Region.US_CENTRAL.getName()) || region_str.equals(Region.US_EAST.getName()) || region_str.equals(Region.US_SOUTH.getName()) || region_str.equals(Region.US_WEST.getName()) || 
				region_str.equals(Region.VIP_US_CENTRAL.getName()) || region_str.equals(Region.VIP_US_EAST.getName()) || region_str.equals(Region.VIP_US_SOUTH.getName()) || region_str.equals(Region.VIP_US_WEST.getName())) {
			region_str += " :flag_us:";
		}

		int bots = 0;
		for(int i = 0; i < event.getGuild().getMembers().size(); i++)
			if(event.getGuild().getMembers().get(i).getUser().isBot())
				bots++;
		
		EmbedBuilder embed_builder = new EmbedBuilder();

		embed_builder.setTitle(event.getGuild().getName(), null);
		embed_builder.setThumbnail(event.getGuild().getIconUrl());

		embed_builder.addField("Server Name", event.getGuild().getName(), true);
		embed_builder.addField("Server Id", event.getGuild().getId(), true);

		embed_builder.addField("Server Owner", event.getGuild().getOwner().getUser().getName() + "#" + event.getGuild().getOwner().getUser().getDiscriminator(), true);
		embed_builder.addField("Owner Id", event.getGuild().getOwner().getUser().getId(), true);
		embed_builder.addField("Server Region", region_str, true);

		embed_builder.addField("Members ",  event.getGuild().getMembers().size() + " Whereof " + bots + " bot(s)", true);

		embed_builder.addField("Text Channels ",  event.getGuild().getTextChannels().size() + "", true);
		embed_builder.addField("Voice Channels", event.getGuild().getVoiceChannels().size() + "", true);

		embed_builder.addField("Verification Level", event.getGuild().getVerificationLevel().toString().charAt(0) + event.getGuild().getVerificationLevel().toString().toLowerCase().substring(1), true);
		embed_builder.addField("AFK Channel", event.getGuild().getAfkChannel() != null ? event.getGuild().getAfkChannel().getName() : "None", true);

		embed_builder.addField("Emotes", event.getGuild().getEmotes().size() + "", true);
		embed_builder.addField("Roles", event.getGuild().getRoles().size() + "", true); 

		embed_builder.addField("Creation Time", event.getGuild().getCreationTime().atZoneSameInstant(ZoneId.of("GMT+0")).format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss")) + " (GMT + 0)", true);
		
		try {
			embed_builder.setColor(Utility.getMostCommonColour(ImageIO.read(APIHelper.getStream(event.getGuild().getIconUrl()))));
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		event.getChannel().sendMessage(embed_builder.build()).queue();
	}
}