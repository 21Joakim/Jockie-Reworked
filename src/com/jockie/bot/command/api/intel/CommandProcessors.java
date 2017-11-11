package com.jockie.bot.command.api.intel;

import java.awt.Color;
import java.util.ArrayList;

import com.jockie.bot.APIs.intel.APIIntel;
import com.jockie.bot.APIs.intel.IntelProduct;
import com.jockie.bot.APIs.intel.Processor;
import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.command.core.impl.CommandListener;
import com.jockie.bot.command.core.non_command.ExecutableNonCommandTriggerPoint;
import com.jockie.bot.command.core.non_command.PagedResult;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandProcessors extends CommandImpl {

	public CommandProcessors() {
		super("processor", new ArgumentString("The name of an Intel processor"));
		super.setCommandDescription("Get Intel processor information and data");
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		String processor = (String) arguments[0];
		
		ArrayList<Processor> processors = APIIntel.getProcessorsByNameCache(processor);
		if(processors.size() > 1) {
			PagedResult<Processor> paged_result = new PagedResult<Processor>(processors, "[%s](https://ark.intel.com/products/%s)", IntelProduct::getProductName, IntelProduct::getProductId);
			CommandListener.doPagedResult(event, this, paged_result,
				new ExecutableNonCommandTriggerPoint(this.getCommand(), paged_result) {
				public boolean execute(MessageReceivedEvent event) {
					int number = -1;
					
					try {
						number = Integer.parseInt(event.getMessage().getRawContent());
					}catch(Exception e) {}
					
					if(number != -1) {
						PagedResult<?> processors = (PagedResult<?>) this.getObject();
						
						if(number > 0 && number <= processors.getCurrentPageEntries().size()) {
							EmbedBuilder embed_builder = ((Processor) processors.getCurrentPageEntries().get(number - 1)).getInformationEmbed();
							
							embed_builder.setColor(Color.cyan);
							
							event.getChannel().sendMessage(embed_builder.build()).queue();
							
							return true;
						}
					}
					return false;
				}
			});
		}else if(processors.size() == 1) {
			event.getChannel().sendMessage(processors.get(0).getInformationEmbed().build()).queue();
		}else{
			event.getChannel().sendMessage("No product(s) by the name of " + processor + " was found").queue();
		}
	}
}