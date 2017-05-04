package ar.com.concentrador.extractor;

import org.junit.Assert;
import org.junit.Test;

import ar.com.concentrador.extractor.impl.AbastoCentralMDQExtractor;

public class ExtractorTest {
	@Test
	public void executeExtractors() {
		AbastoCentralMDQExtractor ex = new AbastoCentralMDQExtractor();
		
		Assert.assertFalse(ex.getQuotes().isEmpty());
	}
}
