package ar.com.concentrador.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;

import ar.com.concentrador.bo.QuotesBO;
import ar.com.concentrador.enums.ProductTypes;
import ar.com.concentrador.extractor.BaseExtractor;
import ar.com.concentrador.model.Quotes;

@Stateless
@Path("/quotation")
public class QuotationEndpoint {
	
	@Inject
	private Logger logger;
	
	@Inject
	private QuotesBO quotesBO;
	
	@GET
	@Path("/byFilter/{code}")
	@Produces(MediaType.APPLICATION_JSON + "; charset=ISO-8859-1")
	public Response byCode(@PathParam("code") String code) {
		if (code == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		Quotes q = new Quotes();
		q.setCode(code);
		
		return byFilter(q);
	}	
	
	@GET
	@Path("/byFilter/{code}/{package}")
	@Produces(MediaType.APPLICATION_JSON + "; charset=ISO-8859-1")
	public Response byCode(@PathParam("code") String code, @PathParam("package") String packageDes) {
		if (code == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		if (packageDes == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		Quotes q = new Quotes();
		q.setCode(code);
		q.setPackageDes(packageDes);
		
		return byFilter(q);
	}	
	
	@GET
	@Path("/byFilter/{code}/{package}/{value}")
	@Produces(MediaType.APPLICATION_JSON + "; charset=ISO-8859-1")
	public Response byCode(@PathParam("code") String code, @PathParam("package") String packageDes, @PathParam("value") String value) {
		if (code == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		if (packageDes == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		if (value == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		Quotes q = new Quotes();
		q.setCode(code);
		q.setPackageDes(packageDes);
		q.setValue(value);
		
		return byFilter(q);
	}
	
	@Path("/byProductsMarketsValue")
	@Produces(MediaType.APPLICATION_JSON + "; charset=ISO-8859-1")
	public Response byProductsMarketsValue(@PathParam("products") List<String> products, 
			@PathParam("markets") List<String> markets, @PathParam("code") String code) {
		if (products == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		if (markets == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		if (code == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		Quotes q = new Quotes();
		q.setCode(code);
		
		return byFilter(q, products, markets);
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
	
	private Response byFilter(Quotes q) {
		try {
			q.setCode( BaseExtractor.deAccent(q.getCode()) );
			
			return Response.ok(quotesBO.retriveFilterList(q)).build();
		} catch (Exception e) {
			logger.error("Error al recuperar productos por filtro. Filtro" + q, e);
			return Response.serverError().build();
		}
	}
	
	private Response byFilter(Quotes q,List<String> products,List<String> markets) {
		try {
			q.setCode( BaseExtractor.deAccent(q.getCode()) );
			
			return Response.ok(quotesBO.retriveFilterList(q, markets, products)).build();
		} catch (Exception e) {
			logger.error("Error al recuperar productos por filtro. Filtro" + q, e);
			return Response.serverError().build();
		}
	}
}
