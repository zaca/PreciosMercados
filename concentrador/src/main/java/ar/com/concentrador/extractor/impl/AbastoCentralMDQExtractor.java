package ar.com.concentrador.extractor.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ar.com.concentrador.extractor.BaseExtractor;
import ar.com.concentrador.model.Quotes;

public class AbastoCentralMDQExtractor extends BaseExtractor {
	private static final String CODE_EXTRACTOR = "01";
	private static final String URL = "http://www.abastocentralmdp.com/lista.php?id_rubro=3";
	private static final char[] CHAR_TO_REMOVE = {'$', '-'}; 
	
	@Override
	public String getCodeExtractor() {
		return CODE_EXTRACTOR;
	}

	@Override
	public List<Quotes> getQuotes() {
		return this.extract();
	}
	
	private List<Quotes> extract() {
		List<Quotes> information = new ArrayList<>();
		Date date = new Date();
		byte [] data = this.call(URL);
		Document doc = Jsoup.parse(new String(data));
		
	    for (Element table : doc.select("table > tbody > tr > td > table")) {
	        for (Element row : table.select("tr")) {
	            Elements tds = row.select("td");
	            
	            Quotes q = new Quotes();
	            q.setCodeExtractor(CODE_EXTRACTOR);
	            q.setDate(date);
	            q.setCode(formatCodeValue(tds.get(0).text()));
	            q.setDescription(formatDescriptionValue(tds.get(2).text(), tds.get(3).text()));
	            q.setMaxValue(formatMoneyValue(tds.get(4).text(), CHAR_TO_REMOVE));
	            q.setMinValue(formatMoneyValue(tds.get(5).text(), CHAR_TO_REMOVE));
	            
	            information.add(q);
	        }
	    }		
		
		return information;
	}
}
