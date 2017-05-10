package ar.com.concentrador.extractor;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;

import ar.com.concentrador.model.Quotes;

public abstract class BaseExtractor {
	private static final String USER_AGENT = "Mozilla/5.0";
	private static final int TIME_OUT_SECONDS = 60;
	
	public abstract String getCodeExtractor();
	public abstract List<Quotes> getQuotes();
	
	protected byte[] call(String stringUrl) {
		HttpURLConnection con = null;
		
		try {
			URL url = new URL(stringUrl);
			
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setReadTimeout(TIME_OUT_SECONDS * 1000);
			
			con.connect();
			
			int responseCode = con.getResponseCode();
			
			if (HttpURLConnection.HTTP_OK == responseCode) {
				 return IOUtils.toByteArray(con.getInputStream());
			}
			
			throw new RuntimeException("Error al recuperar informacion de la URL:" + stringUrl + " responseCode:" + responseCode);
			
		} catch (IOException e) {
			throw new RuntimeException("Error de conexion al recuperar informacion de la URL:" + stringUrl, e);
			
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}
	
	protected static String formatCodeValue(String value) {
		if (value == null) {
			value = "";
		}
		return value.trim().toUpperCase();
	}
	
	protected static String formatDescriptionValue(String ... values) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			sb.append(formatCodeValue(values[i])); 
			sb.append(" ");
		}
		return sb.toString().trim();
	}	
	
	protected static BigDecimal formatMoneyValue(String value, char[] toRemove) {
		for (int i = 0; i < toRemove.length; i++) {
			value = value.replace( "" + toRemove[i], "");
		}
		
		if ("".equals(value.trim())) {
			value = "0";
		}
		
		return new BigDecimal(value);
	}
}
