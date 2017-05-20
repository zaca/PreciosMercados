package ar.com.concentrador.extractor;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import ar.com.concentrador.enums.ProductTypes;
import ar.com.concentrador.model.Quotes;

public abstract class BaseExtractor {
	private static final String USER_AGENT = "Mozilla/5.0";
	private static final int TIME_OUT_SECONDS = 60;
	
    private static final Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    
	protected final String codeExtractor;
	protected final String urlParam;
	protected final ProductTypes productType;

	public abstract String getMarket();
	public abstract String getName();
	public abstract List<Quotes> getQuotes();
	
	protected Map<String, String> mapPackage;
	
	protected BaseExtractor(String codeExtractor, String urlParam, ProductTypes pt){
		this.codeExtractor = codeExtractor;
		this.urlParam = urlParam;
		this.productType = pt;
		
		this.mapPackage = new HashMap<>();
		this.mapPackage.put("AP", "ARGEN-POOL");
		this.mapPackage.put("A", "ATADO");
		this.mapPackage.put("BA", "BANDEJA");
		this.mapPackage.put("BO", "BOLSA");
		this.mapPackage.put("CA", "CAJA");
		this.mapPackage.put("CJ", "CAJON");
		this.mapPackage.put("CT", "CAJA/Telescop");
		this.mapPackage.put("GR", "GRANEL");
		this.mapPackage.put("IF", "IFCO");
		this.mapPackage.put("JA", "JAULA");
		this.mapPackage.put("MA", "MARK 4");
		this.mapPackage.put("PE", "PERDIDO");
		this.mapPackage.put("PL", "PLAFOM");
		this.mapPackage.put("PQ", "PAQUETE");
		this.mapPackage.put("RT", "RISTRA 100");
		this.mapPackage.put("SM", "SAN MARTIN");
		this.mapPackage.put("ST", "STANDARTD");
		this.mapPackage.put("SU", "SUDAFRICANO");
		this.mapPackage.put("TO", "TORO");
		this.mapPackage.put("TT", "TORITO");
	}
	
	public String getCodeExtractor(){
		return this.codeExtractor;
	}
	
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
		
	protected Quotes createQuotes() {
		Quotes q = new Quotes();
		q.setMarket(this.getMarket());
		q.setCodeExtractor(this.getCodeExtractor());
		return q;
	}
	
	protected String formatPackage(String code) {
		return formatDescriptionValue(this.mapPackage.get(code));
	}
	
	protected static String formatCodeValue(String value) {
		if (value == null) {
			value = "";
		}
		return deAccent(value.trim().toUpperCase());
	}
	
	public static String deAccent(String str) {
	    String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
	    return pattern.matcher(nfdNormalizedString).replaceAll("");
	}	
	
	protected static String formatDescriptionValue(String ... values) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			sb.append(formatCodeValue(values[i])); 
			sb.append(" ");
		}
		return sb.toString().toUpperCase().trim();
	}	
	
	protected static BigDecimal formatMoneyValue(String value, char[] toRemove, String replace) {
		for (int i = 0; i < toRemove.length; i++) {
			value = value.replace( "" + toRemove[i], replace);
		}
		
		if ("".equals(value.trim())) {
			value = "0";
		}
		
		return new BigDecimal(value);
	}
	
	protected static BigDecimal formatMoneyValue(String value, char[] toRemove) {
		return formatMoneyValue(value, toRemove, "");
	}
}
