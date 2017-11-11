package com.jockie.bot.APIs.osu;

import com.jockie.bot.APIs.osu.Osu.OsuMode;

import net.dv8tion.jda.core.EmbedBuilder;

public class OsuBeatmap {
	
	public enum Approved {
		GRAVE_YARD("Graveyard", "-2"),
		WIP("Work in progress", "-1"),
		PENDING("Pending", "0"),
		RANKED("Ranked", "1"),
		APPROVED("Approved", "2"),
		QUALIFIED("Qualified", "3"),
		LOVED("Loved", "4");
		
		private final String TEXT;
		private final String ID;
		
		private Approved(String text, String id) {
			this.TEXT = text;
			this.ID = id;
		}
		
		public String getText() {
			return this.TEXT;
		}
		
		public String getId() {
			return this.ID;
		}
		
		public static Approved fromId(String id) {
			for(Approved approved : Approved.values())
				if(approved.getId().equals(id))
					return approved;
			
			return null;
		}
	}
	
	public enum Genre {
		ANY("Any", "0"),
		UNSPECIFIED("Unspecified", "1"),
		VIDEO_GAME("Video game", "2"),
		ANIME("Anime", "3"),
		ROCK("Rock", "4"),
		POP("POP", "5"),
		OTHER("Other", "6"),
		NOVELTY("Novelty", "7"),
		HIP_HOP("Hip hop", "9"),
		ELECTRONIC("Electronic", "10");
		
		private final String TEXT;
		private final String ID;
		
		private Genre(String text, String id) {
			this.TEXT = text;
			this.ID = id;
		}
		
		public String getText() {
			return this.TEXT;
		}
		
		public String getId() {
			return this.ID;
		}
		
		public static Genre fromId(String id) {
			for(Genre genre : Genre.values())
				if(genre.getId().equals(id))
					return genre;
			
			return null;
		}
	}
	
	public enum Language {
		ANY("Any", "0"),
		OTHER("Other", "1"),
		ENGLISH("English", "2"),
		JAPANESE("Japanese", "3"),
		CHINESE("Chinese", "4"),
		INSTRUMENTAL("Instrumental", "5"),
		KOREAN("Korean", "6"),
		FRENCH("French", "7"),
		GERMAN("German", "8"),
		SWEDISH("Swedish", "9"),
		SPANISH("Spanish", "10"),
		ITALIAN("Italian", "11");
		
		private final String TEXT;
		private final String ID;
		
		private Language(String text, String id) {
			this.TEXT = text;
			this.ID = id;
		}
		
		public String getText() {
			return this.TEXT;
		}
		
		public String getId() {
			return this.ID;
		}
		
		public static Language fromId(String id) {
			for(Language language : Language.values())
				if(language.getId().equals(id))
					return language;
			
			return null;
		}
	}
	
	private Approved approved;
	
	public Approved getApproved() { return this.approved; }
	public void setApproved(String approved_id) { this.approved = Approved.fromId(approved_id); }
	
	private String approved_date;
	
	public String getApprovedDate() { return this.approved_date; }
	public void setApprovedDate(String approved_date) { this.approved_date = approved_date; }
	
	private String last_updated;
	
	public String getLastUpdated() { return this.last_updated; }
	public void setLastUpdated(String last_updated) { this.last_updated = last_updated; }
	
	private String artist;
	
	public String getArtist() { return this.artist; }
	public void setArtist(String artist) { this.artist = artist; }
	
	private String beatmap_id;
	
	public String getBeatmapId() { return this.beatmap_id; }
	public void setBeatmapId(String beatmap_id) { this.beatmap_id = beatmap_id; }
	
	private String beatmap_set_id;
	
	public String getBeatmapSetId() { return this.beatmap_set_id; }
	public void setBeatmapSetId(String beatmap_set_id) { this.beatmap_set_id = beatmap_set_id; }
	
	private String bpm;
	
	public String getBPM() { return this.bpm; }
	public void setBPM(String bpm) { this.bpm = bpm; }
	
	private String creator;
	
	public String getCreator() { return this.creator; }
	public void setCreator(String creator) { this.creator = creator; }
	
	private String difficulty_rating;
	
	public String getDifficultyRating() { return this.difficulty_rating; }
	public void setDifficultyRating(String difficulty_rating) { this.difficulty_rating = difficulty_rating; }
	
	private String difficulty_circle_size;
	
	public String getDifficultyCircleSize() { return this.difficulty_circle_size; }
	public void setDifficultyCircleSize(String difficulty_circle_size) { this.difficulty_circle_size = difficulty_circle_size; }
	
	private String difficulty_overall;
	
	public String getDifficultyOverall() { return this.difficulty_overall; }
	public void setDifficultyOverall(String difficulty_overall) { this.difficulty_overall = difficulty_overall; }
	
	private String difficulty_approach_rate;
	
	public String getDifficultyApproachRate() { return this.difficulty_approach_rate; }
	public void setDifficultyApproachRate(String difficulty_approach_rate) { this.difficulty_approach_rate = difficulty_approach_rate; }
	
	private String difficulty_health_drained;
	
	public String getDifficultyHealthDrained() { return this.difficulty_health_drained; }
	public void setDifficultyHealthDrained(String difficulty_health_drained) { this.difficulty_health_drained = difficulty_health_drained; }
	
	private String hit_length;
	
	public String getHitLength() { return this.hit_length; }
	public void setHitLength(String hit_length) { this.hit_length = hit_length; }
	
	private String source;
	
	public String getSource() { return this.source; }
	public void setSource(String source) { this.source = source; }
	
	private Genre genre;
	
	public Genre getGenre() { return this.genre; }
	public void setGenre(String genre_id) { this.genre = Genre.fromId(genre_id); }
	
	private Language language;
	
	public Language getLangugae() { return this.language; }
	public void setLanguage(String language_id) { this.language = Language.fromId(language_id); }
	
	private String title;
	
	public String getTitle() { return this.title; }
	public void setTitle(String title) { this.title = title; }
	
	private String total_length;
	
	public String getTotalLength() { return this.total_length; }
	public void setTotalLength(String total_length) { this.total_length = total_length; }
	
	private String difficulty_name;
	
	public String getDifficultyName() { return this.difficulty_name; }
	public void setDifficultyName(String difficulty_name) { this.difficulty_name = difficulty_name; }
	
	private String file_md5;
	
	public String getFileMD5() { return this.file_md5; }
	public void setFileMD5(String file_md5) { this.file_md5 = file_md5; }
	
	private OsuMode mode;
	
	public OsuMode getMode() { return this.mode; }
	public void setMode(int mode_id) { this.mode = OsuMode.fromId(mode_id); }
	
	private String tags;
	
	public String getTags() { return this.tags.replace(" ", ", "); }
	public void setTags(String tags) { this.tags = tags; }
	
	private String favourite_count;
	
	public String getFavouriteCount() { return this.favourite_count; }
	public void setFavouriteCount(String favourite_count) { this.favourite_count = favourite_count; }
	
	private String play_count;
	
	public String getPlayCount() { return this.play_count; }
	public void setPlayCount(String play_count) { this.play_count = play_count; }
	
	private String pass_count;
	
	public String getPassCount() { return this.pass_count; }
	public void setPassCount(String pass_count) { this.pass_count = pass_count; }
	
	private String max_combo;
	
	public String getMaxCombo() { return this.max_combo; }
	public void setMaxCombo(String max_combo) { this.max_combo = max_combo; }
	
	public EmbedBuilder getInformationEmbed() {
		EmbedBuilder embed_builder = new EmbedBuilder();
		
		embed_builder.addField("Beatmap Title", this.getTitle(), true);
		embed_builder.addField("Beatmap Id", this.getBeatmapId(), true);
		embed_builder.addField("Beatmap-Set Id", this.getBeatmapSetId(), true);
		
		return embed_builder;
	}
}