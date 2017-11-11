package com.jockie.bot.main;

import static net.dv8tion.jda.core.AccountType.BOT;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

public class Shard {
	
	private JDA jda;
	
	private JDABuilder builder;
	
	private int id;
	
	private ExecutorService command_executor = new ThreadPoolExecutor(2, 20, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	
	private ScheduledExecutorService scheduled_executor = Executors.newScheduledThreadPool(1);
	
	public Shard(int id) {
		this.id = id;
		
		this.builder = new JDABuilder(BOT);
		this.builder.setToken(JockieBot.getToken());
		this.builder.setAutoReconnect(true);
		this.builder.setAudioEnabled(false);
		this.builder.setBulkDeleteSplittingEnabled(false);
		this.builder.setGame(Game.of("Loading..."));
		if(JockieBot.getRecommendedShards() > 1)
			this.builder.useSharding(id, JockieBot.getRecommendedShards());
	}
	
	public ExecutorService getCommandExecutor() {
		return command_executor;
	}
	
	public ScheduledExecutorService getScheduledExecutorService() {
		return scheduled_executor;
	}
	
	public JDA getJDA() {
		return this.jda;
	}
	
	public int getId() {
		return id;
	}
	
	public void start() {
		try {
			System.out.println("Starting Shard with id " + this.getId());
			
			this.jda = this.builder.buildBlocking();
		}catch(LoginException | IllegalArgumentException | InterruptedException | RateLimitedException e) {
			e.printStackTrace();
		}
	}
	
	public void restart() {
		System.out.println("Restarting Shard with id " + this.getId());
		
		this.getJDA().removeEventListener(JockieBot.getEventListeners().toArray(new Object[0]));
		this.getJDA().shutdown();
		
		this.start();
	}
	
	public String toString() {
		return "JockieBot (Shard " + this.getJDA().getShardInfo().getShardString() + ")";
	}
}