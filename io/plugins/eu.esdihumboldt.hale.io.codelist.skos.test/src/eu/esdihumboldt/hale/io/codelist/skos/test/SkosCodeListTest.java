package eu.esdihumboldt.hale.io.codelist.skos.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.common.codelist.CodeList.CodeEntry;
import eu.esdihumboldt.hale.common.codelist.io.CodeListReader;
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.io.codelist.skos.reader.SkosCodeListReader;

/**
 * 
 * SKOS Code list reader test
 * 
 * @author Arun
 *
 */
public class SkosCodeListTest {

	/**
	 * test rdf file consisting only concepts
	 * 
	 * @throws Exception throws exception if something wrong occurs
	 */
	@Test
	public void testSKOSFromRDF1() throws Exception {

		CodeList codeList = readCodeList(getResourceURI("/data/test1.rdf"));

		Collection<CodeEntry> entries = codeList.getEntries();
		assertFalse(entries.isEmpty());

		assertEquals(entries.size(), 1);

		assertNotNull(codeList.getLocation());
		assertNotNull(codeList.getIdentifier());

		for (CodeEntry entry : entries) {
			assertEquals("Data scientist", entry.getName());
		}
	}

	/**
	 * test rdf file consisting concept scheme and concepts
	 * 
	 * @throws Exception throws exception if something wrong occurs
	 */
	@Test
	public void testSKOSFromRDF2() throws Exception {

		CodeList codeList = readCodeList(getResourceURI("/data/test2.rdf"));

		Collection<CodeEntry> entries = codeList.getEntries();
		assertFalse(entries.isEmpty());

		assertEquals(entries.size(), 3);

		assertNotNull(codeList.getLocation());
		assertNotNull(codeList.getIdentifier());
		assertEquals("Types of aquifers.", codeList.getDescription());

		List<String> concepts = Arrays.asList("confined subartesian", "confined artesian",
				"unconfined");

		for (CodeEntry entry : entries) {
			assertTrue(concepts.contains(entry.getName()));
		}

	}

	/**
	 * 
	 * test url saved as xml consisting concepts (case: fallback).
	 * 
	 * @throws Exception throws exception if something wrong occurs
	 */
	@Test
	public void testSKOSFromRDF3() throws Exception {

		CodeList codeList = readCodeList(getResourceURI("/data/test3.rdf"));

		Collection<CodeEntry> entries = codeList.getEntries();
		assertFalse(entries.isEmpty());

		assertEquals(entries.size(), 4);

		assertNotNull(codeList.getLocation());
		assertNotNull(codeList.getIdentifier());

		CodeEntry entry = entries.iterator().next();

		assertEquals(
				"% DNA in tail (a measure of the proportion of total DNA present in the comet tail)",
				entry.getName());

	}

	private CodeList readCodeList(URI source) throws Exception {

		CodeListReader reader = new SkosCodeListReader();

		reader.setSource(new DefaultInputSupplier(source));

		IOReport report = reader.execute(new LogProgressIndicator());
		assertTrue(report.isSuccess());

		return reader.getCodeList();

	}

	private URI getResourceURI(String location) throws URISyntaxException {
		return getClass().getResource(location).toURI();
	}

}
