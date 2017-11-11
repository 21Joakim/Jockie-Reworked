package com.jockie.bot.APIs.osu;

import com.jockie.bot.APIs.osu.Osu.OsuMode;

import net.dv8tion.jda.core.EmbedBuilder;

public class OsuUser {
	
	private String user_id, username;
	
	public String getUserId() { return user_id; }
	public void setUserId(String user_id) { this.user_id = user_id; }
	
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	
	private String count300, count100, count50;
	
	public String getCount300() { return count300; }
	public void setCount300(String count300) { this.count300 = count300; }
	
	public String getCount100() { return count100; }
	public void setCount100(String count100) { this.count100 = count100; }
	
	public String getCount50() { return count50; }
	public void setCount50(String count50) { this.count50 = count50; }
	
	private String play_count;
	
	public String getPlayCount() { return play_count; }
	public void setPlayCount(String play_count) { this.play_count = play_count; }
	
	private String ranked_score, total_score;
	
	public String getRankedScore() { return ranked_score; }
	public void setRankedScore(String ranked_score) { this.ranked_score = ranked_score; }
	
	public String getTotalScore() { return total_score; }
	public void setTotalScore(String total_score) { this.total_score = total_score; }
	
	private String pp_country_rank, pp_rank, pp_raw;
	
	public String getPPCountryRank() { return pp_country_rank; }
	public void setPPCountryRank(String pp_country_rank) { this.pp_country_rank = pp_country_rank; }
	
	public String getPPRank() { return pp_rank; }
	public void setPPRank(String pp_rank) { this.pp_rank = pp_rank; }
	
	public String getPPRaw() { return pp_raw; }
	public void setPPRaw(String pp_raw) { this.pp_raw = pp_raw; }
	
	private String level;
	
	public String getLevel() { return level; }
	public void setLevel(String level) { this.level = level; }
	
	private String accuracy;
	
	public String getAccuracy() { return accuracy; }
	public void setAccuracy(String accuracy) { this.accuracy = accuracy; }
	
	private String count_rank_ss, count_rank_s, count_rank_a;
	
	public String getCountRankSS() { return count_rank_ss; }
	public void setCountRankSS(String count_rank_ss) { this.count_rank_ss = count_rank_ss; }
	
	public String getCountRankS() { return count_rank_s; }
	public void setCountRankS(String count_rank_s) { this.count_rank_s = count_rank_s; }
	
	public String getCountRankA() { return count_rank_a; }
	public void setCountRankA(String count_rank_a) { this.count_rank_a = count_rank_a; }
	
	private String country;
	
	public String getCountry() { return country; }
	public void setCountry(String country) { this.country = country; }
	
	private OsuMode mode;
	
	public OsuMode getMode() { return this.mode; }
	public void setMode(int mode_id) { this.mode = OsuMode.fromId(mode_id); }
	
	public EmbedBuilder getInformationEmbed() {
		EmbedBuilder embed_builder = new EmbedBuilder();
		
		embed_builder.setThumbnail("https://a.ppy.sh/" + this.getUserId());
		
		embed_builder.addField("Username", this.getUsername(), true);
		embed_builder.addField("User Id", this.getUserId(), true);
		embed_builder.addField("Country", ":flag_" + ((!this.getCountry().equals("")) ? this.getCountry().toLowerCase() : "white") + ":", true);
		embed_builder.addField("Rank Country", ((!this.getPPCountryRank().equals("N/A")) ? "#" : "") + this.getPPCountryRank(), true);
		embed_builder.addField("Rank Worldwide", ((!this.getPPRank().equals("N/A")) ? "#" : "") + this.getPPRank(), true);
		embed_builder.addField("PP", this.getPPRaw(), true);
		embed_builder.addField("Accuracy", this.getAccuracy() + ((!this.getAccuracy().equals("N/A")) ? "%" : ""), true);
		embed_builder.addField("Level", this.getLevel(), true);
		embed_builder.addField("Total Plays", this.getPlayCount(), true);
		embed_builder.addField("Total Score", this.getTotalScore(), true);
		embed_builder.addField("Ranked Score", this.getRankedScore(), true);
		
		embed_builder.addField("Total A rankings", this.getCountRankA(), true);
		embed_builder.addField("Total S rankings", this.getCountRankS(), true);
		embed_builder.addField("Total SS rankings", this.getCountRankSS(), true);
		
		embed_builder.addField("Mode", this.getMode().getText(), true);
		
		return embed_builder;
	}
}