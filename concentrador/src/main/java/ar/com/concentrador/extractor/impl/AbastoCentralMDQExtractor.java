package ar.com.concentrador.extractor.impl;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ar.com.concentrador.enums.ProductTypes;
import ar.com.concentrador.extractor.BaseExtractor;
import ar.com.concentrador.model.Quotes;

public class AbastoCentralMDQExtractor extends BaseExtractor {
	public static final String CODE_MARKET = "MDQ";
	public static final String NAME_MARKET = "Abasto Central de la Ciudad de Mar del Plata";
	private static final String URL = "http://www.abastocentralmdp.com/lista.php?id_rubro=%s";
	
	private static final char[] CHAR_TO_REMOVE = {'$', '-'}; 
	private static final Charset CHARSET = Charset.forName("ISO-8859-1");
	
	public AbastoCentralMDQExtractor(String codeExtractor, String urlParam, ProductTypes pt) {
		super(codeExtractor, urlParam, pt);
	}
	
	@Override
	public String getName() {
		return NAME_MARKET;
	}
	
	@Override
	public String getMarket() {
		return CODE_MARKET;
	}	
	
	@Override
	public List<Quotes> getQuotes() {
		return this.extract();
	}
	
	private List<Quotes> extract() {
		List<Quotes> information = new ArrayList<>();
		Date date = new Date();
		byte [] data = this.call(String.format(URL, this.urlParam));
		Document doc = Jsoup.parse(new String(data, CHARSET));
		
	    for (Element table : doc.select("table > tbody > tr > td > table")) {
	        for (Element row : table.select("tr")) {
	            Elements tds = row.select("td");
	            
	            Quotes q = new Quotes();
	            q.setDate(date);
	            q.setProductType(this.productType.getId());
	            q.setCode(formatCodeValue(tds.get(0).text()));
	            q.setDescription(formatDescriptionValue( q.getCode(), tds.get(2).text(), tds.get(3).text()));
	            q.setSource(formatDescriptionValue(tds.get(1).text()));
	            q.setPackageDes(formatDescriptionValue(tds.get(2).text()));
	            q.setValue(formatDescriptionValue(tds.get(3).text()));
	            q.setMinValue(formatMoneyValue(tds.get(4).text(), CHAR_TO_REMOVE));
	            q.setMaxValue(formatMoneyValue(tds.get(5).text(), CHAR_TO_REMOVE));
	            q.setDiff(new BigDecimal(0));
	            
	            information.add(this.completeQuotes(q));
	        }
	    }		
		
		return information;
	}

}
