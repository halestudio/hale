/*
 * Copyright (c) 2014 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.gml.writer;
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import org.w3c.dom.Document
import org.w3c.dom.Element

import com.google.common.collect.Multiset

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator
import eu.esdihumboldt.hale.common.core.io.impl.AbstractExportProvider
import eu.esdihumboldt.hale.common.core.io.report.IOReport
import eu.esdihumboldt.hale.common.core.io.report.IOReporter
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier
import eu.esdihumboldt.hale.common.instance.geometry.CRSDefinitionUtil
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements
import groovy.json.JsonException
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

/**
 * Writer for INSPIRE data set feeds as used by INSPIRE download services.
 * 
 * @author Kai Schwierczek
 */
@CompileStatic
public class InspireDatasetFeedWriter extends AbstractExportProvider {

	/**
	 * The parameter name for the title attribute.
	 */
	public static final String PARAM_FEED_TITLE = "inspire.feed.title";

	/**
	 * The parameter name for the subtitle attribute.
	 */
	public static final String PARAM_FEED_SUBTITLE = "inspire.feed.subtitle";

	/**
	 * The parameter name for the rights attribute.
	 */
	public static final String PARAM_FEED_RIGHTS = "inspire.feed.rights";

	/**
	 * The parameter name for the author name attribute.
	 */
	public static final String PARAM_FEED_AUTHOR_NAME = "inspire.feed.author_name";

	/**
	 * The parameter name for the author mail attribute.
	 */
	public static final String PARAM_FEED_AUTHOR_MAIL = "inspire.feed.author_mail";

	/**
	 * The parameter name for the id/selflink attribute.
	 */
	public static final String PARAM_FEED_SELFLINK = "inspire.feed.selflink";

	/**
	 * The parameter name for the GML link attribute.
	 */
	public static final String PARAM_FEED_GMLLINK = "inspire.feed.gmllink";

	private static final String ATOM_NS = "http://www.w3.org/2005/Atom";
	private static final String GEORSS_NS = "http://www.georss.org/georss";

	private static final String INSPIRE_FCD = "http://inspire.ec.europa.eu/featureconcept/featureconcept.en.json";

	private static final DateTimeFormatter dateFormat = ISODateTimeFormat.dateTimeNoMillis();

	private Set<TypeDefinition> types;
	private Multiset<CRSDefinition> crss;

	/**
	 * Returns the parameters used by this writer.
	 * 
	 * @return the parameters used by this writer
	 */
	public static String[] getAdditionalParams() {
		return [
			PARAM_FEED_TITLE,
			PARAM_FEED_SUBTITLE,
			PARAM_FEED_RIGHTS,
			PARAM_FEED_AUTHOR_NAME,
			PARAM_FEED_AUTHOR_MAIL,
			PARAM_FEED_SELFLINK,
			PARAM_FEED_GMLLINK
		].toArray();
	}

	/**
	 * Default constructor.
	 */
	public InspireDatasetFeedWriter() {
		for (String param : getAdditionalParams()) {
			addSupportedParameter(param);
		}
	}

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
	throws IOProviderConfigurationException, IOException {
		try {
			Document doc = createDatasetFeed(reporter);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			t.transform(new DOMSource(doc), new StreamResult(getTarget().getOutput()));
			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.error(new IOMessageImpl(e.getLocalizedMessage(), e));
			reporter.setSuccess(false);
		}
		return reporter;
	}

	@Override
	protected String getDefaultTypeName() {
		return "Atom XML";
	}

	/**
	 * Sets the occurring types. References will be included to the INSPIRE
	 * Feature Concept Dictionary when possible.
	 * 
	 * @param types the occurring types
	 */
	public void setOccurringTypes(Set<TypeDefinition> types) {
		this.types = types;
	}

	/**
	 * Sets the occurring CRSs.
	 * 
	 * @param crss the occurring CRSs
	 */
	public void setOccurringCRSs(Multiset<CRSDefinition> crss) {
		this.crss = crss;
	}

	private Document createDatasetFeed(IOReporter reporter) throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = dbf.newDocumentBuilder().newDocument();

		Element feed = doc.createElementNS(ATOM_NS, "feed");
		doc.appendChild(feed);

		feed.setAttribute("xmlns", ATOM_NS);
		feed.setAttribute("xmlns:georss", GEORSS_NS);

		String title = getParameter(PARAM_FEED_TITLE).as(String.class, "Dataset feed");
		// TG Requirement 21
		feed.appendChild(createFeedTextNode(doc, "title", title));

		// TG Recommendation 8
		if (!getParameter(PARAM_FEED_SUBTITLE).isEmpty()) {
			feed.appendChild(createFeedTextNode(doc, "subtitle", getParameter(PARAM_FEED_SUBTITLE)
					.as(String.class)));
		}

		String datasetLink = getParameter(PARAM_FEED_SELFLINK).as(String.class);
		// TG Requirement 22
		feed.appendChild(createFeedTextNode(doc, "id", datasetLink));

		// TG Requirement 23
		feed.appendChild(createFeedTextNode(doc, "rights",
				getParameter(PARAM_FEED_RIGHTS).as(String.class)));

		String updatedString = dateFormat.print(System.currentTimeMillis());
		// TG Requirement 24
		feed.appendChild(createFeedTextNode(doc, "updated", updatedString));

		// TG Requirement 25
		feed.appendChild(createFeedAuthor(doc, getParameter(PARAM_FEED_AUTHOR_NAME)
				.as(String.class), getParameter(PARAM_FEED_AUTHOR_MAIL).as(String.class)));

		// TG Requirement 26
		// at least one entry -> see below

		// TG Requirement 27
		// one entry for each format/CRS combination -> gml/?

		// TG Requirement 28
		if (types != null && !types.isEmpty()) {
			addFCDLinks(doc, feed, types, reporter);
		}

		// no requirement?
		feed.appendChild(createFeedLink(doc, datasetLink, "self", "application/atom+xml",
				"This document"));

		// TG Recommendation 9
		//		feed.appendChild(createFeedLink(doc, baseURL + "/datasets.atom", "up",
		//				"application/atom+xml", "The parent service feed document"));

		// currently only one format/CRS available
		Element entry = doc.createElementNS(ATOM_NS, "entry");
		feed.appendChild(entry);

		// no requirement, except valid atom
		entry.appendChild(createFeedTextNode(doc, "title", title));

		String downloadLink = getParameter(PARAM_FEED_GMLLINK).as(String.class);
		// TG Requirement 29
		Element linkElement = createFeedLink(doc, downloadLink, "alternate", "application/gml+xml",
				"The dataset encoded as GML");
		// XXX add length-attribute (size in bytes) if possible
		entry.appendChild(linkElement);

		// TG Requirement 30
		// type of download link has to be valid (application/gml+xml is)

		// TG Requirement 31 - not applicable
		// links to the same type/CRS in a different language must use hreflang

		// TG Requirement 32 - not applicable
		// if the dataset is splitted, provide a link to each part with
		// rel=section

		// TG Requirement 33 - not applicable
		// if the dataset is splitted, provide description in content, or
		// external file

		// TG Recommendation 10 - not applicable
		// if the dataset is splitted, may provide 'bbox' attribute (in
		// georss:bbox structure)

		// TG Recommendation 11 - not applicable
		// if the dataset is splitted, may provide 'time' attribute (ISO8601)

		// TG Requirement 34
		// only types in the INSPIRE media types registry shall be used (gml is
		// in there)

		// TG Recommendation 12
		// for uncompressed files, compression is offered by HTTP 1.1

		// TG Recommendation 6+7
		// georss element

		// TG Requirement 35
		if (crss != null) {
			// XXX which CRS to output, if there are multiple/none?
			// using all occurring for now
			// probably better: use the one with the most occurrences
			for (Multiset.Entry<CRSDefinition> crsEntry : crss.entrySet()) {
				// XXX what about null entry?
				if (crsEntry.getElement() != null) {
					String epsg = CRSDefinitionUtil.getEPSG(crsEntry.getElement());
					// XXX lookup "better" name
					if (epsg != null) {
						entry.appendChild(createCategory(doc,
								"http://www.opengis.net/def/crs/EPSG/0/" + epsg,
								"EPSG " + epsg));
					}
				}
			}
		}

		// no requirement, except valid atom
		entry.appendChild(createFeedTextNode(doc, "id", downloadLink));
		entry.appendChild(createFeedTextNode(doc, "updated", updatedString));

		return doc;
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private void addFCDLinks(Document doc, Element feed, Set<TypeDefinition> types, IOReporter reporter) {
		DefaultInputSupplier input = new DefaultInputSupplier(URI.create(INSPIRE_FCD));
		try {
			def json = new JsonSlurper().parse(new InputStreamReader(input.getInput()));
			def fcdMap = [:]
			json.containeditems.each {
				fcdMap[it.featureconcept.umlname] = it.featureconcept.id
			}
			for (TypeDefinition type : types) {
				def names = type.getConstraint(XmlElements).elements.collect {it.name.localPart}
				def href = names.findResult { fcdMap[it] }
				if (href) {
					feed.appendChild(createFeedLink(doc, href, "describedby",
							"text/html", null));
				} else {
					// XXX target may be another scheme, if the dataset is not in an
					// interoperable format
				}

			}
		} catch(IOException | JsonException e) {
			reporter.warn(new IOMessageImpl("Could not access INSPIRE Feature Concept Dictionary.", e));
		}
	}

	private Element createCategory(Document doc, String term, String label) {
		Element category = doc.createElementNS(ATOM_NS, "category");

		category.setAttribute("term", term);
		category.setAttribute("label", label);

		return category;
	}

	private Element createFeedTextNode(Document doc, String tag, String text) {
		return createTextNode(doc, tag, text, ATOM_NS);
	}

	private Element createTextNode(Document doc, String tag, String text, String ns) {
		Element node = doc.createElementNS(ns, tag);
		node.setTextContent(text);
		return node;
	}

	private Element createFeedLink(Document doc, String href, String rel, String type, String title) {
		return createFeedLink(doc, href, rel, type, title, null);
	}

	private Element createFeedLink(Document doc, String href, String rel, String type,
			String title, String hreflang) {
		Element link = doc.createElementNS(ATOM_NS, "link");

		link.setAttribute("href", href);
		link.setAttribute("rel", rel);
		link.setAttribute("type", type);
		if (title != null)
			link.setAttribute("title", title);
		if (hreflang != null)
			link.setAttribute("hreflang", hreflang);

		return link;
	}

	private Element createFeedAuthor(Document doc, String name, String mail) {
		Element title = doc.createElementNS(ATOM_NS, "author");

		// must contain atom:name, atom:email; may contain atom:uri and any
		// other element
		title.appendChild(createFeedTextNode(doc, "name", name));
		title.appendChild(createFeedTextNode(doc, "email", mail));

		return title;
	}
}
