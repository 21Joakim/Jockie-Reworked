package com.jockie.bot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.jockie.bot.command.core.Command;
import com.jockie.bot.database.Database;
import com.jockie.bot.database.column.StatisticsColumn;
import com.jockie.bot.main.JockieBot;
import com.jockie.bot.utility.CountingArray;
import com.jockie.bot.utility.Utility;
import com.jockie.sql.action.ActionCluster;
import com.jockie.sql.action.ActionInsert;

public class Statistics {
	
	private static CountingArray<Integer> action_counter = new CountingArray<Integer>();
	
	private static ArrayList<CommandDiagnostic> commands = new ArrayList<CommandDiagnostic>();
	
	public static class CommandDiagnostic {
		
		private String command;
		
		private long execution_times = 0;
		
		private long exceution_time_nanoseconds = 0;
		
		public CommandDiagnostic(String command) {
			this.command = command;
		}
		
		public synchronized void reset() {
			this.execution_times = 0;
			this.exceution_time_nanoseconds = 0;
		}
		
		public synchronized void execute(long time_elapsed) {
			this.execution_times = this.execution_times + 1;
			this.exceution_time_nanoseconds = this.exceution_time_nanoseconds + time_elapsed;
		}
		
		public synchronized String getCommand() {
			return this.command;
		}
		
		public synchronized long getExecutionTimes() {
			return this.execution_times;
		}
		
		public synchronized long getAverageExecutionTime() {
			if(this.exceution_time_nanoseconds == 0 || this.execution_times == 0)
				return 0;
			return this.exceution_time_nanoseconds/this.execution_times;
		}
		
		public String toString() {
			return this.getCommand() + "|" + this.getExecutionTimes() + "|" + this.getAverageExecutionTime();
		}
	}
	
	private static ScheduledExecutorService save_executor = Executors.newScheduledThreadPool(1);
	
	private static ZonedDateTime last_statistics_insert;
	
	private static final int SAVE_DELAY = 3600;
	private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
	
	public static final int GUILD_MESSAGE = 1;
	public static final int PM_MESSAGE = 2;
	public static final int NEW_GUILD = 3;
	public static final int LOST_GUILD = 4;
	
	static {
		Statistics.last_statistics_insert = ZonedDateTime.now(ZoneId.of("GMT+0"));
		
		long delay = ZonedDateTime.of(LocalDate.now().atTime(LocalDateTime.now().plusHours(1).getHour(), 0), ZoneId.of("GMT+0")).toEpochSecond() - ZonedDateTime.now(ZoneId.of("GMT+0")).toEpochSecond();
		
		System.out.println("Seconds till next statistics insert : " + delay);
		
		Statistics.save_executor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				System.out.println("Statistics insert starts! (From " + Statistics.last_statistics_insert.format(Utility.getDateTimeFormatter()) + " | To "
						+ ZonedDateTime.now(ZoneId.of("GMT+0")).format(Utility.getDateTimeFormatter()) + ")");
				
				String[] data_array = new String[6];
				
				data_array[0] = "Total Commands:" + Statistics.getTotalSuccessfulCommands();
				
				String value = "";
				if(Statistics.commands.size() > 0) {
					ArrayList<CommandDiagnostic> diagnostics = new ArrayList<CommandDiagnostic>(Statistics.commands);
					for(int i = 0; i < diagnostics.size(); i++) {
						value = value + diagnostics.get(i).toString() + ",";
						Statistics.commands.remove(diagnostics.get(i));
					}
					value = value.substring(0, value.length() - ",".length());
				}
				
				data_array[1] = "Command Data:" + value;

				data_array[2] = "Guild Messages:" + Statistics.getActionCount(GUILD_MESSAGE);
				data_array[3] = "Private Messages:" + Statistics.getActionCount(PM_MESSAGE);
				data_array[4] = "Guilds Gained:" + Statistics.getActionCount(NEW_GUILD);
				data_array[5] = "Guilds Lost:" + Statistics.getActionCount(LOST_GUILD);
				
				Statistics.action_counter.clear();
				
				ActionInsert[] inserts = new ActionInsert[data_array.length];
				
				String date_time_from = Statistics.last_statistics_insert.format(Utility.getDateTimeFormatter());
				String date_time_to = ZonedDateTime.now(ZoneId.of("GMT+0")).format(Utility.getDateTimeFormatter());
				
				Statistics.last_statistics_insert = ZonedDateTime.now(ZoneId.of("GMT+0"));
				
				for(int i = 0; i < inserts.length; i++) {
					String name = data_array[i].substring(0, data_array[i].indexOf(":"));
					String data = data_array[i].substring(data_array[i].indexOf(":") + 1, data_array[i].length());
					
					ActionInsert insert = JockieBot.getDatabase().insert(Database.STATISTICS);
					insert.getInsert().insert(StatisticsColumn.NAME, name);
					insert.getInsert().insert(StatisticsColumn.VALUE, data);
					insert.getInsert().insert(StatisticsColumn.DATE_TIME_FROM, date_time_from);
					insert.getInsert().insert(StatisticsColumn.DATE_TIME_TO, date_time_to);
					
					inserts[i] = insert;
				}
				
				ActionCluster cluster = JockieBot.getDatabase().cluster(inserts);
				
				cluster.execute();
				
				System.out.println("Statistics insert is done! Next statistics insert it scheduled for " + ZonedDateTime.now(ZoneId.of("GMT+0")).plusSeconds(TIME_UNIT.toSeconds(SAVE_DELAY)).format(Utility.getDateTimeFormatter()));
			}
		}, delay, SAVE_DELAY, TIME_UNIT);
	}
	
	public static synchronized void addCommandExecuted(Command command, long time_elapsed) {
		Statistics.getCommandDiagnostic(command).execute(time_elapsed);
	}
	
	public static synchronized void addAction(int action) {
		Statistics.action_counter.add(action);
	}
	
	public static synchronized ZonedDateTime getLastStatisticsInsert() {
		return Statistics.last_statistics_insert;
	}
	
	public static synchronized CommandDiagnostic getCommandDiagnostic(Command command) {
		String cmd = command.getCommand();
		if(!command.isCaseSensitive())
			cmd = cmd.toLowerCase();
		
		for(int i = 0; i < Statistics.commands.size(); i++)
			if(Statistics.commands.get(i).getCommand().equals(cmd))
				return Statistics.commands.get(i);
		
		Statistics.commands.add(new CommandDiagnostic(cmd));
		return Statistics.commands.get(Statistics.commands.size() - 1);
	}
	
	public static synchronized long getTotalSuccessfulCommands() {
		long executed_commands = 0;
		for(CommandDiagnostic diagnostic : Statistics.getCommandDiagnostic())
			executed_commands = executed_commands + diagnostic.getExecutionTimes();
		return executed_commands;
	}
	
	public static synchronized List<CommandDiagnostic> getCommandDiagnostic() {
		return Statistics.commands;
	}
	
	public static synchronized CommandDiagnostic getCommandExecutedCount(Command command) {
		return Statistics.getCommandDiagnostic(command);
	}
	
	public static synchronized long getActionCount(int action) {
		return Statistics.action_counter.getCount((Integer) action);
	}
}