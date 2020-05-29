/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.gml.writer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stax.StAXResult;

import org.geotools.gml2.SrsSyntax;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.locationtech.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.groovy.DefinitionAccessor;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.InspireUtil;
import eu.esdihumboldt.hale.io.gml.writer.internal.AbstractXMLStreamWriterDecorator;
import eu.esdihumboldt.hale.io.gml.writer.internal.StreamGmlWriter;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;
import eu.esdihumboldt.util.Pair;
import eu.esdihumboldt.util.groovy.paths.Path;

/**
 * Instance writer for Inspire schemas, using SpatialDataSet as container.
 * 
 * @author Kai Schwierczek
 * @author Simon Templer
 */
public class InspireInstanceWriter extends GmlInstanceWriter {

	/**
	 * Identifier of the writer.
	 */
	public static final String ID = "eu.esdihumboldt.hale.io.inspiregml.writer";

	/**
	 * The parameter name for the identifier.localId attribute, defaults to an
	 * empty string.
	 */
	public static final String PARAM_SPATIAL_DATA_SET_LOCALID = "inspire.sds.localId";

	/**
	 * The parameter name for the identifier.namespace attribute, defaults to an
	 * empty string.
	 */
	public static final String PARAM_SPATIAL_DATA_SET_NAMESPACE = "inspire.sds.namespace";

	/**
	 * The parameter name for the XML file to load to fill the metadata with.
	 */
	public static final String PARAM_SPATIAL_DATA_SET_METADATA_FILE = "inspire.sds.metadata";

	/**
	 * The parameter name metadata provided as DOM.
	 */
	public static final String PARAM_SPATIAL_DATA_SET_METADATA_DOM = "inspire.sds.metadata.inline";

	/**
	 * The parameter specifying whether a dataset feed should be created,
	 * defaults to <code>false</code>.
	 */
	public static final String PARAM_SPATIAL_DATA_SET_CREATE_FEED = "inspire.sds.create_feed";

	private final Set<TypeDefinition> types = new HashSet<>();
	private final Multiset<CRSDefinition> crss = HashMultiset.create();

	/**
	 * Default constructor.
	 */
	public InspireInstanceWriter() {
		addSupportedParameter(PARAM_SPATIAL_DATA_SET_LOCALID);
		addSupportedParameter(PARAM_SPATIAL_DATA_SET_NAMESPACE);
		addSupportedParameter(PARAM_SPATIAL_DATA_SET_METADATA_FILE);
		addSupportedParameter(PARAM_SPATIAL_DATA_SET_METADATA_DOM);
		addSupportedParameter(PARAM_SPATIAL_DATA_SET_CREATE_FEED);

		for (String param : InspireDatasetFeedWriter.getAdditionalParams()) {
			addSupportedParameter(param);
		}

		// set alternating default values

		// EPSG prefix:
		setCustomEPSGPrefix(SrsSyntax.OGC_HTTP_URI.getPrefix());
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		// first write gml file (also collects types-set)
		super.execute(progress, reporter);

		// run feed writer if applicable
		if (getParameter(PARAM_SPATIAL_DATA_SET_CREATE_FEED).as(Boolean.class, false)) {
			InspireDatasetFeedWriter feedWriter = new InspireDatasetFeedWriter();
			for (String copyParam : InspireDatasetFeedWriter.getAdditionalParams()) {
				feedWriter.setParameter(copyParam, getParameter(copyParam));
			}
			feedWriter.setOccurringTypes(types);
			feedWriter.setOccurringCRSs(crss);

			feedWriter.setParameter(PARAM_TARGET,
					Value.of(getDatasetFeedTarget(getTarget().getLocation()).toString()));

			reporter.importMessages(feedWriter.execute(progress));
			// Ignore success of feedWriter-report, since main success criteria
			// is successfully writing the data.
		}

		return reporter;
	}

	/**
	 * Transforms the given GML file target to a target URI for the dataset feed
	 * creation.
	 * 
	 * @param gmlTarget the original GML target
	 * @return the dataset feed target
	 */
	public static URI getDatasetFeedTarget(URI gmlTarget) {
		String location = gmlTarget.toString();
		String locationPath;
		String locationName;
		int nameIndex = location.lastIndexOf('/');
		if (nameIndex != -1) {
			locationName = location.substring(nameIndex + 1);
			locationPath = location.substring(0, nameIndex + 1);
		}
		else {
			locationName = location;
			locationPath = "";
		}
		// XXX split filename at which '.'? (for now first)
		int suffixIndex = locationName.indexOf('.');
		if (suffixIndex != -1) {
			locationName = locationName.substring(0, suffixIndex);
		}
		String feedTaregt = locationPath + locationName + "-feed.atom";
		return URI.create(feedTaregt);
	}

	/**
	 * @see StreamGmlWriter#findDefaultContainter(XmlIndex, IOReporter)
	 */
	@Override
	protected XmlElement findDefaultContainter(XmlIndex targetIndex, IOReporter reporter) {
		XmlElement result = InspireUtil.findSpatialDataSet(targetIndex);
		if (result != null)
			return result;

		throw new IllegalStateException(MessageFormat.format(
				"Element {0} not found in the schema.", "SpatialDataSet"));
	}

	@Override
	protected void writeMember(Instance instance, TypeDefinition type, IOReporter report)
			throws XMLStreamException {
		// collect written types in case a dataset feed will be written
		types.add(type);
		super.writeMember(instance, type, report);
	}

	@Override
	protected Pair<Geometry, CRSDefinition> extractGeometry(Object value, boolean allowConvert,
			IOReporter report) {
		// collect used CRS definitions
		Pair<Geometry, CRSDefinition> result = super.extractGeometry(value, allowConvert, report);
		if (result != null) {
			crss.add(result.getSecond());
		}
		return result;
	}

	/**
	 * @see StreamGmlWriter#writeAdditionalElements(XMLStreamWriter,
	 *      TypeDefinition, IOReporter)
	 */
	@Override
	protected void writeAdditionalElements(XMLStreamWriter writer,
			TypeDefinition containerDefinition, IOReporter reporter) throws XMLStreamException {
		super.writeAdditionalElements(writer, containerDefinition, reporter);

		// determine INSPIRE identifier and metadata names
		Path<Definition<?>> localIdPath = new DefinitionAccessor(containerDefinition)
				.findChildren("identifier").findChildren("Identifier").findChildren("localId")
				.eval(false);

		QName identifierName = localIdPath.getElements().get(1).getName();
		Definition<?> internalIdentifierDef = localIdPath.getElements().get(2);
		QName internalIdentifierName = internalIdentifierDef.getName();
		QName localIdName = localIdPath.getElements().get(3).getName();

		Path<Definition<?>> namespacePath = new DefinitionAccessor(internalIdentifierDef)
				.findChildren("namespace").eval(false);
		QName namespaceName = namespacePath.getElements().get(1).getName();

		Path<Definition<?>> metadataPath = new DefinitionAccessor(containerDefinition)
				.findChildren("metadata").eval(false);
		QName metadataName = metadataPath.getElements().get(1).getName();

		// write INSPIRE identifier
		writer.writeStartElement(identifierName.getNamespaceURI(), identifierName.getLocalPart());
		writer.writeStartElement(internalIdentifierName.getNamespaceURI(),
				internalIdentifierName.getLocalPart());
		writer.writeStartElement(localIdName.getNamespaceURI(), localIdName.getLocalPart());
		writer.writeCharacters(getParameter(PARAM_SPATIAL_DATA_SET_LOCALID).as(String.class, ""));
		writer.writeEndElement();
		writer.writeStartElement(namespaceName.getNamespaceURI(), namespaceName.getLocalPart());
		writer.writeCharacters(getParameter(PARAM_SPATIAL_DATA_SET_NAMESPACE).as(String.class, ""));
		writer.writeEndElement();
		writer.writeEndElement();
		writer.writeEndElement();

		// write metadata
		writer.writeStartElement(metadataName.getNamespaceURI(), metadataName.getLocalPart());

		// retrieve metadata element (if any)
		Element metadataElement = getParameter(PARAM_SPATIAL_DATA_SET_METADATA_DOM).as(
				Element.class);

		// metadata from file (if any)
		if (metadataElement == null) {
			String metadataFile = getParameter(PARAM_SPATIAL_DATA_SET_METADATA_FILE).as(
					String.class);
			if (metadataFile != null && !metadataFile.isEmpty()) {
				try (InputStream input = new BufferedInputStream(new FileInputStream(new File(
						metadataFile)))) {
					metadataElement = findMetadata(input, reporter);
				} catch (IOException e) {
					reporter.warn(new IOMessageImpl("Could not load specified metadata file.", e));
				}
			}
		}

		if (metadataElement != null) {
			try {
				writeElement(metadataElement, writer);
			} catch (TransformerException e) {
				reporter.warn(new IOMessageImpl("Couldn't include specified metadata file.", e));
			}
		}
		else {
			writer.writeAttribute(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "nil", "true");
		}

		writer.writeEndElement();
	}

	/**
	 * Loads the given file and tries to find a MD_Metadata element.
	 * 
	 * @param input the metadata source
	 * @param reporter the reporter
	 * @return the metadata element or <code>null</code> if it couldn't be found
	 */
	private Element findMetadata(InputStream input, IOReporter reporter) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc;
		try {
			doc = dbf.newDocumentBuilder().parse(input);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			reporter.warn(new IOMessageImpl("Couldn't parse specified metadata file.", e));
			return null;
		}
		NodeList nl = doc.getElementsByTagNameNS("http://www.isotc211.org/2005/gmd", "MD_Metadata");

		Element result = null;
		if (nl.getLength() == 1)
			result = (Element) nl.item(0);
		else if (nl.getLength() == 0)
			reporter.warn(new IOMessageImpl(
					"Couldn't include specified metadata file, no MD_Metadata element found.", null));
		else {
			// XXX Maybe ask the user somehow? Or better not include it?
			reporter.warn(new IOMessageImpl(
					"Found multiple MD_Metadata elements. Using first one.", null));
			result = (Element) nl.item(0);
		}

		return result;
	}

	// XXX If needed writeElement could go to some Util-Class.

	/**
	 * Writes a DOM element to a stream writer without starting a new document.
	 * 
	 * @param element the element to write
	 * @param writer the writer to write to
	 * @throws TransformerException if an unrecoverable error occurs during the
	 *             course of the transformation
	 */
	private void writeElement(Element element, XMLStreamWriter writer) throws TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = tf.newTransformer();
		t.transform(new DOMSource(element), new StAXResult(new InternalXMLStreamWriter(writer)));
	}

	/**
	 * Stream writer for elements. Ignores startDocument and endDocument calls.
	 * 
	 * @author Kai Schwierczek
	 */
	private class InternalXMLStreamWriter extends AbstractXMLStreamWriterDecorator {

		/**
		 * @param writer the writer to forward to
		 */
		protected InternalXMLStreamWriter(XMLStreamWriter writer) {
			super(writer);
		}

		@Override
		public void writeDTD(String dtd) throws XMLStreamException {
			// ignore DTD
		}

		@Override
		public void writeEndDocument() throws XMLStreamException {
			// ignore endDocument
		}

		@Override
		public void writeStartDocument() throws XMLStreamException {
			// ignore startDocument
		}

		@Override
		public void writeStartDocument(String encoding, String version) throws XMLStreamException {
			// ignore startDocument
		}

		@Override
		public void writeStartDocument(String version) throws XMLStreamException {
			// ignore startDocument
		}

		@Override
		public void close() throws XMLStreamException {
			writer.flush();
		}
	}
}
