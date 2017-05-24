package ar.com.concentrador.factory;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import ar.com.concentrador.enums.ProductTypes;
import ar.com.concentrador.extractor.BaseExtractor;
import ar.com.concentrador.extractor.impl.AbastoCentralMDQExtractor;
import ar.com.concentrador.extractor.impl.MecadoCentralBSASExtractor;
import ar.com.concentrador.extractor.impl.MinisterioAgroindustriaGOBExtractor;
import ar.com.concentrador.model.Market;

@Singleton
public class ExtractorsFactory {
	
    @Produces
    @ApplicationScoped
    public List<BaseExtractor> produceExtractors() {
    	List<BaseExtractor> list = new ArrayList<>();
    	
    	list.add(new MecadoCentralBSASExtractor("01", "precios_mayoristas/PM-Hortalizas", ProductTypes.HORTALIZAS));
    	list.add(new MecadoCentralBSASExtractor("05", "precios_mayoristas/PM-Frutas", ProductTypes.FRUTAS));
    	
    	list.add(new AbastoCentralMDQExtractor("02", "1", ProductTypes.FRUTAS));
    	list.add(new AbastoCentralMDQExtractor("03", "2", ProductTypes.VERDURAS));
    	list.add(new AbastoCentralMDQExtractor("04", "3", ProductTypes.HORTALIZAS));
    	
    	list.add(new MinisterioAgroindustriaGOBExtractor("06", "hortalizas/01_hortalizas_act", ProductTypes.HORTALIZAS));
    	//TODO: Falta el envase y kg.... esta mal la pagina ??? 
    	//list.add(new MinisterioAgroindustriaGOBExtractor("07", "frutas/01_frutas_actual", ProductTypes.FRUTAS));
    	
        return list; 
    }
    
    @Produces
    @ApplicationScoped
    public List<Market> produceMarketList() {
    	List<Market> markets = new ArrayList<>();
		markets.add(new Market(MecadoCentralBSASExtractor.CODE_MARKET, MecadoCentralBSASExtractor.NAME_MARKET));
		markets.add(new Market(MinisterioAgroindustriaGOBExtractor.CODE_MARKET, MinisterioAgroindustriaGOBExtractor.NAME_MARKET));
		markets.add(new Market(AbastoCentralMDQExtractor.CODE_MARKET, AbastoCentralMDQExtractor.NAME_MARKET));
        return markets; 
    }
}
