package ar.com.concentrador.extractor;

import org.junit.Assert;
import org.junit.Test;

import ar.com.concentrador.enums.ProductTypes;
import ar.com.concentrador.extractor.impl.AbastoCentralMDQExtractor;
import ar.com.concentrador.extractor.impl.MecadoCentralBSASExtractor;

public class ExtractorTest {
	@Test
	public void executeExtractors() {
		BaseExtractor exMDQ = new AbastoCentralMDQExtractor(1,ProductTypes.FRUTAS);
		BaseExtractor exBSAS = new MecadoCentralBSASExtractor();
		
		Assert.assertFalse(exBSAS.getQuotes().isEmpty());
		Assert.assertFalse(exMDQ.getQuotes().isEmpty());
	}
}
