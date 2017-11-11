package com.jockie.bot.APIs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.SSLContext;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;

public class APIHelper {
	
	private static final int TIME_OUT = 10;
	private static final int RETRIES_BEFORE_RENDERED_OFFLINE = 3;
	
	private static RequestConfig config = RequestConfig.custom().setConnectTimeout(TIME_OUT * 1000).build();
	
	private static SSLConnectionSocketFactory sslsf;
	
	static {
		try {
		    SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(final X509Certificate[] chain, final String authType) {
                    return true;
                }
            }).build();
		    
		    APIHelper.sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
		}catch(KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			e.printStackTrace();
		}
		
		System.setProperty("java.net.useSystemProxies","true");
	}
	
	public static SSLConnectionSocketFactory getSocketFactory() {
		return APIHelper.sslsf;
	}
	
	public static HttpGet getDefault(String url) {
		HttpGet http_get = new HttpGet(url);
		http_get.addHeader("User-Agent", "Jockie-Discord-Bot");
		return http_get;
	}
	
	public static JSONObject getJSONFromFile(String file) {
		return APIHelper.getJSONFromFile(new File(file));
	}
	
	public static JSONObject getJSONFromFile(File file) {
		if(file.exists()) {
			FileInputStream input_stream = null;
			try {
				input_stream = new FileInputStream(file);
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				byte[] buf = new byte[1024];
				int len = 0;
				while((len = input_stream.read(buf)) != -1)
				    baos.write(buf, 0, len);
				
				return new JSONObject(new String(baos.toByteArray(), StandardCharsets.UTF_8));
			}catch(IOException e) {
				e.printStackTrace();
			}finally{
				try {
					if(input_stream != null) input_stream.close();
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}else{
			throw new IllegalStateException("[JSON From file] : File does not exist (" + file.getPath() + ")");
		}
		
		return null;
	}
	
	public static byte[] getData(String url) {
		return APIHelper.getData(APIHelper.getDefault(url));
	}
	
	public static JSONObject getJSONFromXML(String url) {
		return APIHelper.getJSONFromXML(APIHelper.getDefault(url));
	}
	
	public static JSONTokener getJSON(String url) {
		return APIHelper.getJSON(APIHelper.getDefault(url));
	}
	
	public static byte[] getData(HttpGet get) {
		return APIHelper.getData(0, get);
	}
	
	public static JSONObject getJSONFromXML(HttpGet get) {
		return XML.toJSONObject(new String(APIHelper.getData(get), StandardCharsets.UTF_8));
	}
	
	public static JSONTokener getJSON(HttpGet get) {
		return new JSONTokener(new String(APIHelper.getData(get), StandardCharsets.UTF_8));
	}
	
	public static InputStream getStream(String url) {
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		
		HttpGet get = APIHelper.getDefault(url);
		
		try {
			client = HttpClients.custom().setSSLSocketFactory(getSocketFactory()).setDefaultRequestConfig(config).build();
			response = client.execute(get);
			
			HttpEntity entity = response.getEntity();
			if(entity != null)
				return entity.getContent();
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static byte[] getData(int retries, HttpGet get) {
		System.out.println("[" + get.getURI().getHost() + get.getURI().getPath() + "] Reading start");
		long nano = System.nanoTime();
		
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		
		try {
			client = HttpClients.custom().setSSLSocketFactory(getSocketFactory()).setDefaultRequestConfig(config).build();
			response = client.execute(get);
			
			try {
				HttpEntity entity = response.getEntity();
				if(entity != null) {
					InputStream stream = entity.getContent();
					try {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						
						byte[] buf = new byte[1024];
						int len = 0;
						while((len = stream.read(buf)) != -1)
						    baos.write(buf, 0, len);
						
						System.out.println("[" + get.getURI().getHost() + get.getURI().getPath() + "] Read complete " + (System.nanoTime() - nano));
						
						return baos.toByteArray();
					}finally{
						stream.close();
					}
				}
			}finally{
				response.close();
			}
		}catch(IOException e) {
			if(e instanceof ConnectTimeoutException || e instanceof SocketTimeoutException) {
				if(retries >= APIHelper.RETRIES_BEFORE_RENDERED_OFFLINE) {
					//Do stuff
					System.out.println("[" + get.getURI().getHost() + get.getURI().getPath() + "] Website is down");
				}else{
					return APIHelper.getData(retries + 1, get);
				}
			}else if(e instanceof ConnectionClosedException) {
				return APIHelper.getData(0, get);
			}else{
				e.printStackTrace();
			}
		}finally{
			try {
				if(client != null) client.close();
				if(response != null) response.close();
				if(get != null) get.releaseConnection();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		throw new IllegalStateException("Something went wrong! No data was retrieved from the website!");
	}
	
    public static char[] getCharArray(JSONTokener x) {
    	char[] characters = new char[10];
    	
    	int added = 0;
    	while(x.more()) {
    		if(added >= characters.length) {
    			characters = Arrays.copyOf(characters, characters.length * 2);
    		}
    		characters[added++] = x.next();
    	}
    	
    	characters = Arrays.copyOf(characters, added);
    	
    	return characters;
    }
}