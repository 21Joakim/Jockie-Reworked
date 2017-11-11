package com.jockie.bot.command.intro;

import java.awt.Color;
import java.util.List;
import java.util.function.Function;

import com.jockie.bot.command.core.Argument;
import com.jockie.bot.command.core.Command;
import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue.ArgumentEntry;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.command.core.impl.CommandListener;
import com.jockie.bot.command.core.impl.CommandManager;
import com.jockie.bot.command.core.non_command.NonCommandTriggerPoint;
import com.jockie.bot.command.core.non_command.PagedResult;
import com.jockie.bot.utility.Utility;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandHelp extends CommandImpl {

	public CommandHelp() {
		super("help", new ArgumentString("Name of the command", ""));
		super.setCommandDescription("Command information");
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		String command_str = (String) arguments[0];
		
		CommandManager command_manager = CommandListener.getCommandManager();
		
		EmbedBuilder embed_builder = new EmbedBuilder();
		embed_builder.setColor(Color.CYAN);
		
		if(!command_str.equals("") && !command_str.equals("how to use")) {
			List<Command> possible_commands = command_manager.getNonDummyCommandsAuthorized(command_str, event.getChannel(), event.getAuthor());
			if(possible_commands.size() > 0) {
				for(int i = 0; i < possible_commands.size(); i++) {
					String help = "";
					
					if(possible_commands.get(i).isDeprecated())
						help += "__**This command is deprecated**__\n\n";
					
					help += "**Description** :\n   " + possible_commands.get(i).getDescription() + "\n";
					
					if(possible_commands.get(i).getArguments().length > 0) {
						help += "\n**Arguments** : \n";
						for(int i2 = 0; i2 < possible_commands.get(i).getArguments().length; i2++) {
							Argument<?> argument = possible_commands.get(i).getArguments()[i2];
							help += "   **#" + (i2 + 1) + "** " + argument.getValueInformation() + "\n";
							if(argument instanceof ArgumentTypeValue) {
								help += "         **Possible Values** :\n";
								for(ArgumentEntry entry : ((ArgumentTypeValue) argument).getEntries()) {
									help += "            **" + Utility.toString(entry.getTriggers(), "** or**") + "**\n                " + entry.getDescription() + "\n";
								}
							}
							
							help += "          **Required?** ";
							if(argument.hasDefault())
								help += "No.\n               **Default Value** : " + argument.getDisplayableDefault(event);
							else help += "Yes.";
							help += "\n\n";
						}
					}
					embed_builder.addField(possible_commands.get(i).getCommand(), help, false);
				}
				event.getChannel().sendMessage(embed_builder.build()).queue();
			}else{
				event.getChannel().sendMessage(embed_builder.setDescription("No command found").build()).queue();
			}
		}else if(command_str.equals("how to use")) {
			String help = 
				"This bot is developed to be as convenient as possible and therefore there are so called **default values** for some of the arguments in commands.\r\n" + 
				"\r\n" + 
				"For instance, let's make up a command." + 
				"\r\n\r\n" + 
				"**```" + 
				"!car\r\n" + 
				"Argument #1\r\n" + 
				"  Car brand\r\n" + 
				"  \r\n" + 
				"  Possible Values :\r\n" + 
				"    Lamborghini\r\n" + 
				"    Jaguar\r\n\r\n" + 
				"  Default Value : \r\n" + 
				"    Lamborghini\r\n\r\n" + 
				"Argument #2\r\n" + 
				"  Car model\r\n" + 
				"```**" + 
				"\r\n" + 
				"The first argument has to be one of the two **possible values** (Lamborghini, Jaguar)  and the second argument a car model, so the command would look something like this.\r\n" + 
				"\r\n**```!car Lamborghini Miura```**\r\n" + 
				"\r\n" + 
				"But because the command also has a default value you don't **have** to provide a Car Brand and in that case it will automatically set the Car Brand to the **default value** (Being Lamborghini) and the command would then look something like this.\r\n" + 
				"\r\n**```!car Miura```**";
			
			event.getChannel().sendMessage(embed_builder.setDescription(help).build()).queue();
		}else{
			PagedResult<Command> paged_result_commands = new PagedResult<Command>(command_manager.getNonDummyCommandsAuthorized(event.getChannel(), event.getAuthor()), 20, false, "%s", new Function<Command, Object>() {
				public Object apply(Command t) {
					String command_str = "**";
					
					if(t.isCaseSensitive()) {
						command_str = command_str + t.getCommand();
					}else command_str = command_str + t.getCommand().toLowerCase();
					
					command_str = command_str + "**";
					
					if(t.getDescription() != null) {
						command_str = command_str + " - " + t.getDescription();
					}
					
					if(t.isDeprecated()) {
						command_str = "~~" + command_str + "~~";
					}
					
					return command_str;
				}
			}).setDefaultColour(Color.CYAN);
			
			event.getChannel().sendMessage(new MessageBuilder()
			.append("**Prefix** : " + prefix + "\n" + "~~-~~ = Deprecated, meaning it might not work as expect or may get replaced or removed soon.\n\n")
			.setEmbed(paged_result_commands.getPageAsEmbed().build()).build()).queue(message -> {
				String location = event.getAuthor().getId() + "," + (event.getChannelType().equals(ChannelType.TEXT) ? event.getGuild().getId() + "," : "") + event.getChannel().getId();
				CommandListener.addNonCommandTriggerPoint(location, new NonCommandTriggerPoint(message.getId(), this.getCommand(), paged_result_commands));
			});
		}
	}
}