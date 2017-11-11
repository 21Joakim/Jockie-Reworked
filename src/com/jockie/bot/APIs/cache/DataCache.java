package com.jockie.bot.APIs.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.jockie.bot.APIs.APIHelper;

public interface DataCache {
	
	public String getAPIName();
	
	public String getSubName();
	
	public File getFile();
	
	public LocalDate getExpires();
	
	public JSONTokener getData();
	
	public void updateData();
	
	public static void writeData(DataCache cache, JSONTokener tokener) {
		FileOutputStream output_stream = null;
		try {
			output_stream = new FileOutputStream(cache.getFile());
			
			String metadata = "\"api_name\":\"" + cache.getAPIName() + "\",\"expires\":\"" + cache.getExpires().toString() + "\"";
			
			output_stream.write(new JSONObject("{" + metadata + ",\"data\":" + new String(APIHelper.getCharArray(tokener)) + "}").toString().getBytes(StandardCharsets.UTF_8));
		}catch(IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(output_stream != null) output_stream.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static JSONObject readData(DataCache cache) {
		return APIHelper.getJSONFromFile(cache.getFile());
	}
}