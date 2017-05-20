package ar.com.concentrador.extractor;

import org.junit.Assert;
import org.junit.Test;

import ar.com.concentrador.enums.ProductTypes;
import ar.com.concentrador.extractor.impl.AbastoCentralMDQExtractor;
import ar.com.concentrador.extractor.impl.MecadoCentralBSASExtractor;
import ar.com.concentrador.extractor.impl.MinisterioAgroindustriaGOBExtractor;

public class ExtractorTest {
	@Test
	public void executeExtractors() {
		BaseExtractor exMDQ = new AbastoCentralMDQExtractor("01", "1", ProductTypes.FRUTAS);
		BaseExtractor exBSAS = new MecadoCentralBSASExtractor("02", "precios_mayoristas/PM-Frutas", ProductTypes.FRUTAS);
		BaseExtractor exGOB = new MinisterioAgroindustriaGOBExtractor("03", "hortalizas/01_hortalizas_act", ProductTypes.HORTALIZAS);
		
		Assert.assertFalse(exGOB.getQuotes().isEmpty());
		Assert.assertFalse(exBSAS.getQuotes().isEmpty());
		Assert.assertFalse(exMDQ.getQuotes().isEmpty());
	}
}
