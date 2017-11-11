package com.jockie.bot.command.set;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.jockie.bot.APIs.country.APICountry;
import com.jockie.bot.APIs.country.Country;
import com.jockie.bot.APIs.osu.Osu;
import com.jockie.bot.APIs.osu.OsuUser;
import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue.ArgumentEntry;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.command.core.impl.CommandListener;
import com.jockie.bot.command.core.non_command.ExecutableNonCommandTriggerPoint;
import com.jockie.bot.command.core.non_command.PagedResult;
import com.jockie.bot.database.Database;
import com.jockie.bot.database.column.UserInformationColumn;
import com.jockie.bot.main.JockieBot;
import com.jockie.sql.action.ActionGet;
import com.jockie.sql.action.ActionInsert;
import com.jockie.sql.action.ActionSet;
import com.jockie.sql.base.Result;
import com.jockie.sql.base.Where.Operator;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandSet extends CommandImpl {

	public CommandSet() {
		super("set", 
			new ArgumentTypeValue("Type to set", null, 
				new ArgumentEntry("Your osu user (Name/Id)", "OSU", "OSU"),
				new ArgumentEntry("Your birthday", "BIRTHDAY", "BIRTHDAY"),
				new ArgumentEntry("Your country", "COUNTRY", "COUNTRY"),
				new ArgumentEntry("Something about yourself", "BIO", "BIO")
			), new ArgumentString("Value to set"));
		super.setCommandDescription("Set information about you for more convenience and commands");
		super.setBotTriggerable(false);
		super.setBeta(true);
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		String type = (String) arguments[0];
		
		String value = (String) arguments[1];
		
		if(type.equals("OSU")) {
			OsuUser osu_user = Osu.getUser(value, 0);
			if(osu_user != null) {
				ActionGet get = JockieBot.getDatabase().get(Database.USER_INFORMATION);
				get.getSelect().select(UserInformationColumn.USER_ID);
				get.getWhere().where(UserInformationColumn.USER_ID, Operator.EQUAL, event.getAuthor().getId());
				
				Result result = get.execute();
				result.next();
				
				if(result.getRows().size() > 0) {
					ActionSet set = JockieBot.getDatabase().set(Database.USER_INFORMATION);
					set.getSet().set(UserInformationColumn.OSU_ID, osu_user.getUserId());
					set.getWhere().where(UserInformationColumn.USER_ID, Operator.EQUAL, event.getAuthor().getId());
					
					set.execute();
				}else{
					ActionInsert insert = JockieBot.getDatabase().insert(Database.USER_INFORMATION);
					insert.getInsert().insert(UserInformationColumn.USER_ID, event.getAuthor().getId()).insert(UserInformationColumn.OSU_ID, osu_user.getUserId());
					
					insert.execute();
				}
				
				event.getChannel().sendMessage(event.getAuthor().getAsMention() + " your osu user has now been set to " + osu_user.getUsername()).queue();
			}else{
				event.getChannel().sendMessage("There is no user with that name/id").queue();
			}
		}else if(type.equals("BIRTHDAY")) {
			try {
				DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				LocalDate date = LocalDate.parse(value, pattern);
				
				if(date.isBefore(LocalDate.now())) {
					ActionGet get = JockieBot.getDatabase().get(Database.USER_INFORMATION);
					get.getSelect().select(UserInformationColumn.USER_ID);
					get.getWhere().where(UserInformationColumn.USER_ID, Operator.EQUAL, event.getAuthor().getId());
					
					Result result = get.execute();
					result.next();
					
					if(result.getRows().size() > 0) {
						ActionSet set = JockieBot.getDatabase().set(Database.USER_INFORMATION);
						set.getSet().set(UserInformationColumn.BIRTHDAY, pattern.format(date));
						set.getWhere().where(UserInformationColumn.USER_ID, Operator.EQUAL, event.getAuthor().getId());
						
						set.execute();
					}else{
						ActionInsert insert = JockieBot.getDatabase().insert(Database.USER_INFORMATION);
						insert.getInsert().insert(UserInformationColumn.USER_ID, event.getAuthor().getId()).insert(UserInformationColumn.BIRTHDAY, pattern.format(date));
						
						insert.execute();
					}
					
					char[] month = date.getMonth().name().toLowerCase().toCharArray();
					month[0] -= 32;
					
					event.getChannel().sendMessage(event.getAuthor().getAsMention() + " your birthday has now been set to " + date.getDayOfMonth() + " " + new String(month) + " " + date.getYear()).queue();
				}else{
					event.getChannel().sendMessage(event.getAuthor().getAsMention() + " sorry but unless you are a time traveler that won't work!").queue();
				}
			}catch(DateTimeException e) {
				event.getChannel().sendMessage("Incorrectly formatted (Correct format should be DD/MM/YYYY) or incorrect values (Too many days of the month or incorrect month)").queue();
			}
		}else if(type.equals("COUNTRY")) {
			ArrayList<Country> countries = APICountry.searchByUnique(value);
			if(countries.size() == 1) {
				ActionGet get = JockieBot.getDatabase().get(Database.USER_INFORMATION);
				get.getSelect().select(UserInformationColumn.USER_ID);
				get.getWhere().where(UserInformationColumn.USER_ID, Operator.EQUAL, event.getAuthor().getId());
				
				Result result = get.execute();
				result.next();
				
				if(result.getRows().size() > 0) {
					ActionSet set = JockieBot.getDatabase().set(Database.USER_INFORMATION);
					set.getSet().set(UserInformationColumn.COUNTRY, countries.get(0).getAlpha3Code());
					set.getWhere().where(UserInformationColumn.USER_ID, Operator.EQUAL, event.getAuthor().getId());
					
					set.execute();
				}else{
					ActionInsert insert = JockieBot.getDatabase().insert(Database.USER_INFORMATION);
					insert.getInsert().insert(UserInformationColumn.USER_ID, event.getAuthor().getId()).insert(UserInformationColumn.COUNTRY, countries.get(0).getAlpha3Code());
					
					insert.execute();
				}
				
				event.getChannel().sendMessage(event.getAuthor().getAsMention() + " your country has now been set to " + countries.get(0).getName()).queue();
			}else if(countries.size() > 1) {
				PagedResult<Country> paged_result = new PagedResult<Country>(countries, Country::getName);
				
				CommandListener.doPagedResult(event, this, paged_result, new ExecutableNonCommandTriggerPoint(this.getCommand(), paged_result) {
					public boolean execute(MessageReceivedEvent event) {
						int number = -1;
						
						try {
							number = Integer.parseInt(event.getMessage().getRawContent());
						}catch(Exception e) {}
						
						if(number != -1) {
							PagedResult<?> countries = (PagedResult<?>) this.getObject();
							
							if(number > 0 && number <= countries.getEntriesPerPage()) {
								Country country = (Country) countries.getCurrentPageEntries().get(number - 1);
								
								ActionGet get = JockieBot.getDatabase().get(Database.USER_INFORMATION);
								get.getSelect().select(UserInformationColumn.USER_ID);
								get.getWhere().where(UserInformationColumn.USER_ID, Operator.EQUAL, event.getAuthor().getId());
								
								Result result = get.execute();
								result.next();
								
								if(result.getRows().size() > 0) {
									ActionSet set = JockieBot.getDatabase().set(Database.USER_INFORMATION);
									set.getSet().set(UserInformationColumn.COUNTRY, country.getAlpha3Code());
									set.getWhere().where(UserInformationColumn.USER_ID, Operator.EQUAL, event.getAuthor().getId());
									
									set.execute();
								}else{
									ActionInsert insert = JockieBot.getDatabase().insert(Database.USER_INFORMATION);
									insert.getInsert().insert(UserInformationColumn.USER_ID, event.getAuthor().getId()).insert(UserInformationColumn.COUNTRY, country.getAlpha3Code());
									
									insert.execute();
								}
								
								event.getChannel().sendMessage(event.getAuthor().getAsMention() + " your country has now been set to " + country.getName()).queue();
								return true;
							}
						}
						return false;
					}
				});
			}else{
				event.getChannel().sendMessage("No country found by that name").queue();
			}
		}else if(type.equals("BIO")) {
			if(value.length() <= 300) {
				ActionGet get = JockieBot.getDatabase().get(Database.USER_INFORMATION);
				get.getSelect().select(UserInformationColumn.USER_ID);
				get.getWhere().where(UserInformationColumn.USER_ID, Operator.EQUAL, event.getAuthor().getId());
				
				Result result = get.execute();
				if(result.next()) {
					ActionSet set = JockieBot.getDatabase().set(Database.USER_INFORMATION);
					set.getSet().set(UserInformationColumn.BIO, value);
					set.getWhere().where(UserInformationColumn.USER_ID, Operator.EQUAL, event.getAuthor().getId());
					
					set.execute();
				}else{
					ActionInsert insert = JockieBot.getDatabase().insert(Database.USER_INFORMATION);
					insert.getInsert().insert(UserInformationColumn.USER_ID, event.getAuthor().getId()).insert(UserInformationColumn.BIO, value);
					
					insert.execute();
				}
				
				event.getChannel().sendMessage(event.getAuthor().getAsMention() + " your biography has now been set to " + value).queue();
			}else event.getChannel().sendMessage(event.getAuthor().getAsMention() + " you may only have a biography with a maximum of 300 characters!").queue();
		}
	}
}