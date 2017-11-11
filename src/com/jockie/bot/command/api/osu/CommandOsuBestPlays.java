package com.jockie.bot.command.api.osu;

import java.awt.Color;
import java.util.ArrayList;

import com.jockie.bot.APIs.osu.Osu;
import com.jockie.bot.APIs.osu.OsuPlay;
import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.command.core.impl.CommandListener;
import com.jockie.bot.command.core.non_command.ExecutableNonCommandTriggerPoint;
import com.jockie.bot.command.core.non_command.PagedResult;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandOsuBestPlays extends CommandImpl {
	
	public CommandOsuBestPlays() {
		super("osu best plays", CommandOsuUser.ARGUMENT_OSU_MODE, new ArgumentString("Osu name/id"));
		super.setCommandDescription("Get recent plays from an osu user");
		super.setBeta(true);
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
		
		ArrayList<OsuPlay> plays = Osu.getBestPlays(user, mode);
		if(plays.size() == 1) {
			EmbedBuilder embed_builder = plays.get(0).getInformationEmbed();
			
			embed_builder.setColor(Color.CYAN);
			
			event.getChannel().sendMessage(embed_builder.build()).queue();
		}else if(plays.size() > 1) {
			PagedResult<OsuPlay> paged_result_osu_plays = new PagedResult<OsuPlay>(plays, OsuPlay::getBeatmapId);
			
			CommandListener.doPagedResult(event, this, paged_result_osu_plays, new ExecutableNonCommandTriggerPoint(this.getCommand(), paged_result_osu_plays) {
				public boolean execute(MessageReceivedEvent event) {
					int number = -1;
					
					try {
						number = Integer.parseInt(event.getMessage().getRawContent());
					}catch(Exception e) {}
					
					if(number != -1) {
						PagedResult<?> osu_beatmaps = (PagedResult<?>) this.getObject();
						
						if(number > 0 && number <= osu_beatmaps.getCurrentPageEntries().size()) {
							EmbedBuilder embed_builder = ((OsuPlay) osu_beatmaps.getCurrentPageEntries().get(number - 1)).getInformationEmbed();
							
							embed_builder.setColor(Color.cyan);
							
							event.getChannel().sendMessage(embed_builder.build()).queue();
							
							return true;
						}
					}
					return false;
				}
			});
		}else{
			event.getChannel().sendMessage("No best plays by " + user).queue();
		}
	}
}