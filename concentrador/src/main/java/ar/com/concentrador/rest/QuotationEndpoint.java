package ar.com.concentrador.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;

import ar.com.concentrador.bo.QuotesBO;
import ar.com.concentrador.enums.ProductTypes;
import ar.com.concentrador.extractor.BaseExtractor;
import ar.com.concentrador.model.FilterQuotes;
import ar.com.concentrador.model.Market;
import ar.com.concentrador.model.ProductType;
import ar.com.concentrador.model.Quotes;

@Stateless
@Path("/quotation")
public class QuotationEndpoint {
	
	@Inject
	private Logger logger;
	
	@Inject
	private QuotesBO quotesBO;
	
	@Inject
	private List<Market> marketList;
	
	@POST
	@Path("/byFilter")
	@Produces(MediaType.APPLICATION_JSON + "; charset=ISO-8859-1")
	@Consumes(MediaType.APPLICATION_JSON + "; charset=ISO-8859-1")
	public Response byFilter(FilterQuotes parameters) {
		if (parameters == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		if (parameters.getPredictiveSearch() != null && !parameters.getPredictiveSearch().isEmpty()) {
			return byFilter(parameters.getPredictiveSearch(), parameters.getProducts(), parameters.getMarkets());
		}
		return byFilter(parameters.getQuotes(), parameters.getProducts(), parameters.getMarkets());		
	}

	@GET
	@Path("/listMarket")
	@Produces(MediaType.APPLICATION_JSON + "; charset=ISO-8859-1")
	public Response listMarket() {
		try {
			return Response.ok(marketList).build();
		} catch (Exception e) {
			logger.error("Error al recuperar codigos de Mercados.", e);
			return Response.serverError().build();
		}		
	}	
	
	@GET
	@Path("/listCodes")
	@Produces(MediaType.APPLICATION_JSON + "; charset=ISO-8859-1")
	public Response listCodes() {
		try {
			return Response.ok(quotesBO.retriveListOfCode()).build();
		} catch (Exception e) {
			logger.error("Error al recuperar codigos de productos.", e);
			return Response.serverError().build();
		}		
	}
	
	@GET
	@Path("/listProductTypes")
	@Produces(MediaType.APPLICATION_JSON + "; charset=ISO-8859-1")
	public Response listProductTypes() {
		try {
			List<ProductType> tmp = new ArrayList<>();
		    for(ProductTypes type : ProductTypes.values()){
		        tmp.add(new ProductType(type.getId(), type.getDescripcion()));
		    }
			return Response.ok(tmp).build();
		} catch (Exception e) {
			logger.error("Error al recuperar codigos de productos.", e);
			return Response.serverError().build();
		}		
	}

	private Response byFilter(Quotes q, List<String> products, List<String> markets) {
		try {
			if (q.getCode() != null && !q.getCode().isEmpty()) {
				q.setCode( BaseExtractor.deAccent(q.getCode().toUpperCase()));
			}
			if (q.getPackageDes() != null && !q.getPackageDes().isEmpty()) {
				q.setPackageDes( BaseExtractor.deAccent(q.getPackageDes().toUpperCase()));
			}
			if (q.getValue() != null && !q.getValue().isEmpty()) {
				q.setValue( BaseExtractor.deAccent(q.getValue().toUpperCase()));
			}			
			
			return Response.ok(quotesBO.retriveFilterList(q, markets, products)).build();
		} catch (Exception e) {
			logger.error("Error al recuperar productos por filtro. Filtro" + q, e);
			return Response.serverError().build();
		}
	}
	
	private Response byFilter(String predictiveSearch, List<String> products, List<String> markets) {
		try {
			String[] toFilters = null;
			if (predictiveSearch.contains("\"")) {
				toFilters = predictiveSearch.split("\"");
			} else {
				toFilters = predictiveSearch.split(" ");
			}
			for (int i = 0; i < toFilters.length; i++) {
				toFilters[i] = BaseExtractor.deAccent(toFilters[i].toUpperCase());
			}
			
			return Response.ok(quotesBO.retriveFilterList( toFilters, markets, products)).build();
		} catch (Exception e) {
			logger.error("Error al recuperar productos por filtro. Filtro: " + predictiveSearch, e);
			return Response.serverError().build();
		}
	}	
}
