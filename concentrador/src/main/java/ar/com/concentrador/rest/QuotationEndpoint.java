package ar.com.concentrador.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;

import ar.com.concentrador.extractor.impl.AbastoCentralMDQExtractor;

@Stateless
@Path("/quotation")
public class QuotationEndpoint {
	
	@Inject
	private Logger logger;
	
	@GET
	@Path("/byCode/{code}")
	@Produces("application/json")
	public Response byCode(@PathParam("code") String code) {
		
		if (code == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		logger.debug("servicio de testing...");
		AbastoCentralMDQExtractor ex = new AbastoCentralMDQExtractor();
		
		return Response.ok(ex.getQuotes()).build();
	}
	
}
