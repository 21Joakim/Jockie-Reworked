package com.jockie.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jockie.bot.APIs.cache.CacheHandler;
import com.jockie.bot.APIs.cache.CountryCache;
import com.jockie.bot.APIs.cache.ProcessorCache;
import com.jockie.bot.command.utility.CommandReminder.Reminder;
import com.jockie.bot.main.JockieBot;
import com.jockie.bot.main.Shard;

import net.dv8tion.jda.core.JDA;

public class Storage {
	
	private static HashMap<JDA, ArrayList<Reminder>> reminders = new HashMap<JDA, ArrayList<Reminder>>();
	
	//Would not be in here
	private static CountryCache country_cache = new CountryCache();
	private static ProcessorCache processor_cache = new ProcessorCache();
	
	static {
		for(Shard shard : JockieBot.getShards()) {
			Storage.reminders.put(shard.getJDA(), new ArrayList<Reminder>());
		}
		
		CacheHandler.addCache(Storage.country_cache);
		CacheHandler.addCache(Storage.processor_cache);
	}
	
	public static void addReminder(JDA jda, Reminder reminder) {
		Storage.reminders.get(jda).add(reminder);
	}
	
	public static void removeReminder(JDA jda, Reminder reminder) {
		Storage.reminders.get(jda).remove(reminder);
	}
	
	public static List<Reminder> getReminders(JDA jda) {
		return new ArrayList<Reminder>(reminders.get(jda));
	}
	
	public static CountryCache getCountryCache() {
		return Storage.country_cache;
	}
	
	public static ProcessorCache getProcessorCache() {
		return Storage.processor_cache;
	}
}