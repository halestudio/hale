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

		String id = "http://vocab.ices.dk/services/rdf/collection/PARAM/%25DNAtail";

		CodeList codeList = readCodeList(getResourceURI("/data/test3.rdf"));

		Collection<CodeEntry> entries = codeList.getEntries();
		assertFalse(entries.isEmpty());

		assertEquals(entries.size(), 4);

		assertNotNull(codeList.getLocation());
		assertNotNull(codeList.getIdentifier());

		CodeEntry entry = codeList.getEntryByIdentifier(id);

		assertEquals(
				"% DNA in tail (a measure of the proportion of total DNA present in the comet tail)",
				entry.getName());

	}

	/**
	 * 
	 * test read SKOS properties in preferred language.
	 * 
	 * @throws Exception throws exception if something wrong occurs
	 */
	@Test
	public void testSKOSFromRDF4_language_nl() throws Exception {

		String base_ns = "http://www.locationframework.eu/codelist/";

		CodeList codeList = readCodeList_WithLanguage(getResourceURI("/data/test4_lang.rdf"), "nl");

		Collection<CodeEntry> entries = codeList.getEntries();
		assertFalse(entries.isEmpty());
		assertEquals(entries.size(), 4);
		assertNotNull(codeList.getLocation());
		assertNotNull(codeList.getIdentifier());

		CodeEntry entry = codeList
				.getEntryByIdentifier(base_ns + "EuroGeoNamesLocationTypeValue/1");

		assertEquals("Landen, administratieve en overige gebieden", entry.getName());
		assertEquals(
				"Country, territorial units of a country for administrative purposes and other manmade areas.",
				entry.getDescription());
	}

	/**
	 * 
	 * test read SKOS properties in preferred language.
	 * 
	 * @throws Exception throws exception if something wrong occurs
	 */
	@Test
	public void testSKOSFromRDF4_language_en() throws Exception {

		String base_ns = "http://www.locationframework.eu/codelist/";

		CodeList codeList = readCodeList_WithLanguage(getResourceURI("/data/test4_lang.rdf"), "en");

		Collection<CodeEntry> entries = codeList.getEntries();
		assertFalse(entries.isEmpty());

		assertEquals(entries.size(), 4);

		assertNotNull(codeList.getLocation());
		assertNotNull(codeList.getIdentifier());

		CodeEntry entry = codeList
				.getEntryByIdentifier(base_ns + "EuroGeoNamesLocationTypeValue/1");

		assertEquals("Countries, administrative units and other areas", entry.getName());
		assertEquals(
				"Country, territorial units of a country for administrative purposes and other manmade areas.",
				entry.getDescription());
	}

	/**
	 * 
	 * test read SKOS properties in preferred language, as "de" not available,
	 * it will load "en" by default
	 * 
	 * @throws Exception throws exception if something wrong occurs
	 */
	@Test
	public void testSKOSFromRDF4_language_de() throws Exception {

		String base_ns = "http://www.locationframework.eu/codelist/";

		CodeList codeList = readCodeList_WithLanguage(getResourceURI("/data/test4_lang.rdf"), "de");

		Collection<CodeEntry> entries = codeList.getEntries();
		assertFalse(entries.isEmpty());

		assertEquals(entries.size(), 4);

		assertNotNull(codeList.getLocation());
		assertNotNull(codeList.getIdentifier());

		CodeEntry entry = codeList
				.getEntryByIdentifier(base_ns + "EuroGeoNamesLocationTypeValue/1");

		assertEquals("Countries, administrative units and other areas", entry.getName());
		assertEquals(
				"Country, territorial units of a country for administrative purposes and other manmade areas.",
				entry.getDescription());
	}

	/**
	 * 
	 * test read SKOS properties in preferred language (fallback).
	 * 
	 * @throws Exception throws exception if something wrong occurs
	 */
	@Test
	public void testSKOSFromRDF5_language_nl() throws Exception {

		CodeList codeList = readCodeList_WithLanguage(
				getResourceURI("/data/test4_lang_fallback.rdf"), "nl");

		Collection<CodeEntry> entries = codeList.getEntries();
		assertFalse(entries.isEmpty());
		assertEquals(entries.size(), 4);
		assertNotNull(codeList.getLocation());
		assertNotNull(codeList.getIdentifier());

		CodeEntry entry = codeList.getEntryByIdentifier("EuroGeoNamesLocationTypeValue/1");

		assertEquals("Landen, administratieve en overige gebieden", entry.getName());
		assertEquals(
				"Country, territorial units of a country for administrative purposes and other manmade areas.",
				entry.getDescription());
	}

	/**
	 * 
	 * test read SKOS properties in preferred language (fallback).
	 * 
	 * @throws Exception throws exception if something wrong occurs
	 */
	@Test
	public void testSKOSFromRDF5_language_en() throws Exception {

		CodeList codeList = readCodeList_WithLanguage(
				getResourceURI("/data/test4_lang_fallback.rdf"), "en");

		Collection<CodeEntry> entries = codeList.getEntries();
		assertFalse(entries.isEmpty());

		assertEquals(entries.size(), 4);

		assertNotNull(codeList.getLocation());
		assertNotNull(codeList.getIdentifier());

		CodeEntry entry = codeList.getEntryByIdentifier("EuroGeoNamesLocationTypeValue/1");

		assertEquals("Countries, administrative units and other areas", entry.getName());
		assertEquals(
				"Country, territorial units of a country for administrative purposes and other manmade areas.",
				entry.getDescription());
	}

	/**
	 * 
	 * test read SKOS properties in preferred language (fallback). As "de" not
	 * available, it will load "en" by default
	 * 
	 * @throws Exception throws exception if something wrong occurs
	 */
	@Test
	public void testSKOSFromRDF5_language_de() throws Exception {

		CodeList codeList = readCodeList_WithLanguage(
				getResourceURI("/data/test4_lang_fallback.rdf"), "de");

		Collection<CodeEntry> entries = codeList.getEntries();
		assertFalse(entries.isEmpty());

		assertEquals(entries.size(), 4);

		assertNotNull(codeList.getLocation());
		assertNotNull(codeList.getIdentifier());

		CodeEntry entry = codeList.getEntryByIdentifier("EuroGeoNamesLocationTypeValue/1");

		assertEquals("Countries, administrative units and other areas", entry.getName());
		assertEquals(
				"Country, territorial units of a country for administrative purposes and other manmade areas.",
				entry.getDescription());
	}

	private CodeList readCodeList(URI source) throws Exception {

		CodeListReader reader = new SkosCodeListReader();

		reader.setSource(new DefaultInputSupplier(source));

		IOReport report = reader.execute(new LogProgressIndicator());
		assertTrue(report.isSuccess());

		return reader.getCodeList();

	}

	private CodeList readCodeList_WithLanguage(URI source, final String language) throws Exception {

		SkosCodeListReader reader = new SkosCodeListReader() {

			/**
			 * @see eu.esdihumboldt.hale.io.codelist.skos.reader.SkosCodeListReader#getLangauge()
			 */
			@Override
			public String getLangauge() {
				return language;
			}
		};

		reader.setSource(new DefaultInputSupplier(source));

		IOReport report = reader.execute(new LogProgressIndicator());
		assertTrue(report.isSuccess());

		return reader.getCodeList();

	}

	private URI getResourceURI(String location) throws URISyntaxException {
		return getClass().getResource(location).toURI();
	}

}
