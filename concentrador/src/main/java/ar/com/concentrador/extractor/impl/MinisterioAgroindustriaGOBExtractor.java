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

public class MinisterioAgroindustriaGOBExtractor extends BaseExtractor {
	public static final String CODE_MARKET = "GOB";
	public static final String NAME_MARKET = "Ministerio de Agroindustria";
	private static final String URL = "http://www.minagri.gob.ar/new/0-0/programas/dma/%s.php";
								
	private static final char[] CHAR_TO_REMOVE = {','}; 
	private static final Charset CHARSET = Charset.forName("ISO-8859-1");
	
	public MinisterioAgroindustriaGOBExtractor(String codeExtractor, String urlParam, ProductTypes pt) {
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
		
	    Element table = doc.select("table.tabla").first();
	    
        for (Element row : table.select("tr:gt(2)")) {
            Elements tds = row.select("td");
            
            Quotes q = new Quotes();
            q.setDate(date);
            q.setProductType(this.productType.getId());
            q.setCode(formatCodeValue(tds.get(0).text() + " " + tds.get(1).text()));

            q.setSource(formatDescriptionValue(tds.get(2).text()));
            q.setPackageDes(this.formatPackage(tds.get(3).text()));
            q.setValue(formatDescriptionValue(tds.get(4).text()));
            try {
            	q.setMaxValue(formatMoneyValue(tds.get(9).text(), CHAR_TO_REMOVE, ".").multiply(new BigDecimal(q.getValue())));     	
            } catch (NumberFormatException e) {
            	q.setMaxValue(formatMoneyValue(tds.get(9).text(), CHAR_TO_REMOVE, "."));
            }
            q.setMinValue(q.getMaxValue());
            q.setDescription(formatDescriptionValue(q.getCode(), q.getPackageDes(), q.getValue()));
            q.setDiff(formatMoneyValue(tds.get(10).text(), CHAR_TO_REMOVE));    
           
            information.add(this.completeQuotes(q));
        }
	    		
		
		return information;
	}

}
