package com.jockie.bot.APIs.country;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.jockie.bot.Storage;
import com.jockie.bot.APIs.country.Country.Currency;
import com.jockie.bot.APIs.country.Country.Language;
import com.jockie.bot.APIs.country.Country.RegionalBloc;

public class APICountry {
	
	private static String[] translatedLanguages = {
		"de",
		"es",
		"fr",
		"ja",
		"it",
		"br",
		"pt"
	};
	
	public static ArrayList<Country> fromJson(JSONTokener tokener) {
		ArrayList<Country> countries = new ArrayList<Country>();
		
		if(tokener != null) {
			JSONArray array = null;
			
			char c = tokener.next();
			tokener.back();
			
			if(c == '[') {
				array = new JSONArray(tokener);
			}else if(c == '{') {
				JSONObject obj = new JSONObject(tokener);
				if(obj.has("status"))
					return countries;
				
				array = new JSONArray("[" + obj + "]");
			}
			
			if(array != null) {
				array.forEach(o -> {
					JSONObject object = (JSONObject) o;
					
					Country country = new Country();
					
					if(!object.isNull("name"))
						country.setName(object.get("name").toString());
					else country.setName("N/A");
					
					if(!object.isNull("alpha2Code"))
						country.setAlpha2Code(object.get("alpha2Code").toString());
					else country.setAlpha2Code("N/A");
					
					if(!object.isNull("alpha3Code"))
						country.setAlpha3Code(object.get("alpha3Code").toString());
					else country.setAlpha3Code("N/A");
					
					if(!object.isNull("capital"))
						country.setCapital(object.get("capital").toString());
					else country.setCapital("N/A");
					
					if(!object.isNull("region"))
						country.setRegion(object.get("region").toString());
					else country.setRegion("N/A");
					
					if(!object.isNull("subregion"))
						country.setSubregion(object.get("subregion").toString());
					else country.setSubregion("N/A");
					
					if(!object.isNull("population"))
						country.setPopulation(object.get("population").toString());
					else country.setPopulation("N/A");
					
					if(!object.isNull("demonym"))
						country.setDemonym(object.get("demonym").toString());
					else country.setDemonym("N/A");
					
					if(!object.isNull("area"))
						country.setArea(object.get("area").toString());
					else country.setArea("N/A");
					
					if(!object.isNull("gini"))
						country.setGini(object.get("gini").toString());
					else country.setGini("N/A");
					
					if(!object.isNull("nativeName"))
						country.setNativeName(object.get("nativeName").toString());
					else country.setNativeName("N/A");
					
					if(!object.isNull("numericCode"))
						country.setNumericCode(object.get("numericCode").toString());
					else country.setNumericCode("N/A");
					
					country.setTopLevelDomain(object.getJSONArray("topLevelDomain").toList().stream().map(obj -> (String) obj).toArray(String[]::new));
					country.setCallingCodes(object.getJSONArray("callingCodes").toList().stream().map(obj -> (String) obj).toArray(String[]::new));
					country.setAltSpellings(object.getJSONArray("altSpellings").toList().stream().map(obj -> (String) obj).toArray(String[]::new));
					country.setTimezones(object.getJSONArray("timezones").toList().stream().map(obj -> (String) obj).toArray(String[]::new));
					country.setBorders(object.getJSONArray("borders").toList().stream().map(obj -> (String) obj).toArray(String[]::new));
					
					JSONArray tempArray;
					
					tempArray = object.getJSONArray("latlng");
					String[] latlng = new String[tempArray.length()];
					for(int i = 0; i < latlng.length; i++)
						latlng[i] = tempArray.get(i).toString();
					
					country.setLatLng(latlng);
					
					tempArray = object.getJSONArray("currencies");
					Currency[] currencies = new Currency[tempArray.length()];
					for(int i = 0; i < currencies.length; i++) {
						JSONObject currencyObj = tempArray.getJSONObject(i);
						Currency currency = new Currency();
						
						if(!currencyObj.isNull("code"))
							currency.setCode(currencyObj.get("code").toString());
						else currency.setCode("N/A");
						
						if(!currencyObj.isNull("name"))
							currency.setName(currencyObj.get("name").toString());
						else currency.setName("N/A");
						
						if(!currencyObj.isNull("symbol"))
							currency.setSymbol(currencyObj.get("symbol").toString());
						else currency.setSymbol("N/A");
						
						currencies[i] = currency;
					}
					
					country.setCurrencies(currencies);
					
					tempArray = object.getJSONArray("languages");
					Language[] languages = new Language[tempArray.length()];
					for(int i = 0; i < languages.length; i++) {
						JSONObject languageObj = tempArray.getJSONObject(i);
						Language language = new Language();
						
						if(!languageObj.isNull("iso639_1"))
							language.setIso639_1(languageObj.get("iso639_1").toString());
						else language.setIso639_1("N/A");
						
						if(!languageObj.isNull("iso639_2"))
							language.setIso639_2(languageObj.get("iso639_2").toString());
						else language.setIso639_2("N/A");
						
						if(!languageObj.isNull("name"))
							language.setName(languageObj.get("name").toString());
						else language.setName("N/A");
						
						if(!languageObj.isNull("nativeName"))
							language.setNativeName(languageObj.get("nativeName").toString());
						else language.setNativeName("N/A");
						
						languages[i] = language;
					}
					
					country.setLanguages(languages);
					
					JSONObject translations = object.getJSONObject("translations");
					for(int i = 0; i < translatedLanguages.length; i++) {
						if(!translations.isNull(translatedLanguages[i]))
							country.addTranslation(translatedLanguages[i], translations.get(translatedLanguages[i]).toString());
					}
					
					tempArray = object.getJSONArray("regionalBlocs");
					RegionalBloc[] regionalBlocs = new RegionalBloc[tempArray.length()];
					for(int i = 0; i < regionalBlocs.length; i++) {
						JSONObject regionalBlocObj = tempArray.getJSONObject(i);
						RegionalBloc regionalBloc = new RegionalBloc();
						
						if(!regionalBlocObj.isNull("acronym"))
							regionalBloc.setAcronym(regionalBlocObj.get("acronym").toString());
						else regionalBloc.setAcronym("N/A");
						
						if(!regionalBlocObj.isNull("name"))
							regionalBloc.setName(regionalBlocObj.get("name").toString());
						else regionalBloc.setName("N/A");
						
						regionalBloc.setOtherAcronyms(regionalBlocObj.getJSONArray("otherAcronyms").toList().stream().map(obj -> (String) obj).toArray(String[]::new));
						regionalBloc.setOtherNames(regionalBlocObj.getJSONArray("otherNames").toList().stream().map(obj -> (String) obj).toArray(String[]::new));
						
						regionalBlocs[i] = regionalBloc;
					}
					
					country.setRegionalBlocs(regionalBlocs);
					
					countries.add(country);
				});
			}
		}
		
		return countries;
	}
	
	public static ArrayList<Country> searchByUnique(String search) {
		LinkedHashSet<Country> countries = new LinkedHashSet<Country>();
		countries.addAll(APICountry.searchByName(search));
		countries.addAll(APICountry.searchByAlphaCode(search));
		countries.addAll(APICountry.searchByNumericCode(search));
		return new ArrayList<Country>(countries);
	}
	
	public static ArrayList<Country> searchByName(String partial) {
		ArrayList<Country> countries = new ArrayList<Country>();
		for(Country country : Storage.getCountryCache().getCountries()) {
			if(!country.getName().equals("N/A"))
				if(country.getName().toLowerCase().contains(partial.toLowerCase()))
					if(countries.add(country)) continue;
			
			if(!country.getNativeName().equals("N/A"))
				if(country.getNativeName().toLowerCase().contains(partial.toLowerCase()))
					if(countries.add(country)) continue;
		}
		
		return countries;
	}
	
	public static ArrayList<Country> searchByAlphaCode(String alphacode) {
		ArrayList<Country> countries = new ArrayList<Country>();
		for(Country country : Storage.getCountryCache().getCountries()) {
			if(!country.getAlpha2Code().equals("N/A"))
				if(country.getAlpha2Code().equalsIgnoreCase(alphacode))
					if(countries.add(country)) continue;
			
			if(!country.getAlpha3Code().equals("N/A"))
				if(country.getAlpha3Code().equalsIgnoreCase(alphacode))
					if(countries.add(country)) continue;
		}
		
		return countries;
	}
	
	public static ArrayList<Country> searchByCapitalCity(String capital) {
		ArrayList<Country> countries = new ArrayList<Country>();
		for(Country country : Storage.getCountryCache().getCountries())
			if(!country.getCapital().equals("N/A"))
				if(country.getCapital().equalsIgnoreCase(capital))
					countries.add(country);
				
		return countries;
	}
	
	public static ArrayList<Country> searchByCallingCode(String callingcode) {
		ArrayList<Country> countries = new ArrayList<Country>();
		for(Country country : Storage.getCountryCache().getCountries())
			for(String country_calling_code : country.getCallingCodes())
				if(country_calling_code != null && !country_calling_code.equals(""))
					if(country_calling_code.equalsIgnoreCase(callingcode))
						countries.add(country);
				
		return countries;
	}
	
	public static ArrayList<Country> searchByNumericCode(String numericcode) {
		ArrayList<Country> countries = new ArrayList<Country>();
		for(Country country : Storage.getCountryCache().getCountries())
			if(!country.getNumericCode().equals("N/A"))
				if(country.getNumericCode().equalsIgnoreCase(numericcode))
					countries.add(country);
		
		return countries;
	}
	
	public static ArrayList<Country> searchByCurrencyName(String currency_name) {
		ArrayList<Country> countries = new ArrayList<Country>();
		for(Country country : Storage.getCountryCache().getCountries())
			for(Currency currency : country.getCurrencies())
				if(!currency.getName().equals("N/A"))
					if(currency.getName().equalsIgnoreCase(currency_name))
						countries.add(country);
		
		return countries;
	}
	
	public static ArrayList<Country> searchByCurrencyCode(String currency_code) {
		ArrayList<Country> countries = new ArrayList<Country>();
		for(Country country : Storage.getCountryCache().getCountries())
			for(Currency currency : country.getCurrencies())
				if(!currency.getCode().equals("N/A"))
					if(currency.getCode().equalsIgnoreCase(currency_code))
						countries.add(country);
		
		return countries;
	}
	
	public static ArrayList<Country> searchByCurrencySymbol(String currency_symbol) {
		ArrayList<Country> countries = new ArrayList<Country>();
		for(Country country : Storage.getCountryCache().getCountries())
			for(Currency currency : country.getCurrencies())
				if(!currency.getSymbol().equals("N/A"))
					if(currency.getSymbol().equalsIgnoreCase(currency_symbol))
						countries.add(country);
		
		return countries;
	}
	
	public static ArrayList<Country> searchByLanguageName(String language) {
		ArrayList<Country> countries = new ArrayList<Country>();
		for(Country country : Storage.getCountryCache().getCountries())
			for(Language country_language : country.getLanguages()) {
				if(!country_language.getName().equals("N/A"))
					if(country_language.getName().equalsIgnoreCase(language))
						if(countries.add(country)) continue;
				
				if(!country_language.getNativeName().equals("N/A"))
					if(country_language.getNativeName().equalsIgnoreCase(language))
						if(countries.add(country)) continue;
			}
		
		return countries;
	}
	
	public static ArrayList<Country> searchByLanguageCode(String languageCode) {
		ArrayList<Country> countries = new ArrayList<Country>();
		for(Country country : Storage.getCountryCache().getCountries())
			for(Language country_language : country.getLanguages()) {
				if(!country_language.getIso639_1().equals("N/A"))
					if(country_language.getIso639_1().equalsIgnoreCase(languageCode))
						if(countries.add(country)) continue;
				
				if(!country_language.getIso639_2().equals("N/A"))
					if(country_language.getIso639_2().equalsIgnoreCase(languageCode))
						if(countries.add(country)) continue;
			}
		
		return countries;
	}
}