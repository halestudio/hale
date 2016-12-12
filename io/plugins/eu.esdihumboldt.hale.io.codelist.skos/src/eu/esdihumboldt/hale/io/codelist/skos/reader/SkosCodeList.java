package eu.esdihumboldt.hale.io.codelist.skos.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.semanticweb.skos.SKOSAnnotation;
import org.semanticweb.skos.SKOSConcept;
import org.semanticweb.skos.SKOSConceptScheme;
import org.semanticweb.skos.SKOSDataset;
import org.semanticweb.skos.SKOSEntity;
import org.semanticweb.skos.SKOSUntypedLiteral;
import org.semanticweb.skosapibinding.SKOSManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.codelist.CodeList;

/**
 * Reads a SKOS code list and treat concepts as code entries
 * 
 * @author Arun
 */
public class SkosCodeList implements CodeList {

	private static final ALogger log = ALoggerFactory.getLogger(SkosCodeList.class);

	private static final String SKOS_URI = "http://www.w3.org/2004/02/skos/core#";
	private static final String SKOS_PREF_LABEL = SKOS_URI + "prefLabel";
	private static final String SKOS_DEF_LABEL = SKOS_URI + "definition";
	private static final String SKOS_TOPCONCEPT_LABEL = SKOS_URI + "topConceptOf";
	private static final String SKOS_INSCHEME_LABEL = SKOS_URI + "inScheme";

	private static final String USAGENOTE_LABEL = "usageNote";

	private String identifier;

	private String namespace;

	private String description;

	private final URI location;

	private final Map<String, CodeEntry> entriesByName = new LinkedHashMap<String, CodeEntry>();

	private final Map<String, CodeEntry> entriesByIdentifier = new LinkedHashMap<String, CodeEntry>();

	private SKOSDataset dataSet;

	/**
	 * Create a code list from a RDF file and URL.
	 * 
	 * @param in input stream of source
	 * @param location the location from where code list loaded
	 * @throws Exception if something will go wrong
	 */
	public SkosCodeList(InputStream in, URI location) throws Exception {
		this.location = location;
		this.identifier = null;
		try {

			SKOSManager manager = new SKOSManager();
			dataSet = manager.loadDatasetFromPhysicalURI(location);

			// get ConceptSchemes
			if (!loadConceptScheme())
				if (!loadConcepts()) {
					if (!loadConceptsAsXML(in)) {
						throw new RuntimeException("no concept found!");
					}
				}

			this.dataSet = null;

		} catch (Exception ex) {
			log.error("Error reading skos code list", ex);
			throw ex;
		}
	}

	@Override
	public Collection<CodeEntry> getEntries() {
		return new ArrayList<CodeEntry>(entriesByIdentifier.values());
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public String getIdentifier() {
		if (identifier != null)
			return identifier;

		if (location != null)
			return location.toString();

		return null;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public CodeEntry getEntryByName(String name) {
		return entriesByName.get(name);
	}

	@Override
	public CodeEntry getEntryByIdentifier(String identifier) {
		return entriesByIdentifier.get(identifier);
	}

	@Override
	public URI getLocation() {
		return location;
	}

	private boolean loadConceptScheme() {

		Set<SKOSConceptScheme> schemes = dataSet.getSKOSConceptSchemes();

		if (schemes.isEmpty())
			return false;

		// get Scheme from uri
		SKOSConceptScheme scheme = schemes.iterator().next();

		// get annotation of ConceptScheme
		handleConceptSchemeNode(scheme);

		// i can get all the concepts from this scheme
		loadConcepts(scheme);

		return true;

	}

	private boolean loadConcepts() {
		return loadConcepts(null);
	}

	private boolean loadConcepts(SKOSConceptScheme scheme) {
		Set<SKOSConcept> concepts;
		if (scheme == null)
			concepts = dataSet.getSKOSConcepts();
		else {
			// get Concepts of Scheme
			concepts = scheme.getConceptsInScheme(dataSet);

			// If isEmpty, then try to load from dataSet
			if (concepts.isEmpty())
				concepts = dataSet.getSKOSConcepts();
		}

		if (concepts.isEmpty())
			return false;

		for (SKOSConcept conceptsInScheme : concepts) {
			// System.err.println("\tConcepts: " + conceptsInScheme.getURI());
			// get Annotation of Concept
			addConcept(conceptsInScheme);
		}

		return true;
	}

	private void handleConceptSchemeNode(SKOSEntity entity) {
		String namespace = null;
		String description = null;
		String identifier = null;
		String usageNote = null;

		for (SKOSAnnotation anno : entity.getSKOSAnnotations(dataSet)) {
			// System.err.print("\t\tAnnotation: " + anno.getURI() + "-> ");
			if (anno.isAnnotationByConstant()) {
				if (!anno.getAnnotationValueAsConstant().isTyped()) {
					SKOSUntypedLiteral con = anno.getAnnotationValueAsConstant()
							.getAsSKOSUntypedLiteral();

					if (isDefinition(anno.getURI().toString())) {
						description = con.getLiteral();
					}
					else if (isUsageNote(anno.getURI().toString())) {
						usageNote = con.getLiteral();
					}
				}
			}
		}
		namespace = entity.getURI().toString();
		identifier = namespace;

		if (description != null && usageNote != null)
			description += "\n\n" + usageNote;

		this.namespace = namespace;
		this.description = description;
		this.identifier = identifier;
	}

	private void addConcept(SKOSEntity entity) {

		String namespace = null;
		String name = null;
		String description = null;
		String usageNote = null;
		String identifier = null;

		String topConcept = null;

		for (SKOSAnnotation anno : entity.getSKOSAnnotations(dataSet)) {
			// System.err.print("\t\tAnnotation: " + anno.getURI() + "-> ");
			if (anno.isAnnotationByConstant()) {
				if (!anno.getAnnotationValueAsConstant().isTyped()) {
					SKOSUntypedLiteral con = anno.getAnnotationValueAsConstant()
							.getAsSKOSUntypedLiteral();
					if (isPrefLabel(anno.getURI().toString())) {
						name = con.getLiteral();
					}
					else if (isDefinition(anno.getURI().toString())) {
						description = con.getLiteral();
					}
					else if (isTopConcept(anno.getURI().toString())) {
						topConcept = con.getLiteral();
					}
					else if (isUsageNote(anno.getURI().toString())) {
						usageNote = con.getLiteral();
					}
				}
			}
		}

		if (this.namespace == null)
			this.namespace = topConcept;

		namespace = entity.getURI().toString();
		identifier = entity.getURI().toString();

		if (description != null && usageNote != null)
			description += "\n\n" + usageNote;

		if (name != null && description != null) {
			CodeEntry entry = new CodeEntry(name, description, identifier, namespace);
			this.entriesByName.put(name, entry);
			this.entriesByIdentifier.put(identifier, entry);
		}
	}

	private boolean isPrefLabel(String uri) {
		return SKOS_PREF_LABEL.equals(uri);
	}

	private boolean isDefinition(String uri) {
		return SKOS_DEF_LABEL.equals(uri);
	}

	private boolean isUsageNote(String uri) {
		return uri.endsWith(USAGENOTE_LABEL);
	}

	private boolean isTopConcept(String uri) {
		return SKOS_TOPCONCEPT_LABEL.equals(uri) || SKOS_INSCHEME_LABEL.equals(uri);
	}

	@SuppressWarnings("unused")
	private String extractIdentifier(String uri) {
		String id;
		id = uri.substring(uri.lastIndexOf("/") + 1, uri.length());
		return id;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
		return result;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SkosCodeList other = (SkosCodeList) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		}
		else if (!identifier.equals(other.identifier))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		}
		else if (!location.equals(other.location))
			return false;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		}
		else if (!namespace.equals(other.namespace))
			return false;
		return true;
	}

	private boolean loadConceptsAsXML(InputStream in) throws Exception {
		String namespace = null;
		String name = null;
		String description = null;
		String identifier = null;

		final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = builderFactory.newDocumentBuilder();

			builder.setEntityResolver(new EntityResolver() {

				@Override
				public InputSource resolveEntity(String publicId, String systemId)
						throws SAXException, IOException {
					return new InputSource(new StringReader("")); //$NON-NLS-1$
				}
			});

			Document doc = builder.parse(in);

			// read scheme
			NodeList listOfConceptScheme = doc.getElementsByTagName("skos:conceptScheme");

			if (listOfConceptScheme != null) {
				// will read first Concept scheme
				Node conceptSchemeNode = listOfConceptScheme.item(0);
				if (conceptSchemeNode != null
						&& conceptSchemeNode.getNodeType() == Node.ELEMENT_NODE) {
					Element conceptScheme = (Element) conceptSchemeNode;
					this.namespace = conceptScheme.getAttribute("rdf:about");
					this.identifier = namespace;

					NodeList children = conceptScheme.getChildNodes();

					for (int j = 0; j < children.getLength(); j++) {

						Node nd = children.item(j);

						if (nd != null) {
							String nodeName = nd.getNodeName();
							if (nodeName.equals("skos:definition")) {
								this.description = nd.getNodeValue();
							}
							else if (this.description == null && nodeName.endsWith("description")) {
								this.description = nd.getNodeValue();
							}
						}
					}
				}
			}

			NodeList listOfConcepts = doc.getElementsByTagName("skos:concept");
			int totalConcepts = listOfConcepts.getLength();

			if (totalConcepts == 0)
				return false;

			for (int i = 0; i < listOfConcepts.getLength(); i++) {

				Node conceptNode = listOfConcepts.item(i);
				if (conceptNode != null && conceptNode.getNodeType() == Node.ELEMENT_NODE) {

					Element concept = (Element) conceptNode;
					namespace = concept.getAttribute("rdf:about");
					identifier = namespace;

					NodeList children = concept.getChildNodes();

					for (int j = 0; j < children.getLength(); j++) {

						Node nd = children.item(j);

						if (nd != null) {
							String nodeName = nd.getNodeName();
							if (nodeName.equals("skos:prefLabel")) {
								name = nd.getFirstChild().getNodeValue();
							}
							else if (nodeName.equals("skos:definition")) {
								description = nd.getFirstChild().getNodeValue();
							}
							else if (nodeName.equals("skos:topConceptOf")) {
								this.namespace = nd.getFirstChild().getNodeValue();
							}
							else if (description == null && nodeName.endsWith("description")) {
								if (nd.getChildNodes().getLength() != 0)
									description = nd.getFirstChild().getNodeValue();
								else
									description = "";
							}
						}
					}

					if (name != null) {
						CodeEntry entry = new CodeEntry(name, description, identifier, namespace);
						this.entriesByName.put(name, entry);
						this.entriesByIdentifier.put(identifier, entry);
					}
					name = null;
					description = null;
					identifier = null;
					namespace = null;
				}
			} // end of for loop

		} catch (Exception e) {
			log.error("Error while reading skos code list as XML", e); //$NON-NLS-1$
			throw e;
		}
		return true;
	}

}
