package com.jockie.bot.command.other;

import java.awt.Color;

import com.jockie.bot.command.core.impl.CommandImpl;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandBanner extends CommandImpl {

	public CommandBanner() {
		super("banner");
		super.setCommandDescription("Official Jockie Banner");
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		event.getChannel().sendMessage(new EmbedBuilder().setImage("https://vgy.me/eBYmRv.png").setColor(Color.CYAN).setFooter("Thanks to Bumbleboss#8014 for creating this banner!", null).build()).queue();
	}
}