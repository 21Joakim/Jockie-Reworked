package com.jockie.bot.command.core.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Predicate;

import com.jockie.bot.command.core.Argument;
import com.jockie.bot.command.core.Command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class DummyCommand implements Command {
	
	private Command command;
	
	private Argument<?>[] arguments;
	
	private HashMap<Integer, Argument<?>> indexes = new HashMap<Integer, Argument<?>>();
	
	public DummyCommand(Command command, Argument<?>... arguments) {
		this.command = command;
		
		this.arguments = Arrays.asList(command.getArguments()).stream().filter(new Predicate<Argument<?>>() {
			public boolean test(Argument<?> argument) {
				if(!argument.hasDefault())
					return true;
				
				for(int i = 0; i < command.getArguments().length; i++) {
					if(command.getArguments()[i].equals(argument)) {
						for(int j = 0; j < arguments.length; j++) {
							if(arguments[j].equals(argument)) {
								indexes.put(i, argument);
								return false;
							}
						}
					}
				}
				
				return true;
			}
		}).toArray(Argument<?>[]::new);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		Object[] args = new Object[command.getArguments().length];
		
		for(int i = 0, offset = 0; i < args.length; i++) {
			if(indexes.get(i) != null) {
				args[i] = indexes.get(i).getDefault(event);
			}else{
				args[i] = arguments[offset];
				offset = offset + 1;
			}
		}
		
		this.command.execute(event, prefix, args);
	}
	
	public Command getInheritedCommand() {
		return command;
	}
	
	public String getCommand() {
		return this.getInheritedCommand().getCommand();
	}
	
	public String getUsage() {
		return this.getInheritedCommand().getUsage();
	}
	
	public String getDescription() {
		return this.getInheritedCommand().getDescription();
	}
	
	public Argument<?>[] getArguments() {
		return this.arguments;
	}
	
	public Permission[] getBotDiscordPermissionsNeeded() {
		return this.getInheritedCommand().getBotDiscordPermissionsNeeded();
	}
	
	public Permission[] getAuthorDiscordPermissionsNeeded() {
		return this.getInheritedCommand().getAuthorDiscordPermissionsNeeded();
	}
	
	@Deprecated
	public boolean hasEndlessArgument() {
		return this.getInheritedCommand().hasEndlessArgument();
	}
	
	public boolean isDeprecated() {
		return this.getInheritedCommand().isDeprecated();
	}
	
	public boolean isHidden() {
		return this.getInheritedCommand().isHidden();
	}
	
	public boolean isBeta() {
		return this.getInheritedCommand().isBeta();
	}
	
	public boolean isDeveloperCommand() {
		return this.getInheritedCommand().isDeveloperCommand();
	}
	
	public boolean isBotTriggerable() {
		return this.getInheritedCommand().isBotTriggerable();
	}
	
	public boolean isCaseSensitive() {
		return this.getInheritedCommand().isCaseSensitive();
	}
	
	public boolean isPMTriggerable() {
		return this.getInheritedCommand().isPMTriggerable();
	}
	
	public boolean isGuildTriggerable() {
		return this.getInheritedCommand().isGuildTriggerable();
	}
	
	public String toString() {
		int spaces = (20 - this.getCommand().length());
		String toString = this.getCommand();
		
		for(int i = 0; i < spaces; i++)
			toString = toString + " ";
		
		return toString = toString + Command.getDisplayableArguments(this.getArguments());
	}
}