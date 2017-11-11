package com.jockie.bot.command.api.osu;

import java.awt.Color;

import com.jockie.bot.APIs.osu.Osu;
import com.jockie.bot.APIs.osu.OsuUser;
import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue.ArgumentEntry;
import com.jockie.bot.command.core.impl.CommandImpl;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandOsuUser extends CommandImpl {
	
	public static final ArgumentTypeValue ARGUMENT_OSU_MODE = new ArgumentTypeValue("Osu mode",
		new ArgumentEntry("Standard", "STANDARD", "STANDARD", "STD"),
		new ArgumentEntry("Taiko", "TAIKO", "TAIKO"),
		new ArgumentEntry("Catch or CtB (Catch the Beat)", "CTB", "CTB", "CATCH"),
		new ArgumentEntry("Mania", "MANIA", "MANIA")
	);
	
	public CommandOsuUser() {
		super("osu", CommandOsuUser.ARGUMENT_OSU_MODE, new ArgumentString("Osu name/id"));
		super.setCommandDescription("Get osu user information and data");
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		String user = (String) arguments[1];
		
		int mode = -1;
		
		if(arguments[0].equals("STANDARD")) {
			mode = 0;
		}else if(arguments[0].equals("TAIKO")) {
			mode = 1;
		}else if(arguments[0].equals("CTB")) {
			mode = 2;
		}else if(arguments[0].equals("MANIA")) {
			mode = 3;
		}
		
		OsuUser osu_user = Osu.getUser(user, mode);
		if(osu_user != null) {
			EmbedBuilder embed_builder = osu_user.getInformationEmbed();
			
			embed_builder.setColor(Color.CYAN);
			
			event.getChannel().sendMessage(embed_builder.build()).queue();
		}else{
			event.getChannel().sendMessage("Could not find anyone with the name " + user).queue();
		}
	}
}