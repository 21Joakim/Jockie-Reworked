package com.jockie.bot.command.utility;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.jockie.bot.Storage;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.command.core.impl.CommandListener;
import com.jockie.bot.command.core.non_command.ExecutableNonCommandTriggerPoint;
import com.jockie.bot.command.core.non_command.PagedResult;
import com.jockie.bot.command.utility.CommandReminder.Reminder;
import com.jockie.bot.utility.Utility;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandRemoveReminder extends CommandImpl {
	
	public CommandRemoveReminder() {
		super("remove reminder");
		super.setCommandDescription("Remove one of your reminders");
		super.setBeta(true);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		List<Reminder> reminders = new ArrayList<Reminder>();
		
		for(Reminder reminder : Storage.getReminders(event.getJDA())) {
			if(event.getAuthor().getIdLong() == reminder.getAuthor()) {
				reminders.add(reminder);
			}
		}
		
		if(reminders.size() > 0) {
			PagedResult<Reminder> paged_result_reminders = new PagedResult<Reminder>(reminders, "%s (%s)", Reminder::getMessage, new Function<Reminder, Object>() {
				public Object apply(Reminder reminder) {
					return Utility.formattedTime(reminder.getTimeFinished().toEpochSecond() - LocalDateTime.now().toEpochSecond(reminder.getTimeFinished().getOffset()));
				}
			});
			
			CommandListener.doPagedResult(event, this, paged_result_reminders, new ExecutableNonCommandTriggerPoint(this.getCommand(), paged_result_reminders) {
				public boolean execute(MessageReceivedEvent event) {
					int number = -1;
					try {
						number = Integer.parseInt(event.getMessage().getRawContent());
					}catch(Exception e) {}
					
					if(number != -1) {
						PagedResult<?> reminders = (PagedResult<?>) this.getObject();
						
						if(number > 0 && number <= reminders.getCurrentPageEntries().size()) {
							Reminder reminder = (Reminder) reminders.getCurrentPageEntries().get(number - 1);
							if(reminder != null) {
								Storage.removeReminder(event.getJDA(), reminder);
								event.getChannel().sendMessage(new MessageBuilder().append("Removed reminder with the message ")
									.appendCodeBlock(reminder.getMessage(), "text")
									.append(" which expires in " + Utility.formattedTime(reminder.getTimeFinished().toEpochSecond() - LocalDateTime.now().toEpochSecond(reminder.getTimeFinished().getOffset()))).build()).queue();
								return true;
							}else{
								event.getChannel().sendMessage("Something went wrong!").queue();
							}
						}
					}
					return false;
				}
			});
		}else{
			event.getChannel().sendMessage("You don't have any active reminders.").queue();
		}
	}
}