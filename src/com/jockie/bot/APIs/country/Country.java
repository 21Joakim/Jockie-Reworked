package com.jockie.bot.APIs.country;

import java.util.HashMap;

import com.jockie.bot.utility.Utility;

import net.dv8tion.jda.core.EmbedBuilder;

public class Country {
	
	private String name, nativeName, capital;
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getNativeName() { return nativeName; }
	public void setNativeName(String nativeName) { this.nativeName = nativeName; }
	
	public String getCapital() { return capital; }
	public void setCapital(String capital) { this.capital = capital; }
	
	private String[] topLevelDomains;
	
	public String[] getTopLevelDomains() { return topLevelDomains; }
	public void setTopLevelDomain(String[] topLevelDomains) { this.topLevelDomains = topLevelDomains; }
	
	private String alpha2Code, alpha3Code;
	
	public String getAlpha2Code() { return alpha2Code; }
	public void setAlpha2Code(String alpha2Code) { this.alpha2Code = alpha2Code; }
	
	public String getAlpha3Code() { return alpha3Code; }
	public void setAlpha3Code(String alpha3Code) { this.alpha3Code = alpha3Code; }
	
	private String region, subregion;
	
	public String getRegion() { return region; }
	public void setRegion(String region) { this.region = region; }
	
	public String getSubregion() { return subregion; }
	public void setSubregion(String subregion) { this.subregion = subregion; }
	
	private String demonym;
	
	public String getDemonym() { return demonym; }
	public void setDemonym(String demonym) { this.demonym = demonym; }
	
	private String population;
	
	public String getPopulation() { return population; }
	public void setPopulation(String population) { this.population = population; }
	
	private String area;
	
	public String getArea() { return area; }
	public void setArea(String area) { this.area = area; }
	
	private String gini;
	
	public String getGini() { return gini; }
	public void setGini(String gini) { this.gini = gini; }
	
	private String numericCode;
	
	public String getNumericCode() { return numericCode; }
	public void setNumericCode(String numericCode) { this.numericCode = numericCode; }

	private String[] latLng;
	
	public String[] getLatLng() { return latLng; }
	public void setLatLng(String[] latLng) { this.latLng = latLng; }
	
	private String[] callingCodes;
	
	public String[] getCallingCodes() { return callingCodes; }
	public void setCallingCodes(String[] callingCodes) { this.callingCodes = callingCodes; }
	
	private String[] altSpellings;
	
	public String[] getAltSpellings() { return altSpellings; }
	public void setAltSpellings(String[] altSpellings) { this.altSpellings = altSpellings; }
	
	private String[] timezones;
	
	public String[] getTimezones() { return timezones; }
	public void setTimezones(String[] timezones) { this.timezones = timezones; }
	
	private String[] borders;
	
	public String[] getBorders() { return borders; }
	public void setBorders(String[] borders) { this.borders = borders; }
	
	private Currency[] currencies;
	
	public Currency[] getCurrencies() { return currencies; }
	public void setCurrencies(Currency[] currencies) { this.currencies = currencies; }
	
	private Language[] languages;
	
	public Language[] getLanguages() { return languages; }
	public void setLanguages(Language[] languages) { this.languages = languages; }
	
	private HashMap<String, String> translations = new HashMap<String, String>();
	
	public String getTranslation(String languageAlpha2Code) { return translations.get(languageAlpha2Code); }
	public void addTranslation(String languageAlpha2Code, String translation) { translations.put(languageAlpha2Code, translation); }
	
	private RegionalBloc[] regionalBlocs;
	
	public RegionalBloc[] getRegionalBlocs() { return regionalBlocs; }
	public void setRegionalBlocs(RegionalBloc[] regionalBlocs) { this.regionalBlocs = regionalBlocs; }
	
	public EmbedBuilder getInformationEmbed() {
		EmbedBuilder embed_builder = new EmbedBuilder();
		String information = "";
		
		embed_builder.setTitle(this.getName() + ((!this.getAlpha2Code().equals("N/A")) ? " :flag_" + this.getAlpha2Code().toLowerCase() + ":" : ""));
		
		information = information + "Native name : " + this.getNativeName() + "\n";
		information = information + "Alternative Spellings : " + ((this.getAltSpellings().length > 0) ? Utility.toString(this.getAltSpellings()) : "N/A") + "\n\n";
		
		information = information + "Capital : " + this.getCapital() + "\n";
		information = information + "Region : " + this.getRegion() + ", " + this.getSubregion() + "\n";
		information = information + "Area : " + ((!this.getArea().equals("N/A")) ? this.getArea() + " km²" : this.getArea()) + "\n";
		information = information + "Population : " + ((!this.getPopulation().equals("N/A")) ? this.getPopulation() + " human beings" : this.getPopulation()) + "\n";
		information = information + "Lat, Lng : " + ((this.getLatLng().length == 2) ? this.getLatLng()[0] + ", " + this.getLatLng()[1] : "N/A") + "\n\n";
		
		information = information + "Alpha2&3 Codes : " + this.getAlpha2Code() + ", " + this.getAlpha3Code() + "\n";
		information = information + "Numeric Code : " + this.getNumericCode() + "\n";
		information = information + "Domain Code(s) : " + ((this.getTopLevelDomains().length > 0) ? Utility.toString(this.getTopLevelDomains()) : "N/A") + "\n";
		information = information + "Calling Code(s) : " + ((this.getCallingCodes().length > 0) ? Utility.toString(this.getCallingCodes()) : "N/A") + "\n\n";
		
		information = information + "Demonym : " + this.getDemonym() + "\n";
		information = information + "Gini : " + ((!this.getGini().equals("N/A")) ? this.getGini() : this.getGini()) + "\n\n";
		
		information = information + "Borders to : " + ((this.getBorders().length > 0) ? Utility.toString(this.getBorders()) : "Does not border to any countries") + "\n";
		information = information + "Timezone(s) : " + ((this.getTimezones().length > 0) ? Utility.toString(this.getTimezones()) : "N/A") + "\n";
		
		if(this.getCurrencies().length > 0) {
			information = information + "\nCurrency(s) :";
			for(Country.Currency currency : this.getCurrencies()) {
				information = information + "\n    Name : " + currency.getName() + "\n";
				information = information + "    Code : " + currency.getCode() + "\n";
				information = information + "    Symbol : " + currency.getSymbol() + "\n";
			}
		}
		
		if(this.getLanguages().length > 0) {
			information = information + "\nLanguage(s) :";
			for(Country.Language language : this.getLanguages()) {
				information = information + "\n    Name : " + language.getName() + "\n";
				information = information + "    Native Name : " + language.getNativeName() + "\n";
			}
		}
		
		//country.getTranslation("")
		//country.getRegionalBlocs()
		
		embed_builder.setDescription(information);
		
		return embed_builder;
	}
	
	public static class Currency {
		private String code;
		
		public String getCode() { return code; }
		public void setCode(String code) { this.code = code; }
		
		private String name;
		
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
		
		private String symbol;
		
		public String getSymbol() { return symbol; }
		public void setSymbol(String symbol) { this.symbol = symbol; }
	}
	
	public static class Language {
		private String iso639_1, iso639_2;
		
		public String getIso639_1() { return iso639_1; }
		public void setIso639_1(String iso639_1) { this.iso639_1 = iso639_1; }
		
		public String getIso639_2() { return iso639_2; }
		public void setIso639_2(String iso639_2) { this.iso639_2 = iso639_2; }
		
		private String name, nativeName;
		
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
		
		public String getNativeName() { return nativeName; }
		public void setNativeName(String nativeName) { this.nativeName = nativeName; }
	}
	
	public static class RegionalBloc {
		private String acronym, name;
		
		public String getAcronym() { return acronym; }
		public void setAcronym(String acronym) { this.acronym = acronym; }
		
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
		
		private String[] otherAcronyms, otherNames;
		
		public String[] getOtherAcronyms() { return otherAcronyms; }
		public void setOtherAcronyms(String[] otherAcronyms) { this.otherAcronyms = otherAcronyms; }
		
		public String[] getOtherNames() { return otherNames; }
		public void setOtherNames(String[] otherNames) { this.otherNames = otherNames; }
	}
}