package com.jockie.bot.command.core.non_command;

import java.awt.Color;
import java.util.List;
import java.util.function.Function;

import net.dv8tion.jda.core.EmbedBuilder;

public class PagedResult<T> {
	
	private int current_page = 1;
	private int entries_per_page;
	
	private int max_pages;
	
	private List<? extends T> entries;
	
	private String function_string;
	
	private Function<T, Object>[] entry_display_function;
	
	private boolean list_indexes = true;
	
	private Color default_colour;
	
	@SafeVarargs
	public PagedResult(List<? extends T> entries, int entries_per_page, boolean list_indexes, String function_string, Function<T, Object>... entry_display_functions) {
		this.entries = entries;
		this.entries_per_page = entries_per_page;
		this.list_indexes = list_indexes;
		this.function_string = function_string;
		this.entry_display_function = entry_display_functions;
		
		this.max_pages = this.calcMaxPages();
		
		int total_insertable_functions = 0;
		
		for(int i = 0; i < function_string.length(); i++) {
			if(i < function_string.length() - 1)
				if(function_string.substring(i, i + 2).equals("%s"))
					total_insertable_functions = total_insertable_functions + 1;
		}
		
		if(total_insertable_functions != entry_display_functions.length)
			throw new IllegalStateException("The function string does not have the correct amount of %s (Current : " + total_insertable_functions + ", Needed : " + entry_display_functions.length + ")");
	}
	
	@SafeVarargs
	public PagedResult(List<? extends T> entries, String function_string, Function<T, Object>... entry_display_function) {
		this(entries, 10, true, function_string, entry_display_function);
	}
	
	public PagedResult(List<? extends T> entries, int entries_per_page, Function<T, Object> entry_display_function) {
		this(entries, 10, true, "%s", entry_display_function);
	}
	
	public PagedResult(List<? extends T> entries, Function<T, Object> entry_display_function) {
		this(entries, 10, true, "%s", entry_display_function);
	}
	
	public PagedResult<T> setDefaultColour(Color default_color) {
		this.default_colour = default_color;
		return this;
	}
	
	private int calcMaxPages() {
		return (int) Math.ceil((double) this.entries.size()/(double) this.entries_per_page);
	}
	
	public int getMaxPages() {
		return this.max_pages;
	}
	
	public int getCurrentPage() {
		return this.current_page;
	}
	
	public int getEntriesPerPage() {
		return this.entries_per_page;
	}
	
	public List<? extends T> getCurrentPageEntries() {
		int start = (this.current_page - 1) * this.entries_per_page;
		int end;
		
		if(this.current_page == max_pages)
			end = this.entries.size() - start;
		else end = this.entries_per_page;
		
		return this.entries.subList(start, start + end); 
	}
	
	public EmbedBuilder getPageAsEmbed() {
		EmbedBuilder embed_builder = new EmbedBuilder();
		
		List<? extends T> entries = getCurrentPageEntries();
		
		embed_builder.appendDescription("Page **" + this.current_page + "**/**" + this.max_pages + "**\n");
		
		for(int i = 0; i < entries.size(); i++) {
			String[] f_strs = new String[this.entry_display_function.length];
			for(int j = 0; j < f_strs.length; j++)
				f_strs[j] = this.entry_display_function[j].apply(entries.get(i)).toString();
			
			embed_builder.appendDescription("\n" + ((this.list_indexes) ? (i + 1) + " - " : "") + "*" + String.format(this.function_string, (Object[]) f_strs) + "*");
		}
		
		String footer = "";
		
		if(this.current_page + 1 <= this.max_pages)
			footer = footer + "next page | ";
		
		if(this.current_page - 1 > 0)
			footer = footer + "previous page | ";
		
		if(this.max_pages > 1)
			footer = footer + "go to page | ";
		
		footer = footer + "cancel";
		
		embed_builder.setFooter(footer, null);
		
		if(this.default_colour != null)
			embed_builder.setColor(this.default_colour);
		
		return embed_builder;
	}
	
	public boolean setPage(int page) {
		if(page > this.max_pages)
			return false;
		
		if(page < 1)
			return false;
		
		this.current_page = page;
		
		return true;
	}
	
	public boolean nextPage() {
		if(this.current_page + 1 > this.max_pages)
			return false;
		
		this.current_page = this.current_page + 1;
		
		return true;
	}
	
	public boolean previousPage() {
		if(this.current_page - 1 < 1)
			return false;
		
		this.current_page = this.current_page - 1;
		
		return true;
	}
}