package ar.com.concentrador.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			Map<String,String> tmp = new HashMap<>();
		    for(ProductTypes type : ProductTypes.values()){
		        tmp.put(type.getId(), type.getDescripcion());
		    }
			return Response.ok(tmp).build();
		} catch (Exception e) {
			logger.error("Error al recuperar codigos de productos.", e);
			return Response.serverError().build();
		}		
	}

	private Response byFilter(Quotes q, List<String> products, List<String> markets) {
		try {
			q.setCode( BaseExtractor.deAccent(q.getCode()));
			
			return Response.ok(quotesBO.retriveFilterList(q, markets, products)).build();
		} catch (Exception e) {
			logger.error("Error al recuperar productos por filtro. Filtro" + q, e);
			return Response.serverError().build();
		}
	}
}
