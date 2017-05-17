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

@Singleton
public class ExtractorsFactory {
	
	
	
    @Produces
    @ApplicationScoped
    public List<BaseExtractor> produceExtractors() {
    	List<BaseExtractor> list = new ArrayList<>();
    	list.add(new MecadoCentralBSASExtractor());
    	list.add(new AbastoCentralMDQExtractor(1,ProductTypes.FRUTAS));
    	list.add(new AbastoCentralMDQExtractor(2, ProductTypes.VERDURAS));
    	list.add(new AbastoCentralMDQExtractor(3,ProductTypes.HORTALIZAS));
    	
        return list; 
    }
}
