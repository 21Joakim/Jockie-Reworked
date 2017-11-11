package com.jockie.bot.APIs.cache;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CacheHandler {
	
	private static ScheduledExecutorService exectuor = Executors.newScheduledThreadPool(1);
	
	private static ArrayList<DataCache> caches = new ArrayList<DataCache>();
	
	static {
		long delay = (LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(0, 0)).toEpochSecond(ZoneOffset.of("+0")) - LocalDateTime.now().toEpochSecond(ZoneOffset.of("+0"))) + 10;
		
		System.out.println("Seconds till next expire-check of the caches : " + delay);
		
		CacheHandler.exectuor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				for(int i = 0; i < caches.size(); i++) {
					DataCache cache = caches.get(i);
					if(LocalDateTime.now().toEpochSecond(ZoneOffset.of("+0")) >= cache.getExpires().atTime(0, 0).toEpochSecond(ZoneOffset.of("+0"))) {
						System.out.println("Updating cache : " + cache.getAPIName());
						cache.updateData();
					}
				}
			}
		}, delay, 86000, TimeUnit.SECONDS);
	}
	
	public static void addCache(DataCache cache) {
		CacheHandler.caches.add(cache);
	}
	
	public static void removeCache(DataCache cache) {
		CacheHandler.caches.remove(cache);
	}
}