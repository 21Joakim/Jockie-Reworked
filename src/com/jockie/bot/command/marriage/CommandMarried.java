package com.jockie.bot.command.marriage;

import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.command.core.impl.Arguments.ArgumentUser;
import com.jockie.bot.database.Marriage;
import com.jockie.bot.database.Marriage.Propose;
import com.jockie.bot.database.column.PersonColumn;
import com.jockie.bot.utility.Utility;
import com.jockie.sql.base.Result;
import com.jockie.sql.base.Row;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandMarried extends CommandImpl {
	
	public CommandMarried() {
		super("married", new ArgumentUser(true));
		super.setBotTriggerable(false);
		super.setCommandDescription("Marriage status of a user");
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		User user = (User) arguments[0];
		
		if(user.isBot()) {
			event.getChannel().sendMessage(event.getAuthor().getAsMention() + " you may not get the marriage status of a bot!").queue();
			return;
		}
		
		boolean isAuthor;
		
		if(user.getId().equals(event.getAuthor().getId())) {
			isAuthor = true;
		}else isAuthor = false;
		
		Result result = Marriage.getMarriageValue(user.getId(), PersonColumn.MARRIED, PersonColumn.PROPOSE, PersonColumn.PARTNER);
		result.next();
		
		if(result.getRows().size() > 0) {
			Row row = result.getRows().get(0);
			
			Propose status = Propose.valueOf((String) row.getColumn(PersonColumn.PROPOSE.getValue()));
			
			Utility.getUser(event.getJDA(), event.getGuild(), user.getId(), name -> {
				if(status.equals(Propose.NONE)) {
					event.getChannel().sendMessage(event.getAuthor().getAsMention() + ((isAuthor) ? " you are" : (", " + name + " is")) + " not married to anyone!").queue();
				}else{
					Utility.getUser(event.getJDA(), event.getGuild(), (String) row.getColumn(PersonColumn.PARTNER.getValue()), partner_name -> {
						if(Boolean.parseBoolean((String) row.getColumn(PersonColumn.MARRIED.getValue()))) {
							event.getChannel().sendMessage(event.getAuthor().getAsMention() + ((isAuthor) ? " you are" : (", " + name + " is")) + " married to " + partner_name).queue();
						}else if(status.equals(Propose.PROPOSEDTO)) {
							event.getChannel().sendMessage(event.getAuthor().getAsMention() + ((isAuthor) ? " you are" : (", " + name + " is")) + " being proposed to by " + partner_name).queue();
						}else if(status.equals(Propose.PROPOSER)) {
							event.getChannel().sendMessage(event.getAuthor().getAsMention() + ((isAuthor) ? " you are" : (", " + name + " is")) + " proposing to " + partner_name).queue();
						}
					});
				}
			});
		}else{
			Utility.getUser(event.getJDA(), event.getGuild(), user.getId(), name -> {
				event.getChannel().sendMessage(event.getAuthor().getAsMention() + ((isAuthor) ? " you are" : (", " + name + " is")) + " not married to anyone!").queue();
			});
		}
	}
}