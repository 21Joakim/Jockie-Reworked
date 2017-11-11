package com.jockie.bot.command.api.intel;

import java.util.ArrayList;

import com.jockie.bot.APIs.intel.APIIntel;
import com.jockie.bot.APIs.intel.Processor;
import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.command.core.impl.CommandListener;
import com.jockie.bot.command.core.non_command.ExecutableNonCommandTriggerPoint;
import com.jockie.bot.command.core.non_command.NonCommandTriggerPoint;
import com.jockie.bot.command.core.non_command.PagedResult;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandCompareProcessors extends CommandImpl {

	public CommandCompareProcessors() {
		super("compare processors", new ArgumentString("The name of the Intel processors seperated by a |"));
		super.setCommandDescription("Compare Intel processors");
	}
	
	/*
	 * Works reasonably well but the code is chaotic and hard to maintain.
	 */
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		String processor_compare = (String) arguments[0];
		String[] processor_names = processor_compare.split("\\|");
		
		if(processor_names.length > 1) {
			if(processor_names.length <= 6) {
				ArrayList<ArrayList<Processor>> processors = new ArrayList<ArrayList<Processor>>();
				
				for(int i = 0; i < processor_names.length; i++) {
					if(processor_names[i].startsWith("|"))
						processor_names[i] = processor_names[i].substring(1);
					processors.add(APIIntel.getProcessorsByNameCache(processor_names[i]));
				}
				
				if(processors.stream().filter(sub_processors -> sub_processors.size() > 0).count() > 1)
					this.next(event, processors, new ArrayList<Processor>(), null, 0, processor_names.length);
				else event.getChannel().sendMessage("Not enough possible processors, a minimum of 2 processors is requried (Some are invalid)").queue();
			}else{
				event.getChannel().sendMessage("A maximum of 6 processors is allowed").queue();
			}
		}else{
			event.getChannel().sendMessage("A minimum of 2 processors is required, seperate each processor name with a |").queue();
		}
	}
	
	private void next(MessageReceivedEvent event, ArrayList<ArrayList<Processor>> all_processors, ArrayList<Processor> final_processors, NonCommandTriggerPoint previous_trigger_point, int current_index, int final_index) {
		ArrayList<Processor> processors = all_processors.get(current_index);
		
		if(processors.size() > 1) {
			PagedResult<Processor> paged_result_processors = new PagedResult<Processor>(all_processors.get(current_index), Processor::getProductName);
			ExecutableNonCommandTriggerPoint trigger_point = null;
			
			trigger_point = new ExecutableNonCommandTriggerPoint(this.getCommand(), paged_result_processors) {
				public boolean execute(MessageReceivedEvent event) {
					int number = -1;
					
					try {
						number = Integer.parseInt(event.getMessage().getRawContent());
					}catch(Exception e) {}
					
					if(number != -1) {
						PagedResult<?> processors = (PagedResult<?>) this.getObject();
						if(number > 0 && number <= processors.getCurrentPageEntries().size()) {
							event.getChannel().deleteMessageById(event.getMessageId()).queue();
							final_processors.add((Processor) processors.getCurrentPageEntries().get(number - 1));
							if(current_index == final_index - 1) {
								if(this.getInitalMessageId() != null)
									event.getChannel().deleteMessageById(this.getInitalMessageId()).queue();
								
								if(final_processors.size() > 1) {
									event.getChannel().sendMessage(builder(final_processors).build()).queue();
								}else{
									event.getChannel().sendMessage("Not enough processors, a minimum of 2 processors is required").queue();
								}
								return true;
							}else{
								CommandListener.removeNonCommandTriggerPoint(event.getAuthor().getId() + "," + (event.getChannelType().equals(ChannelType.TEXT) ? event.getGuild().getId() + "," : "") + event.getChannel().getId());
								next(event, all_processors, final_processors, this, current_index + 1, final_index);
								return false;
							}
						}
					}
					return false;
				}
			};
			
			EmbedBuilder embed_builder = paged_result_processors.getPageAsEmbed();
			embed_builder.setDescription("Stage **" + (current_index + 1) + "**/**" + final_index + "**\n" + embed_builder.getDescriptionBuilder().toString());
			
			if(previous_trigger_point != null) {
				trigger_point.setMessageId(previous_trigger_point.getInitalMessageId());
				event.getChannel().editMessageById(trigger_point.getInitalMessageId(), embed_builder.build()).complete();
			}else{
				String message_id = event.getChannel().sendMessage(embed_builder.build()).complete().getId();
				trigger_point.setMessageId(message_id);
			}
			
			String location = event.getAuthor().getId() + "," + (event.getChannelType().equals(ChannelType.TEXT) ? event.getGuild().getId() + "," : "") + event.getChannel().getId();
			CommandListener.addNonCommandTriggerPoint(location, trigger_point);
			
			return;
		}else if(processors.size() == 1) {
			final_processors.add(processors.get(0));
		}else{
			event.getChannel().sendMessage("No processor by the name of the " + (current_index + 1) + " index! Continuing...").queue();
		}
		
		if(current_index == final_index - 1) {
			if(previous_trigger_point != null)
				event.getChannel().deleteMessageById(previous_trigger_point.getInitalMessageId()).queue();
			
			if(final_processors.size() > 1) {
				event.getChannel().sendMessage(builder(final_processors).build()).queue();
			}else{
				event.getChannel().sendMessage("Not enough processors, a minimum of 2 processors is required").queue();
			}
			return;
		}
		
		CommandListener.removeNonCommandTriggerPoint(event.getAuthor().getId() + "," + (event.getChannelType().equals(ChannelType.TEXT) ? event.getGuild().getId() + "," : "") + event.getChannel().getId());
		next(event, all_processors, final_processors, previous_trigger_point, current_index + 1, final_index);
	}
	
	private EmbedBuilder builder(ArrayList<Processor> final_processors) {
		EmbedBuilder embed_builder = new EmbedBuilder();
		
		String processors_string = "";
		
		for(int i = 0; i < final_processors.size(); i++)
			processors_string = processors_string + "\n**#" + (i + 1) + "** " + final_processors.get(i).getProductName();
		
		embed_builder.addField("Processors", processors_string, false);
		embed_builder.addBlankField(false);
		
		for(int i = 0; i < final_processors.size(); i++) {
			Processor processor = final_processors.get(i);
			embed_builder.addField(processor.getPerformanceField("**#" + (i + 1) + "**", true));
			if((i + 1) % 2 == 0) {
				embed_builder.addBlankField(false);
			}
		}
		
		return embed_builder;
	}
}