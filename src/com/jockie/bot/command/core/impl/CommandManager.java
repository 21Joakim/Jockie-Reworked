package com.jockie.bot.command.core.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.jockie.bot.command.core.Argument;
import com.jockie.bot.command.core.Command;
import com.jockie.bot.main.JockieBot;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class CommandManager {
	
	public enum Authority {
		DEVELOPER,
		BETA,
		NONE;
	}
	
	/*
	 * Could probably be improved performance wise. (I don't really need to call this after every add of command)
	 */
	private static List<Command> sortCommands(List<Command> commands) {
		return commands.stream().sorted(new Comparator<Command>() {
			public int compare(Command command_1, Command command_2) {
				if(command_1.getArguments().length > command_2.getArguments().length) {
					return -1;
				}else if(command_2.getArguments().length > command_1.getArguments().length) {
					return 1;
				}else if(Arrays.asList(command_1.getArguments()).stream().anyMatch(a -> a.hasToBeLast())) {
					if(!Arrays.asList(command_2.getArguments()).stream().anyMatch(a -> a.hasToBeLast())) {
						return 1;
					}else if(command_1.getCommand().length() > command_2.getCommand().length()) {
						return -1;
					}else return 1;
				}else if(Arrays.asList(command_2.getArguments()).stream().anyMatch(a -> a.hasToBeLast())) {
					if(!Arrays.asList(command_1.getArguments()).stream().anyMatch(a -> a.hasToBeLast())) {
						return -1;
					}else if(command_2.getCommand().length() > command_1.getCommand().length()) {
						return 1;
					}else return -1;
				}
				return 0;
			}
		}).collect(Collectors.toList());
	}
	
	private List<Command> commands = new ArrayList<Command>();
	
	private HashMap<String, Authority> authorities = new HashMap<String, Authority>();
	
	public void addAuthorithy(String user_id, Authority authority) {
		this.authorities.put(user_id, authority);
	}
	
	public void removeAuthorithy(String user_id) {
		if(this.authorities.containsKey(user_id))
			this.authorities.remove(user_id);
	}
	
	public Map<String, Authority> getAuthorities() {
		return Collections.unmodifiableMap(this.authorities);
	}
	
	public Authority getAuthority(String user_id) {
		if(this.authorities.containsKey(user_id))
			return this.authorities.get(user_id);
		else return Authority.NONE;
	}
	
	private static <T extends Command> Predicate<T> getPredicate(String command) {
		return new Predicate<T>() {
			public boolean test(T cmd) {
				if(!cmd.isCaseSensitive()) {
					if(cmd.getCommand().toLowerCase().equals(command.toLowerCase()))
						return true;
				}else if(cmd.getCommand().equals(command))
					return true;
				
				return false;
			}
		};
	}
	
	@SuppressWarnings("deprecation")
	public void addCommand(Command command) {
		if(!this.commands.contains(command)) {
			if(!(command instanceof DummyCommand)) {
				List<Argument<?>> defaultArguments = new ArrayList<Argument<?>>();
				if(command.getArguments().length > 0) {
					for(int i = 0; i < command.getArguments().length; i++) {
						if(command.getArguments()[i].hasDefault())
							defaultArguments.add(command.getArguments()[i]);
						
						if(command.getArguments()[i].hasToBeLast()) {
							if(i != command.getArguments().length - 1) {
								System.out.println("(" + command.getCommand() + ") - Only the last argument may be STRING");
								return;
							}else if(command.hasEndlessArgument()) {
								System.out.println("(" + command.getCommand() + ") - You may not have an endless argument with STRING as the last argument");
								return;
							}
						}
					}
					
					if(defaultArguments.size() > 0) {
						ArrayList<Argument<?>> arguments = new ArrayList<Argument<?>>();
				    	for(int i = 1, max = 1 << defaultArguments.size(); i < max; ++i) {
				    	    for(int j = 0, k = 1; j < defaultArguments.size(); ++j, k <<= 1) {
				    	        if((k & i) != 0) {
				    	        	arguments.add(defaultArguments.get(j));
				    	        }
				    	    }
							addCommand(new DummyCommand(command, arguments.toArray(new Argument<?>[0])));
							arguments.clear();
				    	}
					}
				}else if(command.hasEndlessArgument()) {
					System.err.println("(" + command.getCommand() + ") - Command has no arguments but yet the endless argument is equal to true");
					return;
				}
			}
			
			this.commands.add(command);
			
			this.commands = CommandManager.sortCommands(commands);
		}
	}
	
	public void addCommands(Command... commands) {
		for(Command command : commands)
			addCommand(command);
	}
	
	public void removeCommand(Command command) {
		if(this.commands.contains(command))
			this.commands.remove(command);
	}
	
	public List<Command> getCommands() {
		return Collections.unmodifiableList(commands);
	}
	
	public List<Command> getCommands(String command) {
		return Collections.unmodifiableList(this.getCommands().stream().filter(CommandManager.getPredicate(command)).collect(Collectors.toList()));
	}
	
	public List<Command> getCommandsAuthorized(MessageChannel channel, User user) {
		return Collections.unmodifiableList(commands.stream().filter(new Predicate<Command>() {
			public boolean test(Command c) {
				Authority authority = getAuthority(user.getId());
				
				if(c.isBeta() && !authority.equals(Authority.BETA) && !authority.equals(Authority.DEVELOPER)) {
					if(channel.getType().equals(ChannelType.TEXT)) {
						TextChannel text_channel = (TextChannel) channel;
						if(!JockieBot.getGuildProperties().get(text_channel.getGuild().getId()).isBetaServer())
							return false;
					}else return false;
				}
				
				if(c.isDeveloperCommand() && !authority.equals(Authority.DEVELOPER))
					return false;
				
				if(!c.isBotTriggerable() && user.isBot())
					return false;
				
				if(channel.getType().equals(ChannelType.TEXT)) {
					if(!c.isGuildTriggerable())
						return false;
					
					TextChannel text_channel = (TextChannel) channel;
					
					if(c.getAuthorDiscordPermissionsNeeded().length > 0)
						if(text_channel.getGuild().getMember(user) != null)
							if(!text_channel.getGuild().getMember(user).hasPermission(text_channel, c.getAuthorDiscordPermissionsNeeded()) && !authority.equals(Authority.DEVELOPER))
								return false;
				}else if(!c.isPMTriggerable())
					return false;
				
				return true;
			}
		}).collect(Collectors.toList()));
	}
	
	public List<Command> getCommandsAuthorized(String command, MessageChannel channel, User user) {
		return Collections.unmodifiableList(this.getCommandsAuthorized(channel, user).stream().filter(CommandManager.getPredicate(command)).collect(Collectors.toList()));
	}
	
	public List<DummyCommand> getDummyCommands() {
		return Collections.unmodifiableList(this.getCommands().stream().filter(c -> c instanceof DummyCommand).map(c -> (DummyCommand) c).collect(Collectors.toList()));
	}
	
	public List<DummyCommand> getDummyCommands(String command) {
		return Collections.unmodifiableList(this.getDummyCommands().stream().filter(CommandManager.getPredicate(command)).collect(Collectors.toList()));
	}
	
	public List<DummyCommand> getDummyCommandsInherited(Command command) {
		return Collections.unmodifiableList(this.getDummyCommands().stream().filter(c -> c.getInheritedCommand().equals(command)).collect(Collectors.toList()));
	}
	
	public List<Command> getNonDummyCommands() {
		return Collections.unmodifiableList(this.getCommands().stream().filter(c -> !(c instanceof DummyCommand)).collect(Collectors.toList()));
	}
	
	public List<Command> getNonDummyCommands(String command) {
		return Collections.unmodifiableList(this.getNonDummyCommands().stream().filter(CommandManager.getPredicate(command)).collect(Collectors.toList()));
	}
	
	public List<Command> getNonDummyCommandsAuthorized(MessageChannel channel, User user) {
		return Collections.unmodifiableList(this.getCommandsAuthorized(channel, user).stream().filter(c -> !(c instanceof DummyCommand)).collect(Collectors.toList()));
	}
	
	public List<Command> getNonDummyCommandsAuthorized(String command, MessageChannel channel, User user) {
		return Collections.unmodifiableList(this.getNonDummyCommandsAuthorized(channel, user).stream().filter(CommandManager.getPredicate(command)).collect(Collectors.toList()));
	}
}