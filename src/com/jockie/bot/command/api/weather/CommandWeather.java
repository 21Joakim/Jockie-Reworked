package com.jockie.bot.command.api.weather;

import java.awt.Color;
import java.util.ArrayList;

import com.jockie.bot.APIs.weather.APIWeather;
import com.jockie.bot.APIs.weather.Weather;
import com.jockie.bot.APIs.weather.WeatherLocation;
import com.jockie.bot.command.core.impl.Arguments.ArgumentString;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue;
import com.jockie.bot.command.core.impl.Arguments.ArgumentTypeValue.ArgumentEntry;
import com.jockie.bot.command.core.impl.CommandImpl;
import com.jockie.bot.command.core.impl.CommandListener;
import com.jockie.bot.command.core.non_command.ExecutableNonCommandTriggerPoint;
import com.jockie.bot.command.core.non_command.PagedResult;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandWeather extends CommandImpl {

	public CommandWeather() {
		super("weather", 
			new ArgumentTypeValue("Measurement system", new ArgumentEntry("Metric (Celsius)", "METRIC", "METRIC"), new ArgumentEntry("Imperial (Fahrenheit)", "IMPERIAL", "IMPERIAL")), 
			new ArgumentString("Name of location")
		);
		super.setCommandDescription("Get weather data");
	}
	
	public void execute(MessageReceivedEvent event, String prefix, Object... arguments) {
		String location_name = (String) arguments[1];
		
		ArrayList<WeatherLocation> weather_locations = APIWeather.getLocationsByName(location_name, (String) arguments[0]);
		if(weather_locations.size() > 1) {
			PagedResult<WeatherLocation> paged_result_weather = new PagedResult<WeatherLocation>(weather_locations, WeatherLocation::getLocationName);
			
			CommandListener.doPagedResult(event, this, paged_result_weather, new ExecutableNonCommandTriggerPoint(this.getCommand(), paged_result_weather) {
				public boolean execute(MessageReceivedEvent event) {
					int number = -1;
					try {
						number = Integer.parseInt(event.getMessage().getRawContent());
					}catch(Exception e) {}
					
					if(number != -1) {
						PagedResult<?> weather_locations = (PagedResult<?>) this.getObject();
						
						if(number > 0 && number <= weather_locations.getCurrentPageEntries().size()) {
							WeatherLocation location = (WeatherLocation) weather_locations.getCurrentPageEntries().get(number - 1);
							Weather weather = APIWeather.getWeatherFromLocation(location.getLocationCode(), location.getMeasurementSystem());
							if(weather != null) {
								EmbedBuilder embed_builder = weather.getInformationEmbed();
								
								embed_builder.setColor(Color.cyan);
								
								event.getChannel().sendMessage(embed_builder.build()).queue();
								
								return true;
							}else{
								event.getChannel().sendMessage("Something went wrong!").queue();
							}
						}
					}
					return false;
				}
			});
		}else if(weather_locations.size() == 1) {
			Weather weather = APIWeather.getWeatherFromLocation(weather_locations.get(0).getLocationCode(), weather_locations.get(0).getMeasurementSystem());
			if(weather != null) {
				EmbedBuilder embed_builder = weather.getInformationEmbed();
				
				embed_builder.setColor(Color.cyan);
				
				event.getChannel().sendMessage(embed_builder.build()).queue();
			}else{
				event.getChannel().sendMessage("Something went wrong!").queue();
			}
		}else{
			event.getChannel().sendMessage("No location found").queue();
		}
	}
}