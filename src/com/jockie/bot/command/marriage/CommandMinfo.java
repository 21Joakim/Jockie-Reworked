package com.jockie.bot.command.marriage;

import java.awt.Color;
import java.sql.Timestamp;

import com.jockie.bot.command.core.impl.Arguments.ArgumentUser;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.database.Marriage;
import com.jockie.bot.database.Marriage.Propose;
import com.jockie.bot.database.column.PersonColumn;
import com.jockie.bot.utility.Utility;
import com.jockie.sql.base.Result;
import com.jockie.sql.base.Row;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandMinfo extends CommandImpl {
	
	public CommandMinfo() {
		super("minfo", new ArgumentUser(true));
		super.setBotTriggerable(false);
		super.setCommandDescription("Marriage information for a user");
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		User user = (User) arguments[0];
		
		if(user.isBot()) {
			event.getChannel().sendMessage(event.getAuthor().getAsMention() + " you may not get the marriage status of a bot!").queue();
			return;
		}
		
		Result result = Marriage.getMarriageInfo(user.getId());
		result.next();
		
		if(result.getRows().size() > 0) {
			Row row = result.getRows().get(0);
			
			Propose status = Propose.valueOf((String) row.getColumn(PersonColumn.PROPOSE.getValue()));
			
			boolean married = Boolean.parseBoolean((String) row.getColumn(PersonColumn.MARRIED.getValue()));
			
			Utility.getUser(event.getJDA(), event.getGuild(), user.getId(), name -> {
				EmbedBuilder embedBuilder = new EmbedBuilder();
				embedBuilder.setColor(Color.CYAN);
				
				StringBuilder information = new StringBuilder();
				
				information.append("User : " + name + "\n");
				information.append("Married : " + married + "\n");
				
				if(married) {
					Utility.getUser(event.getJDA(), event.getGuild(), (String) row.getColumn(PersonColumn.PARTNER.getValue()), name_partner -> {
						information.append("Partner : " + name_partner + "\n");
						if(status.equals(Propose.PROPOSEDTO)) {
							information.append("Proposer : " + name_partner + "\n");
						}else if(status.equals(Propose.PROPOSER)) {
							information.append("Proposer : " + name + "\n");
						}
						
						Timestamp time = (Timestamp) row.getColumn(PersonColumn.MARRIAGE_DATE.getValue());
						information.append("Marriage Date : " + time);
						
						embedBuilder.setDescription(information);
						event.getChannel().sendMessage(embedBuilder.build()).queue();
					});
				}else{
					if(!status.equals(Propose.NONE)) {
						Utility.getUser(event.getJDA(), event.getGuild(), (String) row.getColumn(PersonColumn.PARTNER.getValue()), name_partner -> {
							if(status.equals(Propose.PROPOSER)) {
								information.append("Status : Proposing to " + name_partner);
							}else if(status.equals(Propose.PROPOSEDTO)) {
								information.append("Status : Being proposed to by " + name_partner);
							}
							embedBuilder.setDescription(information);
							event.getChannel().sendMessage(embedBuilder.build()).queue();
						});
					}else{
						embedBuilder.setDescription(information);
						event.getChannel().sendMessage(embedBuilder.build()).queue();
					}
				}
			});
		}else{
			Utility.getUser(event.getJDA(), event.getGuild(), user.getId(), name -> {
				event.getChannel().sendMessage(new EmbedBuilder().setDescription("User : " + name + "\nMarried : " + false).build()).queue();
			});
		}
	}
}