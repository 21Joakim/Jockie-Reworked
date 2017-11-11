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

public class CommandPropose extends CommandImpl {

	public CommandPropose() {
		super("propose", new ArgumentUser());
		super.setPMTriggerable(false);
		super.setBotTriggerable(false);
		super.setCommandDescription("Propose to marry a user");
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		User mentioned_user = (User) arguments[0];
		
		if(mentioned_user.isBot()) {
			event.getChannel().sendMessage(event.getAuthor().getAsMention() + " you may not propose to bots!").queue();
			return;
		}
		
		if(mentioned_user.getId().equals(event.getAuthor().getId())) {
			event.getChannel().sendMessage(event.getAuthor().getAsMention() + " you may not propose to yourself!").queue();
			return;
		}
		
		if(!event.getGuild().isMember(mentioned_user)) {
			event.getChannel().sendMessage(event.getAuthor().getAsMention() + " you may only propose to users whom are on the current server").queue();
			return;
		}
		
		Result author_result = Marriage.getMarriageValue(event.getAuthor().getId(), PersonColumn.PROPOSE, PersonColumn.PARTNER, PersonColumn.MARRIED);
		if(author_result.next()) {
			Row author_row = author_result.getRows().get(0);
			
			Propose author_status = Propose.valueOf((String) author_row.getColumn(PersonColumn.PROPOSE.getValue()));
			String author_partner = (String) author_row.getColumn(PersonColumn.PARTNER.getValue());
			
			if(!Boolean.parseBoolean((String) author_row.getColumn(PersonColumn.MARRIED.getValue()))) {
				if(author_status.equals(Propose.NONE)) {
					Result result_proposed_to = Marriage.getMarriageValue(mentioned_user.getId(), PersonColumn.PROPOSE, PersonColumn.PARTNER, PersonColumn.MARRIED);
					if(result_proposed_to.next()) {
						Row row_proposed_to = author_result.getRows().get(0);
						
						Propose proposed_to_status = Propose.valueOf((String) row_proposed_to.getColumn(PersonColumn.PROPOSE.getValue()));
						String proposed_to_partner = (String) row_proposed_to.getColumn(PersonColumn.PARTNER.getValue());
						
						Utility.getUser(event.getJDA(), event.getGuild(), mentioned_user.getId(), proposed_to -> {
							if(!Boolean.parseBoolean((String) row_proposed_to.getColumn(PersonColumn.MARRIED.getValue()))) {
								Utility.getUser(event.getJDA(), event.getGuild(), proposed_to_partner, proposed_to_partner_name -> {
									if(proposed_to_status.equals(Propose.PROPOSEDTO)) {
										event.getChannel().sendMessage(event.getAuthor().getAsMention() + ", " + proposed_to + " is already being proposed to by " + proposed_to_partner_name).queue();
									}else if(proposed_to_status.equals(Propose.PROPOSER)) {
										event.getChannel().sendMessage(event.getAuthor().getAsMention() + ", " + proposed_to + " is already proposing to " + proposed_to_partner_name).queue();
									}
								});
							}else{
								Utility.getUser(event.getJDA(), event.getGuild(), proposed_to_partner, proposed_to_partner_name -> {
									event.getChannel().sendMessage(event.getAuthor().getAsMention() + ", " + proposed_to + " is already married to " + proposed_to_partner_name).queue();
								});
							}
						});
					}else{
						Utility.getUser(event.getJDA(), event.getGuild(), mentioned_user.getId(), proposed_to -> {
							Marriage.propose(event.getAuthor().getId(), mentioned_user.getId());
							event.getChannel().sendMessage(event.getAuthor().getAsMention() + " just proposed to " + proposed_to).queue();
						});
					}
				}else{
					Utility.getUser(event.getJDA(), event.getGuild(), author_partner, author_partner_name -> {
						if(author_status.equals(Propose.PROPOSEDTO)) {
							event.getChannel().sendMessage(event.getAuthor().getAsMention() + " you're already being proposed to by " + author_partner_name).queue();
						}else if(author_status.equals(Propose.PROPOSER)) {
							event.getChannel().sendMessage(event.getAuthor().getAsMention() + " you're already proposing to " + author_partner_name).queue();
						}
					});
				}
			}else{
				Utility.getUser(event.getJDA(), event.getGuild(), author_partner, author_partner_name -> {
					event.getChannel().sendMessage(event.getAuthor().getAsMention() + " you're already married to " + author_partner_name).queue();
				});
			}
		}else{
			Result result_proposed_to = Marriage.getMarriageValue(mentioned_user.getId(), PersonColumn.PROPOSE, PersonColumn.PARTNER, PersonColumn.MARRIED);
			if(result_proposed_to.next()) {
				Row row_proposed_to = result_proposed_to.getRows().get(0);
				
				Propose proposed_to_status = Propose.valueOf((String) row_proposed_to.getColumn(PersonColumn.PROPOSE.getValue()));
				String proposed_to_partner = (String) row_proposed_to.getColumn(PersonColumn.PARTNER.getValue());
				
				Utility.getUser(event.getJDA(), event.getGuild(), mentioned_user.getId(), proposed_to -> {
					if(!Boolean.parseBoolean((String) row_proposed_to.getColumn(PersonColumn.MARRIED.getValue()))) {
						Utility.getUser(event.getJDA(), event.getGuild(), proposed_to_partner, proposed_to_partner_name -> {
							if(proposed_to_status.equals(Propose.PROPOSEDTO)) {
								event.getChannel().sendMessage(event.getAuthor().getAsMention() + ", " + proposed_to + " is already being proposed to by " + proposed_to_partner_name).queue();
							}else if(proposed_to_status.equals(Propose.PROPOSER)) {
								event.getChannel().sendMessage(event.getAuthor().getAsMention() + ", " + proposed_to + " is already proposing to " + proposed_to_partner_name).queue();
							}
						});
					}else{
						Utility.getUser(event.getJDA(), event.getGuild(), proposed_to_partner, proposed_to_partner_name -> {
							event.getChannel().sendMessage(proposed_to + " is already married to " + proposed_to_partner_name).queue();
						});
					}
				});
			}else{
				Utility.getUser(event.getJDA(), event.getGuild(), mentioned_user.getId(), proposed_to -> {
					Marriage.propose(event.getAuthor().getId(), mentioned_user.getId());
					event.getChannel().sendMessage(event.getAuthor().getAsMention() + " just proposed to " + proposed_to).queue();
				});
			}
		}
	}
}