package com.jockie.bot.command.intro;

import java.awt.Color;

import com.jockie.bot.command.core.impl.CommandImpl;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandNew extends CommandImpl {
	
	public static final String MESSAGE;
	
	static {
		String message = "";
		
		message = message + "**Prefix** :\n    {p}\n";
		message = message + "**Help** :\n    {p}help" + "\n";
		message = message + "**Help for commands** :\n    {p}help **Command**\n";
		message = message + "**How to use the commands** :\n    {p}help how to use\n";
		message = message + "**Terms of service** :\n    {p}tos\n\n";
		message = message + "If you ever forget the prefix just give Jockie a shout (Mention him with the message **prefix**)\n\n";
		message = message + "By using Jockie and its service you agree to our terms of service therefore we strongly advise you to read them!\n";
		message = message + "Additionally if Jockie stays on the server the person who added it agrees to us storing data about the server when necessary and only then.\n";
		message = message + "(Using **new**, **help** and **tos** is not considered using Jockie and will therefore refrain from the terms of service)";
		message = message + "\n\n**Community Server** :\n    https://discord.gg/HtTprxR";
		
		MESSAGE = message;
	}
	
	public CommandNew() {
		super("new");
		super.setCommandDescription("Simple bot information to get started");
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {		
		event.getChannel().sendMessage(new EmbedBuilder().setDescription(CommandNew.MESSAGE.replace("{p}", prefix)).setColor(Color.CYAN).build()).queue();
	}
}