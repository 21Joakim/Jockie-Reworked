package com.jockie.bot.command.marriage;

import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.database.Marriage;
import com.jockie.bot.database.column.PersonColumn;
import com.jockie.bot.utility.Utility;
import com.jockie.sql.base.Result;
import com.jockie.sql.base.Row;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandDivorce extends CommandImpl {

	public CommandDivorce() {
		super("divorce");
		super.setPMTriggerable(false);
		super.setBotTriggerable(false);
		super.setCommandDescription("Divorces your current partner");
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		User user = event.getAuthor();
		
		Result result = Marriage.getMarriageValue(user.getId(), PersonColumn.MARRIED, PersonColumn.PARTNER, PersonColumn.CAN_DIVORCE);
		result.next();
		
		if(result.getRows().size() > 0) {
			Row row = result.getRows().get(0);
			
			if(Boolean.parseBoolean((String) row.getColumn(PersonColumn.MARRIED.getValue()))) {
				String partner = (String) row.getColumn(PersonColumn.PARTNER.getValue());
				Utility.getUser(event.getJDA(), event.getGuild(), partner, partner_name -> {
					if(Boolean.parseBoolean((String) row.getColumn(PersonColumn.CAN_DIVORCE.getValue()))) {
						Marriage.divorce(user.getId(), partner);
						event.getChannel().sendMessage(user.getAsMention() + " just divorced " + partner_name).queue();
					}else{
						event.getChannel().sendMessage(user.getAsMention() + " you're unable to divorce " + partner_name).queue();
					}
				});
			}else{
				event.getChannel().sendMessage(user.getAsMention() + " you're not married to anyone!").queue();
			}
		}else{
			event.getChannel().sendMessage(user.getAsMention() + " you're not married to anyone!").queue();
		}
	}
}