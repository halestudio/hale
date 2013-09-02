package eu.esdihumboldt.hale.io.codelist.inspire.reader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
 * 
 * TODO Type description
 * 
 * @author Kai Schwierczek
 */
public class INSPIRECodeListReader extends AbstractImportProvider implements CodeListReader {

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
		try (InputStream is = getSource().getInput()) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);

			URI loc = getSource().getLocation();

			reporter.setSuccess(parse(doc, loc, reporter));

			progress.setCurrentTask("Code list loaded.");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return reporter;
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
