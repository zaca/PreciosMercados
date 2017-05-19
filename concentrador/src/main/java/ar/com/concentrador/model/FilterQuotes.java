package ar.com.concentrador.model;

import java.util.List;

public class FilterQuotes {
	
	private List<String> products;
	private List<String> markets;
	
	private Quotes quotes;
	
	public Quotes getQuotes() {
		return quotes;
	}
	public void setQuotes(Quotes quotes) {
		this.quotes = quotes;
	}
	public List<String> getProducts() {
		return products;
	}
	public void setProducts(List<String> products) {
		this.products = products;
	}
	public List<String> getMarkets() {
		return markets;
	}
	public void setMarkets(List<String> markets) {
		this.markets = markets;
	}	
}
