package ar.com.concentrador.extractor.impl;

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
	private static final String CODE_EXTRACTOR = "01";
	private static final String CODE_MARKET = "MDQ";
	private static final String URL = "http://www.abastocentralmdp.com/lista.php?id_rubro=%s";
	private static final char[] CHAR_TO_REMOVE = {'$', '-'}; 
	private static final Charset CHARSET = Charset.forName("ISO-8859-1");
	
	private int urlParam;
	private ProductTypes productType;
	
	public AbastoCentralMDQExtractor(int urlParam, ProductTypes pt) {
		this.urlParam = urlParam;
		this.productType = pt;
	}

	@Override
	public String getCodeExtractor() {
		return CODE_EXTRACTOR;
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
	            
	            Quotes q = createQuotes();
	            q.setDate(date);
	            q.setProductType(this.productType.getId());
	            q.setCode(formatCodeValue(tds.get(0).text()));
	            q.setDescription(formatDescriptionValue(formatCodeValue(tds.get(0).text()), tds.get(2).text(), tds.get(3).text()));
	            q.setSource(formatDescriptionValue(tds.get(1).text()));
	            q.setPackageDes(formatDescriptionValue(tds.get(2).text()));
	            q.setValue(formatDescriptionValue(tds.get(3).text()));
	            q.setMaxValue(formatMoneyValue(tds.get(4).text(), CHAR_TO_REMOVE));
	            q.setMinValue(formatMoneyValue(tds.get(5).text(), CHAR_TO_REMOVE));
	            
	            information.add(q);
	        }
	    }		
		
		return information;
	}

}
