package com.jockie.bot.main;

import java.net.InetSocketAddress;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.jockie.bot.Statistics;
import com.jockie.bot.Storage;
import com.jockie.bot.APIs.APIHelper;
import com.jockie.bot.command.api.country.CommandCountry;
import com.jockie.bot.command.api.currency.CommandCurrencyConvert;
import com.jockie.bot.command.api.currency.CommandCurrencyLatest;
import com.jockie.bot.command.api.intel.CommandCompareProcessors;
import com.jockie.bot.command.api.intel.CommandProcessors;
import com.jockie.bot.command.api.osu.CommandOsuBeatmap;
import com.jockie.bot.command.api.osu.CommandOsuBestPlays;
import com.jockie.bot.command.api.osu.CommandOsuRecentPlays;
import com.jockie.bot.command.api.osu.CommandOsuUser;
import com.jockie.bot.command.api.osu.CommandOsuUserMention;
import com.jockie.bot.command.api.weather.CommandWeather;
import com.jockie.bot.command.core.Command;
import com.jockie.bot.command.core.impl.CommandListener;
import com.jockie.bot.command.core.impl.CommandManager;
import com.jockie.bot.command.core.impl.CommandManager.Authority;
import com.jockie.bot.command.debug.CommandApplyBeta;
import com.jockie.bot.command.debug.CommandReport;
import com.jockie.bot.command.debug.CommandRequest;
import com.jockie.bot.command.developer.CommandSetAuthority;
import com.jockie.bot.command.developer.CommandSetServerSettings;
import com.jockie.bot.command.information.CommandBirthday;
import com.jockie.bot.command.information.CommandBotinfo;
import com.jockie.bot.command.information.CommandProfile;
import com.jockie.bot.command.information.CommandProviders;
import com.jockie.bot.command.information.CommandServerinfo;
import com.jockie.bot.command.information.CommandStatistics;
import com.jockie.bot.command.information.CommandUpcomingBirthdays;
import com.jockie.bot.command.information.CommandUserinfo;
import com.jockie.bot.command.intro.CommandHelp;
import com.jockie.bot.command.intro.CommandNew;
import com.jockie.bot.command.intro.CommandTermsOfService;
import com.jockie.bot.command.marriage.CommandAccept;
import com.jockie.bot.command.marriage.CommandCancel;
import com.jockie.bot.command.marriage.CommandDecline;
import com.jockie.bot.command.marriage.CommandDivorce;
import com.jockie.bot.command.marriage.CommandMarried;
import com.jockie.bot.command.marriage.CommandMinfo;
import com.jockie.bot.command.marriage.CommandPropose;
import com.jockie.bot.command.other.CommandBanner;
import com.jockie.bot.command.other.CommandQuote;
import com.jockie.bot.command.set.CommandSet;
import com.jockie.bot.command.set.CommandSetAuthorized;
import com.jockie.bot.command.utility.CommandBase64;
import com.jockie.bot.command.utility.CommandHasRole;
import com.jockie.bot.command.utility.CommandHash;
import com.jockie.bot.command.utility.CommandPermissions;
import com.jockie.bot.command.utility.CommandReminder;
import com.jockie.bot.command.utility.CommandReminder.Reminder;
import com.jockie.bot.command.utility.CommandRemoveReminder;
import com.jockie.bot.command.utility.CommandRetrieveMessages;
import com.jockie.bot.command.utility.CommandReverse;
import com.jockie.bot.command.utility.CommandScrambleCase;
import com.jockie.bot.command.utility.CommandWords;
import com.jockie.bot.database.Database;
import com.jockie.bot.database.column.GuildColumn;
import com.jockie.bot.database.column.UserInformationColumn;
import com.jockie.bot.events.GeneralEvents;
import com.jockie.bot.safe.Safe;
import com.jockie.sql.JockieDatabase;
import com.jockie.sql.action.ActionGet;
import com.jockie.sql.action.ActionInsert;
import com.jockie.sql.base.GlobalColumn;
import com.jockie.sql.base.Result;
import com.jockie.sql.base.Row;
import com.jockie.sql.base.Where.Operator;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.hooks.EventListener;

public class JockieBot {
	
	private static String token = Safe.TOKEN;
	
	private static int total_shards = JockieBot.getRecommendedShards();
	
	private static List<Shard> shards = new ArrayList<Shard>();
	private static List<EventListener> event_listeners = new ArrayList<EventListener>();
	
	private static HashMap<String, GuildProperties> guilds_properties = new HashMap<String, GuildProperties>();
	
	private static JockieDatabase database = new JockieDatabase();
	
	public static final long BOT_OWNER_ID = 190551803669118976L;
	public static User BOT_OWNER_USER;
	
	public static final String VERSION = "0.18.6";
	
	public static Date date_time_started;
	
	public static final String FILE_STORAGE_PATH = System.getProperty("user.dir") + "/files/";
	
	public static void main(String[] args) {
		/*new GUI();*/
		JockieBot.startBot();
	}
	
	public static void startBot() {
		JockieBot.init();
		JockieBot.start();
		JockieBot.postInit();
		
		System.out.println("Total Guilds : " + CombindJDA.getGuilds().size());
		
		System.out.println("Total Users : " + CombindJDA.getUsers().size());
		
		System.out.println("Total Text Channels : " + CombindJDA.getTextChannels().size());
		
		System.out.println("Total Voice Channels : " + CombindJDA.getVoiceChannels().size());
	}
	
	public static void init() {
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("GMT+0")));
		
		JockieBot.event_listeners.add(new CommandListener());
		JockieBot.event_listeners.add(new GeneralEvents());
		
		JockieBot.getDatabase().setup(
			JockieDatabase.getDefaultConfig(
				new InetSocketAddress(Safe.DATABASE_IP, Safe.DATABASE_PORT), false, Safe.DATABASE_NAME, Safe.DATABASE_USERNAME, Safe.DATABASE_PASSWORD));
		
		CommandManager command_manager = CommandListener.getCommandManager();
		
		ActionGet get = JockieBot.getDatabase().get(Database.USER_INFORMATION);
		get.getSelect().select(UserInformationColumn.USER_ID).select(UserInformationColumn.AUTHORITY);
		get.getWhere().not().where(UserInformationColumn.AUTHORITY, Operator.EQUAL, Authority.NONE.toString());
		
		Result result = get.execute();
		if(result.next()) {
			ArrayList<Row> rows = result.getRows();
			for(int i = 0; i < rows.size(); i++) {
				command_manager.addAuthorithy((String) rows.get(i).getColumn(UserInformationColumn.USER_ID.getValue()), Authority.valueOf((String) rows.get(i).getColumn(UserInformationColumn.AUTHORITY.getValue())));
			}
		}
		
		command_manager.addCommands(new CommandBotinfo(), new CommandServerinfo(), new CommandUserinfo(), new CommandProfile(), new CommandProviders());
		command_manager.addCommands(new CommandMarried(), new CommandMinfo(), new CommandDivorce(), new CommandPropose(), new CommandCancel(), new CommandDecline(), new CommandAccept());
		command_manager.addCommands(new CommandOsuUser(), new CommandOsuUserMention(), new CommandOsuBeatmap(), new CommandOsuRecentPlays(), new CommandOsuBestPlays());
		command_manager.addCommands(new CommandProcessors(), new CommandCompareProcessors(), new CommandWeather(), new CommandCountry(), new CommandQuote(), new CommandCurrencyLatest(), new CommandCurrencyConvert());
		command_manager.addCommands(new CommandReverse(), new CommandScrambleCase(), new CommandBase64(), new CommandHash());
		command_manager.addCommands(new CommandHelp(), new CommandNew(), new CommandBanner());
		command_manager.addCommands(new CommandSet(), new CommandSetAuthorized(), new CommandSetAuthority(), new CommandSetServerSettings());
		command_manager.addCommands(new CommandHasRole(), new CommandPermissions(), new CommandRetrieveMessages(), new CommandWords());
		command_manager.addCommands(new CommandReminder(), new CommandRemoveReminder(), new CommandBirthday(), new CommandUpcomingBirthdays());
		
		command_manager.addCommands(new CommandApplyBeta(), new CommandReport(), new CommandRequest());
		
		command_manager.addCommand(new CommandStatistics());
		command_manager.addCommand(new CommandTermsOfService());
		
		for(Command command : command_manager.getCommands())
			System.out.println(command);
		
		JockieBot.date_time_started = new Date();
	}
	
	public static void start() {
		System.out.println("Recommended Shards : " + JockieBot.getRecommendedShards());
		for(int i = 0; i < total_shards; i++) {
			Shard shard = new Shard(i);
			JockieBot.shards.add(shard);
			
			shard.start();
			
			System.out.println("Shard ID " + shard.getId() + " | Guilds " + shard.getJDA().getGuilds().size());
			
			shard.getJDA().getPresence().setGame(Game.of("m!new for information"));
		}
	}
	
	public static void postInit() {
		JockieBot.BOT_OWNER_USER = getShards().get(0).getJDA().retrieveUserById(BOT_OWNER_ID).complete();
		
		//I am calling these for the static initialization to happen, could be replaced with a .init() method
		Storage.getCountryCache();
		Statistics.getTotalSuccessfulCommands();
		
		for(Guild guild : CombindJDA.getGuilds()) {
			GuildProperties guild_properties = new GuildProperties();
			
			ActionGet get = JockieBot.getDatabase().get(Database.GUILD);
			get.getSelect().select(GlobalColumn.ALL);
			get.getWhere().where(GuildColumn.GUILD_ID, Operator.EQUAL, guild.getId());
			
			Result result = get.execute();
			
			result.next();
			
			if(result.getRows().size() > 0) {
				Row row = result.getRows().get(0);
				
				guild_properties.setPrefix((String) row.getColumn(GuildColumn.PREFIX.getValue()));
				guild_properties.setMentionUsers(Boolean.parseBoolean((String) row.getColumn(GuildColumn.MENTION_USERS.getValue())));
				guild_properties.setBetaServer(Boolean.parseBoolean((String) row.getColumn(GuildColumn.BETA_SERVER.getValue())));
				guild_properties.setAutoRole((String) row.getColumn(GuildColumn.AUTO_ROLE.getValue()));
				
				JockieBot.guilds_properties.put(guild.getId(), guild_properties);
			}else{
				guild_properties.setPrefix(CommandListener.getDefaultPrefix());
				guild_properties.setMentionUsers(true);
				guild_properties.setBetaServer(false);
				guild_properties.setAutoRole(null);
				
				JockieBot.guilds_properties.put(guild.getId(), guild_properties);
				
				ActionInsert insert = JockieBot.getDatabase().insert(Database.GUILD);
				insert.getInsert()
					.insert(GuildColumn.GUILD_ID, guild.getId())
					.insert(GuildColumn.PREFIX, guild_properties.getPrefix())
					.insert(GuildColumn.MENTION_USERS, ((Boolean) guild_properties.shouldMentionUsers()).toString())
					.insert(GuildColumn.BETA_SERVER, ((Boolean) guild_properties.isBetaServer()).toString())
					.insert(GuildColumn.AUTO_ROLE, (String) guild_properties.getAutoRole());
				insert.execute();
			}
		}
		
		for(Shard shard : JockieBot.getShards()) {
			shard.getScheduledExecutorService().scheduleAtFixedRate(new Runnable() {
				public void run() {
					for(int i = 0; i < Storage.getReminders(shard.getJDA()).size(); i++) {
						Reminder reminder = Storage.getReminders(shard.getJDA()).get(i);
						
						OffsetDateTime now = OffsetDateTime.now(ZoneId.of("GMT+0"));
						if(now.isEqual(reminder.getTimeFinished()) || now.isAfter(reminder.getTimeFinished())) {
							try {
								Storage.removeReminder(shard.getJDA(), reminder);
								
								shard.getJDA().retrieveUserById(reminder.getAuthor()).queue(user -> {
									Message message = new MessageBuilder().append(":warning: Reminder :warning:\n" + user.getAsMention() + "\n\n").appendCodeBlock(reminder.getMessage(), "text").build();
									
									MessageChannel channel = shard.getJDA().getTextChannelById(reminder.getChannel());
									if(channel != null)
										channel.sendMessage(message).queue();
									else {
										user.openPrivateChannel().queue(private_channel -> {
											private_channel.sendMessage(message).queue();
										});
									}
								});
							}catch(Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}, 0, 1, TimeUnit.SECONDS);
		}
		
		CombindJDA.getJDAs().forEach(JDA -> JDA.addEventListener(JockieBot.event_listeners.toArray(new Object[0])));
	}
	
	public static List<Shard> getShards() {
		return Collections.unmodifiableList(JockieBot.shards);
	}
	
	public static Shard getShardById(int id) {
		for(Shard shard : JockieBot.getShards()) {
			if(shard.getId() == id) {
				return shard;
			}
		}
		return null;
	}
	
	public static List<EventListener> getEventListeners() {
		return Collections.unmodifiableList(JockieBot.event_listeners);
	}
	
	public static String getToken() {
		return JockieBot.token;
	}
	
	public static JockieDatabase getDatabase() {
		return JockieBot.database;
	}
	
	public static HashMap<String, GuildProperties> getGuildProperties() {
		return JockieBot.guilds_properties;
	}
	
	public static void addGuildProperties(String guild_id, GuildProperties guild_properties) {
		JockieBot.guilds_properties.put(guild_id, guild_properties);
	}
	
	public static void removeGuildProperties(String guild_id) {
		if(JockieBot.guilds_properties.containsKey(guild_id))
			JockieBot.guilds_properties.remove(guild_id);
	}
	
	public static int getRecommendedShards() {
		if(JockieBot.total_shards == 0) {
			HttpGet get = new HttpGet("https://discordapp.com/api/gateway/bot");
			get.setHeader("Authorization", "Bot " + JockieBot.getToken());
			get.setHeader("Content-Type", "application/json");
			
			JSONTokener tokener = APIHelper.getJSON(get);
			
			if(tokener != null) {
				JockieBot.total_shards =  new JSONObject(tokener).getInt("shards");
			}else{
				throw new IllegalStateException("Something went wrong, could not get the recommended shards");
			}
		}
		
		return JockieBot.total_shards;
	}
}