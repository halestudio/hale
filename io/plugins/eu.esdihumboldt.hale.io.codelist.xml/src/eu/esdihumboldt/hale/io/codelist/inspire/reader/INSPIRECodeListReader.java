package eu.esdihumboldt.hale.io.codelist.inspire.reader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.common.codelist.CodeList.CodeEntry;
import eu.esdihumboldt.hale.common.codelist.io.CodeListReader;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;

/**
 * Load XML code lists as provided by the INSPIRE registry.
 * 
 * @author Kai Schwierczek
 */
public class INSPIRECodeListReader extends AbstractImportProvider implements CodeListReader {

	/**
	 * The provider ID.
	 */
	public static final String PROVIDER_ID = "eu.esdihumboldt.hale.io.codelist.inspire.reader";

	private CodeList codelist;

	@Override
	public boolean isCancelable() {
		return false; // TODO
	}

	/**
	 * @see CodeListReader#getCodeList()
	 */
	@Override
	public CodeList getCodeList() {
		return codelist;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Loading code list.", ProgressIndicator.UNKNOWN);

		Document doc;

		URI loc = getSource().getLocation();
		if (loc != null && (loc.getScheme().equals("http") || loc.getScheme().equals("https"))) {
			// load with HTTP client
			// and provide headers to retrieve correct format and language
			doc = loadXmlDocument(loc);
		}
		else {
			// just access stream
			try (InputStream is = getSource().getInput()) {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				doc = db.parse(is);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		try {
			reporter.setSuccess(parse(doc, loc, reporter));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		progress.setCurrentTask("Code list loaded.");

		return reporter;
	}

	/**
	 * Load an XML document via HTTP, providing headers to request proper format
	 * and language.
	 * 
	 * @param loc the location
	 * @return the XML document
	 * @throws IOException if reading the document fails
	 * @throws ClientProtocolException if retrieving the document fails
	 */
	public static Document loadXmlDocument(URI loc) throws ClientProtocolException, IOException {
		return Request.Get(loc)
				.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_XML.getMimeType())
				.addHeader(HttpHeaders.ACCEPT_LANGUAGE, Locale.getDefault().getLanguage())
				.execute().handleResponse(new ResponseHandler<Document>() {

					@Override
					public Document handleResponse(HttpResponse response)
							throws ClientProtocolException, IOException {
						StatusLine statusLine = response.getStatusLine();
						HttpEntity entity = response.getEntity();
						if (statusLine.getStatusCode() >= 300) {
							throw new HttpResponseException(statusLine.getStatusCode(), statusLine
									.getReasonPhrase());
						}
						if (entity == null) {
							throw new ClientProtocolException("Response contains no content");
						}
						DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
						try {
							DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
							ContentType contentType = ContentType.getOrDefault(entity);
							if (!contentType.getMimeType().equals(
									ContentType.APPLICATION_XML.getMimeType())) {
								throw new ClientProtocolException("Unexpected content type:"
										+ contentType);
							}
							return docBuilder.parse(entity.getContent());
						} catch (ParserConfigurationException ex) {
							throw new IllegalStateException(ex);
						} catch (SAXException ex) {
							throw new ClientProtocolException("Malformed XML document", ex);
						}
					}
				});
	}

	private boolean parse(Document doc, URI location, IOReporter reporter) throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();

		String name = null;
		String description = null;
		String namespace = null;

		namespace = (String) xpath.evaluate("codelist/@id", doc, XPathConstants.STRING);
		if (namespace == null) {
			reporter.error(new IOMessageImpl("No id attribute present in INSPIRE codelist.", null));
			return false;
		}

		// XXX what about multiple labels or definitions?
		NodeList labels = (NodeList) xpath.evaluate("codelist/label", doc, XPathConstants.NODESET);
		if (labels.getLength() > 0)
			name = labels.item(0).getTextContent();
		else {
			reporter.error(new IOMessageImpl("No label present in INSPIRE codelist.", null));
			return false;
		}
		NodeList definitions = (NodeList) xpath.evaluate("codelist/definition", doc,
				XPathConstants.NODESET);
		if (definitions.getLength() > 0)
			description = definitions.item(0).getTextContent();
		// XXX ignore descriptions for now

		// also ignore status, extensibility, register, applicationschema and
		// theme

		INSPIRECodeList codelist = new INSPIRECodeList(namespace, name, description, location);

		NodeList entries = (NodeList) xpath.evaluate("codelist/containeditems/value", doc,
				XPathConstants.NODESET);
		for (int i = 0; i < entries.getLength(); i++)
			addEntry(entries.item(i), codelist, xpath, reporter);

		this.codelist = codelist;
		return true;
	}

	private void addEntry(Node item, INSPIRECodeList codelist, XPath xpath, IOReporter reporter)
			throws Exception {
		String name = null;
		String description = null;
		String identifier = null;
		String namespace = null;

		identifier = (String) xpath.evaluate("@id", item, XPathConstants.STRING);
		if (identifier == null) {
			reporter.warn(new IOMessageImpl(
					"No id attribute present in a value of the INSPIRE codelist. Skipping value.",
					null));
			return;
		}

		// XXX what about multiple labels or definitions?
		NodeList labels = (NodeList) xpath.evaluate("label", item, XPathConstants.NODESET);
		if (labels.getLength() > 0)
			name = labels.item(0).getTextContent();
		else {
			reporter.warn(new IOMessageImpl("No label present in a value of the INSPIRE codelist.",
					null));
			name = identifier;
		}
		NodeList definitions = (NodeList) xpath
				.evaluate("definition", item, XPathConstants.NODESET);
		if (definitions.getLength() > 0)
			description = definitions.item(0).getTextContent();
		// in schema no description, but in data; anyways, ignore it for now

		// also ignore status, register, applicationschema and theme

		namespace = (String) xpath.evaluate("codelist/@id", item, XPathConstants.STRING);
		// XXX I guess namespace has to be the same as the codelist. Check this?

		codelist.addEntry(new CodeEntry(name, description, identifier, namespace));
	}

	@Override
	protected String getDefaultTypeName() {
		return "INSPIRE code list";
	}
}
