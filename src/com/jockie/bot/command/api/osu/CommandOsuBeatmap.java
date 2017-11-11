package com.jockie.bot.command.api.osu;

import java.awt.Color;
import java.util.ArrayList;

import com.jockie.bot.APIs.osu.Osu;
import com.jockie.bot.APIs.osu.OsuBeatmap;
import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue.ArgumentEntry;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.command.core.impl.CommandListener;
import com.jockie.bot.command.core.non_command.ExecutableNonCommandTriggerPoint;
import com.jockie.bot.command.core.non_command.PagedResult;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandOsuBeatmap extends CommandImpl {
	
	public CommandOsuBeatmap() {
		super("osu beatmap", 
			CommandOsuUser.ARGUMENT_OSU_MODE, 
			new ArgumentTypeValue("Type to search beatmap by", 
				new ArgumentEntry("User's beatmaps", "USER", "USER"),
				new ArgumentEntry("Beatmap Id", "BEATMAPID", "BEATMAPID"),
				new ArgumentEntry("Beatmap Set Id", "BEATMAPSETID", "BEATMAPSETID")
			),
			new ArgumentString("Value to search beatmap by"));
		super.setCommandDescription("Get beatmap information and data");
		super.setBeta(true);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		String value = (String) arguments[2];
		
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
		
		ArrayList<OsuBeatmap> osu_beatmaps = null;
		
		if(arguments[1].equals("USER")) {
			osu_beatmaps = Osu.getBeatmapsByUser(value, mode);
		}else if(arguments[1].equals("BEATMAPID")) {
			osu_beatmaps = Osu.getBeatmapsById(value, mode);
		}else if(arguments[1].equals("BEATMAPSETID")) {
			osu_beatmaps = Osu.getBeatmapsBySetId(value, mode);
		}
		
		if(osu_beatmaps == null)
			return;
		
		if(osu_beatmaps.size() == 1) {
			EmbedBuilder embed_builder = osu_beatmaps.get(0).getInformationEmbed();
			
			embed_builder.setColor(Color.CYAN);
			
			event.getChannel().sendMessage(embed_builder.build()).queue();
		}else if(osu_beatmaps.size() > 1) {
			PagedResult<OsuBeatmap> paged_result_osu_beatmaps = new PagedResult<OsuBeatmap>(osu_beatmaps, OsuBeatmap::getTitle);
			
			CommandListener.doPagedResult(event, this, paged_result_osu_beatmaps, new ExecutableNonCommandTriggerPoint(this.getCommand(), paged_result_osu_beatmaps) {
				public boolean execute(MessageReceivedEvent event) {
					int number = -1;
					
					try {
						number = Integer.parseInt(event.getMessage().getRawContent());
					}catch(Exception e) {}
					
					if(number != -1) {
						PagedResult<?> osu_beatmaps = (PagedResult<?>) this.getObject();
						
						if(number > 0 && number <= osu_beatmaps.getCurrentPageEntries().size()) {
							EmbedBuilder embed_builder = ((OsuBeatmap) osu_beatmaps.getCurrentPageEntries().get(number - 1)).getInformationEmbed();
							
							embed_builder.setColor(Color.cyan);
							
							event.getChannel().sendMessage(embed_builder.build()).queue();
							
							return true;
						}
					}
					return false;
				}
			});
		}else{
			event.getChannel().sendMessage("Could not find any beatmaps from " + value).queue();
		}
	}
}