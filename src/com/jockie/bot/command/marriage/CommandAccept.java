package com.jockie.bot.command.marriage;

import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.database.Marriage;
import com.jockie.bot.database.Marriage.Propose;
import com.jockie.bot.database.column.PersonColumn;
import com.jockie.bot.utility.Utility;
import com.jockie.sql.base.Result;
import com.jockie.sql.base.Row;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandAccept extends CommandImpl {

	public CommandAccept() {
		super("accept");
		super.setBotTriggerable(false);
		super.setCommandDescription("Accepts the current marriage proposal");
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		User user_author = event.getAuthor();
		
		Result author_result = Marriage.getMarriageValue(user_author.getId(), PersonColumn.PROPOSE, PersonColumn.PARTNER, PersonColumn.MARRIED);
		author_result.next();
		
		if(author_result.getRows().size() > 0) {
			Row author_row = author_result.getRows().get(0);
			
			Propose author_status = Propose.valueOf((String) author_row.getColumn(PersonColumn.PROPOSE.getValue()));
			String author_partner = (String) author_row.getColumn(PersonColumn.PARTNER.getValue());
			
			if(!Boolean.parseBoolean((String) author_row.getColumn(PersonColumn.MARRIED.getValue()))) {
				if(author_status.equals(Propose.PROPOSEDTO)) {
					Utility.getUser(event.getJDA(), event.getGuild(), author_partner, partner_name -> {
						Marriage.marry(author_partner, user_author.getId());
						event.getChannel().sendMessage(user_author.getAsMention() + " just accepted the proposal to marry " + partner_name).queue();
					});
				}else{
					event.getChannel().sendMessage(user_author.getAsMention() + " no one is proposing to you").queue();
				}
			}else{
				Utility.getUser(event.getJDA(), event.getGuild(), author_partner, partner_name -> {
					event.getChannel().sendMessage(user_author.getAsMention() + " you're already married to " + partner_name).queue();
				});
			}
		}else{
			event.getChannel().sendMessage(user_author.getAsMention() + " no one is proposing to you").queue();
		}
	}
}