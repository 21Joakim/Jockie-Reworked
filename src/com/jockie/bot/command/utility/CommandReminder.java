package com.jockie.bot.command.utility;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

import com.jockie.bot.Storage;
import com.jockie.bot.command.core.impl.Arguments.ArgumentBoolean;
import com.jockie.bot.command.core.impl.Arguments.ArgumentNumber;
import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue.ArgumentEntry;
import com.jockie.bot.utility.Utility;
import com.jockie.bot.command.core.impl.CommandImpl;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandReminder extends CommandImpl {
	
	public CommandReminder() {
		super("reminder", new ArgumentNumber("How many of the selected time units before the bot notifies you"), 
				new ArgumentTypeValue("Time unit", null, 
					new ArgumentEntry("Seconds", "SECOND", "SECOND", "SECONDS"),
					new ArgumentEntry("Minutes", "MINUTE", "MINUTE", "MINUTES"),
					new ArgumentEntry("Hours", "HOUR", "HOUR", "HOURS")
				), 
				new ArgumentBoolean("Should send reminder in private? (If this command was triggered in pm this argument won't matter)", false), 
				new ArgumentString("Reason"));
		super.setCommandDescription("A reminder which will remind you with after a certain amount of time");
		super.setBeta(true);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		long time_units = (long) arguments[0];
		
		String time_unit_str = (String) arguments[1];
		if(!time_unit_str.substring(time_unit_str.length() - 1, time_unit_str.length()).equals("S"))
			time_unit_str = time_unit_str + "S";
			
		ChronoUnit time_unit = ChronoUnit.valueOf(time_unit_str);
		
		if(time_units > 0) {
			if(time_unit.equals(ChronoUnit.SECONDS)) {
				if(time_units > 86400) {
					event.getChannel().sendMessage("Maximum reminder time is 24 hours").queue();
					return;
				}
			}else if(time_unit.equals(ChronoUnit.MINUTES)) {
				if(time_units > 1440) {
					event.getChannel().sendMessage("Maximum reminder time is 24 hours").queue();
					return;
				}
			}else if(time_unit.equals(ChronoUnit.HOURS)) {
				if(time_units > 24) {
					event.getChannel().sendMessage("Maximum reminder time is 24 hours").queue();
					return;
				}
			}
		}else{
			event.getChannel().sendMessage("The time units may not be less than 1").queue();
			return;
		}
		
		Reminder reminder = new Reminder(
			time_units, 
			time_unit, 
			event.getMessage().getCreationTime().atZoneSameInstant(ZoneId.of("GMT+0")).toOffsetDateTime(), 
			((boolean) arguments[2]) ? event.getAuthor().openPrivateChannel().complete().getIdLong() : event.getChannel().getIdLong(), 
			event.getAuthor().getIdLong(),
			(String) arguments[3]);
		
		Storage.addReminder(event.getJDA(), reminder);
		
		event.getChannel().sendMessage("Your reminder has been set to remind you in " + Utility.formattedTime(TimeUnit.valueOf(time_unit.name()).toSeconds(time_units))).queue();
	}
	
	public static class Reminder {
		private long time_units;
		private TemporalUnit time_unit;
		private OffsetDateTime time_started;
		private OffsetDateTime time_finished;
		
		private long author;
		
		private long channel;
		
		private String message;
		
		public Reminder(long time_units, TemporalUnit time_unit, OffsetDateTime time_started, long channel, long author, String message) {
			this.time_units = time_units;
			this.time_unit = time_unit;
			this.time_started = time_started;
			this.time_finished = this.time_started.plus(this.time_units, this.time_unit);
			this.channel = channel;
			this.author = author;
			this.message = message;
		}
		
		public OffsetDateTime getTimeFinished() {
			return time_finished;
		}
		
		public String getMessage() {
			return message;
		}
		
		public long getChannel() {
			return channel;
		}
		
		public long getAuthor() {
			return author;
		}
	}
}
