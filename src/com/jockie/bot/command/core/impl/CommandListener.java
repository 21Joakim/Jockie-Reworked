package com.jockie.bot.command.core.impl;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;

import com.jockie.bot.Statistics;
import com.jockie.bot.command.core.Argument;
import com.jockie.bot.command.core.Command;
import com.jockie.bot.command.core.impl.Arguments.ArgumentAttachment;
import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.Arguments.VerifiedValue;
import com.jockie.bot.command.core.non_command.ExecutableNonCommandTriggerPoint;
import com.jockie.bot.command.core.non_command.NonCommandTriggerPoint;
import com.jockie.bot.command.core.non_command.PagedResult;
import com.jockie.bot.main.JockieBot;
import com.jockie.bot.main.Shard;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.MessageBuilder.Formatting;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.ErrorResponse;
import net.dv8tion.jda.core.requests.Request;
import net.dv8tion.jda.core.requests.Response;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.Route;

@SuppressWarnings("deprecation")
public class CommandListener extends ListenerAdapter {
	
	/**
	 * The prefix which will be used when no other prefix is apparent
	 */
	private static String default_prefix = "m!" /*"!"*/;
	
	/**
	 * A map which holds all the apparent 'non command trigger points' which are commands that can be triggered without any apparent prefix and only a number or character sequence. 
	 */
	private static HashMap<String, NonCommandTriggerPoint> non_command_trigger_points = new HashMap<String, NonCommandTriggerPoint>();
	
	/**
	 * The manager which holds all the commands and such.
	 */
	private static CommandManager command_manager = new CommandManager();
	
	/**
	 * General permissions needed for a command to function (These permissions are required for a command to execute even if the command does not use them)
	 */
	private static Permission[] base_permissions = {Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_MANAGE, Permission.MESSAGE_ADD_REACTION};
	
	/**
	 * @return the prefix of the guild, if not prefix is apparent it will return {@link CommandListener#default_prefix}
	 */
	private static String getPrefix(Guild guild) {
		if(guild != null) {
			if(JockieBot.getGuildProperties().containsKey(guild.getId()))
				return JockieBot.getGuildProperties().get(guild.getId()).getPrefix();
		}
		return CommandListener.getDefaultPrefix();
	}
	
	/**
	 * @param command_manager {@link CommandListener#command_manager}
	 */
	public static void setCommandManager(CommandManager command_manager) {
		CommandListener.command_manager = command_manager;
	}
	
	/**
	 * @return {@link CommandListener#command_manager}
	 */
	public static CommandManager getCommandManager() {
		return command_manager;
	}
	
	/**
	 * @param location 
	 * @param trigger_point
	 */
	public static void addNonCommandTriggerPoint(String location, NonCommandTriggerPoint trigger_point) {
		CommandListener.non_command_trigger_points.put(location, trigger_point);
	}
	
	public static void removeNonCommandTriggerPoint(String location) {
		CommandListener.non_command_trigger_points.remove(location);
	}
	
	/**
	 * @return {@link CommandListener#default_prefix}
	 */
	public static String getDefaultPrefix() {
		return CommandListener.default_prefix;
	}
	
	/**
	 * @param default_prefix {@link CommandListener#default_prefix}
	 */
	public static void setDefaultPrefix(String default_prefix) {
		CommandListener.default_prefix = default_prefix;
	}
	
	public static void doPagedResult(MessageReceivedEvent event, Command command, PagedResult<?> paged_result) {
		CommandListener.doPagedResult(event, command, paged_result, new NonCommandTriggerPoint(command.getCommand(), paged_result));
	}
	
	public static void doPagedResult(MessageReceivedEvent event, Command command, PagedResult<?> paged_result, NonCommandTriggerPoint trigger_point) {
		event.getChannel().sendMessage(paged_result.getPageAsEmbed().build()).queue(message -> {
			String location = event.getAuthor().getId() + "," + (event.getChannelType().equals(ChannelType.TEXT) ? event.getGuild().getId() + "," : "") + event.getChannel().getId();
			trigger_point.setMessageId(message.getId());
			CommandListener.addNonCommandTriggerPoint(location, trigger_point);
		});
	}
	
	/**
	 * The message listener inherited from {@link ListenerAdapter} where all the PRE-command logic is located such as {@link Argument} and prefix checking.
	 */
	public void onMessageReceived(MessageReceivedEvent event) {
		String prefix = CommandListener.getPrefix(event.getGuild());
		
		if(event.isFromType(ChannelType.TEXT))
			Statistics.addAction(Statistics.GUILD_MESSAGE);
		else if(event.isFromType(ChannelType.PRIVATE))
			Statistics.addAction(Statistics.PM_MESSAGE);
		
		if(event.getChannelType().equals(ChannelType.TEXT)) {
			if(event.getMessage().getRawContent().equals(event.getGuild().getMember(event.getJDA().getSelfUser()).getAsMention() + " prefix")) {
				event.getChannel().sendMessage(new MessageBuilder().append("The current prefix for this server is ").append(prefix, Formatting.BOLD).build()).queue();
				
				System.out.println("prefix was triggered");
				
				return;
			}
		}
		
		if(event.getMessage().getRawContent().equals(prefix + "mhelp")) {
			MessageBuilder message_builder = new MessageBuilder();
			message_builder.append("mhelp has been discontinued and is now **help**");
			
			if(prefix.equals("!")) {
				message_builder.append("\n\n").append("We strongly advise you to change the prefix by using ").append(prefix + "set prefix", Formatting.BOLD);
				message_builder.append(" because **!** is a somewhat standard prefix and will therefore collide with many other bots!");
			}
			
			event.getChannel().sendMessage(new EmbedBuilder().setDescription(message_builder.getStringBuilder().toString()).setColor(Color.CYAN).build()).queue();
			
			System.out.println("mhelp was triggered");
			
			return;
		}
		
		if(event.getMessage().getRawContent().equals("m!new")) {
			Command command = CommandListener.getCommandManager().getCommands("new").get(0);
			if(command != null) {
				CommandListener.executeCommand(command, event, prefix, System.nanoTime());
				return;
			}
		}
		
		if(event.getMessage().getRawContent().startsWith(prefix)) {
			long command_started = System.nanoTime();
			
			String message = event.getMessage().getRawContent().substring(prefix.length());
			
			COMMANDS :
			for(Command command : CommandListener.getCommandManager().getCommandsAuthorized(event.getChannel(), event.getAuthor())) {
				if(event.getMessage().getRawContent().length() < command.getCommand().length())
					continue COMMANDS;
				
				String msg = message;
				String cmd = command.getCommand();
				
				if(!command.isCaseSensitive()) {
					msg = msg.toLowerCase();
					cmd = cmd.toLowerCase();
				}
				
				if(!msg.startsWith(cmd))
					continue COMMANDS;
				
				msg = message.substring(cmd.length());
				
				if(msg.length() == 0) {
					if(command.getArguments().length == 0) {
						CommandListener.executeCommand(command, event, prefix, command_started);
					}else{
						Object[] arguments = new Object[command.getArguments().length];
						
						for(int i = 0; i < command.getArguments().length; i++) {
							if(command.getArguments()[i].requiresText())
								continue COMMANDS;
							
							if(command.getArguments()[i] instanceof ArgumentAttachment) {
								int attachmentOffset = 0;
								
								for(int i2 = 0; i2 < command.getArguments().length && i < i2; i2++) {
									if(command.getArguments()[i] instanceof ArgumentAttachment) {
										attachmentOffset = attachmentOffset + 1;
									}
								}
								
								if(event.getMessage().getAttachments().size() > attachmentOffset) {
									arguments[i] = event.getMessage().getAttachments().get(attachmentOffset);
								}else continue COMMANDS;
							}
						}
						
						CommandListener.executeCommand(command, event, prefix, command_started, arguments);
					}
					break COMMANDS;
				}
				
				if(msg.length() > 0) {
					if(command.getArguments().length > 0) {
						if(msg.charAt(0) == ' ') {
							int max_possible_arguments = (int) msg.codePoints().filter(c2 -> c2 == ' ').count();
							
							int args = 0;
							
							Object[] arguments = new Object[Math.max(max_possible_arguments, command.getArguments().length)];
							
							ARGUMENTS :
							for(int i = 0; i < arguments.length; i++) {
								Argument<?> argument = null;
								if(command.getArguments().length - 1 >= i) {
									argument = command.getArguments()[i];
								}
								
								if(argument == null && command.hasEndlessArgument()) {
									if(argument instanceof ArgumentString) {
										System.err.println(command.toString() + " | " + "The endless argument was an illegal type (STRING)");
										continue COMMANDS;
									}
								}else if(argument == null && !command.hasEndlessArgument()) {
									System.err.println(command.toString() + " | " + "Something went wrong! No more arguments was found and the endless argument was set to false");
									continue COMMANDS;
								}
								
								if(argument.requiresText()) {
									if(msg.startsWith(" ")) 
										msg = msg.substring(1);
									
									if(msg.length() == 0)
										continue COMMANDS;
									
									String content;
									
									if(argument.hasToBeLast()) {
										content = msg;
										msg = "";
									}else{
										content = msg.substring(0, (msg.contains(" ")) ? msg.indexOf(" ") : msg.length());
										msg = msg.substring(content.length());
									}
									
									VerifiedValue<?> verified = argument.verify(event, command, content);
									
									if(verified.getVerified() == Argument.ARGUMENT_CORRECT) {
										args = args + 1;
										arguments[i] = verified.getVerfiedValue();
										continue ARGUMENTS;
									}else if(verified.getVerified() == Argument.ARGUMENT_INCORRECT) {
										continue COMMANDS;
									}else if(verified.getVerified() == Argument.COMMAND_END) {
										args = args + 1;
										arguments[i] = verified.getVerfiedValue();
										break ARGUMENTS;
									}else if(verified.getVerified() == Argument.NOT_TESTED) {
										System.err.println(command.toString() + " | " + "Something went wrong! Argument (" + argument.toString() + ") returns NOT_TESTED when verified");
										continue COMMANDS;
									}
								}else{
									if(argument instanceof ArgumentAttachment) {
										int attachmentOffset = 0;
										
										for(int i2 = 0; i2 < command.getArguments().length && i < i2; i2++) {
											if(command.getArguments()[i] instanceof ArgumentAttachment) {
												attachmentOffset = attachmentOffset + 1;
											}
										}
										
										if(event.getMessage().getAttachments().size() > attachmentOffset) {
											arguments[i] = event.getMessage().getAttachments().get(attachmentOffset);
											args = args + 1;
										}else continue COMMANDS;
									}
								}
							}
							
							if(!command.hasEndlessArgument()) {
								if(command.getArguments().length != args) {
									continue COMMANDS;
								}
							}else if(args < command.getArguments().length) {
								continue COMMANDS;
							}
							
							Object[] objects = new Object[args];
							for(int i2 = 0; i2 < objects.length; i2++) {
								objects[i2] = arguments[i2];
							}
							
							arguments = objects;
							
							CommandListener.executeCommand(command, event, prefix, command_started, arguments);
							
							break COMMANDS;
						}else continue COMMANDS;
					}else continue COMMANDS;
				}
			}
		}else{
			String location = event.getAuthor().getId() + "," + (event.getChannelType().equals(ChannelType.TEXT) ? event.getGuild().getId() + "," : "") + event.getChannel().getId();
			if(CommandListener.non_command_trigger_points.containsKey(location)) {
				NonCommandTriggerPoint trigger_point = CommandListener.non_command_trigger_points.get(location);
				CommandListener.executeTriggerPoint(event, location, trigger_point);
			}
		}
	}
	
	/**
	 * Executes a non-command
	 * 
	 * Possibly temporary.
	 * 
	 * @param location location is set link this 
	 * {@link User#getId()} + "," + ({@link ChannelType#equals(Object)} {@link ChannelType#TEXT} ? {@link Guild#getId()} + "," : "") + {@link MessageChannel#getId()}
	 * @param trigger_point {@link CommandListener#non_command_trigger_points}
	 */
	private static void executeTriggerPoint(MessageReceivedEvent event, String location, NonCommandTriggerPoint trigger_point) {
		Runnable runnable = new Runnable() {
			public void run() {
				if(trigger_point.isPaged()) {
					String message_content = event.getMessage().getRawContent().toLowerCase();
					if(message_content.equals("next page") || message_content.equals("previous page") || message_content.startsWith("go to page ") || message_content.equals("cancel")) {
						RestAction<Message> get_message_is_deleted = new RestAction<Message>(event.getJDA(), Route.Messages.GET_MESSAGE.compile(event.getChannel().getId(), trigger_point.getInitalMessageId())) {
							protected void handleResponse(Response response, Request<Message> request) {
								if(!response.isOk()) {
									if(ErrorResponse.fromCode(response.getObject().getInt("code")).equals(ErrorResponse.UNKNOWN_MESSAGE)) {
										request.onSuccess(null);
									}else{
										request.onFailure(response);
									}
								}else request.onSuccess(api.getEntityBuilder().createMessage(response.getObject(), event.getChannel(), false));
							}
						};
						
						if(message_content.equals("next page")) {
							PagedResult<?> paged_result = (PagedResult<?>) trigger_point.getObject();
							
							event.getChannel().deleteMessageById(event.getMessageId()).queue();
							
							get_message_is_deleted.queue(message -> {
								if(paged_result.nextPage()) {
									if(message != null)
										message.editMessage(paged_result.getPageAsEmbed().build()).queue();
									else event.getChannel().sendMessage(paged_result.getPageAsEmbed().build()).queue(message_new -> {
										trigger_point.setMessageId(message_new.getId());
									});
								}else event.getChannel().sendMessage("Already on the last page").queue();
							});
						}else if(message_content.equals("previous page")) {
							PagedResult<?> paged_result = (PagedResult<?>) trigger_point.getObject();
							
							event.getChannel().deleteMessageById(event.getMessageId()).queue();
							
							get_message_is_deleted.queue(message -> {
								if(paged_result.previousPage()) {
									if(message != null)
										message.editMessage(paged_result.getPageAsEmbed().build()).queue();
									else event.getChannel().sendMessage(paged_result.getPageAsEmbed().build()).queue(message_new -> {
										trigger_point.setMessageId(message_new.getId());
									});
								}else event.getChannel().sendMessage("Already on the first page").queue();
							});
						}else if(message_content.startsWith("go to page ")) {
							PagedResult<?> paged_result = (PagedResult<?>) trigger_point.getObject();
							
							get_message_is_deleted.queue(message -> {
								int number = -1;
								
								try {
									number = Integer.parseInt(message_content.substring(11));
								}catch(NumberFormatException e) {}
								
								if(number != -1) {
									event.getChannel().deleteMessageById(event.getMessageId()).queue();
									
									if(paged_result.setPage(number)) {
										if(message != null)
											message.editMessage(paged_result.getPageAsEmbed().build()).queue();
										else event.getChannel().sendMessage(paged_result.getPageAsEmbed().build()).queue(message_new -> {
											trigger_point.setMessageId(message_new.getId());
										});
									}else event.getChannel().sendMessage("Incorrect page number, correct range is **1** - **" + paged_result.getMaxPages() + "**").queue();
								}
							});
						}else if(message_content.equals("cancel")) {
							CommandListener.non_command_trigger_points.remove(location);
							
							get_message_is_deleted.queue(message -> {
								if(message != null)
									event.getChannel().deleteMessageById(trigger_point.getInitalMessageId()).queue();
							});
							
							event.getMessage().addReaction("✅").queue();
						}
						
						return;
					}
				}
				
				if(trigger_point instanceof ExecutableNonCommandTriggerPoint)
					if(((ExecutableNonCommandTriggerPoint) trigger_point).execute(event))
						CommandListener.removeNonCommandTriggerPoint(location);
			}
		};
		
		CommandListener.execute(event.getJDA(), runnable);
	}
	
	/**
	 * Executes a command
	 */
	private static void executeCommand(Command command, MessageReceivedEvent event, String prefix, long time_started, Object... arguments) {
		Runnable runnable = new Runnable() {
			public void run() {
				if(checkPermissions(event, command)) {
					command.execute(event, prefix, arguments);
					
					Statistics.addCommandExecuted(command, System.nanoTime() - time_started);
					
					System.out.println("Executed command (" + command.getCommand() + ") with the argument(s) " + Arrays.toString(arguments) + ", time elapsed " + (System.nanoTime() - time_started));
				}
			}
		};
		
		CommandListener.execute(event.getJDA(), runnable);
	}
	
	/**
	 * Checks if the BOT has the required permissions to execute the command, {@link CommandListener#base_permissions} | {@link Command#getBotDiscordPermissionsNeeded()}
	 */
	private static boolean checkPermissions(MessageReceivedEvent event, Command command) {
		if(event.isFromType(ChannelType.TEXT)) {
			Member bot = event.getGuild().getMember(event.getJDA().getSelfUser());
			long permissions_needed = Permission.getRaw(base_permissions) | Permission.getRaw(command.getBotDiscordPermissionsNeeded());
			
			String missing_permissions_text = "";
			
			for(Permission permission : Permission.getPermissions(permissions_needed)) {
				if(!bot.hasPermission(event.getTextChannel(), permission)) {
					missing_permissions_text = missing_permissions_text + permission.getName() + "\n";
				}
			}
			
			if(!missing_permissions_text.equals("")) {
				missing_permissions_text = 
					"Missing permission(s) to execute **" 
					+ command.getCommand() 
					+ "** in " + event.getTextChannel().getName() 
					+ ", " + event.getGuild().getName() 
					+ "\n```" + missing_permissions_text
					+ "```";
				
				MessageChannel channel;
				if(!bot.hasPermission(event.getTextChannel(), Permission.MESSAGE_WRITE))
					channel = event.getAuthor().openPrivateChannel().complete();
				else channel = event.getTextChannel();
				
				channel.sendMessage(missing_permissions_text).queue();
				
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Executes provided runnable with the current Shard's thread pool {@link Shard#getCommandExecutor()}
	 */
	private static void execute(JDA jda, Runnable runnable) {
		int shard;
		if(jda.getShardInfo() != null)
			shard = jda.getShardInfo().getShardId();
		else shard = 0;
		
		JockieBot.getShardById(shard).getCommandExecutor().execute(runnable);
	}
}