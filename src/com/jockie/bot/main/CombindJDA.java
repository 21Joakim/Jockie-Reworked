package com.jockie.bot.main;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class CombindJDA {
	
	public static Stream<JDA> getJDAs() {
		return JockieBot.getShards().stream().map(Shard::getJDA);
	}
	
	public static List<Guild> getGuilds() {
		return CombindJDA.getJDAs().map(JDA::getGuilds).flatMap(List::stream).distinct().collect(Collectors.toList());
	}
	
	public static List<User> getUsers() {
		return CombindJDA.getJDAs().map(JDA::getUsers).flatMap(List::stream).distinct().collect(Collectors.toList());
	}
	
	public static List<TextChannel> getTextChannels() {
		return CombindJDA.getJDAs().map(JDA::getTextChannels).flatMap(List::stream).distinct().collect(Collectors.toList());
	}
	
	public static List<VoiceChannel> getVoiceChannels() {
		return CombindJDA.getJDAs().map(JDA::getVoiceChannels).flatMap(List::stream).distinct().collect(Collectors.toList());
	}
}