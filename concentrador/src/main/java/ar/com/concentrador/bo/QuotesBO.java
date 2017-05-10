package ar.com.concentrador.bo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

import ar.com.concentrador.extractor.BaseExtractor;
import ar.com.concentrador.model.Quotes;

@Singleton
public class QuotesBO {
	
	@Inject
	private Logger logger;
	
	@Inject
	private List<BaseExtractor> extractors;
	
	private List<Quotes> listQuotes;
	private int day = -1;
	
	public Collection<Quotes> retriveFilterList(Quotes filter) {
		return this.getListQuotes().stream()
				.filter(b ->  filter.getCode() == null || "".equals(filter.getCode()) || b.getCode().contains(filter.getCode()))
				.filter(b ->  filter.getPackageDes() == null || "".equals(filter.getPackageDes()) || b.getPackageDes().contains(filter.getPackageDes()))
				.filter(b ->  filter.getValue() == null || "".equals(filter.getValue()) || b.getValue().contains(filter.getValue()))
				.collect(Collectors.toList());
	}
	
	public Collection<String> retriveListOfCode() {
		Set<String> set = new HashSet<>();
		for(Quotes q: this.getListQuotes()) {
			set.add(q.getCode());
		}		
		return set;
	}
	
	private List<Quotes> getListQuotes() {
		Calendar c = Calendar.getInstance();
		if (this.day < 0) {
			this.executeExtractors();
			
		} else if (this.day!=c.get(Calendar.DAY_OF_MONTH)) {
			this.executeExtractors();
			
		} else if (this.listQuotes==null) {
			this.executeExtractors();
		} else {
			if (logger.isInfoEnabled()) {
				this.logger.info("**** Recuperando desde cahce. ****");
			}
		}
		return this.listQuotes;
	}
	
	private void executeExtractors() {
		if (logger.isInfoEnabled()) {
			this.logger.info("**** Recuperando Datos desde los extractores. ****");
		}
		
		this.listQuotes = new ArrayList<>();
		for(BaseExtractor base: this.extractors) {
			this.listQuotes.addAll(base.getQuotes());
			if (logger.isInfoEnabled()) {
				this.logger.info("Recuperando desde el extractor: " + base.getCodeExtractor());
			}
		}
		this.day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}
}
