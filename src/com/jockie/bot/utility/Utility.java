package com.jockie.bot.utility;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;
import java.util.function.Consumer;

import com.jockie.bot.main.JockieBot;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

public class Utility {
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * Modified Version of {@link java.util.Arrays#toString(Object[]) Arrays.toString(Object[])}
	 */
	public static String toString(Object[] a, String seperator) {
		if(a == null)
			return "";
		
		int max_i = a.length - 1;
		
		if(max_i == - 1)
			return "";
		
		StringBuilder b = new StringBuilder();
		for(int i = 0; ; i++) {
			b.append(String.valueOf(a[i]));
			if(i == max_i)
				return b.toString();
			b.append(seperator + " ");
		}
	}
	
	public static String toString(Object[] a) {
		return Utility.toString(a, ",");
	}
	
	public static DateTimeFormatter getDateTimeFormatter() {
		return Utility.formatter;
	}
	
	public static String randomNumber(int length) {
		String numbers = "1234567890";
		char[] randomNumber = new char[length];
		for(int i = 0; i < length; i++) 
			randomNumber[i] = numbers.charAt(new Random().nextInt(numbers.length()));
		return new String(randomNumber);
	}
	
	public static void getUser(JDA jda, Guild guild, String userId, Consumer<String> callback) {
		Member member = null;
		if(guild != null)
			member = guild.getMemberById(userId);
		if(member != null) {
			if(JockieBot.getGuildProperties().containsKey(guild.getId())) {
				if(JockieBot.getGuildProperties().get(guild.getId()).shouldMentionUsers()) {
					callback.accept(member.getUser().getAsMention());
					return;
				}else{
					callback.accept("**" + member.getUser().getName() + "#" + member.getUser().getDiscriminator() + "**");
					return;
				}
			}
			callback.accept(member.getUser().getAsMention());
		}else{
			jda.retrieveUserById(userId).queue(u -> { 
				callback.accept("**" + u.getName() + "#" + u.getDiscriminator() + "**");
			});
		}
	}
	
	public static void getUserName(JDA jda, String userId, Consumer<String> callback) {
		jda.retrieveUserById(userId).queue(u -> {
			callback.accept(u.getName() + "#" + u.getDiscriminator());
		});
	}
	
	public static BufferedImage resize(BufferedImage image, int width, int height) {
		if(image != null) {
			if(image.getWidth() != width && image.getHeight() != height) {
				try {
					Image temp = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
					image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
					Graphics2D g = image.createGraphics();
					g.drawImage(temp, 0, 0, null);
					g.dispose();
					return image;
				}catch(Exception e) {
					e.printStackTrace();
					return null;
				}
			}else{
				return image;
			}
		}else{
			return null;
		}
	}
	
	public static Color getMostCommonColour(BufferedImage image) {
		CountingArray<Integer> rgb = new CountingArray<>();
		
		for(int y = 0; y < image.getHeight(); y++)
			for(int x = 0; x < image.getWidth(); x++)
				rgb.add(image.getRGB(x, y));
		
		return new Color(rgb.getMostPopularEntry());
	}
	
	public static String formattedUptime() {
		LocalDateTime started = LocalDateTime.parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(JockieBot.date_time_started), formatter);
		LocalDateTime now = LocalDateTime.parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), formatter);

		long seconds = Duration.between(started, now).getSeconds();
		
		return Utility.formattedTime(seconds);
	}
	
	public static String formattedTime(long seconds) {
		String str = "";
		
		long days = 0;
		long hours = 0;
		long minutes = 0;

		if(seconds > 0) {
			if(seconds/86400 >= 0) {
				while(seconds >= 86400) {
					seconds -= 86400;
					days++;
				}
			}
			
			if(seconds/3600 >= 0) {
				while(seconds >= 3600) {
					seconds -= 3600;
					hours++;
				}
			}
			
			if(seconds/60 >= 0) {
				while(seconds >= 60) {
					seconds -= 60;
					minutes++;
				}
			}

			if(days > 0) {
				str += "**" + days + "** day";
				if(days != 1) 
					str = str + "s";
				str = str + " ";
			}
			
			if(hours > 0) {
				str += "**" + hours + "** hour";
				if(hours != 1) 
					str = str + "s";
				str = str + " ";
			}
			
			if(minutes > 0) {
				str += "**" + minutes + "** minute";
				if(minutes != 1) 
					str = str + "s";
				str = str + " ";
			}
			
			if(seconds > 0) {
				str += "**" + seconds + "** second";
				if(seconds != 1) 
					str = str + "s";
				str = str + " ";
			}
		}else{
			str += "**" + seconds + "** second";
			if(seconds != 1) 
				str = str + "s";
		}
		
		return str.trim();
	}
}