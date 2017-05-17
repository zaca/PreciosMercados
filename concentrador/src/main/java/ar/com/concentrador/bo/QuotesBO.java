package ar.com.concentrador.bo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

	private Map<String, List<Quotes>> listQuotes;

	private int day = -1;

	public Collection<Quotes> retriveFilterList(Quotes filter) {

		/* Filtra */
		List<Quotes> list = this.getListQuotes().stream()
				.filter(b -> filter.getCode() == null || "".equals(filter.getCode())
						|| b.getCode().contains(filter.getCode()))
				.filter(b -> filter.getPackageDes() == null || "".equals(filter.getPackageDes())
						|| b.getPackageDes().contains(filter.getPackageDes()))
				.filter(b -> filter.getValue() == null || "".equals(filter.getValue())
						|| b.getValue().contains(filter.getValue()))
				.collect(Collectors.toList());

		/* Ordena */
		list.sort(new Comparator<Quotes>() {
			public int compare(Quotes o1, Quotes o2) {
				String o1String = o1.getMarket() + o1.getCode();
				String o2String = o1.getMarket() + o1.getCode();
				return o1String.compareTo(o2String);
			}
		});

		return list;
	}

	public Collection<Quotes> retriveFilterList(Quotes filter,List<String> markets, List<String> products) {

		/* Filtra */
		List<Quotes> list = this.getListQuotes().stream()
				.filter(b -> filter.getCode() == null || "".equals(filter.getCode())
						|| b.getCode().contains(filter.getCode()))
				.filter(b -> filter.getPackageDes() == null || "".equals(filter.getPackageDes())
						|| b.getPackageDes().contains(filter.getPackageDes()))
				.filter(b -> filter.getValue() == null || "".equals(filter.getValue())
						|| b.getValue().contains(filter.getValue()))
				.filter(b -> products == null || products.size()==0
						|| !products.contains(b.getProductType()))
				.filter(b -> markets == null || markets.size()==0
				|| !markets.contains(b.getMarket()))
				.collect(Collectors.toList());
		

		/* Ordena */
		list.sort(new Comparator<Quotes>() {
			public int compare(Quotes o1, Quotes o2) {
				String o1String = o1.getMarket() + o1.getCode();
				String o2String = o1.getMarket() + o1.getCode();
				return o1String.compareTo(o2String);
			}
		});

		return list;
	}

	public Collection<String> retriveListOfCode() {

		/* Quita Duplicados */
		Set<String> set = new HashSet<>();
		for (Quotes q : this.getListQuotes()) {
			set.add(q.getCode());
		}

		/* Ordena */
		List<String> list = new ArrayList<>(set);
		list.sort(new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});

		return list;
	}

	private List<Quotes> getListQuotes() {
		Calendar c = Calendar.getInstance();
		if (this.day < 0) {
			this.executeExtractors();

		} else if (this.day != c.get(Calendar.DAY_OF_MONTH)) {
			this.executeExtractors();

		} else if (this.listQuotes == null) {
			this.executeExtractors();

		} else {
			if (logger.isInfoEnabled()) {
				this.logger.info("**** Recuperando desde cache. ****");
			}
		}

		List<Quotes> newListQuotes = new ArrayList<>();
		for (List<Quotes> elements : this.listQuotes.values()) {
			newListQuotes.addAll(elements);
		}

		return newListQuotes;
	}

	private void executeExtractors() {
		if (logger.isInfoEnabled()) {
			this.logger.info("**** Recuperando Datos desde los extractores. ****");
		}

		if (this.listQuotes == null) {
			this.listQuotes = new HashMap<>();
		}

		for (BaseExtractor base : this.extractors) {
			try {

				this.listQuotes.put(base.getCodeExtractor(), base.getQuotes());
				if (logger.isInfoEnabled()) {
					this.logger.info("Recuperando desde el extractor: " + base.getCodeExtractor());
				}

			} catch (Exception e) {
				logger.error("Extractor mercado " + base.getMarket() + " extractor " + base.getCodeExtractor(), e);
			}
		}
		this.day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}
}
